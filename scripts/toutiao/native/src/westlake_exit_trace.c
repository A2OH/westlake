#define _GNU_SOURCE
#include <dlfcn.h>
#include <stdio.h>
#include <stdarg.h>
#include <sys/prctl.h>
#include <sys/syscall.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>
#include <string.h>

/* Safe interposer subset (exit/_exit/kill/tgkill only — abort/pthread_exit break
 * musl init, so they are NOT hooked). Names the thread + caller for the clean
 * exit_group(1) that kills the noice child. Writes to a dedicated file so the
 * initChild stderr->hilog redirect cannot swallow the line. */

#define HOOK_LOG "/data/service/el1/public/appspawnx/hook.log"

static void hlog(const char* fmt, ...) {
    char b[512];
    va_list ap; va_start(ap, fmt);
    int n = vsnprintf(b, sizeof b, fmt, ap);
    va_end(ap);
    if (n < 0) return; if (n > (int)sizeof b) n = sizeof b;
    int fd = open(HOOK_LOG, O_WRONLY | O_CREAT | O_APPEND, 0666);
    if (fd >= 0) { (void)!write(fd, b, n); close(fd); }
    (void)!write(2, b, n);   /* also stderr (pre-redirect) */
}

static void nm(char* b){ b[0]=0; prctl(PR_GET_NAME, b); }

/* Resolve up to 4 return addresses (caller chain) with dladdr. */
static void dump_ra(void) {
    void* ras[5] = {
        __builtin_return_address(0),
#if 1
        __builtin_return_address(1),
        __builtin_return_address(2),
        __builtin_return_address(3),
#endif
        0
    };
    for (int i = 0; i < 4 && ras[i]; i++) {
        Dl_info di; int ok = dladdr(ras[i], &di);
        hlog("[HOOK]   ra%d=%p %s!%s+0x%lx\n", i, ras[i],
             ok && di.dli_fname ? di.dli_fname : "?",
             ok && di.dli_sname ? di.dli_sname : "?",
             ok && di.dli_saddr ? (long)((char*)ras[i] - (char*)di.dli_saddr) : 0L);
    }
}

void exit(int s){
    char n[24]; nm(n);
    hlog("[HOOK] exit(%d) pid=%d tid=%ld thr='%s'\n", s, getpid(), (long)syscall(SYS_gettid), n);
    dump_ra();
    void(*r)(int)=dlsym(RTLD_NEXT,"exit"); if(r)r(s); syscall(94,s);
}
/* _Exit / quick_exit are DISTINCT symbols from _exit; ART/musl exit paths may
 * call these directly, bypassing an _exit-only interposer. */
void _Exit(int s){
    char n[24]; nm(n);
    hlog("[HOOK] _Exit(%d) pid=%d tid=%ld thr='%s'\n", s, getpid(), (long)syscall(SYS_gettid), n);
    dump_ra();
    void(*r)(int)=dlsym(RTLD_NEXT,"_Exit"); if(r)r(s); syscall(94,s);
}
void quick_exit(int s){
    char n[24]; nm(n);
    hlog("[HOOK] quick_exit(%d) pid=%d tid=%ld thr='%s'\n", s, getpid(), (long)syscall(SYS_gettid), n);
    dump_ra();
    void(*r)(int)=dlsym(RTLD_NEXT,"quick_exit"); if(r)r(s); syscall(94,s);
}
/* Raw exit_group(94)/exit(93) via the libc syscall() wrapper — catches code that
 * bypasses the exit-family symbols. Guarded against our own logging syscalls. */
static __thread int g_in_sc;
long syscall(long n, ...){
    __builtin_va_list ap; __builtin_va_start(ap, n);
    long a0=__builtin_va_arg(ap,long), a1=__builtin_va_arg(ap,long), a2=__builtin_va_arg(ap,long);
    long a3=__builtin_va_arg(ap,long), a4=__builtin_va_arg(ap,long), a5=__builtin_va_arg(ap,long);
    __builtin_va_end(ap);
    if(!g_in_sc && (n==94||n==93)){
        g_in_sc=1; char nm_[24]; nm(nm_);
        hlog("[HOOK] syscall(exit_group=%ld,code=%ld) pid=%d thr='%s'\n", n, a0, getpid(), nm_);
        dump_ra(); g_in_sc=0;
    }
    register long x8 __asm__("x8")=n, x0 __asm__("x0")=a0, x1 __asm__("x1")=a1,
                  x2 __asm__("x2")=a2, x3 __asm__("x3")=a3, x4 __asm__("x4")=a4, x5 __asm__("x5")=a5;
    __asm__ volatile("svc #0":"+r"(x0):"r"(x8),"r"(x1),"r"(x2),"r"(x3),"r"(x4),"r"(x5):"memory","cc");
    return x0;
}
void _exit(int s){
    char n[24]; nm(n);
    hlog("[HOOK] _exit(%d) pid=%d tid=%ld thr='%s'\n", s, getpid(), (long)syscall(SYS_gettid), n);
    dump_ra();
    void(*r)(int)=dlsym(RTLD_NEXT,"_exit"); if(r)r(s); __builtin_trap();
}
int kill(pid_t p,int s){
    char n[24]; nm(n);
    hlog("[HOOK] kill(pid=%d,sig=%d) self=%d thr='%s'\n", p, s, getpid(), n);
    if (s==SIGKILL||s==SIGTERM||s==SIGABRT) dump_ra();
    int(*r)(pid_t,int)=dlsym(RTLD_NEXT,"kill"); return r?r(p,s):-1;
}
int tgkill(int tg,int t,int s){
    char n[24]; nm(n);
    hlog("[HOOK] tgkill(tgid=%d,tid=%d,sig=%d) self=%d thr='%s'\n", tg, t, s, getpid(), n);
    if (s==SIGKILL||s==SIGABRT) dump_ra();
    int(*r)(int,int,int)=dlsym(RTLD_NEXT,"tgkill"); return r?r(tg,t,s):syscall(SYS_tgkill,tg,t,s);
}
