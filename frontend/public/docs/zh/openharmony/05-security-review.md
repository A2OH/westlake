# OpenHarmony 4.1 安全子系统 -- 全面代码审查

**审查者：** Claude Opus 4.6（自动化）
**日期：** 2026-03-10
**范围：** `/base/security/`（所有子目录）和 `/base/useriam/`
**严重性等级：** 严重 / 高 / 中 / 低 / 提示

---

## 概要总结

OpenHarmony 安全子系统规模庞大，在许多方面架构设计合理。它使用成熟的库（OpenSSL、mbedTLS）而非自定义加密实现，在大多数关键路径中应用 `memset_s` 进行密钥清零，并且通常在 IPC 中执行权限检查。然而，本审查发现了若干**严重**和**高**等级问题，其中最值得关注的是**硬编码的加密密钥被编译到生产代码中**、**使用不安全的内存函数**以及**AES-ECB 模式可用**。此外还发现了若干中等严重性的问题，涉及缺少 IV/nonce 唯一性验证和潜在的整数溢出边界情况。

---

## 严重发现

### SEC-C01：源代码中硬编码的平台私钥
- **严重性：** 严重
- **文件：** `base/security/huks/services/huks_standard/huks_engine/main/core_dependency/src/hks_chipset_platform_key_hardcoded.c:25-28`
- **描述：** 一个 32 字节的 ECC 私钥（`PLATFORM_KEY_PLATFORM_PRI_KEY`）作为 `static const` 字节数组被硬编码在源代码中。此密钥在 `HksChipsetPlatformDeriveKeyAndEcdh()`（第 105 行）中用于 ECDH 密钥协商。Salt 参数被显式忽略（第 110 行：`(void)(salt);`），注释说明它"应在基于真实硬件的实现中使用"。
- **影响：** 任何能够访问源代码或编译后二进制文件的人都可以提取此私钥，冒充平台或解密受派生密钥保护的数据。这完全破坏了芯片平台密钥安全模型。
- **建议：** 此文件应仅在开发/测试构建中使用。生产构建必须从硬件支持的存储（TEE/HSM）获取平台密钥。添加构建时保护（`#error`）以防止此文件被编译到发布映像中。

### SEC-C02：硬编码的默认认证令牌密钥
- **严重性：** 严重
- **文件：** `base/security/huks/services/huks_standard/huks_engine/main/core/src/hks_keyblob.c:616-627`
- **描述：** 当未定义 `HKS_SUPPORT_GET_AT_KEY` 时，认证令牌的 MAC 密钥和加密密钥从静态 ASCII 字符串派生：`"huks_default_user_auth_token_mac"` 和 `"huks_default_user_auth_cipherkey"`。这些在所有设备上都是确定性且相同的。
- **影响：** 攻击者可以在任何激活此回退路径的设备上伪造认证令牌或解密受保护的密钥 blob。HUKS 管理密钥的用户认证访问控制被完全绕过。
- **建议：** 完全移除回退路径或确保在生产环境中始终定义 `HKS_SUPPORT_GET_AT_KEY`。添加编译时断言以防止在发布配置中不带此标志进行构建。

---

## 高等级发现

### SEC-H01：Crypto Framework 和 HUKS 中暴露了 AES-ECB 模式
- **严重性：** 高
- **文件：** `base/security/crypto_framework/plugin/openssl_plugin/crypto_operation/cipher/src/cipher_aes_openssl.c:52-65, 203-206`
- **文件：** `base/security/huks/frameworks/huks_standard/main/common/src/hks_base_check.c:100, 135, 1317`
- **描述：** AES-ECB 模式可用，甚至是 `DefaultCiherType()` 返回的**默认**加密类型（第 203-206 行）。ECB 模式不提供语义安全性 -- 相同的明文块产生相同的密文块，使得模式分析攻击成为可能。
- **影响：** 未指定模式或显式选择 ECB 的应用将使用加密强度较弱的加密。这对于图像/数据加密尤其危险，因为模式会被保留。
- **建议：** 移除 ECB 作为默认模式。返回错误而非静默回退。考虑弃用或完全移除 ECB，或至少在选择 ECB 时记录警告。

