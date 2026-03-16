# Android即引擎：在OpenHarmony上运行未修改的APK

**架构设计文档**
**日期：** 2026-03-13

---

## 摘要

我们提出将Android框架作为**可嵌入的运行时引擎**在OpenHarmony上运行未修改的Android APK——与OH承载Flutter的方式完全相同。不需要将57,000个Android API逐一映射到OH API，也不需要在重量级容器中运行Android。我们将Android框架作为自包含引擎移植，渲染到OH表面，并在约15个HAL级边界处桥接OH系统服务。

通过分析13个真实APK（抖音/TikTok、Instagram、YouTube、Netflix、Spotify、Facebook、Google Maps、Zoom、Grab、Duolingo、Uber、PayPal、Amazon）验证了该方案。关键发现：**94%的"未映射API差距"由引擎运行时自动处理，只有6%需要真正的平台桥接工作。**

---

## 1. Flutter先例

OpenHarmony已经承载了Flutter——一个完整的独立平台：

```
Flutter在OH上的运行方式：
┌─────────────────────────┐
│ Flutter应用 (Dart)       │  ← 应用代码，无需修改
├─────────────────────────┤
│ Flutter框架              │  ← 自有Widget树、自有布局引擎
│ (Material, Cupertino)   │
├─────────────────────────┤
│ Flutter引擎 (C++)        │  ← Dart VM + Skia渲染
├─────────────────────────┤
│ OH嵌入层                 │  ← XComponent表面 + 平台通道
├─────────────────────────┤
│ OpenHarmony操作系统       │  ← 提供表面、输入、服务
└─────────────────────────┘
```

Flutter**不会**将Widget翻译成ArkUI组件。它直接通过Skia渲染到XComponent表面。OH不关心是什么绘制了这些像素。

**Android可以用完全相同的方式工作：**

```
Android即引擎在OH上的运行方式：
┌─────────────────────────┐
│ Android应用 (DEX)        │  ← APK字节码，无需修改
├─────────────────────────┤
│ Android框架              │  ← View树、Activity生命周期、Content
│ (移植自AOSP)             │     全部1,968个android.*类
├─────────────────────────┤
│ Dalvik/ART运行时          │  ← DEX执行、GC、JNI
├─────────────────────────┤
│ OH嵌入层                 │  ← Skia→OH Drawing，平台桥接
├─────────────────────────┤
│ OpenHarmony操作系统       │  ← XComponent、NativeWindow、系统API
└─────────────────────────┘
```

---

## 2. 架构详解

### 2.1 分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Android APK (.apk)                       │
│  ┌─────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐   │
│  │ DEX代码  │ │ 资源文件  │ │ NDK .so  │ │AndroidManifest│   │
│  └────┬────┘ └─────┬────┘ └─────┬────┘ └───────┬───────┘   │
└───────┼────────────┼────────────┼───────────────┼───────────┘
        │            │            │               │
   ┌────▼────────────▼────────────▼───────────────▼───────────┐
   │         ANDROID引擎 (类似Flutter引擎)                     │
   │                                                           │
   │  ┌──────────────────────────────────────────────────────┐ │
   │  │  Android框架 (移植自AOSP, ~40 MB)                    │ │
   │  │  View/Widget/Canvas/Paint | Activity/Service/Fragment│ │
   │  │  Content/Intent/Provider  | Media/Camera/Audio       │ │
   │  │  java.*/javax.*/dalvik    | graphics/text/animation  │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  Dalvik/ART运行时 (~30 MB)                           │ │
   │  │  DEX字节码执行、垃圾回收、JNI、类加载                   │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  MiniServer (约2000行Java)                           │ │
   │  │  MiniActivityManager: 管理单个应用的Activity栈         │ │
   │  │  MiniWindowManager:   管理单个应用的窗口→XComponent    │ │
   │  │  MiniPackageManager:  解析单个APK的Manifest和Intent   │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   │                     │                                     │
   │  ┌──────────────────▼───────────────────────────────────┐ │
   │  │  平台桥接 (约15个边界, ~5 MB)                         │ │
   │  │  Skia→OH Drawing | Audio→OH Audio  | Input→Touch     │ │
   │  │  Camera→OH Camera | Net→OH Net    | BT→OH BT        │ │
   │  │  Location→OH Geo | Sensor→OH Sensor                  │ │
   │  └──────────────────┬───────────────────────────────────┘ │
   └─────────────────────┼─────────────────────────────────────┘
                         │
   ┌─────────────────────▼─────────────────────────────────────┐
   │                 OpenHarmony操作系统                        │
   │  XComponent | OH_Drawing Canvas | OpenGL ES | 系统服务     │
   └───────────────────────────────────────────────────────────┘
