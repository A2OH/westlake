# Android-to-OpenHarmony API Migration: Comprehensive Analysis Plan

**Goal:** Run unmodified Android APKs on OpenHarmony by treating the Android framework as an embeddable runtime engine — similar to how OH hosts Flutter or React Native.

**Date:** 2026-03-10 (Updated: 2026-03-14)
**Execution Plan:** See `03-ENGINE-EXECUTION-PLAN.md` for the concrete WS1-4 implementation plan

---

## Architecture: Android-as-Engine on OpenHarmony

### Core Insight

OpenHarmony already hosts **Flutter** — a complete standalone platform with its own rendering engine (Skia), widget system, layout engine, and runtime (Dart VM). Flutter doesn't map to ArkUI widgets; it renders directly to an XComponent surface.

**Android can work the same way.** Instead of shimming 57,000 Android APIs to OH APIs one by one, we port the Android framework as a self-contained engine that renders to OH surfaces.

```
┌─────────────────────────────────────────────────────────────┐
│                    Android APK (.apk)                       │
│  ┌─────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐   │
│  │ DEX code│ │ Resources│ │ NDK .so  │ │AndroidManifest│   │
│  └────┬────┘ └─────┬────┘ └─────┬────┘ └───────┬───────┘   │
└───────┼────────────┼────────────┼───────────────┼───────────┘
        │            │            │               │
   ┌────▼────────────▼────────────▼───────────────▼───────────┐
   │         ANDROID-AS-ENGINE (like Flutter Engine)           │
   │                                                           │
   │  ┌──────────────────────────────────────────────────────┐ │
   │  │  Layer 1: Android Framework (ported from AOSP)       │ │
   │  │  View, ViewGroup, Activity, Service, Intent,         │ │
   │  │  ContentProvider, MediaPlayer, Camera, Bluetooth...  │ │
   │  │  [runs in Dalvik/ART — NOT shimmed to ArkUI]         │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  Layer 2: Dalvik/ART Runtime                         │ │
   │  │  DEX bytecode execution, GC, JNI, class loading      │ │
   │  │  [dalvik-port — already building]                    │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  Layer 3: Platform Bridge (Android→OH)               │ │
   │  │  Skia → OH Drawing/GPU    (rendering)                │ │
   │  │  Binder → OH IPC          (services)                 │ │
   │  │  SurfaceFlinger → NativeWindow (display)             │ │
   │  │  InputDispatcher → XComponent events (touch/key)     │ │
   │  │  AudioFlinger → OH Audio  (sound)                    │ │
   │  │  Camera HAL → OH Camera   (camera)                   │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   └─────────────────────┼─────────────────────────────────────┘
                         │
   ┌─────────────────────▼─────────────────────────────────────┐
   │                 OpenHarmony OS                             │
   │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐   │
   │  │XComponent│ │NativeWin │ │ OH NDK   │ │ OH System  │   │
   │  │ Surface  │ │ Buffer   │ │ Drawing  │ │ Services   │   │
   │  └──────────┘ └──────────┘ └──────────┘ └────────────┘   │
   └───────────────────────────────────────────────────────────┘
```

### Why This Works (Flutter Analogy)

| Aspect | Flutter on OH | Android-as-Engine on OH |
|--------|--------------|------------------------|
| Runtime | Dart VM | Dalvik/ART VM |
| Widget system | Flutter Widgets (own tree) | Android Views (own tree) |
| Layout engine | Flutter RenderObject | Android View.measure/layout |
| Rendering | Skia → XComponent | Skia → XComponent |
| Input | XComponent → Flutter gestures | XComponent → Android MotionEvent |
| Lifecycle | OH Ability → Flutter lifecycle | OH Ability → Android Activity |
| Platform services | Platform Channels → OH APIs | Platform Bridge → OH APIs |
| App modification needed | **None** | **None** |

### What Does NOT Need Mapping

