# Android-as-Engine: Running Unmodified APKs on OpenHarmony

**Architecture Design Document**
**Date:** 2026-03-13

---

## Executive Summary

We propose running unmodified Android APKs on OpenHarmony by treating the Android framework as an **embeddable runtime engine** — the same way OH hosts Flutter. Instead of mapping 57,000 Android APIs to OH APIs individually, or running Android in a heavy container, we port the Android framework as a self-contained engine that renders to OH surfaces and bridges to OH system services at ~15 HAL-level boundaries.

This approach was validated by analyzing 13 real APKs (TikTok, Instagram, YouTube, Netflix, Spotify, Facebook, Google Maps, Zoom, Grab, Duolingo, Uber, PayPal, Amazon) representing 2.3 billion+ monthly active users. Key finding: **94% of the "unmapped API gap" is handled automatically by the engine runtime. Only 6% needs real platform bridge work.**

---

## 1. The Flutter Precedent

OpenHarmony already hosts Flutter — a complete standalone platform:

```
Flutter on OH:
┌─────────────────────────┐
│ Flutter App (Dart)      │  ← App code, unmodified
├─────────────────────────┤
│ Flutter Framework       │  ← Own widget tree, own layout engine
│ (Material, Cupertino)   │
├─────────────────────────┤
│ Flutter Engine (C++)    │  ← Dart VM + Skia rendering
├─────────────────────────┤
│ OH Embedder             │  ← XComponent surface + platform channels
├─────────────────────────┤
│ OpenHarmony OS          │  ← Provides surface, input, services
└─────────────────────────┘
```

Flutter does NOT translate its widgets to ArkUI. It renders directly to an XComponent surface using Skia. OH doesn't care what drew those pixels.

**Android can work the same way:**

```
Android-as-Engine on OH:
┌─────────────────────────┐
│ Android App (DEX)       │  ← APK bytecode, unmodified
├─────────────────────────┤
│ Android Framework       │  ← View tree, Activity lifecycle, Content
│ (AOSP ported)           │     All 1,968 android.* classes
├─────────────────────────┤
│ Dalvik/ART Runtime      │  ← DEX execution, GC, JNI
├─────────────────────────┤
│ OH Embedder             │  ← Skia→OH Drawing, platform bridges
├─────────────────────────┤
│ OpenHarmony OS          │  ← XComponent, NativeWindow, system APIs
└─────────────────────────┘
```

---

## 2. Architecture

### 2.1 Layer Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Android APK (.apk)                       │
│  ┌─────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐   │
│  │ DEX code│ │ Resources│ │ NDK .so  │ │AndroidManifest│   │
│  └────┬────┘ └─────┬────┘ └─────┬────┘ └───────┬───────┘   │
└───────┼────────────┼────────────┼───────────────┼───────────┘
        │            │            │               │
   ┌────▼────────────▼────────────▼───────────────▼───────────┐
   │         ANDROID ENGINE (like Flutter Engine)              │
   │                                                           │
   │  ┌──────────────────────────────────────────────────────┐ │
   │  │  Android Framework (ported from AOSP, ~40 MB)        │ │
   │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐             │ │
   │  │  │ View/    │ │ Activity/│ │ Content/ │             │ │
   │  │  │ Widget/  │ │ Service/ │ │ Intent/  │             │ │
   │  │  │ Canvas/  │ │ Fragment │ │ Provider │             │ │
   │  │  │ Paint    │ │          │ │          │             │ │
   │  │  └──────────┘ └──────────┘ └──────────┘             │ │
   │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐             │ │
   │  │  │ Media/   │ │ java.*/  │ │ android. │             │ │
   │  │  │ Camera/  │ │ javax.*/ │ │ graphics/│             │ │
   │  │  │ Audio    │ │ dalvik   │ │ text/anim│             │ │
   │  │  └──────────┘ └──────────┘ └──────────┘             │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  Dalvik/ART Runtime (~30 MB)                         │ │
   │  │  DEX bytecode execution, GC, JNI, class loading      │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  MiniServer (~2000 lines Java)                       │ │
   │  │  ┌────────────┐ ┌────────────┐ ┌──────────────────┐  │ │
   │  │  │MiniActivity│ │MiniWindow  │ │MiniPackage       │  │ │
   │  │  │Manager     │ │Manager     │ │Manager           │  │ │
   │  │  │(1 app's    │ │(1 app's    │ │(1 APK's manifest │  │ │
   │  │  │ stack)     │ │ surfaces)  │ │ + intents)       │  │ │
   │  │  └────────────┘ └────────────┘ └──────────────────┘  │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  Platform Bridges (~15 boundaries, ~5 MB)            │ │
   │  │  Skia→OH Drawing  |  Audio→OH Audio  |  Input→Touch  │ │
   │  │  Camera→OH Camera  |  Net→OH Net    |  BT→OH BT     │ │
   │  │  Location→OH Geo   |  Sensor→OH Sensor              │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   └─────────────────────┼─────────────────────────────────────┘
                         │
   ┌─────────────────────▼─────────────────────────────────────┐
   │                 OpenHarmony OS                             │
   │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐   │
   │  │XComponent│ │OH_Drawing│ │ OpenGL ES│ │ OH System  │   │
   │  │ Surface  │ │ Canvas   │ │   GPU    │ │ Services   │   │
   │  └──────────┘ └──────────┘ └──────────┘ └────────────┘   │
   └───────────────────────────────────────────────────────────┘
