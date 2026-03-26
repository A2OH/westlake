# Westlake Engine — Architecture V2: Game Engine Model

**Date:** 2026-03-26
**Status:** Architecture redesign — Westlake as a standalone runtime like Unity

---

## Key Insight

Westlake is NOT a plugin inside Android. It IS the runtime.

Like Unity runs its own VM (Mono/IL2CPP) on top of bare hardware, Westlake
runs its own ART11 JVM on top of bare hardware. The phone's Android framework
is not used — the phone just provides CPU, memory, screen, input, and network.

## Previous Architecture (Wrong)

```
Phone's ART Runtime (shared with all apps)
  └── Phone's boot classpath (android.jar — immutable)
  └── Our shim classes (loaded AFTER phone's — shadowed!)
  └── Real APK code → extends phone's Activity → needs phone's framework
  └── RESULT: classloader conflicts, framework coupling, broken
```

**Why it failed:** Two competing frameworks in the same process. The phone's
`android.app.Activity` is abstract and needs `ActivityThread`, `PhoneWindow`,
`WindowManagerService`. Our shim `Activity` is concrete and standalone. When
an APK's code runs, it picks up the phone's Activity (boot classloader wins)
and everything breaks.

## New Architecture (Correct)

```
┌──────────────────────────────────────────────────────────┐
│                   Mate 20 Pro Hardware                    │
│                   (ARM64 CPU + Memory)                    │
├──────────────────────────────────────────────────────────┤
│                                                          │
│   ┌─────────────────────┐   ┌──────────────────────┐    │
│   │   Phone's ART       │   │  Westlake ART11      │    │
│   │   (Android 10)      │   │  (our dalvikvm)      │    │
│   │                     │   │                      │    │
│   │   WestlakeActivity  │   │  Boot: aosp-shim.dex │    │
│   │   (Compose host)    │   │  App: real-app.dex   │    │
│   │   Display surface   │   │  Res: resources.arsc │    │
│   │                     │   │                      │    │
│   │   Shows rendered    │◄──│  OHBridge renders    │    │
│   │   output from       │   │  View tree to PNG/   │    │
│   │   Westlake VM       │   │  framebuffer         │    │
│   └─────────────────────┘   └──────────────────────┘    │
│                                                          │
│   Phone's ART: thin display host only                    │
│   Westlake ART: runs ALL app code against OUR shim       │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## Comparison with Unity

| Aspect | Unity | Westlake |
|--------|-------|----------|
| Runtime | Mono / IL2CPP | ART11 (AOSP Android 11) |
| Language | C# | Java / Kotlin |
| Framework | Unity API | Android API (our 2168-class shim) |
| Rendering | Unity → OpenGL/Vulkan/Metal | OHBridge → Canvas (25 functions) |
| Layout | Unity UI Toolkit | shim LayoutInflater + BinaryXmlParser |
| Resources | Unity AssetBundle | resources.arsc + ResourceTable parser |
| Platform bridge | ~100 native calls per platform | ~25 native calls per platform |
| App packaging | .unity3d / APK | DEX + resources.arsc + AXML |
| Runs on phone | Own VM process | Own dalvikvm process |
| Uses phone framework | No | No |

## Component Stack

```
Layer 5: App Code
  └── Real APK's classes.dex (Java/Kotlin bytecode)
  └── Calls standard Android API: Activity, View, Intent, SQLite, etc.

Layer 4: Android Framework Shim (2168 classes)
  └── android.app.Activity — concrete, standalone, no framework deps
  └── android.widget.* — LinearLayout, ListView, Button, TextView
  └── android.view.* — View, ViewGroup, MotionEvent, Window
  └── android.content.res.* — Resources, ResourceTable, BinaryXmlParser
  └── android.database.sqlite.* — SQLiteDatabase (in-memory or bridged)
  └── MiniServer, MiniActivityManager — Activity lifecycle management

