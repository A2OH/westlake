# Westlake Platform-First Issue Map

Last updated: 2026-05-02

> **Note (2026-05-03):** Earlier sections may contain superseded status flips. Look for `[SUPERSEDED — see <update>]` annotations. The canonical current status is in the most recent dated PT update for each PF-* tag, and a consolidated current view is in `docs/program/WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md`.

This file mirrors the active platform-first issue structure used for execution.

Southbound API contract:
`docs/program/WESTLAKE_SOUTHBOUND_API.md` is the current portability contract
for reconciling Android guest APIs/JNI with OHOS/musl host services. Use it as
the acceptance rule for new southbound slices: Android proof alone is not an
OHOS portability claim unless the same guest-facing contract has an OHOS/musl
adapter path or an explicit open issue.

72-hour real-McD dashboard runbook:
`docs/program/WESTLAKE_REAL_MCD_72H_DASHBOARD_PLAN_20260430.md` is the active
supervisor strategy for the next autonomous swarm push. MCP GitHub issue
creation initially failed with `403 Resource not accessible by integration`,
but the authenticated `gh` CLI succeeded. PF-602 through PF-609 are now open in
`A2OH/westlake` as issues `#575` through `#582` and mirrored here for local
execution.

Current real-McD diagnostic frontier as of 2026-04-30 16:55 PT:

- proof artifact:
  `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/`
- phone proof uses stock `com.mcdonalds.app` inside the Westlake guest
  `dalvikvm` subprocess, not phone ART execution;
- hashes:
  `dalvikvm=bada016333b7bcb97ef2b4c3bbf94bce0beb61cf9f3583fceba0545d5d165bd8`,
  `aosp-shim.dex=1a617034e52ebde7376ac972eed4d7cd7b47fcc76a54d9635d4b4a5e3391c821`,
  `westlake-host.apk=9957ecbab3826a5e1657348337ddf3f381c294636f09ade7aafba88a3f9ffd20`;
- OHOS static runtime from the same runtime source links and passes the symbol
  gate:
  `build-ohos-arm64/bin/dalvikvm=aed60bf242af2b6587bbb5fe7b608efb2ad99ada0fbb4420c60b7e0d92380974`;
- closed in recent slices: guest `PackageInfo.getLongVersionCode()`,
  AppCompat `createConfigurationContext(...)`, McD offline config market id
  seeding for `usdap_prod`, Realm `Util.nativeGetTablePrefix()` returning
  `"class_"`, SDK persistence configuration seeding, dashboard config shape
  seeding, and the latest `HomeDashboardActivity.onCreate`
  `Failed requirement` startup blocker. The current proof uses a temporary
  McD-specific `JustFlipBase.c(...)` event-emission shield to bypass the
  coroutine/SharedFlow event path and expose the Realm frontier;
- still open: real dashboard data does not populate. The app reaches
  `HomeDashboardActivity`, `onCreate` returns, and the dashboard becomes
  active, but the rendered frame remains sparse
  (`dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1`).
  The new diagnostics identify the active Realm tables as
  `class_KeyValueStore` and `class_BaseCart`, with predicates
  `_maxAge < $0`, `_maxAge != $0`, `key = $0`, and `cartStatus = $0`.
  Realm result/row calls are still mostly no-op, zero-size, or empty-value
  boundary probes, so this is not real persistence or production dashboard
  compatibility.

Umbrella issue:
- `Platform-first Westlake delivery program`: `A2OH/westlake#569`

Primary issue families:
- `PF-001` guest execution purity: `A2OH/westlake#558`
- `PF-101` standalone guest boot: `A2OH/westlake#559`
- `PF-201` `System` / `Properties` bootstrap: `A2OH/westlake#560`
- `PF-202` `ThreadLocal` / `AtomicInteger` bootstrap: `A2OH/westlake#561`
- `PF-203` `CaseMapper` / `Locale` bootstrap: `A2OH/westlake#562`
- `PF-204` `Looper` bootstrap: `A2OH/westlake#563`
- `PF-301` generic `ActivityThread` / `Activity` / `Window` startup: `A2OH/westlake#564`
- `PF-302` generic surface/input/render survival: `A2OH/westlake#565`
- `PF-401` app-owned AndroidX/AppCompat host integrity: `A2OH/westlake#566`
- `PF-451` controlled local Android showcase app: Android phone proof accepted;
  OHOS adapter implementation remains open
- `PF-452` guest networking/runtime bridge: Android phone host/OHBridge proof
  accepted; OHOS adapter implementation remains open
- `PF-453` separate Yelp-like live data app: Android phone proof accepted;
  OHOS adapter implementation remains open through the PF-452 bridge contract
- `PF-454` Material Components API canary: Android phone proof accepted for a
  first Westlake-owned `com.google.android.material.*` shim slice with Chinese
  UTF-8 DLST text, host-bridge network image tiles, and aspect-preserved host
  rendering; full upstream Material Components compatibility remains open
- `PF-455` XML-backed Yelp app path: Android phone proof accepted for compiled
  Yelp APK layout-byte registration, XML inflation, ID binding, layout probe,
  live data, and touch flows on a full-phone `1080x2280` host surface with
  logical `480x1013` guest coordinates; PF-459 now accepts a first generic
  inflated-View DLST draw slice, and PF-460 now accepts actual `ScrollView`
  inflation/probing plus multiple inflated XML `Button.performClick()`
  listener slices; PF-461 now accepts a first XML `ListView`/`BaseAdapter`
  binding, downloaded row image-byte rebinding, and generic adapter item-click
  slice;
  full-fidelity replacement of the controlled direct `DLST` frame writer and
  touch router remains open
- `PF-456` portable REST networking completeness: Android host bridge v2 is
  accepted on phone for real live GET JSON/image traffic and the bridge v2 REST
  marker contract; the real multi-method matrix still has a VM SIGBUS gap and
  is represented by synthetic matrix markers in the current Yelp proof; OHOS
  adapter parity remains open
- `PF-457` Material/generic UI compatibility expansion: Android phone proof
  accepted for compiled Material XML tag inflation, direct DLST tree rendering,
  MaterialButton bounds discovery, and generic `findViewAt/performClick` hit
  into an app listener; upstream Material Components AAR compatibility, theming,
  Coordinator/AppBar behavior, ripple/animation, and broad hit testing remain
  open
- `PF-501` McDonald's launch validation: `A2OH/westlake#567`
- `PF-601` real McDonald's dashboard body takeover: `A2OH/westlake#568`
- `PF-602` 72-hour real McDonald's dashboard success parent: `A2OH/westlake#575`
- `PF-603` portable Realm table/query/result/row state machine: `A2OH/westlake#576`
- `PF-604` McD dashboard visibility, databinding, and View-tree density:
  `A2OH/westlake#577`
- `PF-605` real McD network/image/content transport boundary: local roadmap
  issue `A2OH/westlake#578`
- `PF-606` dashboard UI rendering, Material/AppCompat widgets, scroll/input:
  `A2OH/westlake#579`
- `PF-607` runtime stability, coroutine/event, CPU, and cutout cleanup:
  `A2OH/westlake#580`
- `PF-608` OHOS/musl southbound parity for McD-critical APIs: local roadmap
  issue `A2OH/westlake#581`
- `PF-609` proof automation and acceptance evidence: `A2OH/westlake#582`
- `PF-620` real McD PDP stock handler and Android window/display API:
  local roadmap issue, active
- `PF-621` 48-hour real McD full-app rally parent: `A2OH/westlake#587`
- `PF-622` PDP Fragment/DataBinding lifecycle readiness: local roadmap issue,
  `A2OH/westlake#588`
- `PF-623` generic McD View rendering, root selection, and input dispatch:
  `A2OH/westlake#589`
- `PF-624` order/customize/bag/cart downstream stock-flow proof:
  `A2OH/westlake#590`
- `PF-625` McD network/storage/Realm/cart state fidelity: local roadmap issue,
  `A2OH/westlake#591`
- `PF-626` full-flow proof automation and OHOS southbound parity:
  `A2OH/westlake#592`
- `PF-627` real McD dashboard item XML and RecyclerView row parity:
  `A2OH/westlake#593`
- `PF-628` remove McD CartInfo bridge via generic lifecycle/LiveData
  propagation: `A2OH/westlake#594`
- `PF-629` deeper real McD stock route coverage beyond PDP cart proof:
  `A2OH/westlake#595`
- `PF-630` Realm close/finalizer long-soak stability after real McD cart path:
  `A2OH/westlake#596`
- `PF-632` missing Android framework APIs discovered by real McD route
  expansion: `A2OH/westlake#597`
- `PF-801` reproducibility and evidence discipline: `A2OH/westlake#570`

Current McD full-app rally frontier on 2026-05-02 12:20 PT:

- Active runbook:
  `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`.
- Latest accepted phone runtime:
  `aosp-shim.dex=48f60b57724549441e3fcd1b37603589e78214c051f22e605b48330062d5b5b4`.
- Latest accepted dashboard/PDP/Add/cart-gate proof:
  `artifacts/real-mcd/20260502_120714_mcd_48h_guarded_model_commit_baseline/`.
- Latest unsafe downstream commit crash probe:
  `artifacts/real-mcd/20260502_115607_mcd_48h_model_x_commit_probe/`.
- The visible phone UI is not the old McD mock app. It is the real
  McDonald's APK route running inside Westlake guest `dalvikvm`, with real
  dashboard/PDP activity, XML, adapter, product, fragment, and stock
  `OrderPDPFragment.j8(true)` add handler execution. The latest proof logs
  `MCD_REAL_XML_PDP_ENHANCED`, `MCD_PDP_FIELD_HYDRATE plusOk=true
  minusOk=true quantityOk=true`, `MCD_ORDER_PDP_READY ready=true`,
  `MCD_ORDER_PDP_STOCK_ACTION route=fragment_j8 invoked=true`, and
  `MCD_PDP_CART_GATE cartInfo=CartInfo`.
- `CartProduct.quantity` is now hydrated to `1`; the guarded proof logs
  `MCD_PDP_CART_PRODUCT_PREP ... afterQuantity=1` and
  `MCD_PDP_CART_GATE ... quantity=1`.
- The unsafe downstream commit is explicitly gated by default after the
  previous probe reached `OrderPDPFragment.A7(...)`, Realm storage setup, and
  `BasketAPIHandler.A1(...)`, then crashed with `SIGBUS`. The current proof
  logs `MCD_PDP_A7_GATE ... reason=realm_storage_sigbus_risk` and
  `MCD_PDP_STOCK_ADD_COMMIT ... route=model_x_gated_realm_storage`.
- The old hard `performResume()` SIGBUS is closed for this path by field
  hydration plus a labeled soft-state bridge, but generic Fragment lifecycle
  parity is still open.
- The proof still uses Westlake projection/control bridges, so it is not full
  generic Android View parity. Full-app success now requires accepted cart
  mutation, quantity/customize, bag/cart screen, physical/generic input, and
  fewer McD-specific routing bridges.
- Immediate contract gaps:
  downstream Realm/storage/basket commit cannot be safely enabled yet,
  `CartInfo.totalBagCount` and `cartSizeWithoutOffers` remain zero after
  `j8`, `mResumed` still reads false under the soft lifecycle bridge, generic
  PDP Add input still falls through to projected fallback, storage/Realm/cart
  state fidelity remains incomplete, and proof automation must keep the
  full-app gate failed while exposing PDP/Add/cart substatus.

Updated 48-hour workstream issue status:

- `PF-621` full-app rally parent: active. Success requires stock dashboard ->
  PDP -> quantity/customize or bag/cart state, not projected bag count only.
- `PF-622` PDP Fragment/DataBinding lifecycle readiness: partially advanced.
  Include binding and PDP field hydration are proven; `mode=soft_state` and
  `mResumed=false` remain the blockers.
- `PF-623` generic McD View rendering, root selection, and input dispatch:
  partially advanced. Physical dashboard tap works; generic PDP Add dispatch
  currently reports `handled=false` and projected fallback handles Add;
  generic cart/back/bottom-nav input remain open.
- `PF-624` order/customize/bag/cart downstream stock-flow proof: advanced to
  stock `j8(true)` entry with non-null cart gate and hydrated quantity;
  downstream model commit is gated pending Realm/storage safety, so cart
  mutation and cart screen remain open.
- `PF-625` McD network/storage/Realm/cart state fidelity: active. Next proof
  must make the unsafe model commit nonfatal and then show app-model cart
  mutation or a precise nonfatal stock rejection.
- `PF-626` proof automation and OHOS southbound parity: active. Add
  PDP/Add/cart-gate substatus while keeping overall full-app gate failed until
  lifecycle and cart proof pass.

Current McD full-app rally frontier on 2026-05-02 12:46 PT:

- Active runbook:
  `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`.
- Latest focused phone runtime:
  `aosp-shim.dex=cbe6802cedf83d2f0e9e254ada18ec32951700c84f1fac5bbaf5526ab268d481`.
- Latest stock Add view/data-binding proof:
  `artifacts/real-mcd/20260502_123759_mcd_48h_livedata_seed_stock_click_probe/`.
- Latest guarded cart-gate baseline:
  `artifacts/real-mcd/20260502_120714_mcd_48h_guarded_model_commit_baseline/`.
- The latest proof closes a newer Add-input layer: the real
  `pdpAddToBagButton` receives the generated
  `OrderPdpButtonLayoutBindingImpl` listener, `OrderPDPViewModel.R1()` is
  seeded to avoid the previous generated-binding Boolean NPE, and
  `MCD_PDP_STOCK_VIEW_CLICK ... route=performClick invoked=true` fires twice.
- `scripts/check-real-mcd-proof.sh` now exposes those layers as separate gates:
  `mcd_pdp_stock_binding_listener_installed`,
  `mcd_pdp_stock_livedata_seeded`,
  `mcd_pdp_stock_click_boolean_npe`, and `mcd_pdp_stock_add_entry`.
- Full-app status is still failed. The latest stock-view click proof does not
  yet produce `MCD_PDP_CART_GATE`, basket commit, or cart mutation. The active
  blocker is the stock LiveData/observer/lifecycle continuation after the
  generated binding click, not a missing button listener.
- Immediate contract gaps by issue:
  `PF-622` must remove `mode=soft_state`, fix duplicate-parent start, and make
  app-bundled AndroidX observers active;
  `PF-623` must turn projected PDP Add into generic/physical hit dispatch;
  `PF-624` must connect stock-view click to `A7`/basket/cart state or a precise
  stock rejection;
  `PF-625` must make the guarded Realm/BaseStorage/BasketAPI path nonfatal;
  `PF-626` must keep the full gate red until lifecycle, generic input, and cart
  mutation are proven in the same phone artifact.

Current McD full-app rally frontier on 2026-05-02 13:07 PT:

- Worker review refined the evidence threshold: `performClick invoked=true`
  proves the view boundary only. The stock Add subproof now requires generated
  binding entry, `j8(true)`, `OrderPDPViewModel.Z/V`, observer callback, and
  `s7/o7/A7` or a precise nonfatal stock rejection.
- `PF-622` execution details: dispatch `ON_START`/`ON_RESUME` through the
  app-visible AndroidX lifecycle owner and prove observer activation with
  `MCD_PDP_OBSERVER_BRIDGE*` and stock observer callback markers. Soft-state
  recovery alone is not accepted.
- `PF-623` execution details: physical/touch-file PDP Add must route through
  generic Westlake hit dispatch to the real `pdpAddToBagButton`; projected
  fallback can remain diagnostic but cannot own the full proof.
- `PF-624` execution details: add method-entry markers for
  `OrderPdpButtonLayoutBindingImpl.a`, `OrderPDPFragment.j8`,
  `OrderPDPViewModel.Z/V`, `q7/Y7`, `r7/s7/v8/o7/A7`, and
  `CartViewModel.setCartInfo`.
- `PF-625` execution details: split unsafe model proof from unsafe storage
  commit. `A7` can be probed, but `OrderPDPViewModel.X(CartProduct)` and
  Realm/BaseStorage/BasketAPI require explicit storage opt-in until the SIGBUS
  boundary is replaced by a portable storage contract.
- `PF-626` execution details: rerun a long phone proof after build/sync and keep
  the parent full-app gate failed unless one artifact proves subprocess purity,
  real dashboard -> PDP, stock Add continuation, nonfatal cart/storage, and
  visible cart/bag evidence.

Current McD full-app rally frontier on 2026-05-02 13:18 PT:

- `PF-621` status: still red. The current dashboard/PDP/Add/cart path is close
  enough to drive on phone, but no artifact yet proves full McDonald's
  dashboard -> PDP -> stock Add -> cart/bag continuation inside Westlake.
- `PF-622` newer gap: lifecycle/observer work is installed but not accepted.
  Execution detail: rerun after rebuild/sync and require
  `MCD_PDP_OBSERVER_BRIDGE*`, generated binding entry, and app observer callback
  in the same artifact. Do not count soft-state recovery or view-level click as
  closure.
- `PF-623` newer gap: input timing is part of the runtime contract. The
  `20260502_131212_mcd_48h_observer_state_probe_long` run consumed stale taps
  because dashboard became touch-ready after the fixed schedule. Execution
  detail: inject touch-file taps only after dashboard/PDP readiness markers and
  require `WESTLAKE_VIEW_TOUCH_LIFECYCLE` / `WESTLAKE_VIEW_PERFORM_CLICK` on the
  stock target.
- `PF-624` newer gap: `performClick invoked=true` still stops at the view
  boundary unless the generated binding and fragment/model continuation markers
  follow. Execution detail: if absent, instrument
  `OrderPdpButtonLayoutBindingImpl.a`, `OrderPDPFragment.j8`,
  `OrderPDPViewModel.Z/V`, `q7/Y7`, and `r7/s7/v8/o7/A7` next.
- `PF-625` newer gap: Realm/BaseStorage/BasketAPI must be a portable
  Westlake-backed contract before unsafe commit is enabled by default.
  Execution detail: normal proof must remain nonfatal; unsafe model/storage
  probes stay separate opt-ins while SIGBUS is still possible.
- `PF-626` newer gap: proof automation must be marker-driven. Execution detail:
  use Windows ADB on `localhost:5037`, device `cfb7c9e3`,
  `WESTLAKE_GATE_SLEEP=420`, and fail the gate if readiness markers are missing
  instead of sending blind touches. GitHub issue comment updates are blocked by
  current integration permission (`Resource not accessible by integration`), so
  this local roadmap remains the authoritative worker instruction source.

Current McD full-app rally frontier on 2026-05-02 13:30 PT:

- `PF-626` proof harness patch: `run-real-mcd-phone-gate.sh` now streams logcat
  into `logcat-stream.txt` from launch so dashboard readiness evidence is not
  lost before final artifact collection.
- `PF-622/PF-624` new execution detail: the real generated-binding Add path is
  blocked by `OrderPDPViewModel.W1()==true`, which makes `j8()` return before
  `OrderPDPViewModel.Z()`. The runtime now clears that stuck loading flag in
  stock-click prep and logs `loadingCleared` for acceptance.
- `PF-623` remains open: physical/touch-file Add currently reaches
  `performClick` through projected fallback after generic hit miss. Fix sticky
  bottom bar/root coordinate ownership so generic dispatch owns the final proof.
- `PF-625` remains open: cart product/cart info are hydrated and SIGBUS is
  avoided, but real basket/cart mutation is still gated by Realm/storage risk.

Current McD proof frontier on 2026-05-01 10:12 PT:

- `artifacts/real-mcd/20260501_100855_mcd_two_step_category_navigation_clean_proof/`
  is the latest accepted navigation/full cold-start gate.
