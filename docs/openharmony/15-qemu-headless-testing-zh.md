# OpenHarmony 4.1 QEMU 无头测试环境

**日期：** 2026-03-15
**范围：** 在 QEMU ARM32 上构建和启动 OpenHarmony，用于无头 API 测试
**作者：** Claude Opus 4.6 + dspfac

---

## 概述

我们构建了一个功能完整的 OpenHarmony 4.1 无头测试环境，运行在 QEMU 上，无需真实硬件或显示器即可进行 Ability 框架 API 测试。系统约 5 秒内启动，运行完整的服务栈，包括 ability_manager、bundle_manager 和 app_manager。

## 架构

```
┌─────────────────────────────────────────────────┐
│                  QEMU ARM32                      │
│         (cortex-a7, 1GB 内存, 4 CPU)             │
│                                                  │
│  ┌─────────────┐  ┌──────────────────────────┐  │
│  │    内核      │  │       系统服务             │  │
│  │ Linux 5.10   │  │  samgr, hilogd, hdcd,    │  │
│  │              │  │  faultloggerd, softbus,   │  │
│  │  ARM32 SMP   │  │  accesstoken, deviceauth │  │
│  └─────────────┘  └──────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐   │
│  │         Foundation (sa_main)               │   │
│  │  SA 180: libabilityms.z.so (元能力管理)   │   │
│  │  SA 401: libbms.z.so (包管理)             │   │
│  │  SA 501: libappms.z.so (应用管理)         │   │
│  └──────────────────────────────────────────┘   │
│                                                  │
│  ┌────────────┐  ┌──────────┐  ┌────────────┐  │
│  │  466 个库   │  │  控制台   │  │ /dev/binder│  │
│  │  (.z.so)    │  │   (sh)   │  │  IPC 就绪  │  │
│  └────────────┘  └──────────┘  └────────────┘  │
│                                                  │
│  磁盘布局 (virtio-mmio, 反序):                   │
│  vda=updater, vdb=system(256MB), vdc=vendor,    │
│  vdd=userdata                                    │
└─────────────────────────────────────────────────┘
```

## 快速开始

### 1. 构建最小系统（约 2.5 分钟）
```bash
export SOURCE_ROOT_DIR=/home/dspfac/openharmony
export PATH=${SOURCE_ROOT_DIR}/prebuilts/python/linux-x86/current/bin:${SOURCE_ROOT_DIR}/prebuilts/build-tools/linux-x86/bin:/home/dspfac/miniconda3/bin:$PATH
python3 ${SOURCE_ROOT_DIR}/build/hb/main.py build --product-name qemu-arm-linux-min --no-prebuilt-sdk
```

### 2. 准备镜像
```bash
./tools/prepare_images.sh
```

### 3. 启动
```bash
./tools/qemu_boot.sh
```

### 4. 验证
```
# ps -A
  PID CMD
    1 init
   88 hilogd
   89 device_manager
   90 hdcd
   91 sh
   92 samgr
   93 param_watcher
  100 deviceauth_serv
  105 faultloggerd
  106 softbus_server
  107 accesstoken_ser

# param get ohos.boot.hardware
qemu.arm.linux

# param get const.ohos.fullname
OpenHarmony-4.1.10.5
```

## 运行中的服务

### 核心服务（11+）
| 服务 | PID | 说明 |
|------|-----|------|
| init | 1 | 系统初始化（已修复 use-after-free 缺陷） |
| samgr | 92 | 系统能力管理器 |
| hilogd | 88 | 系统日志服务 |
| faultloggerd | 105 | 崩溃处理服务 |
| softbus_server | 106 | 分布式软总线 |
| accesstoken_service | 107 | 权限管理服务 |
| deviceauth_service | 100 | 设备认证服务 |
| device_manager | 89 | 设备管理服务 |
| param_watcher | 93 | 参数服务 |
| hdcd | 90 | 调试守护进程 (hdc) |
| console | 91 | 交互式 Shell |

### IPC 基础设施
- `/dev/binder` — Binder IPC 通信
- `/dev/vndbinder` — 厂商 Binder
- 9 个 Unix 域套接字（paramservice、hilog、faultloggerd、hdcd 等）

