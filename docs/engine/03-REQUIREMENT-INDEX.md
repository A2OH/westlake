# 03-Requirement Corpus Index + Westlake Relevance Map

Generated: 2026-05-19 (agent 56, option 1)
Status: READ-ONLY analysis; corpus is owned by the V3-W3 requirement team.

## Source

- Pull source on GZ02: `/opt/15.WestLake/03-Requirement/`
- Local mirror: `/home/dspfac/android-to-openharmony-migration/artifacts/v3-w3-requirements/`
- Schema: **V5.0** (50 packages, full FORMAL_DRAFT in `package-requirement-design-v5.0/`) +
  **V7.0** (workspace skeletons in `package-requirement-design/`, only `android.net.wifi` and
  `android.app.admin` have substantive content yet)
- Companion older batches kept for traceability:
  - `package-requirement-design-50/` — V4.0 historical predecessor (50 packages)
  - `package-requirement-design-10/` — V3.0 even-older 10-package pilot
- Top-level planner doc: `artifacts/v3-w3-requirements/PACKAGE_PLANNER_CURRENT.md`
- V7.0 templates: `artifacts/v3-w3-requirements/templates/WestLake_Package_Deep_Research_Template_V7.0_Core.md` +
  `WestLake_Profile_V7{A,B,C,D,E,F}.0_*.md` (six domain profiles)

## Schema crib-sheet

### V5.0 per-package files

| File | Purpose |
|---|---|
| `CURRENT.json` | Version + status pointer (all 50 = `FORMAL_DRAFT_IMPLEMENTATION_BLOCKED`) |
| `PACKAGE_REQUIREMENTS_V5.0.md` | Human-facing behavioral requirements |
| `PACKAGE_DESIGN_DRAFT_V5.0.md` | Implementation-facing design draft |
| `METHOD_MATRIX_V5.0.yaml` | One row per Android API member with `behavior_family`, `risk_tier`, `evidence_status`, `route_decision`, `acceptance` |
| `DATA_ALIGNMENT_V5.0.yaml` | AOSP ↔ OH type/field mapping |
| `SOURCE_EVIDENCE_V5.0.md` | First-source pointers |
| `EXTERNAL_REFERENCES_V5.0.md` | Reference placeholders (pending) |
| `MISSING_FACTS_V5.0.md` | Templated 6-row gap table (identical across all 50) |
| `CRITIQUE_V5.0.md` | Auto-self-critique (identical 4-row P0 table across all 50) |

### V5.0 `route_decision` vocabulary

The V5.0 batch declares the route vocabulary as
`SAFE_DEFAULT / OH_BINDER / STUB / REJECT / DISCOVERY_REQUIRED`, but **100% of rows across all 50
packages are `DISCOVERY_REQUIRED`** (see §Inventory). No routing has actually happened yet — this
is the "implementation blocked" status the planner doc warns about.

### V7.0 per-package files (Wi-Fi pilot is the only filled example)

```
android.<pkg>/
  CURRENT.json
  versions/V7.0/
    VERSION.json
    PACKAGE_REQUIREMENTS_V7.0.md       # human-facing (1014 LOC in wifi)
    PACKAGE_DESIGN_V7.0.md             # machine-facing (489 LOC in wifi)
    method_matrix.yaml                 # richer route set (see below)
    data_contract.yaml                 # structures + per-field strategy
    source_evidence.yaml               # AOSP/OH/D200 anchors w/ confidence
    missing_facts.md                   # real per-package P0/P1 ledger
    COMMANDS.md                        # reproducible probe commands
    ORACLE_PILOT_SUBMISSION.md         # wifi only — review packet
    CRITIQUE_ROUND_{1,2,3}.md          # wifi only — repair rounds
    kimi/KIMI_DEEP_RESEARCH_OUTPUT.md  # external LLM raw input
    evidence/                          # raw evidence drops
```

### V7.0 `route` vocabulary (richer than V5.0)

| Route | Meaning |
|---|---|
| `OH_DIRECT` | 1:1 OH API call with error wrapping (e.g. `isWifiEnabled → isWifiActive`) |
| `OH_ADAPTER` | Semantic / state-machine / error-code translation |
| `JAVA_SYNTH` | Pure-Java synthesis (e.g. `calculateSignalLevel`) |
| `UNSUPPORTED_EXPLICIT_FAILURE` | OH/D200 has no equivalent — must fail loudly |
| `HUMAN_DECISION_REQUIRED` | Policy choice owed by project lead |
| `DISCOVERY_REQUIRED` | Still needs source-side investigation |

### V7.0 domain profiles

| Profile | Domain | Example packages |
|---|---|---|
| `V7A.0` | Hardware / Driver / Service | bluetooth, hardware.*, location, net.wifi, nfc, telephony |
| `V7B.0` | UI / View / Widget | animation, content.res, graphics, transition, view, webkit, widget |
| `V7C.0` | Pure-Java / Text / Util | text, text.format, text.method, text.style, util |
| `V7D.0` | Security / Identity / Permission | accounts, app.admin, app.usage, security, security.keystore |
| `V7E.0` | Media / Camera / Codec | hardware.camera2, media.*, speech, speech.tts |
| `V7F.0` | Network / IPC / ContentProvider | app, app.job, content, content.pm, database, database.sqlite, net, os, print, provider, telecom |
| (special) | Accessibility | `android.accessibilityservice` → `V7B.0` |