With this architecture, the entire View/Widget/Animation/Text subsystem is **not an API mapping problem** — it runs natively in the Android engine. Our APK analysis showed these represent 2,841 unmapped APIs per app (11.7% of Tier 4). They're now zero-cost.

### What DOES Need the Platform Bridge (Layer 3)

Only the system service boundaries need translation. Based on our 13-app APK analysis, these are the OH subsystems that need bridges:

| Bridge | Android Side | OH Side | Status |
|--------|-------------|---------|--------|
| Graphics/Rendering | Skia/Canvas/OpenGL ES | OH_Drawing + XComponent | OH has full Skia + OpenGL ES |
| Audio | AudioFlinger/AudioTrack | @ohos.multimedia.audio | OH has AudioRenderer/Capturer |
| Camera | Camera HAL / camera2 | @ohos.multimedia.camera | OH has camera pipeline |
| Display/Surface | SurfaceFlinger/NativeWindow | OHNativeWindow | OH has NativeWindow API |
| Input | InputDispatcher | XComponent.DispatchTouchEvent | OH has full touch/key |
| Network | ConnectivityManager | @ohos.net.connection | OH has net APIs |
| Location | LocationManager | @ohos.geoLocationManager | OH has location |
| Bluetooth | Bluetooth HAL | @ohos.bluetooth.* | OH has BT stack |
| Telephony | RIL/TelephonyManager | @ohos.telephony.* | OH has telephony |
| Sensors | SensorService | @ohos.sensor | OH has sensors |
| Storage/DB | VFS/SQLite | @ohos.file.fs + SQLite | OH has file + SQLite |
| Notifications | NotificationManager | @ohos.notificationManager | OH has notifications |
| Permissions | PackageManager | @ohos.abilityAccessCtrl | OH has permissions |
| DRM | MediaDrm/Widevine | **NO EQUIVALENT** | True gap |

---

## Findings from Real APK Analysis (13 Apps, March 2026)

### Apps Analyzed

| App | Size | DEX Files | Unique Framework APIs | OH Coverage |
|-----|-----:|----------:|---------------------:|----------:|
| TikTok 44.3 | 355 MB | 45 | 18,225 | 17.2% |
| Instagram | 110 MB | 17 | 18,531 | 17.0% |
| YouTube 21.10 | 170 MB | 7 | 26,957 | 9.9% |
| Netflix 9.57 | 175 MB | 8 | 22,988 | 11.1% |
| Spotify | 36 MB | 10 | 23,496 | 10.7% |
| Facebook (base) | 91 MB | 2 | 3,669 | 30.0% |
| Google Maps | 37 MB | 10 | 31,838 | 8.9% |
| Zoom | 137 MB | 13 | 160,519 | 1.9% |
| Grab | 231 MB | 21 | 111,675 | 3.0% |
| Duolingo | 126 MB | 12 | 76,997 | 3.5% |
| Uber Driver | 234 MB | 24 | 1,384 | 13.5% |
| PayPal (base) | 149 MB | 42 | 516 | 32.8% |
| Amazon | 45 MB | 8 | 27,576 | 7.1% |

### Tier 4 Gap Decomposition

The headline "67-83% Tier 4 gap" is misleading. Actual breakdown (YouTube as reference):

| Category | % of Tier 4 | Action Required |
|----------|:----------:|-----------------|
| **Noise** (proprietary libs, AndroidX, Dalvik) | **70.1%** | None — handled by Android-as-Engine runtime |
| **UI Paradigm Shift** (View/Widget/Animation) | **11.7%** | None — Android View pipeline runs natively in engine |
| **OH Has Capability** (mapping gap in our DB) | **11.8%** | Fix DB + build Platform Bridges |
| **Java Runtime** (java.*/javax.*) | **6.3%** | Handled by Dalvik/ART runtime |
| **True Platform Gap** (DRM) | **0.1%** | Platform-level work needed |

