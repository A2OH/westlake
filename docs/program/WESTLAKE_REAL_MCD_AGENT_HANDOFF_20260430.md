# Westlake Real McD Agent Handoff - 2026-04-30

## Southbound API Contract

Use
`$HOME/android-to-openharmony-migration/docs/program/WESTLAKE_SOUTHBOUND_API.md`
as the current Westlake southbound API contract.

Use
`$HOME/android-to-openharmony-migration/docs/program/WESTLAKE_REAL_MCD_72H_DASHBOARD_PLAN_20260430.md`
as the current 72-hour supervisor runbook for driving stock McDonald's
dashboard success on the phone.

Use
`$HOME/android-to-openharmony-migration/docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`
as the current two-day rally runbook for moving from the accepted
dashboard/PDP/Add proof to fuller stock McDonald's app execution through PDP
quantity/customize, bag/cart, generic input/rendering, and OHOS southbound
parity.

Use
`$HOME/android-to-openharmony-migration/docs/program/WESTLAKE_REAL_MCD_72H_AGENT_PROMPTS_20260430.md`
to launch subagents with scoped ownership and acceptance criteria.

The key architecture rule is that Android-shaped guest APIs and JNI signatures
remain northbound, while all host/OS work moves through Westlake-owned
southbound APIs that can be backed by either the Android phone proof adapter or
the OHOS/musl adapter. Do not count direct Android bionic native-library
loading as OHOS portability unless it is covered by an explicit per-library
compatibility contract or a declared bionic compatibility-capsule scope.

## Supervisor Update - 2026-05-02 11:35 PT

Current phone truth:

- The two-day rally runbook is updated and is now the contract source:
  `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`.
- Runtime pushed to phone `cfb7c9e3` through Windows ADB
  `localhost:5037`; local and phone `aosp-shim.dex` hash:
  `c17e5dbe6fd9ccdc5239022e03ec3a7da85849b2846af0df81acf5a55633bb1b`.
- Best current phone artifact:
  `artifacts/real-mcd/20260502_112809_mcd_48h_j8_cart_gate_probe/`.
- The proof keeps McD inside Westlake:
  `host_pid=18896`, `vm_pid=18931`, direct McD ART processes `0`.
- Real dashboard popular tap launches real PDP. The route logs
  `MCD_DASH_SEMANTIC_POPULAR_CLICK ... handled=true` and
  `Navigated to OrderProductDetailsActivity`.
- The PDP XML path is active:
  `MCD_REAL_XML_PDP_ENHANCED productInfo=3 scroll=ScrollView normalized=true`.
- The bridge hydrates stock PDP fields from the inflated binding tree:
  `MCD_PDP_FIELD_HYDRATE plusOk=true minusOk=true quantityOk=true`.
- Direct Westlake Add touch now reaches the stock handler without the earlier
  `performResume()` SIGBUS:
  `MCD_ORDER_PDP_STOCK_ACTION control=add_to_order route=fragment_j8 invoked=true`.
- Cart gate is no longer null:
  `MCD_PDP_CART_GATE ... cartInfo=CartInfo cartSizeWithoutOffers=0 totalBagCount=0`.

What is proven:

- The visible phone route is real McD dashboard -> real McD PDP -> stock
  `OrderPDPFragment.j8(true)` entry inside Westlake.
- The older crash from hard `performResume()` is avoided in the current path.
- The proof can show "Added to bag" and projected bag count `1` on the phone.

Remaining gaps:

- This is still not full McD app success. The current lifecycle recovery uses a
  labeled soft-state bridge; `mResumed` still reads false.
- Real cart mutation is not proven. `CartInfo.totalBagCount` and
  `cartSizeWithoutOffers` remain zero before and after `j8`, and the current
  `CartProduct` cart gate shows `quantity=0 maxQtty=0`.
- The accepted Add path still uses direct Westlake touch-file injection after
  physical dashboard navigation; physical host Add tap and generic hit dispatch
  must be recovered.
- Quantity plus/minus, customize, cart/bag screen, and OHOS adapter parity
  remain open rally workstreams.
- `scripts/check-real-mcd-proof.sh` still fails the overall full gate because
  dashboard child-fragment and hard Fragment lifecycle markers are not yet
  satisfied. Keep the full gate strict; add a separate PDP/Add/cart-gate
  sub-result if needed.

Immediate worker priorities:

1. WS3/order-flow: set or derive stock `CartProduct.quantity/maxQtty` before
   `j8`, then instrument downstream `A7`, `OrderPDPViewModel.X`, basket use
   case, API handler, `getCartInfo`, and `CartViewModel.setCartInfo`.
2. WS1/lifecycle: replace `mode=soft_state` with real FragmentManager
   start/resume without SIGBUS.
3. WS2/UI-input: convert direct touch-file Add to physical host tap through
   generic hit dispatch.

## Supervisor Update - 2026-05-02 12:20 PT

Current phone truth:

- Runtime pushed to phone `cfb7c9e3` through Windows ADB `localhost:5037`;
  local and phone `aosp-shim.dex` hash:
  `48f60b57724549441e3fcd1b37603589e78214c051f22e605b48330062d5b5b4`.
- Current guarded baseline artifact:
  `artifacts/real-mcd/20260502_120714_mcd_48h_guarded_model_commit_baseline/`.
- Unsafe downstream commit crash artifact:
  `artifacts/real-mcd/20260502_115607_mcd_48h_model_x_commit_probe/`.
- The guarded proof keeps McD inside Westlake:
  `PASS westlake_subprocess_purity ... direct_mcd_processes=0`.
- It has no VM fatal:
  `PASS no_fatal_failed_requirement count=0`.
- PDP/Add/cart substatus is green:
  `pdp_add_cart_gate_status=PASS`.
- `CartProduct.quantity` is now hydrated:
  `MCD_PDP_CART_PRODUCT_PREP ... afterQuantity=1` and
  `MCD_PDP_CART_GATE ... quantity=1`.
- The dangerous downstream commit is intentionally gated by default:
  `MCD_PDP_A7_GATE ... reason=realm_storage_sigbus_risk` and
  `MCD_PDP_STOCK_ADD_COMMIT ... route=model_x_gated_realm_storage`.

What changed:

- The old `CartProduct.quantity=0` blocker is closed for the current Add
  route.
- Direct reflective calls to `s7()` or `OrderPDPViewModel.X(CartProduct)` are
  not safe yet. The unsafe probe enters Realm/BaseStorage and
  `BasketAPIHandler.A1(...)`, then dies with `SIGBUS`.
- The unsafe model commit can only be enabled for focused diagnosis using
  `-Dwestlake.mcd.unsafe_model_commit=true`,
  `WESTLAKE_MCD_UNSAFE_MODEL_COMMIT=1`, or the launch-file equivalent.

Remaining gaps:

- Full gate is still failed. Real app cart mutation is not proven:
  `CartInfo.totalBagCount=0` and `cartSizeWithoutOffers=0` after `j8`.
- Fragment lifecycle remains soft-state only:
  `soft_resume_recovery=9 fragment_resumed=0`.
- Generic PDP Add input still fails:
  `WESTLAKE_GENERIC_TOUCH_DISPATCH ... handled=false`; projected fallback is
  still handling Add.
- Real customize, bag/cart screen, repeated Add/remove, back/bottom-nav, and
  OHOS adapter parity remain open.

Immediate worker priorities:

1. WS4/storage: implement the targeted Realm/BaseStorage/BasketAPIHandler
   southbound slice so unsafe model commit can run without SIGBUS.
2. WS1/lifecycle: make real `MCD_PDP_FRAGMENT_RESUMED` appear without
   soft-state recovery.
3. WS2/input: make physical/generic Add dispatch reach the real stock target
   without projected fallback.
4. WS3/order-flow: once storage is nonfatal, re-enable unsafe model commit in
   a focused probe and require either cart mutation or a precise nonfatal
   stock rejection.
4. WS4/state-network: prove cart/menu/product storage and network state through
   app models, not frame-only projection.
5. WS5/OHOS: map every new Android phone dependency to
   `WESTLAKE_SOUTHBOUND_API.md` or a PF gap.

## Supervisor Update - 2026-05-02 12:46 PT

Current phone truth:

- Runtime pushed to phone `cfb7c9e3` through Windows ADB `localhost:5037`;
  local and phone `aosp-shim.dex` hash:
  `cbe6802cedf83d2f0e9e254ada18ec32951700c84f1fac5bbaf5526ab268d481`.
- Latest focused proof:
  `artifacts/real-mcd/20260502_123759_mcd_48h_livedata_seed_stock_click_probe/`.
- This proof is stock McD inside Westlake guest `dalvikvm`:
  `PASS westlake_subprocess_purity ... direct_mcd_processes=0`.
- It has no VM fatal:
  `PASS no_fatal_failed_requirement count=0`.
- The real PDP renders densely through the Westlake path:
  `Strict McD order PDP frame ... bytes=56031 views=45 texts=10 buttons=3 images=3`.
- The real generated button binding now installs and clicks:
  `MCD_PDP_STOCK_BINDING_PREP ... listenerInstalled=true`,
  `MCD_PDP_STOCK_LIVEDATA_PREP ... before=null after=true set=true`, and
  `MCD_PDP_STOCK_VIEW_CLICK ... route=performClick invoked=true`.
- The previous generated-binding Boolean NPE is closed in this proof:
  `mcd_pdp_stock_click_boolean_npe count=0`.

What changed:

- The safest Add route is now the stock view/data-binding route, not direct
  reflection into `s7`, `A7`, `OrderPDPViewModel.X`, or basket classes.
- The checker is hardened for PDP-only artifacts and now reports the stock
  binding/listener/LiveData/click milestones separately.

Remaining gaps:

- Full gate is still failed. The stock view click has no proven cart
  continuation yet: no `MCD_PDP_CART_GATE`, no basket commit marker, and no
  cart mutation in the latest proof.
- PDP lifecycle remains soft-state:
  `MCD_PDP_FRAGMENT_RESUME_RECOVERY mode=soft_state resumedField=false`.
- `MiniActivityManager.performStart` still logs duplicate-parent start failure:
  `The specified child already has a parent`.
- The next blocker is likely app-bundled AndroidX lifecycle/LiveData observer
  dispatch after the generated binding click.
- Generic/physical PDP Add is still not accepted. The current action is a
  projected hit that invokes the stock button view; final input proof must use
  generic hit dispatch or a physical host touch to reach that view.

Immediate worker priorities:

1. WS1/lifecycle: fix duplicate-parent start and make the real fragment reach
   resumed/observer-active state without `mode=soft_state`.
2. WS3/order-flow: instrument the stock generated binding click continuation:
   `OrderPdpButtonLayoutBindingImpl.a`, `OrderPDPViewModel.R1/Z/V`,
   observer callbacks, `OrderPDPFragment.s7/o7/A7`, and
   `CartViewModel.setCartInfo`.
3. WS4/storage: keep unsafe model commit guarded; only re-enable it in focused
   probes after Realm/BaseStorage handles are made nonfatal.
4. WS2/input: make generic hit dispatch find `pdpAddToBagButton` and invoke
   the same generated listener without projected fallback.

## Supervisor Update - 2026-05-02 13:07 PT

Current rally correction:

- Do not count `MCD_PDP_STOCK_VIEW_CLICK ... performClick invoked=true` as a full
  Add proof by itself. It only proves the view boundary.
- The next accepted Add proof must show generated binding entry, `j8(true)`,
  `OrderPDPViewModel.Z/V`, observer callback (`q7`, `Y7`, or product-limit),
  fragment continuation (`r7/s7/v8/o7/A7`), and then cart mutation or exact
  nonfatal stock rejection.
- Keep projected PDP Add as a diagnostic only. Final input proof must be host
  pointer/touch-file -> Westlake generic dispatch -> real `pdpAddToBagButton`.

Worker results to continue from:

1. WS1 lifecycle added a bounded lifecycle/observer bridge. Rebuild and phone
   proof must show `MCD_PDP_OBSERVER_BRIDGE*` and real resumed/observer-active
   state.
2. WS2 input added synthetic DOWN repair for orphan UP streams. The next proof
   must show generic touch lifecycle markers on `pdpAddToBagButton` and no
   projected fallback ownership.
3. WS3 mapped the exact stock Add path and marker sequence. Instrument missing
   method-entry markers if the long proof still only shows view-level click.
4. WS4 split unsafe model commit from unsafe storage commit. Keep Realm/storage
   guarded until the basket path no longer SIGBUSes.

Next proof:

```bash
./scripts/build-shim-dex.sh
./scripts/sync-westlake-phone-runtime.sh
WESTLAKE_GATE_SLEEP=180 scripts/run-real-mcd-phone-gate.sh mcd_48h_observer_state_probe_long
```

Use Windows ADB on `localhost:5037`, device `cfb7c9e3`, and tap dashboard then
PDP Add late enough that dashboard/PDP are actually ready.

## Supervisor Update - 2026-05-02 13:18 PT

New fact for the next agent: the long proof
`artifacts/real-mcd/20260502_131212_mcd_48h_observer_state_probe_long/` failed
because the harness touched too early, not because dashboard launch is proven
broken. The checker saw `dashboard_active count=0`, but live log later showed
dashboard launch, stock section attach, and `MCD_DASH_SECTIONS_READY`.

Do not rerun with blind sleeps. Use marker-driven touches:

```bash
ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
ADB_HOST=localhost \
ADB_PORT=5037 \
ADB_SERIAL=cfb7c9e3 \
WESTLAKE_GATE_SLEEP=420 \
scripts/run-real-mcd-phone-gate.sh mcd_48h_marker_driven_add_gate
```

Touch only after readiness:

1. dashboard readiness -> tap `(56,523)`;
2. PDP readiness -> tap `(358,988)`;
3. short delay -> tap `(350,960)`;
4. inspect for `MCD_PDP_STOCK_VIEW_CLICK`, generated binding entry,
   `MCD_PDP_LIVEDATA_STATE`, `MCD_PDP_OBSERVER_BRIDGE*`,
   `MCD_PDP_OBSERVER_DISPATCH_GATE`, `MCD_PDP_CART_GATE`,
   `MCD_PDP_STORAGE_SAFETY_GATE`, and generic input markers.

If the proof still stops at view-level click, the next patch is not another
fallback route. Instrument/generated-binding continuation is the blocker:
`OrderPdpButtonLayoutBindingImpl.a` -> `j8(true)` ->
`OrderPDPViewModel.Z/V` -> observer callback -> `r7/s7/v8/o7/A7`.

## Supervisor Update - 2026-05-02 13:30 PT

Result of marker-driven run:
`artifacts/real-mcd/20260502_132152_mcd_48h_marker_driven_add_gate/`.

What improved:

- real dashboard marker appeared before touch;
- dashboard tap routed through `HomePopularItemsAdapter`;
- real PDP XML was visible on phone;
- Add taps reached the stock Add view and `performClick`;
- storage stayed nonfatal.

What failed:

- artifact checker lost early dashboard markers because logcat was collected
  only at the end. The proof script now streams logcat throughout the run.
- `W1()` was stuck true, so `OrderPDPFragment.j8()` returned before
  `OrderPDPViewModel.Z()` could set `normalAdd/editAdd`.
- Add still used projected fallback after generic hit miss.
- Realm/storage still blocks real cart mutation.

Next proof should validate the new `W1` clearing patch:
`MCD_PDP_STOCK_LIVEDATA_PREP ... loadingCleared=true`, followed by
`MCD_PDP_OBSERVER_DISPATCH_GATE route=q7|Y7 requested=true`.

## Supervisor Update - 2026-05-02 03:05 PT

Current phone truth:

- The real dashboard -> real PDP -> add-to-order path now reaches the stock
  McDonald's PDP add handler in the Westlake guest process.
- Runtime pushed to phone `cfb7c9e3` through Windows ADB
  `localhost:5037`; phone `aosp-shim.dex` hash:
  `ea537af24862f441ef947b6a86106ef88f19cdea285637e8449e34a0091bc406`.
- Fresh gate proof:
  `artifacts/real-mcd/20260502_031019_mcd_pdp_deferred_mainpost_cancel_probe/`
  passes Westlake subprocess purity, dashboard active/root selection,
  network bridge, real hero/menu/promotion/popular section XML inflation, and
  strict dashboard frame checks.