## Package Inventory (50 packages)

LOC = sum of lines across the 8 V5.0 doc files. Routes: every package has 0/0/0/0/N where
N = all rows = `DISCOVERY_REQUIRED`. Risk = A/B/C from `risk_tier` field.

| # | Package | V5 status | V7 status | V7 profile | Rows | Risk A/B/C | LOC | Top blocker |
|---:|---|---|---|---|---:|---|---:|---|
|  1 | android.accessibilityservice | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 185 | 34/56/95 | 3 563 | OH route DISCOVERY_REQUIRED |
|  2 | android.accounts | FORMAL_DRAFT | WORKSPACE_ONLY | V7D.0 | 121 | 16/55/50 | 2 614 | OH route DISCOVERY_REQUIRED |
|  3 | android.animation | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 231 | 2/220/9 | 4 551 | OH route DISCOVERY_REQUIRED |
|  4 | android.app | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | **1 724** | 133/1101/490 | **25 758** | OH route DISCOVERY_REQUIRED |
|  5 | android.app.admin | FORMAL_DRAFT | **READY_WITH_GAPS** | V7D.0 | 500 | 149/124/227 | 8 116 | Substantive V7 exists; V5 routes still TBD |
|  6 | android.app.job | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 94 | 1/79/14 | 2 171 | OH route DISCOVERY_REQUIRED |
|  7 | android.app.usage | FORMAL_DRAFT | WORKSPACE_ONLY | V7D.0 | 134 | 18/74/42 | 2 927 | OH route DISCOVERY_REQUIRED |
|  8 | android.bluetooth | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 688 | 42/160/486 | 11 425 | OH route DISCOVERY_REQUIRED |
|  9 | android.bluetooth.le | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 196 | 0/140/56 | 4 156 | OH route DISCOVERY_REQUIRED |
| 10 | android.content | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 1 228 | 41/646/541 | 18 814 | OH route DISCOVERY_REQUIRED |
| 11 | android.content.pm | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 836 | 46/274/516 | 13 436 | OH route DISCOVERY_REQUIRED |
| 12 | android.content.res | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 215 | 1/115/99 | 3 997 | OH route DISCOVERY_REQUIRED |
| 13 | android.database | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 299 | 8/269/22 | 5 536 | OH route DISCOVERY_REQUIRED |
| 14 | android.database.sqlite | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 192 | 0/179/13 | 3 785 | OH route DISCOVERY_REQUIRED |
| 15 | android.graphics | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 1 100 | 0/810/290 | 16 912 | OH route DISCOVERY_REQUIRED |
| 16 | android.hardware | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 235 | 25/61/149 | 4 301 | OH route DISCOVERY_REQUIRED |
| 17 | android.hardware.camera2 | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 338 | 18/79/241 | 5 795 | OH route DISCOVERY_REQUIRED |
| 18 | android.hardware.display | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 25 | 5/14/6 | 994 | thinnest matrix in corpus |
| 19 | android.hardware.input | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 9 | 4/3/2 | 647 | thinnest matrix in corpus |
| 20 | android.hardware.usb | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 110 | 4/66/40 | 2 502 | OH route DISCOVERY_REQUIRED |
| 21 | android.location | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 333 | 16/230/87 | 6 034 | OH route DISCOVERY_REQUIRED |
| 22 | android.media | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | **2 584** | 85/1086/1413 | **37 473** | OH route DISCOVERY_REQUIRED |
| 23 | android.media.audiofx | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 388 | 0/279/109 | 6 894 | OH route DISCOVERY_REQUIRED |
| 24 | android.media.midi | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 73 | 5/55/13 | 1 919 | OH route DISCOVERY_REQUIRED |
| 25 | android.media.projection | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 8 | 3/5/0 | 625 | thinnest matrix in corpus |
| 26 | android.media.session | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 175 | 13/128/34 | 3 523 | OH route DISCOVERY_REQUIRED |
| 27 | android.net | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 485 | 19/323/143 | 8 082 | OH route DISCOVERY_REQUIRED |
| 28 | android.net.wifi | FORMAL_DRAFT | **ORACLE_PILOT_READY** | V7A.0 | 251 (V5) / 101 (V7) | 28/98/125 | 4 873 (V5) + 3 771 (V7) | D200 wifi service stopped; wpa_cli blocked; AP syscap |
| 29 | android.net.wifi.aware | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 77 | 2/66/9 | 2 044 | OH route DISCOVERY_REQUIRED |
| 30 | android.net.wifi.p2p | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 133 | 5/81/47 | 2 908 | OH route DISCOVERY_REQUIRED |
| 31 | android.nfc | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 88 | 9/35/44 | 2 020 | OH route DISCOVERY_REQUIRED |
| 32 | android.nfc.tech | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 141 | 0/118/23 | 3 061 | OH route DISCOVERY_REQUIRED |
| 33 | android.os | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 967 | 43/601/323 | 15 085 | OH route DISCOVERY_REQUIRED |
| 34 | android.print | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 169 | 0/64/105 | 3 420 | OH route DISCOVERY_REQUIRED |
| 35 | android.provider | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | **1 884** | 15/242/1627 | **27 669** | OH route DISCOVERY_REQUIRED |
| 36 | android.security | FORMAL_DRAFT | WORKSPACE_ONLY | V7D.0 | 39 | 1/29/9 | 1 293 | OH route DISCOVERY_REQUIRED |
| 37 | android.security.keystore | FORMAL_DRAFT | WORKSPACE_ONLY | V7D.0 | 104 | 0/68/36 | 2 409 | OH route DISCOVERY_REQUIRED |
| 38 | android.speech | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 84 | 0/32/52 | 1 992 | OH route DISCOVERY_REQUIRED |
| 39 | android.speech.tts | FORMAL_DRAFT | WORKSPACE_ONLY | V7E.0 | 133 | 0/82/51 | 2 815 | OH route DISCOVERY_REQUIRED |
| 40 | android.telecom | FORMAL_DRAFT | WORKSPACE_ONLY | V7F.0 | 792 | 47/479/266 | 12 930 | OH route DISCOVERY_REQUIRED |
| 41 | android.telephony | FORMAL_DRAFT | WORKSPACE_ONLY | V7A.0 | 1 567 | 89/346/1132 | 23 325 | OH route DISCOVERY_REQUIRED |
| 42 | android.text | FORMAL_DRAFT | WORKSPACE_ONLY | V7C.0 | 414 | 0/311/103 | 7 098 | OH route DISCOVERY_REQUIRED |
| 43 | android.text.format | FORMAL_DRAFT | WORKSPACE_ONLY | V7C.0 | 55 | 0/29/26 | 1 420 | OH route DISCOVERY_REQUIRED |
| 44 | android.text.method | FORMAL_DRAFT | WORKSPACE_ONLY | V7C.0 | 148 | 0/137/11 | 3 287 | OH route DISCOVERY_REQUIRED |
| 45 | android.text.style | FORMAL_DRAFT | WORKSPACE_ONLY | V7C.0 | 425 | 0/331/94 | 7 262 | OH route DISCOVERY_REQUIRED |
| 46 | android.transition | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 198 | 6/182/10 | 4 104 | OH route DISCOVERY_REQUIRED |
| 47 | android.util | FORMAL_DRAFT | WORKSPACE_ONLY | V7C.0 | 536 | 0/411/125 | 9 019 | OH route DISCOVERY_REQUIRED |
| 48 | android.view | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | **2 709** | 49/1770/890 | **39 393** | OH route DISCOVERY_REQUIRED |
| 49 | android.webkit | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 437 | 16/338/83 | 7 301 | OH route DISCOVERY_REQUIRED |
| 50 | android.widget | FORMAL_DRAFT | WORKSPACE_ONLY | V7B.0 | 1 974 | 180/1670/124 | 29 043 | OH route DISCOVERY_REQUIRED |