```

### 2.2 引擎大小

| 组件 | 大小 | 对比 |
|------|-----:|------|
| Dalvik/ART虚拟机 | ~30 MB | Dart VM约15 MB |
| Android框架（移植） | ~40 MB | Flutter框架约15 MB |
| java.*标准库 | ~20 MB | 包含在Dalvik中 |
| 平台桥接 | ~5 MB | Flutter嵌入层约3 MB |
| Skia | 0 MB | **与OH共享** —— 双方都使用Skia |
| **引擎总计** | **~85 MB** | Instagram APK本身就有110 MB |

对比：
- 容器方案：2-4 GB Android系统镜像 + 500 MB-1 GB内存开销
- Flutter引擎：~30 MB
- React Native：~10 MB

### 2.3 MiniServer：关键简化

完整的AOSP需要一个SystemServer进程，包含80多个服务，通过Binder IPC通信。这是因为Android要同时管理多个应用、多个进程、多个窗口。

**在引擎模式下，我们一次只运行一个应用。** 这将整个SystemServer简化为一个轻量级的进程内Java对象：

| | 完整AOSP SystemServer | 引擎MiniServer |
|---|---|---|
| 服务数量 | 80+个服务 | 4个轻量管理器 |
| 运行方式 | 独立进程 | 与应用同进程 |
| 通信方式 | Binder IPC | 直接方法调用 |
| 管理范围 | 100+个应用 | 1个应用 |
| 内存占用 | ~2 GB | ~5 MB |

---

## 3. 渲染管线：Skia是关键

### 3.1 Android和OH都使用Skia

```
Android渲染：                          OH渲染：
App → View.draw(Canvas)               App → ArkUI Component.build()
  → Canvas.drawRect/Text/Path           → RenderNode
  → Skia SkCanvas                        → Skia SkCanvas（相同！）
  → OpenGL ES / Vulkan                   → OpenGL ES / Vulkan（相同！）
  → GPU → 显示                           → GPU → 显示
```

渲染引擎**是同一套软件**。唯一的区别是Skia上层是什么（Android View vs ArkUI组件）。在引擎模式下，我们保留Android View在Skia上层 —— 无需转换。

### 3.2 OH Drawing API与Android Canvas的映射

| Android Canvas | OH_Drawing_Canvas | 匹配度 |
|---------------|-------------------|:------:|
| `drawRect()` | `OH_Drawing_CanvasDrawRect()` | 直接映射 |
| `drawCircle()` | `OH_Drawing_CanvasDrawCircle()` | 直接映射 |
| `drawLine()` | `OH_Drawing_CanvasDrawLine()` | 直接映射 |
| `drawPath()` | `OH_Drawing_CanvasDrawPath()` | 直接映射 |
| `drawBitmap()` | `OH_Drawing_CanvasDrawBitmap()` | 直接映射 |
| `drawText()` | `OH_Drawing_CanvasDrawText()` | 近似映射 |
| `save()/restore()` | `OH_Drawing_CanvasSave/Restore()` | 直接映射 |
| `translate/scale/rotate` | `OH_Drawing_CanvasTranslate/Scale/Rotate` | 直接映射 |
| `clipRect/clipPath` | `OH_Drawing_CanvasClipRect/Path` | 直接映射 |
| `Paint` | `OH_Drawing_Pen` + `OH_Drawing_Brush` | 拆分映射 |
| `Bitmap` | `OH_Drawing_Bitmap` | 直接映射 |

### 3.3 View渲染流程

Android View管线完全在Java中运行。只有最终的绘制调用跨越到原生层：

```
1. View.measure()     ← 纯Java，在Dalvik中运行，无需修改
2. View.layout()      ← 纯Java，在Dalvik中运行，无需修改
3. View.draw(canvas)  ← 纯Java，调用Canvas方法
   ├── drawBackground(canvas)
   ├── onDraw(canvas)           ← 应用的自定义绘制代码
   ├── dispatchDraw(canvas)     ← ViewGroup绘制子View
   │   └── 递归调用child.draw(canvas)
   └── drawForeground(canvas)