- Real PDP add proof:
  `artifacts/real-mcd/20260502_031155_mcd_pdp_deferred_mainpost_cancel_early_tap/`.
  The first early Add tap logged
  `MCD_ORDER_PDP_CART_PRODUCT_SYNTH success=true ... productId=1001`, then
  `fragment_deps_missing E0=null ... G0=null`, then a main-looper deferred
  worker retried until the real fragment binding was ready and logged
  `MCD_ORDER_PDP_STOCK_ACTION control=add_to_order
  route=fragment_j8_deferred invoked=true` and
  `MCD_ORDER_PDP_DEFERRED_STOCK_ADD phase=invoked ... pending=0`.
  The same artifact records `j8_deferred_count=1` and
  `post_timeout_count=0`, so the accepted proof does not double-submit the
  stock add handler.
- A separate delayed manual retry also proved direct stock execution once the
  PDP fragment finished binding:
  `artifacts/real-mcd/20260502_025705_mcd_pdp_j8_delayed_add_retry/`
  logged `route=fragment_j8 invoked=true` and `stockClick=true`.

What is proven:

- Dashboard popular item tap uses the real `HomePopularItemsAdapter` listener
  and real `Product` model, then launches real
  `com.mcdonalds.order.activity.OrderProductDetailsActivity`.
- The PDP bridge can synthesize the app SDK `CartProduct` from that real
  `Product` and place it in the stock `OrderPDPFragmentExtended.t0` field.
- The stock add-to-order method `OrderPDPFragment.j8(true)` can execute on
  phone without fatal VM exit once `E0/G0/t0` are present.

Remaining gaps:

- This is still a Westlake projection/control bridge over real McDonald's
  activity, fragment, model, and XML inflation. Do not call it full generic
  Android View rendering yet.
- `E0` and `G0` are still late on the first early Add tap; the deferred worker
  makes the user action reconcile to stock state, but the architectural fix is
  real FragmentManager/lifecycle/data-binding readiness before visible
  interaction.
- The real bottom add bar view is not reliably part of the selected render/hit
  root at first tap, so generic View hit testing and root selection still need
  work.
- Quantity plus/minus and customize still need the same stock-handler proof,
  then navigation to customization/bag/cart should become the next screen goal.

## Supervisor Update - 2026-05-02 01:25 PT

Current phone truth:

- The previous stock PDP `Activity.getWindowManager()Landroid/view/WindowManager;`
  `NoSuchMethodError` is closed. The shim now exposes a Westlake-side
  `WindowManager` service for `Activity.getWindowManager()`,
  `Context.getSystemService(Context.WINDOW_SERVICE)`, `Window.getWindowManager()`,
  default display, and API-30 window metrics.
- Fresh phone artifact:
  `artifacts/real-mcd/20260502_011431_mcd_windowmanager_bridge_pdp_guard_probe/`.
- Local and phone `aosp-shim.dex` hash:
  `fe2c190e8d8b8f66061c6326e87b979ad84ec7d2c60ea7e43d64f3e3191bbeb2`.
- The real dashboard -> real PDP path still works through the Westlake
  subprocess. The proof shows `MCD_DASH_SEMANTIC_POPULAR_CLICK`, real
  `MCD_ORDER_PDP_RENDER_ROOT selectedReason=holder_pdp holderHasPdp=true`, and
  `Strict McD order PDP frame ... bytes=2556 views=50 texts=21 images=3`.
- The direct PDP quantity touch no longer kills `dalvikvm`. It now logs
  `MCD_ORDER_PDP_STOCK_ACTION control=quantity_plus
  route=fragment_deps_missing ... E0=null t0=null G0=null`.
- The add-to-order direct touch also remains blocked, currently by
  `route=view_missing` / `route=fragment_j8_no_target`; the selected PDP root
  logs `bottom=null`, so the bottom add bar still needs real layout/scroll/root
  coverage before stock `j8(true)` can be proven.
- `scripts/check-real-mcd-proof.sh` on the artifact passes subprocess purity,
  no fatal/failed requirement, dashboard root selection, network bridge, and
  no known HTTP/config NPE checks. Focused grep count for
  `NoSuchMethodError|getWindowManager|SIGBUS|SIGSEGV|Failed requirement|OrderPDPViewModel`
  is zero in the captured tail.

Immediate next gaps:

- Initialize the real `OrderPDPFragment` lifecycle/data-binding state so `E0`
  (view model), `t0` (cart product), and `G0` (binding) are non-null before
  invoking stock PDP listeners.
- Make the PDP bottom/add-to-order bar part of the real selected root or
  scrollable hit map; current accepted root has `bottom=null` and cannot target
  the stock add button.
- Keep the dependency guard until the real fields are proven non-null; do not
  re-enable unconditional `onClick(View)` or `j8(true)` calls because the prior
  artifact proves that crashes inside `OrderPDPViewModel.X2`.
- Add a regression check for the new window/display service contract:
  no `getWindowManager` `NoSuchMethodError`, no classloader mismatch from host
  `WindowManager`, and successful `getDefaultDisplay`/window-metrics calls.

## Supervisor Update - 2026-05-02 01:10 PT

Current phone truth:

- The real dashboard -> real PDP route still works on phone `cfb7c9e3` via
  `WESTLAKE_ART_MCD`, and the visible PDP remains the real McD Big Mac PDP
  projected by Westlake.
- A guarded stock-handler bridge was added for PDP controls. It can reach the
  real `OrderPDPFragment.onClick(View)` entry point from a direct Westlake touch
  event.
- The stock PDP click path is not ready to execute safely yet. The first direct
  stock quantity invocation logged
  `MCD_ORDER_PDP_STOCK_ACTION control=quantity_plus route=fragment_onClick_enter`
  and then failed inside
  `OrderPDPViewModel.X2(int, CartProduct)` with an NPE followed by SIGBUS.
- The next shim build now guards stock calls behind fragment dependency checks
  for `E0` (PDP view model), `t0` (cart product), and `G0` (PDP binding for
  add-to-bag). Missing dependencies should now log
  `route=fragment_deps_missing` instead of killing `dalvikvm`.

Accepted artifacts:

- Fresh hardened relaunch to dashboard and PDP:
  `artifacts/real-mcd/20260502_005858_mcd_hardened_bridge_relaunch_to_pdp_controls/`.
- Direct Westlake-touch stock bridge crash proof:
  `artifacts/real-mcd/20260502_010455_mcd_direct_touch_pdp_stock_bridge_controls/`.
- Shim hash with dependency guard pushed to phone:
  `f9da41b3f0540e0584fa05c5de8c4c228ba8b73706f55dc93f43eea5043a7d33`.

Immediate next gaps:

- Make `OrderPDPFragment` lifecycle/data binding real enough that `E0`, `t0`,
  and `G0` are non-null before invoking stock PDP listeners.
- Add a regression probe that writes direct single-UP events to
  `/sdcard/Android/data/com.westlake.host/files/westlake_touch.dat`; physical
  `adb input tap` can lose UP events while the render loop is busy.
- Add `Activity.getWindowManager()` to the shim. The fresh PDP launch now logs
  a real stock `NoSuchMethodError` for that API during `performCreate`; it did
  not block projection, but it is a concrete stock API gap.

## Supervisor Update - 2026-05-02 00:35 PT

Current phone truth:

- The real McD dashboard-to-PDP route still launches and renders on phone
  `cfb7c9e3` through `WESTLAKE_ART_MCD`.
- PDP projected control routing is now proven for the current strict-projection
  tier. Westlake handles `Customize`, quantity `+`, and `Add to order` from adb
  taps, updates the projected frame, and emits explicit
  `MCD_ORDER_PDP_PROJECTED_HIT` markers.
- This is still a Westlake projection bridge over the real inflated PDP tree:
  `stockClick=false` for these controls means the stock McD click listeners are
  not yet fully attached/executing. Do not claim stock cart/customization flow
  parity yet.

Accepted artifacts:

- Full relaunch-to-PDP proof:
  `artifacts/real-mcd/20260502_002819_mcd_real_pdp_preroute_add_probe_live/`.
- Focused quantity/add follow-up:
  `artifacts/real-mcd/20260502_003211_mcd_live_pdp_quantity_add_followup/`.
- Shim hash pushed to the phone:
  `6c46126a69762cc003b63ea60b31bcff5f68d5b80b0bf717bb9a3138a2e10657`.
- PDP before-taps hash:
  `293c8fd315c6a435cbe00ed2cfc00f39dc5f18eebbd1b87b8638a49fe8eb6052`.
- Focused follow-up after quantity/add hash:
  `a44cb4da0a3e4d15b1f46dd4b5f88a2143d50e6c9539a852e32e16e9bdceabc9`.

Accepted markers:

- `ready=1`
- `pdp=1`
- `MCD_ORDER_PDP_PROJECTED_HIT control=customize stockClick=false handled=true quantity=1 bag=0 ...`
- `MCD_ORDER_PDP_PROJECTED_HIT control=quantity_plus stockClick=false handled=true quantity=2 bag=1 ...`
- `MCD_ORDER_PDP_PROJECTED_HIT control=add_to_order stockClick=false handled=true quantity=2 bag=3 ...`
- `Strict McD order PDP frame ... bytes=2593 views=50 texts=21 images=3`
- `Frame: 2593 bytes -> View`

Implementation note:

- The input bridge now pre-routes known PDP projected controls before stock
  `dispatchTouchEvent()` on ACTION_UP. This fixes the bottom footer case where
  the stock dispatch path could delay or swallow the Westlake projection bridge
  before `add_to_order` was handled.
- Use the host launch extra `--es launch WESTLAKE_ART_MCD` for this Activity
  build. `--es className WESTLAKE_ART_MCD` starts the host UI only and leaves no
  guest `dalvikvm` child.

Immediate next gaps:

- Replace `stockClick=false` with real stock listener execution for PDP controls
  by fixing fragment/data-binding listener attach and command routing.
- Generalize the pre-route bridge into proper projected hit testing instead of
  McD PDP coordinate bands.
- Continue from PDP into real customization and bag/cart screens; current proof
  updates the Westlake frame but does not execute the real cart workflow.

## Supervisor Update - 2026-05-02 00:10 PT

Current phone truth:

- The real McD dashboard-to-PDP route is now visually useful on the phone for
  the current strict-projection tier. The stock `HomeDashboardActivity` popular
  item route launches stock `OrderProductDetailsActivity`, inflates real
  `layout/fragment_order_pdp`, directly attaches the real PDP XML tree into
  `simple_product_holder`, and renders a full-height McD product detail screen
  through Westlake DLST.
- This is not the old mock McD app. The accepted run uses the real McD APK
  activity path, real stock adapter/listener route, real resource ID lookup, and
  real PDP XML nodes. The final visible PDP is still a Westlake strict
  projection, not generic Android hardware View rendering.
- Latest accepted artifact:
  `artifacts/real-mcd/20260502_000401_mcd_real_order_detail_pdp_projected_frame_probe/`.
- Shim hash pushed to the phone:
  `ea3de257eff2f3627da52805d99becc05d0ae4392aaa285a6d8b7b150707c0b7`.
- Screenshot proof:
  before hash `c322b9a41b8d1c58bce2bfdf4ccce7155951f1c662b67c3fd4264150750fb09d`,
  after hash `293c8fd315c6a435cbe00ed2cfc00f39dc5f18eebbd1b87b8638a49fe8eb6052`.
  The after PNG increased from the prior black/partial PDP artifact's 17,506
  bytes to 106,409 bytes and now shows a full Big Mac PDP.

Accepted markers:

- `ready=1`
- `MCD_DASH_SEMANTIC_POPULAR_CLICK ... handled=true`
- `MCD_DASH_RECYCLER_HIT_CLICK ... handled=true`
- `Navigated to OrderProductDetailsActivity`
- `MCD_REAL_XML_INFLATED layout=layout_fragment_order_pdp resource=0x7f0e02e2 root=ConstraintLayout`
- `MCD_REAL_XML_PDP_ENHANCED productInfo=3 scroll=ScrollView normalized=true`
- `MCD_REAL_XML_PDP_DIRECT_ATTACH already=false childCount=1 parentMatches=true`
- `MCD_ORDER_PDP_PROJECTION_LAYOUT ... scroll=...w_480_h_1013 ... main=...w_480_h_1433 ... info=...w_480_h_380 ... title=...w_480_h_60`
- `MCD_ORDER_PDP_RENDER_ROOT selectedReason=holder_pdp holderHasPdp=true selectedTexts=21`
- `Strict McD order PDP frame ... bytes=2556 views=50 texts=21 images=3`
- `Frame: 2556 bytes -> View`

Implementation note:

- The vertical-compression bug from the previous PDP milestone is closed at the
  selected render-root geometry level. The immediate visual gap was producer
  projection policy: the generic renderer let black framework backgrounds and
  drawable-less ImageViews dominate. The current patch adds a PDP-specific
  projection pass that reads real PDP view IDs/text from the inflated tree and
  emits a phone-visible McD PDP frame with product title, price/calories,
  quantity, customize, info rows, and add-to-order footer.

Remaining gaps after this milestone:

- Generalize this PDP-specific projection into reusable `ScrollView`,
  `ConstraintLayout`, `TextView`, `ImageView`, rounded background, and bottom
  bar rendering. The current visible success is acceptable as a boundary proof,
  but it is not generic stock View parity.
- Replace the placeholder burger hero with real Glide/ImageView bitmap data
  when the stock PDP image pipeline has a decoded drawable.
- Route PDP clicks (`Customize`, nutrition row, add-to-order, quantity +/-)
  through Westlake hit testing into the stock activity/fragment handlers.
- Replace direct PDP attach glue with generic FragmentManager transaction/view
  attach parity.
- Full McD app parity remains open: customization screens, bag/cart, store and
  location, auth, payments, full AppCompat/Material behavior, Realm/storage,
  analytics tolerance, and OHOS southbound adapters.

## Supervisor Update - 2026-05-01 22:55 PT

Current phone truth:

- The real McD dashboard gap is closed for the current projection tier. The
  phone reaches the stock `HomeDashboardActivity`, stock popular-item adapter
  route, and launches `OrderProductDetailsActivity`; this is not the old mock
  dashboard fallback.
- The next-screen PDP holder gap is now also partly closed: the real
  `layout/fragment_order_pdp` XML is inflated, enhanced, and directly attached
  into `simple_product_holder` when the stock fragment transaction path does
  not attach it.
- Latest accepted artifact:
  `artifacts/real-mcd/20260501_225802_mcd_real_order_detail_space_mapping_probe/`.
- Shim hash pushed to the phone:
  `0ebe462eb750721cf7feec46a6d0bd168b27bc8b151889836496114a92021e63`.
- Screenshot proof changed from dashboard to PDP:
  before hash `c322b9a41b8d1c58bce2bfdf4ccce7155951f1c662b67c3fd4264150750fb09d`,
  after hash `e80d93c13f5509d9c796aa4ce0b9ec846f32cbda4118cc27e3637bce89fa2edd`.

Accepted markers:

- `MCD_DASH_SEMANTIC_POPULAR_CLICK ... handled=true`
- `Navigated to OrderProductDetailsActivity`
- `MCD_REAL_XML_INFLATED layout=layout_fragment_order_pdp resource=0x7f0e02e2`
- `MCD_REAL_XML_PDP_ENHANCED productInfo=3 scroll=ScrollView`
- `MCD_REAL_XML_PDP_DIRECT_ATTACH already=false childCount=1 parentMatches=true`
- `MCD_ORDER_PDP_RENDER_ROOT selectedReason=holder_pdp holderHasPdp=true selectedTexts=21`
- PDP XML now maps `Space` tags to `android.widget.Space`; the prior
  `ClassCastException: android.view.View cannot be cast to android.widget.Space`
  is gone from the focused grep.
- PDP frame increased from the previous blank `214 bytes` case to
  `Frame: 1318 bytes -> View`.

Remaining gaps after this milestone:

- The PDP is still a strict display-list/projection path, not generic Android
  View hardware drawing.
- The direct attach is McD-targeted glue around a missing generic
  FragmentManager/FragmentTransaction attach path; workers should generalize
  this instead of expanding one-off PDP attach cases.
- The PDP content is vertically compressed in places (`ScrollView` and product
  info heights are still too small), so the next work is ConstraintLayout,
  Barrier/Group, Space, ScrollView, and measurement parity.