Layer 3: OHBridge (25 native functions)
  └── Canvas: drawText, drawRect, drawLine, drawBitmap, etc.
  └── Paint: setColor, setTextSize, measureText, getFontMetrics
  └── Path: moveTo, lineTo, quadTo, close
  └── Surface: createSurface, lockCanvas, flush

Layer 2: ART11 Runtime
  └── dalvikvm binary (ARM64/x86_64 static)
  └── Boot image: boot.art + boot.oat (AOT compiled)
  └── Boot classpath: core-oj.jar + core-libart.jar + aosp-shim.dex
  └── JIT compiler, GC, thread management

Layer 1: Hardware Abstraction
  └── CPU (ARM64 / x86_64)
  └── Memory, filesystem
  └── Display (via PNG/framebuffer or native surface)
  └── Touch input (via file or native events)
  └── Network (via java.net.*)
```

## What This Means for Validation

**On the Android phone (Mate 20 Pro):**
- Run our dalvikvm as a subprocess (adb shell or spawned from Compose host)
- dalvikvm loads the APK's classes.dex with our shim as boot classpath
- NO phone framework involvement — our shim IS the framework
- OHBridge renders to PNG → Compose host displays it
- Touch events sent from Compose host → dalvikvm process

**On OHOS:**
- Same dalvikvm, same shim, same app DEX
- OHBridge renders to ArkUI/Skia instead of PNG
- Same 25 native functions, different implementation

**On x86_64 host (dev machine):**
- Same dalvikvm (x86_64 build), same everything
- Already working (MockDonalds 14/14 tests, 60fps)

## What's Already Built

| Component | Status | Lines |
|-----------|--------|-------|
| ART11 dalvikvm (ARM64) | ✅ Built, runs on phone | AOSP binary |
| ART11 dalvikvm (x86_64) | ✅ Built, runs on host | AOSP binary |
| Boot image (AOT) | ✅ Compiled | boot.art + boot.oat |
| Shim framework | ✅ 2168 classes | ~100K lines |
| MiniServer + MiniActivityManager | ✅ Working | ~800 lines |
| OHBridge (x86_64 stubs) | ✅ 169 methods | ~500 lines |
| OHBridge (phone bridge) | ✅ 170 methods | ~400 lines |
| ResourceTable parser | ✅ Verified with 3 APKs | 810 lines |
| BinaryXmlParser | ✅ Inflates AXML | 762 lines |
| XmlTestHelper (View inflater) | ✅ 91 View types | 559 lines |
| SQLiteBridge | ✅ Reflection bridge | 362 lines |
| Compose host (display) | ✅ Material 3 gallery | Kotlin |
| MockDonalds app | ✅ 14/14 tests pass | 2568 lines |
| Dialer app | ✅ 7 screens | 1453 lines |

## What Needs Wiring

| Task | What | Effort |
|------|------|--------|
| Spawn dalvikvm from Compose | WestlakeActivity starts dalvikvm process | 1 day |
| IPC: render output | dalvikvm → PNG → Compose display | 1 day |
| IPC: touch input | Compose touch → file → dalvikvm reads | 1 day |
| Load real APK in dalvikvm | Extract DEX + resources, pass to dalvikvm | 2 days |
| Shim LayoutInflater | Wire ResourceTable + BinaryXmlParser + XmlTestHelper | 1 week |
| Shim theme/style resolution | ?attr/ and @style/ lookup | 2 weeks |

## The Vision

```
User installs Westlake Engine (11MB APK)
  └── Compose gallery shows available apps
  └── User taps "Install APK" → picks any .apk file
  └── Westlake extracts classes.dex + resources.arsc
  └── Spawns dalvikvm with our shim as framework
  └── App runs against our shim — identical to OHOS
  └── Rendered output displayed in Compose host
  └── Touch events forwarded to dalvikvm
  └── Works on ANY Android phone, ANY OHOS device, ANY x86 host
```

This is the Unity model: one engine, multiple platforms, apps don't know or
care what's underneath.