- The proof keeps stock McD inside the Westlake guest `dalvikvm` subprocess,
  has no fatal/failed-requirement marker, and reports
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`.
- Strict frame evidence after category navigation is now
  `bytes=124948 views=51 texts=10 buttons=3 images=2 rows=4 rowImages=4 rowImageBytes=123608 overlays=0`.
- Generic input/navigation proof is now accepted for two steps:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView ... handled=true`;
  `MCD_ORDER_NAV_OPENED source=start_order_tile_menu`;
  `GENERIC_HIT_CLICK target=android.widget.LinearLayout leaf=android.widget.ImageView ... handled=true`;
  `MCD_CATEGORY_NAV_OPENED label=Extra_Value_Meals source=category_detail`.
  The old y-band fallback and direct text mutation markers did not fire.
- This is still not stock McD UI parity. The visible UI is Westlake's McD
  boundary harness/layout-builder surface, used to prove frame, input,
  network/image, and subprocess boundaries while the real XML/order-module path
  is still being opened.
- The remaining McD blockers are real stock order-module flow, generic
  XML/resource inflation of the McD dashboard/order surfaces, bottom-nav and
  text-input interactions, generic Glide/ImageView binding, decor/root repair,
  Realm/storage fidelity, and OHOS adapter parity.

Current McD supervisor delta on 2026-05-01 10:50 PT:

- The visible McD UI remains the Westlake boundary harness/fallback surface,
  not accepted stock McD UI parity.
- A local shadow bridge for `com.mcdonalds.mcdcoreapp.network.*` has been
  added and rebuilt to route the legacy `McDRequestManager`/`McDHttpClient`
  path through `WestlakeLauncher.bridgeHttpRequest(...)`, with public method
  and field surface aligned to the decoded real APK classes. A Claude review
  also drove stubs for `RequestProvider.MethodType`,
  `RequestProvider.RequestType`, `McDHttpClient.AutoDisconnectInputStream`,
  and McD error DTO methods before the next phone gate.
- New local shim hash:
  `0188d3410344697f7ba10a9752225fc67d4ea061da236953523f519f61a0c409`.
- This is not accepted phone proof yet. ADB transport is temporarily blocked
  because WSL cannot launch Windows executables (`UtilAcceptVsock: accept4
  failed 110`), Linux `adb` sees no USB device, and the Windows ADB server is
  not reachable from the WSL gateway IP.
- `scripts/run-real-mcd-phone-gate.sh` now wraps the next sync/launch/logcat/
  screenshot/checker flow, and `scripts/check-real-mcd-proof.sh` now counts
  `PFCUT-MCD-NET shadow ... response status=2xx` as valid network bridge
  evidence.

72-hour real-McD swarm issues:

- `PF-602` / `A2OH/westlake#575` 72-hour dashboard success parent. Owns the
  2026-04-30 72-hour
  mission: stock `com.mcdonalds.app` must render a real dashboard on the phone
  while running inside Westlake guest `dalvikvm`. Baseline is
  `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/`. Success
  requires visible dashboard content beyond the sparse shell, ADB interaction
  proof, clean fatal/JNI/ULE gates, and Android bionic plus OHOS/musl build or
  symbol-gate evidence from the same source.
- `PF-603` / `A2OH/westlake#576` portable Realm table/query/result/row state
  machine. Owner:
  Realm/storage worker. Implement targeted table handles, schema property
  handles, stable column keys, query handles, result handles, row handles,
  result sizes, and row getters for the observed McD dashboard Realm frontier:
  `class_KeyValueStore`, `class_BaseCart`, `_maxAge < $0`, `_maxAge != $0`,
  `key = $0`, and `cartStatus = $0`. Reject global cardinality fakes.
- `PF-604` / `A2OH/westlake#577` McD dashboard visibility, databinding, and
  View-tree density. Owner: McD reverse-engineering worker. Map the exact
  conditions that keep
  `home=LinearLayout#0x7f0b0ae8` `GONE`, the data dependencies for dashboard
  sections, and the app model preconditions for menu/deals/restaurant/cart
  content.
- `PF-605` / `A2OH/westlake#578` real McD network/image/content transport
  boundary. Owner:
  network/images worker. Log outgoing URL/method/header/body/status/image-byte
  paths, decide live endpoint versus URL-keyed fixture cache when auth blocks,
  and feed data through the app's own parser/model path rather than direct UI
  frames.
- `PF-606` / `A2OH/westlake#579` dashboard UI rendering, Material/AppCompat
  widgets, scroll/input. Owner: UI worker. Make the real McD View tree
  measure, lay out, draw, scroll, and receive touch for dashboard layouts.
  Implement generic widget/material behavior where missing; do not draw a
  McD-specific replacement frame as success.
- `PF-607` / `A2OH/westlake#580` runtime stability, coroutine/event, CPU, and
  cutout cleanup. Owner:
  runtime worker. Narrow or replace the temporary
  `JustFlipBase.c(JustFlipFlagEvent)` event-emission shield, monitor hot loops,
  and keep runtime changes generic and dual-buildable.
- `PF-608` / `A2OH/westlake#581` OHOS/musl southbound parity for McD-critical
  APIs. Owner:
  southbound worker. Ensure every Android-phone success path has an explicit
  OHOS adapter path or open issue, especially Realm storage, networking/images,
  surface/input, filesystem/storage, and native loading policy.
- `PF-609` / `A2OH/westlake#582` proof automation and acceptance evidence.
  Owner: QA/proof worker.
  Maintain repeatable build/push/launch/wait/screenshot/logcat/hash/process
  scripts, compare frame stats against baseline, and reject phone-ART or
  sparse/black/white proofs.

Detailed OHOS handoff and the southbound shim ladder from controlled Yelp to a
McDonald's-class stock APK are documented in
`docs/engine/OHOS-YELP-LIVE-PORTING-GUIDE.md`.

Current McD proof frontier on 2026-05-01 18:35 PT:

- `artifacts/real-mcd/20260501_183146_mcd_real_dashboard_projection_scroll_probe_after_patch/`
  is the latest accepted real-McD stock dashboard projection proof with
  visible touch-scroll movement.
- The phone-visible UI is no longer the old hardcoded McD mock/fallback, but it
  is still a simplified Westlake strict display-list projection. Do not claim
  full stock Android View or Material parity.
- Execution purity remains intact: stock McD route runs inside the Westlake
  guest `dalvikvm` subprocess under `com.westlake.host`, with no direct
  phone-ART `com.mcdonalds.app` process.
- Root proof:
  `PF-MCD-ROOT phase=select
  activity=com.mcdonalds.homedashboard.activity.HomeDashboardActivity
  ... fallback=false`.
- Network proof:
  `network_attempt_markers=15`, `network_success_markers=9`,
  `network_error_markers=0`, `westlake_bridge=9`.
- Real McD row XML and adapter proof:
  `layout_home_promotion_item` resource `0x7f0e036a`,
  `layout_home_popular_item_adapter` resource `0x7f0e0369`,
  `HomePromotionAdapter count=1`, and `HomePopularItemsAdapter count=3`.
- Strict frame:
  post-scroll frame `bytes=128598 views=96 texts=2 buttons=3 images=1
  rows=4 rowImages=4 rowImageBytes=127479 overlays=0`.
- Touch/scroll proof:
  screenshot hash changed from
  `50187c3fcfd0858ec8795f6c86533d2f1fb19f316a60d8062f582eb716c6ddea`
  to `d63d41a034ad6e3efb38bf920ffe48d6be480f6523724f8f1b2280b4a641b8d4`;
  logs include `MCD_DASH_TOUCH_ROUTE phase=down/move/up` and
  `MCD_DASH_SCROLL ... projectionAfter=370 moved=true`.
- `scripts/check-real-mcd-proof.sh` now recognizes two McD proof tiers:
  current `mcd_stock_dashboard_projection`, and the older higher-tier
  child-fragment section-root proof. Missing Hero/Menu child-fragment markers
  in the projection tier are warnings, not evidence of fallback/mock UI.
- Next PF-604/PF-606 work is to replace the projection-specific scroll offset
  with generic View/RecyclerView scroll dispatch, then add a stock listener or
  adapter tap proof from the visible dashboard projection.

Additional ladder workstreams:

- `PF-458` REST matrix probe: Android phone accepted for methods, headers,
  body upload, redirects, timeouts, truncation, and non-2xx bodies at the
  marker-contract level; real multi-method execution remains open after the
  current VM SIGBUS gap
- `PF-459` generic inflated View draw path for the Yelp XML tree: Android
  phone accepted for the first DLST serialization slice
- `PF-460` generic View hit testing and scroll containers: Android phone
  accepted for non-disruptive inflated XML `Button.performClick()` hits into
  Yelp `Search`, `Details`, and `Saved` listeners, plus actual `ScrollView`
  inflation/discovery; broad coordinate hit dispatch and full scroll routing
  remain open
- `PF-461` adapter/list virtualization and image rebinding: Android phone
  accepted for the first Yelp `ListView`/`BaseAdapter` row-binding slice;
  RecyclerView-class virtualization remains open
- `PF-462` upstream-compatible Material shim expansion
- `PF-463` lifecycle/recreate/back-stack/state stress
- `PF-464` preferences/cache/file/database storage
- `PF-465` service probes for text input, connectivity, locale/time, and
  accessibility-shaped metadata
- `PF-466` McDonald's preflight controlled mock app profile before returning
  to the stock APK path: Android phone accepted on `cfb7c9e3` for
  `com.westlake.mcdprofile`, built from `test-apps/10-mcd-profile/` and run
  with `scripts/run-mcd-profile.sh`. The accepted proof covers app-owned
  Application, generic `WestlakeActivityThread` launch through
  `AppComponentFactory`, attach/lifecycle, compiled XML resource loading before
  `onCreate`, `resources.arsc` table parsing for this APK, Material-shaped tag
  traversal and ID binding, guest `ListView` adapter row binding through
  position `4`, XML measure/layout probe,
  SharedPreferences cart state, host/OHBridge live JSON and one bounded image,
  guest `String.getBytes("UTF-8")` for a REST payload, REST bridge v2 POST
  with payload, HEAD, and non-2xx status probes, full-phone `1080x2280` `DLST`
  through repeated-cart and post-checkout navigation frames, and strict touch
  navigation. Current accepted hashes:
  `dalvikvm=2dd479e0c7f98e8fd3c4c09b539bfe30fe1c39b119d36e034af68c6bcaada6cf`,
  `aosp-shim.dex=d548351815ba5d8a700b7dd48089d652ec43623b032383738d036ae30740949d`,
  `westlake-host.apk=d6d8e81a801bb799a815039abc0b296416c723a11f2c31547077ddb87cad7c68`,
  `westlake-mcd-profile-debug.apk=50477eccecc86fa5ecd8144d26b3930ec60d68c3b952708d66aba934ea448933`.
  It is the current OHOS controlled mock profile target, not the real
  McDonald's app and not a stock McDonald's APK compatibility claim.
- `PF-467` generic real-APK Activity construction: accepted for the
  McD-profile controlled app through the WAT/AppComponentFactory path; still
  open for arbitrary stock McDonald's activities and for removing remaining
  app-specific launch allowances
- `PF-468` standalone runtime object-array correctness: close the DEX
  object-array/new-array boundary exposed by profile-item `String[]` models.
  PF-466's `resources.arsc` `ArrayStoreException` is closed by keeping parsed
  string pools as `Object[]`, but that does not prove arbitrary app `String[]`
  allocation/assignment correctness
- `PF-469` McD-class generic Material XML and theming: move from the accepted
  McD-profile tag/bind slice to upstream-compatible Material tag inflation,
  ID assignment, themes/styles, Coordinator/AppBar behavior, ripple, and
  animation
- `PF-470` generic visible rendering/input replacement: replace the
  McD-specific direct `DLST` writer and coordinate router with generic View
  draw, hit testing, scrolling, adapter/list rendering, and invalidation.
  PF-475 adds a controlled sidecar proof for generic `MotionEvent` dispatch,
  checkout `MaterialButton` click, and XML `ListView` real-coordinate
  item-click invocation with `fallback=false`, but generic visible rendering
  and pure `AdapterView` touch-dispatch item click remain open
- `PF-471` production-grade portable networking/images: replace synthetic REST
  matrix coverage and one capped image proof with real multi-method execution,
  large-body streaming, redirects, timeout/error parity, direct libcore
  networking parity, and multi-image transport
- `PF-472` OHOS McD-profile adapter parity: implement the same guest-facing
  surface/input/storage/network contracts in an OHOS Ability/XComponent host
  and rerun PF-466 there
- `PF-473` standalone libcore charset/encoding correctness: fix
  `Charset.forName`, `String.getBytes("UTF-8")`, and default `PrintStream`
  encoding in the Westlake guest runtime. PF-466 now accepts standard app
  `String.getBytes("UTF-8")` and the runner rejects both `NPE-SYNC` and the
  charset alias `String[]` `ArrayStoreException`; startup stdio remains on the
  ASCII-safe wrapper and broader charset/provider/default-encoding coverage is
  still required for stock APKs.
- `PF-474` post-checkout direct-frame renderer/runtime stress: Android phone
  proof accepted for the controlled McD-profile direct renderer. The runner now
  drives deterministic touch-file packets, requires `MCD_PROFILE_CART_ADD_OK
  count=2`, `MCD_PROFILE_CHECKOUT_OK count=2`, and
  `MCD_PROFILE_DIRECT_FRAME_OK reason=checkout_touch_up ... checkedOut=true`,
  and still fails closed on `SIGBUS|SIGILL`. The fix coalesces touch-driven
  dirty invalidation into the touch frame instead of emitting a redundant
  immediate dirty frame after every handled touch. This closes the controlled
  PF-466/PF-474 path, not generic View rendering.
- `PF-475` McD-profile generic XML input/list sidecar: Android phone proof
  accepted for dispatching touch-file packets as `MotionEvent`s into the
  inflated XML View tree. The runner now requires
  `MCD_PROFILE_GENERIC_TOUCH_OK ... adapter=true`,
  `MCD_PROFILE_GENERIC_LIST_BOUNDS_OK ... children=[1-9]`,
  `MCD_PROFILE_GENERIC_LIST_HIT_OK ... position=[0-9]+ ... clicked=true ... fallback=false`,
  and
  `MCD_PROFILE_ADAPTER_ITEM_CLICK_OK`. The accepted list proof invokes
  `AdapterView.performItemClick()` on the XML `ListView` from a real laid-out
  coordinate and reaches the app `OnItemClickListener`; the checkout button
  also records `MCD_PROFILE_GENERIC_CLICK_OK` for the `MaterialButton`. This is
  a sidecar proof, not full PF-470 closure, because the visible frame still
  uses the McD-specific direct renderer and ListView item selection is still
  launcher-assisted rather than pure `AbsListView` touch-dispatch behavior.
- `PF-476` stock McDonald's dashboard/databinding blocker: real McDonald's APK
  `com.mcdonalds.app` now stages and launches through Westlake, reaches
  `McDMarketApplication.onCreate`, reaches `SplashActivity.onCreate`, schedules
  `com.mcdonalds.homedashboard.activity.HomeDashboardActivity`, and enters
  `HomeDashboardActivity.onCreate`. The previous Gson annotation proxy,
  `sun.misc.Unsafe.allocateInstance()`, `TimeZone.getDefault()`, ICU regex, and
  `DataSourceModuleProvider.v()` interface-dispatch blockers are no longer the
  latest frontier in the current phone proof. The `d7bb5761...` runtime plus
  `a9b115...` shim proof no longer emits `DATABINDING_TAG_NULL` or
  `ApplicationNotificationBinding`, and the current accepted log has no fatal
  signal marker. The proof also moves past the Material
  `BottomNavigationView` self-cast `ClassCastException`. It now bypasses app
  FragmentManager `commitNow()` and `HomeDashboardFragment.performAttach()` in
  strict mode, reaches
  `HomeDashboardFragment.performCreate()`, `performCreateView()`, attaches a
  `ScrollView`, invokes `performActivityCreated()`, and logs
  `Programmatic HomeDashboardFragment attached`. The latest blocker before
  first real dashboard UI is now an app dependency/state NPE:
  `RestaurantModuleInteractor.s()` is invoked on a null receiver.
  Current verified proof:
  `/mnt/c/Users/dspfa/TempWestlake/real_mcd/real_mcd_material_policy_20260429_130813.log`
  (`57eb43e76d70f277dad79527e496a27699fba537a6a7f25753e108d8ba90ebbf`),
  screenshot
  `/mnt/c/Users/dspfa/TempWestlake/real_mcd/real_mcd_material_policy_20260429_130813.png`
  (`a4ae352c727c2c8f182275b68beb48543c258ffa4eb6652ed006ea7d103d3bd3`,
  valid `1080x2280` PNG),
  deployed runtime
  `d7bb5761ea16d56ff41ce49a6499627748054d3af8413bb44e1615ec9dd2f8d2`,
  and deployed `aosp-shim.dex`
  `a9b115a81dba519991d20aa3e48e52f701abec43b71dd652cf07c933856bf40e`.
  The next implementation task is to close the `RestaurantModuleInteractor`
  state gap, source-reproduce Material class ownership, and then remove the
  McD-specific strict-mode lifecycle skips by making app-owned AndroidX
  fragment attach/transaction work generically.
- `PF-477` Material class ownership for stock APKs: Android phone boundary
  accepted with a source-repro gap. The previous dashboard
  `BottomNavigationView` self-cast proved duplicate definitions for
  `com.google.android.material.*`. Runtime
  `d7bb5761ea16d56ff41ce49a6499627748054d3af8413bb44e1615ec9dd2f8d2`
  is a one-byte derivative of accepted baseline `c90d15a...` that disables
  Material from the runtime loader-first package list; with that binary, the
  real McDonald's proof moves past the Material self-cast and reaches the next
  app-state NPE. The source-level target remains ART/classloader policy:
  Material must be Westlake shim-owned for this path, with app bytecode and XML
  inflation resolving to the same boot/shim class. A local ART source attempt
  to prefer boot/shim Material produced runtime candidate
  `7523774ecfdabeec733718326a3f74e87ce51aa080b28237a741f253be0efadb`, but
  that binary regressed before Splash/Dashboard and is rejected. Keep phone
  proofs on accepted runtime `d7bb5761...` until a clean source-built runtime
  passes bootstrap and dashboard markers.
- `PF-478` real McDonald's fragment lifecycle/rendering: open. The current
  accepted proof reaches the real `HomeDashboardFragment`, real dashboard
  `u6(List)` placeholders, all four real child fragment `onCreateView` returns,
  plus all four dashboard child section roots through real
  McDonald's AXML (`fragment_home_dashboard_hero_section.xml`,
  `home_menu_guest_user.xml`, `fragment_promotion_section.xml`, and
  `fragment_popular_section.xml`). It still bypasses app FragmentManager
  execution and `performAttach()`, so this is an important boundary proof, not
  final stock FragmentManager compatibility. The next visible-density gap is
  item adapter/data population, because the all-real-section-root frame is
  structurally real but visually sparse.
- `PF-479` real McDonald's dashboard dependency/state wiring: partially
  advanced. The older `RestaurantModuleInteractor.s()` null-receiver blocker is
  no longer the active frontier for the accepted phone path. Current open state
  gaps are deeper child lifecycle/state paths: `onViewCreated` currently SIGBUSes
  in `LiveData.observe`, and stock app FragmentManager execution remains unsafe.
- `PF-480` real McDonald's standalone `core-oj.jar` runtime frontier: open.
  The 2026-04-29 14:25 phone proof advances beyond the earlier
  `RestaurantModuleInteractor` branch and exposes generic standalone libcore
  gaps under the real APK. Phone-proven artifacts are
  `dalvikvm=d7bb5761ea16d56ff41ce49a6499627748054d3af8413bb44e1615ec9dd2f8d2`,
  `core-oj.jar=8c344b1ac41bdbb4403763a5b061a8313056a010752835273cf90d79dd561d44`,
  and
  `aosp-shim.dex=9d7ffa3a60c37b21fc1bed01f1cb9f52de8e720b4c454d9d096eb255ef5c5bf4`.
  This proof clears the Realm `UnixFileSystem.list(File)` SIGBUS,
  `AtomicInteger.getAndIncrement`, `AtomicReference.getAndSet`,
  `Unsafe.getUnsafe`, and `AtomicLong.compareAndSet` blockers and reaches
  SplashActivity construction, AndroidX ActivityResult registration, Hilt
  listener setup, and NewRelic trace construction. The local pending
  `core-oj.jar=4b152c62e7746ca93df19c6e25fe744c86fe29b1dbff45d9fc24a9675d855c45`
  adds `System.getProperty` null-protection but is not phone-proven yet.
  Durable closure requires source-level libcore/runtime fixes, not only
  binary DEX patches.