```

### 2.2 Engine Size

| Component | Size | Comparison |
|-----------|-----:|------------|
| Dalvik/ART VM | ~30 MB | Dart VM is ~15 MB |
| Android Framework (ported) | ~40 MB | Flutter Framework is ~15 MB |
| java.* standard library | ~20 MB | Included in Dalvik |
| Platform bridges | ~5 MB | Flutter embedder is ~3 MB |
| Skia | 0 MB | **Shared with OH** — both use Skia |
| **Total engine** | **~85 MB** | Instagram APK alone is 110 MB |

For comparison:
- Container approach: 2-4 GB Android system image + 500 MB-1 GB RAM
- Flutter engine: ~30 MB
- React Native: ~10 MB

### 2.3 MiniServer: The Key Simplification

Full AOSP requires a SystemServer process with 80+ services communicating via Binder IPC. This is because Android manages many apps, many processes, many windows simultaneously.

**In the engine model, we run ONE app at a time.** This collapses the entire SystemServer into a lightweight in-process Java object:

```
Full AOSP SystemServer:                Engine MiniServer:
─────────────────────                  ──────────────────
80+ services                           4 lightweight managers
Separate process                       Same process as app
Binder IPC to communicate             Direct method calls
Manages 100+ apps                      Manages 1 app
Manages all windows                    Manages 1 app's windows
~2 GB RAM                             ~5 MB RAM
```

**MiniActivityManager:**
- Maintains the Activity back stack (a Java `ArrayList<Activity>`)
- Calls `onCreate()`, `onResume()`, `onPause()`, `onDestroy()` in correct order
- Handles `startActivity(Intent)` by resolving within the same APK's manifest
- Handles `finish()` by popping the stack and resuming the previous Activity

**MiniWindowManager:**
- Each Activity gets an OH XComponent surface
- Maps Android Window concepts to OH window management
- Routes touch events from OH → Android `MotionEvent` → `View.dispatchTouchEvent()`

**MiniPackageManager:**
- Parses the APK's `AndroidManifest.xml` at startup
- Resolves internal Intents (explicit intents within the same app)
- Provides `PackageInfo`, `ApplicationInfo` for the running app
- Handles `uses-permission` → maps to OH permission requests

**MiniContentResolver:**
- Routes `ContentResolver.query/insert/update/delete` to the app's own ContentProviders
- No cross-app provider support (not needed for single-app engine)

---

## 3. Rendering Pipeline: Why Skia Is the Key

### 3.1 Both Android and OH Use Skia

```
Android rendering:                     OH rendering:
App → View.draw(Canvas)               App → ArkUI Component.build()
  → Canvas.drawRect/Text/Path           → RenderNode
  → Skia SkCanvas                        → Skia SkCanvas (same!)
  → OpenGL ES / Vulkan                   → OpenGL ES / Vulkan (same!)
  → GPU → Display                        → GPU → Display
