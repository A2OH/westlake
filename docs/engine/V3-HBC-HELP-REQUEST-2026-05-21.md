# 紧急请教：Westlake V3 部署 /system 反复回滚

**发件：** Westlake V3 团队（[REDACTED-EMAIL]）
**日期：** 2026-05-21
**对象：** user / adapter / 部署组同事

## TL;DR

我们尝试在自己的 DAYU200 上跑你们的 `deploy_stage.sh`（直接 AS-IS），9 次部署后都遇到同一个怪现象：**Stage 3.9 全部 PASS，Stage 3.5 reboot 后 /system 完全回到 factory baseline（连 .orig_* 备份都没了）**。我们已经排除了多个常见原因，但还是找不到根因，想请教你们这条链路在你们自己的 dev 板上到底是怎么 work 的。

## 我们做了什么

1. 操作员用你们的 `D200/V7-images/` 烧到我们的 DAYU200（RKDevTool，HBC 的 V7-images，2026-04-04 build + 我们今天 pull 的新版本）
2. 拉了你们 `$HOME/adapter/deploy/deploy_stage.sh`（38398B，2026-05-19 timestamp）
3. 同步拉了 `$HOME/adapter/out/` 下当前的 artifacts（27 boot-image 段 + oh-adapter-framework.jar + oh-adapter-runtime.jar + 4 个 .so + appspawn-x，md5 都和你们 live 对上）
4. 极简改动（≤5 行）：HDC 路径换成我们 Windows hdc.exe、ADAPTER_ROOT 指向 mirror，MSYS_NO_PATHCONV=1，加了 wslpath fallback。其他完全不动
5. 按你们 SOP 顺序跑：stages `0 1 2 3.0 3b 3c 3d 3e 3f 3.9 3.5`

## 现象（每次都一样）

- Stages 0..3.9 全部 PASS（101 个文件 md5+size 在 staging 验过，再 cp 到 /system，restorecon 也跑了，appspawn-x 的 SELinux label 是 `appspawn_exec:s0` 正确，boot.* 是 `system_lib_file:s0` 正确）
- Stage 3.5 fire `sync; reboot`，HBC 脚本默认 poll 300s，超时报错
- **板子其实没死**，大概要等 7-9 分钟才重新 enumerate（首次 V3 部署后 boot 时间异常长）
- 板子回来后：
  - `/system/bin/appspawn-x` MISSING
  - `/system/lib/libwms.z.so` 存在但 mtime 是 2026-04-04（factory baseline，不是我们部署的）
  - 13 个 `.orig_20260521` 备份全部消失
  - `/system/android/` 整个目录消失
  - 用 `find /system -newer /system/build.prop` 找新文件，结果 0
  - SELinux Enforcing，所有 OH 服务正常起

## 我们经过实测已经排除的假设

| 假设 | 实测结论 |
|---|---|
| H1: source patch 里有禁用 /system 重建的补丁 | 71 个本地 patch + 86 个 server-side patch 全扫了，无 disable patch |
| H2: `appspawn_x.cfg` 配错 / 损坏 | 与你们 live md5 一致（3e1f9ac1），critical/start-mode 都对 |
| H3: `module_update_sa` / `sys_installer` 主动恢复 | 都是 `ondemand:true`，不会自动起 |
| H4: `mksandbox system` 复制磁盘 | 它只是 bind-mount namespace，不动盘 |
| H5: `/data/update/ota_package/` 有残留 OTA | 该目录每次 boot 后都被重建为空，里面什么都没有 |
| H6: `write_updater` 写 `/misc` 触发 bootloader recovery 模式重刷 | **我们专门在 reboot 前 dd 清零了 /misc，验证清零生效到 reboot 之后 /misc 仍然是 zeros — 但 /system 还是被刷回 factory** |
| H7: ramdisk overlay / overlayfs | mount 表里只有 ext4 + tmpfs，无 overlayfs |
| H8: dm-verity 强制完整性 | cmdline 无 verity=、dm=；/sys/block/dm-* 不存在 |

## 我们具体想请教

1. **你们自己的 dev 板上，跑完 Stage 3.5 reboot 之后，是怎么保证 /system 写入 persists 的？** 是 ROM build 时关了什么？init 跑了什么 hook？还是有什么命令行参数？
2. 你们的 dev 板 V7-images 和你们 push 到 `$HOME/D200/V7-images/` 的 SHARED 版本是同一个吗？还是说你们日常自己刷的是别的 image（比如 eng_system 或某个本地 build）？
3. 我们看到 `/system/etc/init/updater_normal.cfg` 里的 `write_updater` 触发条件是 `param:persist.global.locale=*`（trivially-true）。**你们在自己环境里有禁掉这个触发吗？** 还是说有别的原因让它不触发？
4. `libinstalls.z.so`：我们看到你们最近从 `adapter/out/` 移除了，但 `deploy_stage.sh` 第 80/487/533/611 行还在 unconditional 引用它。这是 known issue 吗？你们最近的 deploy 是怎么跑过去的？
5. 你们最近 Apr 7 那个 `$HOME/oh/out/rk3568/packages/phone/images/system.img`（2.1GB，比 V7-images/system.img 新 3 天）是日常用的 build 吗？我们应该用那个吗？

## 我们的环境

- 板子：DAYU200，根据你们 V7-images 烧的
- 部署主机：Windows + WSL（用 Windows-native `hdc.exe 3.2.0b`）
- ROM 是你们 `D200/V7-images/` 2026-04-04 build
- 部署的 adapter artifacts 是今天（2026-05-21）从你们 server 全量同步过来的
- 9 次部署都同一个症状

## 我们已生成的证据材料

如果有用的话，下面是我们今天产生的关键调研文档（在我们 Linux 主机的 `$HOME/android-to-openharmony-migration/docs/engine/` 下）：

- `V3-HBC-SYSTEM-RESTORATION-PATCHES.md` — patch 排查
- `V3-V7-INIT-PRE-INIT-CHAIN.md` — init.cfg 链路分析（identified write_updater）
- `V3-SYSTEM-RESTORATION-MECHANISM.md` — 分区表 + updater 分区分析
- `V3-MISC-WIPE-FIX-RUN.md` — /misc wipe 实测失败的 forensic
- `V3-HBC-DEPLOY-STAGE-AS-IS-RUN.md` — 我们 AS-IS 跑你们脚本的完整 log

需要的话可以贴出来。

非常感谢你们的时间！我们目前对 /system 反复 revert 的真实机制完全没头绪，再调一轮我们都要走 serial UART 这种重投入的路径了，希望能从你们这边借力。

—— Westlake V3
2026-05-21