### SEC-H02：SELinux 共享内存中不安全的 `memcpy` 无边界检查
- **严重性：** 高
- **文件：** `base/security/selinux_adapter/interfaces/policycoreutils/src/selinux_share_mem.c:61`
- **描述：** `WriteSharedMem()` 函数使用裸 `memcpy(sharedMem, data, length)` 而未验证 `length` 是否超过共享内存区域大小。共享内存大小在 `InitSharedMem()` 时通过 `spaceSize` 确定，但 `WriteSharedMem()` 没有引用此边界。
- **影响：** 提供大于映射共享内存区域的 `length` 的调用者会导致缓冲区溢出，可能在安全关键的 SELinux 策略组件中导致代码执行或权限提升。
- **建议：** 将共享内存总大小传递给 `WriteSharedMem()` 并在复制前验证 `length <= spaceSize`。使用 securec 的 `memcpy_s`。

### SEC-H03：`VerifyAccessTokenInner` IPC 缺少权限检查
- **严重性：** 高
- **文件：** `base/security/access_token/services/accesstokenmanager/main/cpp/src/service/accesstoken_manager_stub.cpp:106-112`
- **描述：** `VerifyAccessTokenInner()` 处理程序从 IPC 消息中读取 `tokenID` 和 `permissionName` 并直接调用 `VerifyAccessToken()`，对调用者没有任何权限检查。虽然此函数执行只读查询，但它允许任何进程探测任意令牌是否具有任意权限。
- **影响：** 非特权应用可以枚举系统上任何其他应用被授予的权限，为权限提升攻击提供侦察。敏感权限状态（如相机、麦克风授权）可被恶意应用发现。
- **建议：** 要求调用者持有 `GET_SENSITIVE_PERMISSIONS` 权限或为原生/特权进程。或者，除非持有权限，否则将 `tokenID` 限制为调用者自身的令牌。

### SEC-H04：GCM IV 最大长度设为 16 字节（未强制推荐的 12 字节）
- **严重性：** 高
- **文件：** `base/security/crypto_framework/plugin/openssl_plugin/crypto_operation/cipher/src/cipher_aes_openssl.c:29-30`
- **描述：** AES-GCM 实现接受 1 到 16 字节的 IV（`GCM_IV_MIN_LEN = 1`，`GCM_IV_MAX_LEN = 16`）。虽然根据 NIST 规范这在技术上是有效的，但 NIST SP 800-38D 强烈建议 GCM 使用 12 字节的 IV。非 12 字节长度的 IV 需要经过 GHASH 处理，这可能降低安全保证并引入微妙的漏洞。
- **影响：** 短 IV（1-11 字节）显著减少了随机性空间，增加了碰撞概率。极度宽松的最小值 1 字节尤其危险，因为它使 IV 碰撞（以及由此导致的完整认证密钥恢复）在正常使用下几乎确定会发生。
- **建议：** 强制 `GCM_IV_MIN_LEN = 12` 和 `GCM_IV_MAX_LEN = 12`（或至少将最小值设为 12）。对非 12 字节的 IV 记录弃用警告。

---

## 中等级发现

### SEC-M01：AES 加密操作中无 IV/Nonce 唯一性验证
- **严重性：** 中
- **文件：** `base/security/crypto_framework/plugin/openssl_plugin/crypto_operation/cipher/src/cipher_aes_openssl.c:275-286`
- **描述：** GCM、CCM、CBC 和 CTR 模式中的 IV/nonce 参数按原样从调用者处接受。没有机制检测或防止同一密钥下的 IV 重用。对于 GCM 和 CTR，同一密钥下的 IV 重用是灾难性的（使得明文恢复和 GCM 的认证密钥提取成为可能）。
- **影响：** 管理不当 IV（重用）的应用将静默产生不安全的密文。
- **建议：** 虽然在框架层面强制唯一性很复杂，但可以考虑：(1) 提供自动递增/随机 IV 生成模式，(2) 在文档中突出说明此要求，(3) 如果在会话中检测到同一密钥使用相同的 IV 则记录日志。