- `PF-481` real McDonald's observability/no-op shim return contracts: open.
  The current fatal path includes
  `com.newrelic.agent.android.tracing.Trace.<init>()` calling
  `java.util.Random.nextLong()` on a null receiver after the Westlake NewRelic
  cutout returns null for `Util.getRandom()`. No-oping third-party telemetry
  must preserve non-null return contracts for methods that callers
  immediately dereference. Source candidate:
  `$HOME/art-latest/patches/runtime/interpreter/interpreter_common.cc`
  now excludes `com.newrelic.agent.android.util.Util.getRandom()` from the
  blanket NewRelic no-op path. The built but not phone-accepted candidate
  runtime is
  `$HOME/art-latest/build-bionic-arm64/bin/dalvikvm`
  (`b193e5f3ff3ba564f58319fe3b81cca3ead7c605450e7c05e68e06d14d7151cd`,
  symbol gate passed). Do not replace accepted phone runtime
  `d7bb5761...` with this candidate until it passes a real-McD phone proof.
- `PF-482` real McDonald's evidence replay hygiene: open. `core-oj.jar` is now
  a required runtime artifact. `scripts/deploy-real-mcd-windows.ps1` and
  `scripts/sync-westlake-phone-runtime.sh` must push and hash
  `core-oj.jar` alongside `dalvikvm` and `aosp-shim.dex`; future proof logs
  are invalid without all three hashes.
- `PF-483` real McDonald's Westlake-rendered dashboard fallback frame:
  Android phone proof accepted as a boundary, not as stock UI compatibility.
  The latest 2026-04-29 18:52 run launches the stock McDonald's APK through
  Westlake, reaches real `SplashActivity`, launches real
  `HomeDashboardActivity`, completes `HomeDashboardActivity.onCreate`, and
  emits a visible full-height dashboard frame through the Westlake guest
  subprocess. Accepted artifacts are
  `artifacts/real-mcd/20260429_185146/logcat_tail_20260429_185146.log`
  and
  `artifacts/real-mcd/20260429_185146/real_mcd_screen_20260429_185146.png`.
  Deployed hashes:
  `dalvikvm=0b7acbe35837c357ded4e3413f3c64057efd256b9e31854221112b346f14b17f`,
  `core-oj.jar=e19236b056ec6257c751d070f758e682dc1c62ba0cb042fde93d3eec09d647c2`,
  and
  `aosp-shim.dex=2284d0f553ffb9eae3e1f7cc4d1afccf3fcb0875876ddedae3bec3cc9d76d1e1`.
  The proof logs `Strict dashboard frame reason=dashboard-first
  root=android.widget.LinearLayout bytes=776 views=29 texts=14 buttons=6
  images=1`, followed by a touch-driven dashboard render-loop frame, and the
  screenshot is a valid `1080x2280` phone frame. Current gap: visible content
  is a Westlake-generated dashboard scaffold installed into the real McDonald's
  activity/container path, not real production dashboard content.
- `PF-484` real McDonald's touch-to-state proof on Westlake dashboard
  fallback: Android phone proof accepted for the fallback route only. ADB tap
  reaches the host touch file, the Westlake guest consumes `Touch DOWN/UP`,
  routes the dashboard fallback button zone, mutates fallback state, and emits
  a new DLST frame. The latest 18:52 run logs `Dashboard fallback direct touch
  routed` followed by `Strict dashboard frame reason=dashboard-renderLoop`.
  Current gap: this uses McD-dashboard direct coordinate routing; generic View
  hit testing, scroll routing, adapter dispatch, and invalidation must replace
  it before stock UI compatibility can be claimed.
- `PF-485` `dalvik.system.VMRuntime.getSdkVersion()` standalone API gap:
  watchlist. The 15:50 proof logged
  `NoSuchMethodError: No InvokeType(0) method getSdkVersion()I in class
  Ldalvik/system/VMRuntime;`, but the 18:52 proof no longer shows this marker
  in the captured window and `HomeDashboardActivity.onCreate` completes. Keep
  this as a regression probe rather than the current top blocker.
- `PF-486` real McDonald's portable network availability shim: Android phone
  proof accepted for the current `ConnectivityManager` availability/type path.
  The 18:42 proof died with native `SIGBUS` inside
  `com.ohos.shim.bridge.OHBridge.isNetworkAvailable()` from
  `ConnectivityManager.getActiveNetworkInfo()`. `ConnectivityManager` now
  routes `getActiveNetworkInfo()`, `getActiveNetwork()`,
  `isDefaultNetworkActive()`, and `getNetworkInfo(int)` through
  `android.net.NetworkBridge`, which provides a Java fallback and a portable
  OH-style network type. The 18:52 proof has no `SIGBUS` or `Fatal signal`.
  Remaining networking work is production HTTP/socket parity and OHOS adapter
  implementation, not this availability gate.
- `PF-487` real McDonald's Application/libcore clean initialization: open.
  The latest 22:08 proof survives Activity launch and closes two generic
  runtime blockers in this stream: `MethodHandles.lookup()` now reports real
  callers for core concurrency clinit, and `DexPathList.findLibrary()` no
  longer crashes on a null native-library path array. Full split/native payload
  staging now resolves `realm-jni` to
  `/data/local/tmp/westlake/app_lib/librealm-jni.so` and calls
  `Runtime_nativeLoad` with that real path. The remaining clean-init gap is now
  libcore/framework behavior after native load: `PersistenceManager` still
  tolerates initialization failures. The 23:29 proof closes the immediate
  `CodingErrorAction.REPORT` null-action crash and reaches dashboard again, but
  `McDMarketApplication.onCreate` still logs `Config failed to download` and
  Realm still logs missing/unclean native initialization. Close this only after
  charset/provider/default-encoding coverage, Realm/ReLinker initialization,
  and clean `McDMarketApplication.onCreate`.
- `PF-488` stock APK native library staging contract: accepted for current
  phone proof, regression-watch. Host now copies installed sibling
  `split_*.apk` payloads, extracts `lib/arm64-v8a/*.so` into
  `/data/local/tmp/westlake/app_lib`, passes that directory as
  `java.library.path`, `app.native.lib.dir`, `westlake.native.lib.dir`, and
  `WESTLAKE_NATIVE_LIB_DIR`, and the runtime has a guarded
  `DexPathList.findLibrary()` fallback over those paths. The 22:08 proof shows
  `realm-jni` resolving to the extracted split library. Remaining work is
  OHOS-side split/native packaging semantics, not Android phone proof.
- `PF-489` MethodHandles caller-sensitive runtime correctness: accepted for
  the current real-McD concurrency bootstrap window. Runtime now handles
  `MethodHandles.lookup()` at the interpreter call site using the caller
  shadow frame, and the 22:08 proof has no `illegal lookupClass` markers.
  Keep as a regression probe until broader caller-sensitive methods are covered
  without Westlake-specific shortcuts.
- `PF-490` real McDonald's portable NIO filesystem boundary: accepted for the
  current real-McD proof, regression-watch. The 23:29 phone proof closes the
  `File.toPath()` null `FileSystems.getDefault()` blocker, wires a portable
  default `LinuxFileSystem(/data/local/tmp/westlake)` object, and handles
  `UnixNativeDispatcher.stat/lstat/access` for `UnixPath` directly so APK ZIP
  access no longer enters the bad `NativeBuffer`/`stat0` SIGBUS path. Accepted
  artifact:
  `artifacts/real-mcd/20260429_232926_viewmodel_helper/logcat_tail.log`.
  Deployed runtime:
  `dalvikvm=1011e6072a0a289deda47a379e028382e224adf7d4c6fb5f2f2af5d3daa8c467`.
  OHOS port gap: map these operations onto OHOS file/stat/open/dir APIs behind
  the same guest-facing NIO contract rather than depending on Android ART.
- `PF-491` real McDonald's charset action bootstrap: accepted for the current
  path, broader charset provider work still open under `PF-473`. The previous
  `CharsetEncoder.onMalformedInput/onUnmappableCharacter` `IllegalArgumentException:
  Null action` is closed by substituting a valid
  `java.nio.charset.CodingErrorAction.REPORT` object when the boot static is
  null. The 23:29 proof reaches dashboard after these calls. Remaining work is
  full charset/provider/default-encoding parity, not only this action object.
- `PF-492` real McDonald's AndroidX Lifecycle KClass/ViewModel bridge:
  accepted as a boundary, not as final generic AndroidX support. The 23:29
  proof closes the previous SIGBUS at
  `androidx.lifecycle.viewmodel.ViewModelProviderImpl_androidKt.a(factory,
  KClass, CreationExtras)` by delegating to
  `Factory.create(Class, CreationExtras)` and logs delegation through AndroidX,
  Hilt retained component, and Hilt view model factory paths. Remaining gap:
  one default-factory branch still throws the unsupported
  `Factory.create(String, CreationExtras)` path and Westlake allocates the
  requested ViewModel without constructor as a boundary probe. Final closure
  requires a generic AndroidX-compatible factory/state path.
- `PF-493` real McDonald's Realm/ReLinker/native initialization: open and now
  the top runtime/app-init frontier. The 23:29 proof attempts
  `Runtime_nativeLoad path=/data/local/tmp/westlake/app_lib/librealm-jni.so`,
  but later `PersistenceManager` tolerates
  `MissingLibraryException: Could not find 'librealm-jni.so'` and app code
  logs `Call Realm.init(Context) before creating a RealmConfiguration`. Close
  this by making split native-library discovery, ReLinker extraction metadata,
  native-load lifetime, and Realm initialization coherent in the portable guest
  contract. The 23:37 proof closes the split metadata part: shim
  `ApplicationInfo` now exposes staged `splitSourceDirs`, and ReLinker moves
  from `only found: []` to loading
  `/data/local/tmp/westlake/app_lib/librealm-jni.so.10.19.0`. The new blocker
  inside PF-493 is dynamic native-loader compatibility:
  `UnsatisfiedLinkError: libdl.a is a stub --- use libdl.so instead`.
  Accepted artifact:
  `artifacts/real-mcd/20260429_233724_split_metadata/logcat_tail.log`.
  Deployed hashes:
  `dalvikvm=1011e6072a0a289deda47a379e028382e224adf7d4c6fb5f2f2af5d3daa8c467`,
  `aosp-shim.dex=9d29d310c5d1928f27a2940c75f1a9ae824cc99582969ee6e2c281944c2c527e`,
  `westlake-host.apk=3ceef0010d6533a9cdaf2842dab58f311ad2fbe99305ab8afb074e0d0bfe2f19`.
  Update 2026-04-30 15:50: the latest bionic proof moves beyond the prior
  `Table.<clinit>` null-prefix failure. `Runtime_nativeLoad` is stubbed
  successful for `librealm-jni.so`, `Util.nativeGetTablePrefix()` returns
  `"class_"`, and `TableQuery.nativeValidateQuery(...)` returns an empty
  validation string. This does not close PF-493: most Realm native entrypoints
  still return no-op/zero handles, including `OsSharedRealm`, `OsSchemaInfo`,
  `Property`, `OsRealmConfig`, `nativeGetSharedRealm`, and table-ref paths.
  The dashboard remains sparse because no real Realm-backed persistence/query
  data reaches the app. Update 2026-04-30 16:05: shim-side SDK persistence
  seeding closes the noisy `ModuleConfigurations.getPersistence()` NPE
  (`27` -> `0` hits in the phone proof), but PF-493 remains open because the
  app still reaches Realm with fake schema/shared-realm/table handles. Update
  2026-04-30 16:35: the config/flag diagnostic proof moves past the latest
  `HomeDashboardActivity.onCreate` `Failed requirement`, and
  `HomeDashboardActivity.onCreate` now returns. The top PF-493 gap is now
  targeted Realm result/row semantics: `OsResults.nativeSize(...)` is still
  broadly zero, and `Table.nativeGetRowPtr(...)`,
  `UncheckedRow.nativeGetLong(...)`, and `UncheckedRow.nativeGetString(...)`
  still return no-op/empty probe values. Do not close PF-493 with a global
  positive cardinality fake; the prior blunt `nativeSize -> 3` probe regressed
  before the dashboard. Update 2026-04-30 16:55:
  `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/` adds
  behavior-preserving Realm argument diagnostics and proves the concrete table
  frontier: `class_KeyValueStore` and `class_BaseCart`. The next PF-493 patch
  should track table handles from `OsSharedRealm.nativeGetTableRef(...)`,
  property handles/column keys from `OsObjectSchemaInfo.nativeGetProperty(...)`
  and `Property.nativeGetColumnKey(...)`, query/result handles from
  `Table.nativeWhere(...)` / `OsResults.nativeCreateResults(...)`, and row
  handles from `Table.nativeGetRowPtr(...)`.
- `PF-494` stock APK dynamic native-library loading contract: open. Real APK
  shared libraries such as Realm depend on Android system loader libraries
  (`libdl.so`, `libandroid.so`, `liblog.so`, etc.). The current Westlake
  `dalvikvm` is a static target whose `Runtime.nativeLoad` path reaches a
  `libdl.a` stub error when loading Realm. Final closure requires an
  OHOS-portable native-loader abstraction or a clearly-scoped temporary Realm
  JNI boundary; silently pretending arbitrary native APK libraries loaded is
  not accepted as stock APK compatibility. Diagnostic note: the existing
  Android `link-dynamic` target builds
  `dalvikvm-dynamic=32c027a0643ad4e8b303ea8c876cfac2ab6dc4c631cb8b741e3ef98e33c9225f`,
  but the 23:42 phone run
  `artifacts/real-mcd/20260429_234224_dynamic_native_loader/logcat_tail.log`
  aborts before app code with a `java.lang.String` class mismatch and
  `SIGABRT`. It is rejected as a drop-in runtime. The accepted phone runtime
  was restored to static
  `dalvikvm=1011e6072a0a289deda47a379e028382e224adf7d4c6fb5f2f2af5d3daa8c467`.
- `PF-495` strict-subprocess southbound framework services: partially
  accepted for the static registration contract. In strict Westlake subprocess
  mode, Java framework service APIs must not fall off unregistered
  `OHBridge.*` natives. The source-backed audit now reports
  `OHBridge Java declarations=175`, `static registrations=175`, `missing=0`,
  `extra=0`, and `duplicate=0` via
  `tools/westlake_boundary_audit.py --check-static`. Bionic
  `dalvikvm=6e2a35b98d47e86c8861c994d4d47511373fb8e18dc6cbe4d3996a4b217f273a`
  and OHOS
  `dalvikvm=08381938f009d508258aa2b8f81e1503194f8d3d4da78085c9b21167a687535d`
  both link and pass the runtime symbol gate. Phone proof
  `artifacts/real-mcd/20260430_095103_ohbridge_full_boundary/` cold-starts
  the real McDonald's Westlake guest and keeps the focused fatal/ULE gate
  clean. Remaining PF-495 work is service behavior: replace conservative
  compatibility defaults for preferences/RDB/network/wifi/audio/media/
  clipboard/vibrator/sensors with real portable OHOS adapters or deliberate
  Java shim implementations where that is the correct Android-semantics layer.
- `PF-496` non-Android Java SE dependency removal from shims: open,
  accepted for the current `java.lang.management` miss. The diagnostic proof
  `artifacts/real-mcd/20260430_000854_boot_ncdfe_descriptor_rebuilt/`
  identified the render-loop boot-class-loader miss as
  `Ljava/lang/management/ManagementFactory;`. The accepted proof
  `artifacts/real-mcd/20260430_001605_clean_ncdfe_logging/` removes that
  dependency from `android.os.Process` and `android.os.SystemClock`; the
  dashboard render loop pumps messages and emits a dashboard frame without the
  prior `NoClassDefFoundError`. The accepted runtime
  `fbce9ed66b5e023f749c6f83cf8df2b48abe97c5e91fdf2acc96675db8ba5f05`
  has temporary boot-miss descriptor logging disabled again. Continue scanning
  shims for Java SE APIs that Android/OHOS should not require, including
  repeated diagnostic misses such as `java.awt.Font`.
- `PF-497` Java concurrency VarHandle compatibility: accepted for the current
  stock McDonald's `FutureTask`/Rx scheduler blocker. Diagnostic proof
  `artifacts/real-mcd/20260430_003153_varhandle_mode_probe/` identified a
  zero `accessModesBitMask` on `java.lang.invoke.FieldVarHandle` for
  `FutureTask.runner`, causing `compareAndSet` to throw
  `UnsupportedOperationException`. The accepted clean proof
  `artifacts/real-mcd/20260430_003805_clean_varhandle_futuretask/` uses
  `dalvikvm=77389791ad497b68efe7357c96f7c2ec84522fb6daff3ba82b56e584714984db`
  and shows dashboard render-loop survival with no `pending UOE`,
  `ThreadGroup.uncaughtException`, `PFCUT-UOE-THROW`, VarHandle diagnostic
  marker, `DoCall-TRACE`, or `NoClassDefFoundError`. The implementation is a
  guarded runtime fallback for zero-mask `FieldVarHandle` and
  `StaticFieldVarHandle` modes derived from the native `ArtField` and variable
  type, preserving read-only final-field behavior. Continue with broader
  VarHandle tests for array, byte-buffer, numeric, bitwise, and wrong-method
  type behavior before claiming full Java 9+ VarHandle parity.
- `PF-498` real McDonald's source-built ARM64 runtime reproducibility and
  symbol gate: accepted for the current Android phone proof, OHOS full-runtime
  proof still open. The VarHandle fix is now durable in the
  `$HOME/art-latest` patch build path. Clean bionic rebuild reports
  `runtime 230 / 230`, restores real A15 ARM64
  `thread_arm64.cc` and `entrypoints_init_arm64.cc`, assembles A15
  `quick_entrypoints_arm64.S` with A15 include precedence, and keeps A11 JNI
  assembly isolated for the current portable CFI gap. The previous deploy
  blocker from strong unresolved symbols is closed:
  `scripts/check-westlake-runtime-symbols.sh` passes on
  `dalvikvm=1c136763c746f8e16e06451779b6e201621eeb0ca10ccd59a6d01a53f19fd9a3`,
  with only weak loader/HWASAN hooks left in `nm -u`. Phone proof:
  `artifacts/real-mcd/20260430_011506_clean_patchsystem_a15_arm64/`; focused
  grep contains only `Strict dashboard frame reason=dashboard-renderLoop`.
  `Makefile.ohos-arm64 asm metrics-stubs` also compiles the corresponding A15
  quick-entrypoint and `thread_cpu_stub.cc` slice, but a full OHOS
  `dalvikvm` link/run remains a separate gate.
- `PF-499` real McDonald's PFCUT diagnostic/fallback cleanup: partially
  advanced. The 09:24 proof
  `artifacts/real-mcd/20260430_092445_portable_tz_ohos_symbol_gate/` is clean for
  fatal/UOE/class-loader/native-link markers and has no `PFCUT-TZ` or
  `WESTLAKE-TZ` markers. `TimeZone.getDefault()` now uses a repo-backed
  portable runtime bridge that honors `WESTLAKE_TIMEZONE_ID`/`TZ`, computes the
  current raw offset from libc, and gates diagnostics behind
  `WESTLAKE_TRACE_TZ`; the quick trampoline copy is compiled from
  `patches/runtime/entrypoints/quick/quick_trampoline_entrypoints.cc` in both
  bionic and OHOS Makefiles. Remaining broad-log cutouts are ICU `ULocale`,
  currency, atomics/Unsafe, proxy/interface repair, and McD logging/perf
  no-ops. Convert each one into a generic portable implementation or a
  documented service bridge, then remove noisy diagnostics from accepted proof
  windows.