- Full McD app parity remains open: customization, add-to-bag, bag/cart,
  store/location, auth, payments, full Material/AppCompat behavior, and OHOS
  southbound parity.

## Supervisor Update - 2026-05-01 18:35 PT

Current phone truth:

- We are closer to a real McD dashboard proof, but not close to full stock McD
  UI parity. The accepted screen is still a Westlake strict display-list
  projection, not generic Android View drawing.
- Latest accepted interactive projection artifact:
  `artifacts/real-mcd/20260501_183146_mcd_real_dashboard_projection_scroll_probe_after_patch/`.
- The projection now responds visibly to ADB swipe input:
  before hash `50187c3fcfd0858ec8795f6c86533d2f1fb19f316a60d8062f582eb716c6ddea`,
  after hash `d63d41a034ad6e3efb38bf920ffe48d6be480f6523724f8f1b2280b4a641b8d4`.
- Touch routing is proven inside Westlake:
  `MCD_DASH_TOUCH_ROUTE phase=down/move/up ... rawDispatch=skipped`.
- Projection scroll state is now explicit:
  `MCD_DASH_SCROLL ... projectionBefore=0 projectionAfter=370 moved=true`.
- The normalized checker passes this artifact as
  `mcd_stock_dashboard_projection` with `gate_status=PASS`, while warning that
  child-fragment full-section parity remains open.

Accepted artifact facts:

- Execution purity:
  `host_pid=2966 vm_pid=3005 vm_ppid=2966 direct_mcd_processes=0`.
- Stock route:
  `PF-MCD-ROOT phase=select
  activity=com.mcdonalds.homedashboard.activity.HomeDashboardActivity
  ... fallback=false`.
- Live network:
  `network_attempt_markers=11`, `network_success_markers=7`,
  `network_error_markers=0`, `westlake_bridge=7`.
- Real McD XML:
  `layout_fragment_promotion_section`, `layout_fragment_popular_section`,
  `layout_home_promotion_item`, and `layout_home_popular_item_adapter`.
- Post-scroll strict frame:
  `bytes=128598 views=96 texts=2 buttons=3 images=1 rows=4
  rowImages=4 rowImageBytes=127479 overlays=0`.

Immediate next gaps:

- Convert this projection-specific scroll offset into generic visible
  `ScrollView`/RecyclerView scroll routing.
- Recover full Hero/Menu/Promotion/Popular child-fragment View attachment, or
  formally supersede that tier with a stronger adapter-driven stock route.
- Replace the remaining projection renderer shortcuts with broader
  ImageView/Glide, Material/AppCompat style, RecyclerView, and hit-test parity.
- Keep all OHOS claims bounded to the southbound API contract; this Android
  phone proof does not yet mean the real McD APK runs on OHOS.

## Supervisor Update - 2026-05-01 17:45 PT

Current phone truth:

- The phone is no longer showing the old hardcoded yellow McD mock/fallback.
  It is showing a Westlake strict display-list projection of the stock McD
  dashboard path. Visually it is still simplified and mock-like because
  Westlake renders a projected frame with placeholder image tiles and reduced
  styling; do not call it full stock Android View parity.
- Latest accepted projection artifact:
  `artifacts/real-mcd/20260501_172943_mcd_real_dashboard_long_gate_fast_row_images/`.
- Live recheck on phone `cfb7c9e3` matched the same screen hash:
  `screen_sha=4b3eee911a8c932ed53c775befb5d4e309f2ea6aff4a8b29aff791fde04720c2`.
- Execution purity remains correct: `com.westlake.host` is running, and there
  is no direct `com.mcdonalds.app` phone-ART process.
- The proof classifier now accepts this as tier
  `mcd_stock_dashboard_projection`, while warning that the older
  four-child-fragment/full-section proof markers are not present in this path.

Accepted projection facts from the latest artifact:

- `PF-MCD-ROOT phase=select
  activity=com.mcdonalds.homedashboard.activity.HomeDashboardActivity
  ... fallback=false`.
- Live network remains in Westlake user space:
  `network_attempt_markers=15`, `network_success_markers=9`,
  `network_error_markers=0`, and `westlake_bridge=9`.
- Real McD row XML inflates through the app/resource path:
  `MCD_REAL_XML_INFLATED layout=layout_home_promotion_item
  resource=0x7f0e036a root=LinearLayout` and
  `MCD_REAL_XML_INFLATED layout=layout_home_popular_item_adapter
  resource=0x7f0e0369 root=LinearLayout`.
- Real adapter classes are visible:
  `HomePromotionAdapter count=1` and `HomePopularItemsAdapter count=3`.
- Strict dashboard frame:
  `bytes=126064 views=90 texts=14 buttons=3 images=1 rows=8 rowImages=5
  rowImageBytes=331544 overlays=0`.
- The updated checker output for this artifact ends with `gate_status=PASS`.

Important remaining gaps:

- This is a projected Westlake frame, not generic stock View drawing parity.
- Hero/Menu child-fragment XML markers and
  `MCD_DASH_REAL_VIEW_ATTACHED section=HERO/MENU/PROMOTION/POPULAR` are missing
  in the latest projection path. Keep the older
  `20260501_134422_mcd_real_promo_seed_image_bytes_probe/` artifact as the
  higher-tier child-fragment regression proof.
- Image fidelity is incomplete. Category images appear, but promotion/popular
  rows still show placeholder/hash-style tiles in the projection. The next UI
  worker should finish generic Glide/ImageView readiness and strict-frame image
  serialization rather than adding McD-specific drawing.
- Touch/scroll must be rerun against this latest real dashboard projection.
- OHOS portability shape is sound for process, frame, input, and HTTP bridge,
  but OHOS adapters are not implemented and Realm/storage remains the largest
  stock-app compatibility gap.

## Supervisor Update - 2026-05-01 13:50 PT

Current accepted real-McD phone frontier:

- Accepted artifact:
  `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`.
- Launch path:
  `WESTLAKE_ART_MCD` runs `/data/local/tmp/westlake/com_mcdonalds_app.apk`
  inside the Westlake guest subprocess on phone `cfb7c9e3`; the gate proves
  `host_pid=9542`, guest `dalvikvm` `vm_pid=9579`, `vm_ppid=9542`, and zero
  direct `com.mcdonalds.app` phone-ART processes.
- Dashboard ownership:
  the shell is the real `HomeDashboardFragment` view
  (`MCD_DASH_STOCK_VIEW_ATTACHED`), and the four section placeholders are
  produced by the real dashboard `u6(List)` path (`MCD_DASH_U6_SEEDED`).
- Real child-fragment ownership:
  all four visible dashboard section contents are returned by real McDonald's
  child fragment `onCreateView` methods and attached into the real placeholders:
  `HomeHeroFragment`, `HomeMenuGuestUserFragment`, `HomePromotionFragment`,
  and `HomePopularFragment`.
- Real XML ownership now covers all four dashboard child section roots:
  Hero inflates the real McDonald's AXML
  `res/layout/fragment_home_dashboard_hero_section.xml` as resource
  `0x7f0e0282` and emits
  `MCD_REAL_XML_INFLATED layout=layout_fragment_home_dashboard_hero_section
  resource=0x7f0e0282 root=RelativeLayout`; Menu guest inflates
  `res/layout/home_menu_guest_user.xml` as resource `0x7f0e0366` and emits
  `MCD_REAL_XML_INFLATED layout=layout_home_menu_guest_user resource=0x7f0e0366
  root=LinearLayout`; Promotion inflates
  `res/layout/fragment_promotion_section.xml` as resource `0x7f0e030e` and
  emits `MCD_REAL_XML_INFLATED layout=layout_fragment_promotion_section
  resource=0x7f0e030e root=RelativeLayout`; Popular inflates
  `res/layout/fragment_popular_section.xml` as resource `0x7f0e0305` and emits
  `MCD_REAL_XML_INFLATED layout=layout_fragment_popular_section resource=0x7f0e0305
  root=RelativeLayout`.
- Gate result:
  `gate_status=PASS`, `network_attempt_markers=3`,
  `network_success_markers=3`, `network_error_markers=0`,
  `mcd_real_hero_xml_inflated count=1`,
  `mcd_real_menu_guest_xml_inflated count=1`,
  `mcd_real_promotion_section_xml_inflated count=1`,
  `mcd_real_popular_section_xml_inflated count=1`,
  `mcd_real_child_fragment_views sections=4`, zero
  `MCD_DASH_SECTION_VIEW_ATTACHED` fallback markers, and zero dashboard
  fallback/real-view failure markers.
- PF-613 item/adapter progress:
  Promotion now reaches the real `HomePromotionAdapter` with `itemCount=1`.
  The adapter inflates real McDonald's item AXML
  `layout_home_promotion_item` as resource `0x7f0e036a`, and strict frame row
  evidence remains `rows=1`. The strict-frame image bridge now fetches the
  row's promotion image URL through `WestlakeHttp` and renders `rowImages=1`
  with `rowImageBytes=54022`. Popular attaches the real
  `HomePopularItemsAdapter` but still has `itemCount=0`.
- Strict frame:
  `bytes=54799 views=69 texts=6 buttons=0 images=1 rows=1 rowImages=1
  rowImageBytes=54022 overlays=0`, `screen_bytes=79396`,
  `screen_sha=aa8cbaa4d0e9d9c2036c908e4c84130ab080e5304c00460a6d11818dc13ebb40`.

What changed in this frontier:

- `LayoutInflater` now prefers real McD Hero, Menu guest, Promotion, and
  Popular dashboard XML by default through the generic AXML parser and no
  longer uses `File.length()` for resource file reads on this path; streaming
  file reads avoided the previous `UnixFileSystem.getLength` SIGBUS.
- `WestlakeLauncher` attaches dashboard child fragment views through the
  manual create-view path before any dashboard replace probe. The dangerous
  app `FragmentManager` dashboard-replace path is now opt-in behind
  `westlake.mcd.dashboard.replace.probe`.
- `WestlakeLauncher` now probes real dashboard section RecyclerViews and
  bootstraps Promotion through the app's own `HomePromotionAdapter`. The current
  row uses a Westlake-seeded `Promotion` model only to drive the stock adapter;
  this is not yet real backend/cache data.
- The latest image proof is a strict-frame image-byte bridge, not proof that
  stock Glide fully completed into `ImageView.setImageBitmap`. Keep generic
  Glide/ImageView completion as an open southbound/framework blocker.
- `HomeHeroFragment.onViewCreated` remains disabled by default:
  `westlake.mcd.child.onviewcreated=false`. The diagnostic artifact
  `artifacts/real-mcd/20260501_122703_mcd_child_onviewcreated_probe/`
  SIGBUSed in `LiveData.observe`.

Next highest-value implementation gap:

- With Promotion now proving a real adapter/item XML row and live image bytes,
  move next into the remaining item/data surfaces: generic Glide completion,
  real backend/cache promotion data instead of a seed object, Popular item
  population, and then safe lifecycle callbacks.
- Keep `performAttach`, `onViewCreated`, and app `FragmentManager.h0(true)`
  disabled until they have isolated, passing proofs. They are stock-compatibility
  blockers, not closed APIs.

## Supervisor Update - 2026-05-01 10:50 PT

Current visible-phone McD status:

- the McD UI visible on the phone is still the Westlake McD boundary
  harness/fallback surface, not a proven stock McDonald's APK dashboard/order
  UI rendered end to end from the app's own XML/activity/fragment path;
- the latest accepted proof remains
  `artifacts/real-mcd/20260501_100855_mcd_two_step_category_navigation_clean_proof/`;
- do not claim stock McD UI parity until a new proof contains stock XML or
  adapter evidence such as `MCD_REAL_XML_*` markers plus a passing phone gate.

Current local implementation frontier:

- added a shadow bridge for the legacy McD network path:
  `com.mcdonalds.mcdcoreapp.network.McDRequestManager`,
  `McDHttpClient`, `Request`, `Response`, `RequestProvider`, and related
  listener/exception DTO classes now route compatible calls through
  `WestlakeLauncher.bridgeHttpRequest(...)`;
- hardened the shadow class surface to match the real APK's public
  `Request.Method.GET`, `Request` fields/constructor/methods, and
  `McDHttpClient.c/d/e/f/g` methods; a Claude review then drove additional
  shape hardening for `RequestProvider.MethodType.GET`,
  `RequestProvider.RequestType.JSON`, `McDHttpClient.AutoDisconnectInputStream`,
  and the McD error DTO path, reducing the risk of verifier or
  `NoSuchMethodError` failures in the next phone run;
- rebuilt `aosp-shim.dex` successfully with hash
  `0188d3410344697f7ba10a9752225fc67d4ea061da236953523f519f61a0c409`;
- updated `scripts/check-real-mcd-proof.sh` so shadow network bridge markers
  count as network proof;
- added `scripts/run-real-mcd-phone-gate.sh` as the reproducible launch,
  screenshot, logcat, process snapshot, grep, and checker wrapper for the next
  phone artifact.

Important blocker:

- this shell currently cannot launch Windows executables from WSL; even
  `cmd.exe /c echo hi` fails with
  `WSL ... UtilAcceptVsock: accept4 failed 110`;
- Linux `adb` is present, but its local server currently shows no USB device,
  and the Windows ADB server is not reachable at the WSL gateway IP;
- therefore the new `aosp-shim.dex` has not yet been synced to the phone and
  the shadow bridge is not accepted proof until ADB transport is restored.

Next phone gate after ADB transport is restored:

```bash
cd $HOME/android-to-openharmony-migration
ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  ./scripts/sync-westlake-phone-runtime.sh
ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_shadow_requestmanager_bridge_gate
```

Acceptance target for the next proof:

- zero fatal/linkage markers: no `SIGBUS`, `SIGSEGV`, `Failed requirement`,
  `NoSuchMethodError`, `IncompatibleClassChangeError`, `ClassCastException`,
  or `VerifyError`;
- at least one `PFCUT-MCD-NET shadow ... response status=2xx` marker;
- dashboard/root/strict-frame markers recover instead of the previous
  pre-dashboard Realm/native crash;
- if startup recovers, immediately continue to the real XML proof path and
  require `MCD_REAL_XML_*` markers before claiming non-fallback UI progress.

## Supervisor Update - 2026-05-01 10:12 PT

Accepted two-step Start Order -> category-detail navigation proof artifact:

- `artifacts/real-mcd/20260501_100855_mcd_two_step_category_navigation_clean_proof/`

Proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `21546`, guest `dalvikvm` PID
  `21581`, guest parent PID `21546`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- network proof:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- screenshot hashes:
  - dashboard:
    `fe12e38e867038b3dc866fa71c867f31685f95fd14b47a59abcfdb794491b36e`;
  - menu surface:
    `a404b1c815f6dc578e1fe382e4fb8f04c73d20a82060815f02db5611a4d7cc3c`;
  - category/detail surface:
    `12f2c6078d46f2cfb613e0cde6118e684c8f6281acbd1159096c13429c2f0466`;
