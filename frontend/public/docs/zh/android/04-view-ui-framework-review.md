# Android 11 (AOSP) 代码审查：View 系统和 UI 框架

**审查日期：** 2026-03-10
**源代码基础：** Android 11 (API Level 30) AOSP
**范围：** View 层级结构、控件库、动画框架、图形子系统

---

## 目录

1. [概述](#1-概述)
2. [核心 View 层级结构](#2-核心-view-层级结构)
   - 2.1 [View.java](#21-viewjava)
   - 2.2 [ViewGroup.java](#22-viewgroupjava)
3. [Window 和窗口管理](#3-window-和窗口管理)
   - 3.1 [Window.java](#31-windowjava)
   - 3.2 [WindowManager.java](#32-windowmanagerjava)
4. [布局加载](#4-布局加载)
5. [输入和事件处理](#5-输入和事件处理)
   - 5.1 [InputEvent.java](#51-inputeventjava)
   - 5.2 [MotionEvent.java](#52-motioneventjava)
   - 5.3 [KeyEvent.java](#53-keyeventjava)
   - 5.4 [GestureDetector.java](#54-gesturedetectorjava)
   - 5.5 [触摸事件分发机制](#55-触摸事件分发机制)
6. [控件库](#6-控件库)
   - 6.1 [TextView.java](#61-textviewjava)
   - 6.2 [EditText.java](#62-edittextjava)
   - 6.3 [Button.java](#63-buttonjava)
   - 6.4 [ImageView.java](#64-imageviewjava)
7. [布局管理器](#7-布局管理器)
   - 7.1 [FrameLayout.java](#71-framelayoutjava)
   - 7.2 [LinearLayout.java](#72-linearlayoutjava)
   - 7.3 [RelativeLayout.java](#73-relativelayoutjava)
   - 7.4 [ConstraintLayout](#74-constraintlayout)
8. [动画框架](#8-动画框架)
   - 8.1 [Animator.java](#81-animatorjava)
   - 8.2 [ValueAnimator.java](#82-valueanimatorjava)
   - 8.3 [ObjectAnimator.java](#83-objectanimatorjava)
   - 8.4 [AnimatorSet.java](#84-animatorsetjava)
   - 8.5 [ViewPropertyAnimator](#85-viewpropertyanimator)
9. [图形子系统](#9-图形子系统)
   - 9.1 [Canvas.java](#91-canvasjava)
   - 9.2 [Paint.java](#92-paintjava)
   - 9.3 [Bitmap.java](#93-bitmapjava)
   - 9.4 [Drawable.java](#94-drawablejava)
10. [Surface 渲染](#10-surface-渲染)
    - 10.1 [SurfaceView.java](#101-surfaceviewjava)
    - 10.2 [TextureView.java](#102-textureviewjava)
    - 10.3 [Choreographer.java](#103-choreographerjava)
11. [RecyclerView（内部）](#11-recyclerview内部)
12. [设计模式总结](#12-设计模式总结)
13. [性能注意事项](#13-性能注意事项)
14. [隐藏和系统 API 总结](#14-隐藏和系统-api-总结)

---

## 1. 概述

Android View 系统是 Android 上所有 UI 渲染和交互的基础。其核心实现了树状层级结构（组合模式），其中 `View` 是叶子节点，`ViewGroup` 是复合容器。该系统横跨 `android.view`、`android.widget`、`android.animation` 和 `android.graphics` 包中约 100,000 多行 Java 代码。

**关键架构特性：**
- **单线程 UI 模型**：所有 View 操作必须在主（UI）线程上执行，通过 `@UiThread` 注解和运行时检查强制执行。
- **三阶段渲染管线**：measure -> layout -> draw，由 `ViewRootImpl` 和 `Choreographer` 与 VSYNC 同步协调。
- **通过责任链进行事件分发**：触摸事件通过 `dispatchTouchEvent()` / `onInterceptTouchEvent()` / `onTouchEvent()` 自顶向下流动。
- **硬件加速渲染**：默认渲染路径使用 `RenderNode` 对象和 `RecordingCanvas` 进行 GPU 支持的绘制。
- **广泛的向后兼容性**：普遍使用 `@UnsupportedAppUsage` 注解和基于 SDK 版本的行为控制，以保持与早期 Android 版本的兼容性。

---

## 2. 核心 View 层级结构

### 2.1 View.java

**文件：** `frameworks/base/core/java/android/view/View.java`（30,408 行）

#### 类用途
`View` 是所有 UI 组件的基本构建块。它表示屏幕上的一个矩形区域，负责绘制自身内容和处理用户交互。每个控件（Button、TextView、ImageView）和每个布局（LinearLayout、FrameLayout）最终都继承自 `View`。

**类声明（第 130 行附近，在大量 Javadoc 之后）：**
```java
@UiThread
public class View implements Drawable.Callback, KeyEvent.Callback,
        AccessibilityEventSource
```

#### 应用开发者的关键公共 API

**可见性和定位：**
- `setVisibility(int)`（第 11718 行）—— `VISIBLE`、`INVISIBLE`、`GONE`
- `setAlpha(float)`、`setTranslationX/Y/Z(float)`、`setRotation(float)`、`setScaleX/Y(float)`
- `setPadding(int, int, int, int)`、`setBackgroundColor(int)`、`setBackground(Drawable)`
- `setElevation(float)` —— Material Design 阴影的 Z 排序

**事件监听器：**
- `setOnClickListener(OnClickListener)`（第 7235 行）
- `setOnTouchListener(OnTouchListener)`（第 7721 行）
- `setOnLongClickListener(OnLongClickListener)`
- `setOnFocusChangeListener(OnFocusChangeListener)`
- `setOnDragListener(OnDragListener)`
- `setOnScrollChangeListener(OnScrollChangeListener)`（第 16328 行）

**焦点管理：**
- `requestFocus()`、`clearFocus()`、`isFocused()`、`isFocusable()`
- `setFocusableInTouchMode(boolean)`

**动画：**
- `animate()`（第 28326 行）—— 返回 `ViewPropertyAnimator` 用于流式动画 API

#### Measure-Layout-Draw 生命周期

这是创建自定义视图的应用开发者最关键的机制。

**阶段 1：测量**（`measure()` 在第 25429 行，`onMeasure()` 在第 25540 行）

`measure(int widthMeasureSpec, int heightMeasureSpec)` 是 `final` 的 —— 不能被重写。它委托给 `onMeasure()`，子类必须重写并调用 `setMeasuredDimension()`。

```
measure() [final]
  --> onMeasure(widthMeasureSpec, heightMeasureSpec) [重写此方法]
       --> setMeasuredDimension(width, height) [必须调用]
```

`MeasureSpec` 类（第 28104 行）在单个 int 中编码大小和模式：
- `UNSPECIFIED`（0）：父容器不施加约束
- `EXACTLY`（1 << 30）：父容器已确定精确大小
- `AT_MOST`（2 << 30）：子视图最大可达指定大小

MeasureSpec 通过以下方式将模式打包到高 2 位，大小打包到低 30 位：
```java
public static int makeMeasureSpec(int size, int mode) {
    return (size & ~MODE_MASK) | (mode & MODE_MASK);
}
```

**性能说明（第 25440-25492 行）：** `measure()` 实现了一个 `LongSparseLongArray` 缓存（`mMeasureCache`），以 `(widthSpec << 32 | heightSpec)` 为键。当规格未改变时，可避免冗余测量。

**阶段 2：布局**（`layout()` 在第 22829 行，`onLayout()` 在第 22934 行）

```
layout(l, t, r, b) [不要重写]
  --> setFrame(l, t, r, b) [设置 mLeft/mTop/mRight/mBottom]
  --> onLayout(changed, l, t, r, b) [在 ViewGroup 子类中重写]
```

`layout()` 不是 `final` 的，但 Javadoc 明确说明"派生类不应重写此方法"（第 22818 行）。基类 `View` 中的 `onLayout()` 为空（第 22934 行）—— 只有 ViewGroup 子类需要定位子视图。

**阶段 3：绘制**（`draw()` 在第 22322 行，`onDraw()` 在第 19909 行）

`draw()` 方法记录了精确的 7 步绘制顺序（第 22326-22336 行）：
1. 绘制背景
2. 为渐隐边缘保存画布图层（如需要）
3. 绘制视图内容（`onDraw(canvas)`）
4. 绘制子视图（`dispatchDraw(canvas)`）
5. 绘制渐隐边缘并恢复图层（如需要）
6. 绘制装饰（滚动条、前景）
7. 绘制默认焦点高亮

对于常见情况（无渐隐边缘），步骤 2 和 5 被跳过（第 22348 行）。

**失效机制：**
- `invalidate()`（第 18469 行）—— 标记视图需要重绘
- `requestLayout()`（第 25371 行）—— 标记需要重新测量和重新布局，通过 `mParent.requestLayout()` 向上传播
- `postInvalidate()` —— 从后台线程安全地触发失效

#### 监听器接口（在 View 中定义）

| 接口 | 行号 | 用途 |
|---|---|---|
| `OnClickListener` | 28517 | 单击/点击事件 |
| `OnLongClickListener` | 28463 | 长按事件 |
| `OnTouchListener` | 28411 | 原始触摸事件（优先于 `onTouchEvent`） |
| `OnHoverListener` | 28429 | 鼠标悬停事件 |
| `OnDragListener` | 28486 | 拖放事件 |
| `OnFocusChangeListener` | 28504 | 焦点获取/丢失 |
| `OnKeyListener` | 28371 | 硬件按键事件 |
| `OnScrollChangeListener` | 28328 | 滚动位置变化 |
| `OnLayoutChangeListener` | 28345 | 布局边界变化 |
| `OnAttachStateChangeListener` | 28585 | 窗口附加/分离 |
| `OnApplyWindowInsetsListener` | 28610 | 系统窗口内边距 |
| `OnContextClickListener` | 28529 | 上下文点击（右键点击） |
| `OnCapturedPointerListener` | 27865 | 捕获的指针事件 |
| `OnSystemUiVisibilityChangeListener` | 28568 | 系统 UI 可见性变化 |
| `OnUnhandledKeyEventListener` | 28394 | 未处理的按键事件 |

#### 内部 ListenerInfo 模式（第 4672 行）

View 使用 `ListenerInfo` 内部类来持有所有监听器引用。当没有设置监听器时，它是惰性分配的以节省内存：
```java
static class ListenerInfo {
    OnFocusChangeListener mOnFocusChangeListener;
    OnClickListener mOnClickListener;
    OnLongClickListener mOnLongClickListener;
    OnTouchListener mOnTouchListener;
    // ... 更多
}
```

#### 值得注意的隐藏/系统 API

- `@UnsupportedAppUsage` 被广泛使用（数百个实例），用于标记应用不应访问但历史上可以通过反射访问的字段和方法。
- `canReceivePointerEvents()`（第 14341 行，`@hide`）—— 检查视图是否 VISIBLE 或有动画。
- `makeSafeMeasureSpec()`（第 28169 行，`@hide`）—— 兼容性包装器，为旧应用将 UNSPECIFIED 规格清零。
- `setFrame()` —— 设置位置边界的内部方法。
- 各种 `PFLAG_*` 常量通过 `mPrivateFlags`、`mPrivateFlags2`、`mPrivateFlags3` 中的位标志控制内部状态。

---

### 2.2 ViewGroup.java

**文件：** `frameworks/base/core/java/android/view/ViewGroup.java`（9,277 行）

#### 类用途
`ViewGroup` 是所有布局容器的抽象基类。它继承 `View` 并实现 `ViewParent` 和 `ViewManager`。它管理子 View 的集合，并处理测量、布局和绘制操作向子视图的委托。

```java
@UiThread
public abstract class ViewGroup extends View implements ViewParent, ViewManager
```

#### 关键公共 API

**子视图管理：**
- `addView(View child)`（第 4975 行）—— 多个重载版本，接受索引、宽度/高度、LayoutParams
- `addView(View child, int index, LayoutParams params)`（第 5050 行）—— 核心 addView 实现
- `removeView(View view)`（第 5461 行）
- `removeViewAt(int index)`（第 5506 行）
- `removeAllViews()`
- `getChildCount()`（第 6872 行）
- `getChildAt(int index)`（第 6883 行）

**测量辅助方法：**
- `measureChildren(int widthMeasureSpec, int heightMeasureSpec)`（第 6899 行）
- `measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec)`（第 6919 行）
- `measureChildWithMargins(View child, ...)`（第 6945 行）—— 考虑父容器内边距和子视图外边距

**绘制：**
- `drawChild(Canvas canvas, View child, long drawingTime)`（第 4515 行）
- `setClipChildren(boolean)`、`setClipToPadding(boolean)`

**布局动画：**
- `setLayoutTransition(LayoutTransition)` —— 启用子视图添加/移除的动画
- `setLayoutAnimation(LayoutAnimationController)` —— 旧版基于 XML 的布局动画

#### 触摸事件分发（责任链）

ViewGroup 中架构上最重要的代码是 `dispatchTouchEvent()`（第 2633 行）。这实现了完整的触摸事件分发算法：

```
ViewGroup.dispatchTouchEvent(MotionEvent)
  |
  |-- 1. 在 ACTION_DOWN 时：cancelAndClearTouchTargets()，resetTouchState()
  |
  |-- 2. 检查拦截：
  |      if (ACTION_DOWN || mFirstTouchTarget != null) {
  |          if (!FLAG_DISALLOW_INTERCEPT)
  |              intercepted = onInterceptTouchEvent(ev)  // <-- 可以窃取事件
  |      } else {
  |          intercepted = true  // 没有目标，继续拦截
  |      }
  |
  |-- 3. 如果未拦截，查找子目标：
  |      for (i = childrenCount - 1; i >= 0; i--)  // 从前到后的顺序
  |          if (child.canReceivePointerEvents() && isTransformedTouchPointInView())
  |              if (dispatchTransformedTouchEvent(ev, child))
  |                  addTouchTarget(child)
  |                  break
  |
  |-- 4. 分发到触摸目标：
  |      if (mFirstTouchTarget == null)
  |          dispatchTransformedTouchEvent(ev, null)  // 作为普通视图处理
  |      else
  |          for each target in linked list:
  |              dispatchTransformedTouchEvent(ev, target.child)
```

**`onInterceptTouchEvent(MotionEvent)`**（第 3282 行）—— 默认返回 `false`。子类（如 `ScrollView`）重写以拦截滚动手势。当在手势进行中开始拦截时，先前被定位的子视图会收到 `ACTION_CANCEL`。

**触摸目标管理使用单链表**（`mFirstTouchTarget`），每个 `TouchTarget` 持有一个子 View 引用和一个指针 ID 位掩码。这在设置了 `FLAG_SPLIT_MOTION_EVENTS` 时（第 2687 行）实现了多点触控分割运动事件分发。

**`requestDisallowInterceptTouchEvent(boolean)`** —— 允许子视图阻止父容器拦截（常用于嵌套滚动场景）。

#### 焦点管理（第 3306 行）

`requestFocus()` 遵循 `descendantFocusability`：
- `FOCUS_BEFORE_DESCENDANTS`：ViewGroup 首先尝试获取焦点
- `FOCUS_AFTER_DESCENDANTS`：子视图优先
- `FOCUS_BLOCK_DESCENDANTS`：只有 ViewGroup 可以接收焦点

#### 设计模式

- **组合模式**：ViewGroup 是一个包含其他 View 的 View，形成树结构
- **责任链**：触摸事件通过 `dispatchTouchEvent()` 在视图层级中流动
- **模板方法**：`onMeasure()`、`onLayout()`、`onDraw()` 是由框架调用的模板方法
- **观察者模式**：`OnHierarchyChangeListener` 用于子视图添加/移除通知

---

## 3. Window 和窗口管理

### 3.1 Window.java

**文件：** `frameworks/base/core/java/android/view/Window.java`（2,656 行）

#### 类用途
`Window` 是一个抽象基类，表示具有视觉策略（背景、标题区域、按键处理）的顶级窗口。唯一的具体实现是 `PhoneWindow`（第 64-65 行 Javadoc）。每个 Activity 拥有一个 Window，Window 又拥有根 View 层级结构。

#### 关键特性/常量

**窗口特性（第 69-165 行）：**
| 常量 | 值 | 用途 |
|---|---|---|
| `FEATURE_NO_TITLE` | 1 | 移除标题栏 |
| `FEATURE_ACTION_BAR` | 8 | 启用 Action Bar |
| `FEATURE_ACTION_BAR_OVERLAY` | 9 | Action Bar 覆盖在内容上 |
| `FEATURE_CONTENT_TRANSITIONS` | 12 | 启用内容过渡动画 |
| `FEATURE_ACTIVITY_TRANSITIONS` | 13 | 启用 Activity 场景过渡 |

#### Window.Callback 接口（约第 430 行开始）

`Callback` 接口将 Window 连接到其宿主（通常是 Activity）：
- `dispatchKeyEvent(KeyEvent)` —— 路由按键事件
- `dispatchTouchEvent(MotionEvent)` —— 路由触摸事件
- `onWindowFocusChanged(boolean)` —— 焦点变化通知
- `onAttachedToWindow()` / `onDetachedFromWindow()` —— 生命周期回调
- `onWindowStartingActionMode(ActionMode.Callback, int)` —— Action Mode 管理
- `onProvideKeyboardShortcuts(...)` —— 键盘快捷键声明

#### 关键公共 API
- `setContentView(int layoutResID)` / `setContentView(View view)` —— 抽象方法，由 PhoneWindow 实现
- `setStatusBarColor(int)`、`setNavigationBarColor(int)` —— 系统栏主题化
- `requestFeature(int)` —— 必须在 `setContentView()` 之前调用
- `getDecorView()` —— 返回顶级 DecorView
- `setWindowAnimations(int)` —— 过渡动画

---

### 3.2 WindowManager.java

**文件：** `frameworks/base/core/java/android/view/WindowManager.java`（3,870 行）

#### 类用途
`WindowManager` 是应用与窗口管理器服务交互的接口。它继承 `ViewManager`（定义了 `addView()`、`updateViewLayout()`、`removeView()`）。

```java
@SystemService(Context.WINDOW_SERVICE)
public interface WindowManager extends ViewManager
```

#### 关键 API
- `getDefaultDisplay()` —— 获取此 WindowManager 关联的 Display
- `removeViewImmediate(View)` —— 同步移除（与正常异步 `removeView()` 相对）

#### WindowManager.LayoutParams（文件的主体部分）

内部类 `LayoutParams` 继承 `ViewGroup.LayoutParams`，添加了窗口特定的属性。关键窗口类型（许多是 `@hide`）：

**应用窗口（1-99）：**
- `TYPE_BASE_APPLICATION`（1）—— 基础 Activity 窗口
- `TYPE_APPLICATION`（2）—— 普通应用窗口
- `TYPE_APPLICATION_STARTING`（3）—— 启动画面

**系统窗口（2000+，大多是 `@SystemApi` 或 `@hide`）：**
- `TYPE_STATUS_BAR`（2000）
- `TYPE_SEARCH_BAR`（2001）
- `TYPE_TOAST`（2005）
- `TYPE_SYSTEM_OVERLAY`（2006，已废弃）
- `TYPE_APPLICATION_OVERLAY`（2038）—— 针对 O+ 应用替代 `SYSTEM_ALERT`

**窗口标志（位掩码）：**
- `FLAG_NOT_FOCUSABLE` —— 窗口不接收输入焦点
- `FLAG_NOT_TOUCHABLE` —— 窗口不接收触摸事件
- `FLAG_FULLSCREEN` —— 隐藏状态栏
- `FLAG_KEEP_SCREEN_ON` —— 防止屏幕休眠
- `FLAG_HARDWARE_ACCELERATED` —— 启用硬件加速渲染
- `FLAG_SECURE` —— 防止截屏/录屏
- `FLAG_TRANSLUCENT_STATUS` / `FLAG_TRANSLUCENT_NAVIGATION` —— 透明栏

**隐藏过渡常量（第 137-225 行）** 定义了系统内部使用的窗口过渡类型（`TRANSIT_ACTIVITY_OPEN`、`TRANSIT_TASK_CLOSE` 等），用于 Activity 动画。

---

## 4. 布局加载

### LayoutInflater.java

**文件：** `frameworks/base/core/java/android/view/LayoutInflater.java`（1,360 行）

#### 类用途
将布局 XML 文件实例化为 View 对象树。不直接使用；通过 `Activity.getLayoutInflater()` 或 `Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)` 获取。

```java
@SystemService(Context.LAYOUT_INFLATER_SERVICE)
public abstract class LayoutInflater
```

#### 关键公共 API

**inflate 方法：**
- `inflate(@LayoutRes int resource, @Nullable ViewGroup root)`（第 478 行）
- `inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot)`（第 519 行）
- `inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot)`（第 627 行）

`root` 参数有双重用途：
1. 当 `attachToRoot=true` 时：加载的视图被添加到 root
2. 当 `root != null` 但 `attachToRoot=false` 时：root 为加载的视图根元素提供 LayoutParams

**工厂系统：**
- `setFactory(Factory factory)` —— 只能调用一次（第 106 行，`mFactorySet` 守护）
- `setFactory2(Factory2 factory)` —— 首选；接收父 View 上下文
- `cloneInContext(Context)` —— 为不同的 Context 创建新的 LayoutInflater

#### 设计模式
- **工厂方法**：`Factory` / `Factory2` 接口允许自定义 View 创建
- **享元**：`sConstructorMap`（第 135 行）缓存 `Constructor` 对象以避免重复反射

#### 性能特性
- **预编译布局**（第 83-87 行）：实验性功能，使用预编译的 DEX 文件替代运行时 XML 解析
- **构造函数缓存**：静态 `HashMap<String, Constructor<? extends View>>` 避免反射开销
- **特殊标签处理**：`<merge>`、`<include>`、`<requestFocus>` 和 `<tag>` 在内联处理，无需构造函数查找

#### 值得注意的实现细节
- View 构造函数必须具有签名 `(Context, AttributeSet)`（第 131-132 行）
- 特殊标签 `<blink>`（第 144 行，`TAG_1995`）创建一个使子视图闪烁的 BlinkLayout —— 这是对 HTML `<blink>` 标签的彩蛋

---

## 5. 输入和事件处理

### 5.1 InputEvent.java

**文件：** `frameworks/base/core/java/android/view/InputEvent.java`（272 行）

#### 类用途
所有输入事件（按键事件和运动事件）的抽象基类。实现 `Parcelable` 用于 IPC 传输。

**关键 API：**
- `getDeviceId()` —— 源设备 ID
- `getDevice()` —— 返回 `InputDevice` 对象
- `getSource()` —— 事件来源（触摸、鼠标、键盘等）
- `isFromSource(int source)` —— 使用位掩码检查来源：`(getSource() & source) == source`
- `getEventTime()` —— 时间戳，以 `uptimeMillis` 为单位

**隐藏 API：**
- `setSource(int)`、`setDisplayId(int)` —— `@hide`，供框架内部使用
- `getDisplayId()` —— `@hide`，标识事件来自哪个显示器
- `getId()` —— `@hide`，由 CSPRNG 生成，用于跨进程追踪
- `recycle()` / `recycleIfNeededAfterDispatch()` —— `@hide`，对象池支持
- `isTainted()` / `setTainted(boolean)` —— `@hide`，标记不一致的事件序列

**设计：** 使用原子序列号（`mNextSeq`）进行本地事件排序（第 35 行）。

---

### 5.2 MotionEvent.java

**文件：** `frameworks/base/core/java/android/view/MotionEvent.java`（4,179 行）

#### 类用途
报告来自触摸屏、鼠标、触控笔、轨迹球和摇杆的运动事件。通过指针追踪支持多点触控，并为提高效率而批量处理多个 `ACTION_MOVE` 采样。

#### 关键动作常量（第 200-310 行）

| 常量 | 值 | 描述 |
|---|---|---|
| `ACTION_DOWN` | 0 | 第一根手指触摸屏幕 |
| `ACTION_UP` | 1 | 最后一根手指离开屏幕 |
| `ACTION_MOVE` | 2 | 手指移动（可能包含批量历史记录） |
| `ACTION_CANCEL` | 3 | 手势被中止 |
| `ACTION_OUTSIDE` | 4 | 在窗口边界外触摸 |
| `ACTION_POINTER_DOWN` | 5 | 额外的手指触摸屏幕 |
| `ACTION_POINTER_UP` | 6 | 非主手指抬起 |
| `ACTION_HOVER_MOVE` | 7 | 鼠标/触控笔无接触移动 |
| `ACTION_SCROLL` | 8 | 滚轮/手势滚动 |
| `ACTION_HOVER_ENTER` | 9 | 指针进入视图边界 |
| `ACTION_HOVER_EXIT` | 10 | 指针离开视图边界 |
| `ACTION_BUTTON_PRESS` | 11 | 按钮按下（鼠标） |
| `ACTION_BUTTON_RELEASE` | 12 | 按钮释放（鼠标） |

**多点触控：** `ACTION_POINTER_DOWN/UP` 在 `getAction()` 的高位中编码指针索引。始终使用 `getActionMasked()` 和 `getActionIndex()` 来解码。

#### 多点触控关键 API
- `getPointerCount()` —— 活跃指针数量
- `getPointerId(int pointerIndex)` —— 跨事件追踪的稳定 ID
- `findPointerIndex(int pointerId)` —— 反向查找：ID 到当前索引
- `getX(int pointerIndex)`、`getY(int pointerIndex)` —— 每个指针的坐标
- `getToolType(int pointerIndex)` —— `TOOL_TYPE_FINGER`、`TOOL_TYPE_STYLUS`、`TOOL_TYPE_MOUSE`
- `getPressure(int)`、`getSize(int)` —— 压力和接触面积
- `getButtonState()` —— 鼠标按钮状态

#### 历史数据（批处理）
对于 `ACTION_MOVE`，系统批量处理中间采样：
- `getHistorySize()` —— 批量历史采样数量
- `getHistoricalX(int pointerIndex, int pos)`、`getHistoricalY(...)` —— 过去的坐标
- `getHistoricalEventTime(int pos)` —— 批量采样的时间戳

按时间顺序处理所有采样：先历史记录，然后当前。

#### 性能特性
- 对坐标访问器使用带 `@CriticalNative` 和 `@FastNative` 注解的 JNI
- 通过 `obtain()` / `recycle()` 模式的对象池来最小化 GC 压力
- 事件数据的原生后备存储（指针坐标存储在 C++ 中）

---

### 5.3 KeyEvent.java

**文件：** `frameworks/base/core/java/android/view/KeyEvent.java`（3,166 行）

#### 类用途
表示硬件和虚拟键盘事件。包含 Android 按键代码的完整目录。

**关键动作：**
- `ACTION_DOWN` —— 按键按下
- `ACTION_UP` —— 按键释放
- `ACTION_MULTIPLE` —— 重复按键或字符序列（旧版）

**按键代码（定义了数百个，第 88 行以后）：**
- 导航：`KEYCODE_HOME`（3）、`KEYCODE_BACK`（4）、`KEYCODE_DPAD_*`
- 媒体：`KEYCODE_VOLUME_UP/DOWN`、`KEYCODE_MEDIA_PLAY/PAUSE`
- 字母：`KEYCODE_A` 到 `KEYCODE_Z`
- 系统：`KEYCODE_MENU`、`KEYCODE_SEARCH`、`KEYCODE_POWER`

**重要说明：** `KEYCODE_HOME` 由框架处理，永远不会传递给应用程序（第 101 行）。软键盘输入通常通过 `InputConnection` 传递，而非 `KeyEvent`。

**关键 API：**
- `getKeyCode()`、`getScanCode()`、`getMetaState()`
- `getRepeatCount()` —— `ACTION_DOWN` 的重复次数
- `isShiftPressed()`、`isCtrlPressed()`、`isAltPressed()`
- `getUnicodeChar()` —— 将按键代码 + 元状态映射到 Unicode 字符
- `dispatch(Callback, DispatcherState, Object)` —— 分发到 `Callback` 接口

---

### 5.4 GestureDetector.java

**文件：** `frameworks/base/core/java/android/view/GestureDetector.java`（920 行）

#### 类用途
从原始 `MotionEvent` 流中检测高级手势。提供常见手势的回调接口。

#### OnGestureListener 接口
- `onDown(MotionEvent)` —— 每次触摸从这里开始
- `onShowPress(MotionEvent)` —— 视觉反馈点击（预点击）
- `onSingleTapUp(MotionEvent)` —— 单击确认
- `onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)` —— 拖拽/滚动
- `onLongPress(MotionEvent)` —— 检测到长按
- `onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)` —— 甩动手势

#### OnDoubleTapListener 接口
- `onSingleTapConfirmed(MotionEvent)` —— 单击确认（不会有双击跟随）
- `onDoubleTap(MotionEvent)` —— 检测到双击
- `onDoubleTapEvent(MotionEvent)` —— 双击手势中的事件

#### SimpleOnGestureListener
便利适配器类，用空操作默认实现所有方法，这样你只需重写需要的方法。

**使用模式：**
```java
GestureDetector detector = new GestureDetector(context, new SimpleOnGestureListener() {
    @Override public boolean onFling(...) { /* 处理甩动 */ }
});
// 在你的 View 中：
@Override public boolean onTouchEvent(MotionEvent event) {
    return detector.onTouchEvent(event);
}
```

---

### 5.5 触摸事件分发机制

跨视图层级的完整触摸分发流程：

```
Activity.dispatchTouchEvent(MotionEvent)
  |
  +-- Window.superDispatchTouchEvent(MotionEvent)
       |
       +-- DecorView.superDispatchTouchEvent(MotionEvent)
            |
            +-- ViewGroup.dispatchTouchEvent(MotionEvent)    [上面 2.2 节]
                 |
                 +-- onInterceptTouchEvent(MotionEvent)      [可以窃取事件]
                 |
                 +-- child.dispatchTouchEvent(MotionEvent)   [递归进入子视图]
                      |
                      +-- View.dispatchTouchEvent(MotionEvent)  [第 14275 行]
                           |
                           +-- OnTouchListener.onTouch()     [监听器优先]
                           |
                           +-- onTouchEvent(MotionEvent)     [视图处理器其次]
```

**View.dispatchTouchEvent 中的关键顺序（第 14297-14311 行）：**

1. 安全过滤：`onFilterTouchEventForSecurity()` —— 如果窗口被遮挡且设置了 `FILTER_TOUCHES_WHEN_OBSCURED`，则丢弃事件（防点击劫持保护）
2. 滚动条拖拽处理（如适用）
3. `OnTouchListener.onTouch()` —— 如果返回 `true`，事件被消费
4. `onTouchEvent()` —— 仅在监听器未消费时调用

**嵌套滚动支持**已集成：`stopNestedScroll()` 在 `ACTION_DOWN` 时调用（第 14294 行），在手势结束时也会调用（第 14321-14324 行）。

---

## 6. 控件库

### 6.1 TextView.java

**文件：** `frameworks/base/core/java/android/widget/TextView.java`（13,705 行）

#### 类用途
向用户显示文本，可选地允许编辑。是 `EditText`、`Button` 及其他几个控件的基类。是框架中最大、最复杂的控件之一。

#### 关键公共 API
- `setText(CharSequence)` / `setText(@StringRes int)` —— 设置显示文本
- `getText()` —— 返回当前文本（作为 `CharSequence`，可能是 `Spannable`）
- `setTextColor(int)`、`setTextSize(float)`、`setTypeface(Typeface)`
- `setGravity(int)` —— 视图内的文本对齐
- `setInputType(int)` —— 控制编辑时的键盘类型
- `setLines(int)`、`setMaxLines(int)`、`setEllipsize(TextUtils.TruncateAt)`
- `addTextChangedListener(TextWatcher)` —— 观察文本变化
- `setCompoundDrawables(...)` —— 文本旁边的图标
- `setAutoSizeTextTypeUniformWithConfiguration(...)` —— 自动调整文本大小（API 26+）

#### 文本布局引擎
内部使用 `Layout` 子类：
- `BoringLayout` —— 针对单行、LTR 文本优化
- `StaticLayout` —— 不可变文本布局
- `DynamicLayout` —— 用于可编辑文本，增量更新布局

#### 性能注意事项
- 使用 `PrecomputedText`（API 28+）将文本测量卸载到后台线程
- `BoringLayout` 快速路径避免了简单文本的完整布局计算
- 通过 `Paint.FontMetricsInt` 进行字体度量缓存

---

### 6.2 EditText.java

**文件：** `frameworks/base/core/java/android/widget/EditText.java`（约 160 行）

#### 类用途
对 `TextView` 的薄封装，用于文本输入。源代码注释（第 29 行）明确说明："这应该是对 TextView 的一层*非常*薄的封装。"

**与 TextView 的主要区别：**
- `getDefaultEditable()` 返回 `true`（第 96-98 行）
- `getFreezesText()` 返回 `true`（第 91-93 行）—— 在配置更改时保存文本状态
- `getDefaultMovementMethod()` 返回 `ArrowKeyMovementMethod`
- `getText()` 返回 `Editable`（而不仅仅是 `CharSequence`）
- `setSelection(int start, int stop)` —— 光标/选区定位
- `selectAll()`、`extendSelection(int index)`

**设计：** 展示了模板方法模式 —— EditText 重写 TextView 中的几个行为钩子，而不是重新实现文本编辑。

---

### 6.3 Button.java

**文件：** `frameworks/base/core/java/android/widget/Button.java`（约 155 行）

#### 类用途
按钮控件。继承 `TextView` 并应用适当的默认样式。标注了 `@RemoteView` 以用于 RemoteViews（小组件/通知）。

实际的按钮行为（点击处理、按下状态视觉反馈）完全继承自 `View`。Button 的角色主要是应用正确的默认样式（`buttonStyle`）。

**`onResolvePointerIcon()`** —— 鼠标悬停时返回 `PointerIcon.TYPE_HAND`（手形光标）。

---

### 6.4 ImageView.java

**文件：** `frameworks/base/core/java/android/widget/ImageView.java`（1,730 行）

#### 类用途
显示图片资源（Bitmap、Drawable）。处理图片缩放、着色和宽高比管理。

#### 关键公共 API
- `setImageResource(@DrawableRes int)` —— 从资源 ID 设置
- `setImageDrawable(Drawable)` —— 从 Drawable 设置
- `setImageBitmap(Bitmap)` —— 从 Bitmap 设置（包装为 BitmapDrawable）
- `setImageURI(Uri)` —— 从内容 URI 加载
- `setImageIcon(Icon)` —— 从 Icon 设置（API 23+）
- `setScaleType(ScaleType)` —— 图片缩放行为
- `setImageTintList(ColorStateList)` —— 颜色着色
- `setAdjustViewBounds(boolean)` —— 在测量时保持宽高比
- `setCropToPadding(boolean)` —— 裁剪到内边距区域

#### ScaleType 枚举
| 值 | 行为 |
|---|---|
| `CENTER` | 居中，不缩放 |
| `CENTER_CROP` | 均匀缩放，填满整个视图，裁剪多余部分 |
| `CENTER_INSIDE` | 缩小以适应，居中 |
| `FIT_CENTER` | 缩放以适应边界内，居中（默认） |
| `FIT_START` | 缩放以适应，对齐顶部/左侧 |
| `FIT_END` | 缩放以适应，对齐底部/右侧 |
| `FIT_XY` | 非均匀缩放以精确填充 |
| `MATRIX` | 使用自定义矩阵变换 |

#### 性能注意事项
- 图片缩放使用 `Matrix` 对象，在 `configureBounds()` 中计算一次并重用
- 完成后应调用 `setImageBitmap(null)` 以释放引用
- 大位图应通过 `BitmapFactory.Options.inSampleSize` 降采样

---

## 7. 布局管理器

### 7.1 FrameLayout.java

**文件：** `frameworks/base/core/java/android/widget/FrameLayout.java`（500 行）

#### 类用途
最简单的布局容器。设计用于持有单个子视图，但多个子视图会以 Z 排序堆叠（最近添加的在最上面）。大小由最大的子视图决定。

**关键 API：**
- `setMeasureAllChildren(boolean)` / `setConsiderGoneChildrenWhenMeasuring(boolean)` —— GONE 子视图是否影响大小
- `setForegroundGravity(int)` —— 定位前景 Drawable

**测量策略：** 当存在 `match_parent` 子视图时进行两次遍历。第一次正常测量所有子视图。第二次使用找到的最大宽度/高度重新测量 `match_parent` 子视图。

**布局行为：** 子视图根据其 `layout_gravity` 属性定位（默认：`Gravity.TOP | Gravity.START`，第 60 行）。

---

### 7.2 LinearLayout.java

**文件：** `frameworks/base/core/java/android/widget/LinearLayout.java`（2,099 行）

#### 类用途
将子视图排列在单个水平行或垂直列中。支持基于权重的剩余空间分配。

**关键 API：**
- `setOrientation(int)` —— `HORIZONTAL`（0）或 `VERTICAL`（1）（第 96-97 行）
- `setGravity(int)` —— 所有子视图的对齐方式
- `setWeightSum(float)` —— 总权重除数
- `setDividerDrawable(Drawable)` —— 子视图之间的视觉分隔符
- `setShowDividers(int)` —— `SHOW_DIVIDER_NONE`、`SHOW_DIVIDER_BEGINNING`、`SHOW_DIVIDER_MIDDLE`、`SHOW_DIVIDER_END`
- `setBaselineAligned(boolean)` —— 对齐子视图基线（文本对齐）

**布局权重系统：** `layout_weight > 0` 的子视图按比例接收剩余空间的份额。当设置了 `weightSum` 时，权重相对于该总和而非所有子视图权重的总和。

**测量最多进行两次遍历：**
1. 第一次遍历：正常测量所有权重为 `0` 的子视图
2. 如果有子视图有权重：按比例重新分配剩余空间并重新测量

**性能说明：** 嵌套的带权重 LinearLayout（权重中嵌套权重）会导致指数级测量 —— 每层使测量次数翻倍。考虑使用 `ConstraintLayout`（外部库）或 `RelativeLayout` 处理复杂布局。

---

### 7.3 RelativeLayout.java

**文件：** `frameworks/base/core/java/android/widget/RelativeLayout.java`（2,081 行）

#### 类用途
相对于彼此或父容器边缘定位子视图。使用布局规则的有向无环图（DAG）。

**关键规则常量（第 91 行以后）：**
| 常量 | 描述 |
|---|---|
| `LEFT_OF` / `RIGHT_OF` | 相对于兄弟视图定位 |
| `ABOVE` / `BELOW` | 相对于兄弟视图定位 |
| `ALIGN_LEFT` / `ALIGN_RIGHT` / `ALIGN_TOP` / `ALIGN_BOTTOM` | 与兄弟视图边缘对齐 |
| `ALIGN_PARENT_LEFT` / `ALIGN_PARENT_RIGHT` / `ALIGN_PARENT_TOP` / `ALIGN_PARENT_BOTTOM` | 与父容器边缘对齐 |
| `CENTER_IN_PARENT` / `CENTER_HORIZONTAL` / `CENTER_VERTICAL` | 居中规则 |
| `START_OF` / `END_OF` / `ALIGN_START` / `ALIGN_END` | RTL 感知变体（API 17+） |

**测量策略：**
1. 使用拓扑排序按依赖顺序排序子视图
2. 分两次遍历测量子视图：水平规则，然后垂直规则
3. 根据其依赖项的已解析位置测量子视图

**已知缺陷说明（第 63-76 行）：** 在 API 17 及以下版本中，RelativeLayout 存在一个测量缺陷，滚动容器中的子视图会收到 `AT_MOST` 规格而非 `UNSPECIFIED`。这为了与 `targetSdkVersion <= 17` 的向后兼容性而保留。

**设计模式：** DAG 依赖解析 —— 内部使用带拓扑排序的 `DependencyGraph` 确定布局顺序。

---

### 7.4 ConstraintLayout

**不在 AOSP 框架中。** ConstraintLayout 作为 Jetpack（AndroidX）库分发，不是核心框架的一部分。应用通过 `implementation 'androidx.constraintlayout:constraintlayout:x.y.z'` 引入。

---

## 8. 动画框架

### 8.1 Animator.java

**文件：** `frameworks/base/core/java/android/animation/Animator.java`（约 400 行）

#### 类用途
所有基于属性的动画器的抽象基类。定义了核心动画生命周期和监听器接口。

**关键 API：**
- `start()`、`cancel()`、`end()` —— 生命周期控制
- `pause()` / `resume()`（第 113 行以后）—— 暂停/恢复而不重置
- `isStarted()`、`isRunning()`、`isPaused()`
- `setDuration(long)`、`getDuration()`
- `setStartDelay(long)`
- `setInterpolator(TimeInterpolator)`
- `addListener(AnimatorListener)` —— 生命周期回调
- `addPauseListener(AnimatorPauseListener)` —— 暂停/恢复回调
- `clone()` —— 深拷贝支持

**AnimatorListener 接口：**
- `onAnimationStart(Animator)`
- `onAnimationEnd(Animator)`
- `onAnimationCancel(Animator)`
- `onAnimationRepeat(Animator)`

**AnimatorListenerAdapter**（单独的文件）提供空的默认实现。

**线程规则：** 动画必须在有 `Looper` 的线程上启动。修改视图的属性动画必须在 UI 线程上运行。

---

### 8.2 ValueAnimator.java

**文件：** `frameworks/base/core/java/android/animation/ValueAnimator.java`（约 1,600 行）

#### 类用途
提供一个计时引擎，随时间计算动画值。不直接修改任何对象 —— 调用者注册 `UpdateListener` 并手动应用值。

```java
public class ValueAnimator extends Animator
        implements AnimationHandler.AnimationFrameCallback
```

#### 关键工厂方法（第 356-433 行）
- `ofInt(int... values)` —— 整数插值
- `ofFloat(float... values)` —— 浮点数插值
- `ofArgb(int... values)` —— ARGB 颜色插值
- `ofObject(TypeEvaluator evaluator, Object... values)` —— 自定义类型插值
- `ofPropertyValuesHolder(PropertyValuesHolder... values)` —— 多属性动画

#### 关键配置 API
- `setDuration(long)`（第 596 行）—— 动画持续时间，毫秒
- `setRepeatCount(int)`（第 899 行）—— `INFINITE` 或特定次数
- `setRepeatMode(int)`（第 919 行）—— `RESTART` 或 `REVERSE`
- `setInterpolator(TimeInterpolator)`（第 985 行）—— 默认：`AccelerateDecelerateInterpolator`
- `setStartDelay(long)`（第 802 行）
- `addUpdateListener(AnimatorUpdateListener)`（第 940 行）—— 每帧调用

#### 观察动画值
- `getAnimatedValue()`（第 863 行）—— 当前插值
- `getAnimatedFraction()`（第 1538 行）—— 当前进度 0.0 到 1.0

#### 系统级持续时间缩放
```java
private static float sDurationScale = 1.0f;  // 第 88 行
```
这个隐藏字段控制全局动画速度。当设置为 0（例如在开发者选项"动画程序时长缩放"中）时，所有动画立即完成。`areAnimatorsEnabled()` 检查 `sDurationScale != 0`。

#### 性能
- 由 `AnimationHandler` 驱动，使用 `Choreographer.postFrameCallback()` 实现 VSYNC 对齐更新
- 内部使用 `PropertyValuesHolder` 进行高效值计算
- 专门的 `FloatKeyframeSet` 和 `IntKeyframeSet` 避免装箱开销

---

### 8.3 ObjectAnimator.java

**文件：** `frameworks/base/core/java/android/animation/ObjectAnimator.java`（约 800 行）

#### 类用途
继承 `ValueAnimator`，通过反射或 `Property` 对象自动在目标对象的属性上设置动画值。

```java
public final class ObjectAnimator extends ValueAnimator
```

#### 关键工厂方法
- `ofFloat(Object target, String propertyName, float... values)`
- `ofInt(Object target, String propertyName, int... values)`
- `ofArgb(Object target, String propertyName, int... values)`
- `ofObject(Object target, String propertyName, TypeEvaluator evaluator, Object... values)`
- `ofFloat(Object target, Property<?, Float> property, float... values)` —— 类型安全变体
- `ofMultiFloat(Object target, String propertyName, Path path)` —— 沿路径动画

#### 属性解析
给定属性名 `"foo"`，ObjectAnimator：
1. 查找 `setFoo(float)` 或 `setFoo(int)` 作为 setter
2. 查找 `getFoo()` 作为 getter（当需要起始值时）
3. 如果可用，使用 `Property<T, V>` 对象进行直接访问（更好的性能）

**性能说明（第 96-101 行）：** "为获得最佳性能……使用 `float` 或 `int` 类型的值，并使 setter 函数具有 `void` 返回值。" 这启用了优化的 JNI 路径。

**目标引用：** 对目标使用 `WeakReference<Object>`（第 80 行），防止内存泄漏。如果目标被 GC 回收，动画自动取消。

**自动取消（第 86 行）：** `setAutoCancel(true)` 导致动画器在启动时取消同一目标+属性组合上正在运行的任何动画。

---

### 8.4 AnimatorSet.java

**文件：** `frameworks/base/core/java/android/animation/AnimatorSet.java`（约 1,200 行）

#### 类用途
按指定顺序播放一组 `Animator` 对象：同时、依次或具有自定义依赖关系。

```java
public final class AnimatorSet extends Animator
        implements AnimationHandler.AnimationFrameCallback
```

#### 关键 API
- `playTogether(Animator... items)` —— 同时播放所有动画
- `playSequentially(Animator... items)` —— 依次播放动画
- `play(Animator)` —— 返回 `Builder` 用于链式依赖

#### Builder 模式（流式 API）
```java
AnimatorSet set = new AnimatorSet();
set.play(anim1).with(anim2).before(anim3).after(anim4);
```
- `with(Animator)` —— 同时播放
- `before(Animator)` —— 此动画在参数之前播放
- `after(Animator)` —— 此动画在参数之后播放
- `after(long delay)` —— 插入延迟

#### 内部架构
使用基于 `Node` 对象（第 94 行）和 `AnimationEvent` 排序时间线（第 87 行）的依赖图。`mNodeMap`（`ArrayMap<Animator, Node>`，第 82 行）将每个子动画器映射到其依赖节点。

循环依赖检测在文档中提到（第 47-51 行），但会导致"未定义"行为而非抛出异常。

---

### 8.5 ViewPropertyAnimator

**文件：** `frameworks/base/core/java/android/view/ViewPropertyAnimator.java`（约 1,000 行）

#### 类用途
为动画 View 属性提供优化的流式 API。通过 `view.animate()` 获取。

#### 流式 API 示例
```java
view.animate()
    .alpha(0f)
    .translationX(100f)
    .scaleX(0.5f)
    .setDuration(300)
    .setInterpolator(new AccelerateInterpolator())
    .withEndAction(() -> view.setVisibility(View.GONE))
    .start();
```

#### 可动画属性
- `alpha(float)`、`alphaBy(float)`
- `translationX/Y/Z(float)`、`translationXBy/YBy/ZBy(float)`
- `rotation(float)`、`rotationX/Y(float)`
- `scaleX/Y(float)`、`scaleXBy/YBy(float)`
- `x(float)`、`y(float)`、`z(float)` —— 绝对位置

#### 性能优势
"此类可能为多个同时动画提供更好的性能，因为它会优化 invalidate 调用，使其对多个属性只执行一次，而不是每个动画属性独立地导致自己的失效"（第 37-40 行）。

内部批处理属性更改，并使用 `RenderNode` 属性进行硬件加速更新，完全绕过主 `draw()` 路径。

---

## 9. 图形子系统

### 9.1 Canvas.java

**文件：** `frameworks/base/graphics/java/android/graphics/Canvas.java`（2,327 行）

#### 类用途
承载 2D 渲染的"绘制"调用。需要目标 `Bitmap`（软件）或由 `RenderNode`（硬件加速）支持。实际绘制方法继承自 `BaseCanvas`。

#### 关键绘制方法（来自 BaseCanvas）
- `drawColor(int color)`、`drawColor(int color, BlendMode mode)` —— 填充整个画布
- `drawRect(float l, float t, float r, float b, Paint)` —— 矩形
- `drawRoundRect(float l, float t, float r, float b, float rx, float ry, Paint)` —— 圆角矩形
- `drawCircle(float cx, float cy, float radius, Paint)` —— 圆
- `drawOval(float l, float t, float r, float b, Paint)` —— 椭圆
- `drawArc(...)` —— 弧段
- `drawLine(float startX, float startY, float stopX, float stopY, Paint)` —— 线
- `drawPath(Path, Paint)` —— 任意路径
- `drawText(String, float x, float y, Paint)` —— 文本渲染
- `drawBitmap(Bitmap, float left, float top, Paint)` —— 位图渲染
- `drawBitmap(Bitmap, Rect src, Rect dst, Paint)` —— 带源/目标矩形的位图
- `drawBitmapMesh(...)` —— 通过网格变形的位图

#### Canvas 状态管理
- `save()` / `restore()` —— 保存/恢复变换和裁剪状态
- `saveLayer(...)` —— 保存到离屏缓冲区，用于混合模式效果
- `translate(float dx, float dy)` —— 平移原点
- `rotate(float degrees)` —— 旋转
- `scale(float sx, float sy)` —— 缩放
- `skew(float sx, float sy)` —— 倾斜
- `concat(Matrix)` —— 应用任意矩阵
- `clipRect(...)`、`clipPath(...)` —— 裁剪

#### 最大位图大小
```java
private static final int MAXMIMUM_BITMAP_SIZE = 32766;  // 第 77 行
```
注意：常量名有拼写错误（"MAXMIMUM" 应为 "MAXIMUM"）。

#### 硬件加速
`isHardwareAccelerated()` 区分软件 Canvas（由 Bitmap 支持）和硬件 Canvas（由 RenderNode/GPU 支持）。某些操作在硬件模式下行为不同或不受支持。

---

### 9.2 Paint.java

**文件：** `frameworks/base/graphics/java/android/graphics/Paint.java`（3,218 行）

#### 类用途
持有绘制操作的样式、颜色和效果信息。

#### 关键属性
- **颜色：** `setColor(int)`、`setARGB(int a, int r, int g, int b)`
- **样式：** `setStyle(Style)` —— `FILL`、`STROKE`、`FILL_AND_STROKE`
- **描边：** `setStrokeWidth(float)`、`setStrokeCap(Cap)`、`setStrokeJoin(Join)`
- **抗锯齿：** `setAntiAlias(boolean)` —— 平滑边缘
- **文本：** `setTextSize(float)`、`setTypeface(Typeface)`、`setTextAlign(Align)`
- **效果：** `setShader(Shader)`、`setColorFilter(ColorFilter)`、`setPathEffect(PathEffect)`、`setMaskFilter(MaskFilter)`
- **混合：** `setXfermode(Xfermode)`、`setBlendMode(BlendMode)` —— 合成模式
- **阴影：** `setShadowLayer(float radius, float dx, float dy, int color)`
- **亚像素：** `setSubpixelText(boolean)` —— 亚像素文本渲染

#### 文本测量
- `measureText(String)` —— 返回文本宽度（像素）
- `getTextBounds(String, int start, int end, Rect bounds)` —— 紧密边界框
- `getFontMetrics()` / `getFontMetricsInt()` —— 上升、下降、行间距
- `breakText(String, boolean, float maxWidth, float[] measuredWidth)` —— 断行辅助

#### 性能
- 通过带 `@CriticalNative` 注解的 JNI 原生实现频繁操作
- `NativeAllocationRegistry` 用于确定性的原生内存管理
- 通过 `sMinikinLocaleListIdCache`（第 98 行）进行区域设置缓存，避免重复的字符串到 ID 转换

---

### 9.3 Bitmap.java

**文件：** `frameworks/base/graphics/java/android/graphics/Bitmap.java`（2,335 行）

#### 类用途
表示 2D 像素数组。典型 Android 应用中最占用内存的图形对象。

#### 关键工厂方法
- `createBitmap(int width, int height, Config config)` —— 空位图
- `createBitmap(Bitmap src)` —— 复制
- `createBitmap(Bitmap src, int x, int y, int width, int height)` —— 子区域
- `createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)` —— 缩放
- `BitmapFactory.decodeResource(...)`、`BitmapFactory.decodeFile(...)` —— 从文件/资源

#### Config 枚举
| Config | 位/像素 | 描述 |
|---|---|---|
| `ALPHA_8` | 8 | 仅 Alpha 通道 |
| `RGB_565` | 16 | 无 Alpha，降低的色彩深度 |
| `ARGB_4444` | 16 | 已废弃，质量差 |
| `ARGB_8888` | 32 | 全色彩 + Alpha（默认） |
| `RGBA_F16` | 64 | 广色域，HDR |
| `HARDWARE` | 可变 | 仅 GPU 位图，无像素访问 |

#### 关键 API
- `getWidth()`、`getHeight()`、`getConfig()`
- `getPixel(int x, int y)`、`setPixel(int x, int y, int color)` —— 逐像素访问
- `getPixels(int[] pixels, ...)`、`setPixels(int[] pixels, ...)` —— 批量像素访问
- `compress(CompressFormat, int quality, OutputStream)` —— 编码为 PNG/JPEG/WEBP
- `recycle()` —— 释放原生内存（对内存管理很重要）
- `isRecycled()` —— 检查是否已回收
- `copy(Config, boolean mutable)` —— 以不同配置复制
- `isMutable()` —— 像素是否可修改
- `setDensity(int)` —— 设置用于缩放的显示密度

#### 内存管理
- `getAllocationByteCount()` —— 实际原生内存大小
- `getByteCount()` —— 逻辑大小（对于重用的位图可能不同）
- 硬件位图（`Config.HARDWARE`）位于 GPU 内存中，无法读写像素
- `prepareToDraw()` —— 提示预上传纹理到 GPU

#### 性能注意事项
- **完成后始终 recycle** 位图，或使用 `BitmapFactory.Options.inBitmap` 重用分配
- **降采样**大图：设置 `BitmapFactory.Options.inSampleSize` 以减少内存
- **使用 HARDWARE 配置**用于仅显示的位图以避免 CPU 端内存
- **密度感知**：位图密度（`mDensity`）影响在不同密度显示器上绘制时的缩放方式

---

### 9.4 Drawable.java

**文件：** `frameworks/base/graphics/java/android/graphics/drawable/Drawable.java`（1,737 行）

#### 类用途
"可以被绘制的东西"的抽象基类。与 View 不同，Drawable 没有事件处理能力。用于背景、图标、状态相关的图形等。

#### 关键抽象方法
- `draw(Canvas canvas)` —— 渲染 Drawable
- `setAlpha(@IntRange(from=0, to=255) int alpha)` —— 透明度
- `setColorFilter(@Nullable ColorFilter colorFilter)` —— 颜色修改
- `getOpacity()` —— `PixelFormat.OPAQUE`、`TRANSLUCENT`、`TRANSPARENT` 或 `UNKNOWN`

#### 重要的具体方法
- `setBounds(int left, int top, int right, int bottom)` —— 绘制前必须调用
- `getBounds()` —— 返回绘制边界
- `setState(int[] stateSet)` —— 按下、聚焦、选中等
- `setLevel(int level)` —— 连续值（0-10000），用于进度等
- `setTint(int)`、`setTintList(ColorStateList)`、`setTintMode(BlendMode)` —— 颜色着色
- `getIntrinsicWidth()`、`getIntrinsicHeight()` —— 首选大小（如果没有则为 -1）
- `getPadding(Rect)` —— 内容内边距
- `setCallback(Callback)` —— 用于失效通知（View 实现了 `Drawable.Callback`）
- `mutate()` —— 创建可变副本（共享的常量状态变为独立的）

#### 关键子类（在 android.graphics.drawable 中）
- `BitmapDrawable` —— 包装 Bitmap
- `ColorDrawable` —— 纯色填充
- `GradientDrawable` —— 带渐变、描边、圆角的形状
- `ShapeDrawable` —— 可编程形状
- `LayerDrawable` —— 堆叠的 Drawable
- `StateListDrawable` —— 状态相关的 Drawable 选择
- `AnimatedVectorDrawable` —— 带动画的矢量图形
- `VectorDrawable` —— 分辨率无关的矢量图形
- `NinePatchDrawable` —— 可拉伸的 9-patch 图片
- `RippleDrawable` —— Material Design 涟漪效果
- `TransitionDrawable` —— 两个 Drawable 之间的交叉淡入淡出
- `InsetDrawable`、`ScaleDrawable`、`ClipDrawable`、`RotateDrawable` —— 变换包装器

#### 状态管理（观察者模式）
```
View implements Drawable.Callback {
    invalidateDrawable(Drawable who) --> invalidate()
    scheduleDrawable(Drawable who, Runnable what, long when) --> postDelayed()
    unscheduleDrawable(Drawable who, Runnable what) --> removeCallbacks()
}
```
当 Drawable 的状态改变时（例如动画帧推进），它调用 `invalidateSelf()`，通知其回调（View），触发重绘。

#### ConstantState 模式
Drawable 使用 `ConstantState` 内部类在从相同资源加载的实例之间共享不可变数据。这在多个视图使用相同 Drawable 资源时节省内存。当需要单独修改时，`mutate()` 打破这种共享。

---

## 10. Surface 渲染

### 10.1 SurfaceView.java

**文件：** `frameworks/base/core/java/android/view/SurfaceView.java`（1,888 行）

#### 类用途
提供嵌入在视图层级中的专用绘图表面。该表面存在于主窗口后面（或前面）的单独窗口层中。SurfaceView 在父窗口中"打一个洞"以使其表面可见。

**关键特性：**
- 表面独立于视图层级渲染
- 可以从任何线程绘制（不限于 UI 线程）
- 默认 Z 排序在窗口后面（可放在前面）
- 不支持 View 动画、变换或与兄弟视图的 Alpha 混合（改用 TextureView）

#### 关键 API
- `getHolder()` —— 返回 `SurfaceHolder` 用于表面访问
- `SurfaceHolder.addCallback(SurfaceHolder.Callback)` —— 生命周期事件：
  - `surfaceCreated(SurfaceHolder)` —— 表面准备好绘制
  - `surfaceChanged(SurfaceHolder, int format, int width, int height)` —— 大小/格式改变
  - `surfaceDestroyed(SurfaceHolder)` —— 表面正在释放
- `SurfaceHolder.lockCanvas()` / `unlockCanvasAndPost(Canvas)` —— 软件渲染
- `SurfaceHolder.getSurface()` —— 获取 `Surface` 用于 OpenGL/MediaCodec
- `setZOrderOnTop(boolean)` —— 将表面放在窗口上方
- `setZOrderMediaOverlay(boolean)` —— 放在媒体上方但在窗口下方
- `setSecure(boolean)` —— 防止截屏

#### 使用场景
- 相机预览
- 视频播放
- 游戏渲染（OpenGL ES）
- 媒体编解码器输出

---

### 10.2 TextureView.java

**文件：** `frameworks/base/core/java/android/view/TextureView.java`（862 行）

#### 类用途
将内容流（视频、OpenGL 场景、相机）作为普通 View 显示在层级结构中。与 SurfaceView 不同，TextureView 参与正常的 View 渲染，支持变换、Alpha 和动画。

**关键要求：** TextureView 只能在硬件加速窗口中使用。在软件渲染中，它什么都不绘制（第 39-40 行）。

#### 关键 API
- `setSurfaceTextureListener(SurfaceTextureListener)` —— 生命周期回调：
  - `onSurfaceTextureAvailable(SurfaceTexture, int width, int height)`
  - `onSurfaceTextureSizeChanged(SurfaceTexture, int width, int height)`
  - `onSurfaceTextureDestroyed(SurfaceTexture)` —— 返回 `true` 以释放
  - `onSurfaceTextureUpdated(SurfaceTexture)` —— 新帧可用
- `getSurfaceTexture()` —— 访问底层 `SurfaceTexture`
- `getBitmap()` —— 将当前内容捕获为 Bitmap
- `lockCanvas()` / `unlockCanvasAndPost(Canvas)` —— 软件渲染
- `setTransform(Matrix)` —— 自定义变换矩阵
- `setOpaque(boolean)` —— 内容是否不透明

#### SurfaceView 与 TextureView 对比

| 特性 | SurfaceView | TextureView |
|---|---|---|
| View 动画 | 否 | 是 |
| Alpha/变换 | 否 | 是 |
| 线程安全 | 任何线程 | lockCanvas 需 UI 线程 |
| 合成 | 单独图层 | 与视图同一图层 |
| 性能 | 更好（专用表面） | 稍差（GPU 合成） |
| 需要硬件加速 | 否 | 是 |
| DRM 内容 | 是 | 困难 |
| 可用版本 | API 1 | API 14 |

---

### 10.3 Choreographer.java

**文件：** `frameworks/base/core/java/android/view/Choreographer.java`（1,046 行）

#### 类用途
协调动画、输入和绘制与显示 VSYNC 的时序。每个 `Looper` 线程有自己的 Choreographer 实例。

```java
public final class Choreographer
```

#### 关键 API
- `getInstance()` —— 获取当前线程的 Choreographer
- `postFrameCallback(FrameCallback callback)` —— 为下一帧安排工作
- `postFrameCallbackDelayed(FrameCallback callback, long delayMillis)` —— 延迟回调
- `removeFrameCallback(FrameCallback callback)` —— 取消待处理回调

#### FrameCallback 接口
```java
public interface FrameCallback {
    void doFrame(long frameTimeNanos);
}
```

#### 内部帧处理顺序
Choreographer 每帧按四个阶段处理回调：
1. **输入**（`CALLBACK_INPUT`）—— 输入事件
2. **动画**（`CALLBACK_ANIMATION`）—— 动画帧更新
3. **Insets 动画**（`CALLBACK_INSETS_ANIMATION`）—— 窗口 Insets 动画
4. **遍历**（`CALLBACK_TRAVERSAL`）—— measure/layout/draw

#### VSYNC 集成
- 从 `DisplayEventReceiver` 接收 VSYNC 信号
- `DEFAULT_FRAME_DELAY = 10ms`（第 99 行）—— VSYNC 不可用时的后备
- 启用 `DEBUG_JANK` 时检测并记录卡顿（跳帧）（第 86 行）

#### 性能
- 使用 `FrameInfo` 追踪帧时序
- 与 `Trace` 集成用于 systrace 可视化
- 线程本地单例模式避免同步开销

---

## 11. RecyclerView（内部）

**文件：** `frameworks/base/core/java/com/android/internal/widget/RecyclerView.java`（12,264 行）

#### 重要说明
这是位于 `com.android.internal.widget.RecyclerView` 的**内部副本**，不是公共 AndroidX `RecyclerView`。公共版本在 AndroidX 库（`androidx.recyclerview:recyclerview`）中。

内部副本由系统 UI（设置、通知面板等）使用，不是公共 SDK 的一部分。应用开发者应始终使用 AndroidX 版本。

内部版本遵循相同的架构：
- `LayoutManager` 用于定位项目
- `Adapter` 用于将数据绑定到视图
- `ViewHolder` 模式用于视图回收
- `ItemDecoration` 用于视觉添加
- `ItemAnimator` 用于添加/移除/更改动画

---

## 12. 设计模式总结

| 模式 | 应用位置 | 描述 |
|---|---|---|
| **组合模式** | View/ViewGroup | ViewGroup 包含 View 的树结构 |
| **责任链** | 触摸分发 | 事件通过 `dispatchTouchEvent()` 链流动 |
| **模板方法** | `onMeasure()`、`onLayout()`、`onDraw()` | 框架调用这些方法；子类重写 |
| **观察者** | 监听器接口、`Drawable.Callback` | 通过回调进行事件通知 |
| **工厂方法** | `LayoutInflater.Factory/Factory2` | 加载过程中自定义 View 创建 |
| **策略** | `LayoutManager`（RecyclerView）、`ScaleType`（ImageView） | 可互换的算法 |
| **享元** | `LayoutInflater.sConstructorMap`、`MotionEvent.obtain()` | 对象共享/池化 |
| **建造者** | `AnimatorSet.Builder`、`ViewPropertyAnimator` | 流式构造 API |
| **装饰器** | `InsetDrawable`、`ScaleDrawable`、包装 Drawable | 动态添加行为到对象 |
| **状态** | `StateListDrawable`、`StateListAnimator` | 行为随状态变化 |
| **桥接** | `Canvas`（Java）<-> 原生 Skia | 抽象/实现分离 |
| **代理** | `Bitmap.HARDWARE` 配置 | GPU 驻留位图限制操作 |
| **MVC** | 列表视图中的 View/Adapter 分离 | 模型-视图分离 |
| **弱引用** | `ObjectAnimator.mTarget` | 防止动画内存泄漏 |

---

## 13. 性能注意事项

### 测量和布局

1. **避免深层视图层级** —— 每层增加测量工作。扁平层级（ConstraintLayout）更优。
2. **避免带权重的嵌套 LinearLayout** —— 导致指数级测量遍历。
3. **`requestLayout()` 传播到根节点** —— 每次调用都从顶部触发完整的 measure/layout 遍历。在单帧内批量处理布局更改。
4. **MeasureSpec 缓存**（View 第 25440 行）有助于避免冗余测量，但仅在规格未改变时有效。
5. **`GONE` 视图**在某些布局中仍然被测量（例如启用了 `measureAllChildren` 的 FrameLayout）。

### 绘制

1. **`invalidate()` vs `postInvalidateOnAnimation()`** —— 优先使用 `postInvalidateOnAnimation()` 以合并到下一个 VSYNC 帧。
2. **避免在 `onDraw()` 中分配对象** —— Paint、Rect、Path 对象应预分配为实例字段。
3. **硬件加速** —— 默认启用（API 14+）。某些 Canvas 操作不受支持（例如带颜色的 `drawBitmapMesh`、某些 `clipPath` 情况）。
4. **`setWillNotDraw(true)`** —— 跳过不自行绘制的 ViewGroup 的绘制遍历。
5. **`setLayerType(LAYER_TYPE_HARDWARE, paint)`** —— 将复杂视图缓存为 GPU 纹理用于动画。完成后必须调用 `setLayerType(LAYER_TYPE_NONE, null)` 以释放 GPU 内存。

### 触摸事件

1. **MotionEvent 批处理** —— 始终处理 `getHistorySize()` 个历史采样以实现平滑的绘图/滚动。
2. **`onInterceptTouchEvent()` 性能** —— 手势中的每个事件都会调用。保持轻量。
3. **避免分配 MotionEvent** —— 创建合成事件时使用 `obtain()` / `recycle()`。

### 动画

1. **ViewPropertyAnimator** 比多个属性的单独 ObjectAnimator 更快 —— 它批量处理失效。
2. **检查 `areAnimatorsEnabled()`** —— 动画可能在开发者选项中被禁用。动画逻辑应处理即时完成的情况。
3. **避免在动画期间使用 `setLayerType(HARDWARE)`** —— `ViewPropertyAnimator` 和 RenderNode 属性（alpha、translation、rotation、scale）上的 `ObjectAnimator` 已经硬件加速，无需显式的 layer type。
4. **在 `onDetachedFromWindow()` 或 `onPause()` 中取消动画** 以防止泄漏和浪费 CPU。

### 位图内存

1. **使用 `inSampleSize`** 以降低分辨率解码大图。
2. **使用 `Config.HARDWARE`** 用于仅显示的位图（API 26+）。
3. **使用 `inBitmap`** 进行位图重用以避免分配抖动。
4. **完成后调用 `recycle()`**，特别是在 Android 8.0 之前（位图像素数据位于 Java 堆中）。
5. **优先使用 `ImageDecoder`**（API 28+）而非 `BitmapFactory`，以获得更好的错误处理和头部优先解码。

---

## 14. 隐藏和系统 API 总结

View 框架包含大量被系统 UI 和特权应用使用的隐藏 API。关键类别：

### View.java 隐藏 API
- **私有标志**：`mPrivateFlags`、`mPrivateFlags2`、`mPrivateFlags3` —— 控制布局、绘制和焦点状态的内部状态位掩码。超过 50 个 `PFLAG_*` 常量。
- **系统可见性**：`setSystemUiVisibility(int)` 标志 —— `SYSTEM_UI_FLAG_FULLSCREEN`、`SYSTEM_UI_FLAG_HIDE_NAVIGATION`、`SYSTEM_UI_FLAG_IMMERSIVE_STICKY`
- **`@UnsupportedAppUsage` 字段**：`mAttachInfo`、`mParent`、`mLeft/mTop/mRight/mBottom`、`mScrollX/mScrollY` —— 尽管非公开，但应用通过反射频繁访问。
- **自动填充集成**：`onProvideAutofillStructure()`、`autofill(AutofillValue)`、`getAutofillId()`
- **内容捕获**：`notifyAppearedOrDisappearedForContentCaptureIfNeeded()`
- **滚动捕获**（Android 11）：`ScrollCaptureInternal` 集成用于长截图

### WindowManager.java 隐藏 API
- **窗口过渡类型**：`TRANSIT_ACTIVITY_OPEN`、`TRANSIT_TASK_CLOSE` 等 —— 20 多个用于系统管理窗口过渡的隐藏常量
- **输入消费者**：`INPUT_CONSUMER_PIP`、`INPUT_CONSUMER_NAVIGATION` —— 用于系统手势处理的命名输入消费者
- **停靠常量**：`DOCKED_LEFT`、`DOCKED_TOP` 等 —— 分屏定位
- **LayoutParams 私有标志**：`PRIVATE_FLAG_*` 常量用于系统窗口行为

### InputEvent.java 隐藏 API
- `setSource(int)`、`setDisplayId(int)` —— 事件路由修改
- `copy()`、`recycle()`、`prepareForReuse()` —— 对象池管理
- `isTainted()` / `setTainted(boolean)` —— 事件一致性追踪
- `getEventTimeNano()` —— 纳秒精度时间戳
- `cancel()` —— 将事件标记为已取消

### MotionEvent.java 隐藏 API
- 用于坐标访问的 `@CriticalNative` 和 `@FastNative` JNI 方法
- `setTargetAccessibilityFocus(boolean)` —— 无障碍焦点路由
- `isTargetAccessibilityFocus()` —— 检查无障碍定位
- `FLAG_WINDOW_IS_OBSCURED` —— 点击劫持检测标志

### 动画隐藏 API
- `ValueAnimator.sDurationScale` —— 系统级动画速度乘数（`@UnsupportedAppUsage`）
- `AnimationHandler` —— 未暴露给应用的内部帧调度
- `RevealAnimator` —— 圆形揭示动画实现细节

### Bitmap 隐藏 API
- `mNativePtr` —— 直接原生指针（`@UnsupportedAppUsage`）
- `mNinePatchChunk`、`mNinePatchInsets` —— 9-patch 元数据
- `createGraphicBufferHandle()` —— GPU 缓冲区互操作
- `BLASTBufferQueue` —— Android 11 中新的缓冲区队列实现

---

## 文件参考索引

| 文件 | 路径 | 行数 |
|---|---|---|
| View.java | `frameworks/base/core/java/android/view/View.java` | 30,408 |
| ViewGroup.java | `frameworks/base/core/java/android/view/ViewGroup.java` | 9,277 |
| Window.java | `frameworks/base/core/java/android/view/Window.java` | 2,656 |
| WindowManager.java | `frameworks/base/core/java/android/view/WindowManager.java` | 3,870 |
| LayoutInflater.java | `frameworks/base/core/java/android/view/LayoutInflater.java` | 1,360 |
| InputEvent.java | `frameworks/base/core/java/android/view/InputEvent.java` | 272 |
| MotionEvent.java | `frameworks/base/core/java/android/view/MotionEvent.java` | 4,179 |
| KeyEvent.java | `frameworks/base/core/java/android/view/KeyEvent.java` | 3,166 |
| GestureDetector.java | `frameworks/base/core/java/android/view/GestureDetector.java` | 920 |
| Choreographer.java | `frameworks/base/core/java/android/view/Choreographer.java` | 1,046 |
| ViewPropertyAnimator.java | `frameworks/base/core/java/android/view/ViewPropertyAnimator.java` | ~1,000 |
| SurfaceView.java | `frameworks/base/core/java/android/view/SurfaceView.java` | 1,888 |
| TextureView.java | `frameworks/base/core/java/android/view/TextureView.java` | 862 |
| TextView.java | `frameworks/base/core/java/android/widget/TextView.java` | 13,705 |
| EditText.java | `frameworks/base/core/java/android/widget/EditText.java` | ~160 |
| Button.java | `frameworks/base/core/java/android/widget/Button.java` | ~155 |
| ImageView.java | `frameworks/base/core/java/android/widget/ImageView.java` | 1,730 |
| FrameLayout.java | `frameworks/base/core/java/android/widget/FrameLayout.java` | 500 |
| LinearLayout.java | `frameworks/base/core/java/android/widget/LinearLayout.java` | 2,099 |
| RelativeLayout.java | `frameworks/base/core/java/android/widget/RelativeLayout.java` | 2,081 |
| Animator.java | `frameworks/base/core/java/android/animation/Animator.java` | ~400 |
| ValueAnimator.java | `frameworks/base/core/java/android/animation/ValueAnimator.java` | ~1,600 |
| ObjectAnimator.java | `frameworks/base/core/java/android/animation/ObjectAnimator.java` | ~800 |
| AnimatorSet.java | `frameworks/base/core/java/android/animation/AnimatorSet.java` | ~1,200 |
| Canvas.java | `frameworks/base/graphics/java/android/graphics/Canvas.java` | 2,327 |
| Paint.java | `frameworks/base/graphics/java/android/graphics/Paint.java` | 3,218 |
| Bitmap.java | `frameworks/base/graphics/java/android/graphics/Bitmap.java` | 2,335 |
| Drawable.java | `frameworks/base/graphics/java/android/graphics/drawable/Drawable.java` | 1,737 |
| RecyclerView.java（内部） | `frameworks/base/core/java/com/android/internal/widget/RecyclerView.java` | 12,264 |