**Conclusion:** With the Android-as-Engine architecture, ~94% of Tier 4 is handled by the runtime itself. Only ~6% (Platform Bridge + DRM) needs real engineering work.

### Subsystem Usage Across 13 Apps

| Subsystem | Max APIs (any app) | Avg Score | Bridge Priority |
|-----------|-------------------:|:---------:|:---------------:|
| View/Widget | 2,916 (TikTok) | 1.2-1.3 | **Engine** (no bridge) |
| Graphics | 942 (TikTok) | 2.0-2.1 | **Engine** (Skia shared) |
| Java Standard | 2,822 (TikTok) | 3.2 | **Engine** (Dalvik) |
| Media | 741 (YouTube) | 3.1-3.4 | P0 Bridge |
| App Framework | 886 (Maps) | 2.5-2.8 | P0 Bridge |
| OS | 465 (Instagram) | 3.0-3.1 | P0 Bridge |
| Content | 316 (TikTok) | 3.1-3.4 | P0 Bridge |
| Networking | 240 (YouTube) | 2.0-2.3 | P1 Bridge |
| Camera | 133 (Maps) | 2.7-3.2 | P1 Bridge |
| Location | 125 (Maps) | 4.5-6.5 | P1 Bridge |
| Bluetooth | 85 (Spotify) | 3.8-3.9 | P2 Bridge |
| Database | 153 (Zoom) | 3.4-4.0 | P1 Bridge |
| Telephony | 192 (Instagram) | 2.8-3.7 | P2 Bridge |
| WebView | 283 (TikTok) | 1.6-2.1 | P1 Bridge |
| Security | 38 (Grab) | 1.8-2.0 | P2 Bridge |
| Notifications | 6 (YouTube) | 2.7-2.9 | P2 Bridge |

### APIs Unmapped Across ALL 13 Apps (Highest Priority for DB)

These APIs appear in every single analyzed app and have no OH mapping:
- `Intent.putExtra`, `Intent.<init>` — Content/Navigation
- `ViewGroup.addView`, `FrameLayout.<init>` — View (handled by engine)
- `StringBuilder.append`, `String.valueOf`, `Arrays.*` — Java Standard (handled by runtime)
- `Handler.<init>`, `Handler.obtainMessage` — OS threading

---

## Revised Execution Plan

### Phase 1: Dalvik/ART Runtime on OH (FOUNDATION)

**Status:** In progress (`dalvik-port/`)
**Goal:** Run DEX bytecode on OpenHarmony

Already have:
- Dalvik KitKat VM building for x86_64, OHOS aarch64, OHOS ARM32
- 1,968 android.* stub classes compiling
- Mock OHBridge for JVM testing

Remaining:
- Complete VM stability (GC, threading, JNI)
- Class loader for multi-DEX APKs (TikTok has 45!)
- JNI bridge to OH NDK

### Phase 2: Graphics/Rendering Bridge (CRITICAL — Unlocks UI)

**Goal:** Android Canvas → OH_Drawing_Canvas + XComponent

This is the Flutter-equivalent rendering bridge. OH provides:
- `OH_Drawing_Canvas` — CPU 2D rendering (drawRect, drawText, drawPath, drawBitmap)
- `OH_Drawing_Pen/Brush` — stroke/fill styles (maps to Android Paint)
- `OH_Drawing_Bitmap` — pixel buffer (maps to Android Bitmap)
- `XComponent` — native rendering surface with touch/lifecycle callbacks
- `OHNativeWindow` — buffer queue for surface rendering
- OpenGL ES — GPU rendering (shared with Android)
- **Skia** — both Android and OH use Skia internally