- first input marker:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView leaf=com.mcdonalds.mcduikit.widget.McDTextView text=Order_Started handled=true adapter=false x=173 y=565`;
- first navigation marker:
  `MCD_ORDER_NAV_OPENED source=start_order_tile_menu`;
- second input marker:
  `GENERIC_HIT_CLICK target=android.widget.LinearLayout leaf=android.widget.ImageView text=none handled=true adapter=false x=173 y=565`;
- second navigation marker:
  `MCD_CATEGORY_NAV_OPENED label=Extra_Value_Meals source=category_detail`;
- old direct fallback markers did not fire:
  `Dashboard fallback direct touch routed` count `0` and `MCD_DASH_ACTION`
  count `0`;
- strict dashboard frame after category navigation:
  `bytes=124948 views=51 texts=10 buttons=3 images=2 rows=4 rowImages=4 rowImageBytes=123608 overlays=0`;
- deployed `aosp-shim.dex` hash:
  `57660c18f5f4e0b9b8503f6bc39ebc21ebb8da866fca27b23e2150b5ae6155be`.

Closed or improved in this slice:

- Westlake now has an accepted two-step, phone-visible McD navigation proof:
  Start Order opens the menu surface, then a category card opens a detail/add
  surface;
- both clicks go through root-aware generic hit testing and `performClick()`;
- the accepted path no longer uses the dashboard y-band fallback or direct
  text-mutation marker;
- button accounting now sees multiple clickable controls after the category
  transition.

Important caveat:

- the visible McD UI in this artifact is still Westlake's McD boundary
  harness/layout-builder surface. It is useful for proving rendering, input,
  network, image-byte, and navigation plumbing, but it is not yet the stock McD
  order module rendered from generic upstream layouts end to end.

Next blocker ranking:

1. Replace McD-specific menu/detail layout builders with generic XML/resource
   inflation of the real McD order/dashboard layouts.
2. Identify and drive the real order-module entrypoint instead of the
   Start Order tile bridge.
3. Prove generic RecyclerView/card/bottom-nav hit dispatch across real stock
   widgets, not only the harness tree.
4. Replace McD-specific adapter image lookup with stock
   Glide/OkHttp/ImageView binding evidence.
5. Keep every new phone-proof API behind the southbound contract so the OHOS
   adapter can implement the same shape.

## Supervisor Update - 2026-05-01 09:57 PT

Accepted Start Order menu-navigation proof artifact:

- `artifacts/real-mcd/20260501_095501_mcd_start_order_tile_menu_navigation_proof/`

Proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `20224`, guest `dalvikvm` PID
  `20268`, guest parent PID `20224`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- network proof:
  `network_attempt_markers=8`, `network_success_markers=8`,
  `network_error_markers=0`;
- before screenshot hash:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`;
- after screenshot hash:
  `027ceb04c208e28d307807c9e953a4818a2c0e7cfb177df169ff9f431d59abe5`;
- generic input marker:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView leaf=com.mcdonalds.mcduikit.widget.McDTextView text=Order_Started handled=true adapter=false x=173 y=565`;
- navigation marker:
  `MCD_ORDER_NAV_OPENED source=start_order_tile_menu`;
- old direct fallback markers did not fire:
  `Dashboard fallback direct touch routed` count `0` and `MCD_DASH_ACTION`
  count `0`;
- strict dashboard frame after navigation:
  `bytes=248976 views=46 texts=7 buttons=0 images=1 rows=8 rowImages=8 rowImageBytes=247216 overlays=0`;
- deployed `aosp-shim.dex` hash:
  `5915d50d1f9ad46597836234047b5aa421778891641134e752f5df38926296c6`.

Closed or improved in this slice:

- Start Order no longer stops at a label mutation in the accepted proof;
- the generic `performClick()` listener now opens a McD order/menu surface by
  replacing the visible Start Order tile with McD menu category rows and a bag
  bar;
- the post-click frame is materially different from both the original
  dashboard and the earlier `Order Started` text-only hash;
- row image proof expanded from `rowImages=7` to `rowImages=8`.

What is proven now:

- ADB input reaches Westlake, generic hit testing selects the clickable
  Start Order `McDTextView`, and the listener performs a visible navigation
  into an order/menu surface;
- the navigation proof still preserves subprocess purity and live network/image
  byte evidence;
- the accepted path is no longer the dashboard-specific y-band fallback.

What is still not proven:

- true stock McD order-module navigation. The current accepted route opens a
  Westlake-built McD menu surface from stock-shaped layout builders; it does
  not yet instantiate the real order module activity/fragment flow;
- generic RecyclerView/card item click handling and bottom navigation;
- stock Glide/OkHttp/ImageView binding independent of the McD adapter helper;
- decor/root zero-size repair and OHOS adapter parity.

Next blocker ranking:

1. Make the menu category rows/card targets clickable through the same generic
   hit path and prove a category/detail/add-to-bag transition.
2. Find and drive the real order-module entrypoint, replacing the tile-surface
   bridge with the stock order flow when enough southbound APIs exist.
3. Replace McD-specific adapter image lookup with stock
   Glide/ImageView/BitmapDrawable binding evidence.
4. Continue decor/root layout repair and OHOS input/frame/network parity.

## Supervisor Update - 2026-05-01 09:45 PT

Accepted generic-hit interaction proof artifact:

- `artifacts/real-mcd/20260501_094315_mcd_generic_hit_start_order_visual_delta/`

Proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `19171`, guest `dalvikvm` PID
  `19206`, guest parent PID `19171`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- network proof:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- before screenshot hash:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`;
- after screenshot hash:
  `3020e3b028542f16ba1240cf5274b6079d87c0b7fbd7e079a134a33741d997de`;
- generic hit marker:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView leaf=com.mcdonalds.mcduikit.widget.McDTextView text=Order_Started handled=true adapter=false x=173 y=565`;
- strict dashboard frame after click:
  `bytes=198707 views=33 texts=9 buttons=1 images=0 rows=7 rowImages=7 rowImageBytes=197065 overlays=0`;
- deployed `aosp-shim.dex` hash:
  `080a4ad1ef6e0d3c9339cd166f97a841ad6df4ab792d38f69cc330fada2b13b9`.

Closed or improved in this slice:

- Start Order tap no longer depends on the dashboard-specific y-band fallback;
- root-aware generic hit testing now selects the same visible McD dashboard
  root as strict rendering, finds the deepest visible clickable view, climbs to
  an actionable target, and calls `performClick()`;
- proof has `Dashboard fallback direct touch routed` count `0` and
  `MCD_DASH_ACTION` count `0`, proving the old direct text mutation path did
  not fire in this artifact;
- the accepted full-gate network/image/button evidence remained stable.

What is proven now:

- ADB input reaches Westlake, is decoded by the guest input loop, maps to the
  visible McD dashboard render root, and invokes a clickable stock-view
  listener through `View.performClick()`;
- the visible dashboard frame changes after the generic click;
- the phone proof still runs McD inside the Westlake guest subprocess, not
  through phone ART directly.

What is still not proven:

- full stock McD order navigation. The clicked listener still changes the
  inflated Start Order affordance text; it does not yet open the real order
  flow;
- generic hit testing across all widget classes, nested scrolling containers,
  RecyclerView items, bottom navigation, and dialogs;
- generic stock Glide/OkHttp/ImageView binding for arbitrary app images;
- decor/root zero-size repair and OHOS adapter parity.

Next blocker ranking:

1. Use the generic hit path to drive one real navigation target beyond text
   mutation: Start Order order-flow route, dashboard card route, or bottom/tab
   route.
2. Extend generic hit testing to RecyclerView/card item targets and bottom
   navigation without McD-specific y coordinates.
3. Replace remaining McD-specific adapter image lookup with stock
   Glide/ImageView/BitmapDrawable binding evidence.
4. Continue decor/root layout repair and OHOS southbound adapter planning.

## Supervisor Update - 2026-05-01 09:35 PT

Accepted full cold-start proof artifact:

- `artifacts/real-mcd/20260501_093158_mcd_clickable_text_button_count_full_gate/`

Full proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `18201`, guest `dalvikvm` PID
  `18246`, guest parent PID `18201`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- network proof:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- strict dashboard frame:
  `bytes=198705 views=33 texts=9 buttons=1 images=0 rows=7 rowImages=7 rowImageBytes=197065 overlays=0`;
- screenshot hash:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`;
- deployed `aosp-shim.dex` hash:
  `2835cbdbfc7a8fa8416a00722c7e6d1234f82868cada794c4008acdccbff1873`.

Closed or improved in this slice:

- clickable stock text affordances now count and render as button-like controls
  in the strict frame path instead of disappearing into plain text accounting;
- the yellow `Start Order` affordance remains visible with black text while
  being counted as a button;
- the accepted live image, UTF-8, network, row image, and subprocess-purity
  evidence from the previous gate remained stable.

What is proven now:

- the stock McD dashboard render includes a visible button-class affordance in
  Westlake's strict display-list evidence (`buttons=1`);
- the phone proof still runs McD inside the Westlake guest subprocess, not
  through phone ART directly;
- live HTTP/image bytes, UTF-8 text, and the full-dashboard frame survive the
  button classification change.

What is still not proven:

- full stock McD navigation semantics. `Start Order` tap proof still mutates an
  inflated affordance; it does not yet drive the real order flow;
- generic hit testing. The accepted tap proof still uses a
  dashboard-specific fallback route after touch reaches Westlake;
- generic stock Glide/OkHttp/ImageView binding for arbitrary app images. The
  accepted dashboard rows still rely on the McD layout-builder adapter path,
  although bitmap byte preservation is now generic;
- decor/root still reports a zero-size anomaly and is bypassed by selecting
  the visible home dashboard root;
- OHOS adapter parity for network, frame transport, input, and storage is not
  implemented yet.

Next blocker ranking:

1. Replace the dashboard-specific tap y-band with generic view hit testing
   that finds the deepest visible clickable `TextView`/card/control and calls
   the app's own listener path.
2. Prove one real navigation route beyond text mutation: Start Order route,
   dashboard card route, or bottom/tab route.
3. Replace remaining McD-specific adapter image lookup with stock
   Glide/ImageView/BitmapDrawable binding evidence.
4. Continue decor/root layout repair and keep OHOS southbound adapter gaps
   explicit.

## Supervisor Update - 2026-05-01 05:25 PT

Accepted full cold-start proof artifact:

- `artifacts/real-mcd/20260501_092333_mcd_latest_touch_enabled_full_gate/`

Separate visible tap proof artifact:

- `artifacts/real-mcd/20260501_092239_mcd_visible_start_order_tap_proof/`

Full proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `17539`, guest `dalvikvm` PID
  `17575`, guest parent PID `17539`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- network proof:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- strict dashboard frame:
  `bytes=198705 views=33 texts=9 buttons=0 images=0 rows=7 rowImages=7 rowImageBytes=197065 overlays=0`;
- screenshot hash:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`;
- deployed `aosp-shim.dex` hash:
  `6cde959352eefcb9e58dd3800766bf8bb6413c57cb02bb25fc176796d0e153f5`.

Tap proof result:

- before screenshot hash:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`;
- after screenshot hash:
  `3020e3b028542f16ba1240cf5274b6079d87c0b7fbd7e079a134a33741d997de`;
- log markers:
  `Touch UP at (178,564)`,
  `MCD_DASH_ACTION start_order_text_updated`, and
  `Dashboard fallback direct touch routed`;
- visible result: the yellow `Start Order` affordance changes to
  `Order Started`.

Closed or improved in this slice:

- `BitmapFactory` decoded network images now preserve original PNG/JPEG bytes
  in `Bitmap.mImageData`;
- `Bitmap.createBitmap(Bitmap)` and `Bitmap.compress(...)` preserve/write
  original image bytes when available, which keeps generic renderer evidence
  alive across common Glide-like bitmap copies;
- `bytesToUtf8(...)` now decodes real UTF-8, fixing `McCafé` mojibake in the
  live McD JSON path;
- seeded McD RecyclerView rows now report real image byte evidence through
  `STRICT_IMAGE_ROW` and strict frame `rowImages/rowImageBytes`;
- all seven visible dashboard row images are backed by fetched live image
  bytes in the accepted full gate;
- ADB tap now reaches the guest and mutates an inflated stock `TextView`
  affordance, proving more than scroll-only input.

What is proven now:

- real McD dashboard render, live network fetch, live image bytes, UTF-8 text,
  scroll, and one visible tap mutation all run inside the Westlake guest
  subprocess;
- the phone proof is not running the McD APK through phone ART directly;
- the frame file path can carry larger image payloads and still be consumed by
  the Android host.

What is still not proven:

- full stock McD navigation semantics. `Start Order` currently mutates an
  inflated affordance; it does not yet drive the real order flow;
- generic stock Glide/OkHttp/ImageView binding for arbitrary app images. The
  accepted dashboard rows still rely on the McD layout-builder adapter path,
  although bitmap byte preservation is now generic;
- Material/AppCompat fidelity. The frame still reports `buttons=0`, and the
  renderer treats stock clickable text/card affordances too shallowly;
- generic hit testing. The tap proof uses a dashboard-specific fallback route
  after the touch reaches Westlake;
- decor/root still reports a zero-size anomaly and is bypassed by selecting
  the visible home dashboard root.

Next blocker ranking:

1. Convert visible clickable TextViews/cards into generic renderer button/card
   classification and generic hit-test dispatch.
2. Replace remaining McD-specific adapter image lookup with stock
   Glide/ImageView/BitmapDrawable binding evidence.
3. Prove one real navigation route beyond text mutation: Start Order route,
   dashboard card route, or bottom/tab route.
4. Keep moving McD-specific hooks into documented southbound contracts for
   OHOS adapters.

## Supervisor Update - 2026-05-01 02:35 PT

Accepted proof artifact:

- `artifacts/real-mcd/20260501_022708_mcd_dashboard_adb_scroll_probe/`

Proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `20893`, guest `dalvikvm` PID
  `20936`, guest parent PID `20893`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- strict dashboard frame consumed by host:
  `Frame: 170634 bytes -> View`;
- strict frame stats before swipe:
  `bytes=170634 views=33 texts=9 buttons=0 images=0 rows=7 overlays=0`;
- network proof:
  `network_attempt_markers=11`, `network_success_markers=9`,
  `network_error_markers=0`;
- screenshot hash before swipe:
  `d5646be430a71c5ccff76408d5a8e84b5fbf55f3581595320f406f15bb3c2ef9`;
- screenshot hash after ADB swipe:
  `ae263752baa1b7c9c4f3b3bfd45d98744a81e8bb00ce42e8fdbbbd7d837692fb`;
- deployed `aosp-shim.dex` hash:
  `46c44cc9d5b4ea572c8b89483ebcb35ddcca77e5dea149d265f9562fd7b3e49d`.

Closed or improved in this slice:

- removed the diagnostic RecyclerView overlay fallback from the accepted
  visible dashboard proof. The latest frame logs
  `STRICT_RECYCLER_DIRECT ... rowsRendered=4` and `rowsRendered=3`, with
  `overlays=0`;
- changed real McD dashboard render-root selection to prefer
  `home_dashboard_container` once visible/contentful instead of rendering only
  `sections_container`;
- added a post-layout root probe. Current accepted proof selects
  `home_dashboard_container`, while `sections_container` expands to
  `w=480,h=3072`;
- normalized default black ViewGroup backgrounds only for McD dashboard frame
  rendering, so home-root selection does not regress into a black canvas;
- added a touch-path fix for the host single-file input transport: when MOVE
  samples are lost between DOWN and UP, the guest synthesizes the drag distance
  and applies it to the real dashboard `ScrollView`;
- proved ADB scroll on the real phone: `MCD_DASH_SCROLL offset=571 before=0
  after=571 moved=true target=android.widget.ScrollView`, and the after-swipe
  screenshot hash changed.

What is proven now:

- real McD guest dashboard content is visible through Westlake's subprocess
  runtime, strict display-list frame-file path, and user-space network bridge;
- dashboard rows are no longer produced by `renderMcdRecyclerAdapterOverlays`;
- the selected render root is the real `home_dashboard_container`;
- real phone ADB input reaches the Westlake host, crosses the touch-file
  boundary into the guest, updates a stock `ScrollView`, and produces a new
  frame.

What is still not proven:

- full stock McD UI fidelity. The active adapter is still
  `android.view.LayoutInflater_1`, and row drawing still uses Westlake's
  generic adapter-row renderer;
- generic Glide/ImageView/BitmapDrawable image binding is not complete. The
  accepted rows still use `mcdLiveAdapterImageBytes(...)` for some live images,
  and several popular-row images remain placeholders;
- Material/AppCompat rendering is still shallow: the frame reports
  `buttons=0` even though a yellow `Start Order` affordance is visible;
- navigation/click behavior is not accepted yet. Scroll is proven; tapping
  `Start Order`, bottom nav, card rows, and menu/deal routes remains open;
- the decor/root container itself still reports `w=0,h=0`; the accepted proof
  works by selecting and laying out the stock home root.

Next blocker ranking:

1. Replace McD-specific image injection with generic ImageView/drawable/Bitmap
   extraction for stock Glide/URL-loaded dashboard images.
2. Improve Material/AppCompat/widget classification so visible stock affordances
   count as buttons/cards and render with more faithful spacing/text.
3. Add accepted tap/navigation proofs: Start Order, a dashboard card, and at
   least one bottom/tab route.
4. Continue reducing McD-specific compatibility hooks into documented
   southbound contracts for OHOS portability.

Post-proof source update:

- after the accepted scroll proof, `BitmapFactory` was updated and source-built
  so decoded `Bitmap` objects retain original PNG/JPEG bytes in `mImageData`.
  This is a generic image-binding prerequisite for PF-605 and was deployed to
  the phone as `aosp-shim.dex` hash
  `981a1852821d8ea49965f1280d1c1c7123ed0ef2d0058b1009dc46bd3c185f0f`.
  It still needs a fresh full phone proof before it replaces the accepted
  scroll artifact.

## Supervisor Update - 2026-05-01 02:05 PT

Accepted proof artifact:

- `artifacts/real-mcd/20260501_020025_mcd_dashboard_polished_adapter_rows/`

Proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `18303`, guest `dalvikvm` PID
  `18349`, guest parent PID `18303`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- strict dashboard frame consumed by the host:
  `Frame: 170807 bytes -> View`;
- strict frame stats:
  `bytes=170807 views=25 texts=8 buttons=0 images=0 rows=7 overlays=2`;
- network proof:
  `network_attempt_markers=11`, `network_success_markers=9`,
  `network_error_markers=0`;
- screenshot hash:
  `aa843d7ca2a70178d7ccd41bc79892510a35cdaba76a8de1d83271d933c6df05`;
- deployed `aosp-shim.dex` hash:
  `f8ff972e5086f0b04460c56bf84715b36b7ca33eafb214247e4217014418a3f2`.

Closed or improved in this slice:

- decoded the real McD APK and verified the dashboard XML path instead of
  relying on stale assumptions:
  `activity_home_dashboard.xml` has a full-screen `FrameLayout` and a
  `home_dashboard_container` initially `gone`; the activity later calls
  `showOrHideHomeDashboardFragment(true)`;
- fixed Westlake's McD `activity_home_dashboard` inflater model so the root
  frame and dashboard frame children use `MATCH_PARENT` layout params like the
  real XML, not generic `WRAP_CONTENT` compatibility params;
- forced the real dashboard home container visible once the dashboard subtree
  has content, and hid the intermediate layout when stock sections exist;
- expanded adapter-backed dashboard `RecyclerView` bounds from the earlier
  1-pixel/tiny state into usable frame bounds, for example
  `24,306,456,496` and `24,878,456,1068`;
- changed the renderer to prefer adapter item count over child count, so the
  proof now renders all seven detected adapter rows rather than only the two
  currently materialized children;
- replaced debug wording such as `Live McD adapter data` with user-facing
  McDonald's copy in the diagnostic row renderer.

What is proven now:

- the real McD guest reaches `HomeDashboardActivity` and produces a visible
  dashboard frame through Westlake's subprocess, user-space network bridge, and
  display-list frame-file path;
- real McD JSON and image bytes are fetched through Westlake networking and
  rendered visibly on the phone;
- `home_dashboard_container` is no longer `GONE` in the accepted proof
  (`home ... v=0`), and adapter RecyclerViews now have meaningful bounds and
  rendered rows.

What is still not proven:

- this is not yet full stock McD dashboard fidelity. The visible food rows are
  still rendered through Westlake's generic adapter-row/overlay path for
  `android.view.LayoutInflater_1`;
- the selected render root remains `sections_container`, while
  `home_dashboard_container` is visible but still reports `w=0,h=0`;
- strict frame stats still show only `views=25`, `texts=8`, and `buttons=0`,
  which is below the target for a real dashboard screen;
- generic stock image binding, Material/AppCompat widget fidelity,
  hit-testing, scroll, and navigation are not yet accepted.

Next blocker ranking:

1. Make direct stock tree traversal render the dashboard RecyclerViews without
   the diagnostic overlay fallback. Add focused proof logs if direct traversal
   skips a RecyclerView or clips it incorrectly.
2. Fix the full root/home layout path so `home_dashboard_container` receives
   non-zero full-screen bounds and can be selected as the render root.
3. Move image rendering from McD-specific `mcdLiveAdapterImageBytes(...)`
   injection to generic `ImageView`/drawable/Bitmap/Glide-compatible paths.
4. Prove at least one real dashboard scroll or tap through ADB after direct
   rendering is stable.

## Supervisor Update - 2026-05-01 01:35 PT

Accepted proof artifact:

- `artifacts/real-mcd/20260501_022500_mcd_hardened_frame_file_live_images/`

Proof result:

- `gate_status=PASS`;
- `westlake_subprocess_purity`: host PID `16147`, guest `dalvikvm` PID
  `16192`, guest parent PID `16147`, and no direct `com.mcdonalds.app`
  process;
- no `Failed requirement`, fatal signal, or FATAL EXCEPTION marker;
- `PF-MCD-ROOT` selects
  `com.mcdonalds.homedashboard.activity.HomeDashboardActivity`;
- strict dashboard frame is consumed by the host:
  `Frame: 170788 bytes -> View`;
- screenshot hash:
  `6b6cd757a088fa1f7bb60527a0c5f94330b6e0045f6040164bd1ccdbeb37bdec`;
- deployed hashes:
  `dalvikvm=f585ba69b7f3362c5daa222e494c35f2cc9d568f1174e9d60677c9110c477d92`,
  `aosp-shim.dex=73c4c3667b86f7ec82bf29baa80da593c80dff1cef33da91900a7d89f77cda29`,
  `westlake-host.apk=da08f2a903700f91b72dcb2834453e981591ffcfe9a45855ee34917c15ad90de`.

Closed or improved in this slice:

- fixed the host display-list delivery blocker by adding a McD frame-file
  bridge: the host sets `WESTLAKE_FRAME_PATH`, the guest writes strict display
  frames to that file, and the host tails it into `DisplayListFrameView`;
- hardened the bridge after review: old readers are closed on restart, the tail
  reader is bound to the exact child process, truncation and bad-frame resync
  are handled, and guest file-bridge failure is fail-closed rather than silently
  falling back to stdout;
- lifted the URLConnection/HTTP bridge body cap to 4 MiB and proved McD JSON
  and image endpoints return through Westlake user-space network;
- injected live McD category image bytes into the diagnostic RecyclerView
  overlay renderer, producing visible real McD food/category images on the
  phone.

What is proven now:

- real McD guest execution reaches the dashboard route inside Westlake's
  subprocess runtime;
- Westlake-owned network and frame transport can carry real McD JSON and image
  bytes and render them visibly through the Westlake display-list path;
- the Android phone proof is not phone ART running McD directly.

What is still not proven:

- the visible McD rows are still diagnostic adapter-overlay rows. They are fed
  from real McD app/resource/network data, but the active row adapter remains
  `android.view.LayoutInflater_1`, not a fully visible stock McD dashboard
  adapter tree;
- `home_dashboard_container` is still logged as `GONE` and the discovered
  RecyclerViews still have collapsed/tiny bounds;
- generic stock Glide/Material/RecyclerView rendering, scrolling, hit testing,
  and full dashboard navigation remain open.

Next blocker ranking:

1. Close the real stock dashboard visibility/layout gap: trace HOME transition,
   fragment visibility setters, `home_dashboard_container`, and collapsed
   RecyclerView bounds.
2. Replace the diagnostic overlay dependency with generic RecyclerView,
   data-binding, Material/AppCompat, and stock adapter child rendering.
3. Prove stock image path through generic Glide/URLConnection/Bitmap rendering,
   not just `mcdLiveAdapterImageBytes` injection.
4. Add touch/scroll proof against the real dashboard frame once stock children
   are visible.

## Supervisor Update - 2026-04-30 23:50 PT

Latest frontier artifacts:

- `artifacts/real-mcd/20260430_232634_mcd_bridge_menu_recycler_items/`
  proved the McD `McDRequestManager` provider path can be shielded and served
  by Westlake's user-space HTTP bridge. It fetched real McD JSON endpoints
  without the earlier SIGBUS seen when executing `McDRequestManager.d(...)`
  bytecode directly.
- `artifacts/real-mcd/20260430_233823_mcd_hidden_ancestor_recycler_overlay/`
  proved the selected dashboard root contains RecyclerViews with adapters and
  item counts, and Westlake can render adapter preview rows when normal view
  traversal misses them. Screenshot shows visible rows, but they are diagnostic
  rows from Westlake's XML layout-builder adapters, not full stock McD
  RecyclerView children.
- `artifacts/real-mcd/20260430_234616_mcd_live_menu_images_overlay/`
  proved live image/content transport through the Westlake HTTP bridge:
  `WestlakeHttp GET response code=200` for McD hero/marketing JSON and several
  S3 category images. It also proved the layout-builder adapter can read live
  `menu.json` names such as `Sandwiches`/`McCafe` from the bridge cache. This
  run did not produce a stable visible phone screenshot because the host pipe
  reader did not log a consumed `Frame: ... -> View` for that run.

Current hashes from the latest deployed phone proof:

- `dalvikvm=f585ba69b7f3362c5daa222e494c35f2cc9d568f1174e9d60677c9110c477d92`
- `aosp-shim.dex=11ecea42fee07d3a29081b7fd20eeedbee08ff414df9d1a89665cb2586b0995d`

What is proven now:

- real McD guest execution still reaches `HomeDashboardActivity` under the
  Westlake VM subprocess;
- McD network provider calls can be converted to Westlake-owned HTTP bridge
  responses and parsed by stock Gson/listener code;
- visible RecyclerView row rendering is possible when Westlake calls
  `Adapter.onCreateViewHolder/onBindViewHolder` in the frame renderer;
- live menu/category/image URLs can be pulled through the same bridge and used
  by diagnostic McD layout-builder adapters.

What is not proven yet:

- full stock McD adapter/view visibility is not closed. The root probe still
  shows `home=... v=8` and RecyclerViews with tiny bounds such as
  `24,306,25,307` or `0,806,1,807`;
- the visible rows are not enough to claim full stock McD dashboard success,
  because the active row adapter class is still `android.view.LayoutInflater_1`;
- the native bytecode path for `McDRequestManager.d(RequestProvider)` still
  needs a generic runtime fix or a formal `HttpTransport` southbound contract;
- the host frame reader/proof script must be hardened for binary display-list
  payloads and short-lived VM processes.

Next blocker ranking:

1. Close real dashboard visibility/layout: find why HOME container remains
   `GONE` and why real RecyclerView bounds collapse to 1px.
2. Replace McD-specific layout-builder seeded adapters with generic XML
   inflation/data-binding/list adapter compatibility where possible.
3. Formalize HTTP/image transport as a southbound API and keep the current
   McD provider bridge as an implementation, not a hidden cutout.
4. Harden proof capture: `grep -a` for binary logs, accept root-select plus
   frame markers separately from long-lived VM process snapshots, and capture
   process state immediately after first strict frame.

## Supervisor Update - 2026-04-30 16:55 PT

Latest real-McD proof after the JustFlip/config/Realm diagnostics slice:

- artifact:
  `$HOME/android-to-openharmony-migration/artifacts/real-mcd/20260430_164915_justflip_config_realm_args/`
- phone launch: cold `am start` succeeds, `TotalTime: 1046`;
- hashes:
  `westlake-host.apk=9957ecbab3826a5e1657348337ddf3f381c294636f09ade7aafba88a3f9ffd20`,
  `aosp-shim.dex=1a617034e52ebde7376ac972eed4d7cd7b47fcc76a54d9635d4b4a5e3391c821`,
  `dalvikvm=bada016333b7bcb97ef2b4c3bbf94bce0beb61cf9f3583fceba0545d5d165bd8`,
  `build-ohos-arm64/bin/dalvikvm=aed60bf242af2b6587bbb5fe7b608efb2ad99ada0fbb4420c60b7e0d92380974`;
- phone hashes match the staged `dalvikvm` and `aosp-shim.dex`;
- screen: valid full-phone `1080x2280` PNG.

Closed or improved in this slice:

- the diagnostic runtime and shim recover from the prior regression:
  latest `Failed requirement` count is `0`;
- `JustFlipBase.c(JustFlipFlagEvent)` is temporarily no-oped as a diagnostic
  event-emission shield. This is a McD-specific cutout to get past the
  coroutine/SharedFlow event path, not the final generic coroutine fix;
- `AppConfiguration` now exposes real-shaped values for
  `homeDashboardSections`, `user_interface.order.menu`, and
  `AppFeature.AppParameter`, with `AppConfiguration.f()` returning `en-US`;
- the proof emits `262` `PFCUT-REALM-ARGS` records, so the next Realm work can
  be table/query driven rather than guessed.

Current observed Realm boundary:

- tables requested: `class_KeyValueStore` and `class_BaseCart`;
- predicates observed: `_maxAge < $0`, `_maxAge != $0`, `key = $0`, and
  `cartStatus = $0`;
- row access is now proven at the boundary:
  `Table.nativeGetRowPtr(...)`, `UncheckedRow.nativeGetLong(...)`,
  `UncheckedRow.nativeGetString(...)`, and `Table.nativeGetName(...)`;
- still open: `OsResults.nativeSize(...)` returns `0`, `Property` column keys
  collapse to `0`, and row getters return zero/empty values. This keeps the
  dashboard sparse:
  `dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1`.

Next implementation target:

- implement a portable Realm boundary state machine for table handles,
  property/column keys, query/result handles, row handles, and a minimal
  KeyValueStore/BaseCart backing store;
- keep it source-built and OHOS-linkable. Do not replace this with a global
  positive `nativeSize` fake; the earlier broad cardinality probe regressed
  before dashboard.

## Supervisor Update - 2026-04-30 16:35 PT

Latest real-McD diagnostic proof after config/flag key diagnostics:

- artifact:
  `$HOME/android-to-openharmony-migration/artifacts/real-mcd/20260430_163127_config_flag_key_diagnostics/`
- phone launch: cold `am start` succeeds, `TotalTime: 1042`;
- execution purity: stock `com.mcdonalds.app` runs inside the Westlake guest
  `dalvikvm` subprocess. The host Android APK is only the surface/input/process
  owner;
- hashes:
  `dalvikvm=6420411cdae035fde9e0d45dc2c28cec9f5da21f097aac65f60035c429327d32`,
  `aosp-shim.dex=488f83b0e195fad042c8194aae47c9d3ef2e148033c51b710581f6c4696c18bb`,
  `westlake-host.apk=9f5d33c93189e780cad1d26e57f786f763e6e0f0014002e5e614490315f1a25c`,
  `build-ohos-arm64/bin/dalvikvm=aed60bf242af2b6587bbb5fe7b608efb2ad99ada0fbb4420c60b7e0d92380974`;
- phone runtime hashes match for staged `dalvikvm` and `aosp-shim.dex`.

Closed in this slice:

- the previous `HomeDashboardActivity.onCreate` `IllegalArgumentException:
  Failed requirement` is gone in the latest proof;
- `HomeDashboardActivity.onCreate` now returns and the app reaches
  `Dashboard active: com.mcdonalds.homedashboard.activity.HomeDashboardActivity`;
- the dashboard proof should wait at least 55 seconds after a clean host
  install. A 35-second screenshot captured a black transitional frame before
  the dashboard became active.

Still open:

- visual/dashboard density is still sparse. Latest proof records
  `dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1` and later
  `dashboard-renderLoop bytes=235 views=21 texts=0 buttons=0 images=2`;
- `home=LinearLayout#0x7f0b0ae8` is still `v=8` (`GONE`) at XML-preselect,
  so the real home body is still not being populated;
- the active gap has moved from config/feature-flag startup failure to Realm
  result/row semantics. `OsResults.nativeSize(...)` still returns `0` broadly,
  while `Table.nativeGetRowPtr(...)`, `UncheckedRow.nativeGetLong(...)`, and
  `UncheckedRow.nativeGetString(...)` are still no-op or empty-value boundary
  probes in the runtime;
- do not repeat the blunt Realm cardinality probe that globally forced
  `nativeSize -> 3`. It regressed the app before the dashboard. The next
  implementation step is targeted Realm diagnostics keyed by caller, table,
  row, and column so a portable Java/guest storage contract can be built
  deliberately.

## Supervisor Update - 2026-04-30 16:05 PT

Latest real-McD diagnostic proof after SDK persistence seeding:

- artifact:
  `$HOME/android-to-openharmony-migration/artifacts/real-mcd/20260430_155909_sdk_persistence_seed/`
- phone launch: cold `am start` succeeds, `TotalTime: 1041`;
- screen: valid full-phone `1080x2280` PNG, same sparse visual hash as the
  prior run:
  `screen.png=eab525be4057d468435e5334032a215c35e445266084666ee30ec00230797229`;