4. Canvas.drawXxx()   ← JNI桥接到OH_Drawing  ← 唯一需要桥接的地方
```

**这意味着：**
- 所有Android View都能工作（TextView、RecyclerView、WebView、自定义View）
- 所有布局都能工作（LinearLayout、ConstraintLayout、CoordinatorLayout）
- 所有动画都能工作（ValueAnimator、ObjectAnimator）
- 无范式转换 —— 命令式View代码作为命令式View代码运行

---

## 4. 平台桥接

### 4.1 桥接清单

仅需约15个系统级边界的桥接。这些边界以上的一切都是纯Java，在Dalvik中原样运行。

| # | 桥接 | Android端 | OH端 | 复杂度 |
|---|------|----------|------|:------:|
| 1 | 渲染 | Canvas/Skia | OH_Drawing + XComponent | 中 |
| 2 | 显示 | SurfaceFlinger | OHNativeWindow | 中 |
| 3 | 输入 | InputDispatcher | XComponent触摸事件 | 低 |
| 4 | 音频 | AudioTrack/Record | OH Audio渲染/采集 | 中 |
| 5 | 相机 | Camera2 HAL | @ohos.multimedia.camera | 高 |
| 6 | 网络 | java.net.Socket | OH Socket/Net | 低 |
| 7 | 定位 | LocationManager | @ohos.geoLocationManager | 低 |
| 8 | 蓝牙 | BT HAL | @ohos.bluetooth.* | 中 |
| 9 | 传感器 | SensorService | @ohos.sensor | 低 |
| 10 | 存储 | VFS / SQLite | @ohos.file.fs + SQLite | 低 |
| 11 | 通信 | RIL | @ohos.telephony.* | 中 |
| 12 | 通知 | NotificationService | @ohos.notificationManager | 低 |
| 13 | 权限 | PackageManager | @ohos.abilityAccessCtrl | 低 |
| 14 | 剪贴板 | ClipboardService | @ohos.pasteboard | 低 |
| 15 | 振动 | VibratorService | @ohos.vibrator | 低 |

### 4.2 无法桥接的功能

| 功能 | 原因 | 影响 |
|------|------|------|
| MediaDrm / Widevine | 需要Google认证 + TEE | Netflix、YouTube Premium无法使用 |
| Google Play Services | 专有闭源 | 部分应用崩溃；使用microG替代 |
| 多进程应用 | 引擎为单进程 | <5%的应用受影响 |
| 跨应用Intent | 无其他Android应用可解析 | 深链接失效；优雅处理 |

---

## 5. 方案对比：引擎 vs 容器 vs API逐一映射

| 维度 | 引擎（本方案） | 容器（Anbox风格） | API逐一映射 |
|------|:-------------:|:----------------:|:----------:|
| 应用兼容性 | ~90-95% | ~99% | ~30-50% |
| 需要修改应用代码 | **不需要** | **不需要** | 需要大量修改 |
| 内存开销 | **~85 MB** | 500 MB - 1 GB | ~50 MB |
| 存储开销 | **~85 MB** | 2-4 GB系统镜像 | ~50 MB |
| 性能 | **原生性能** | 容器开销 | 原生性能 |
| OH集成度 | **深度集成** | 隔离（两个世界） | 深度集成 |
| 用户体验 | **Android应用感觉像原生OH应用** | 应用感觉像"外来者" | 取决于实现 |
| 通知栏 | **与OH共享** | 分离/重复 | 共享 |
| 电池效率 | **单OS栈** | 双OS开销 | 单OS |
| $50手机可行 | **是** | 否（内存/存储不足） | 是 |
| 开发周期 | 6-12个月 | 2-3个月 | 12-18个月 |
| GMS/Play服务 | 否（使用microG） | 可能（需授权） | 否 |
| DRM/Widevine | 否 | 可能 | 否 |
| 监管合规 | **单一操作系统** | 双OS顾虑 | 单一操作系统 |

### 5.1 引擎方案的优势

1. **设备经济性** —— OH面向从$50手机到物联网的广泛设备。容器增加500MB+内存和2-4GB存储。引擎只增加85MB。这决定了能否在低端设备上运行。

2. **用户体验** —— 容器中的应用与OH处于平行世界。复制粘贴笨拙，通知重复，应用切换不流畅。引擎应用是OH的一等公民。

3. **监管合规** —— 容器实质上是"运行两个操作系统"。某些市场可能从安全性、数据主权或竞争角度审查这一点。引擎是单一OS加兼容库 —— 标准做法。

4. **性能** —— 无虚拟化开销，无容器与宿主之间的IPC，无重复的系统服务消耗CPU/内存。

### 5.2 容器仍然胜出的场景

1. **DRM应用** —— Netflix、YouTube Premium需要Widevine。
2. **GMS依赖应用** —— 部分应用没有Google Play Services会崩溃。
3. **快速演示** —— 容器可以在几周内引导Android镜像。

### 5.3 推荐策略

**主路径：引擎** —— 为90-95%的应用构建Android即引擎
**备选：轻量容器** —— 为需要DRM/GMS的5%应用提供容器选项

---

## 6. 验证：13个真实APK分析

### 6.1 分析的应用

| 应用 | DEX文件数 | 唯一框架API | 引擎覆盖率 | 关键桥接需求 |
|------|--------:|----------:|:---------:|------------|
| 抖音/TikTok | 45 | 18,225 | 17.2% | 相机、媒体、图形、传感器 |
| Instagram | 17 | 18,531 | 17.0% | 相机、媒体、图形、定位 |
| YouTube | 7 | 26,957 | 9.9% | 媒体、DRM(!)、图形 |
| Netflix | 8 | 22,988 | 11.1% | 媒体、DRM(!)、网络 |
| Spotify | 10 | 23,496 | 10.7% | 音频、蓝牙、媒体 |
| Facebook | 2 | 3,669 | 30.0% | WebView、网络 |
| Google Maps | 10 | 31,838 | 8.9% | 定位、传感器、图形 |
| Zoom | 13 | 160,519 | 1.9% | 相机、音频、网络 |
| Grab | 21 | 111,675 | 3.0% | 定位、网络、安全 |
| Duolingo | 12 | 76,997 | 3.5% | 音频、通知 |
| Uber Driver | 24 | 1,384 | 13.5% | 定位、网络 |
| PayPal | 42 | 516 | 32.8% | 安全、网络、NFC |
| Amazon | 8 | 27,576 | 7.1% | WebView、网络 |

### 6.2 Tier 4差距分解

"未映射差距"的真实构成（以YouTube为参考，24,296个未映射API）：

| 类别 | 数量 | 占比 | 引擎能否处理？ |
|------|-----:|-----:|:------------:|
| 噪声（专有库、AndroidX、Dalvik） | 17,026 | 70.1% | **能** —— 在Dalvik中运行 |
| UI（View/Widget/Animation/Text） | 2,841 | 11.7% | **能** —— Android View管线 |
| OH有能力（DB映射差距） | 2,867 | 11.8% | **能** —— 平台桥接 |
| Java运行时（java.*/javax.*） | 1,529 | 6.3% | **能** —— 在Dalvik中运行 |
| 真正的平台差距（DRM） | 33 | 0.1% | **不能** —— 需要容器备选 |

**引擎架构下，99.9%的"差距"都能处理。只有DRM（0.1%）是真正的阻断因素。**

---

## 7. 应用迁移优先级

| 优先级 | 应用 | 为什么先做 |
|--------|------|-----------|
| P0 | PayPal、Amazon、McDonald's | 桥接面最小，无DRM，无重度Native |
| P1 | Facebook、Instagram、TikTok | 需要相机+媒体桥接，用户基数大 |
| P2 | Duolingo、Maps、Uber、Grab | 需要定位+音频桥接 |
| P3 | Spotify | 需要音频+蓝牙桥接 |
| P4 | Zoom、Teams | 相机+音频+网络（实时通信） |
| 最后 | Netflix、YouTube | **DRM阻断** —— 需要容器备选 |

---

## 8. 执行路线图

### 第一阶段：基础（第1-3个月）
- 完成Dalvik/ART VM在OH上的稳定性
- 构建渲染桥接（Canvas → OH_Drawing）
- 构建显示桥接（Surface → XComponent）
- 构建输入桥接（触摸 → MotionEvent）
- 构建MiniActivityManager
- **里程碑：第一个Android应用在OH上渲染**

### 第二阶段：核心桥接（第3-6个月）
- 音频桥接（播放+录制）
- 网络桥接（HTTP+Socket）
- 存储桥接（文件系统+SQLite）
- SharedPreferences桥接
- **里程碑：PayPal/Amazon可以启动并显示UI**

### 第三阶段：设备桥接（第6-9个月）
- 相机桥接
- 定位桥接
- 蓝牙桥接
- 传感器桥接
- 通知桥接
- **里程碑：Instagram/TikTok相机功能可用**

### 第四阶段：优化（第9-12个月）
- 性能优化（GPU渲染路径）
- 多窗口支持
- 后台服务桥接
- WebView桥接（封装ArkWeb）
- 权限映射
- **里程碑：分析的13个应用中10个可运行**

### 第五阶段：备选容器（并行）
- 为DRM/GMS应用提供轻量级Android容器
- 最小系统镜像（精简AOSP）
- **里程碑：Netflix/YouTube通过容器运行**

---

## 9. 客观风险评估

### 9.1 这个方案的真实挑战

本文档不回避困难。以下是引擎方案面临的真实挑战：

**挑战1：Android框架不是设计来被嵌入的**
- Flutter从一开始就设计为可嵌入库，边界清晰
- Android框架假设自己是操作系统，深度耦合Linux内核特性
- 剥离OS依赖并作为库运行需要大量工程工作
- 风险：某些框架内部假设可能导致微妙的兼容性问题

**挑战2：Native库（NDK .so文件）兼容性**
- Android NDK库链接到bionic libc，OH使用musl libc
- TikTok有45个DEX文件，但也附带大量Native .so库
- 需要bionic兼容层或二进制翻译
- 这是引擎方案中风险最高的部分之一

**挑战3：5%的应用无法支持**
- 需要Google Play Services的应用（microG只有约80%兼容性）
- 需要Widevine DRM的应用（Netflix、YouTube Premium）
- 使用多进程的应用（引擎是单进程模型）
- 对于这些应用，容器方案是更好的选择

**挑战4：工程规模**
- 6-12个月的开发周期
- 需要深入理解AOSP源码和OH NDK
- 容器方案可以在2-3个月内产出演示
- 投资回报需要更长时间才能显现

### 9.2 为什么仍然值得

尽管有这些挑战，引擎方案仍然是正确的长期架构，因为：

1. **设备覆盖** —— OH的目标市场包含大量低端设备，容器的内存/存储开销在这些设备上不可接受
2. **用户体验** —— 在竞争激烈的市场中，"外来感"的容器体验会损害用户留存
3. **可维护性** —— 85MB引擎比2-4GB系统镜像更容易更新和维护
4. **Flutter已验证** —— OH生态已接受Flutter作为一等运行时，Android引擎是同一类别

### 9.3 成功指标

| 指标 | 目标 | 测量方式 |
|------|------|---------|
| 可启动的应用 | Top 100中的80% | APK分析 + 引擎测试 |
| 完全可用的应用 | Top 100中的60% | 手动测试 |
| 引擎内存占用 | <100 MB | 系统监控 |
| 应用启动时间 | <3秒 | 计时测量 |
| UI滚动帧率 | 60 fps | GPU分析 |
| 桥接数量 | <20个 | 架构审查 |
| 代码量 | <5万行 | 代码统计 |
