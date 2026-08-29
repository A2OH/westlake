#define _GNU_SOURCE
#include <errno.h>
#include <inttypes.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ptrace.h>
#include <sys/uio.h>
#include <sys/wait.h>
#include <unistd.h>

#include <asm/ptrace.h>
#include <elf.h>

static int read_words(pid_t tid, uintptr_t address, uintptr_t words[2]) {
    struct iovec local = { .iov_base = words, .iov_len = 2 * sizeof(uintptr_t) };
    struct iovec remote = { .iov_base = (void *)address, .iov_len = local.iov_len };
    ssize_t got = process_vm_readv(tid, &local, 1, &remote, 1, 0);
    return got == (ssize_t)local.iov_len ? 0 : -1;
}

int main(int argc, char **argv) {
    if (argc != 2 && argc != 4) {
        fprintf(stderr, "usage: %s TID [ADDRESS WORDS]\n", argv[0]);
        return 2;
    }
    pid_t tid = (pid_t)strtol(argv[1], NULL, 10);
    if (ptrace(PTRACE_ATTACH, tid, NULL, NULL) != 0) {
        fprintf(stderr, "PTRACE_ATTACH(%d): %s\n", tid, strerror(errno));
        return 1;
    }
    int status = 0;
    if (waitpid(tid, &status, __WALL) != tid) {
        fprintf(stderr, "waitpid(%d): %s\n", tid, strerror(errno));
        ptrace(PTRACE_DETACH, tid, NULL, NULL);
        return 1;
    }

    struct user_pt_regs regs;
    struct iovec regs_iov = { .iov_base = &regs, .iov_len = sizeof(regs) };
    if (ptrace(PTRACE_GETREGSET, tid, (void *)NT_PRSTATUS, &regs_iov) != 0) {
        fprintf(stderr, "PTRACE_GETREGSET(%d): %s\n", tid, strerror(errno));
        ptrace(PTRACE_DETACH, tid, NULL, NULL);
        return 1;
    }

    printf("tid=%d pc=%#" PRIx64 " sp=%#" PRIx64 " fp=%#" PRIx64
           " lr=%#" PRIx64 "\n",
           tid, (uint64_t)regs.pc, (uint64_t)regs.sp,
           (uint64_t)regs.regs[29], (uint64_t)regs.regs[30]);
    for (int i = 0; i < 29; ++i) {
        printf("x%-2d=%#" PRIx64 "%c", i, (uint64_t)regs.regs[i],
               i % 3 == 2 ? '\n' : ' ');
    }
    if (29 % 3 != 0) putchar('\n');
    printf("#00 %#" PRIx64 "\n", regs.pc);
    if (regs.regs[30] != 0) {
        printf("#01 %#" PRIx64 "\n", (uint64_t)regs.regs[30] - 4);
    }

    if (argc == 4) {
        uintptr_t address = (uintptr_t)strtoull(argv[2], NULL, 0);
        int count = atoi(argv[3]);
        for (int i = 0; i < count; i += 2, address += 2 * sizeof(uintptr_t)) {
            uintptr_t words[2] = {0, 0};
            if (read_words(tid, address, words) != 0) break;
            printf("mem[%#" PRIxPTR "]=%#" PRIxPTR " %#" PRIxPTR "\n",
                   address, words[0], words[1]);
        }
    }

    uintptr_t fp = (uintptr_t)regs.regs[29];
    for (int frame = 2; frame < 100 && fp != 0; ++frame) {
        uintptr_t record[2] = {0, 0};
        if ((fp & (sizeof(uintptr_t) - 1)) != 0 || read_words(tid, fp, record) != 0) {
            fprintf(stderr, "stop: unreadable fp=%#" PRIxPTR " errno=%d\n", fp, errno);
            break;
        }
        uintptr_t previous_fp = record[0];
        uintptr_t return_address = record[1];
        if (return_address == 0) break;
        printf("#%02d %#" PRIxPTR " fp=%#" PRIxPTR "\n",
               frame, return_address - 4, fp);
        if (previous_fp <= fp || previous_fp - fp > (32u << 20)) {
            fprintf(stderr, "stop: invalid fp chain %#" PRIxPTR " -> %#" PRIxPTR "\n",
                    fp, previous_fp);
            break;
        }
        fp = previous_fp;
    }

    if (ptrace(PTRACE_DETACH, tid, NULL, NULL) != 0) {
        fprintf(stderr, "PTRACE_DETACH(%d): %s\n", tid, strerror(errno));
        return 1;
    }
    return 0;
}