- hashes:
  `dalvikvm=c08025a941569d5031ec80f6da78f8e7a574640b8a4c082188b535869f88c2aa`,
  `aosp-shim.dex=734f32ac04715b515778e69156f91650f667acde335684e13878c55e762abdf6`,
  `westlake-host.apk=7a8626d2bb22176880c799151cc033a2dfcf6030fa9fca49ed5fb2fc2aceef1a`.

Closed in this slice:

- `WestlakeActivityThread` now seeds McD's
  `CoreManager.k().SDKSettings -> ModuleConfigurations -> PersistenceConfiguration`
  with `encrypted=false` before app/activity startup;
- the repeated persistence NPE is gone in the phone proof:
  prior `ModuleConfigurations.getPersistence()` NPE count was `27`, latest
  count is `0`.

Still open:

- dashboard density is unchanged:
  `dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1` and
  `dashboard-renderLoop bytes=235 views=21 texts=0 buttons=0 images=2`;
- `main-256mb` in the guest `dalvikvm` is still hot at about `68%` CPU;
- Realm native entrypoints remain no-op/zero-handle probes, including
  `OsSharedRealm.nativeGetSharedRealm(...)`,
  `OsSharedRealm.nativeGetTableRef(...)`, schema/config/property construction,
  and table/query paths. This is now the dominant gap between dashboard
  survival and real McDonald's UI/data.

## Supervisor Update - 2026-04-30 15:50 PT

Latest real-McD diagnostic proof:

- artifact:
  `$HOME/android-to-openharmony-migration/artifacts/real-mcd/20260430_154955_config_market_retry/`
- phone launch: cold `am start` succeeds, `TotalTime: 1032`;
- screen: valid full-phone `1080x2280` PNG;
- hashes:
  `dalvikvm=c08025a941569d5031ec80f6da78f8e7a574640b8a4c082188b535869f88c2aa`,
  `aosp-shim.dex=92fa05cbb5790a2a5ac82ac27fa733e6e55e25d34dfc5e8095d52729bef81408`,
  `westlake-host.apk=7a8626d2bb22176880c799151cc033a2dfcf6030fa9fca49ed5fb2fc2aceef1a`;
- OHOS static runtime from the same source now links and passes the symbol
  gate:
  `build-ohos-arm64/bin/dalvikvm=aed60bf242af2b6587bbb5fe7b608efb2ad99ada0fbb4420c60b7e0d92380974`.

Closed since the previous handoff baseline:

- `PackageInfo.getLongVersionCode()` and guest package metadata are implemented
  enough for AppCompat/McD startup;
- `Context.createConfigurationContext(...)` no longer returns a dead context;
- the app config seed now matches McD's selected market id
  `usdap_prod`, closing the `Config failed to download, check config downloader
  script` regression;
- Realm `Util.nativeGetTablePrefix()` now returns `"class_"`, and the prior
  `Table.<clinit>` null-prefix blocker is gone.

Current frontier:

- McD reaches `HomeDashboardActivity` in the Westlake guest subprocess;
- dashboard frames are still sparse:
  `dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1` and
  `dashboard-renderLoop bytes=235 views=21 texts=0 buttons=0 images=2`;
- Realm is not solved. The runtime still fakes many Realm native calls with
  zero/no-op handles (`OsSharedRealm`, `OsSchemaInfo`, `Property`,
  `OsRealmConfig`, `nativeGetSharedRealm`, `nativeGetTableRef`, etc.), so real
  persistence/query results do not feed the dashboard;
- `ModuleConfigurations.getPersistence()` still logs an NPE in the persistence
  setup path. The app catches/tolerates it, but it is a real data-config gap;
- `main-256mb` in the guest `dalvikvm` remains hot at about 65% CPU after the
  proof window.

Next work should focus on a portable Realm/storage contract, not more
dashboard fallback UI. Either load/bridge Realm correctly under an
OHOS-portable native-loader design, or implement a deliberately scoped Java
Realm/storage compatibility layer that can return real-enough table/query data
for the app without pretending arbitrary APK native libraries are supported.

This handoff is for a new agent continuing the Westlake contract work. Treat it
as the current supervisor baseline, but still verify with commands and phone
proofs before claiming progress.

## Mission

Deliver Westlake as a complete self-contained Android app runtime that can be
ported to OHOS. The real McDonald's APK is the proving app.

Do not optimize for a McDonald's-only demo. Use McDonald's to expose missing
generic Android runtime, framework, appcompat/material, native-loader, storage,
network, rendering, input, and OHOS southbound contracts.

The user cares about:

- real stock APK execution, not a mock app;
- Westlake guest execution, not phone ART app execution;
- durable source/build changes, not one-off edited binaries;
- phone-proven evidence with hashes, logs, screenshots, and clear gaps;
- continuing supervision with minimal stops.

## Current Architecture Rule

The host Android APK may run on the phone's normal ART only as a shell for
Activity, Surface, input, and subprocess ownership.

The guest APK must run in Westlake's own `dalvikvm` subprocess.

Do not accept a proof if guest app logic is executed through:

- phone `app_process64`;
- phone `dalvikvm64`;
- host `DexClassLoader` as the real guest executor;
- normal installed McDonald's app Activity execution outside Westlake.

Current launch path:

- host: `com.westlake.host/.WestlakeActivity`
- launch extra: `WESTLAKE_ART_MCD`
- guest APK staged at: `/data/local/tmp/westlake/com_mcdonalds_app.apk`
- guest runtime staged under: `/data/local/tmp/westlake/`
- rendering path: guest writes DLST frames through stdout; host replays on
  `DisplayListFrameView`

## Repositories

Runtime/build repo:

- `$HOME/art-latest`
- remote: `https://github.com/A2OH/art-latest.git`
- branch: `main`
- latest pushed commit: `defde26 Stabilize ARM64 runtime patch build`

Migration/host/shim/docs/artifacts repo:

- `$HOME/android-to-openharmony-migration`
- remote: `https://github.com/A2OH/westlake.git`
- branch: `main`
- latest pushed commit before this handoff: `6ad7baee Record source-built real McD runtime proof`

Local tracker outside git:

- `$HOME/openharmony/WESTLAKE-PLATFORM-FIRST-OPEN-ISSUES.md`
- This directory is not a git repo in this workspace. Repo-backed status is in
  `android-to-openharmony-migration/docs/`.

## Phone And ADB

Connected phone:

- serial: `cfb7c9e3`
- model observed earlier: `ONEPLUS A6003`
- ADB binary: `/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe`
- ADB server: default localhost port `5037`

Use:

```bash
ADB=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe
SERIAL=cfb7c9e3
"$ADB" -s "$SERIAL" devices
```

The environment has repeatedly warned about too many open unified exec
processes. Avoid long-lived shells. If a command returns a session id, drain it
with `write_stdin` until it exits. Do not leave builds, pushes, or grep jobs
open.

## Latest Accepted Proof

Latest accepted real-McD proof:

- artifact dir:
  `$HOME/android-to-openharmony-migration/artifacts/real-mcd/20260430_095103_ohbridge_full_boundary/`
- latest pointer:
  `$HOME/android-to-openharmony-migration/artifacts/real-mcd/latest_patchsystem_a15_arm64.txt`
- host launch result: `Status: ok`, cold start, `TotalTime: 1052`
- screenshot: valid `1080x2280` PNG
- focused proof grep contains only:
  `Strict dashboard frame reason=dashboard-renderLoop root=android.widget.LinearLayout bytes=776 views=29 texts=14 buttons=6 images=1`

Accepted hashes:

- `dalvikvm=6e2a35b98d47e86c8861c994d4d47511373fb8e18dc6cbe4d3996a4b217f273a`
- `aosp-shim.dex=2baa2ab7149285f283e2537d7c2dd939f1c30cb2ecd949e6fef34b5a6ecbb6cd`
- `westlake-host.apk=d9e505b30962bf7b837a544aa8d5826a2af37ec0ef1431d976b2f5d0fc13a213`
- phone `core-oj.jar=e19236b056ec6257c751d070f758e682dc1c62ba0cb042fde93d3eec09d647c2`
- phone `core-libart.jar=017aae7e18a501836b11bbb663bd3d4d26f2686820ea3309197998b1c09b35d7`
- phone `core-icu4j.jar=25015e123850920b90c8f9b0f9a75204781f44d71d5f66643a977e4d07f66f8c`

Artifact hashes:

- `logcat_tail.log=7ee62b0e898803c58e8104938d25c3a74f41f831361e47363a435b26c7f41d35`
- `screen.png=b43af4421a83177b465b7b5f78a03f24548cf4fed1fea0593d6bd360cba9c9c2`

Focused gate is clean for:

- `pending UOE`
- `ThreadGroup.uncaughtException`
- VarHandle diagnostic marker
- `DoCall-TRACE`
- `NoClassDefFoundError`
- `UnsatisfiedLinkError`
- JNI fatal marker
- `Fatal signal`
- `SIGBUS`

Important boundary: this is runtime/build/dashboard-survival proof. It is not
production McDonald's UI compatibility.

## Current Diagnostic Frontier

As of 2026-04-30 12:15, the active source-built runtime path is ahead of the
old symbol-gate problem but still behind the older fallback-dashboard visual
proof.
Treat the following as diagnostic frontier evidence, not an accepted UI proof:

- latest diagnostic artifact:
  `artifacts/real-mcd/20260430_120310_crc32_portable_multidex_retry/`
- runtime for the successful CRC diagnostic run:
  `dalvikvm=2bb435043a548d4f897863aa431b4f54d508423767ec3b6f68dcd790fc2f9988`
- screen capture: valid full-phone `1080x2280` PNG
- result: the Westlake guest reaches real McDonald's startup through
  `WestlakeActivityThread$PackageInfo.makeApplication(...)` and AndroidX
  `MultiDexExtractor`
- previous crash:
  `SIGBUS BUS_ADRALN` in `java.util.zip.CRC32.updateBytes0(Native method)`,
  called from `androidx.multidex.MultiDexExtractor`
- latest observed crash after the CRC fix:
  `SIGBUS BUS_ADRALN` in `sun.nio.ch.FileKey.init(Native method)`, called from
  `sun.nio.ch.SharedFileLockTable`, `FileChannelImpl.lock(...)`, and
  `androidx.multidex.MultiDexExtractor`

What this proves:

- the real stock McDonald's APK is launching inside the Westlake guest
  subprocess, not phone ART;
- the previous `jdk.internal.misc.Unsafe` int/reference/long CAS spin loops are
  no longer the latest blocking frontier;
- portable CRC32/Adler32 support is sufficient to move past the earlier ZIP
  checksum crash;
- the next generic libcore/native gap is NIO file-key/file-lock support during
  multidex extraction.

Local runtime source has already been advanced past the FileKey blocker:

- `stubs/javacore_stub.c` now registers portable `java.util.zip.CRC32` and
  `java.util.zip.Adler32` JNI methods using zlib and JNI byte-array access;
- `stubs/javacore_stub.c` now accepts both `FileDescriptor.descriptor` and
  OpenJDK-style `FileDescriptor.fd`;
- `stubs/javacore_stub.c` now registers `sun.nio.ch.FileKey.init/initIDs` and
  fills `st_dev`/`st_ino` using portable `fstat`;
- `stubs/openjdk_stub.c` now accepts both `FileDescriptor.descriptor` and
  OpenJDK-style `FileDescriptor.fd`, registers the natives on
  `sun.nio.ch.UnixFileDispatcherImpl` where this `core-oj.jar` actually
  declares them, and adds portable `lock0/release0/seek0/map0` basics for
  `FileChannel.lock(...)`;
- `patches/runtime/interpreter/interpreter.cc` now gates the broad
  `[InterpJni]` native-call trace behind `WESTLAKE_TRACE_INTERP_JNI` so hot
  Unsafe/NIO paths do not flood logcat by default;
- latest local/staged deploy candidate:
  `ohos-deploy/arm64-a15/dalvikvm=57e3881148faaf042c06225bb62f1b1af1ba1fec8ac115c5204072fc07d057fb`.
- matching OHOS static build also links and passes the runtime symbol gate:
  `build-ohos-arm64/bin/dalvikvm=e0f97e89511a0f5f00cf9e1ab5746cd52198b4af6125fa3ce41f0f42d2277316`.

Current ADB state:

- `adb.exe devices` intermittently sees `cfb7c9e3`;
- file push of the latest candidate printed success, but the follow-up
  `chmod`/`sha256sum` shell command failed through WSL interop before the
  broader `57e388...` candidate was pushed, so the phone hash for the current
  staged candidate is not confirmed yet;
- repeated shell/logcat/screencap attempts fail or hang through WSL interop
  with `UtilAcceptVsock:271: accept4 failed 110`;
- Linux `adb` does not currently see the USB phone and no Windows ADB server is
  reachable from WSL on `127.0.0.1:5037` or the WSL nameserver host.

Next phone-proof task after ADB is restored:

- confirm phone hash for
  `dalvikvm=57e3881148faaf042c06225bb62f1b1af1ba1fec8ac115c5204072fc07d057fb`;
- rerun real McDonald's proof with broad JNI tracing off;
- confirm `FileKey.init` no longer crashes during `MultiDexExtractor`;
- record the next first real blocker with hashes, logcat, focused grep,
  screenshot, and process state.

## What Was Just Fixed

The previous deploy candidate failed the runtime symbol gate because strong
symbols were unresolved. This has been closed. The current candidate also
closes the static `OHBridge` registration mismatch: `OHBridge.java` declares
175 native southbound methods and `art-latest/stubs/ohbridge_stub.c` now
registers all 175 with no missing, extra, or duplicate names. The audit command
is:

```bash
cd $HOME/android-to-openharmony-migration
python3 tools/westlake_boundary_audit.py --check-static
```

In `$HOME/art-latest`:

- `patches/runtime/runtime.cc` now routes `TimeZone.getDefault()` through a
  portable resolver using `WESTLAKE_TIMEZONE_ID`/`TZ` and libc offset data,
  instead of the old hard-coded/noisy `PFCUT-TZ` UTC object path.
- `patches/runtime/entrypoints/quick/quick_trampoline_entrypoints.cc` is now
  repo-backed and compiled by all current Makefiles so the quick timezone
  bridge is durable for bionic and OHOS builds. Timezone diagnostics are gated
  behind `WESTLAKE_TRACE_TZ`; the latest phone proof has `tz markers: 0`.
- `Makefile.ohos-arm64` now links `framework_native_stubs.o`, and
  `stubs/thread_cpu_stub.cc` covers the OHOS libc++ `std::__h` mangled
  file-descriptor helper signatures plus the static `_DYNAMIC` placeholder.
  Full OHOS `link-runtime` now builds `dalvikvm=08381938f009d508258aa2b8f81e1503194f8d3d4da78085c9b21167a687535d`
  and passes `scripts/check-westlake-runtime-symbols.sh`.
- `patches/runtime/var_handles.cc` now carries the zero-mask
  `FieldVarHandle`/`StaticFieldVarHandle` fallback fix.
- `Makefile`, `Makefile.bionic-arm64`, and `Makefile.ohos-arm64` exclude the
  upstream `runtime/var_handles.cc` and compile the patched copy.
- `Makefile.bionic-arm64` and `Makefile.ohos-arm64` no longer exclude real A15
  `runtime/arch/arm64/thread_arm64.cc`.
- Same for real A15 `runtime/arch/arm64/entrypoints_init_arm64.cc`.
- A15 `quick_entrypoints_arm64.S` is used again, but with A15 include
  precedence so it does not accidentally include A11 `arch/quick_alloc_entrypoints.S`.
- A11 `jni_entrypoints_arm64.S` remains isolated because A15 JNI assembly still
  has portable-toolchain CFI issues.
- `thread_cpu_stub.cc` is now built as its own support object. It is no longer
  accidentally produced from `metrics_stubs.cc`.
- `thread_cpu_stub.cc` supplies support stubs for WebP, Android cmsg send/recv,
  log error write, `GetShorty`, `create_disassembler`, and related static
  build gaps.

Verification already run:

- clean bionic build: `runtime 230 / 230`
- bionic runtime symbol gate: passed
- `nm -u` on deployed runtime shows only weak loader/HWASAN hooks
- phone sync hashes match
- real-McD phone proof captured
- OHOS sanity slice:
  `make -f Makefile.ohos-arm64 asm metrics-stubs -j4`
  compiles A15 quick entrypoints and `thread_cpu_stub.cc`