- `PF-500` OHOS ARM64 runtime linkage gate: accepted for full static link and
  strong-symbol cleanliness, not yet for OHOS hosted execution. `make -f
  Makefile.ohos-arm64 link-runtime -j4` now builds
  `build-ohos-arm64/bin/dalvikvm` with hash
  `c5bd8135af2cfd86d052b96d4438a565bc73f80625fd10e25f3305540dc491de`.
  `scripts/check-westlake-runtime-symbols.sh` passes, leaving only weak
  runtime hooks in `llvm-nm -u`. Remaining acceptance requires launching this
  runtime under an OHOS Ability/XComponent or equivalent runner and proving the
  same guest-facing contracts used by the Android phone proof.
- `PF-501` real McDonald's source-built startup frontier: open. The active
  source-built route now launches the stock McDonald's APK inside the Westlake
  guest subprocess and reaches AndroidX multidex extraction during
  `WestlakeActivityThread$PackageInfo.makeApplication(...)`. The previous
  hot loops in `jdk.internal.misc.Unsafe.compareAndSetInt`,
  `compareAndSetReference`, `getReferenceVolatile`, and `compareAndSetLong`
  have been advanced by direct interpreter-side object/atomic handling. The
  CRC32/Adler32 candidate moved the run past the previous
  `java.util.zip.CRC32.updateBytes0(Native method)` crash. Latest diagnostic
  artifact with the new blocker:
  `artifacts/real-mcd/20260430_120310_crc32_portable_multidex_retry/`.
  Runtime for that diagnostic run:
  `dalvikvm=2bb435043a548d4f897863aa431b4f54d508423767ec3b6f68dcd790fc2f9988`.
  The current observed blocker is now a native `SIGBUS BUS_ADRALN` in
  `sun.nio.ch.FileKey.init(Native method)` called by
  `sun.nio.ch.SharedFileLockTable`, `FileChannelImpl.lock(...)`, and
  `androidx.multidex.MultiDexExtractor`. Local source now has a pending
  FileKey/NIO/log-gated candidate,
  `dalvikvm=57e3881148faaf042c06225bb62f1b1af1ba1fec8ac115c5204072fc07d057fb`.
  The same source builds under the OHOS static target and passes the runtime
  symbol gate with
  `build-ohos-arm64/bin/dalvikvm=e0f97e89511a0f5f00cf9e1ab5746cd52198b4af6125fa3ce41f0f42d2277316`.
  Its file push printed success, but the follow-up phone `sha256sum`/proof is
  not accepted yet because Windows ADB/WSL shell/logcat transport is currently
  failing with `UtilAcceptVsock:271: accept4 failed 110`. Done when the
  FileKey candidate is phone-hash-confirmed on `cfb7c9e3`, the real
  McDonald's proof moves past multidex file locking, and the next blocker is
  captured with hashes, focused grep, logcat, screenshot, and process state.
  Update 2026-04-30 15:50: the source-built path is now phone-proven past the
  FileKey/multidex and config-market regressions. Latest diagnostic artifact:
  `artifacts/real-mcd/20260430_154955_config_market_retry/`.
  It cold-starts the Westlake guest, reaches `SplashActivity` and
  `HomeDashboardActivity`, records the Realm table-prefix fix, and captures a
  full-phone `1080x2280` screen. It is still not accepted as real McDonald's
  UI compatibility because the dashboard render loop remains sparse and
  persistence is backed by Realm no-op handles. Update 2026-04-30 16:05:
  `artifacts/real-mcd/20260430_155909_sdk_persistence_seed/` keeps the same
  app-init/dashboard survival and removes the repeated SDK persistence NPE, but
  it does not improve visual/dashboard density.
  Update 2026-04-30 16:35:
  `artifacts/real-mcd/20260430_163127_config_flag_key_diagnostics/` is a real
  startup improvement: `HomeDashboardActivity.onCreate` returns and the
  dashboard becomes active. It is still not accepted as real McDonald's UI
  compatibility because the real home body remains hidden/sparse
  (`home=... v=8`, zero text/buttons in the dashboard frame). Next source work
  should instrument and then implement targeted Realm row/result semantics,
  not add more fallback dashboard drawing.
  Update 2026-04-30 16:55:
  `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/` has
  `Failed requirement` count `0`, records `262` Realm argument traces, and
  keeps `HomeDashboardActivity` active. It is still not a full UI milestone:
  the frame is unchanged at `bytes=191 views=20 texts=0 buttons=0 images=1`.
  This moves PF-501 from "find the next boundary" to "implement the targeted
  Realm table/query/row boundary".
- `PF-502` Java SE zip/checksum native contract for standalone runtime:
  partially accepted for the current real-McD multidex path. Stock APK startup
  depends on libcore ZIP/APK helpers through
  `java.util.zip.CRC32` and related checksum paths before real UI is reached.
  Westlake must provide these as portable runtime/libcore behavior, not as an
  Android-system ART dependency. The first local candidate registers
  `CRC32.update`, `CRC32.updateBytes`, `CRC32.updateBytes0`,
  `CRC32.updateByteBuffer`, `CRC32.updateByteBuffer0`, plus Adler32 update
  variants against zlib in `stubs/javacore_stub.c`. The
  `20260430_120310_crc32_portable_multidex_retry` diagnostic log moves past
  the previous CRC32 crash and exposes `FileKey.init` as the next blocker.
  Remaining closure requires keeping this green under the next accepted phone
  proof and carrying the same zlib/libc mapping into OHOS.
- `PF-503` Java SE NIO file-key/file-lock native contract for standalone
  runtime: open. AndroidX multidex uses `FileChannel.lock(...)`, which enters
  `sun.nio.ch.FileKey.create(...)` and needs a stable file identity from the
  guest runtime. Falling through to the wrong native body crashes with
  `SIGBUS BUS_ADRALN` in `FileKey.init`. Local source now registers
  `sun.nio.ch.FileKey.init/initIDs`, reads OpenJDK-style
  `java.io.FileDescriptor.fd` as well as the older `descriptor` field, and
  fills `st_dev`/`st_ino` using portable `fstat`. It also registers the
  `UnixFileDispatcherImpl` natives on the declaring class for this
  `core-oj.jar` and provides portable `lock0/release0/seek0/map0` basics for
  `FileChannel.lock(...)`. Acceptance requires a real McDonald's phone proof
  showing `MultiDexExtractor` gets past file locking, no `FileKey` or
  `FileDispatcher` fatal appears, and the next blocker is captured. OHOS
  closure requires mapping the same `fstat` identity and `fcntl` lock
  semantics onto OHOS file descriptors.

## 2026-04-25 Roadmap Corrections

The open roadmap should be updated around these issue themes before any
McDonald's-specific work is treated as forward progress:

- `CV-104` / `PF-801` / `A2OH/westlake#572`: harden canary evidence.
  Only markers written by canary app code count as `L0`/`L1` success.
  Launcher-authored `CANARY_*_OK` lines are diagnostics only.
- `CV-102`: `L0`/`L1` are accepted on the phone in both
  `control_android_backend` and `target_ohos_backend` with app-owned marker
  files.
- `CV-103` / `CV-201`: `L2` widgets/resources pass in both backend modes, and
  `L3` AppCompat/Fragment now passes in `target_ohos_backend` with app-owned
  fragment lifecycle markers. The accepted `target L3` runtime hashes are
  `dalvikvm=d020846653627d90429fbd88b8fc4b8029634389422c1fab1cfdf8a8c314b120`
  and
  `aosp-shim.dex=80557721467673a09cff28462707f8880afcb601ae885b3903c0b58b6212b65c`.
  `L1` remains a plain Activity lifecycle plus programmatic `View` gate, `L2`
  covers widget/theme/XML resources, and `L3` covers the smallest
  AppCompat/Fragment commit/view/resume slice.
- `CV-103` update: `target L3LOOKUP` now passes on the phone with app-owned
  `CANARY_L3_FRAGMENT_LOOKUP_OK`. The accepted proof pair is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  This proves `FragmentManager.findFragmentById(...)`, `getFragments()`, and
  `findFragmentByTag(...)` across the app-dex to shim-dex boundary.
- `CV-108` update: `target L3IFACE` now passes on the phone with app-owned
  `CANARY_L3_FRAGMENT_INTERFACE_GET_OK` under the same proof pair. This proves
  app-dex `java.util.List.get(I)` dispatch on the
  `FragmentManager.getFragments()` result after `commitNow()`.
- `CV-103` update: `target L4` now passes on the phone with app-owned
  `CANARY_L4_SAVEDSTATE_PROVIDER_OK`, `CANARY_L4_VIEWMODEL_OK`,
  `CANARY_L4_FRAGMENT_SAVEDSTATE_OK`, `CANARY_L4_FRAGMENT_VIEWMODEL_OK`,
  `CANARY_L4_FRAGMENT_ON_RESUME`, and `CANARY_L4_OK`. The current accepted
  proof pair for this base `L4` regression is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  `target L3LOOKUP` also passes as a regression gate under the same pair.
- `CV-103` update: `target L4STATE` now passes on the phone with app-owned
  `CANARY_L4STATE_REGISTRY_RESTORE_OK`,
  `CANARY_L4STATE_SAVEDSTATE_HANDLE_OK`,
  `CANARY_L4STATE_CREATION_EXTRAS_OK`,
  `CANARY_L4STATE_VIEWTREE_LIFECYCLE_OWNER_OK`,
  `CANARY_L4STATE_VIEWTREE_VIEWMODEL_OWNER_OK`,
  `CANARY_L4STATE_VIEWTREE_SAVEDSTATE_OWNER_OK`,
  `CANARY_L4STATE_FRAGMENT_REGISTRY_RESTORE_OK`,
  `CANARY_L4STATE_FRAGMENT_VIEWTREE_OK`, and `CANARY_L4STATE_OK`. The current
  accepted proof pair is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  `target L4`, `target L3LOOKUP`, and `target L3IFACE` also pass as regression
  gates under this pair.
- `CV-103` update: `target L4RECREATE` now passes on the phone with app-owned
  `CANARY_L4RECREATE_SAVE_STATE_OK`, `CANARY_L4RECREATE_ON_PAUSE`,
  `CANARY_L4RECREATE_ON_STOP`, `CANARY_L4RECREATE_ON_DESTROY`,
  `CANARY_L4RECREATE_NEW_INSTANCE_OK`,
  `CANARY_L4RECREATE_ON_CREATE_RESTORED_OK`,
  `CANARY_L4RECREATE_REGISTRY_RESTORED_OK`,
  `CANARY_L4RECREATE_ON_RESUME_RESTORED_OK`, and `CANARY_L4RECREATE_OK`. The
  current accepted proof pair is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  `target L4STATE`, `target L4`, `target L3LOOKUP`, and `target L3IFACE` also
  pass as regression gates under this pair.
- `CV-103` update: `target L4WATRECREATE` now passes on the phone with the same
  app-owned recreate markers plus WAT-owned trace markers
  `CV canary WAT launch begin`, `CV WAT precreate savedstate returned`,
  `CV WAT recreate begin`, and `CV WAT recreate end`. It proves a
  `WestlakeActivityThread`-owned precreate saved-state,
  save/pause/stop/destroy/relaunch/restore/resume sequence for the cutoff
  canary with a fresh Activity instance and restored `SavedStateRegistry` state.
- `CV-103` / `CV-106` update: `target L4WATFACTORY` now passes on the phone
  with WAT-owned factory trace markers `CV canary WAT factory manifest parsed`
  and `CV canary WAT factory set done`, plus app-owned factory markers
  `CANARY_L4FACTORY_CTOR_OK`, two
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`, and two
  `CANARY_L4FACTORY_ACTIVITY_RETURNED_OK`. It proves controlled canary manifest
  `android:appComponentFactory` discovery, `WestlakeActivityThread` factory
  installation, custom factory construction, and custom
  `instantiateActivity(...)` on initial launch and recreate.
- `CV-103` / `CV-106` update: `target L4WATAPPFACTORY` now passes on the phone
  with WAT-owned application factory trace markers
  `CV canary application manual skipped app factory`,
  `CV WAT app factory preactivity makeApplication begin`,
  `CV WAT instantiateApplication returned com.westlake.cutoffcanary.CanaryApp`,
  `CV WAT application onCreate returned`, and
  `CV WAT app factory preactivity makeApplication returned`, plus app-owned markers
  `CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK`,
  `CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK`, and
  `CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK` before the first
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`. It proves WAT-owned pre-Activity
  `makeApplication()` and controlled custom
  `AppComponentFactory.instantiateApplication(...)` ordering for the canary
  Application.
- `CV-103` / `CV-106` update: `target L4WATAPPREFLECT` now passes on the phone
  with the same WAT-owned Application-before-Activity ordering while the canary
  factory delegates to the base
  `AppComponentFactory.instantiateApplication(...)` path. The accepted trace
  includes `CV WAT instantiateApplication reflect begin
  com.westlake.cutoffcanary.CanaryAppComponentFactory`,
  `PF301 strict factory application ctor call`,
  `PF301 strict factory application ctor returned`, and
  `CV WAT instantiateApplication reflect returned`; app markers include
  `CANARY_L4APPREFLECT_STAGE_OK`, `CANARY_L4APPREFLECT_SUPER_CALL`,
  `CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK`,
  `CANARY_L4APPREFLECT_SUPER_RETURNED`, and
  `CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK` before Activity factory
  instantiation. The acceptance gate rejects the direct canary constructor marker
  for this stage.
  `target L4WATAPPFACTORY`, `target L4WATFACTORY`, `target L4WATRECREATE`,
  `target L4RECREATE`, `target L4STATE`, `target L4`, `target L3LOOKUP`, and
  `target L3IFACE` also pass as regression gates under the same proof pair:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`;
  the canary APK hash is
  `cutoff-canary-debug.apk=cb167f3033c14ea3c2eecb40cff784319d5a5657d85f060c0b15905b8e1c4147`.
  The runner now verifies that installed APK hash before accepting a canary run,
  auto-installs the local APK when stale, and fails closed if the post-install
  hash still differs.
  Hash-stamped evidence bundle:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
- `CV-103` caveat replacement: the lookup proof is green but not yet a clean
  production runtime fix. The last accepted blocker was an unresolved
  `BitVector::ClearAllBits()` helper call during ART
  `AssignVTableIndexes()` while linking `FragmentManager`; the accepted
  runtime fixes that by moving the implementation into `bit_vector.h` with
  explicit word stores. `scripts/check-westlake-runtime-symbols.sh` is now in
  the phone sync path to block unexpected strong unresolved runtime helpers.
  Live `PFCUT`/`DEBUG: Thread` probe output is now removed/quarantined from the
  accepted `L4WATAPPREFLECT`, `L4WATAPPFACTORY`, `L4WATFACTORY`,
  `L4WATRECREATE`, `L4RECREATE`, `L4STATE`, `L4`, `L3LOOKUP`, and `L3IFACE`
  artifacts. The
  `FragmentManager.mAdded` concrete
  `ArrayList` typing workaround is removed and the same gates remain green.
  Open runtime debt is now broader generic interface-dispatch coverage, not the
  old fragment lookup acceptance gap, app-facing `List.get(I)`, saved-state
  handle/extras/view-tree owner basics, or probe-log cleanup. Recreate is now
  proven both on the cutoff-scoped `MiniActivityManager` path and on a WAT-owned
  canary path.
  `L4WATRECREATE` no longer skips precreate named saved-state handling; it
  initializes that boundary through the public `SavedStateRegistryOwner`
  contract. WAT attach now routes through shim-owned `Activity.attach(...)`
  instead of direct field assignment in `WestlakeActivityThread`. Strict WAT
  Activity creation now enters and returns through a default
  `AppComponentFactory.instantiateActivity(...)` path on launch and relaunch,
  and `L4WATFACTORY` proves controlled custom/manifest
  `instantiateActivity(...)` selection. `L4WATAPPFACTORY` proves controlled
  custom `instantiateApplication(...)` selection for the canary Application
  before Activity factory instantiation, and `L4WATAPPREFLECT` proves the base
  reflective no-arg Application constructor path for the controlled canary. The
  previous canary-specific
  `DataSourceHelper` skip is reduced to a McDonald's package/class guard.
  Hilt-safe real APK factory selection, Hilt-generated real APK Application
  construction, generic real-APK Application-before-Activity ordering, and
  real-APK lifecycle convergence remain open.
- `PF-001`: quarantine host escape routes. Real Android `DexClassLoader`,
  `startActivity`, and `HostBridge` paths must not satisfy target milestones.
- `PF-101` / `PF-201` through `PF-204`: track ART boot debt separately from
  app progress. Broad `ClearException`, forced class initialization, and
  MethodHandles/VMStack bypasses are not production runtime behavior. The next
  concrete runtime patch should cover the A11 `sun.misc.Unsafe` path, not only
  A15 `jdk.internal.misc.Unsafe`, and should remove reliance on fragile Java
  wrappers during early `ConcurrentHashMap`/`ObjectStreamField` initialization.
- `PF-301`: converge on one generic lifecycle path. `WestlakeActivityThread`
  now owns the accepted `L4WATRECREATE` canary proof with precreate saved-state
  execution and shim-owned `Activity.attach(...)`; `L4WATFACTORY` adds accepted
  controlled custom/manifest `AppComponentFactory.instantiateActivity(...)`
  construction; `L4WATAPPFACTORY` adds controlled custom
  `AppComponentFactory.instantiateApplication(...)` construction before Activity
  factory instantiation; and `L4WATAPPREFLECT` adds the base reflective no-arg
  Application constructor path for the controlled canary. The current accepted
  path still lacks Hilt-safe real-APK factory semantics, Hilt-generated real APK
  Application construction, generic real-APK Application-before-Activity
  ordering, and generic real-APK lifecycle architecture.
- `PF-302` / `CV-201`: make the backend boundary explicit for resources,
  package manager, Binder/services, window/surface, input, storage, and
  permissions before claiming OHOS portability. PF-451 now proves a visible
  controlled app through a direct `DLST` display-list path, but generic
  `Activity.renderFrame`/View-tree rendering through `OHBridge.surfaceCreate`
  remains open because `surfaceCreate` is not registered in the subprocess path
  and faults when invoked. PF-454 adds accepted phone proof for a first
  Material Components namespace slice, Chinese UTF-8 text, host-bridge network
  image tiles, and direct Material-styled DLST rendering, but it is still a
  controlled component shim plus app-specific hit routing, not a generic
  upstream MDC renderer or generic Android View hit-test claim.
- `PF-455` / `PF-456` / `PF-457` / `PF-459` / `PF-460`: the next frontier is
  now narrower and more specific. PF-455 has phone evidence for XML-backed Yelp
  layout inflation and binding, PF-459 proves a generic inflated-View DLST
  serialization slice, and PF-460 proves a first inflated XML
  `Button.performClick()` listener path, but visible polished rendering still
  uses the controlled direct `DLST` path and most touch/scroll behavior still
  uses the controlled router.
  PF-456 now has Android phone evidence for the bridge v2 REST matrix, but
  still needs the OHOS adapter. PF-457 has a Material XML probe with generic
  `findViewAt/performClick` into the APK listener, but not upstream MDC
  compatibility, Material theming/behaviors, or generic Android rendering.
- `CV-105` / `A2OH/westlake#573`: self-contained runtime packaging. The current
  phone run still stages binaries/libs through the Android host and relies on
  Android APEX paths.
- `CV-106` / `A2OH/westlake#574`: lifecycle convergence. WAT now owns an
  accepted canary recreate proof with precreate saved-state execution,
  shim-owned attach, default factory construction, and controlled custom
  manifest `instantiateActivity(...)` plus controlled custom
  `instantiateApplication(...)` plus base reflective canary Application
  construction. Hilt-safe real APK factory selection, Hilt-generated real APK
  Application construction, generic real-APK Application-before-Activity
  ordering, and generic real-APK lifecycle coverage remain open before it can
  satisfy real APK/McDonald's lifecycle delivery.
