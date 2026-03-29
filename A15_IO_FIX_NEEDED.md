# A15 ART: `new String(...)` hangs — must fix before ANY app works

## Problem

ANY `new String(char[])`, `new String(byte[])`, or `new String("literal")` constructor call hangs forever on A15 dalvikvm in imageless mode. This blocks everything — not just I/O, but any string construction from arrays.

`Integer.toString(42)`, `String.valueOf(42)`, `System.out.println()` — all hang because they eventually call a String constructor.

## Precise isolation (tested 2026-03-28)

| Test | Code | Result |
|------|------|--------|
| Test1 | `void main() {}` (empty) | **WORKS** (0ms) |
| Test4 | `int y = x + args.length` | **WORKS** (0ms) |
| Test5 | `String s = "hello"` (literal ref) | **WORKS** (0ms) |
| Test6 | `new StringBuilder()` | **WORKS** (0ms) |
| Test11 | `Integer.valueOf(42)` | **WORKS** (1ms) |
| Test7 | `sb.append(42)` | **HANGS** (calls Integer.toString → new String) |
| Test8 | `String.valueOf(42)` | **HANGS** |
| Test9 | `Integer.toString(42)` | **HANGS** |
| Test10 | `new String(char[])` | **HANGS** |
| Test13 | `new String("literal")` | **HANGS** |
| Test14 | `new String(byte[])` | **HANGS** |
| Hello | `System.err.println("hello")` | **HANGS** |

**Root cause: `new String(...)` constructor replacement doesn't work in imageless mode.**

In ART, `String.<init>(...)` constructors are rewritten to `StringFactory.newStringFrom*()` calls. `register_java_lang_StringFactory` succeeds (confirmed in logs). But in imageless/interpreter mode (`-Xnoimage-dex2oat`), the rewrite mechanism doesn't fire — the interpreter tries to call the actual `String.<init>()` which either loops or deadlocks.

The A15 boot image (from dex2oat) has these rewrites pre-compiled, but the boot image was built with `$HOME/art-latest/core-jars/` paths that don't match the phone's `/data/local/tmp/westlake/` paths, so it's rejected.

## Fix options

1. **Regenerate boot image on-device** with correct paths: `dex2oat --dex-file=core-oj.jar --dex-file=core-libart.jar --dex-file=core-icu4j.jar --oat-file=boot.oat --image=boot.art` using absolute `/data/local/tmp/westlake/` paths
2. **Fix String constructor rewriting in interpreter mode** — the runtime's `UnstartedRuntime` or `interpreter::DoCall` should intercept `String.<init>` and redirect to `StringFactory` even without a boot image
3. **Make boot image path-relocatable** — ART supports `--relocate` but the standalone build may not implement it

Option 1 is the fastest fix. However, on-device `dex2oat` produces 0-byte output files (tested with `--base=0x70000000 --instruction-set=arm64`). The A15 agent's dex2oat worked on x86_64 QEMU but fails silently on real ARM64 phone. This needs investigation.

The deployed boot image at `ohos-deploy/arm64-a15/` has hardcoded paths `$HOME/art-latest/core-jars/` which can't be created on the phone (read-only /home). Must either fix dex2oat on-device or make the boot image relocatable.

## Root Cause Analysis (I/O, secondary issue)

A15 also moved file I/O from native FileOutputStream methods to a Java call chain:

```
System.out.println("hello")
  → PrintStream.println()
    → BufferedOutputStream.write()
      → FileOutputStream.write(byte[], int, int)   ← THIS IS NOW PURE JAVA in A15
        → IoBridge.write(fd, bytes, offset, count)
          → BlockGuardOs.write(fd, bytes, offset, count)
            → ForwardingOs.write(fd, bytes, offset, count)
              → Linux.writeBytes(fd, buffer, offset, count)  ← NATIVE
```

### What changed from A11 to A15:
- **A11**: `FileOutputStream.write()` is a **native** method → direct `write(2)` syscall in `openjdk_stub.c`
- **A15**: `FileOutputStream.write()` is a **Java** method → calls through 5 Java wrapper classes → eventually reaches `libcore.io.Linux.writeBytes()` native

### What's registered vs what's needed:

The `javacore_stub.c` registers `Linux.writeBytes(FileDescriptor, Object, int, int)` and it has a real `write(2)` implementation. But the call never reaches it.

### Likely failure point:

The Java call chain goes through `IoBridge` → `BlockGuardOs` → `ForwardingOs` → `Linux`. One of these intermediate classes:
1. May fail during `<clinit>` (static initialization) — causing a deadlock
2. May call another unimplemented native method
3. May throw an exception that's silently caught and retried in a loop

### Evidence:
- `Failed to register native method java.io.FileOutputStream.open0(Ljava/lang/String;Z)V` — confirms FileOutputStream.open0 is NOT native in A15 (registration correctly skipped)
- `Failed to register native method java.io.FileOutputStream.write(IZ)V` — confirms write is NOT native in A15
- No exception logged after `Calling main()...` — suggests silent hang, not crash

## What needs to be fixed

1. **Verify `libcore.io.Linux` natives are actually registered** — add `fprintf(stderr, ...)` to `javacore_stub.c` JNI_OnLoad to confirm `Linux` class registration succeeds

2. **Check the Java call chain** — the likely culprit is one of:
   - `android.system.Os` class initialization
   - `libcore.io.BlockGuardOs` class initialization
   - `libcore.io.IoBridge` class initialization
   - Some missing native in these classes

3. **Add missing natives** — A15 may need additional natives in the `libcore.io.Linux` class that A11 didn't need. Check for:
   - `Linux.getpid()`, `Linux.getuid()`, `Linux.fstat()`, `Linux.isatty()`
   - `Linux.android_fdsan_*` methods (file descriptor sanitizer, new in later Android)
   - `Os.sysconf()`, `Os.getenv()`

4. **Test in isolation** — try calling `Linux.writeBytes` directly via reflection, bypassing the IoBridge chain, to see if the native itself works

## Quick test to narrow the hang

Add this to dalvikvm.cc before `CallStaticVoidMethod`:
```cpp
// Test if basic I/O works at JNI level
jclass fdClass = env->FindClass("java/io/FileDescriptor");
jfieldID errField = env->GetStaticFieldID(fdClass, "err", "Ljava/io/FileDescriptor;");
jobject fdErr = env->GetStaticObjectField(fdClass, errField);
// If we get here, FileDescriptor.err exists
fprintf(stderr, "[test] FileDescriptor.err = %p\n", fdErr);
```

## Files involved

- `$HOME/art-latest/stubs/javacore_stub.c` — has Linux natives (lines 43-160, 577-600)
- `$HOME/art-latest/stubs/openjdk_stub.c` — FileOutputStream stubs (registration fails, expected)
- `$HOME/art-latest/patches/dalvikvm/dalvikvm.cc` — main entry point
- `$HOME/art-latest/patches/runtime/runtime.cc` — runtime init (skips CreateSystemClassLoader)

## Current state

- A15 dalvikvm boots, finds main class, calls main() — confirmed working for compute-only code (FibOnly)
- System.out/err.println hangs — blocks ALL app rendering
- A11 dalvikvm works fine for app rendering (pipe + SurfaceView verified)
- Phone is currently running A11 (restored after A15 testing)