**Corpus totals**: 50 packages, ~30 000 method rows, ~1 350 risk-A rows, ~427 000 LOC of V5.0 doc.

### Data-quality observations

- **All 50 V5.0 `route_decision` rows = `DISCOVERY_REQUIRED`.** No package has yet been refined
  with SAFE_DEFAULT / OH_BINDER / STUB / REJECT routing as the planner schema promises. This is
  consistent with the `FORMAL_DRAFT_IMPLEMENTATION_BLOCKED` status banner.
- **`MISSING_FACTS_V5.0.md` and `CRITIQUE_V5.0.md` are identical across all 50 packages**
  (14 and 20 lines respectively, same boilerplate body — only the package name token differs).
  They report the same 6 generic gaps (AOSP behavior, OH equivalent, data alignment, perms,
  D200 validation, official refs) and the same 4 P0/P1 critique rows. Useful for gating
  enforcement but provides zero per-package signal.
- **Only `android.net.wifi` has reached `ORACLE_PILOT_READY`** with 3 critique rounds closed
  (P0=0, P1=0 final). `android.app.admin` has `READY_WITH_GAPS` V7.0 content. The other 48
  V7.0 dirs are workspace skeletons (no method_matrix, no requirements doc yet).
- Five packages dominate the corpus by row count and LOC: `android.view` (2 709), `android.media`
  (2 584), `android.provider` (1 884), `android.widget` (1 974), `android.app` (1 724),
  `android.telephony` (1 567). Together they are >50% of the matrix volume.
- Three packages have suspiciously small matrices (<30 rows): `android.hardware.display` (25),
  `android.hardware.input` (9), `android.media.projection` (8). These are likely under-extracted
  from AOSP; the per-package V5.0 doc claims AOSP `current.txt` as source but coverage looks low.
  **Data quality issue, not a blocker.**

## Westlake Relevance Matrix

Scoring:
- `3` = Phase 1 in-process apps currently crash/won't render without it (used by both AOSP shim
  ladder and `{Noice,Mcd}InProcessActivity.kt`).