### SEC-M02：Base64 编码中的整数溢出检查有缺陷
- **严重性：** 中
- **文件：** `base/security/device_security_level/baselib/utils/src/utils_base64.c:68`
- **描述：** 溢出检查 `if (toLen >= (toLen * 4))` 在逻辑上是有问题的。由于 C 中无符号整数溢出的语义，如果 `toLen` 足够大使得 `toLen * 4` 溢出并回绕到小于 `toLen` 的值，检查会正确通过。然而，如果 `toLen` 为 0（由于 `fromLen > 0` 意味着 `toLen >= 1`，这不可能发生），或如果 `toLen * 4` 恰好溢出到正好等于 `toLen`，检查会失败。标准的溢出检查如 `toLen > UINT32_MAX / 4` 会更清晰和可靠。
- **建议：** 替换为 `if (toLen > UINT32_MAX / 4)` 以获得清晰、正确的溢出检查。

### SEC-M03：`HKS_FREE_BLOB` 在释放前不进行清零
- **严重性：** 中
- **文件：** `base/security/huks/frameworks/huks_standard/main/common/include/hks_mem.h:49-55`
- **描述：** `HKS_FREE_BLOB` 宏在释放 blob 数据前不清零。只有 `HKS_MEMSET_FREE_BLOB` 执行清零。多个调用点（例如 `hks_client_service_ipc.c`）使用 `HKS_FREE_BLOB` 来释放可能包含密钥材料的 blob，将敏感数据留在已释放的内存中。
- **影响：** 密钥材料可能残留在已释放的堆内存中，可通过内存分析、堆喷射攻击或核心转储恢复。
- **建议：** 审计所有 `HKS_FREE_BLOB` 调用点。在 blob 可能包含密钥材料、会话密钥或其他秘密的地方，替换为 `HKS_MEMSET_FREE_BLOB`。考虑让 `HKS_FREE_BLOB` 始终清零，因为 `memset_s` 在小缓冲区上的性能开销可以忽略不计。

### SEC-M04：令牌同步 SoftBus 通道缺少数据认证
- **严重性：** 中
- **文件：** `base/security/access_token/services/tokensyncmanager/src/remote/soft_bus_channel.cpp:262-307`
- **描述：** 令牌同步数据通过 SoftBus 传输，使用 zlib 压缩/解压缩，但在此层没有明显的消息认证或完整性验证。`Decompress()` 函数在没有对传入压缩数据进行任何 HMAC 或签名检查的情况下分配 1MB（`RPC_TRANSFER_BYTES_MAX_LENGTH`）用于解压缩。
- **影响：** 如果 SoftBus 传输层不提供完整性保护（或该保护被破坏），攻击者可以注入精心构造的压缩数据来操纵令牌同步，可能向远程令牌授予未经授权的权限。1MB 的解压缩缓冲区也是潜在的解压缩炸弹攻击向量。
- **建议：** 在压缩前/解压缩后的应用层添加消息认证（HMAC 或签名）。根据预期边界验证解压缩后的大小。考虑对令牌同步负载使用认证加密。

### SEC-M05：Scrypt PIN 派生使用按值传递处理敏感数据
- **严重性：** 中
- **文件：** `base/useriam/pin_auth/frameworks/scrypt/src/scrypt.cpp:48, 89`
- **描述：** `DoScrypt()` 和 `GetScrypt()` 函数按值接受 PIN `data` 参数（`std::vector<uint8_t> data`），在栈上创建了敏感 PIN 材料的副本。虽然 `ClearPinData()` 清零了本地副本（第 83 行），但调用者的副本和向量复制期间创建的任何中间副本未被清零。
- **影响：** PIN 材料可能在函数完成后残留在内存中的多个位置。
- **建议：** 按引用传递（`std::vector<uint8_t> &data`）以避免创建不必要的 PIN 材料副本。

### SEC-M06：UserAuth IPC Stub 多个端点缺少授权检查
- **严重性：** 中
- **文件：** `base/useriam/user_auth_framework/frameworks/native/ipc/src/user_auth_stub.cpp:37-72`
- **描述：** `OnRemoteRequest` 处理程序根据 IPC 代码分发到各种处理程序（`GetAvailableStatusStub`、`GetPropertyStub`、`AuthStub` 等）。描述符令牌检查已存在（第 40 行），但各个处理程序必须执行自己的权限检查。`GetAvailableStatusStub()` 处理程序（第 74-103 行）没有执行权限检查。虽然检查可能在调用链更深处（`UserAuthService`）进行，但纵深防御要求在 Stub 层进行检查。
- **建议：** 在 Stub 层为所有敏感操作添加权限检查，而不仅仅在服务实现中。记录哪些操作是有意公开的。