OHOS full link/run is not proven yet.

## Rebuild And Deploy Commands

Bionic runtime rebuild:

```bash
cd $HOME/art-latest
make -f Makefile.bionic-arm64 clean
make -f Makefile.bionic-arm64 link-runtime -j4
$HOME/android-to-openharmony-migration/scripts/check-westlake-runtime-symbols.sh \
  $HOME/art-latest/build-bionic-arm64/bin/dalvikvm
```

Deploy to phone:

```bash
ROOT=$HOME/android-to-openharmony-migration
cp $HOME/art-latest/build-bionic-arm64/bin/dalvikvm \
  "$ROOT/ohos-deploy/arm64-a15/dalvikvm"
cd "$ROOT"
./scripts/sync-westlake-phone-runtime.sh
```

Run real McD proof:

```bash
ADB=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe
SERIAL=cfb7c9e3
ROOT=$HOME/android-to-openharmony-migration
OUT="$ROOT/artifacts/real-mcd/$(date +%Y%m%d_%H%M%S)_next_real_mcd"
mkdir -p "$OUT"

"$ADB" -s "$SERIAL" install -r \
  "$ROOT/westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk" \
  > "$OUT/install.txt"
sha256sum \
  "$ROOT/westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk" \
  "$ROOT/aosp-shim.dex" \
  "$ROOT/ohos-deploy/arm64-a15/dalvikvm" \
  > "$OUT/hashes.txt"
"$ADB" -s "$SERIAL" shell sha256sum \
  /data/local/tmp/westlake/dalvikvm \
  /data/local/tmp/westlake/aosp-shim.dex \
  /data/local/tmp/westlake/core-oj.jar \
  /data/local/tmp/westlake/core-libart.jar \
  /data/local/tmp/westlake/core-icu4j.jar \
  > "$OUT/phone_hashes.txt"
"$ADB" -s "$SERIAL" logcat -c
"$ADB" -s "$SERIAL" shell am start -S -W \
  -n com.westlake.host/.WestlakeActivity \
  --es launch WESTLAKE_ART_MCD \
  > "$OUT/am_start.txt"
sleep 30
"$ADB" -s "$SERIAL" logcat -d > "$OUT/logcat_tail.log"
"$ADB" -s "$SERIAL" exec-out screencap -p > "$OUT/screen.png"
grep -E "PFCUT-VARHANDLE|PFCUT-UOE-THROW|Strict dashboard frame|ThreadGroup\\.uncaughtException|pending UOE|UnsupportedOperationException|NoClassDefFoundError|Config failed|DoCall-TRACE|SIGBUS|Fatal signal|ExceptionInInitializerError|UnsatisfiedLinkError|JNI DETECTED ERROR|FATAL EXCEPTION" \
  "$OUT/logcat_tail.log" > "$OUT/proof_grep.txt" || true
sha256sum "$OUT/logcat_tail.log" "$OUT/screen.png" > "$OUT/artifact_hashes.txt"
```

## Current Gap Estimate

Rough supervisor estimate as of 2026-04-30 16:05:

- stock McDonald's launches inside Westlake guest subprocess: proven
- source-built runtime route through app init and dashboard Activity survival:
  `69-73%`
- first real, non-fallback McDonald's UI: `35-45%`
- usable real McDonald's app flow: `20-30%`
- OHOS portability of the engine contracts: `49-53%`
- complete stock APK runtime: still `50%+` remaining

This is not because the architecture is wrong. The latest proof strengthens
the architecture. The remaining work is the real Android platform surface that
stock apps expect.

## Top Workstreams

### 0. PF-501/PF-480/PF-502: Current Source-Built Real-McD Startup Frontier

Current state:

- real McDonald's launches in a Westlake guest `dalvikvm` subprocess;
- the current source-built path is past AndroidX multidex extraction,
  Activity launch, AppCompat configuration context, config-market seeding, and
  Realm `Table.<clinit>`;
- interpreter-side `jdk.internal.misc.Unsafe` direct atomics have advanced the
  run past the previous CAS loops;
- portable CRC32/Adler32 JNI registration moves the run past the previous
  CRC32 crash;
- source now includes a portable `sun.nio.ch.FileKey` implementation and
  OpenJDK-style `FileDescriptor.fd` support, with phone proof captured;
- latest bionic runtime on phone:
  `dalvikvm=c08025a941569d5031ec80f6da78f8e7a574640b8a4c082188b535869f88c2aa`;
- latest shim on phone:
  `aosp-shim.dex=92fa05cbb5790a2a5ac82ac27fa733e6e55e25d34dfc5e8095d52729bef81408`;
- latest OHOS static runtime from same source:
  `build-ohos-arm64/bin/dalvikvm=aed60bf242af2b6587bbb5fe7b608efb2ad99ada0fbb4420c60b7e0d92380974`.

Immediate acceptance target:

- replace Realm no-op/zero-handle behavior with a portable persistence/query
  contract or a real native-loader strategy;
- eliminate the sparse-dashboard result by letting app-owned data-bound views
  receive real data;
- keep capturing hashes, focused grep, logcat, screenshot, and process state
  for each frontier move.

Do not claim real McDonald's UI progress from this stream until the source-built
runtime again reaches `SplashActivity`/`HomeDashboardActivity` and emits a
non-fallback app-owned view path.

### 1. PF-494: Native Library Loading And Realm

Current state:

- split APK metadata and native library staging have been proven earlier;
- Realm/ReLinker can find staged native payloads;
- static `dalvikvm` cannot truly load real APK native libraries depending on
  Android linker/system libraries;
- the current Realm behavior is a boundary probe, not real persistence.

Do not claim stock native compatibility until real `.so` loading, lifetime,
JNI registration, and dependencies are coherent under the portable contract.

Acceptable directions:

- real OHOS-portable native-loader abstraction;
- deliberately scoped Java Realm/storage compatibility layer;
- staged intermediate proof that is clearly labeled as boundary exploration.

Unacceptable:

- silent success from `Runtime.nativeLoad` for arbitrary native APK libraries;
- McDonald's-only native shortcuts without a generic contract.

### 2. PF-499: Remove Or Productize PFCUT Paths

The focused fatal/UOE gate is clean. The latest proof also productizes and
quarantines the former timezone PFCUT path. Broader logs still contain known
cutouts:

- ICU `ULocale`;
- currency;
- atomics/Unsafe forced paths;
- proxy repair;
- McD logging/perf no-ops.

Convert these to generic portable implementations or documented bridge
contracts. Then remove noisy diagnostics from accepted proof windows.

### 3. Production Config/Data Bootstrap

Do not treat dashboard Activity survival as production app init.

Need proof that McDonald's config/data/bootstrap path succeeds or is replaced
by a clearly generic app/network/storage contract. The latest focused proof did
not show the old `Config failed to download` marker, but no successful
production config/data path has been proven.

### 4. Real Dashboard UI

2026-05-01 current frontier:

- Accepted phone artifact:
  `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`.
- Real APK path, not mock path:
  `WESTLAKE_ART_MCD` launches the real McDonald's APK inside the Westlake
  subprocess on phone `cfb7c9e3`.
- The dashboard shell is the real `HomeDashboardFragment` view:
  `MCD_DASH_STOCK_VIEW_ATTACHED`.
- The four dashboard section placeholders are produced by the real McD
  dashboard `u6(List)` path:
  `MCD_DASH_U6_SEEDED`.
- All four visible dashboard section contents are now created by real McD child
  fragment `onCreateView` methods and attached into the real placeholders:
  `MCD_DASH_REAL_VIEW_ATTACHED section=HERO`,
  `MENU`, `PROMOTION`, and `POPULAR`.
- All four dashboard child section roots now go through the real McDonald's XML
  inflation path:
  `MCD_REAL_XML_INFLATED layout=layout_fragment_home_dashboard_hero_section
  resource=0x7f0e0282 root=RelativeLayout` and
  `MCD_REAL_XML_INFLATED layout=layout_home_menu_guest_user resource=0x7f0e0366
  root=LinearLayout` and
  `MCD_REAL_XML_INFLATED layout=layout_fragment_promotion_section
  resource=0x7f0e030e root=RelativeLayout` and
  `MCD_REAL_XML_INFLATED layout=layout_fragment_popular_section
  resource=0x7f0e0305 root=RelativeLayout`. This closes the section-root XML
  boundary that previously used controlled builders.
- The latest proof gate now rejects synthetic section fallback by requiring all
  four `MCD_DASH_REAL_VIEW_ATTACHED` markers and requiring zero
  `MCD_DASH_SECTION_VIEW_ATTACHED` fallback markers. It also requires all four
  section-root real-XML markers, plus the new PF-613 evidence that Promotion
  uses the real `HomePromotionAdapter` and inflates
  `layout_home_promotion_item`. The latest accepted frame also carries live
  image bytes through `STRICT_IMAGE_LIVE_ADAPTER`.
- Latest strict frame proof:
  `bytes=54799 views=69 texts=6 buttons=0 images=1 rows=1 rowImages=1
  rowImageBytes=54022 overlays=0`, `screen_bytes=79396`,
  `westlake_bridge=1`, `gate_status=PASS`.

Important remaining gap:

- Section roots are now real AXML and Promotion has one real stock-adapter row
  with visible live image bytes, but the row data is Westlake-seeded and the
  accepted image proof is still a strict-frame bridge rather than stock Glide
  completion. The next UI-density work is generic Glide/ImageView completion,
  real promotion data hydration, and Popular adapter data.
- McD's own activity `FragmentManager` transaction path is not the accepted
  path. It previously queued empty section containers and can reach unsafe
  `FragmentManager.h0(true)` behavior. The dashboard replace probe is now
  opt-in behind `westlake.mcd.dashboard.replace.probe`. This remains the
  generic AndroidX FragmentManager execution blocker.
- Child `performAttach` is also unsafe and SIGBUSes on this path. The accepted
  path seeds the host/activity fields, skips `performAttach`, then calls the
  real child fragment create-view path.
- Child `onViewCreated` is also unsafe on the Hero probe; the diagnostic run
  SIGBUSed in `LiveData.observe`, so the accepted gate leaves
  `westlake.mcd.child.onviewcreated=false`.
- This is stronger than the old scaffolded fallback, but it is not yet a stock
  FragmentManager-compatible execution path. The next swarm task is to replace
  this controlled lifecycle bypass with a safe generic AndroidX fragment
  lifecycle/transaction implementation.

### 5. OHOS Full Runtime Proof

Current OHOS status:

- A15 quick entrypoint assembly slice compiles;
- `thread_cpu_stub.cc` support object compiles;
- full OHOS static runtime link and symbol gate now pass;
- OHOS hosted runtime execution is not accepted yet.

Next OHOS gate should be:

- run under an OHOS Ability/XComponent host or nearest available OHOS runner;
- prove same guest-facing contracts used by Android phone proof.

## Do Not Waste Time On

- Mock McD/Yelp polish as proof of real McD progress. Those are useful canaries
  only.
- Phone ART execution of the McDonald's APK.
- Landing-page UI work.
- Claiming Material Components compatibility from a small shim slice.
- Treating fallback dashboard frames as real McDonald's UI.
- Reverting broad dirty worktree changes. There are many unrelated generated
  files and previous edits in both repos.
- Leaving command sessions open. Process pressure is already high.

## Current Docs To Read First

Repo-backed current docs:

- `docs/engine/REAL-APK-STATUS.md`
- `docs/engine/WESTLAKE-STATUS.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_CONTRACT.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`

Do not trust old sections without checking dates and artifacts. The current
accepted clean fallback/dashboard-survival frontier is older than the current
source-built diagnostic frontier. For current blocker work, start with
`artifacts/real-mcd/20260430_120310_crc32_portable_multidex_retry/`, then use
the pending FileKey/NIO/log-gated candidate runtime hash
`57e3881148faaf042c06225bb62f1b1af1ba1fec8ac115c5204072fc07d057fb`.

## Evidence Acceptance Standard

For any next claim, require:

- local runtime hash;
- phone runtime hash;
- host APK hash;
- `aosp-shim.dex` hash;
- boot classpath jar hashes if touched;
- symbol gate result for runtime;
- `am start` output;
- logcat;
- screenshot;
- focused grep;
- clear statement of what is proven and what is not.

If a proof still uses a fallback or cutout, label it as such.

## Optional Claude Usage

The user said Claude Code may be used via:

```bash
claude --dangerously-skip-permissions
```

Use it only as a sidecar reviewer or pair-programming helper, and close the
process. Previous process pressure was high, so do not spawn Claude while a
build, push, or phone proof session is still open.

## Supervisor Update - 2026-05-02 13:51 PT

Current artifact to inspect first:

- `artifacts/real-mcd/20260502_133533_mcd_48h_w1_clear_add_gate/`

What is proven:

- Real McD dashboard and real McD PDP render inside Westlake child
  `dalvikvm`, with no direct McDonald's ART process.
- Streamed logcat preserves dashboard markers in the artifact.
- The stock PDP Add view listener is installed and reached.
- The previous `OrderPDPViewModel.W1()` loading early-return blocker is closed.

What is still blocking full McD app success:

- Generated binding `OrderPdpButtonLayoutBindingImpl.a(2, View)` enters
  `OrderPDPFragment.j8(true)`, then fails around
  `OrderHelper.getMaxBasketQuantity()` /
  `AppCoreUtils.getMaxQtyOnBasket()` before stock Add LiveData is set.
- Generic input does not yet own the PDP bottom Add bar; it still needs a
  McD-specific projected route.
- Cart commit remains behind Realm/BaseStorage/BasketAPI safety gates.

Patch now staged and synced to phone:

- `aosp-shim.dex`
  `0bb7575f690f967e693f2ef75aff67d6badf622204827f56230ffa98b78918da`
- `WestlakeLauncher` markers added:
  - `MCD_JUSTFLIP_BASKET_FLAG_SEED`
  - `MCD_PDP_GENERATED_BINDING_CLICK`
  - richer `MCD_PDP_STOCK_VIEW_CLICK err/msg/top`
  - `MCD_PDP_VIEWMODEL_Z_GATE`

Next command:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_generated_binding_z_gate
```

Interpretation rule:

- If `MCD_PDP_GENERATED_BINDING_CLICK invoked=true` and Add LiveData becomes
  true, continue into observer/storage/cart.
- If generated binding still throws but `MCD_PDP_VIEWMODEL_Z_GATE invoked=true`
  sets Add LiveData, the next blocker is data/config or storage, not UI
  clickability.
- If neither marker appears, return to PDP binding/target resolution.

## Supervisor Update - 2026-05-02 14:25 PT

Start from this artifact:

- `artifacts/real-mcd/20260502_141628_mcd_48h_justflip_config_cart_gate/`

What changed since the prior handoff:

- `WestlakeActivityThread.seedMcdonaldsJustFlipState()` now returns portable
  config JSON for JustFlip:
  - `maxQtyOnBasket -> {"maxQtyOnBasket":99}`
  - `maxItemQuantity -> {"maxItemQuantity":99}`
- Rebuilt and pushed runtime to phone:
  `aosp-shim.dex=5d1ac39cd71f2c7f348b4cffa1a700ce0931e3056a8e9d1903b2cdb0251f69f2`.

What is newly proven:

- The old `AppCoreUtils.getMaxQtyOnBasket()` null/boolean-NPE frontier is
  closed. The artifact shows
  `MCD_JUSTFLIP_BASKET_FLAG_SEED ... maxQtyBefore=99 ... maxItemBefore=99`.
- The stock Add path reaches downstream McD basket storage:
  `PASS mcd_pdp_downstream_basket_commit_reached count=3`.

Current blocker:

- Full app gate still fails at Realm/BaseStorage:
  `FAIL mcd_full_app_cart_mutation_gate ... basketCommit=3 sigbus=1`.
- The SIGBUS occurs after `BasketAPIHandler.A1(...)`,
  `BaseStorage.<init>(...)`, and Realm pseudo-native calls. Treat this as the
  primary P0 workstream. Do not return to dashboard/PDP visual work unless a
  regression removes the current proof.
- Generic PDP Add input is still a separate open gap; the working route is the
  projected McD Add route.

Next worker command after a storage patch:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_storage_cart_gate
```

