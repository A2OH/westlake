// Minimal non-syscall ptrace launcher for locating fatal native signals in
// appspawn-x descendants.  Unlike exit_hook.c this does not interpose libc or
// alter the tracee's ABI.  It follows fork/clone, records signal-delivery PCs,
// and otherwise resumes each task unchanged.

#include <elf.h>
#include <errno.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ptrace.h>
#include <sys/types.h>
#include <sys/uio.h>
#include <sys/wait.h>
#include <unistd.h>

#ifndef PTRACE_O_TRACEFORK
#define PTRACE_O_TRACEFORK 0x00000002
#define PTRACE_O_TRACEVFORK 0x00000004
#define PTRACE_O_TRACECLONE 0x00000008
#define PTRACE_O_TRACEEXIT 0x00000040
#endif

struct regs_a64 {
    unsigned long long x[31];
    unsigned long long sp;
    unsigned long long pc;
    unsigned long long pstate;
};

static int getregs(pid_t pid, struct regs_a64 *regs)
{
    struct iovec io = { regs, sizeof(*regs) };
    return ptrace(PTRACE_GETREGSET, pid, (void *)NT_PRSTATUS, &io);
}

static int map_for(pid_t pid, unsigned long long address, char *out,
                   size_t out_size, unsigned long long *file_offset)
{
    char maps_path[64];
    snprintf(maps_path, sizeof(maps_path), "/proc/%d/maps", pid);
    FILE *maps = fopen(maps_path, "r");
    if (maps == NULL) return 0;

    char line[512];
    while (fgets(line, sizeof(line), maps) != NULL) {
        unsigned long long lo = 0, hi = 0, offset = 0;
        char perms[8] = {0};
        char path[320] = {0};
        int fields = sscanf(line, "%llx-%llx %7s %llx %*s %*s %319[^\n]",
                            &lo, &hi, perms, &offset, path);
        if (fields >= 4 && address >= lo && address < hi) {
            snprintf(out, out_size, "%s", fields >= 5 ? path : "(anonymous)");
            *file_offset = offset + address - lo;
            fclose(maps);
            return 1;
        }
    }
    fclose(maps);
    return 0;
}

static void print_register_site(pid_t pid, int sig, const char *phase)
{
    struct regs_a64 regs;
    if (getregs(pid, &regs) != 0) {
        fprintf(stderr, "[WESTLAKE-PTRACE] %s pid=%d sig=%d getregs errno=%d\n",
                phase, pid, sig, errno);
        return;
    }
    char pc_map[384] = {0}, lr_map[384] = {0};
    unsigned long long pc_off = 0, lr_off = 0;
    int pc_ok = map_for(pid, regs.pc, pc_map, sizeof(pc_map), &pc_off);
    int lr_ok = map_for(pid, regs.x[30], lr_map, sizeof(lr_map), &lr_off);
    fprintf(stderr,
            "[WESTLAKE-PTRACE] %s pid=%d sig=%d pc=%#llx %s+%#llx lr=%#llx %s+%#llx sp=%#llx\n",
            phase, pid, sig, regs.pc, pc_ok ? pc_map : "?", pc_off,
            regs.x[30], lr_ok ? lr_map : "?", lr_off, regs.sp);
    fflush(stderr);
}

int main(int argc, char **argv)
{
    if (argc < 2) {
        fprintf(stderr, "usage: %s command [args...]\n", argv[0]);
        return 2;
    }

    pid_t root = fork();
    if (root == 0) {
        if (ptrace(PTRACE_TRACEME, 0, 0, 0) != 0) _exit(126);
        raise(SIGSTOP);
        execvp(argv[1], &argv[1]);
        _exit(127);
    }

    int status = 0;
    if (waitpid(root, &status, 0) != root) return 3;
    long options = PTRACE_O_TRACEFORK | PTRACE_O_TRACEVFORK |
                   PTRACE_O_TRACECLONE | PTRACE_O_TRACEEXIT;
    ptrace(PTRACE_SETOPTIONS, root, 0, (void *)options);
    ptrace(PTRACE_CONT, root, 0, 0);
    fprintf(stderr, "[WESTLAKE-PTRACE] tracing root=%d\n", root);
    fflush(stderr);

    for (;;) {
        pid_t pid = waitpid(-1, &status, __WALL);
        if (pid < 0) {
            if (errno == EINTR) continue;
            if (errno == ECHILD) break;
            return 4;
        }
        if (WIFEXITED(status) || WIFSIGNALED(status)) continue;
        if (!WIFSTOPPED(status)) continue;

        int sig = WSTOPSIG(status);
        unsigned int event = (unsigned int)status >> 16;
        if (event == PTRACE_EVENT_FORK || event == PTRACE_EVENT_VFORK ||
            event == PTRACE_EVENT_CLONE) {
            unsigned long new_pid = 0;
            ptrace(PTRACE_GETEVENTMSG, pid, 0, &new_pid);
            ptrace(PTRACE_CONT, pid, 0, 0);
            continue;
        }
        if (event == PTRACE_EVENT_EXIT) {
            unsigned long exit_status = 0;
            ptrace(PTRACE_GETEVENTMSG, pid, 0, &exit_status);
            if (exit_status != 0) {
                print_register_site(pid, (int)(exit_status & 0x7f), "exit-event");
                fprintf(stderr,
                        "[WESTLAKE-PTRACE] exit-event pid=%d waitstatus=%#lx\n",
                        pid, exit_status);
                fflush(stderr);
            }
            ptrace(PTRACE_CONT, pid, 0, 0);
            continue;
        }

        if (sig == SIGSEGV || sig == SIGBUS || sig == SIGILL || sig == SIGABRT) {
            print_register_site(pid, sig, "signal-delivery");
        }
        int deliver = (sig == SIGTRAP || sig == SIGSTOP) ? 0 : sig;
        ptrace(PTRACE_CONT, pid, 0, (void *)(long)deliver);
    }
    return 0;
}