```

The rendering engines are **the same software**. The only difference is what sits above Skia (Android Views vs ArkUI Components). In the engine model, we keep Android Views above Skia — no conversion needed.

### 3.2 OH Drawing API Maps to Android Canvas

OH provides `OH_Drawing_Canvas` which maps nearly 1:1 to Android's `Canvas`:

| Android Canvas | OH_Drawing_Canvas | Match |
|---------------|-------------------|:-----:|
| `drawRect(l,t,r,b, paint)` | `OH_Drawing_CanvasDrawRect(canvas, rect)` | Direct |
| `drawCircle(cx,cy,r, paint)` | `OH_Drawing_CanvasDrawCircle(canvas, x,y,r)` | Direct |
| `drawLine(x1,y1,x2,y2, paint)` | `OH_Drawing_CanvasDrawLine(canvas, x1,y1,x2,y2)` | Direct |
| `drawPath(path, paint)` | `OH_Drawing_CanvasDrawPath(canvas, path)` | Direct |
| `drawBitmap(bmp, x,y, paint)` | `OH_Drawing_CanvasDrawBitmap(canvas, bmp, x,y)` | Direct |
| `drawText(text, x,y, paint)` | `OH_Drawing_CanvasDrawText(canvas, blob, x,y)` | Near |
| `save()` | `OH_Drawing_CanvasSave(canvas)` | Direct |
| `restore()` | `OH_Drawing_CanvasRestore(canvas)` | Direct |
| `translate(dx, dy)` | `OH_Drawing_CanvasTranslate(canvas, dx, dy)` | Direct |
| `scale(sx, sy)` | `OH_Drawing_CanvasScale(canvas, sx, sy)` | Direct |
| `rotate(degrees)` | `OH_Drawing_CanvasRotate(canvas, deg, x, y)` | Direct |
| `clipRect(l,t,r,b)` | `OH_Drawing_CanvasClipRect(canvas, rect)` | Direct |
| `clipPath(path)` | `OH_Drawing_CanvasClipPath(canvas, path)` | Direct |
| `Paint` (color, style) | `OH_Drawing_Pen` + `OH_Drawing_Brush` | Split |
| `Bitmap` (pixel buffer) | `OH_Drawing_Bitmap` | Direct |

OH also provides `XComponent` + `OHNativeWindow` for surface management:
- `OH_NativeXComponent`: rendering surface with lifecycle callbacks
- `OHNativeWindow`: buffer queue (request buffer → draw → flush)
- Touch event dispatch: `DispatchTouchEvent()` with multi-touch support

### 3.3 View Rendering Flow

The Android View pipeline runs entirely in Java. Only the final draw calls cross into native:

```
1. View.measure(widthSpec, heightSpec)     ← Pure Java, runs in Dalvik
   └── calculates mMeasuredWidth/Height

2. View.layout(left, top, right, bottom)   ← Pure Java, runs in Dalvik
   └── sets mLeft/mTop/mRight/mBottom

3. View.draw(canvas)                       ← Pure Java, calls Canvas methods
   ├── drawBackground(canvas)
   ├── onDraw(canvas)                      ← App's custom drawing code
   ├── dispatchDraw(canvas)                ← ViewGroup draws children
   │   └── for each child:
   │       canvas.save()
   │       canvas.translate(child.mLeft, child.mTop)
   │       child.draw(canvas)              ← Recursive
   │       canvas.restore()
   └── drawForeground(canvas)

4. Canvas.drawRect/Text/Path(...)          ← JNI bridge to OH_Drawing
   └── OH_Drawing_CanvasDrawRect(...)      ← Native OH API
       └── Skia → GPU → Display