Next acceptance rule:

- Green for this slice requires dashboard -> PDP -> Add, JustFlip values at
  `99`, basket commit reached, no SIGBUS, and observable cart/bag mutation or
  app Add LiveData mutation.

## Supervisor Rally Kickoff - 2026-05-02 14:30 PT

This is the handoff state for any new agent joining the two-day rally.

Do not chase solved gaps:

- The visible McD dashboard/PDP proof is real enough to move forward.
- The old `W1()` loading gap is closed.
- The old JustFlip `maxQtyOnBasket` null gap is closed by
  `WestlakeActivityThread.seedMcdonaldsJustFlipState()`.

Current critical path:

1. Suppress duplicate Add re-entry after a storage-bound click. The current
   shim should emit `MCD_PDP_ADD_REENTRY_SUPPRESSED` and skip generated-binding
   or model fallback re-entry once the stock click has already crossed into
   downstream storage.
2. Patch Realm/BaseStorage in the runtime. Inspect
   `$HOME/art-latest/patches/runtime/interpreter/interpreter_common.cc`
   and latest artifact
   `artifacts/real-mcd/20260502_141628_mcd_48h_justflip_config_cart_gate/`.
3. Rerun the phone gate through Windows ADB:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_storage_cart_gate
```

4. Keep the full gate red until the same artifact has subprocess purity, no
   fatal signal, no SIGBUS, stock Add, downstream basket commit, and cart/bag
   or Add LiveData mutation.

Agent split:

- Storage/runtime agent owns PF-625 and native Realm pseudo-handles.
- Input agent owns PF-623 generic hit testing for PDP Add after storage is
  stable.
- Proof agent owns PF-626 checker and artifact discipline.
- Lifecycle agent owns PF-622 generated binding/observer continuation and
  removal of soft-state-only acceptance.

## Supervisor Update - 2026-05-02 14:49 PT

New baseline artifact:

- `artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/`

What changed:

- Re-entry suppression is now proven. After the first stock Add attempt enters
  `BasketAPIHandler.A1(...)` and throws `Telemetry not initialized`, Westlake
  emits `MCD_PDP_ADD_REENTRY_SUPPRESSED` and skips callOnClick/generated/model
  fallback re-entry.
- The prior duplicate-reentry SIGBUS is gone in this artifact:
  `PASS no_fatal_failed_requirement count=0`, `sigbus=0`.
- Phone hash checking was fixed for ADB CRLF output; rerun checker output is
  `check-real-mcd-proof-rerun.txt`.

Current blocker:

- The first app-owned basket continuation still aborts with
  `IllegalStateException: Telemetry not initialized`.
- Cart count remains zero and Add LiveData remains null.

Next worker focus:

1. Add a targeted core/domain telemetry no-throw boundary.
2. Rebuild/sync and run:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_telemetry_cart_gate
```

3. Green for the next slice requires no SIGBUS, no telemetry abort after
   `BasketAPIHandler.A1(...)`, and either Add LiveData/cart mutation or a
   precise nonfatal stock-app rejection.

## Two-Day Rally Handoff - 2026-05-02 14:55 PT

The rally is now organized around the latest red gates, not the older mock app
or dashboard-only milestones.

Baseline artifact:

```text
artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/
```

Must stay true:

- use Windows ADB:
  `/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3`;
- keep McD inside the Westlake guest child `dalvikvm`;
- reject direct phone ART, mock UI, projected-only click, or synthetic cart
  mutation as final success.

Current red gates:

- `PF-624`: `Telemetry not initialized` after `BasketAPIHandler.A1(...)`;
- `PF-625`: cart remains zero after stock Add reaches downstream basket code;
- `PF-623`: generic PDP input gate is zero;
- `PF-622`: normal Fragment lifecycle gate is still soft-state only;
- `PF-626`: checker must add telemetry/cart/input/lifecycle closure before
  full success.

Execution split:

- Supervisor owns `WestlakeLauncher.java` telemetry seeding and Add-path
  marker discipline.
- Storage/native agent owns Realm/BaseStorage pseudo-native handle fidelity.
- Input agent owns generic Android View dispatch.
- Lifecycle agent owns Fragment/LifecycleRegistry/LiveData readiness.
- Proof agent owns checker, artifacts, and false-positive rejection.

## Two-Day Rally Handoff - 2026-05-02 15:28 PT

Use this as the active handoff if starting a new worker now.

Normal proof that moved the frontier:

```text
artifacts/real-mcd/20260502_150027_mcd_48h_telemetry_cart_gate/
```

What it proves:

- real McD dashboard/PDP/Add run inside Westlake child `dalvikvm`;
- subprocess purity passes in the normal gate;
- telemetry seeding works: `MCD_TELEMETRY_MANAGER_SEED count=2`;
- `BasketAPIHandler.A1(...)` is reached with no later
  `Telemetry not initialized` abort;
- generic PDP input is accepted by the checker:
  `mcd_full_app_generic_pdp_input_gate count=5`;
- Add LiveData mutates to true;
- cart count still stays zero and lifecycle is still soft-resume only.

Unsafe crash map:

```text
artifacts/real-mcd/20260502_151742_mcd_48h_true_unsafe_cart_commit_probe/
```

What it proves:

- when observer/storage/model unsafe flags are enabled, the stock path reaches
  Realm/BaseCart and then the child VM SIGBUSes;
- active crash classification is
  `type=realm_sigbus realmNative=6735 sigbus=1`;
- observer dispatch is allowed in the run but still does not complete a safe
  stock cart mutation;
- the observed Realm frontier is `class_BaseCart`, `_maxAge` predicates,
  repeated `cartStatus = $0`, `TableQuery.nativeFind(...)`,
  `OsResults.nativeCreateResults(...)`, `OsResults.nativeSize(...)`, and
  `OsSharedRealm.nativeBeginTransaction(...)`.

Immediate worker instructions:

1. Storage/native worker: close PF-625 first. Patch portable Realm/BaseCart
   handle, query, result, row, transaction, and close behavior in
   `$HOME/art-latest/patches/runtime/interpreter/`. Do not touch
   `WestlakeLauncher.java` unless explicitly coordinating with supervisor.
2. Lifecycle worker: continue PF-622, but do not block the storage patch.
   Target normal Fragment/LiveData resumed state, not McD-only forced observer
   dispatch.
3. Input worker: keep PF-623 green by generalizing hit dispatch; do not replace
   the accepted generic input proof with a projected-only shortcut.
4. Proof worker: keep unsafe flag-file probes out of final success. Final
   PF-621 success must run with flags removed and prove subprocess purity.
5. Supervisor: after each storage patch, run unsafe probe once to prove the
   Realm crash moved, remove the flags, then run the normal acceptance gate.

Do not count as success:

- mock McD UI;
- direct phone ART launching `com.mcdonalds.app`;
- projected-only Add without generic input proof;
- synthetic cart count mutation;
- any artifact where the child `dalvikvm` is missing because it crashed.
## Two-Day Rally Handoff - 2026-05-02 15:47 PT

Use this section as the current handoff if you are joining the two-day McD
full-app rally.

Accepted normal proof:

```text
artifacts/real-mcd/20260502_150027_mcd_48h_telemetry_cart_gate/
```

Current PF-625 crash-isolation proof:

```text
artifacts/real-mcd/20260502_153758_mcd_48h_model_storage_no_observer_true_probe/
```

Do not use the visible McD dashboard/PDP as final success. It is real McD UI
running through Westlake, but the cart path is still failing. Do not use mock
UI, direct phone ART execution, or unsafe flag files as PF-621 closure.

Latest facts:

- Phone/local runtime hashes match in the corrected unsafe proof.
- Telemetry is seeded and nonfatal after basket entry.
- Add LiveData mutates true before storage.
- Observer dispatch was off, by design.
- The stock model/storage route crashes with SIGBUS after
  `OrderPDPViewModel.X(...)` enters the Realm/BaseCart boundary.
- BaseCart queries currently return `rows=0` for `_maxAge` and `cartStatus`
  predicates, and `nativeFind(...)` has no row to return.

Immediate worker order:

1. Storage/native worker: implement deterministic `class_BaseCart` row,
   predicate, result, row, and transaction semantics in
   `$HOME/art-latest/patches/runtime/interpreter/`.
2. Proof worker: keep unsafe probes and normal acceptance gates distinct.
   Final PF-621 must require unsafe flags off, subprocess purity, no fatal,
   telemetry green, generic input green, lifecycle green, and cart/rejection
   green.
3. Lifecycle worker: make Fragment and View lifecycle owners reach RESUMED
   generically. Soft resume recovery markers are diagnostic only.
4. Supervisor: after every storage patch, build, sync, and run the
   model/storage no-observer unsafe loop first. If SIGBUS clears, immediately
   run the normal no-unsafe gate.

Phone command base:

```bash
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3
```

Normal acceptance loop:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_realm_cart_fix_normal_gate
```

Unsafe crash-isolation loop must set `WESTLAKE_GATE_UNSAFE_PROBE=1`, enable
only model/storage flags, and clean the phone flag files afterward.

## Two-Day Rally Handoff - 2026-05-02 16:27 PT

Use this section as the current handoff.

Best current diagnostic artifact:

```text
artifacts/real-mcd/20260502_161942_mcd_48h_skip_t2_after_model_x_no_observer_probe/
```

Current verified facts:

- Real McD dashboard and PDP are running through Westlake, not mock UI.
- The latest unsafe probe is subprocess-pure and proves real
  `com.mcdonalds.app` guest execution in child `dalvikvm`.
- `OrderPDPViewModel.X(cartProduct)` reaches downstream basket code without
  fatal SIGBUS.
- The previous `CartProduct.getChoices()` crash came from the optional
  post-`model_x` `T2()` analytics call; supervisor gated that call off and the
  latest proof has `choicesNullNpe=0`.
- BaseCart active predicates now return rows.
- Add LiveData mutates, and generic PDP input is green.
- Cart/bag count still does not mutate:
  `cartSizeWithoutOffers=0 totalBagCount=0`.
- PDP product stock data is still wrong: `maxQtty=0`/`maxQttyZero=14`.
- Lifecycle is still red: `fragment_resumed=0`.
- Network is still red: McD GraphQL bridge returns status `599`.

Next worker order:

1. PF-625 cart persistence: continue runtime Realm row/list/write semantics
   until the app mutates cart/bag state or emits a stock rejection. Do not
   synthesize success counters in launcher code.
2. PF-625 product stock: hydrate the real PDP product so Add sees positive
   stock quantity, or produce the app's own exact stock rejection.
3. PF-622 lifecycle: make AndroidX Fragment/view lifecycle owners reach
   RESUMED normally.
4. PF-626 proof: count `MCD_PDP_REALMLIST_HYDRATE` as CartProduct list
   hydration when `choicesNullNpe=0`, and keep unsafe probes out of PF-621
   success.
5. PF-620 network: fix REST/GraphQL status `599` after cart persistence moves.

Current phone/runtime:

```text
ADB: /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3
runtime: 15143258367ff1363e52991dd176153e5f7d6dbf8bbbbc9f5958a52ddc96791f
shim: b47d63b69e339ed003f5a9a6099e86b8bdf9b6ed5a57ca2ea8bebdd92cebf0e9
```

## Two-Day Rally Handoff - 2026-05-02 16:39 PT

Use this as the current handoff if a new agent joins.

Best current proof:

```text
artifacts/real-mcd/20260502_162934_mcd_48h_product_stock_hydrate_no_observer_probe/
```

Current verified facts:

- Real McD dashboard/PDP are still running through Westlake child
  `dalvikvm`, not mock UI and not direct phone ART.
- PDP Add reaches the stock basket downstream path without fatal crash.
- Product stock is hydrated: latest cart gate has `quantity=1 maxQtty=99`.
- CartProduct RealmList hydration is green: `seen=10 present=10`,
  `choicesNullNpe=0`.
- Telemetry is no longer aborting the Add path.
- Generic PDP input is green.
- Cart/bag state is still not mutating:
  `cartSizeWithoutOffers=0 totalBagCount=0`.
- Lifecycle remains red:
  `lifecycleState=null`, `fragment_resumed=0`.
- Network remains red: McD GraphQL/REST status `599`.

New runtime candidate from storage worker:

```text
$HOME/art-latest/build-bionic-arm64/bin/dalvikvm
sha256: 83aceaf740cab758cd8871cf6e0d02414f5ccebde668d807f41e2126698d629b
```

Next supervisor sequence:

1. Copy the runtime candidate to `ohos-deploy/arm64-a15/dalvikvm`.
2. Rebuild `aosp-shim.dex` so lifecycle/input/product-stock changes are all
   in the same deployed shim.
3. Sync to phone with:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  ./scripts/sync-westlake-phone-runtime.sh
```

4. Run the diagnostic no-observer gate:

```bash
set -euo pipefail
ADB='/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3'
cleanup() {
  $ADB shell "rm -f /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_model_commit.txt \
                 /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_storage_commit.txt \
                 /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_observer_dispatch.txt \
                 /storage/emulated/0/Android/data/com.westlake.host/files/westlake_mcd_unsafe_model_commit.txt \
                 /storage/emulated/0/Android/data/com.westlake.host/files/westlake_mcd_unsafe_storage_commit.txt \
                 /storage/emulated/0/Android/data/com.westlake.host/files/westlake_mcd_unsafe_observer_dispatch.txt" >/dev/null 2>&1 || true
}
trap cleanup EXIT
cleanup
$ADB shell "mkdir -p /sdcard/Android/data/com.westlake.host/files; \
  printf 1 > /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_model_commit.txt; \
  printf 1 > /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_storage_commit.txt; \
  rm -f /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_observer_dispatch.txt"
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=360 WESTLAKE_GATE_INTERACT=1 WESTLAKE_GATE_UNSAFE_PROBE=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_realm_list_readback_runtime_probe
```

Expected next proof movement:

- `PFCUT-REALM-WRITE list-create ... column=cartProducts ... size=1`
- `PFCUT-REALM-STATE list-get-row ... target=class_CartProduct`
- then either `cartSizeWithoutOffers>0`, `totalBagCount>0`, or a stock exact
  rejection marker.

If the runtime proves link-list readback but the cart still reads zero, the
next owner should instrument stock Java basket propagation rather than adding
launcher-side counters.

## Superseding Handoff Update - 2026-05-02 18:10 PT

Use this update instead of the older product-stock/no-observer frontier above.

Best current proof:

```text
artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/
gate_status=PASS
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 51ba606bc829ab4cf57c759cc2b65f5f71e51dd8d0bbe304df4602ebe5572fbe
```

Facts now proven in one phone artifact:

- real `com.mcdonalds.app` guest `dalvikvm`, no direct phone-ART McD process;
- unsafe flags and unsafe markers off;
- live McD network through the Westlake HTTP bridge/proxy;
- real dashboard section XML plus promotion/popular item-row XML;
- dashboard -> PDP -> stock PDP/basket route;
- Realm cart-product list hydration and downstream basket commit;
- positive stock `CartInfo` readback and app-visible cart/bag count;
- lifecycle, generic PDP input, subprocess purity, and guest proof gates pass.

Do not overclaim this as full stock-runtime completion. The active gaps for
the next agents are:

1. `PF-630` long-soak stability:
   `20260502_174634_mcd_48h_network_pf621_full_app_final` reaches the same
   success path and later crashes near `OsSharedRealm.nativeCloseSharedRealm`.
   Fix close/finalizer handle ownership and prove a 10-minute post-cart soak.
2. `PF-628` no-bridge model propagation:
   remove reliance on `MCD_PDP_CARTINFO_SET_BRIDGE` by making the stock
   lifecycle/LiveData observer path update `CartViewModel` naturally.
3. `PF-629` deeper route coverage:
   bag/cart screen, edit/remove/re-add, quantity, customize, back/bottom-nav,
   and login/location-safe screens.
4. `PF-632` framework API misses:
   first concrete miss is `AlertDialog.Builder(Context,int)` from a stock PDP
   click path.
5. `PF-626/PF-608` OHOS parity:
   map every new network, Realm, lifecycle, input, render, dialog, and storage
   surface to the southbound API contract before claiming portability.

Primary docs updated for the rally:

- `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- `docs/program/WESTLAKE_SOUTHBOUND_API.md`