### 无头构建（466 个库，Ability 框架）
无头构建下，Foundation 加载以下系统能力：
- **SA 180**：`libabilityms.z.so` — 元能力生命周期管理
- **SA 401**：`libbms.z.so` — 包管理（HAP 安装）
- **SA 501**：`libappms.z.so` — 应用进程管理

## 发现并修复的缺陷

### 1. Init 触发器释放后使用（严重）
**文件：** `base/startup/init/services/param/trigger/trigger_processor.c`

`ExecuteQueueWork()` 在 `StartTriggerExecute_()` 之后调用 `GetTriggerName(trigger)`，但后者会释放带有 `TRIGGER_FLAGS_ONCE` 标志的触发器。被释放触发器的 `type` 字段被破坏，导致 `GetTriggerName()` 中发生 SIGSEGV 段错误。

**修复：** 在执行前将触发器名称保存到栈缓冲区。

### 2. OpenSSL 构建目标错误
**文件：** `third_party/openssl/make_openssl_build_all_generated.sh`

使用了 `make build_generated` 而非 `make build_all_generated`，导致缺少 `buildinf.h` 和 `der_*.h` 头文件。

### 3. GN defines 覆盖问题（321 个文件，837 行）
系统性缺陷：`if` 块内的 `defines = []` 会重置父作用域中累积的 defines。在整个代码库的 321 个 BUILD.gn 文件中发现此问题。

### 4. 缺少 SUPPORT_GRAPHICS 条件编译保护（30+ 个文件）
ability_runtime 框架在 `#ifdef SUPPORT_GRAPHICS` 内声明了许多函数，但实现时没有相应的条件保护。无头构建（`ability_runtime_graphics=false`）时导致编译错误。

### 5. Skia GPU 符号引用缺少条件保护
`render_service_base` 中 10 个文件无条件引用 GPU API（`GetGPUContext`、`GrBackendTexture` 等）。添加了 `#ifdef RS_ENABLE_GL` 保护。

### 6. LTO 模板虚函数表裁剪
`RSCurveValueEstimator<float>` 显式模板实例化在从静态库链接时被 LTO 裁剪。通过在特化声明上添加 `RSB_EXPORT` 并禁用 `-fwhole-program-vtables` 修复。

## 补丁汇总

| 类别 | 修改文件数 | 说明 |
|------|-----------|------|
| GN defines 覆盖 | 321 | 移除冗余的 `defines = []` 重置 |
| SUPPORT_GRAPHICS 保护 | 30+ | ability_runtime、window_manager、access_token |
| Skia/GPU 保护 | 13 | render_service_base `#ifdef RS_ENABLE_GL` |
| EGL/GL/HDI 桩头文件 | 12 | sysroot 中的桩头文件 |
| Ruby 3.4 兼容 | 5 | `ostruct`、`stringio`、`File.exist?` |
| Init 缺陷修复 | 2 | trigger_processor.c、trigger_manager.c |
| OpenSSL 构建 | 2 | make 目标、buildinf.h |
| LTO/虚函数表修复 | 2 | rs_value_estimator.h、render_service_client BUILD.gn |
| 其他桩实现 | 6 | RenderContext、CmdList、UIContent、MissionDataStorage |
| **合计** | **约 75** | |

## 测试能力

### 当前已实现
- 启动至 Shell，11+ 个服务运行
- IPC/Binder 通信正常
- 系统参数读写
- 通过 samgr 注册服务
- 元能力管理服务已加载（SA 180）
- 包管理服务已加载（SA 401）

### 后续计划
- `bm install -p /path/to/test.hap` — 安装测试 HAP 包
- `aa start -a TestAbility -b com.example.test` — 启动 Ability
- 验证 Ability 生命周期回调
- 测试 Ability 间 IPC 通信
- CI/CD 集成

## 文件位置

| 项目 | 路径 |
|------|------|
| 启动脚本 | `tools/qemu_boot.sh` |
| 镜像准备 | `tools/prepare_images.sh` |
| QEMU 程序 | `tools/qemu-extracted/usr/bin/qemu-system-arm` |
| 构建输出 | `out/qemu-arm-linux/packages/phone/images/` |
| 系统库 | `out/qemu-arm-linux/packages/phone/system/lib/` |
| SA 配置 | `out/qemu-arm-linux/packages/phone/system/profile/` |
| Init 配置 | `out/qemu-arm-linux/packages/phone/system/etc/init/` |
