# Westlake 引擎 — OpenHarmony 上的 Android 应用兼容层

**[English](README.md)** | **[中文](README_CN.md)**

[![测试](https://img.shields.io/badge/测试-2%2C476_通过-brightgreen)]()
[![Shim 类数](https://img.shields.io/badge/shim_类-2%2C056-blue)]()
[![AOSP 代码量](https://img.shields.io/badge/AOSP_框架-193K_行_(166_文件)-orange)]()
[![架构](https://img.shields.io/badge/方案-引擎_%7E15_桥接点-purple)]()
[![许可证](https://img.shields.io/badge/许可证-Apache_2.0-blue)]()

Westlake 通过提供一个将 Android API 调用转换为 OHOS 等效调用的 shim 层，在 **OpenHarmony 上运行未修改的 Android APK**。引擎不是逐个映射 57,000 个 Android API，而是将 Android 框架作为客户运行时嵌入 — 就像 OpenHarmony 托管 Flutter 一样 — 仅在约 15 个 HAL 级别边界与 OHOS 桥接。

*Westlake*（西湖）这个名字代表了 Android 和 OpenHarmony 两个世界之间的桥梁 — 正如杭州西湖连接传统与现代。

## 当前状态

**MockDonalds 演示应用已在真机上使用原生 Android View 运行。**

- 真实 Android APK 通过手机原生 ART 运行时加载和渲染
- 5 个页面：菜单浏览、分类筛选、商品详情、购物车、结账
- 原生 Android `View`、`TextView`、`ImageView`、`RecyclerView` 全部正常工作
- 从真实 APK 资源中解析二进制 XML 布局
- 13 个测试应用中 2,476 项测试通过
- 193K+ 行未修改的 AOSP 框架代码（166 个文件）编译并运行

## 架构

```
┌─────────────────────────────────────────────────┐
│            Android APK（未修改）                   │
│        DEX 字节码 + 资源 + 清单文件                  │
├─────────────────────────────────────────────────┤
│          Android 框架（客户运行时）                   │
│   View 树 · Activity 生命周期 · LayoutInflater      │
│   193K 行 AOSP 代码运行在 Dalvik/ART 上             │
├─────────────────────────────────────────────────┤
│             Westlake 引擎层                       │
│   ShimCompat · OHBridge · MiniActivityManager     │
│   MiniServer · BinaryXmlParser                    │
├─────────────────────────────────────────────────┤
│        约 25 个 C 桥接函数 + 1 个宿主 Activity       │
├─────────────────────────────────────────────────┤
│             OpenHarmony 操作系统                    │
│   XComponent 画面 · 输入 · 系统服务                  │
└─────────────────────────────────────────────────┘
```

**平台耦合度：** 仅 **25 个 C 函数** 和 **1 个宿主 Activity** 将 Android 客户运行时连接到 OpenHarmony。桥接层以上的所有代码都是未修改的 AOSP 原始代码。

## 核心组件

| 组件 | 位置 | 用途 |
|------|------|------|
| **MiniServer** | `shim/java/com/` | 轻量级替代 Android 的 `system_server`；无需完整 OS 即可管理服务 |
| **MiniActivityManager** | `shim/java/com/` | Activity 生命周期管理 — `startActivity()`、返回栈、Intent 路由 |
| **OHBridge** | `westlake-host/jni/` | Android 客户运行时与 OpenHarmony 原生 API 之间的 JNI 桥接（C） |
| **ShimCompat** | `shim/java/android/` | 2,056 个 shim 类，以最小实现满足 Android 框架依赖 |
| **BinaryXmlParser** | `shim/java/android/` | 解析真实 APK 中的 Android 二进制 XML（AXML）布局和 `resources.arsc` |
| **Westlake Host** | `westlake-host/` | 托管 Android 客户运行时的唯一 OpenHarmony Activity |
| **OHOS Deploy** | `ohos-deploy/` | 设备端部署：Dalvik VM、ART 引导镜像、`liboh_bridge.so` |

## 仓库结构

```
├── shim/              # Android 框架 shim 层（Java + C++ + Rust 桥接）
│   ├── java/          # 2,056 个 shim 类（android.*、dalvik.*、libcore.*）
│   └── bridge/        # 原生桥接代码（C++、Rust）
├── westlake-host/     # OpenHarmony 宿主应用（1 个 Activity + JNI 桥接）
├── ohos-deploy/       # 设备端运行时：Dalvik VM、ART 引导镜像、库文件
├── dalvik-port/       # Dalvik VM 移植到 OpenHarmony（KitKat，64 位）
├── test-apps/         # 13 个测试应用（MockDonalds、计算器等）
├── docs/engine/       # 详细技术文档
├── backend/           # 分析后端（Python/FastAPI）
├── frontend/          # Web 前端
└── scripts/           # 构建和部署脚本
```

## 构建和测试

### 前置条件

- Android SDK（API 30+），包含 `d8`、`aapt2` 和 `adb`
- OpenHarmony 设备或模拟器（用于真机测试）
- Java 11+（用于 shim 编译）

### 构建 shim DEX

```bash
# 编译所有 shim 类并打包为 DEX
cd shim/
./build.sh          # 生成 aosp-shim.dex
```

### 构建宿主 APK

```bash
cd westlake-host/
./build-apk.sh      # 生成 westlake-host.apk
```

### 部署并在设备上运行

```bash
cd westlake-host/
./deploy.sh          # 推送运行时 + APK，在设备上启动
```

### 运行测试

```bash
# 测试通过 Dalvik/ART 运行时在设备上运行
cd test-apps/
./run-tests.sh       # 13 个应用中的 2,476 项测试
```

## 文档

详细技术文档位于 `docs/engine/`：

| 文档 | 描述 |
|------|------|
| [架构设计](docs/engine/ARCHITECTURE.md) | 完整引擎设计：为什么 Android 在 OH 上"只是另一个 Flutter 应用" |
| [架构设计（中文）](docs/engine/ARCHITECTURE_CN.md) | 架构设计文档（中文版） |
| [Westlake 状态](docs/engine/WESTLAKE-STATUS.md) | 当前里程碑追踪和路线图 |
| [调用流程](docs/engine/CALL-FLOWS.md) | 关键操作的详细调用流程图 |
| [性能分析](docs/engine/PERFORMANCE-ANALYSIS.md) | 7 层性能差距分析：Dalvik vs ART、渲染、输入 |
| [真实 APK 状态](docs/engine/REAL-APK-STATUS.md) | 真实 APK 兼容性测试状态 |
| [执行计划](docs/engine/EXECUTION-PLAN.md) | 分步实施计划 |

## 许可证

Apache 2.0 — 详见 [LICENSE](LICENSE)。