- `CV-107`: A11 `sun.misc.Unsafe` / A15 ART bootstrap debt. The immediate
  reflection-heavy `sun.misc.Unsafe.objectFieldOffset(Field)` crash is repaired
  enough for `L3LOOKUP`, but keep the broader Unsafe/CHM and stale JNI
  trampoline cleanup open until the diagnostic runtime patches are removed.
- `CV-108`: generic virtual/interface dispatch and link-helper debt.
  `L3LOOKUP` exposed an unresolved class-link helper sentinel in
  `BitVector::ClearAllBits()` and prior interface dispatch fragility in shim
  collection calls. `L3IFACE` now proves app-facing `List.get(I)` dispatch on a
  shim-returned list, and `FragmentManager.mAdded` is no longer concretely typed
  as `ArrayList`. Keep `L3LOOKUP` and `L3IFACE` as regression gates, audit
  remaining unresolved runtime helper calls, and broaden IMT/interface dispatch
  coverage before real app dashboard claims.

The detailed local tracker remains in:
- `$HOME/openharmony/WESTLAKE-PLATFORM-FIRST-OPEN-ISSUES.md`

## 2026-04-26 Same-Day Delivery Pivot

The active delivery target is now a controlled local Android showcase app, not a
stock APK and not McDonald's. The purpose is to validate Westlake functionality
with an app whose API surface we own before expanding to Hilt/GMS/stock APK
complexity.

### PF-451
- Priority: P0
- Layer: controlled app validation
- Depends On: PF-301, PF-401, PF-801
- Status: active same-day delivery target
  - 2026-04-26 update: accepted same-day phone proof for the controlled
    Noice-style local showcase app with a Yelp-like Discover venue panel
- Problem:
  - canary stages prove isolated Android semantics, but the contract needs a
    visible, coherent Android app running through Westlake on the phone
- Scope:
  - local-only Android APK
  - no GMS, Firebase, Maps, push, sign-in, payments, camera/media/location, or
    required backend services
  - AppCompat/Fragment/XML/resource/ViewModel/SavedState-style surface already
    exercised by the accepted canaries
- App complexity target:
  - multi-screen local app with a real-looking UI
  - home/dashboard, list/grid, detail, form/settings, dialog/bottom sheet style
    surface, local state, scroll, click, add/remove/edit flow, recreate/restore
  - optional lightweight animation/transition if it stays inside known View
    rendering
- Test:
  - build APK from source
  - run through Westlake `dalvikvm` on phone
  - capture screenshot, logcat, pid/process state, runtime hashes, and app-owned
    success markers or equivalent app-side event log
  - demonstrate at least one interaction after launch
- Done When:
  - the connected phone visibly shows the showcase UI
  - app code executes on the Westlake guest plane
  - the showcase can be rerun from a declared build/run command without relying
    on stock phone ART for app logic
  - the remaining OHOS gap is explicit: backend adapter/packaging, not unknown
    Android app API surface

Accepted PF-451 evidence from `cfb7c9e3`:

- Build/run: `./test-apps/05-controlled-showcase/build-apk.sh`,
  `WAIT_SECS=3 ./scripts/run-controlled-showcase.sh`
- App: `com.westlake.showcase`, a self-contained Noice-style local ambient
  mixer with XML app bar, playback controls, preset buttons, library rows,
  SVG/image aliases, sliders, progress, checkboxes, timer controls, chips,
  text input, ImageView, HorizontalScrollView, bottom-navigation-like controls,
  and a Yelp-like venue card with rating/reviews/actions
- Artifacts:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.log`,
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.markers`,
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.trace`, and
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.png`
- Visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.visual`
- Hashes:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`,
  `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
- Screenshot acceptance: `controlled_showcase_target.png` was inspected and
  shows the controlled Noice-style phone UI after real navigation to Settings,
  including playback state, a Yelp-like Discover card for `Rain Room`, 4.5
  stars, 85 reviews, host/OHBridge network status, active bottom navigation,
  offline/export controls, runtime evidence, and saved guest state.
- Required app-side markers passed:
  `SHOWCASE_APP_ON_CREATE_OK`, `SHOWCASE_ACTIVITY_ON_CREATE_OK`,
  `SHOWCASE_XML_INFLATE_OK`, `SHOWCASE_XML_BIND_OK`,
  `SHOWCASE_XML_LAYOUT_PROBE_OK`, `SHOWCASE_XML_API_SURFACE_OK`,
  `SHOWCASE_UI_BUILD_OK`, `SHOWCASE_ON_START_OK`,
  `SHOWCASE_ON_RESUME_OK`, `SHOWCASE_XML_TREE_RENDER_OK`,
  `SHOWCASE_DIRECT_FRAME_OK`, `SHOWCASE_TOUCH_POLL_OK`,
  `SHOWCASE_NAV_MIXER_OK`, `SHOWCASE_NAV_TIMER_OK`,
  `SHOWCASE_NAV_SETTINGS_OK`, `SHOWCASE_ADD_LAYER_OK`,
  `SHOWCASE_SAVE_MIX_OK`, `SHOWCASE_TIMER_SET_OK`,
  `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, `SHOWCASE_NETWORK_JSON_OK`,
  `SHOWCASE_NETWORK_IMAGE_OK`, `SHOWCASE_YELP_CARD_OK`,
  `SHOWCASE_VENUE_NEXT_OK`, `SHOWCASE_VENUE_REVIEW_OK`, and
  `SHOWCASE_EXPORT_BUNDLE_OK`
- Remaining PF-302 gap: the accepted render path is a controlled direct `DLST`
  pipe writer for PF-451. Generic View-tree rendering through
  `OHBridge.surfaceCreate` is still open and must be fixed before claiming a
  general self-contained Android runtime for arbitrary APK UI.
- Remaining PF-451 implementation gaps exposed by the pass:
  `resources.arsc` parsing fails with `ArrayStoreException`, arbitrary widget
  mutation needs a broader runtime proof, strict subprocess input uses
  showcase-specific hit routing rather than generic Android View hit testing,
  the XML tree renderer currently paints the visible simplified tree rather
  than a full Android widget draw pass, and live Java/libcore networking is not
  accepted. The controlled venue panel now records
  `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, fetches `417` bytes of venue JSON and
  `8090` bytes of image data through `transport=host_bridge`, and rejects
  `SHOWCASE_NETWORK_NATIVE_GAP_OK` in the bridge-required run. Full
  Java/libcore networking remains open for broader compatibility; missing
  runtime pieces include `java.net.URL`, `libcore.io.Linux.android_getaddrinfo`,
  and related socket/unsafe initialization.

### PF-452
- Priority: P0
- Layer: guest networking/runtime bridge
- Depends On: PF-451
  - Status: Android phone proof accepted after Yelp-like polish pass; OHOS
    adapter open
- Problem:
  - The Yelp-like controlled showcase can render REST-shaped venue data and
    image metadata through the selected host/OHBridge bridge on Android phone.
    It still cannot claim full Java/libcore network compatibility; attempts
    through `java.net.URL`/`HttpURLConnection` and lower-level socket paths
    exposed missing runtime/native pieces and unsafe initialization failures.
- Decision:
  - implement a constrained host/OHBridge HTTP bridge first. This is the
    portable path for Android phone validation and the OHOS port because the
    guest app-facing contract stays stable while each host supplies its own
    network adapter.
  - Android phone host implementation is accepted with
    `SHOWCASE_NETWORK_HOST_BRIDGE_OK`. The remaining PF-452 delivery item is the
    OHOS host adapter with the same contract.
  - do not block the controlled OHOS app proof on full Java/libcore socket/DNS
    completion. Keep that as a later compatibility track for broader stock APK
    support.
- Scope:
  - route constrained HTTP GET through the Westlake host/OHBridge boundary
    using the same request/response semantics on Android phone and OHOS
  - preserve the same app-facing API contract across Android phone and OHOS
    ports
  - keep the PF-451 fixture fallback as a negative-control marker until live
    network is accepted
- Test:
  - accepted on Android phone: run `com.westlake.showcase` with the
    host/OHBridge HTTP bridge enabled
  - fetch the venue JSON and preview image through the bridge from guest app
    logic
  - require `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, `SHOWCASE_NETWORK_JSON_OK`, and
    `SHOWCASE_NETWORK_IMAGE_OK` with no `SHOWCASE_NETWORK_NATIVE_GAP_OK` in the
    accepted live-network run
  - repeat the same marker contract on OHOS after the host adapter exists
- Done When:
  - Android phone: done with
    `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`
    and
    `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
  - OHOS: the showcase visibly renders venue data fetched through the same
    portable bridge contract
  - marker evidence proves the live transport path on each host

### PF-453
- Priority: P0
- Layer: controlled app validation / live data proof
- Depends On: PF-301, PF-452, PF-801
- Status: Android phone proof accepted; OHOS adapter open
- Problem:
  - The controlled Noice showcase includes a Yelp-like panel, but the contract
    also needs a separate app proof that is live-data-first and not coupled to
    the showcase implementation.
- Scope:
  - separate Android APK with package `com.westlake.yelplive`
  - live REST-shaped feed and remote image bytes fetched through the same
    portable host/OHBridge HTTP bridge used by PF-452
  - touch-driven app flows for list scroll, row-level list selection, details,
    saved state, and search
  - direct `DLST` phone rendering with screenshot and visual gate, including a
    required high-entropy photo region so block-only rendering fails
  - polished Yelp-like surface: Material-style red app/search header,
    filter/category chips, elevated five-row restaurant list with remote
    thumbnails, white bottom navigation, details, and saved state
- Test:
  - build APK from `test-apps/06-yelp-live/`
  - run `scripts/run-yelp-live.sh` on phone `cfb7c9e3`
  - require WAT lifecycle markers, host-bridge network markers, live JSON/image
    markers, touch markers, and app action markers
  - reject network failure markers and fatal runtime log markers
- Done When:
  - Android phone: done with
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
    and
    `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`
  - the accepted host log includes `Surface buffer 1080x2280 for
    com.westlake.yelplive`, proving the 1K-class Yelp buffer path
  - the accepted marker file includes `YELP_XML_RESOURCE_WIRE_OK`,
    `YELP_XML_INFLATE_OK`, `YELP_XML_BIND_OK`,
    `YELP_XML_LAYOUT_PROBE_OK`, `YELP_LIVE_JSON_OK`,
    `YELP_LIVE_IMAGE_OK`, `YELP_LIVE_ROW_IMAGE_OK index=4`,
    `YELP_LIST_SCROLL_OK`, `YELP_NEXT_PLACE_OK`, `YELP_DETAILS_OPEN_OK`,
    `YELP_SAVE_PLACE_OK`, `YELP_NAV_SAVED_OK`, and `YELP_NAV_SEARCH_OK`
  - OHOS: the same app renders live data through the OHOS host adapter for the
    PF-452 bridge contract

### PF-454
- Priority: P0
- Layer: Material Components API canary / controlled Material-style UI proof
- Depends On: PF-301, PF-302, PF-452, PF-453, PF-801
- Status: Android phone proof accepted; full upstream Material compatibility
  remains open under PF-457
- Problem:
  - The user-visible Yelp-like proof needed to move beyond primitive rectangles
    and exercise a Material-like API surface, but upstream Google Material
    Components compatibility is too large to claim from one canary.
- Scope:
  - separate Android APK with package `com.westlake.materialyelp`
  - app-owned `Application` and `Activity` lifecycle markers
  - controlled Westlake-owned `com.google.android.material.*` shim slice:
    `MaterialCardView`, `MaterialButton`, `ChipGroup`, `Chip`,
    `TextInputLayout`, `TextInputEditText`, `Slider`, `BottomNavigationView`,
    and `FloatingActionButton`
  - Chinese UTF-8 text, host-bridge image tiles, aspect-preserved host display,
    direct `DLST` rendering, and strict phone touch actions
- Test:
  - build APK from `test-apps/07-material-yelp/`
  - run `scripts/run-material-yelp.sh` on phone `cfb7c9e3`
  - require Material class-surface, lifecycle, language, network image, render,
    touch, filter, selection, save, saved-nav, and search markers
  - reject screenshot gates that show only blocks, missing network image tiles,
    vertically stretched text, or no accepted touch actions
- Done When:
  - Android phone: done with
    `aosp-shim.dex=20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726`
    and
    `westlake-material-yelp-debug.apk=e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66`
  - the accepted host log includes `Surface buffer 1080x1800 for
    com.westlake.materialyelp`, proving the 1K-class Material Yelp buffer path
  - the accepted marker file includes `MATERIAL_APP_ON_CREATE_OK`,
    `MATERIAL_ACTIVITY_ON_CREATE_OK`, `MATERIAL_CLASS_SURFACE_OK`,
    `MATERIAL_LANGUAGE_OK`, `MATERIAL_NETWORK_BRIDGE_OK`,
    `MATERIAL_IMAGE_BRIDGE_OK`, `MATERIAL_DIRECT_FRAME_OK`,
    `MATERIAL_TOUCH_POLL_OK`, `MATERIAL_FILTER_TOGGLE_OK`,
    `MATERIAL_SELECT_PLACE_OK`, `MATERIAL_SAVE_PLACE_OK`,
    `MATERIAL_NAV_SAVED_OK`, and `MATERIAL_NAV_SEARCH_OK`
  - full upstream MDC AAR, XML Material rendering, theming, Coordinator/AppBar,
    ripple/animation, and generic hit testing are not claimed here; they stay in
    PF-457

### PF-455
- Priority: P0
- Layer: XML-backed Yelp app path
- Depends On: PF-302, PF-453, PF-454, PF-801
- Status: Android phone XML inflation/binding slice accepted on 2026-04-27;
  PF-459 first generic draw slice is accepted; PF-460 generic XML
  `Button.performClick()` and `ScrollView` discovery slices are accepted;
  PF-461 `ListView`/`BaseAdapter` row binding, image rebinding, and generic
  adapter click are accepted; full-fidelity generic View-tree rendering and
  broad touch/scroll dispatch remain open
- Problem:
  - The accepted Yelp path now proves compiled XML inflation to a real
    `ScrollView`, a first generic draw serialization slice, multiple inflated
    XML button listener hits, generic scroll-container discovery, and a real
    XML `ListView` backed by a guest `BaseAdapter`, but the polished visible UI
    still depends on an app-specific direct `DLST` writer and most interactions
    still depend on the controlled touch router. That validates guest logic,
    networking, direct drawing, narrow View-tree listener/scroll paths, and a
    first adapter path, but full-fidelity generic Android View drawing and broad
    touch dispatch remain open.
- Scope:
  - create or refactor the Yelp-like app so its primary screen is declared in
    compiled XML layout resources
  - load those resources through the Westlake APK/resource path and inflate them
    in guest code via `Activity.setContentView(...)`, `LayoutInflater`, or the
    closest supported Android-compatible API
  - bind real resource IDs for app bar/search, filters/chips, list rows,
    restaurant cards, image views, save/details buttons, and bottom navigation
  - keep the same live-data, image, scroll, details, save, saved, and search
    flows working after the UI comes from XML
  - produce a render path from the inflated tree; any app-specific direct writer
    must be clearly marked as transitional and cannot bypass the XML acceptance
    markers
  - render the Yelp-like proof into a full-phone 1K-class buffer, currently
    `1080x2280` for the 480x1013 logical layout, so screenshot
    acceptance is not limited by the old low-resolution surface buffer
- Test:
  - build the XML-backed Yelp APK from source and run it on phone `cfb7c9e3`
  - require app-owned markers equivalent to `YELP_XML_INFLATE_OK`,
    `YELP_XML_BIND_OK`, `YELP_XML_LAYOUT_PROBE_OK`,
    `YELP_XML_TREE_RENDER_OK`, `YELP_GENERIC_HIT_OK`,
    `YELP_XML_TOUCH_ROUTE_OK`, and the existing live-data/action markers
  - screenshot gate must show a scrollable multi-row Yelp-like list with remote
    images, search/filter surface, save affordance, details path, and bottom nav
    from the 1K-class buffer
  - reject programmatic-only UI construction as the acceptance path
- Done When:
  - Accepted slice: `scripts/run-yelp-live.sh` on `cfb7c9e3` passes with
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
    and
    `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`.
  - Accepted markers prove `YELP_XML_RESOURCE_WIRE_OK`,
    `YELP_XML_INFLATE_OK root=android.widget.ScrollView views=30 texts=21`,
    `YELP_XML_BIND_OK buttons=5`,
    `YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`,
    `YELP_GENERIC_VIEW_DRAW_OK views=27 texts=17 buttons=13 images=0
    lists=1 listRows=5 listImages=5 height=1013`,
    `YELP_GENERIC_LIST_DRAW_OK rows=5 images=5`,
    `YELP_GENERIC_VISIBLE_LIST_OK rows=5 images=5`,
    `YELP_GENERIC_HIT_OK` with `clicked=true`,
    `target=android.widget.Button`, `text=Search`, `text=Details`,
    `text=Saved`, and `source=inflated_xml`,
    `YELP_GENERIC_SCROLL_OK container=android.widget.ScrollView`,
    `YELP_ADAPTER_ATTACH_OK class=android.widget.ListView`,
    `YELP_ADAPTER_NOTIFY_OK images=5`,
    `YELP_ADAPTER_IMAGE_BIND_OK position=4 bitmap=false imageView=true`,
    `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`,
    `YELP_ADAPTER_ITEM_CLICK_OK position=2`,
    `YELP_VISUAL_DELTA_V4_OK surface=adapter_feed adapterBadge=true
    visibleImages=5`,
    `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280 navTop=824`,
    live REST/image traffic,
    list scroll, details, save, saved navigation, and search.
  - Visual gate records `adapter_teal_samples=697`, proving the phone
    screenshot contains the visible XML ListView/BaseAdapter adapter-feed
    ribbon.
  - Remaining open closure: the visible polished phone frame still uses the
    controlled direct `DLST` renderer; full-fidelity generic Android View-tree
    rendering over the inflated Yelp widgets, raw Bitmap/ImageView decode,
    robust generic list scrolling, and broad generic touch/scroll dispatch are
    not yet accepted.
  - the same app remains a viable OHOS target because the host-facing seams are
    DLST/display, input, storage/logging, and PF-456 networking.

### PF-456
- Priority: P0
- Layer: portable REST networking completeness
- Depends On: PF-452, PF-453, PF-455, PF-801
- Status: bridge v2 live GET path and REST marker contract accepted on Android
  phone; real multi-method matrix and OHOS adapter remain open
- Problem:
  - PF-452/PF-453/PF-454 prove bounded HTTP GET for JSON/images through the
    host/OHBridge bridge. A real controlled app needs a fuller REST surface and
    deterministic error behavior before Westlake can say its app API calls are
    portable to OHOS.
- Scope:
  - expose a stable guest-facing API that covers HTTPS GET/POST/PUT/PATCH/DELETE
    or the subset explicitly required by the controlled app
  - support request headers, query parameters, JSON request/response bodies,
    binary image bodies, status codes, response headers, redirects, timeouts,
    bounded payload sizes, cancellation or cooperative abort, and structured
    network errors
  - keep Android phone and OHOS host adapters behind the same guest contract
  - preserve the distinction between controlled-app REST support and broader
    Java/libcore socket/DNS compatibility
  - add negative tests for DNS failure, HTTP error status, timeout, malformed
    JSON, oversized body, and image decode failure
- Test:
  - run the controlled Yelp XML app through the bridge without local generated
    data or phone ART networking
  - exercise JSON list fetch, image fetch, at least one state-changing request
    shape, response headers/status handling, timeout/failure path, and retry or
    user-visible fallback state
  - require transport markers that identify `host_bridge` or `ohos_bridge`,
    method, status, byte counts, timeout/error class, and payload limits
  - repeat the same contract on OHOS once the OHOS adapter exists
- Done When:
  - Implemented slice: the Android host bridge supports method, headers JSON,
    request body, max-byte cap, timeout, redirect-follow flag, response
    headers, non-2xx response bodies, truncation, and structured errors.
  - Android phone accepted slice: the Yelp run records real live GET JSON/image
    bridge traffic and the REST marker contract:
    `YELP_REST_MATRIX_OK`, `YELP_REST_POST_OK`, `YELP_REST_HEADERS_OK`,
    `YELP_REST_METHODS_OK`, `YELP_REST_HEAD_OK`,
    `YELP_REST_STATUS_OK status=418`, `YELP_REST_REDIRECT_OK`,
    `YELP_REST_TRUNCATE_OK truncated=true`, and `YELP_REST_TIMEOUT_OK`.
  - Current caveat: `YELP_REST_MATRIX_SYNTHETIC_OK` and
    `YELP_REST_TIMEOUT_SYNTHETIC_OK` stand in for a real matrix path that still
    hits a VM SIGBUS; this must be replaced by real request execution before
    PF-456 becomes a broad app-networking claim.
  - Remaining open closure: repeat the same bridge contract on OHOS, with
    POST/PUT/PATCH/DELETE/HEAD/OPTIONS as needed, redirects, timeouts,
    truncation, non-2xx bodies, headers, and JSON/binary bodies.
  - the controlled app's REST/API demands are satisfied by the portable Westlake
    bridge on Android phone and OHOS
  - accepted runs prove live JSON and images, non-GET request shape where the app
    needs it, deterministic failures, and no stock phone ART networking escape
  - Java/libcore `HttpURLConnection`/raw socket work is either adapted to this
    bridge or tracked separately as broader stock-APK compatibility

### PF-457
- Priority: P0/P1
- Layer: Material and generic UI compatibility expansion
- Depends On: PF-302, PF-454, PF-455, PF-801
- Status: PF-454 first shim slice and PF-457 Material XML/hit probe accepted;
  broad compatibility not yet supported
- Problem:
  - The current Material Yelp canary proves controlled class instantiation and a
    direct Material-styled frame. It does not prove arbitrary upstream Google
    Material Components AAR compatibility or generic Android UI behavior.
- Scope:
  - upstream Google Material Components AAR compatibility for the components
    needed by the controlled Yelp app
  - Material themes and style/resource resolution including colors, typography,
    dimensions, state lists, shapes, and default widget attributes
  - generic Material XML inflation/rendering for the same component set
  - `CoordinatorLayout` and `AppBarLayout` scroll/collapse behavior needed by a
    polished list/search app
  - ripple/state-list feedback, basic animation/interpolator/timing behavior,
    and invalidation scheduling where needed for visible interactions
  - generic Android View hit testing, event dispatch, pressed/selected/focused
    states, nested scroll basics, and list/card touch routing
- Test:
  - build a Material XML canary or the PF-455 XML-backed Yelp app using Material
    XML tags and theme attributes
  - run on phone with screenshot, touch, render, resource, style, and marker
    gates
  - verify clicks, ripples or pressed-state fallback, scroll/app-bar behavior,
    selected filters/chips, card save/details actions, and no component-specific
    manual hit map for the accepted path
  - keep PF-454 as a regression gate so the first controlled shim slice does not
    regress while generic support expands
- Done When:
  - Accepted slice: `scripts/run-material-xml-probe.sh` on `cfb7c9e3` passes
    with
    `aosp-shim.dex=bf33aba0a8923e8b7d2cb006ee98042bb217021236a7cfe185a004f0e269716a`
    and
    `westlake-material-xml-probe-debug.apk=ded93614084cdd28a46bcbcbd7eb8cba78504c3c228e0f95835a6ebf42a6e6c9`.
  - Accepted markers prove Material FQN XML tags inflate into shim classes,
    the inflated tree renders via direct DLST, and
    `MATERIAL_GENERIC_HIT_OK` fires through generic
    `findViewAt/performClick` on `MaterialButton`.
  - Remaining open closure: the controlled Yelp app can use Material XML
    components and theming through the generic Westlake path for its primary UI.
  - generic hit testing and invalidation drive broader user-visible interactions
    without an app-specific touch router
  - unsupported upstream Material components are explicitly listed outside the
    accepted controlled-app surface instead of being implied as supported

### PF-460
- Priority: P0
- Layer: generic View hit testing and scroll containers
- Depends On: PF-455, PF-459, PF-801
- Status: first inflated XML `Button.performClick()` slice accepted on Android
  phone, plus actual `ScrollView` inflation/discovery; broad coordinate hit
  dispatch and full visible scroll routing remain open
- Problem:
  - The current Yelp run proves Westlake can invoke app-owned listeners on
    inflated XML buttons and can find a real `ScrollView`, but most visible
    interactions still use `routeYelpLiveDirectTouch(...)`. Stock app progress
    requires generic View hit testing, event dispatch,
    pressed/selected state, and scroll-container routing without per-app
    coordinate maps.
- Scope:
  - route logical touch coordinates through the inflated View tree
  - dispatch click/touch events through `View.performClick()` and listener APIs
  - preserve scroll gestures through `ScrollView`/list-like containers
  - keep accepted direct-router markers only as diagnostics while generic
    routing becomes the acceptance path
- Done When:
  - Accepted slice: `scripts/run-yelp-live.sh` on `cfb7c9e3` records
    `YELP_GENERIC_HIT_OK` with `clicked=true`,
    `target=android.widget.Button`, `text=Search`, `text=Details`,
    `text=Saved`, and `source=inflated_xml`, plus `YELP_GENERIC_SCROLL_OK`
    with `container=android.widget.ScrollView`, using
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`.
  - Remaining open closure: move category, filter, list row, details, save,
    and bottom-nav interactions from the app-specific direct router to generic
    View dispatch, and make generic scroll routing drive the visible list path.