Bridge mapping:
```
android.graphics.Canvas.drawRect()  →  OH_Drawing_CanvasDrawRect()
android.graphics.Canvas.drawText()  →  OH_Drawing_CanvasDrawText()
android.graphics.Canvas.drawPath()  →  OH_Drawing_CanvasDrawPath()
android.graphics.Canvas.drawBitmap()→  OH_Drawing_CanvasDrawBitmap()
android.graphics.Canvas.save()      →  OH_Drawing_CanvasSave()
android.graphics.Canvas.restore()   →  OH_Drawing_CanvasRestore()
android.graphics.Canvas.translate() →  OH_Drawing_CanvasTranslate()
android.graphics.Canvas.clipRect()  →  OH_Drawing_CanvasClipRect()
android.graphics.Paint (color,style)→  OH_Drawing_Pen + OH_Drawing_Brush
android.graphics.Bitmap             →  OH_Drawing_Bitmap
android.view.Surface                →  OHNativeWindow
```

Once this bridge works, the **entire Android View system runs unchanged**:
- View.measure() / layout() — pure Java, runs in Dalvik
- View.draw(canvas) — canvas routes to OH_Drawing
- ViewGroup.dispatchDraw() — recursively draws children
- Touch events — XComponent.DispatchTouchEvent → Android MotionEvent

**This eliminates the "UI paradigm shift" problem entirely.**

### Phase 3: Improve API Mapping DB (ACCURACY)

**Goal:** Fix the 11.8% of Tier 4 where OH has capability but DB mapping is wrong

Already planned and approved:
1. Fix OH JS/TS parser (`import_oh_js.py`) — fill 267 empty modules
2. Fix OH NDK parser (`import_oh_ndk.py`) — callback typedefs, struct members
3. Schema enhancements — tier classification, multi-candidate, capability assessment
4. Rewrite auto-mapper — multi-signal scoring, 200+ known mappings, real confidence
5. Generate migration guidance — gap descriptions, migration guides

Expected impact: OH API count 29,290 → 55,000+, coverage 10% → 40-50%

### Phase 4: Platform Service Bridges (FUNCTIONALITY)

Build bridges for each system service, prioritized by real app usage:

**P0 (needed for basic app launch):**
- Activity lifecycle → OH UIAbility lifecycle
- Intent/Want navigation
- SharedPreferences → @ohos.data.preferences
- Context → OH application context
- Window/Display → OH window manager

**P1 (needed for media/content apps):**
- MediaPlayer → @ohos.multimedia.media AVPlayer
- AudioTrack/AudioRecord → OH audio renderer/capturer
- Camera2 → @ohos.multimedia.camera
- ConnectivityManager → @ohos.net.connection
- SQLiteDatabase → @ohos.data.relationalStore (or just use SQLite directly)
- WebView → @ohos.web.webview (ArkWeb)
- LocationManager → @ohos.geoLocationManager

**P2 (needed for device features):**
- BluetoothAdapter → @ohos.bluetooth.*
- SensorManager → @ohos.sensor
- TelephonyManager → @ohos.telephony.*
- NotificationManager → @ohos.notificationManager
- Vibrator → @ohos.vibrator

**P3 (specialized):**
- NFC → @ohos.nfc.*
- BiometricPrompt → @ohos.userIAM.userAuth
- AccountManager → @ohos.account.osAccount
- MediaDrm/Widevine → **NO OH EQUIVALENT** (park)

### Phase 5: App Category Migration Roadmap

Based on APK analysis, attack apps in this order:

| Priority | Category | Key Bridges Needed | Example Apps |
|----------|----------|-------------------|-------------|
| P0 | Payment/Finance | Network + Security + NFC | PayPal |
| P1 | Social/Messaging | Camera + Media + Notifications | Facebook, Instagram |
| P2 | E-commerce | WebView + Network | Amazon |
| P3 | Navigation | Location + Sensors + Graphics | Maps, Uber, Grab |
| P4 | Education | Audio + Notifications | Duolingo |
| P5 | Music Streaming | Audio + Bluetooth + Media | Spotify |
| LAST | Video+DRM | MediaDrm (BLOCKER) | Netflix, YouTube |

### Phase 6: Rebuild Pipeline & Validation

