/*
 * Android arm64 setjmp/longjmp compatibility for the patched Chromium DSO.
 *
 * OH uses musl, whose jmp_buf layout is not the bionic layout embedded in the
 * Android WebView build.  libwebviewchromium.so therefore imports wl_sjp and
 * wl_ljmp instead of binding its bionic-sized buffers to musl's setjmp ABI.
 * Save and restore exactly the AArch64 callee-saved state used by bionic:
 * x19-x30, x18/sp, and d8-d15 (176 bytes total).
 */

    .text
    .p2align 2

    .global wl_sjp
    .type wl_sjp, %function
wl_sjp:
    stp x19, x20, [x0, #0]
    stp x21, x22, [x0, #16]
    stp x23, x24, [x0, #32]
    stp x25, x26, [x0, #48]
    stp x27, x28, [x0, #64]
    stp x29, x30, [x0, #80]
    mov x1, sp
    stp x18, x1, [x0, #96]
    stp d8, d9, [x0, #112]
    stp d10, d11, [x0, #128]
    stp d12, d13, [x0, #144]
    stp d14, d15, [x0, #160]
    mov w0, wzr
    ret
    .size wl_sjp, .-wl_sjp

    .global wl_ljmp
    .type wl_ljmp, %function
wl_ljmp:
    mov x16, x0
    ldp x19, x20, [x16, #0]
    ldp x21, x22, [x16, #16]
    ldp x23, x24, [x16, #32]
    ldp x25, x26, [x16, #48]
    ldp x27, x28, [x16, #64]
    ldp x29, x30, [x16, #80]
    ldp x18, x17, [x16, #96]
    ldp d8, d9, [x16, #112]
    ldp d10, d11, [x16, #128]
    ldp d12, d13, [x16, #144]
    ldp d14, d15, [x16, #160]
    mov sp, x17
    cmp w1, wzr
    csinc w0, w1, wzr, ne
    ret
    .size wl_ljmp, .-wl_ljmp

    .section .note.GNU-stack,"",%progbits