- `2` = imported by `Noice/McdInProcessActivity` or in `westlake-host` manifest permissions; feature
  degraded if missing.
- `1` = referenced indirectly (in McD manifest queries / `uses-feature`, or noice fragment libs).
- `0` = neither app uses.

Evidence sources:
- noice imports in `/home/dspfac/android-to-openharmony-migration/westlake-host-gradle/app/src/main/java/com/westlake/host/NoiceInProcessActivity.kt`
  (android.app.{Activity,Application,Instrumentation}, content.{Context,Intent,pm.{ActivityInfo,ApplicationInfo}}, os.Bundle, util.Log, view.{View,ViewGroup})
- McD imports in `McdInProcessActivity.kt` (adds: app.Service, content.res.{AssetManager,Resources},
  graphics.{Bitmap,Canvas,Color,Paint,Typeface,drawable.GradientDrawable}, os.IBinder, util.Base64,
  view.{ContextThemeWrapper,Gravity,LayoutInflater,SurfaceHolder,SurfaceView}, widget.*)
- McD AndroidManifest: `/home/dspfac/android-to-openharmony-migration/artifacts/real-mcd/apktool_decoded/AndroidManifest.xml`
  (declares perms ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE, CAMERA, READ_PHONE_STATE,
   USE_BIOMETRIC, USE_FINGERPRINT, READ_MEDIA_AUDIO, POST_NOTIFICATIONS, VIBRATE, WAKE_LOCK,
   FOREGROUND_SERVICE{,_LOCATION}; declares `McDMarketApplication` Application; queries com.android.vending, paypal, venmo, facebook, gmaps)
- westlake-host manifest: lists app-level perms (location absent — noice/McD location is permission-only)
- Handoff doc Phase-1 5-pillar pattern: hidden-API bypass + LoadedApk dir redirect + safe-context
  bind stub + LocaleManager binder hook + lifecycle drive to Resumed.