### SEC-M07：证书管理器目录权限 0705 允许其他用户执行
- **严重性：** 中
- **文件：** `base/security/certificate_manager/services/cert_manager_standard/cert_manager_engine/main/core/src/cert_manager_file_operator.c:242`
- **描述：** 默认目录权限 `S_IRWXU | S_IROTH | S_IXOTH`（0705）允许"其他"用户读取和遍历证书存储目录。
- **影响：** 其他进程可以列出和读取证书文件，可能访问私有证书或 CA 信任存储。
- **建议：** 使用 0700（`S_IRWXU`）作为默认值，仅允许所有者进程访问目录。

---

## 低等级发现

### SEC-L01：无边界 Parcel 读取的潜在拒绝服务
- **文件：** `base/security/access_token/services/accesstokenmanager/main/cpp/src/service/accesstoken_manager_stub.cpp:126-141`
- **描述：** `GetDefPermissionsInner()` 从存储中读取权限列表，并将所有权限写入回复 Parcel 而无上限检查。而 `GetSelfPermissionsStateInner()`（第 182 行）正确地使用 `MAX_PERMISSION_SIZE` 进行了边界检查，`GetDefPermissions` 路径却没有。
- **影响：** 如果权限列表非常大，可能导致回复 Parcel 构建中的过度内存分配。
- **建议：** 添加与 `MAX_PERMISSION_SIZE` 一致的大小限制检查。

### SEC-L02：调试日志泄漏密钥别名字节
- **文件：** `base/security/device_auth/services/legacy/authenticators/src/account_unrelated/pake_task/pake_v1_task/pake_v1_protocol_task/pake_v1_protocol_task_common.c:147`
- **描述：** `LOGI("Psk alias(HEX): %x%x%x%x****.", pskKeyAliasVal[0], pskKeyAliasVal[1], pskKeyAliasVal[2], pskKeyAliasVal[3]);` 在生产日志中记录了密钥别名的前 4 个字节。虽然这是别名（非密钥材料），但它泄漏了可能有助于密钥枚举的信息。
- **建议：** 移除或放在调试标志保护之后。

### SEC-L03：strnlen 的差一长度检查使用不正确
- **文件：** `base/security/device_auth/services/key_agree_sdk/src/key_agree_sdk.c:40, 48, 229`
- **描述：** 类似 `strnlen((const char *)(sharedSecret->data), sharedSecret->length) > sharedSecret->length + 1` 的检查中 `+ 1` 有疑问。如果字符串恰好是 `length` 字节（缓冲区内无空终止符），`strnlen` 返回 `length`，检查 `> length + 1` 为假，这是预期行为。但语义意图令人困惑且容易出错。
- **建议：** 明确意图并简化边界检查。

### SEC-L04：某些路径中 SQL 使用字符串格式化而非参数化查询
- **文件：** `base/security/access_token/services/common/database/src/sqlite_helper.cpp:153`
- **描述：** `sqlite3_exec(db_, sql.c_str(), ...)` 使用预构建的 SQL 字符串。虽然 `Statement` 类（第 27-29 行）确实使用了带参数绑定的 `sqlite3_prepare_v2`，但一些直接 exec 路径如果 SQL 字符串由用户输入构造则可能存在漏洞。
- **影响：** 风险较低 -- 大多数 SQL 字符串似乎是架构 DDL 和内部生成的查询，而非源自用户输入。
- **建议：** 审计所有 `sqlite3_exec` 调用点以确认没有用户输入进入 SQL 字符串。

---

## 提示 / 正面观察

### SEC-I01：密钥材料清零中良好使用 memset_s
- **文件：** `crypto_framework/plugin/openssl_plugin/key/sym_key_generator/src/sym_key_openssl.c:75, 179`
- 对称密钥材料在 `ClearMem()` 和 `DestroySymKeySpi()` 中使用 `memset_s` 在释放前正确清零。这是正确的做法。