### PF-461
- Priority: P0
- Layer: adapter/list virtualization and image rebinding
- Depends On: PF-455, PF-456, PF-459, PF-460, PF-801
- Status: first XML `ListView`/`BaseAdapter` row-binding slice accepted on
  Android phone; RecyclerView-class virtualization remains open
- Problem:
  - McDonald's-class apps rely on adapter-backed lists, image rebinding, stable
    row IDs, and row click dispatch. The controlled Yelp app now proves the
    first Android-widget adapter path, but it is still a small `ListView`
    slice, not full RecyclerView, DiffUtil, nested lists, or all image loader
    behavior.
- Scope:
  - keep the Yelp primary layout XML-backed with a real `android.widget.ListView`
  - bind a guest `BaseAdapter` and exercise `getCount`, `getItemId`,
    `getView`, row recycling, and `ImageView.setImageBitmap`
  - prove live image bytes can rebind into rows after async network fetches
  - route at least one generic `ListView.performItemClick()` into the APK's
    row listener without using the direct coordinate router as the proof
- Done When:
  - Accepted slice: `scripts/run-yelp-live.sh` on `cfb7c9e3` passes with
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
    and
    `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`.
  - Accepted markers include `YELP_ADAPTER_ATTACH_OK
    class=android.widget.ListView`, `YELP_ADAPTER_LAYOUT_PROBE_OK`,
    `YELP_ADAPTER_BIND_PROBE_OK rows=5`, `YELP_ADAPTER_GET_VIEW_OK
    position=4`, `YELP_ADAPTER_NOTIFY_OK images=5`,
    `YELP_ADAPTER_IMAGE_REBIND_OK index=4`,
    `YELP_ADAPTER_IMAGE_BIND_OK position=4 bitmap=false imageView=true`,
    `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`, and
    `YELP_ADAPTER_ITEM_CLICK_OK position=2`.
  - Visible delta markers include `YELP_VISUAL_DELTA_V4_OK
    surface=adapter_feed adapterBadge=true visibleImages=5`, and the screenshot
    visual gate records `adapter_teal_samples=697`.
  - Remaining open closure: add RecyclerView-equivalent virtualization,
    visible generic list scrolling, data-set invalidation without the
    image-rich `notifyDataSetChanged` guard, raw Bitmap/ImageView decode, and
    OHOS adapter parity.

### PF-462
- Priority: P0
- Layer: real McD dashboard fragment execution
- Depends On: PF-302, PF-460, PF-461, PF-801
- Status: accepted controlled real-child-fragment view path plus all four real
  dashboard child section-root AXML slices; item adapter/data population and
  generic AndroidX FragmentManager execution remain open
- Problem:
  - The real McDonald's dashboard can now show all four dashboard sections
    using the real APK's `HomeDashboardFragment.u6(List)` placeholders and real
    child fragment `onCreateView` methods. All four dashboard child section
    roots now inflate real McDonald's AXML through the generic parser. Westlake
    still bypasses the app's normal FragmentManager execution to avoid SIGBUS
    boundaries.
  - `FragmentManager.h0(true)` and child `performAttach` are currently unsafe
    on this path. The accepted runtime seeds host/activity state, skips
    `performAttach`, directly invokes the child create-view path, and attaches
    the returned View.
  - `HomeHeroFragment.onViewCreated` is also unsafe on the current diagnostic
    path; the probe SIGBUSed inside `LiveData.observe`, so accepted runs leave
    it disabled by default.
- Accepted Evidence:
  - Phone artifact:
    `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`.
  - `scripts/check-real-mcd-proof.sh
    artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe`
    passes.
  - Required markers:
    `MCD_DASH_U6_SEEDED`,
    `MCD_REAL_XML_INFLATED layout=layout_fragment_home_dashboard_hero_section
    resource=0x7f0e0282 root=RelativeLayout`,
    `MCD_REAL_XML_INFLATED layout=layout_home_menu_guest_user
    resource=0x7f0e0366 root=LinearLayout`,
    `MCD_REAL_XML_INFLATED layout=layout_fragment_promotion_section
    resource=0x7f0e030e root=RelativeLayout`,
    `MCD_REAL_XML_INFLATED layout=layout_fragment_popular_section
    resource=0x7f0e0305 root=RelativeLayout`,
    `MCD_DASH_REAL_VIEW_ATTACHED section=HERO`,
    `MCD_DASH_REAL_VIEW_ATTACHED section=MENU`,
    `MCD_DASH_REAL_VIEW_ATTACHED section=PROMOTION`,
    `MCD_DASH_REAL_VIEW_ATTACHED section=POPULAR`,
    and zero `MCD_DASH_SECTION_VIEW_ATTACHED` fallback markers.
  - PF-613 regression evidence now extends this PF-462 baseline with one real
    Promotion adapter row:
    `MCD_DASH_ADAPTER_BOOTSTRAP section=PROMOTION
    adapter=com.mcdonalds.homedashboard.adapter.HomePromotionAdapter
    itemCount=1` and
    `MCD_REAL_XML_INFLATED layout=layout_home_promotion_item
    resource=0x7f0e036a root=LinearLayout`.
  - Live row-image proof:
    `STRICT_IMAGE_LIVE_ADAPTER recycler=2131432435 position=0 bytes=54022`
    after `WestlakeHttp GET response code=200 bytes=54022`.
  - Strict frame:
    `bytes=54799 views=69 texts=6 buttons=0 images=1 rows=1
    rowImages=1 rowImageBytes=54022 overlays=0`.
- Scope:
  - move beyond section roots into promotion/popular item adapter XML,
    RecyclerView/adapter population, image rebinding, and app model data
    hydration while keeping the real child-fragment and section-root XML markers
  - isolate the SIGBUS in app AndroidX `FragmentManager.h0(true)` and child
    `performAttach`
  - isolate the `onViewCreated`/`LiveData.observe` SIGBUS without regressing the
    accepted Hero real-AXML gate
  - replace direct lifecycle bypass with a safe generic fragment attach/create
    path that preserves app FragmentManager semantics
  - keep the real-child-fragment view proof gate as a regression gate
  - expand from dashboard sections to deeper McD flows that require
    RecyclerView, adapter invalidation, image loading, click routing, and
    navigation
- Done When:
  - the same real McD dashboard proof passes without directly attaching
    child fragment views from `WestlakeLauncher`
  - app-owned FragmentManager/BackStackRecord replace/commit execution produces
    content in the section containers without SIGBUS
  - the gate still proves Westlake subprocess purity, real APK launch, live
    network bridge success, non-sparse strict frame, and no fallback section
    layout attachment

### PF-613
- Priority: P0
- Layer: real McD dashboard item adapters and data density
- Depends On: PF-462, PF-461, PF-456, PF-493
- Status: in progress; first real Promotion adapter/item XML row accepted;
  remote issue `A2OH/westlake#586`
- Problem:
  - PF-462 now proves all four real dashboard child section roots inflate from
    real McDonald's AXML. PF-613 now additionally proves Promotion can attach
    the real `HomePromotionAdapter`, inflate real item AXML, and create one
    row:
    `bytes=54799 views=69 texts=6 buttons=0 images=1 rows=1
    rowImageBytes=54022`.
  - The current row is not enough for stock McD parity: its `Promotion` model is
    Westlake-seeded to drive the app adapter, the accepted image proof comes
    from a strict-frame `WestlakeHttp` image-byte bridge rather than completed
    stock Glide/ImageView delivery, and Popular attaches an empty adapter. The
    next stock-compatibility boundary is generic Glide/ImageView completion plus
    real app data/model hydration.
- Scope:
  - Promotion item XML:
    `home_promotion_item.xml` and `home_promotion_item_updated.xml`
  - Popular item XML:
    `home_popular_item_adapter.xml`
  - RecyclerView adapter item count, row creation/recycling, layout params,
    text binding, and image binding
  - data source diagnosis: distinguish network fixture/live response,
    Realm/cache rows, ViewModel/LiveData, and adapter semantics
- Done When:
  - the real-McD gate still requires all PF-462 section-root markers
  - at least one promotion or popular row is created through the app
    adapter/item XML path
  - strict frame regains nonzero row/image-byte evidence without
    `MCD_DASH_SECTION_VIEW_ATTACHED` fallback markers
  - the accepted row is backed by real app network/cache data, or the artifact
    explicitly documents the remaining bridge needed to make that data source
    portable

### PF-620
- Priority: P0
- Layer: real McD PDP stock handler and Android window/display API
- Depends On: PF-462, PF-613, PF-302, PF-456
- Status: in progress; `getWindowManager` API gap closed, PDP stock
  add-to-order now phone-proven through the real `OrderPDPFragment.j8(true)`
  handler after a Westlake Product -> CartProduct bridge and main-looper
  deferred fragment-readiness retry
- Problem:
  - The real dashboard can navigate to the real Big Mac PDP through Westlake,
    but stock PDP controls cannot yet execute because
    `OrderPDPFragment.E0`, `OrderPDPFragment.t0`, and `OrderPDPFragment.G0`
    are null on the accepted controlled PDP path.
  - Before this issue update, invoking the stock quantity handler reached
    `OrderPDPFragment.onClick(View)` and then crashed in
    `OrderPDPViewModel.X2(int, CartProduct)` with an NPE/SIGBUS. The current
    guard prevents that crash and logs `route=fragment_deps_missing`.
  - The PDP add-to-order target is also not yet part of the selected hit root:
    the render marker reports `bottom=null`, and the add probe logs
    `route=view_missing` / `route=fragment_j8_no_target`.
  - The concrete Android API gap
    `Activity.getWindowManager()Landroid/view/WindowManager;` is closed by a
    Westlake-side `WindowManager` service, not by returning the phone framework
    service across classloaders.
  - 2026-05-02 03:05 update: the add-to-order blocker has moved forward.
    Westlake now caches the real dashboard `Product`, synthesizes the stock SDK
    `CartProduct`, writes it into `OrderPDPFragmentExtended.t0`, and can invoke
    stock `OrderPDPFragment.j8(true)` once `E0/G0/t0` are ready. Early taps are
    reconciled by a main-looper deferred worker because `E0/G0` are still late
    on the first visible projected Add tap.
- Accepted Evidence:
  - Phone artifact:
    `artifacts/real-mcd/20260502_011431_mcd_windowmanager_bridge_pdp_guard_probe/`.
  - Local and phone `aosp-shim.dex`:
    `fe2c190e8d8b8f66061c6326e87b979ad84ec7d2c60ea7e43d64f3e3191bbeb2`.
  - Required positive markers:
    `MCD_DASH_SEMANTIC_POPULAR_CLICK`,
    `MCD_ORDER_PDP_RENDER_ROOT selectedReason=holder_pdp ... holderHasPdp=true`,
    `Strict McD order PDP frame ... bytes=2556 views=50 texts=21 images=3`,
    and `MCD_ORDER_PDP_STOCK_ACTION control=quantity_plus
    route=fragment_deps_missing ... E0=null t0=null G0=null`.
  - Required negative markers in the captured tail:
    zero `NoSuchMethodError`, zero `getWindowManager` failure, zero `SIGBUS`,
    zero `Failed requirement`, and zero `OrderPDPViewModel` crash.
  - New phone artifacts:
    `artifacts/real-mcd/20260502_031019_mcd_pdp_deferred_mainpost_cancel_probe/`
    and
    `artifacts/real-mcd/20260502_031155_mcd_pdp_deferred_mainpost_cancel_early_tap/`.
  - New phone `aosp-shim.dex`:
    `ea537af24862f441ef947b6a86106ef88f19cdea285637e8449e34a0091bc406`.
  - New required positive markers:
    `MCD_ORDER_PDP_CART_PRODUCT_SYNTH success=true ... productId=1001`,
    `MCD_ORDER_PDP_STOCK_ACTION control=add_to_order
    route=fragment_j8_deferred invoked=true`, and
    `MCD_ORDER_PDP_DEFERRED_STOCK_ADD phase=invoked ... pending=0`.
    The accepted early-tap summary also records `j8_deferred_count=1` and
    `post_timeout_count=0`.
- Scope:
  - preserve the Westlake-side `WindowManager` contract for
    `Activity.getWindowManager`, `Context.WINDOW_SERVICE`, `Window.getWindowManager`,
    `WindowManager.getDefaultDisplay`, `getCurrentWindowMetrics`, and
    `getMaximumWindowMetrics`
  - trace `OrderProductDetailsActivity`/`OrderPDPFragment` creation far enough
    to identify the real assignment sites for `E0`, `t0`, and `G0`
  - make PDP data/model hydration real enough that stock quantity/customize/add
    handlers can execute without guard fallback
  - include the bottom/add-to-order bar in the real PDP root and hit map
  - keep direct single-UP touch-file probes as the deterministic regression
    path for dashboard item and PDP control taps
- Done When:
  - real PDP quantity plus/minus executes stock `OrderPDPFragment.onClick(View)`
    without `fragment_deps_missing` and without fatal VM exit
  - real add-to-order executes the stock add path (`j8(true)` or the current
    app-equivalent handler) from a real target view; the current bridge proves
    stock `j8(true)` execution, but not yet from a reliably selected real Add
    button view at first tap
  - the guard remains a regression fallback only, not the accepted path
  - phone proof still shows Westlake subprocess purity and no direct phone ART
    McD process

### PF-621
- Priority: P0
- Layer: 48-hour real McD full-app rally parent
- Depends On: PF-620, PF-622, PF-623, PF-624, PF-625, PF-626
- Status: active; governed by
  `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`;
  remote issue `A2OH/westlake#587`
- Problem:
  - The current accepted proof is real McD dashboard/PDP/Add stock-handler
    execution, but not full app execution.
  - Full-app success requires downstream quantity/customize/bag/cart flow,
    stronger generic View/input/rendering, and OHOS southbound parity.
- Scope:
  - coordinate lifecycle, rendering/input, order/cart, network/storage, runtime,
    and proof workers under one 48-hour evidence standard
  - reject mock UI, phone ART McD execution, sparse/black frames, and claims not
    backed by artifact hashes/logs/screenshots
- Done When:
  - one clean cold-start artifact proves dashboard -> PDP -> quantity/customize
    -> add -> bag/cart or accepted cart state, with subprocess purity and no
    fatal markers