```

Steps 1-3 are **pure Java** — they run unchanged in Dalvik. Only step 4 bridges to OH. This means:
- **Every Android View works** (TextView, RecyclerView, WebView, custom Views)
- **Every layout works** (LinearLayout, ConstraintLayout, CoordinatorLayout)
- **Every animation works** (ValueAnimator, ObjectAnimator, ViewPropertyAnimator)
- **No paradigm shift** — imperative View code runs as imperative View code

---

## 4. Platform Bridges

### 4.1 Bridge Inventory

Only ~15 system-level boundaries need bridging. Everything above these boundaries is pure Java that runs in Dalvik unchanged.

| # | Bridge | Android Side | OH Side | Complexity |
|---|--------|-------------|---------|:----------:|
| 1 | **Rendering** | Canvas/Skia | OH_Drawing + XComponent | Medium |
| 2 | **Display** | SurfaceFlinger | OHNativeWindow | Medium |
| 3 | **Input** | InputDispatcher | XComponent.DispatchTouchEvent | Low |
| 4 | **Audio** | AudioTrack/AudioRecord | OH AudioRenderer/Capturer | Medium |
| 5 | **Camera** | Camera2 HAL | @ohos.multimedia.camera | High |
| 6 | **Network** | java.net.Socket | OH socket/net | Low |
| 7 | **Location** | LocationManager | @ohos.geoLocationManager | Low |
| 8 | **Bluetooth** | BT HAL | @ohos.bluetooth.* | Medium |
| 9 | **Sensors** | SensorService | @ohos.sensor | Low |
| 10 | **Storage** | VFS / SQLite | @ohos.file.fs + SQLite | Low |
| 11 | **Telephony** | RIL | @ohos.telephony.* | Medium |
| 12 | **Notifications** | NotificationService | @ohos.notificationManager | Low |
| 13 | **Permissions** | PackageManager | @ohos.abilityAccessCtrl | Low |
| 14 | **Clipboard** | ClipboardService | @ohos.pasteboard | Low |
| 15 | **Vibration** | VibratorService | @ohos.vibrator | Low |

### 4.2 Bridge Priority (Based on 13-App Analysis)

**P0 — Required for any app to launch:**
1. Rendering bridge (Canvas → OH_Drawing)
2. Display bridge (Surface → XComponent)
3. Input bridge (touch/key events)
4. MiniActivityManager (Activity lifecycle)

**P1 — Required for media/content apps:**
5. Audio bridge (playback + recording)
6. Camera bridge
7. Network bridge
8. Storage/SQLite bridge
9. WebView bridge (wrap OH ArkWeb)

**P2 — Required for device feature apps:**
10. Location bridge
11. Bluetooth bridge
12. Sensor bridge
13. Notification bridge
14. Telephony bridge
15. Permission bridge

### 4.3 What Cannot Be Bridged

| Feature | Reason | Impact |
|---------|--------|--------|
| MediaDrm / Widevine | Requires Google certification + TEE | Netflix, YouTube Premium blocked |
| Google Play Services | Proprietary, closed-source | Some apps crash; use microG |
| Multi-process apps | Engine is single-process | <5% of apps affected |
| Cross-app Intents | No other Android apps to resolve to | Deep links fail; handle gracefully |
| Android Auto / Wear | Platform-specific extensions | Out of scope |

---

## 5. Comparison: Engine vs Container vs API Shimming

| Dimension | Engine (this proposal) | Container (Anbox-style) | API Shimming |
|-----------|:---------------------:|:----------------------:|:------------:|
| App compatibility | ~90-95% | ~99% | ~30-50% |
| App code changes | **None** | **None** | Significant |
| Memory overhead | **~85 MB** | 500 MB - 1 GB | ~50 MB |
| Storage overhead | **~85 MB** | 2-4 GB system image | ~50 MB |
| Performance | **Native** | Container overhead | Native |
| OH integration | **Deep** (shared UI, notifications) | Isolated (two worlds) | Deep |
| User experience | **Android app feels native** | App feels foreign | Depends |
| Multi-window | **Native OH windows** | Needs compositor bridge | Native |
| Clipboard sharing | **Native** | Clunky bridge | Native |
| Notification tray | **Shared with OH** | Separate/duplicated | Shared |
| Battery efficiency | **Single OS stack** | Dual OS overhead | Single OS |
| $50 phone viable | **Yes** | No (RAM/storage) | Yes |
| Time to build | 6-12 months | 2-3 months | 12-18 months |
| GMS/Play Services | No (use microG) | Possible (licensing) | No |
| DRM/Widevine | No | Possible | No |
| Maintenance | Track AOSP framework | Track AOSP image | Track both |
| Regulatory | **Single OS** | Dual OS concerns | Single OS |
| OEM certification | **Easier** | Complex | Easier |

### 5.1 Why Engine Wins Over Container for Most Cases

1. **Device economics** — OH targets $50 phones to IoT devices. Container adds 500MB+ RAM and 2-4GB storage. Engine adds 85MB. That's the difference between running on a budget device and not running at all.

2. **User experience** — Container apps live in a parallel universe. Copy/paste is clunky, notifications are duplicated, switching between OH and Android apps is jarring. Engine apps are first-class OH citizens.

3. **Regulatory** — A container is literally "dual OS." Some markets may scrutinize this for security, data sovereignty, or competition reasons. An engine is one OS with a compatibility library — standard practice.

4. **Performance** — No virtualization overhead, no IPC between container and host, no duplicate system services consuming CPU/RAM.

5. **Maintenance** — Update one engine binary, not a full Android system image. Smaller update packages, simpler OTA.

### 5.2 Where Container Still Wins

1. **DRM apps** — Netflix, YouTube Premium, Disney+ require Widevine which requires Google certification and TEE access. The container can potentially provide this.

2. **GMS-dependent apps** — Some apps hard-crash without Google Play Services. Container can include GMS (with licensing). Engine uses microG (open-source reimplementation, ~80% compatible).

3. **Time to first demo** — Container can boot an Android image in weeks. Engine takes months to build. For stakeholder demos, container ships faster.

4. **Edge-case compatibility** — Container runs real AOSP, so every Android quirk and edge case is preserved. Engine has a ported framework that may have subtle behavioral differences.

### 5.3 Recommended Strategy

**Primary path: Engine** — Build the Android-as-Engine for 90-95% of apps
**Fallback: Lightweight container** — For the 5% that need DRM/GMS, offer a container option

Over time, as engine bridges mature, migrate apps from container to engine.

---

## 6. Validation: 13-App APK Analysis

### 6.1 Real Apps Analyzed

| App | DEX Files | Unique Framework APIs | Engine Coverage | Key Bridges Needed |
|-----|----------:|---------------------:|:--------------:|-------------------|
| TikTok 44.3 | 45 | 18,225 | 17.2% | Camera, Media, Graphics, Sensors |
| Instagram | 17 | 18,531 | 17.0% | Camera, Media, Graphics, Location |
| YouTube 21.10 | 7 | 26,957 | 9.9% | Media, DRM(!), Graphics |
| Netflix 9.57 | 8 | 22,988 | 11.1% | Media, DRM(!), Network |
| Spotify | 10 | 23,496 | 10.7% | Audio, Bluetooth, Media |
| Facebook (base) | 2 | 3,669 | 30.0% | WebView, Network |
| Google Maps | 10 | 31,838 | 8.9% | Location, Sensors, Graphics |
| Zoom | 13 | 160,519 | 1.9% | Camera, Audio, Network |
| Grab | 21 | 111,675 | 3.0% | Location, Network, Security |
| Duolingo | 12 | 76,997 | 3.5% | Audio, Notifications |
| Uber Driver | 24 | 1,384 | 13.5% | Location, Network |
| PayPal (base) | 42 | 516 | 32.8% | Security, Network, NFC |
| Amazon | 8 | 27,576 | 7.1% | WebView, Network |

### 6.2 Tier 4 Gap Decomposition

What the "unmapped gap" actually is (YouTube as reference, 24,296 unmapped APIs):

| Category | Count | % | Engine Handles? |
|----------|------:|--:|:---------------:|
| Noise (proprietary libs, AndroidX, Dalvik) | 17,026 | 70.1% | **YES** — runs in Dalvik |
| UI (View/Widget/Animation/Text) | 2,841 | 11.7% | **YES** — Android View pipeline |
| OH has capability (DB mapping gap) | 2,867 | 11.8% | **YES** — platform bridges |
| Java runtime (java.*/javax.*) | 1,529 | 6.3% | **YES** — runs in Dalvik |
| True platform gap (DRM) | 33 | 0.1% | **NO** — need container fallback |

**With the engine architecture, 99.9% of the "gap" is handled. Only DRM (0.1%) is a true blocker.**

### 6.3 App Migration Priority

| Priority | Apps | Why First |
|----------|------|-----------|
| P0 | PayPal, Amazon, McDonald's | Smallest bridge surface, no DRM, no heavy native |
| P1 | Facebook, Instagram, Threads, TikTok | Camera + Media bridges needed, huge user base |
| P2 | Duolingo, Maps, Uber, Grab | Location + Audio bridges needed |
| P3 | Spotify | Audio + Bluetooth bridges needed |
| P4 | Zoom, Teams | Camera + Audio + Network (real-time) |
| LAST | Netflix, YouTube | **DRM blocker** — need container fallback |

---

## 7. Execution Roadmap

### Phase 1: Foundation (Months 1-3)
- Complete Dalvik/ART VM stability on OH
- Build rendering bridge (Canvas → OH_Drawing)
- Build display bridge (Surface → XComponent)
- Build input bridge (touch → MotionEvent)
- Build MiniActivityManager
- **Milestone: "Hello World" Android app renders on OH**

### Phase 2: Core Bridges (Months 3-6)
- Audio bridge (playback + recording)
- Network bridge (HTTP + sockets)
- Storage bridge (file system + SQLite)
- SharedPreferences bridge
- **Milestone: PayPal/Amazon can launch and show UI**

### Phase 3: Device Bridges (Months 6-9)
- Camera bridge
- Location bridge
- Bluetooth bridge
- Sensor bridge
- Notification bridge
- **Milestone: Instagram/TikTok camera features work**

### Phase 4: Polish (Months 9-12)
- Performance optimization (GPU rendering path)
- Multi-window support
- Background service bridge
- WebView bridge (wrap ArkWeb)
- Permission mapping
- **Milestone: 10 of 13 analyzed apps running**

### Phase 5: Fallback Container (Parallel)
- Lightweight Android container for DRM/GMS apps
- Minimal system image (stripped AOSP)
- Bridge container notifications to OH notification tray
- **Milestone: Netflix/YouTube running via container**

---

## 8. Risks and Mitigations

| Risk | Impact | Probability | Mitigation |
|------|--------|:-----------:|------------|
| Dalvik VM stability issues | Blocks everything | Medium | KitKat Dalvik is simple, well-understood |
| Skia version mismatch | Rendering artifacts | Low | Both use recent Skia; pin to compatible version |
| NDK .so binary compat | Apps with native code crash | High | Ship bionic libc shim alongside engine |
| GMS-dependent apps crash | User-visible failures | High | Integrate microG; detect and warn user |
| Performance of CPU rendering | Slow complex UIs | Medium | Add GPU path via OpenGL ES in Phase 4 |
| Android version expectations | Apps require API 30+ | Medium | Port API 30 framework; older Dalvik handles bytecode |
| Legal/patent concerns | Project blocked | Low | Clean-room where possible; leverage Apache 2.0 license |

---

## 9. Success Metrics

| Metric | Target | How to Measure |
|--------|--------|---------------|
| Apps that can launch | 80% of top 100 | APK analysis + engine testing |
| Apps fully functional | 60% of top 100 | Manual testing with test plans |
| Engine memory footprint | <100 MB | System monitoring |
| App startup time | <3 seconds | Instrumented timing |
| Frame rate (UI scrolling) | 60 fps | GPU profiling |
| Bridge count | <20 | Architecture review |
| Code size | <50K lines | Source code metrics |