| # | Package | noice | McD | Σ | Rationale |
|---:|---|---:|---:|---:|---|
|  1 | android.app | **3** | **3** | **6** | Activity/Application/Instrumentation lifecycle — Phase 1 directly invokes `Activity.performCreate`/`performResume` and constructs `Instrumentation`; ApplicationInfo + LoadedApk redirect; both InProcess activities subclass `Activity`. |
|  2 | android.content | **3** | **3** | **6** | Context/Intent/PackageManager — every cross-package launch, Intent rewriting, asset/resource lookup. Phase 1 wraps `WestlakeContextImpl`; CR56/CR59 root cause was `getApplicationContext()` null. |
|  3 | android.os | **3** | **3** | **6** | Bundle, IBinder, Handler/Looper, Build, MessageQueue — touched on every lifecycle event. McD uses Bundle and IBinder. LocaleManager binder hook is in os/system_server. |
|  4 | android.view | **3** | **3** | **6** | View/ViewGroup/Window/SurfaceView/LayoutInflater — substrate-shadowed `WestlakeActivity/Window+PhoneWindow/DecorView`. McD uses SurfaceView for canvas blit. noice inflates `main_activity.xml`. |
|  5 | android.widget | **2** | **3** | **5** | TextView/LinearLayout/ScrollView — McD's Wi-Fry offline screen built from `widget.*` programmatically. noice tabs use widgets transitively via Compose interop. |
|  6 | android.graphics | 2 | **3** | **5** | Bitmap/Canvas/Color/Paint/Typeface/Drawable — McD InProcess uses all of these directly for brand drawing. noice icon assets. |
|  7 | android.content.pm | 2 | **3** | **5** | PackageManager queries — McD declares 9 `<queries>` for uber, paypal, venmo, gmaps; ApplicationInfo plumbing for both apps. |
|  8 | android.content.res | 2 | **3** | **5** | AssetManager/Resources — Phase 1 builds a thin shadow `Resources` over the APK; McD imports `AssetManager` for brand assets. CR41 LoadedApk dir redirect is here. |
|  9 | android.util | 2 | 2 | 4 | Log/Base64/DisplayMetrics — both InProcess files import Log/Base64 directly; touched in every error path. |
| 10 | android.text | 2 | 2 | 4 | TextUtils/Spannable used transitively by widget.TextView + Material/Compose; both apps render Latin strings. |
| 11 | android.app.job | 1 | 2 | 3 | McD's foreground-services declarations + JobScheduler for promo refresh. noice has WorkManager. |
| 12 | android.media | 1 | 2 | 3 | McD uses notification sounds + RingtoneManager. **noice = audio player but uses ExoPlayer / OpenSL ES directly**, bypassing most of `android.media`; M5 substrate hosts via AAudio daemon. |
| 13 | android.media.session | 1 | 1 | 2 | noice posts MediaSession for lockscreen controls; McD shows notifications with media style. |
| 14 | android.provider | 1 | 2 | 3 | McD reads Settings.System for analytics + MediaStore for image picking; noice reads Settings.Global. |
| 15 | android.location | 0 | **3** | **3** | McD declares ACCESS_FINE_LOCATION + FOREGROUND_SERVICE_LOCATION; store-finder is core flow. noice does not use location. |
| 16 | android.net | 1 | 2 | 3 | ConnectivityManager — both apps check network; McD's network_security_config is here. |
| 17 | android.net.wifi | 0 | 2 | 2 | McD declares ACCESS_WIFI_STATE + CHANGE_WIFI_STATE; uses Wi-Fi for store identification. noice = offline-capable. |
| 18 | android.hardware.camera2 | 0 | **3** | **3** | McD declares CAMERA perm + 3 camera features (ar, autofocus); used for QR scanning (McAfricka, in-store loyalty). noice has no camera. |
| 19 | android.security | 0 | 2 | 2 | McD uses BiometricPrompt for transaction auth (USE_BIOMETRIC + USE_FINGERPRINT perms). |
| 20 | android.security.keystore | 0 | 2 | 2 | Same — biometric key gen + token wrapping. |
| 21 | android.telephony | 0 | 2 | 2 | McD declares READ_PHONE_STATE — historically used for IMEI-based device fingerprint, now permission-only. |
| 22 | android.webkit | 0 | 2 | 2 | McD: in-app browser for terms / partner-login; android.p2pmobile (PayPal) flows. noice has settings WebView. |
| 23 | android.app.admin | 0 | 0 | 0 | Neither app is a device admin. |
| 24 | android.app.usage | 0 | 0 | 0 | Internal analytics packages don't call this directly. |
| 25 | android.accessibilityservice | 0 | 0 | 0 | Neither app is an accessibility service. |
| 26 | android.accounts | 0 | 1 | 1 | McD has Google Sign-In which transitively uses AccountManager. |
| 27 | android.animation | 1 | 1 | 2 | Activity/fragment transitions inflate ValueAnimator; both apps use mild motion. |
| 28 | android.transition | 1 | 1 | 2 | Material activity transitions. |
| 29 | android.database | 0 | 1 | 1 | McD uses Room which is on top of SQLite. |
| 30 | android.database.sqlite | 0 | 1 | 1 | Same — Room transitively. |
| 31 | android.text.format | 1 | 1 | 2 | DateFormat / Formatter for time display. |
| 32 | android.text.method | 0 | 1 | 1 | Password / link-movement methods in McD's sign-in form. |
| 33 | android.text.style | 0 | 1 | 1 | McD T&C link spans. |
| 34 | android.bluetooth | 0 | 0 | 0 | Neither app uses. |
| 35 | android.bluetooth.le | 0 | 0 | 0 | Neither app uses. |
| 36 | android.hardware | 0 | 1 | 1 | SensorManager: McD ar/autofocus check. |
| 37 | android.hardware.display | 0 | 0 | 0 | Neither app uses. |
| 38 | android.hardware.input | 0 | 0 | 0 | Neither app uses. |
| 39 | android.hardware.usb | 0 | 0 | 0 | Neither app uses. |
| 40 | android.media.audiofx | 1 | 0 | 1 | noice: audio FX for ambient sound shaping (transitively, via ExoPlayer). |
| 41 | android.media.midi | 0 | 0 | 0 | Neither app uses. |
| 42 | android.media.projection | 0 | 0 | 0 | Neither app uses. |
| 43 | android.net.wifi.aware | 0 | 0 | 0 | Neither app uses. |
| 44 | android.net.wifi.p2p | 0 | 0 | 0 | Neither app uses. |
| 45 | android.nfc | 0 | 0 | 0 | Neither app uses. |
| 46 | android.nfc.tech | 0 | 0 | 0 | Neither app uses. |
| 47 | android.print | 0 | 0 | 0 | Neither app uses. |
| 48 | android.speech | 0 | 0 | 0 | Neither app uses. |
| 49 | android.speech.tts | 0 | 0 | 0 | Neither app uses. |
| 50 | android.telecom | 0 | 0 | 0 | Neither app uses. |

**Coverage summary**: 22 of 50 packages have non-zero Westlake relevance; 8 packages have
relevance ≥4 (the Phase 1 hot core). 28 packages are currently 0-relevant — these can be
deprioritised for V3 W4 without losing noice/McD coverage.

## Top-10 Critical Packages for V3 W4