- `database/rebuild_db.sh` — full pipeline orchestration
- `database/validate_db.py` — automated quality checks
- `database/analyze_apk.py` — real APK analysis tool (built, working)
- Before/after comparison reports

---

## Key Architectural Decisions

### Decision 1: Android-as-Engine, NOT API Shimming

**Rejected:** Map 57,000 Android APIs to OH APIs individually
**Adopted:** Port Android framework as a self-contained runtime engine

Rationale:
- Flutter proves OH can host foreign rendering engines
- 70% of Tier 4 "gaps" are handled automatically by the runtime
- UI paradigm shift (11.7% of gaps) is eliminated entirely
- Only ~12% of gaps need real Platform Bridge work
- App developers change ZERO lines of code

### Decision 2: Skia Bridge, NOT Canvas-to-ArkUI Translation

**Rejected:** Translate Android View tree to ArkUI components at runtime
**Adopted:** Android Views render through Skia → OH_Drawing/OpenGL → XComponent

Rationale:
- Both Android and OH use Skia internally
- OH_Drawing_Canvas maps near-1:1 to Android Canvas (rect, text, path, bitmap, transforms, clip)
- Paradigm mismatch (imperative vs declarative) is irrelevant — Android Views render to pixels, not ArkUI nodes
- Performance: direct GPU path via shared OpenGL ES, CPU fallback via OH_Drawing

### Decision 3: Bridge at System Boundary, NOT at API Level

**Rejected:** Shim every android.* class individually (57,000 bridges)
**Adopted:** Bridge only at ~15 system service boundaries (HAL-equivalent)

Rationale:
- The Android framework is self-consistent internally — Activity calls View which calls Canvas which calls Paint
- Only the bottom layer (where it touches hardware/OS services) needs bridging
- Same approach as Anbox/Waydroid: run Android framework, bridge at HAL/binder level
- Reduces bridge surface from 57,000 APIs to ~500 HAL/service interfaces

---

## Role of the API Mapping DB Going Forward

The `api_compat.db` serves a different purpose in the Android-as-Engine architecture:

1. **Platform Bridge prioritization** — which OH system APIs map to Android HAL/service interfaces
2. **Capability gap identification** — where OH genuinely lacks a feature (DRM)
3. **App migration readiness scoring** — given the current bridge state, which apps can run
4. **APK analysis** — `analyze_apk.py` tells us which bridges a specific APK needs
5. **Developer documentation** — for apps that DO want to go native ArkUI, the mapping guide helps

The DB is NOT used for runtime API translation anymore — the engine handles that internally.

---

## Files & Tools

| What | Path | Status |
|------|------|--------|
| Analysis plan (this file) | `00-ANALYSIS-PLAN.md` | Updated 2026-03-13 |
| API compatibility database | `database/api_compat.db` | 57,289 Android + 29,290 OH APIs |
| DB schema | `database/schema.sql` | Extended with tiers, candidates, capabilities |
| OH JS parser | `database/import_oh_js.py` | Rewritten (brace-counting, declare/export default) |
| OH NDK parser | `database/import_oh_ndk.py` | Enhanced (callbacks, struct members) |
| Auto-mapper | `database/auto_mapper.py` | Rewritten (multi-signal, 200+ known mappings) |
| APK analyzer | `database/analyze_apk.py` | Working — tested on 13 real APKs |
| APK analysis results | `database/apk_analysis_results.json` | 13-app comparison data |
| Dalvik VM port | `dalvik-port/` | Building for x86_64, OHOS aarch64, ARM32 |
| Android shim stubs | `shim/java/android/` | 1,968 stub classes |
| Platform bridge (C) | `shim/bridge/cpp/cpp_shim.h` | Initial OH API wrappers |
| Skills/guides | `skills/` | Migration guides per subsystem |
| Frontend | `frontend/` | Web dashboard for API mapping visualization |
| Backend | `backend/` | FastAPI server for DB queries |