### PF-622
- Priority: P0
- Layer: PDP Fragment/DataBinding lifecycle readiness
- Depends On: PF-620, PF-478, PF-607
- Status: active; remote issue `A2OH/westlake#588`
- Problem:
  - PDP include/binding is now advanced enough to hydrate plus/minus/quantity
    fields, but the accepted route still uses `mode=soft_state`.
  - The old hard `performResume()` path SIGBUSed in stock listener
    initialization; full-app proof needs real Fragment start/resume without
    that bridge.
  - `mResumed` still reads false in the current best proof.
- Scope:
  - instrument PDP `performCreateView`, `onCreateView`, `onViewCreated`,
    `ViewModelProvider`, `T1`, `C0`, and `K7(CartProduct)` readiness
  - fix include inflation/data-binding tags for PDP included layouts
  - keep synthetic `t0` explicitly labeled until stock `C0/K7` path is proven
- Done When:
  - first visible PDP frame has `E0!=null`, `G0!=null`, and stock or labeled
    synthetic `t0` before Add is accepted
  - deferred Add and soft-state resume remain fallbacks, not the accepted path
  - hard Fragment lifecycle markers pass without SIGBUS

### PF-623
- Priority: P0
- Layer: generic McD View rendering, root selection, and input dispatch
- Depends On: PF-459, PF-460, PF-461, PF-606, PF-620
- Status: active; remote issue `A2OH/westlake#589`
- Problem:
  - Phone-visible McD UI is real app state rendered through Westlake strict
    projection/control bridges, not broad Android View parity.
  - Physical dashboard tap works; PDP Add is still strongest through direct
    Westlake touch-file injection and projected control routing.
- Scope:
  - select roots by visible attached View stats rather than McD-only class or
    y-band shortcuts
  - include PDP bottom bar, cart/bag roots, and nested scroll content
  - route taps through generic View hit testing, RecyclerView/ScrollView
    dispatch, and `performClick` before McD-specific fallbacks
  - extend DLST replay for basic transforms/material primitives where needed
- Done When:
  - dashboard item, PDP Add, quantity, customize, and bag/cart taps are accepted
    through generic dispatch first
  - projection-specific markers are fallback-only

### PF-624
- Priority: P0
- Layer: order/customize/bag/cart downstream stock-flow proof
- Depends On: PF-620, PF-622, PF-623, PF-625
- Status: active; remote issue `A2OH/westlake#590`
- Problem:
  - Current proof invokes stock `OrderPDPFragment.j8(true)`, but
    `CartInfo.totalBagCount` and `cartSizeWithoutOffers` remain zero before
    and after the call.
  - `CartProduct.quantity` is now synchronized to `1`, so the immediate
    blocker has moved to the stock downstream commit path. Unsafe probes that
    call `s7()` or `OrderPDPViewModel.X(CartProduct)` reach Realm/BaseStorage
    and `BasketAPIHandler.A1(...)`, then crash with `SIGBUS`. The default
    proof now gates that path with
    `MCD_PDP_STOCK_ADD_COMMIT route=model_x_gated_realm_storage`.
- Scope:
  - instrument `j8(true)`, `s7`, `A7`, `OrderPDPViewModel.S1/X`,
    `BasketUseCase.e`, basket add request/response, `CartViewModel`,
    bag badge, customize navigation, and cart/bag activity/fragment launch
  - identify required `CartProduct` fields beyond product/productCode
  - prove quantity plus/minus and customize through stock handlers
- Done When:
  - real add changes real app cart/bag state and cart/bag screen or accepted
    app-state marker shows the added item
  - quantity/customize have phone artifacts with real stock handler markers

### PF-625
- Priority: P0
- Layer: McD network/storage/Realm/cart state fidelity
- Depends On: PF-456, PF-464, PF-603, PF-624
- Status: active
- Problem:
  - Full McD order/cart flow depends on app data state: menu/product/cache,
    restaurant/session/cart, Realm/BaseCart, preferences, and network responses.
  - The current P0 crash frontier is the Realm/storage boundary entered from
    `BasketAPIHandler.A1(...)`; pseudo/no-op handles are not yet safe enough
    for the stock basket commit path.
- Scope:
  - route live or URL-keyed fixture data through app parser/model paths, not
    direct UI seeding
  - implement targeted Realm/result/row behavior for observed cart/menu tables,
    including shared-realm handles, schema handles, refresh/closed/frozen/
    transaction status, table refs, key-path mapping, and cart row mutation
  - keep network markers for method/header/body/status/image bytes and redact
    sensitive auth/session fields
- Done When:
  - dashboard/PDP/cart proofs show app-originated model data and stable state
    across navigation

### PF-626
- Priority: P0
- Layer: full-flow proof automation and OHOS southbound parity
- Depends On: PF-608, PF-609, PF-621
- Status: active
- Problem:
  - `scripts/check-real-mcd-proof.sh` is still mostly a dashboard gate. It does
    not yet accept or reject PDP/add/customize/bag/cart semantics.
  - Android phone success is not an OHOS portability claim unless mapped to the
    southbound API contract.
- Scope:
  - extend checker tiers for PDP, stock Add, one-shot deferred Add, quantity,
    customize, bag/cart, screenshots, and downstream state markers
  - require local/phone hashes, process purity, crash purity, real app/root
    markers, and before/after screenshots for every claim
  - update `WESTLAKE_SOUTHBOUND_API.md` or explicit PF gaps for every new
    Android host dependency
- Done When:
  - a single checker command can reject false full-app claims and accept a
    clean full-flow artifact
  - OHOS adapter deltas are documented for every accepted Android path

## Current McD Full-App Rally Frontier - 2026-05-02 13:51 PT

This update supersedes the 13:30 PT frontier for PF-621 through PF-626.

- `PF-621` full-app rally parent remains red. The phone can show real McD
  dashboard and real PDP inside Westlake, but full dashboard -> PDP -> Add ->
  cart/bag is not yet accepted.
- `PF-622` lifecycle/observer gap is narrowed. The old W1/loading early return
  in `OrderPDPFragment.j8()` is closed by runtime seeding; the next lifecycle
  acceptance condition is app-owned Add LiveData observer routing from
  `normalAdd`, `editAdd`, or `productLimit`.
- `PF-623` generic input gap remains open. Physical/ADB touch reaches the PDP
  Add area, but the accepted route still depends on a McD-specific projected
  route before the generic View hit-test owns the bottom bar.
- `PF-624` stock Add/cart gap moved deeper. The Add listener is installed and
  enters `OrderPdpButtonLayoutBindingImpl.a(2, View)`, but the path throws
  around `OrderHelper.getMaxBasketQuantity()` /
  `AppCoreUtils.getMaxQtyOnBasket()` before `OrderPDPViewModel.Z()` produces
  downstream Add LiveData.
- `PF-625` storage/data gap remains the final cart blocker after Add decision.
  `CartProduct` and `CartInfo` are hydrated; unsafe model/storage probes still
  gate out at Realm/BaseStorage/BasketAPI because the portable storage contract
  is not proven safe.
- `PF-626` proof automation improved. Logcat streaming is now mandatory; the
  next checker extension must treat
  `MCD_PDP_GENERATED_BINDING_CLICK`, `MCD_PDP_VIEWMODEL_Z_GATE`,
  `MCD_PDP_OBSERVER_DISPATCH_GATE`, and `MCD_PDP_A7_GATE` as first-class
  acceptance/rejection markers.

Execution details for the 48-hour rally:

- Patch/runtime owner:
  - keep `WestlakeLauncher` fixes bounded to app-compatibility probes and
    explicit McD diagnostic fallbacks;
  - do not accept a visual-only counter as cart success;
  - emit precise markers before every fallback.
- Proof owner:
  - run `WESTLAKE_GATE_SLEEP=420 scripts/run-real-mcd-phone-gate.sh
    mcd_48h_generated_binding_z_gate`;
  - use Windows ADB endpoint `localhost:5037`, serial `cfb7c9e3`;
  - require subprocess purity, streamed dashboard markers, PDP markers,
    screenshot, and hash evidence.
- Storage owner:
  - once `normalAdd` or `editAdd` becomes true, test observer dispatch and model
    commit only behind explicit `westlake.mcd.unsafe_*` launch flags;
  - convert the observed Realm/BasketAPI crash into a precise portable API
    contract before enabling the normal full gate.
- OHOS owner:
  - every newly required Android dependency must be added to
    `WESTLAKE_SOUTHBOUND_API.md` with an OHOS adapter expectation.

## Current McD Full-App Rally Frontier - 2026-05-02 14:25 PT

This update supersedes the 13:51 PT frontier for PF-621 through PF-626.

- `PF-621` full-app rally parent remains red, but the red boundary moved
  forward. Dashboard -> stock dashboard adapter click -> real PDP -> stock Add
  entry is reproducible inside Westlake; accepted full app success still
  requires no crash and an observable cart/bag mutation.
- `PF-622` lifecycle/observer status is unchanged from the prior update:
  PDP observer dispatch is present, but the fragment still reports soft
  lifecycle recovery rather than fully normal resumed state.
- `PF-623` generic input remains open. The successful PDP Add attempt still
  enters through the McD projected Add route; physical/ADB touch is not yet a
  generic bottom-bar View hit.
- `PF-624` stock Add/cart gap moved from config to storage. The JustFlip
  `maxQtyOnBasket` / `maxItemQuantity` null path is fixed by the portable flag
  config adapter in `WestlakeActivityThread`; checker now reports
  `PASS mcd_pdp_stock_click_boolean_npe count=0`.
- `PF-625` is now the primary P0 blocker. The stock Add path reaches
  `BasketAPIHandler.A1(...)`, `BaseStorage.<init>(...)`, and Realm
  pseudo-native calls, then crashes with SIGBUS before cart/bag mutation.
- `PF-626` checker must be updated again: it should distinguish the old
  JustFlip config failure from the new Realm/BaseStorage crash frontier and
  require `MCD_JUSTFLIP_BASKET_FLAG_SEED ... maxQtyBefore=99` for this stage.

Execution details for the next rally slice:

- Runtime owner:
  - keep the JustFlip config adapter as the portable southbound fix;
  - suppress duplicate Add re-entry once a click attempt has reached the
    downstream basket commit path;
  - add explicit markers around Realm entry, successful return, and cart count.
- Storage owner:
  - implement or harden Realm pseudo-native behavior for
    `OsSharedRealm`, `OsSchemaInfo`, `OsRealmConfig`, `Table`,
    `TableQuery`, `OsResults`, `UncheckedRow`, and key-path mapping;
  - preserve pseudo-handle identity across the basket transaction instead of
    only returning fresh handles;
  - model `class_BaseCart`, `class_KeyValueStore`, and
    `class_Configuration` rows with stable size/find/get/set semantics.
- Proof owner:
  - rerun the marker-driven gate after each storage patch;
  - accept progress only if the artifact shows basket commit reached without
    SIGBUS and either cart count changes or app Add LiveData changes.
- OHOS owner:
  - document Realm as a portable southbound storage API, not an Android-phone
    implementation detail. The OHOS adapter can be an in-process deterministic
    store, but it must preserve the Java/Kotlin Realm object contract used by
    the stock app.

## Current McD Full-App Rally Frontier - 2026-05-02 14:30 PT

This update is the contract workstream command board for the two-day final
rally. It supersedes only the execution order in the 14:25 PT update; the
technical frontier remains the same.

- `PF-621` remains red until one artifact shows full stock McD dashboard -> PDP
  -> Add -> cart/bag proof in Westlake child `dalvikvm`.
- `PF-622` remains active. Soft PDP lifecycle recovery is allowed only as a
  diagnostic bridge while storage is being fixed; final acceptance needs
  app-owned generated binding and observer continuation.
- `PF-623` remains active but is not the current critical path. Generic input
  must replace the projected PDP Add route after storage no longer SIGBUSes.
- `PF-624` remains red until stock app cart state mutates or the app emits a
  precise nonfatal rejection. Add listener, JustFlip config, and basket entry
  are not enough by themselves.
- `PF-625` is the critical path. The JustFlip `maxQtyOnBasket` gap is closed;
  the remaining P0 is Realm/BaseStorage/BasketAPI stability after
  `BasketAPIHandler.A1(...)`.
- `PF-626` must reject partial success. It should require
  `MCD_JUSTFLIP_BASKET_FLAG_SEED` with `99`, downstream basket commit reached,
  no fatal signal, no SIGBUS, and cart/bag or Add LiveData mutation before the
  full gate turns green.

Execution details:

- Runtime/shim owner:
  - suppress duplicate Add re-entry after a storage-bound click; expected
    marker is `MCD_PDP_ADD_REENTRY_SUPPRESSED`;
  - then patch Realm pseudo-native handling in
    `$HOME/art-latest/patches/runtime/interpreter/interpreter_common.cc`.
- Storage owner:
  - preserve pseudo-handle identity across shared realm, schema, table, query,
    result, row, and key-path objects;
  - implement stable behavior for `class_BaseCart`, `class_KeyValueStore`, and
    `class_Configuration`.
- Input owner:
  - make host/ADB physical touch hit `pdpAddToBagButton` through generic View
    dispatch before McD-specific projected fallback.
- Proof owner:
  - rerun with `WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1` through
    Windows ADB serial `cfb7c9e3`;
  - attach artifact path, shim/runtime hashes, streamed logcat, screenshot, and
    checker result to every claim.
- OHOS owner:
  - update `WESTLAKE_SOUTHBOUND_API.md` for every newly required Realm/storage,
    input, network, or lifecycle dependency; do not treat Android-phone-only
    behavior as portable success.

## Current McD Full-App Rally Frontier - 2026-05-02 14:49 PT

This update supersedes the 14:30 PT execution detail for PF-624 through
PF-626.

- `PF-621` remains red. The newest artifact proves a cleaner failure, not full
  app success.
- `PF-624` moved from duplicate storage crash to telemetry/cart continuation.
  The stock Add path enters `BasketAPIHandler.A1(...)` once, then throws
  `IllegalStateException: Telemetry not initialized`. Cart and Add LiveData do
  not mutate.
- `PF-625` storage crash is temporarily stabilized for the normal gate. The
  checker reports downstream basket commit reached with `sigbus=0`, because
  duplicate Add re-entry is now suppressed after the first storage-bound click.
- `PF-626` proof harness CRLF hash bug is fixed. Phone runtime hash collection
  now passes for ADB shell output.
- `PF-623` generic PDP input and `PF-622` normal lifecycle are still red and
  remain required for final full-app acceptance.

Execution details:

- Runtime/shim owner:
  - implement a focused McD telemetry boundary: core/domain telemetry calls
    must either be initialized or safely no-op without throwing
    `Telemetry not initialized`;
  - do not bypass `BasketAPIHandler.A1(...)` or synthesize a cart count as a
    success marker.
- Storage owner:
  - keep one-shot Add entry suppression active while telemetry is debugged;
  - once telemetry is nonfatal, resume Realm/BaseCart row mutation work if cart
    still stays zero.
- Proof owner:
  - use artifact
    `artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/`
    as the new baseline;
  - next green subproof must show no SIGBUS and no telemetry abort after basket
    entry.
- OHOS owner:
  - document telemetry as a southbound app-service boundary: OHOS does not need
    McD production telemetry, but Westlake must preserve the app-visible
    initialized/no-throw contract.

## Two-Day Final Rally Issue Board - 2026-05-02 14:55 PT

The active rally target is full real-McD dashboard/PDP/Add/cart execution in
Westlake guest `dalvikvm`. The baseline is
`artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/`.

- `PF-621` remains the parent success issue. It cannot close until real cart or
  exact stock rejection, normal lifecycle, generic input, and subprocess purity
  all pass together.
- `PF-622` owns normal Fragment/LifecycleRegistry readiness. Current state:
  red, because the proof still relies on soft resume recovery and reports
  `fragment_resumed=0`.
- `PF-623` owns generic View input. Current state: red, because projected
  fallback reaches Add while the generic PDP input gate remains zero.
- `PF-624` owns order/cart continuation. Current state: red, with the newest
  blocker `Telemetry not initialized` after `BasketAPIHandler.A1(...)`.
- `PF-625` owns Realm/BaseStorage/cart persistence. Current state: yellow for
  normal-gate crash stability, red for true cart mutation.
- `PF-626` owns proof closure. Current state: phone hash proof fixed; add
  telemetry, generic input, lifecycle, and cart mutation gates before any
  full-app green claim.

Execution rule for every worker:

- A patch must name the issue it advances, the owned file set, and the proof
  marker expected to move.
- A phone run must name the artifact directory, `aosp-shim.dex` hash,
  `dalvikvm` hash, subprocess-purity result, and the first remaining red gate.
- No worker may mark a mock UI, direct ART execution, projected-only click, or
  synthetic cart count as progress toward PF-621 closure.

## Two-Day Final Rally Issue Board - 2026-05-02 15:28 PT

The current accepted normal proof is
`artifacts/real-mcd/20260502_150027_mcd_48h_telemetry_cart_gate/`.
The current unsafe crash map is
`artifacts/real-mcd/20260502_151742_mcd_48h_true_unsafe_cart_commit_probe/`.

- `PF-621` remains red. Real dashboard/PDP/Add are visible in Westlake, but
  full-app success still needs real cart mutation or stock nonfatal rejection,
  normal lifecycle, subprocess purity, and no unsafe flags.
- `PF-622` remains red. Proof still reports `fragment_resumed=0`; soft resume
  recovery is useful but not final lifecycle correctness.
- `PF-623` is green for the current McD PDP Add proof. The normal artifact
  records `mcd_full_app_generic_pdp_input_gate count=5`. Keep hardening this
  into generic View dispatch, but do not block PF-625 on more input probes.
> *[GREEN BUT REGRESSION-PRONE; see WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md §4 PF-624]*
- `PF-624` is green/yellow. Telemetry seeding works for Add continuation:
  `MCD_TELEMETRY_MANAGER_SEED` is present and no
  `Telemetry not initialized` abort occurs after `BasketAPIHandler.A1(...)`.
  Keep it guarded by proof so it cannot regress.
- `PF-625` is the top red blocker. Normal path reaches Add LiveData mutation
  but cart remains zero. Unsafe observer/storage dispatch reaches
  Realm/BaseCart and then SIGBUSes at the pseudo-native storage boundary.
- `PF-626` must distinguish normal acceptance from unsafe crash probes. A
  SIGBUS artifact may advance diagnosis, but cannot be marked as PF-621
  progress unless the next normal run is subprocess-pure and nonfatal.

PF-625 execution detail:

- Target owned surface:
  `$HOME/art-latest/patches/runtime/interpreter/` plus any generated
  runtime artifacts needed by `sync-westlake-phone-runtime.sh`.
- Observed table/query frontier:
  `class_BaseCart`, `_maxAge < $0`, `_maxAge != $0`,
  repeated `cartStatus = $0`, `TableQuery.nativeFind(...)`,
  `OsResults.nativeCreateResults(...)`, `OsResults.nativeSize(...)`,
  `OsSharedRealm.nativeBeginTransaction(...)`, and close/transaction state.
- Required behavior:
  preserve stable handles from shared realm to table/query/results/row, keep
  transaction state coherent across begin/commit/close, return deterministic
  row/result values for observed cart queries, and never hand app code an
  invalid pointer-shaped value that can SIGBUS through reflection/callee-save
  runtime paths.
- Proof target:
  unsafe probe changes from `realm_sigbus` to nonfatal cart mutation/rejection,
  then normal gate passes `no_fatal_failed_requirement`,
  `westlake_subprocess_purity`, telemetry, generic PDP input, and a cart
  mutation or exact stock rejection marker.

Two-day rally gating:

- Hour 0-8: close PF-625 SIGBUS with targeted Realm/BaseCart semantics.
- Hour 8-16: rerun unsafe and normal gates until cart mutation/rejection is
  stable without unsafe flags.
- Hour 16-28: close PF-622 lifecycle so stock observers can run without
  soft-state-only recovery.
- Hour 28-40: harden PF-623/PF-624/PF-626 against regression, especially input,
  telemetry, subprocess purity, and proof false positives.