The W4 adapter customization (issue #629) needs concrete specs for shadow classes. The top-10 by
Σ-score:

### 1. `android.app` — Σ=6
- **Why critical**: All `WestlakeActivity / NoiceInProcessActivity / McdInProcessActivity` subclass
  `android.app.Activity`. CR41/CR56/CR59 fixes all touch `Application.mBase`, `Instrumentation.callActivityOnCreate`,
  `ActivityThread`-shaped lifecycle. The 1 724-row matrix is the largest by volume aside from
  view/media/provider/widget/telephony.
- **W4 must do**: enumerate every Activity / Application / Service / Instrumentation hook
  Phase 1 already shadows and route it (`OH_ADAPTER` for lifecycle, `JAVA_SYNTH` for
  Application.mBase/getApplicationContext, `OH_BINDER` for ActivityManager.* calls).
- **Docs**: `package-requirement-design-v5.0/android.app/{PACKAGE_REQUIREMENTS_V5.0.md,PACKAGE_DESIGN_DRAFT_V5.0.md,METHOD_MATRIX_V5.0.yaml}`
- **Open MISSING_FACTS**: V5.0 boilerplate (6 generic items); V7.0 workspace empty — needs Deep Research input.

### 2. `android.content` — Σ=6
- **Why critical**: Every Context/Intent/PackageManager interaction. The cross-package intent rewriter
  in `installIntentRewriter()` is at this layer. `WestlakeContextImpl.getApplicationContext()` null
  was CR56's root cause.
- **W4 must do**: spec the safe-context bind stub + Intent component-rewriting policy. Most rows route `OH_ADAPTER`.
- **Docs**: `package-requirement-design-v5.0/android.content/*`
- **Open MISSING_FACTS**: needs explicit `ContextImpl.attachBaseContext` + `bindIsolatedService` matrix.

### 3. `android.os` — Σ=6
- **Why critical**: Bundle marshalling, IBinder bridge, Handler/Looper integration with OHOS
  event-loop, Build constants for app branding. McD imports `IBinder` directly; noice posts on the
  main Looper from background.
- **W4 must do**: route Build.* constants `JAVA_SYNTH` with target values from D200; Bundle
  marshalling `OH_DIRECT`; Looper/Handler `OH_ADAPTER` to OHOS event handler.
- **Docs**: `package-requirement-design-v5.0/android.os/*` (967 rows)
- **Open MISSING_FACTS**: Looper/Handler ↔ OHOS HandlerThread mapping is `DISCOVERY_REQUIRED`.

### 4. `android.view` — Σ=6
- **Why critical**: **Largest matrix** in the corpus (2 709 rows). V2 substrate shadows
  `Window/PhoneWindow/DecorView/View/ViewGroup/WindowManagerImpl` via `framework_duplicates.txt`.
  This is the rendering and input dispatch core.
- **W4 must do**: catalog every shadow class and route ViewRootImpl-shaped behavior to OHOS
  Surface/ArkUI primitives. SurfaceView import in `McdInProcessActivity` makes Surface routing
  critical.
- **Docs**: `package-requirement-design-v5.0/android.view/*`
- **Open MISSING_FACTS**: ViewRootImpl / Choreographer / Surface lifecycle is the single biggest
  spec gap.

### 5. `android.widget` — Σ=5
- **Why critical**: McD's Wi-Fry offline screen builds programmatic LinearLayout/TextView/ScrollView.
  noice uses Material widgets via Compose interop. 1 974 matrix rows, 180 risk-A.
- **W4 must do**: route the leaf widgets `JAVA_SYNTH` (pure-Java RemoteViews-free implementation
  is fine) over our shadow View hierarchy.
- **Docs**: `package-requirement-design-v5.0/android.widget/*`
- **Open MISSING_FACTS**: AdapterView / RemoteViews behavior with OH binder ICs.

### 6. `android.graphics` — Σ=5
- **Why critical**: McD InProcess directly imports Bitmap/Canvas/Color/Paint/Typeface/Drawable for
  branded offline screen. Both apps load drawable resources. CR-W/X/Y/Z chain (the canonical
  "additive shim" failure) was at this layer with SoftwareCanvas.
- **W4 must do**: route most C-side Skia equivalents to OH's existing Skia (`libskia_canvaskit.z.so`);
  Bitmap/Canvas should `OH_DIRECT` onto OH Skia.
- **Docs**: `package-requirement-design-v5.0/android.graphics/*` (1 100 rows)
- **Open MISSING_FACTS**: HardwareBitmap + HardwareBuffer interop with OH GPU pipeline.

### 7. `android.content.pm` — Σ=5
- **Why critical**: McD declares 9 `<queries>` packages — `PackageManager.queryIntentActivities` must
  return realistic results to avoid crashes during deep-link routing. ApplicationInfo plumbing
  blocks getApplicationContext (CR56 reproducer chain).
- **W4 must do**: stub PackageManager to (a) return self info, (b) return empty for queries that
  would launch external apps (uber, paypal, venmo, gmaps), (c) `UNSUPPORTED_EXPLICIT_FAILURE` for
  ResolveInfo where the answer would crash the app.
- **Docs**: `package-requirement-design-v5.0/android.content.pm/*` (836 rows)
- **Open MISSING_FACTS**: which intent filters McD/noice actually check via reflection (need APK probe).

### 8. `android.content.res` — Σ=5
- **Why critical**: AssetManager + Resources — Phase 1 builds thin shadow `Resources`, and the
  LoadedApk dir redirect (5-pillar pattern) lives here. McD imports AssetManager directly.
- **W4 must do**: route Resources getters `OH_ADAPTER` onto OH ResourceManager where possible,
  fall back to `JAVA_SYNTH` reading from APK assets.
- **Docs**: `package-requirement-design-v5.0/android.content.res/*` (215 rows)
- **Open MISSING_FACTS**: Configuration / locale / density delta handling — relates to the
  LocaleManager binder hook (5-pillar).

### 9. `android.util` — Σ=4
- **Why critical**: Log/Base64/DisplayMetrics/SparseArray — touched on every error path. `android.util.Log`
  is one of the very first calls a starting app makes.
- **W4 must do**: trivially `OH_ADAPTER` Log → hilog; the rest is `JAVA_SYNTH` (pure-Java collections).
- **Docs**: `package-requirement-design-v5.0/android.util/*` (536 rows)
- **Open MISSING_FACTS**: minor — DisplayMetrics density math should match D200 actual values.

### 10. `android.text` — Σ=4
- **Why critical**: TextUtils + Spannable underlie every TextView render. McD's offline screen
  uses spans for "McD" branding; noice has localized strings.
- **W4 must do**: mostly `JAVA_SYNTH` (pure-Java text); some Spannable callbacks may need
  `OH_ADAPTER` to OH text rendering.
- **Docs**: `package-requirement-design-v5.0/android.text/*` (414 rows)
- **Open MISSING_FACTS**: ICU / locale-data divergence between AOSP V15 ICU and OH ICU.

**Honourable mentions just below the cutoff**: `android.location` (Σ=3, but McD-critical hard
blocker if user opens store finder), `android.hardware.camera2` (Σ=3, McD QR scanner).

## V3 Workstream Mapping

This is read-only; recommended changes are flagged but not applied to V3-WORKSTREAMS.md.

| V3 W# | Workstream | Use of 03-Requirement corpus |
|---|---|---|
| W4 | Adapter customization | Consume the top-10 above as the **adapter scope diff catalog**. Every `route_decision != OH_DIRECT` row in their METHOD_MATRIX is a candidate shadow-class entry. Most rows still `DISCOVERY_REQUIRED` so W4 must close routes itself or kick back to the requirement team. |
| W5 | Mock APK validation | Should validate against packages where Σ-score=0 too (basic Activity boot path) — i.e. the W4 top-10 are *necessary but insufficient*. |
| W6 | noice on V3 | Consume noice-3 rows: `android.{app, content, os, view}` (Σnoice=3 each). Driving factor is fragment lifecycle (`androidx.fragment` is not in the 50 — note as gap). |
| W7 | McD on V3 | Consume McD-3 rows: `android.{app, content, os, view, widget, graphics, content.pm, location, hardware.camera2}` (Σmcd=3 each). |
| W11 | V2→V3 carryforward audit | Top-10 maps closely onto what V2 substrate already shadows; the 6 packages in V2's `framework_duplicates.txt` should round-trip into the W4 catalog without surprise. |

**Cross-team coordination items**:
1. The 03-Requirement V5.0 corpus is gated `IMPLEMENTATION_BLOCKED`; V3 W4 cannot consume it as
   a finished spec. Either (a) V3 closes routes itself locally and pushes evidence back upstream,
   or (b) we ask the requirement team to fast-track the top-10 via V7.0 Deep Research (Wi-Fi-style)
   ahead of the general queue.
2. `androidx.*` packages (fragment, lifecycle, room) are entirely absent from the 50. Both apps
   depend on AndroidX. Either the corpus needs to grow, or Phase 1 absorbs them as Java-side
   shadow without OH-route (since AndroidX is pure-Java and ships in the APK).

## Wi-Fi V7.0 Pilot Highlights

`android.net.wifi/versions/V7.0/` is the only fully-realised V7.0 package and serves as the
forward-looking template for everything else.

### Headline numbers
- `PACKAGE_REQUIREMENTS_V7.0.md`: 489 LOC, fully Chinese-language, human-facing, organized around
  Wi-Fi user-visible capabilities (state, scan, connect, saved-net, enterprise, hotspot, P2P,
  callback, perms).
- `PACKAGE_DESIGN_V7.0.md`: 1 014 LOC, machine-facing.
- `method_matrix.yaml`: 101 rows (vs V5.0's 251 rows for same package — V7 deliberately
  cherry-picks the high-signal API surface). Routes: `OH_ADAPTER`=34, `DISCOVERY_REQUIRED`=28,
  `JAVA_SYNTH`=15, `HUMAN_DECISION_REQUIRED`=13, `UNSUPPORTED_EXPLICIT_FAILURE`=10, `OH_DIRECT`=1.
- `data_contract.yaml`: 507 LOC, per-structure field-level mapping with per-field `strategy` +
  `evidence_status` + `risk`. Example: `ScanResult.BSSID → wifiManager.WifiScanInfo.bssid` with
  `direct_string_with_privacy_provenance` strategy.
- `source_evidence.yaml`: 479 LOC, evidence ledger with `confidence: SOURCE_CONFIRMED` /
  `D200_CONFIRMED` / `D200_BLOCKED` markers tied to specific AOSP / OH header file paths.
- `missing_facts.md`: 60 LOC, real per-package P0/P1 ledger (not the 6-row boilerplate).
- 3 critique rounds documented: P0/P1 starting 6/4 → 3/3 → 2/2 → final 0/0.
- `ORACLE_PILOT_SUBMISSION.md` packages everything for external review.

### V7.0 schema lessons applicable to the other 49 packages
1. **Methods get richer route vocabulary** (6 routes vs 5 in V5.0) — `JAVA_SYNTH` for pure-Java
   reproduction, `UNSUPPORTED_EXPLICIT_FAILURE` for explicit OS-level gaps, `HUMAN_DECISION_REQUIRED`
   for policy escalation. Critically there is no `STUB` — V7 forces a real decision per row.
2. **Data alignment now has `strategy` enum**, not just AOSP↔OH name mapping
   (`direct_string` / `enum_to_capabilities_string` / `direct_dbm` / `unit_normalization` /
   `int_enum_map` / `eid_content_array_map` etc.). This is what the V5 `DATA_ALIGNMENT.yaml`
   matrices need to evolve into.
3. **Source evidence is path-anchored** to specific AOSP files and line ranges, e.g.
   `packages/modules/Wifi/service/java/com/android/server/wifi/ScanRequestProxy.java` —
   `claim_supported: Scan behavior cannot be inferred from current.txt alone`.
4. **Critique rounds are first-class**, with a closure trail (`CRITIQUE_ROUND_1.md` →
   `_ROUND_2.md` → `_ROUND_3.md`) and a final independent review verdict in `ORACLE_PILOT_SUBMISSION.md`.
   V5.0 has only a single auto-generated critique with no closure mechanism.
5. **Profile-driven extra sections**: V7A.0 (Hardware/Driver/Service) adds Concurrency Contract,
   Permission & Privacy Contract, Driver/Firmware Provenance, Power & Performance — none of
   which the V5.0 schema requires. Other profiles (V7B/C/D/E/F) add their own extras.
6. **D200 runtime gaps are not blockers for document closure** — they are tracked as `D200_BLOCKED`
   evidence-status alongside the `SOURCE_CONFIRMED` rows; the design can land as
   `ORACLE_PILOT_READY_D200_RUNTIME_BLOCKED`.

### Wi-Fi-specific findings worth surfacing to W4
- WifiManager.isWifiEnabled → wifiManager.isWifiActive is one of only **two** `OH_DIRECT` rows in
  the whole pilot. Almost everything else needs adapter work.
- `startLocalOnlyHotspot`, Passpoint, WPS, DPP, WEP/WAPI all are `UNSUPPORTED_EXPLICIT_FAILURE`
  on D200 — relevant for McD if it ever calls AP-side Wi-Fi.
- D200 Wi-Fi service is stopped + wpa_cli blocked → even after a perfect adapter, runtime
  validation is currently impossible. Action: D200-side service revival should be a separate
  workstream (not in the requirement team's scope).

## Open Question for Cross-team Coordination

> **Will the V7.0 Deep Research treatment be applied to the top-10 Westlake-relevant packages
> next, or stay in package-number order?** If the queue is FIFO, packages 4 (`android.app`),
> 10 (`android.content`), 33 (`android.os`), 48 (`android.view`) are critical and will be reached
> at different rates — V3 W4 needs to know whether to (a) wait for V7.0 closure on the top-10
> (estimated weeks given the wifi pilot took 3 critique rounds), (b) close V5.0 routes locally
> using AOSP+OH source already in our tree, or (c) request a "westlake-priority slice" carve-out
> of the V7.0 pipeline.

Secondary coordination item: **`androidx.*` is entirely absent from the 50**. Both target apps
depend on `androidx.fragment`, `androidx.lifecycle`, `androidx.compose.*`, `androidx.room`. We
should clarify whether AndroidX is in scope for the requirement team or whether it stays in
Westlake's Java-side shadow only.

## File-path reference (for downstream agents)

- Per-package V5.0 root: `artifacts/v3-w3-requirements/package-requirement-design-v5.0/<android.pkg>/`
- Per-package V7.0 root (Wi-Fi pilot + 49 workspaces): `artifacts/v3-w3-requirements/package-requirement-design/<android.pkg>/versions/V7.0/`
- Planner doc: `artifacts/v3-w3-requirements/PACKAGE_PLANNER_CURRENT.md`
- V7.0 Core template: `artifacts/v3-w3-requirements/templates/WestLake_Package_Deep_Research_Template_V7.0_Core.md`
- V7.0 domain profiles: `artifacts/v3-w3-requirements/templates/WestLake_Profile_V7{A..F}.0_*.md`
- V5.0 batch index: `artifacts/v3-w3-requirements/package-requirement-design-v5.0/{SUMMARY,PACKAGE_INVENTORY}_V5.0.yaml`
- Wi-Fi pilot Oracle packet: `artifacts/v3-w3-requirements/package-requirement-design/android.net.wifi/versions/V7.0/ORACLE_PILOT_SUBMISSION.md`
- V3 workstream master: `docs/engine/V3-WORKSTREAMS.md` (W4 = #629, W6 = #631, W7 = #632)
- Phase 1 in-process code (for Σ-score evidence): `westlake-host-gradle/app/src/main/java/com/westlake/host/{Noice,Mcd}InProcessActivity.kt`
- McD decoded manifest: `artifacts/real-mcd/apktool_decoded/AndroidManifest.xml`