### SEC-I02：密钥生成正确使用 RAND_priv_bytes
- **文件：** `crypto_framework/plugin/openssl_plugin/key/sym_key_generator/src/sym_key_openssl.c:124`
- 对称密钥生成使用 `Openssl_RAND_priv_bytes()`，这是生成敏感随机材料的适当 API（而非 `RAND_bytes` 或 `rand()`）。

### SEC-I03：未使用不安全的伪随机数生成器（rand/srand/random）
- 在安全子系统的 C 源文件中未发现 `rand()`、`srand()` 或 `random()` 的实例。这确认了整个子系统使用了密码学安全的随机数生成。

### SEC-I04：Access Token Manager 中一致的 IPC 权限检查
- `accesstoken_manager_stub.cpp` 在几乎所有修改权限状态的 IPC 处理程序（授予、撤销、删除、更新）上一致地应用权限检查（`IsPrivilegedCalling()`、`VerifyAccessToken()`、`IsNativeProcessCalling()`、`IsSystemAppCalling()`）。

### SEC-I05：HUKS 进程名注入防护
- **文件：** `base/security/huks/services/huks_standard/huks_service/main/core/src/hks_client_service.c:285-288`
- HUKS 服务显式检查并拒绝客户端提供的 `HKS_TAG_PROCESS_NAME` 参数，防止客户端冒充另一个进程的密钥命名空间。这是对参数注入的良好防御。

### SEC-I06：HUKS 密钥 Blob Nonce 随机生成
- **文件：** `base/security/huks/services/huks_standard/huks_engine/main/core/src/hks_keyblob.c:294-296`
- 密钥 blob 加密的 Nonce 使用 `HksCryptoHalFillRandom()` 生成，确保 AEAD 操作的随机性正确。

### SEC-I07：设备认证 PAKE 实现正确清零返回密钥
- **文件：** `base/security/device_auth/services/legacy/authenticators/src/account_unrelated/pake_task/pake_v1_task/pake_v1_protocol_task/pake_v1_protocol_task_common.c:36`
- 返回密钥材料在释放前使用 `memset_s` 清零。

### SEC-I08：Asset 模块（Rust）提供内存安全
- **文件：** `base/security/asset/services/core_service/src/operations/operation_add.rs`
- Asset 存储模块使用 Rust 实现，提供内存安全保证（无缓冲区溢出、释放后使用等）。秘密数据加密使用每个所有者独立的密钥模型，并绑定 AAD。

### SEC-I09：存在模糊测试基础设施
- access_token、HUKS、crypto_framework 和 device_auth 中存在广泛的模糊测试用例，覆盖了关键的 IPC 接口和 API 接口。

---

## 按严重性分类的发现汇总

| 严重性 | 数量 | 关键问题 |
|--------|------|----------|
| 严重 | 2 | 硬编码的平台密钥、硬编码的认证令牌密钥 |
| 高 | 4 | AES-ECB 默认模式、不安全的 memcpy、IPC 认证缺失、GCM IV 边界过松 |
| 中 | 7 | 无 IV 重用检测、整数溢出检查、密钥清零缺陷、令牌同步认证、PIN 复制、UserAuth 桩函数、证书目录权限 |
| 低 | 4 | 无边界 Parcel 读取、调试密钥别名泄漏、strnlen 逻辑、SQL exec 路径 |
| 提示 | 9 | 加密实践、PRNG 使用、权限检查方面的正面观察 |

---

## 优先建议（按优先级排序）

1. **立即对硬编码密钥文件添加门禁**（`hks_chipset_platform_key_hardcoded.c`、`hks_keyblob.c` 中的默认 AT 密钥），在生产构建中使用 `#error`。这些代表了完整的安全绕过。
2. **移除 AES-ECB 作为默认**加密模式，并强制最小 12 字节的 GCM IV。
3. **为 SELinux 适配器中的 `WriteSharedMem()` 添加边界验证**。
4. **为 `VerifyAccessTokenInner` 添加权限检查**以防止跨应用权限状态枚举。
5. **审计所有 `HKS_FREE_BLOB` 的使用**，在 blob 可能包含秘密的地方替换为 `HKS_MEMSET_FREE_BLOB`。
6. **为令牌同步 SoftBus 通道添加消息认证**。
7. **修复 PIN scrypt 函数签名**，通过引用传递敏感数据。