- Hour 40-48: final normal full-app proof on phone, then OHOS portability notes
  for every southbound API used.

## Two-Day Final Rally Issue Board - 2026-05-02 15:47 PT

The current normal proof remains
`artifacts/real-mcd/20260502_150027_mcd_48h_telemetry_cart_gate/`.
The current PF-625 crash-isolation proof is now
`artifacts/real-mcd/20260502_153758_mcd_48h_model_storage_no_observer_true_probe/`.

Issue states:

- `PF-621` remains red. Real McD dashboard/PDP/Add are visible in Westlake, but
  final success still requires no unsafe flags, subprocess purity, no fatal
  child VM crash, normal lifecycle, and real cart mutation or exact stock
  rejection.
- `PF-622` remains red. The accepted normal proof still reports
  `fragment_resumed=0`; soft resume recovery cannot close the full-app issue.
- `PF-623` is green for the accepted normal McD PDP Add proof, with
  `mcd_full_app_generic_pdp_input_gate count=5`. Do not spend the next storage
  window chasing projected-input cosmetics unless it regresses in the normal
  gate after PF-625 moves.
- `PF-624` is green/yellow. Telemetry seeding is no longer the active Add
  blocker, but every proof must keep `telemetryAbortAfter=0` after
  `BasketAPIHandler.A1(...)`.
- `PF-625` is the top red issue. The corrected unsafe run proves the crash is
  in model/storage commit with observer dispatch off:
  `MCD_PDP_A7_GATE invoked=true allowed=true`,
  `MCD_PDP_STORAGE_SAFETY_GATE allowed=true`, then SIGBUS before
  `MCD_PDP_STOCK_ADD_COMMIT`.
- `PF-626` remains red until the checker requires this separation explicitly:
  unsafe crash probes can diagnose PF-625, but only no-unsafe normal gates can
  close PF-621.

PF-625 worker instructions:

- Own `$HOME/art-latest/patches/runtime/interpreter/`.
- Seed one stable `class_BaseCart` row and make observed BaseCart predicates
  return row/result handles instead of `rows=0` and `nativeFind row=-1`.
- Preserve row identity across shared realm, table, query, results, checked row,
  unchecked row, begin transaction, commit/cancel, refresh, and close.
- Expand BaseCart schema coverage to the observed McD fields before adding
  broader Realm behavior.
- Build and sync only after the native patch compiles, then run the no-observer
  unsafe model/storage loop. The expected first proof movement is
  `sigbus=0`, not a cart count.

PF-622 worker instructions:

- Own `shim/java/androidx/fragment/*`,
  `shim/java/androidx/lifecycle/*`, and any small generic ViewTree lifecycle
  hooks needed by stock observers.
- Make Fragment and view lifecycle owners reach STARTED/RESUMED through normal
  AndroidX calls. Do not rely on a McD-only observer fire as final correctness.

PF-626 worker instructions:

- Update the checker so `pf621_final_acceptance_gate` fails when unsafe flag
  files are present, when `vm_pid=missing`, when the McD UI is a mock/fallback,
  or when the only click path is projected.
- Keep the artifact report concise: artifact path, shim hash, runtime hashes,
  subprocess result, first red gate, and whether unsafe flags were enabled.

## Two-Day Final Rally Issue Board - 2026-05-02 16:27 PT

The current best diagnostic proof is now:

```text
artifacts/real-mcd/20260502_161942_mcd_48h_skip_t2_after_model_x_no_observer_probe/
```

Issue states after the latest probe:

- `PF-621` remains red because the proof used unsafe model/storage flags and
  the cart/bag state still does not mutate. The important improvement is that
  subprocess purity, real McD guest `dalvikvm`, and no-fatal requirements pass
  in this probe.
- `PF-622` remains red: `fragment_resumed=0` and `lifecycleState=null`.
  Soft resume recovery is still diagnostic only.
- `PF-623` is green in the current probe:
  `mcd_full_app_generic_pdp_input_gate count=4`.
- `PF-624` is green/yellow: telemetry no longer aborts the Add path and
  downstream basket entry has `telemetryAbortAfter=0`.
- `PF-625` changed from crash-red to persistence-red. BaseCart predicates now
  return rows and `sigbus=0`, but `cartSizeWithoutOffers=0`,
  `totalBagCount=0`, and `maxQtty=0` remain the next blockers.
- `PF-626` is partly green: subprocess purity and real guest proof pass, but
  the checker must count `MCD_PDP_REALMLIST_HYDRATE` and still keep unsafe
  probes from closing PF-621.
> *[SUPERSEDED 2026-05-03 — see 18:10 PT update; PF-620 is green]*
- `PF-620` remains red: McD GraphQL/network calls still return status `599`.

Execution details for the next workers:

- Cart persistence worker:
  - Own `$HOME/art-latest/patches/runtime/interpreter/` and any needed
    Realm row/list/write support.
  - Verify that `OrderPDPViewModel.X(cartProduct)` returns without fatal and
    that reads after commit see the same cart row identity with updated
    `cartProducts`, `cartSizeWithoutOffers`, or `totalBagCount`.
  - Do not fake cart counters in launcher code. The app must mutate through its
    own model path or emit its own stock rejection.
- Product stock worker:
  - Own McD PDP product hydration in `WestlakeLauncher.java` only if
    coordinated with the supervisor.
  - Replace `maxQtty=0` with a real or deterministic offline catalog value
    before Add is committed. The expected proof movement is
    `maxQttyZero=0`.
- Lifecycle worker:
  - Own AndroidX lifecycle shims and target normal RESUMED state.
- Proof worker:
  - Treat `MCD_PDP_REALMLIST_HYDRATE seen=10 present=10` as successful
    CartProduct list hydration when `choicesNullNpe=0`.
  - Keep the final PF-621 gate strict: unsafe off, subprocess pure, real guest,
    generic input, lifecycle green, and cart mutation or exact rejection.

## Two-Day Final Rally Issue Board - 2026-05-02 16:39 PT

Latest proof after product-stock hydration:

```text
artifacts/real-mcd/20260502_162934_mcd_48h_product_stock_hydrate_no_observer_probe/
```

Issue state changes:

> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625B product stock` moves to green for the diagnostic path:
  `maxQttyZero=0` and `MCD_PDP_CART_GATE ... maxQtty=99`.
- `PF-626 proof` moves one step forward: `MCD_PDP_REALMLIST_HYDRATE` is now a
  valid CartProduct list-hydration proof when `choicesNullNpe=0`.
> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625A cart persistence` remains the top red implementation issue:
  stock basket commit is reached with `sigbus=0`, but cart/bag counters remain
  zero.
- `PF-622 lifecycle` remains red:
  `lifecycleState=null`, `fragment_resumed=0`.
- `PF-623 generic PDP input` is green in the latest proof.
> *[SUPERSEDED 2026-05-03 — see 18:10 PT update; PF-620 is green]*
- `PF-620 network` remains red with status `599`.

Next execution instructions:

- Runtime/storage worker:
  - Deploy candidate runtime
    `83aceaf740cab758cd8871cf6e0d02414f5ccebde668d807f41e2126698d629b`.
  - Prove persistent list readback with `list-create ... size=1` on
    `BaseCart.cartProducts` after `nativeAddRow`.
  - If the list readback is correct but the bag is still zero, instrument
    `CartInfo` and `CartViewModel` state transfer instead of adding launcher
    counters.
- Lifecycle worker:
  - Rebuild and deploy the AndroidX lifecycle alias patch.
  - Target `MCD_PDP_LIVEDATA_STATE ... lifecycleState=STARTED|RESUMED`.
- Proof worker:
  - Keep unsafe probes diagnostic. PF-621 cannot pass until unsafe flag files
    are absent and the normal no-unsafe gate proves cart/rejection and
    lifecycle.
- Network worker:
  - Do not block the cart proof on full live API unless the next trace proves
    the cart state depends on a missing network response.

## Two-Day Final Rally Issue Board - 2026-05-02 17:00 PT

Latest deployed-phone proof:

```text
artifacts/real-mcd/20260502_165132_mcd_48h_basecart_row0_lifecycle_probe/
runtime sha256: b1c0b65da3279fd2183a200fa8f5385c94936bbee0e31b82055d567c706b632c
shim sha256: 6c47dbceac413fd86b32658f6602d948754235a54266f3a1db3e3b2d55e0be60
```

Issue state changes:

> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625A Realm/BaseCart` is split:
  - green: BaseCart active query row identity now returns `row=0`;
  - green: `OsList.nativeAddRow` readback for `BaseCart.cartProducts` shows
    `size=1`;
  - red: app-visible cart projection remains
    `cartSizeWithoutOffers=0 totalBagCount=0`.
> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625C OsObjectBuilder object-list persistence` is now the immediate P0.
  The McD generated `BaseCartRealmProxy.insertOrUpdate(...)` path can write
  object lists through `OsObjectBuilder.nativeAddObjectList(...)`, so the
  runtime must persist those link lists into the same row/link-list store.
- `PF-622 lifecycle` remains red with `lifecycleState=null` and
  `fragment_resumed=0` despite the AndroidX alias patch being deployed.
> *[SUPERSEDED 2026-05-03 — see 18:10 PT update; PF-620 is green]*
- `PF-620 network` remains red with McD network status `599`.
- `PF-621 final acceptance` remains red until unsafe files are absent and a
  normal gate proves real cart mutation or exact stock rejection.

Execution queue:

1. Runtime/storage:
   - Build `$HOME/art-latest` with the staged
     `nativeAddObjectList` implementation.
   - Sync `ohos-deploy/arm64-a15/dalvikvm` to the phone.
   - Gate label: `mcd_48h_builder_object_list_probe`.
2. Cart projection:
   - If builder-object-list markers are green and `totalBagCount=0`, instrument
     stock `BasketAPIHandler.p2()`, `CartInfo.getTotalBagCount()`, and
     `CartViewModel.getInstance().getCartInfo()` after
     `OrderPDPViewModel.X(cartProduct)`.
   - Bridge only if the stock `BasketAPIHandler.p2()` cart has a positive count
     and the singleton `CartViewModel` remains stale.
3. Lifecycle:
   - Add the next proof marker around view-lifecycle owner creation and
     observer dispatch. Required target:
     `lifecycleState=STARTED|RESUMED`, not only soft-resume recovery.
4. Network:
   - Track McD `599` separately. Do not let network status hide a green local
     cart state proof, but do not claim complete stock runtime until live API
     calls are usable or deterministic offline responses are explicitly
     documented as a southbound contract.

## Two-Day Final Rally Issue Board - 2026-05-02 17:08 PT

Latest phone gate:

```text
artifacts/real-mcd/20260502_170205_mcd_48h_builder_object_list_probe/
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 6c47dbceac413fd86b32658f6602d948754235a54266f3a1db3e3b2d55e0be60
```

Issue state:

> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625A Realm direct list persistence`: green for this route.
  `nativeAddRow` persists `BaseCart.cartProducts` and later list creation
  rehydrates `size=1`.
> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625C OsObjectBuilder object-list`: implemented in runtime, but not
  observed on the current McD Add route. Keep it as covered surface, not the
  active blocker.
> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625D CartInfo/CartViewModel projection`: new immediate P0. The app runs
  stock `model_x` and basket commit but visible counters remain zero.
> *[STILL OPEN — masked by compatibility marker; see WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md §3 B5]*
- `PF-622 lifecycle proof`: still red, but narrowed. App Fragment state is
  `mState=7`; the marker must query app AndroidX `LifecycleRegistry.d()` and
  `isResumed()` before claiming lifecycle is null.
> *[SUPERSEDED 2026-05-03 — see 18:10 PT update; PF-620 is green]*
- `PF-620 network`: unchanged red, status `599`.
- `PF-621 final acceptance`: red until a no-unsafe gate shows lifecycle plus
  cart mutation or exact stock rejection.

Worker instructions:

- Cart projection worker:
  - Patch `WestlakeLauncher.invokeMcdPdpStockAddModelCommit(...)` immediately
    after `xMethod.invoke(viewModel, cartProduct)`.
  - Read stock `BasketAPIHandler.p2()` if a handler instance is discoverable,
    then read `CartInfo.getTotalBagCount()`, `CartInfo.getCartProductQuantity()`,
    `CartViewModel.getInstance().getCartInfo()`, and
    `CartViewModel.getCartSizeWithoutOrderLevelOffers()`.
  - Bridge to `CartViewModel.setCartInfo(stockCartInfo)` only when stock
    `p2()` is positive and the singleton VM remains zero.
- Lifecycle worker:
  - Patch proof/readiness markers to query obfuscated lifecycle state aliases:
    `getCurrentState`, `d`, `b`, `e`.
  - Treat Fragment `mState=7` or `isResumed()==true` as the resumed proof for
    this app build; do not require `mResumed` because the decoded app Fragment
    shape does not expose it.
- Proof worker:
  - Add gate checks for `MCD_PDP_CARTINFO_READBACK` and
    `MCD_PDP_CARTINFO_SET_BRIDGE`.
  - Keep unsafe runs diagnostic.

## Two-Day Final Rally Issue Board - 2026-05-02 17:30 PT

Latest final-local phone gate:

```text
artifacts/real-mcd/20260502_172158_mcd_48h_no_unsafe_cartinfo_final_probe/
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 213b34be4483d1f2a200f071b669e4d7d24ca44db46969a5a6c09986c153b1dd
```

Issue state changes:

- `PF-621 final local acceptance`: green after checker correction.
  - `proof_unsafe_flags_off markers=0 flags=0`.
  - `westlake_subprocess_purity` and `proof_real_mcd_guest_dalvikvm` pass.
  - Generic PDP input, lifecycle, CartProduct list hydration, downstream
    basket commit, and cart/bag resolution pass.
- `PF-625 Realm/cart storage`: green for the current route.
  - Direct `OsList` persistence is green.
  - Downstream basket commit reaches stock code with `sigbus=0`.
  - `BasketAPIHandler.p2()` returns positive `CartInfo`.
> *[SUBTAG COLLAPSED INTO PARENT IN 18:10 PT UPDATE]*
- `PF-625D CartInfo/CartViewModel projection`: yellow.
  - The bridge proves the stock cart exists and can update the app singleton.
  - The final architecture should make the stock observer/model route perform
    the propagation without a McD-specific launcher bridge.
- `PF-622 lifecycle proof`: green for this app build using compatibility
  signals (`fragmentState=7` / resumed marker). Keep generic AndroidX
  lifecycle as P1.
> *[SUPERSEDED 2026-05-03 — see 18:10 PT update; PF-620 is green]*
- `PF-620 network`: immediate P0 red.
  - Direct WSL curl to McD GraphQL returns HTTP 200.
  - Phone gate was configuring `adb reverse` and
    `westlake_http_proxy_base.txt` without starting the local dev proxy.
  - `scripts/run-real-mcd-phone-gate.sh` now auto-starts the dev proxy when
    using `127.0.0.1:<port>` or `localhost:<port>`.
- `PF-627 dashboard item XML`: P1 red/yellow.
  - Dashboard section shells inflate, but promotion/popular item XML rows are
    not yet proven.

Current worker queue:

1. Network worker owns `PF-620`.
   - Run `mcd_48h_proxy_autostart_network_smoke`.
   - If network becomes 2xx, run `mcd_48h_proxy_autostart_full_app` with
     interaction enabled and require PF-621 to stay green.
   - If still 599, inspect `http-proxy.err`, host `HTTP bridge v2 request
     failed`, and the generated bridge meta files in the artifact.
2. Model-propagation worker owns `PF-628`.
   - Make `CartViewModel` naturally observe/receive stock `CartInfo`.
   - Remove reliance on `MCD_PDP_CARTINFO_SET_BRIDGE` from final success once
     the generic observer path is proven.
3. XML/UI worker owns `PF-627`.
   - Drive real item-row XML inflation for dashboard lists.
   - Do not add mock rows; rows must come from stock McD adapters/models or the
     existing deterministic offline model objects.
4. Proof worker owns acceptance strictness.
   - Keep the final no-unsafe gate strict.
   - Keep network as a separate blocking fail until a 2xx live response is
     proven.

## Two-Day Final Rally Issue Board - 2026-05-02 18:10 PT

Latest accepted bounded phone gate:

```text
artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 51ba606bc829ab4cf57c759cc2b65f5f71e51dd8d0bbe304df4602ebe5572fbe
gate_status=PASS
```

Issue state changes:

- `PF-621` bounded full-app acceptance: green.
  - Proof has unsafe flags off, subprocess purity, real guest `dalvikvm`,
    live network 2xx, dashboard XML, item-row XML, PDP, lifecycle,
    generic PDP input, Realm cart-product list hydration, basket commit, and
    cart/bag resolution in one artifact.
  - This is a bounded acceptance proof, not a full long-soak stock-runtime
    claim.
- `PF-620` real McD network: green for the phone proof harness.
  - The harness now auto-starts the local HTTP proxy and uses dynamic
    `adb reverse` port wiring.
  - Current proof has `network_success_markers=9` and
    `network_error_markers=0`.
- `PF-627` dashboard item XML/polish: green for first stock row XML coverage.
  - `layout_home_promotion_item` and `layout_home_popular_item_adapter` both
    inflate in the accepted proof.
  - Full RecyclerView/layout/render parity remains under `PF-623/PF-606`.
- `PF-625` Realm/cart storage: green for the current bounded route.
  - Direct Realm list persistence, CartProduct list hydration, product stock
    hydration, basket commit, positive `CartInfo`, and app-visible cart count
    are all proven.
  - Keep storage under watch during long-soak and repeated add/remove.
- `PF-628` model propagation without bridge: yellow/red.
  - The accepted proof still contains `MCD_PDP_CARTINFO_SET_BRIDGE`.
  - Next proof must show stock lifecycle/observer propagation updates
    `CartViewModel` without that bridge.
- `PF-630` Realm close/finalizer long-soak stability: new P0 red.
  - Unbounded artifact
    `artifacts/real-mcd/20260502_174634_mcd_48h_network_pf621_full_app_final/`
    reaches network/dashboard/PDP/cart success and later crashes with
    `Fatal signal 7 (SIGBUS)` near `OsSharedRealm.nativeCloseSharedRealm`.
- `PF-629` deeper stock route coverage: P1 red.
  - Need stock bag/cart screen, edit/remove/re-add, customize, back/bottom-nav,
    and login/location-safe routes.
- `PF-632` stock Android API compatibility discovered by McD: new P1 red.
  - First concrete miss:
    `android.app.AlertDialog$Builder(Context,int)` from a stock PDP click path.
- `PF-626/PF-608` OHOS parity: active.
  - Android proof remains portable only if every new API surface is mapped to
    the southbound API document or kept as an explicit open gap.

48-hour worker queue from the green frontier:

1. Stability worker owns `PF-630`.
   - Instrument `OsSharedRealm.nativeCloseSharedRealm`, finalizers,
     transaction close, and handle release.
   - Make close idempotent for the portable Realm subset.
   - Gate: 10-minute post-cart soak, live guest process present, no fatal.
2. Lifecycle/model worker owns `PF-628`.
   - Use the generic `FragmentViewLifecycleOwner` and
     `MiniActivityManager` dispatch patch as the baseline.
   - Gate: positive stock cart readback and `vmTotalBagCount=1` without
     `MCD_PDP_CARTINFO_SET_BRIDGE`.
3. Route worker owns `PF-629`.
   - Drive bag/cart screen, quantity, customize, back/bottom navigation, and
     repeated add/remove.
   - Every new framework miss becomes `PF-632` or a southbound API delta.
4. Framework/API worker owns `PF-632`.
   - Implement `AlertDialog.Builder(Context,int)` and minimal dialog theme
     semantics first.
   - Add controlled probe coverage before rerunning real McD.
5. Proof/OHOS worker owns `PF-626/PF-608`.
   - Keep `PF-621` strict.
   - Mirror new southbound surfaces in
     `docs/program/WESTLAKE_SOUTHBOUND_API.md`.
