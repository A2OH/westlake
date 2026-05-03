# Westlake Real McDonald's 48-Hour Full-App Rally

Prepared: 2026-05-02 PT

This is the active supervisor runbook for the next two-day push from the
current real-McD dashboard/PDP proof toward a fuller stock McDonald's app run
inside Westlake. It replaces "make a convincing mock" with a stricter target:
drive the real `com.mcdonalds.app` APK through real dashboard, PDP, add,
customize, bag/cart, and navigation flows while preserving Westlake subprocess
purity and OHOS portability boundaries.

## Rally Goal

Within 48 hours, make the stock McDonald's app usable enough in Westlake that
the phone proof can show:

- real dashboard content rendered from the stock app route;
- real dashboard item tap into a real PDP;
- real PDP controls for add, quantity, and customize;
- real bag/cart screen or accepted cart-state proof after add;
- no direct phone-ART McDonald's process;
- no mock McD/Yelp app used as the success proof;
- every Android phone success path mapped to a southbound OHOS/musl adapter
  contract or explicit open gap.

This is not accepted as "full app runs" if the proof only shows Westlake
projection screenshots without stock handlers, or only invokes stock handlers
without downstream cart/navigation state.

## Current Accepted Frontier

Phone: `cfb7c9e3`

ADB:

```bash
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3
```

Latest accepted phone proof:

- artifact:
  `artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/`
- result:
  `gate_status=PASS`
- runtime `dalvikvm`:
  `d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872`
- phone/current `aosp-shim.dex`:
  `51ba606bc829ab4cf57c759cc2b65f5f71e51dd8d0bbe304df4602ebe5572fbe`
- longer unbounded success-path stability probe:
  `artifacts/real-mcd/20260502_174634_mcd_48h_network_pf621_full_app_final/`
  reached live network, real dashboard/PDP/cart proof, then hit a late Realm
  close `SIGBUS`; this is tracked separately as the long-soak blocker.

Proven:

- Stock `com.mcdonalds.app` runs in the Westlake guest `dalvikvm` subprocess
  under `com.westlake.host`; direct phone-ART McD process count is zero.
- Unsafe model/storage/observer opt-ins are off in the accepted proof:
  `proof_unsafe_flags_off markers=0 flags=0`.
- Live McD network is green through the Westlake HTTP bridge/proxy path:
  `network_bridge_or_urlconnection ... westlake_bridge=9`, with no network
  error markers.
- Real dashboard content is rendered from the stock app route. The accepted
  proof inflates dashboard section XML and item-row XML:
  `layout_home_promotion_item` and `layout_home_popular_item_adapter`.
- A physical dashboard/popular interaction reaches the real stock path and
  launches a real PDP. The PDP proof includes strict PDP frame publication,
  real PDP XML, include binding, hydrated fields, stock generated binding
  listener, and resumed Fragment/lifecycle markers.
- The downstream basket path is no longer only an outer `j8(true)` trigger.
  The accepted proof reaches stock basket work, persists Realm cart-product
  lists, reads back positive `CartInfo`, updates the McD `CartViewModel`, and
  passes:
  `mcd_full_app_cart_mutation_gate`,
  `mcd_full_app_generic_pdp_input_gate`, and
  `pf621_final_acceptance_gate`.
- The bounded proof has no fatal signal, no SIGBUS, subprocess purity, and
  guest-DalvikVM proof in the same artifact.

Not yet proven:

- long-soak stability after the cart path. The unbounded full-app run reached
  the same success path and then crashed later in Realm close/finalization;
- natural stock observer/model propagation without the temporary
  `MCD_PDP_CARTINFO_SET_BRIDGE` from stock `BasketAPIHandler.p2()` to
  `CartViewModel`;
- complete stock bag/cart screen navigation, edit/remove, customize,
  checkout/auth/location/payment routes;
- generic ViewRoot-style rendering/input parity across more widgets, nested
  scroll, animations, dialogs, popups, and navigation transitions;
- a missing Android framework API exposed by the bounded proof:
  `AlertDialog.Builder(Context,int)` is needed before the stock generated PDP
  click path can rely only on app-owned dialog/listener behavior;
- full Material/AppCompat/Glide/ImageView behavior beyond the currently proven
  dashboard/PDP surfaces;
- OHOS execution of the same southbound contracts.

Latest gap update, 2026-05-02:

- The current accepted frontier is no longer the older deferred-add or
  no-unsafe local proof. It is the bounded PF-621 network/full-app phone gate:
  dashboard -> real dashboard item XML -> PDP -> stock basket path ->
  positive cart readback -> no unsafe flags -> live network 2xx ->
  `gate_status=PASS`.
- This is still not the final Westlake/McD contract. The two-day rally now
  starts from a green bounded proof and removes the remaining Westlake-specific
  bridges, expands the route coverage, and hardens long-running stability.
- The longer unbounded proof is the key risk signal:
  `20260502_174634_mcd_48h_network_pf621_full_app_final` proves the same path
  but later hits Realm close `SIGBUS`. That becomes `PF-630` and blocks any
  claim of full stock-runtime stability.
- `PF-628` remains open until stock lifecycle/LiveData observer propagation
  updates `CartViewModel` without `MCD_PDP_CARTINFO_SET_BRIDGE`.
- `PF-629` is the deeper route expansion: bag/cart screen, edit/remove,
  customize, back/bottom-nav, location/auth-safe screens, and repeated add.
- `PF-632` tracks missing stock-framework APIs discovered by the current proof,
  starting with `AlertDialog.Builder(Context,int)`.

## Two-Day Rally Launch State - 2026-05-02 11:35 PT

Supervisor objective:

Drive from the current stock PDP Add entry proof to a phone-visible full McD
dashboard/PDP/cart path. The rally is successful only when the proof shows real
stock app state after Add, not just a projected bag count.

Immediate execution order:

1. Close cart mutation:
   - set/derive `CartProduct.quantity` and max quantity from stock PDP state;
   - instrument `A7`, `OrderPDPViewModel.X`, basket use case, API handler,
     `getCartInfo`, and `CartViewModel.setCartInfo`;
   - prove `CartInfo.totalBagCount` or cart row data changes after `j8`.
2. Prove stock quantity/customize:
   - use hydrated `M0/N0/mDisplayQuantity` fields;
   - route plus/minus/customize through stock handlers first;
   - keep projected controls as labeled fallback only.
3. Replace soft lifecycle bridge:
   - eliminate the need for `mode=soft_state` by making FragmentManager,
     `performStart`, `performResume`, and data binding safe for the PDP;
   - no `performResume()` SIGBUS regression is allowed.
4. Make input/render generic enough for accepted phone proof:
   - physical host taps should work for dashboard, PDP controls, back, and bag;
   - direct touch-file injection is allowed for diagnosis but not final user
     acceptance.
5. Keep OHOS portability accounted:
   - each new Android phone API dependency must be added to
     `WESTLAKE_SOUTHBOUND_API.md` or an explicit PF gap.

## Two-Day Rally Execution State - 2026-05-02 12:20 PT

Latest phone proof:

- artifact:
  `artifacts/real-mcd/20260502_120714_mcd_48h_guarded_model_commit_baseline/`
- runtime hash:
  `48f60b57724549441e3fcd1b37603589e78214c051f22e605b48330062d5b5b4`
- result: full gate still `FAIL`, but PDP/Add/cart-gate substatus is `PASS`.

What changed:

- The unsafe direct `OrderPDPViewModel.X(CartProduct)` / `s7()` path is no
  longer entered by default because it crashed the guest VM in the previous
  probe. It can be re-enabled only for focused diagnosis with
  `-Dwestlake.mcd.unsafe_model_commit=true`,
  `WESTLAKE_MCD_UNSAFE_MODEL_COMMIT=1`, or the launch-file equivalent.
- The guarded proof has `PASS no_fatal_failed_requirement count=0`.
- Quantity is no longer a blocker for the Add entry path:
  `MCD_PDP_CART_PRODUCT_PREP ... afterQuantity=1` and
  `MCD_PDP_CART_GATE ... quantity=1`.
- The current explicit blocker marker is:
  `MCD_PDP_A7_GATE ... reason=realm_storage_sigbus_risk` followed by
  `MCD_PDP_STOCK_ADD_COMMIT ... route=model_x_gated_realm_storage`.
- Generic input still fails the full proof:
  `mcd_full_app_generic_pdp_input_gate count=0`; current Add success is still
  routed through the projected fallback after generic dispatch reports
  `handled=false`.

Updated execution order:

1. Implement the portable Realm/storage slice needed by McD cart commit:
   - handle `OsSharedRealm.nativeGetSharedRealm`,
     `nativeGetSchemaInfo`, `nativeRefresh`, `nativeIsClosed`,
     `nativeIsFrozen`, `nativeIsInTransaction`, table/key-path handles, and
     `BaseStorage` paths without invalid pseudo handles;
   - instrument `BasketAPIHandler.A1(...)`, `BasketAPIHandler.d(...)`,
     `getCartInfo()`, and `CartViewModel.setCartInfo(...)`;
   - acceptance: unsafe model commit can be enabled without SIGBUS and
     produces either a cart-count mutation or a precise nonfatal app-level
     rejection.
2. Replace PDP soft lifecycle with real AndroidX lifecycle:
   - current proof has `soft_resume_recovery=9 fragment_resumed=0`;
   - acceptance: `MCD_PDP_FRAGMENT_RESUMED` is present and no soft-state bridge
     is required.
3. Make physical/generic Add input own the route:
   - current proof shows `WESTLAKE_GENERIC_TOUCH_DISPATCH ... handled=false`
     before projected fallback handles Add;
   - acceptance: generic dispatch or real View click marker is sufficient for
     the full-app input gate without projected fallback.
4. Only after the above, expand to customize, bag screen, cart edits, back,
   bottom navigation, and repeated Add/remove flows.

## Two-Day Rally Execution State - 2026-05-02 12:46 PT

Latest focused phone proof:

- artifact:
  `artifacts/real-mcd/20260502_123759_mcd_48h_livedata_seed_stock_click_probe/`
- runtime hash:
  `cbe6802cedf83d2f0e9e254ada18ec32951700c84f1fac5bbaf5526ab268d481`
- result: full gate still `FAIL`, but the stock Add view/data-binding layer is
  now a named passing subproof.

What changed:

- The rally no longer treats direct `j8`, `s7`, `A7`, or
  `OrderPDPViewModel.X(CartProduct)` reflection as the main Add route. Those
  paths remain guarded because the unsafe model-commit probe reached
  `BasketAPIHandler.A1(...)` and crashed with `SIGBUS`.
- The current Add route prepares the real PDP data-binding tree and installs
  the generated listener from `OrderPdpButtonLayoutBindingImpl` onto the real
  `pdpAddToBagButton`.
- The `R1()` Boolean LiveData null gap is closed for this route. The previous
  proof failed in generated binding with `Boolean.booleanValue()`; the latest
  proof logs `MCD_PDP_STOCK_LIVEDATA_PREP ... after=true` and
  `mcd_pdp_stock_click_boolean_npe count=0`.
- The generated binding click is now invoked:
  `MCD_PDP_STOCK_VIEW_CLICK ... route=performClick invoked=true`, twice in the
  latest artifact.
- `scripts/check-real-mcd-proof.sh` now reports these milestones explicitly:
  `mcd_pdp_stock_binding_listener_installed`,
  `mcd_pdp_stock_livedata_seeded`,
  `mcd_pdp_stock_click_boolean_npe`, and `mcd_pdp_stock_add_entry`.
- The checker was also hardened so PDP-only artifacts without dashboard frame
  markers still reach the PDP sub-gates instead of exiting early under
  `set -euo pipefail`.

Current open blockers:

- The latest stock-view click proof still has no cart gate, basket commit, or
  cart mutation marker after `performClick`.
- PDP lifecycle remains a compatibility bridge:
  `MCD_PDP_FRAGMENT_RESUME_RECOVERY mode=soft_state resumedField=false`.
- `MiniActivityManager.performStart` no longer hits the old executing
  FragmentManager race in this proof, but `onStart` still reports duplicate
  parent attachment: `The specified child already has a parent`.
- The likely active blocker is LiveData/observer lifecycle dispatch. The click
  reaches generated binding, but the stock observer path from
  `OrderPDPViewModel.R1()/Z()/V()` into `OrderPDPFragment` and then
  `s7/o7/A7` is not firing under the soft lifecycle state.
- Generic/physical PDP Add input is still not accepted. The current proof is a
  projected hit that invokes the stock view; final proof needs host pointer
  event -> Westlake hit test -> real button listener without projected fallback.

Updated two-day execution order:

1. **WS1 lifecycle/observer dispatch**
   - Fix duplicate-parent handling in PDP `performStart`.
   - Make real app-bundled AndroidX Fragment resume markers appear without
     `mode=soft_state`.
   - Instrument and prove the generated binding click causes the stock
     LiveData observer to fire.
   - Acceptance: `MCD_PDP_FRAGMENT_RESUMED` is present, soft-state recovery is
     gone, and stock observer/callback markers appear after
     `MCD_PDP_STOCK_VIEW_CLICK`.
2. **WS3 order/cart continuation**
   - Reconcile the two useful proof branches: guarded cart-gate baseline
     (`j8` with non-null cart/quantity) and latest stock-view click
     (`performClick` through generated binding).
   - Instrument `OrderPdpButtonLayoutBindingImpl.a`, `OrderPDPViewModel.R1`,
     `OrderPDPViewModel.Z/V`, `OrderPDPFragment.s7/o7/A7`, and
     `CartViewModel.setCartInfo`.
   - Acceptance: stock-view click produces either `MCD_PDP_CART_GATE` with
     changed `CartInfo` or a precise nonfatal app-level rejection.
3. **WS4 storage/Realm safety**
   - Keep unsafe model commit disabled by default.
   - Implement only the Realm/BaseStorage surface needed by the basket path
     until the unsafe probe runs without `SIGBUS`.
   - Acceptance: opt-in unsafe probe reaches basket storage/network and exits
     nonfatally.
4. **WS2 input/render**
   - Convert projected PDP Add to generic hit dispatch on
     `pdpAddToBagButton`.
   - Acceptance: `mcd_full_app_generic_pdp_input_gate` passes without the
     projected fallback owning the action.
5. **WS6 proof/OHOS**
   - Every new Android phone API used by these fixes must be added to
     `WESTLAKE_SOUTHBOUND_API.md`.
   - Full success requires subprocess purity, no fatal signal, real dashboard
     -> PDP -> Add -> cart/bag proof, and documented OHOS/musl adapter work for
     every southbound dependency.

## Two-Day Rally Supervisor Update - 2026-05-02 13:07 PT

Worker review tightened the final acceptance standard. The previous
`performClick invoked=true` marker proves only the Android view boundary; it is
not enough to claim the stock McDonald's Add flow. The next accepted proof must
show the whole app-owned continuation:

1. `MCD_PDP_STOCK_VIEW_CLICK ... invoked=true`;
2. generated binding method entry for
   `OrderPdpButtonLayoutBindingImpl.a(sourceId=2, view)`;
3. generated binding computes `R1().getValue() == true` and calls
   `OrderPDPFragment.j8(true)`;
4. `j8(true)` reaches `OrderPDPViewModel.Z()` and then `V()` or `L2()`;
5. app observer callback fires (`q7`, `Y7`, or product-limit callback);
6. stock fragment continuation reaches `r7/s7/v8/o7/A7`;
7. basket/cart path either mutates app cart state or emits a precise nonfatal
   stock rejection;
8. no projected fallback owns the PDP Add action in the final full proof.

Worker execution details:

- **WS1 lifecycle/observer**: patch queue dispatches Android-shaped
  `ON_START`/`ON_RESUME` through `getLifecycle().handleLifecycleEvent(...)` or
  the app-bundled alias and adds PDP observer-bridge markers. Acceptance is
  `MCD_ORDER_PDP_READY ... resumed=true` with no soft-state-only proof, plus
  observer callback markers after Add.
- **WS2 input/render**: patch queue repairs orphan `ACTION_UP` streams by
  sending a synthetic generic `ACTION_DOWN` through the decor before the real
  `UP`. Acceptance is real generic dispatch to `pdpAddToBagButton`, stock
  `View` touch lifecycle markers, and no `MCD_ORDER_PDP_PROJECTED_FALLBACK`.
- **WS3 order/cart**: next instrumentation must prove
  `OrderPdpButtonLayoutBindingImpl.a`, `OrderPDPViewModel.Z/V`,
  `OrderPDPFragment.q7/Y7/r7/s7/v8/o7/A7`, and
  `CartViewModel.setCartInfo`. Acceptance is a cart-size/bag-count delta or
  exact stock-app rejection.
- **WS4 storage/Realm**: storage commit stays guarded by default. Unsafe model
  commit may reach `A7`, but `OrderPDPViewModel.X(CartProduct)` and the
  Realm/BaseStorage/BasketAPI boundary require an explicit storage opt-in until
  the SIGBUS path is closed.
- **WS6 proof**: the next phone proof must be a long real-McD run, because the
  13:02 short rerun timed out before dashboard/PDP and did not exercise the new
  markers. Use later touches and record both local and phone runtime hashes.

Concrete next proof command:

```bash
./scripts/build-shim-dex.sh
./scripts/sync-westlake-phone-runtime.sh
WESTLAKE_GATE_SLEEP=180 scripts/run-real-mcd-phone-gate.sh mcd_48h_observer_state_probe_long
```

Touch schedule for phone `cfb7c9e3` through Windows ADB:

- after dashboard readiness or about 80-90 seconds: tap guest popular item;
- after PDP readiness or about 35 seconds later: tap PDP Add;
- after 8-10 seconds: tap PDP Add again to catch delayed observer activation.

Do not mark the rally green until one artifact contains subprocess purity,
dashboard -> PDP, stock generated binding entry, observer continuation,
nonfatal storage/cart behavior, and cart/bag UI evidence.

## Two-Day Rally Supervisor Update - 2026-05-02 13:18 PT

The `20260502_131212_mcd_48h_observer_state_probe_long` artifact is not a
valid blocker closure even though it passed subprocess purity and network
bridging. The checker collected too early:

- artifact result: dashboard gate failed with `dashboard_active count=0`;
- later live log: real dashboard launched, attached stock sections, and emitted
  `MCD_DASH_SECTIONS_READY hero=true menu=true promotion=true popular=true`;
- the scheduled PDP Add taps were consumed as stale dashboard touches because
  input polling became active after the fixed schedule had already advanced.

New rally execution rule: PF-626 owns proof orchestration as a product
blocker, not a test nicety. The proof harness must drive touch input from
Westlake readiness markers instead of wall-clock guesses.

Updated execution details:

- **PF-622 lifecycle/observer**: accepted only when the same artifact has PDP
  readiness, `MCD_PDP_OBSERVER_BRIDGE*`, and app observer continuation after
  Add. A dashboard-only late-start run is neutral evidence, not a pass.
- **PF-623 input/render**: dashboard tap may be sent only after
  `Host input polling enabled for dashboard frame` or
  `MCD_DASH_SECTIONS_READY`. PDP Add taps may be sent only after
  `MCD_ORDER_PDP_READY`, `MCD_ORDER_PDP_RENDER_ROOT`, or equivalent PDP frame
  publication. The final proof must not be owned by projected fallback.
- **PF-624 order/cart**: if `MCD_PDP_STOCK_VIEW_CLICK invoked=true` appears but
  no generated-binding method entry follows, add method-entry instrumentation
  before making another cart claim.
- **PF-625 storage/Realm**: keep unsafe storage commit off for the normal gate.
  A valid intermediate pass is a nonfatal, precise stock rejection after the
  app-owned Add continuation. A SIGBUS or silent storage shortcut is red.
- **PF-626 proof automation**: the next run must use `WESTLAKE_GATE_SLEEP=420`
  and marker-driven touch injection on phone `cfb7c9e3` through Windows ADB:

```bash
ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
ADB_HOST=localhost \
ADB_PORT=5037 \
ADB_SERIAL=cfb7c9e3 \
WESTLAKE_GATE_SLEEP=420 \
scripts/run-real-mcd-phone-gate.sh mcd_48h_marker_driven_add_gate
```

Marker-driven touch sequence:

1. wait for dashboard readiness marker;
2. write a touch-file tap near guest `(56,523)` to open PDP;
3. wait for PDP readiness marker;
4. write Add taps near guest `(358,988)` and `(350,960)`;
5. collect `MCD_PDP_LIVEDATA_STATE`, observer bridge/dispatch, stock binding,
   fragment continuation, storage/cart, and generic input markers.

Do not spend worker time polishing fallback visuals until this command either
closes the Add/cart path or produces the exact missing marker in the app-owned
chain.

## Two-Day Rally Supervisor Update - 2026-05-02 13:30 PT

Marker-driven run
`20260502_132152_mcd_48h_marker_driven_add_gate` improved the evidence and
found the next exact blockers:

- real subprocess purity still passed: host `com.westlake.host` owned child
  `dalvikvm`, with no direct McDonald's process;
- real dashboard readiness was observed by the marker driver, and the dashboard
  tap routed through `HomePopularItemsAdapter` into
  `OrderProductDetailsActivity`;
- real PDP XML rendered on phone with product image, title, quantity controls,
  customize row, and bottom Add bar;
- Add taps reached the stock `pdpAddToBagButton` view and
  `performClick invoked=true`;
- no SIGBUS occurred because storage commit stayed guarded;
- the final gate still failed.

New exact gaps:

- **PF-626 proof artifact gap**: the late `logcat -d` collection lost early
  dashboard markers because the buffer filled with startup diagnostics. The
  proof script now streams logcat from launch into `logcat-stream.txt` and uses
  it as `logcat.txt` when present.
- **PF-622/PF-624 app-owned Add gap**: `j8()` is blocked at its first line
  because `OrderPDPViewModel.W1()` remains true. The next runtime patch clears
  this stuck loading LiveData in stock-click prep and logs
  `loadingBefore`, `loadingAfter`, and `loadingCleared` on
  `MCD_PDP_STOCK_LIVEDATA_PREP`.
- **PF-623 generic input gap**: the bottom Add tap still ends in
  `MCD_ORDER_PDP_PROJECTED_FALLBACK reason=generic_hit_miss`; projected fallback
  remains diagnostic only. Generic hit-testing needs the sticky bottom bar and
  scrolled PDP root mapping fixed before final acceptance.
- **PF-625 storage/cart gap**: cart product and cart info are hydrated, but cart
  counts remain zero and `MCD_PDP_A7_GATE` is guarded by
  `reason=realm_storage_sigbus_risk`.

Next accepted subproof after the `W1` patch must show
`loadingCleared=true`, `normalAdd=true` or `editAdd=true`, and
`MCD_PDP_OBSERVER_DISPATCH_GATE route=q7|Y7 requested=true`. If that happens,
the remaining red work is generic input ownership and portable Realm/storage.

Rally command discipline:

```bash
./scripts/build-shim-dex.sh
./scripts/sync-westlake-phone-runtime.sh
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 shell ...
scripts/check-real-mcd-proof.sh artifacts/real-mcd/<new-proof-dir>
```

## Architecture Rule

Northbound APIs remain Android-shaped because the guest APK expects Android.
Southbound implementation must be Westlake-owned and portable:

- phone proof adapter may use Android host services only behind Westlake
  abstractions;
- OHOS/musl adapter must be able to implement the same contract;
- direct bionic-native assumptions do not count as portable unless explicitly
  scoped as a compatibility capsule.

## Workstream WS1: Fragment/DataBinding/Lifecycle Readiness

Owner: lifecycle worker

Objective:

Make the real PDP fragment reach stock-ready state before the phone-visible Add
button is interactive, without relying on deferred handler retries as the
accepted path.

Known facts:

- `G0` is assigned in `OrderPDPFragment.onCreateView` after
  `DataBindingUtil.h(fragment_order_pdp)`.
- `E0` is assigned immediately after through `ViewModelProvider`.
- `t0` is assigned later from the `OrderPDPViewModel.C0()` observer path
  (`K7(CartProduct)`), or currently synthesized by Westlake from the real
  dashboard `Product`.
- PDP included binding is now far enough to hydrate the stock plus/minus and
  quantity fields from the inflated binding root.
- Calling the real hard `performResume()` path before the fragment fields are
  fully stock-valid caused a SIGBUS in
  `OrderPDPFragment.initializeListeners()` when `M0/N0` were null. The current
  runtime avoids the crash by hydrating fields and using a labeled soft-state
  resume bridge.
- `mResumed` still reads false in the current proof. That is acceptable only as
  a boundary-exposure bridge, not as final lifecycle correctness.

Execution details:

1. Instrument PDP fragment lifecycle:
   - after `performCreateView`;
   - after `onViewCreated`;
   - after `OrderPDPViewModel.T1`;
   - after `C0`/`K7` observer or synthetic `t0`.
2. Fix `<include>` inflation/data-binding tags for PDP included layouts:
   - `order_pdp_button_layout`;
   - `order_pdp_edit_bottom_layout`;
   - `skeleton_order_pdp_default`;
   - `skeleton_order_pdp_choice_customization`;
   - `skeleton_order_pdp_small_grid`.
3. Ensure `simple_product_holder` and fragment transactions attach through the
   generic FragmentManager path before projection/root shortcuts claim success.
4. Acceptance marker:
   - first visible PDP frame has `E0!=null`, `G0!=null`, and either stock
     `t0!=null` or explicitly labeled synthetic `t0`;
   - stock `performStart`/`performResume` does not crash and leaves the
     fragment resumed/started by normal AndroidX state;
   - no reliance on `mode=soft_state` for accepted full-app proof.
5. Reject:
   - accepted proof where first Add requires deferred retry because `E0/G0`
     are null;
   - fatal `OrderPDPViewModel` crash;
   - hidden direct attach that bypasses fragment field initialization;
   - accepted full-app proof where `mResumed=false` is treated as good enough.

## Workstream WS2: Generic View Rendering and Input

Owner: UI/input worker

Objective:

Replace McD-specific projection/control bridges with generic enough Android
View root selection, traversal, drawing, hit testing, scroll, and click dispatch
for dashboard/PDP/cart flows.

Execution details:

1. Make selected render root generic:
   - dashboard root should be chosen by attached visible content and view stats;
   - PDP root should include bottom Add bar and scroll content;
   - cart/customize roots should be discoverable without class-name hacks where
     possible.
2. Implement or harden generic hit dispatch:
   - coordinate to visible child path;
   - `View.performClick()` and listener dispatch;
   - `RecyclerView` item hit and adapter listener;
   - `ScrollView`/nested scroll movement;
   - back navigation and bottom navigation taps.
3. Promote physical host input to accepted path:
   - dashboard physical tap already reaches the stock popular-item listener;
   - PDP Add is currently strongest through direct Westlake touch-file
     injection, so convert it to physical host tap parity;
   - preserve direct touch-file injection as a low-level diagnostic channel.
4. Expand renderer coverage:
   - real text, background, image, rounded rects;
   - basic transforms without aborting DLST replay;
   - Material/AppCompat text/button/card enough for McD screens.
5. Acceptance marker:
   - dashboard item and PDP Add/quantity/customize taps resolve through generic
     hit path first, with McD-specific route used only as a regression fallback.
6. Reject:
   - success based only on y-band projected controls;
   - sparse/black/white frames;
   - `MCD_DASH_FALLBACK` or mock app UI.

## Workstream WS3: Order, Customize, Bag, and Cart

Owner: order-flow worker

Objective:

Prove stock order flow beyond synchronous `j8(true)` return.

Execution details:

1. Add markers around the full stock chain:
   - `MCD_PDP_J8_ENTER`: `E0`, `G0`, `t0`, `W1`, quantity text, cart info,
     product code, max quantity;
   - `MCD_PDP_A7_LIMIT`: null cart info, cart size without offers, max basket,
     product-specific quantity, return value;
   - `MCD_PDP_CAN_ADD_PRODUCT`: return from
     `OrderingManager.canAddProduct(t0)`;
   - `MCD_PDP_VM_X`: entry into `OrderPDPViewModel.X(CartProduct)`;
   - `MCD_BASKET_USECASE_E`: repository add branch vs edit branch;
   - `MCD_BASKET_API_ADD`: before/after backing cart count and exceptions;
   - `MCD_BASKET_GET_INFO`: source cart quantity and resulting
     `CartInfo.totalBagCount`;
   - `MCD_CART_VM_SET_INFO`: previous/new `CartInfo.totalBagCount`;
   - `MCD_PDP_ADD_OBSERVER`: `Pair<Boolean, CartInfo>` result;
   - `MCD_CART_SCREEN_LAUNCH`: cart screen receives at least one real row.
2. Fill or replace the synthetic `CartProduct` only with fields required by
   stock validation:
   - product;
   - productCode;
   - quantity. This is now hydrated to `1` in the guarded baseline and must
     stay covered by proof;
   - max quantity. The current cart gate still shows `maxQtty=0`, but the
     unsafe probe shows the more urgent blocker is downstream Realm/storage;
   - product options/customizations;
   - price/reference fields if serializer requires them.
3. If `CartInfo` is null before `A7`, seed or fetch a real empty cart through
   the app's basket path. The latest proof has non-null `CartInfo`, so the
   immediate test is now whether app mutation changes `cartSizeWithoutOffers`,
   `totalBagCount`, or downstream cart row state. Do not satisfy this by only
   updating Westlake projection state.
4. Expand `Product` -> `CartProduct` proof to include product code, product
   type, quantity, max quantity, choices/components/customizations sizes,
   daypart/menu types, and UUID.
5. Prove quantity plus/minus through stock handler.
6. Prove customize navigation through stock handler:
   `OrderPDPFragment.o8()` and `OrderPDPViewModel.f0()` must route from real
   `CartProduct` choices/customizations.
7. Prove bag/cart screen opens and shows the added item or an accepted cart
   state marker if auth/network blocks server checkout.
8. Acceptance marker:
   - item added via real stock path changes real cart/bag state and survives
     navigation to bag/cart.
9. Reject:
   - only Westlake projected bag count;
   - only `j8(true)` method entry without downstream cart proof.

Immediate local patch queue for WS3:

1. Keep the quantity hydration proof green:
   `MCD_PDP_CART_PRODUCT_PREP ... afterQuantity=1` and
   `MCD_PDP_CART_GATE ... quantity=1`.
2. The default path must keep the unsafe model commit gated:
   `MCD_PDP_STOCK_ADD_COMMIT route=model_x_gated_realm_storage`.
3. For focused probes only, enable `westlake.mcd.unsafe_model_commit` and add
   nonfatal markers around `A7`, `OrderPDPViewModel.X`, basket use case,
   `BasketAPIHandler.A1/d`, `getCartInfo`, and `CartViewModel.setCartInfo`.
4. Implement enough Realm/storage behavior that the unsafe path no longer
   reaches `SIGBUS` around `BasketAPIHandler.A1(...)`.
5. Re-run the phone proof and require either:
   - real cart count changes in `CartInfo`; or
   - a narrower next blocker with exact method, object, field, and exception.

## Workstream WS4: Network, Auth, Storage, and State

Owner: state/network worker

Objective:

Make app data sources real enough for dashboard, menu, product, cart, restaurant
selection, and cached images.

Execution details:

1. Keep current portable network bridge green:
   - method;
   - headers;
   - body;
   - redirects;
   - TLS/cookies;
   - image byte transport.
2. Add URL/status/body markers for cart/menu/product APIs, with redaction for
   auth tokens.
3. Implement targeted storage fidelity:
   - `SharedPreferences`;
   - cache files;
   - Realm table/query/result/row state for observed McD tables;
   - cart/session persistence.
4. Use live endpoints where possible; when auth blocks, use URL-keyed fixtures
   through the same parser/model path, not direct UI frames.
5. Acceptance marker:
   - dashboard/PDP/cart proofs show network/storage path used by app models,
     not one-off direct UI seeding.

## Workstream WS5: Runtime Stability and Portability

Owner: runtime/OHOS worker

Objective:

Keep Android phone proof and OHOS portability moving together.

Execution details:

1. Maintain subprocess purity:
   - host `com.westlake.host`;
   - guest `dalvikvm`;
   - direct McD ART processes zero.
2. Keep dual-build discipline:
   - Android phone runtime sync;
   - OHOS static symbol gate;
   - no untracked Android-only host service calls outside southbound adapter.
3. Map every new dependency to:
   - frame/surface;
   - input;
   - network/TLS;
   - file/storage/Realm;
   - time/thread/looper;
   - native library policy.
4. Acceptance marker:
   - every accepted Android phone proof updates `WESTLAKE_SOUTHBOUND_API.md` or
     an explicit PF issue for the OHOS adapter delta.

## Workstream WS6: Proof Automation and Evidence

Owner: QA/proof worker

Objective:

Make success/failure reproducible and reject false positives.

Required command baseline:

```bash
./scripts/build-shim-dex.sh

env WSL_INTEROP=/run/WSL/1348400_interop \
  ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  ./scripts/sync-westlake-phone-runtime.sh

env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=75 \
  ./scripts/run-real-mcd-phone-gate.sh <tag>
```

Every accepted claim needs:

- local and phone hashes for `dalvikvm`, `core-oj.jar`, `core-libart.jar`,
  `core-icu4j.jar`, `bouncycastle.jar`, and `aosp-shim.dex`;
- host launch output;
- process purity check;
- logcat;
- screenshot before/after interaction;
- focused grep markers;
- explicit "proven / not proven" statement.

## 48-Hour Schedule

### Hours 0-6: Lock The PDP Foundation

- Update docs and launch swarm ownership.
- Completed at rally launch:
  - latest runtime pushed to phone;
  - direct dashboard -> PDP -> Add touch-file proof captured;
  - PDP bound fields hydrate from XML;
  - stock `j8(true)` enters and returns without SIGBUS;
  - cart gate proves non-null `CartInfo` before and after `j8`.
- Active acceptance target:
  - `CartProduct.quantity/maxQtty` are no longer zero at `j8`;
  - downstream cart mutation blocker is narrowed to exact app method/field, or
    real cart count changes.

### Hours 6-18: Stock PDP Controls

- Prove quantity plus/minus through stock handler using hydrated
  `M0/N0/mDisplayQuantity`.
- Prove customize opens real customization surface through stock handler.
- Prove Add changes real cart/bag state beyond projected counter.
- Acceptance target:
  - dashboard -> PDP -> quantity -> customize -> back -> add -> bag marker.

### Hours 18-30: Cart/Bag And Generic Input

- Route bag/cart tap through generic hit dispatch.
- Render cart/bag screen with real app root, text, and item data.
- Replace direct touch-file and McD y-band projection for accepted controls
  where possible.
- Acceptance target:
  - real cart/bag screen visible with added item or accepted app cart-state
    marker if server auth blocks visual cart.

### Hours 30-42: Generic Rendering And State Hardening

- Expand generic View drawing and image path for dashboard/PDP/cart.
- Move remaining McD-specific bridges behind guarded fallback markers.
- Harden storage/network paths found by cart/customize flows.
- Acceptance target:
  - fewer projection-specific markers; no regression in dashboard/PDP/Add.

### Hours 42-48: Acceptance, Cleanup, OHOS Delta

- Run clean full proof from cold start.
- Update handoff, PF issues, southbound API doc.
- Remove or gate noisy diagnostics that hide real failures.
- Produce one final artifact bundle and exact gap list. Full app success is not
  claimable unless the bundle includes stock dashboard, PDP, quantity/customize
  or cart/bag state, subprocess purity, no crash, and OHOS southbound delta.

## Supervisor Decision Rules

- Prefer generic Westlake Android compatibility fixes over McD-only shortcuts.
- Temporary McD-specific bridges are allowed only when they expose the next
  stock-app boundary and are labeled as such.
- Do not claim full app success until cart/customize/bag or equivalent stock
  downstream state is proven.
- Do not wait for user decisions unless the choice affects contract scope,
  credentials/auth, or whether to accept a fixture instead of live backend data.

## Two-Day Rally Supervisor Update - 2026-05-02 13:51 PT

Latest accepted proof before this update:

- Artifact:
  `artifacts/real-mcd/20260502_133533_mcd_48h_w1_clear_add_gate/`.
- Proven:
  - Westlake subprocess purity: host `com.westlake.host` owns child
    `dalvikvm`; no direct McDonald's ART process.
  - Streamed logcat preserved real dashboard markers.
  - Stock dashboard item routed to real
    `OrderProductDetailsActivity` / `OrderPDPFragment`.
  - Real PDP XML rendered with image, controls, and bottom Add bar.
  - The stock `pdpAddToBagButton` listener is installed and reached.
  - `OrderPDPViewModel.W1()` loading was cleared from `true` to `false`, so
    the old early-return blocker in `OrderPDPFragment.j8()` is closed.
- Not proven:
  - The generated-binding Add path still throws before it sets
    `normalAdd`, `editAdd`, or `productLimit` LiveData.
  - Generic PDP Add hit dispatch still falls back to the McD y-band route.
  - Cart/bag mutation is blocked behind the Realm/BaseStorage/BasketAPI
    safety gate.

New exact blocker:

- The generated binding reaches
  `OrderPdpButtonLayoutBindingImpl.a(2, View)`, which calls
  `OrderPDPFragment.j8(true)`.
- `j8(true)` now gets past `W1()`, enters the Add path, then hits
  `OrderHelper.getMaxBasketQuantity()` /
  `AppCoreUtils.getMaxQtyOnBasket()`. The observed failure is a
  `java.lang.IllegalStateException` after a JustFlip max-basket
  `Integer.intValue()` null path is logged.
- Execution detail: this is now a runtime/data southbound problem, not a
  button-visibility problem. The Add listener is present; the app-owned
  continuation state is not yet produced.

Runtime patch staged for the next phone proof:

- Build hash pushed to the phone:
  `aosp-shim.dex=0bb7575f690f967e693f2ef75aff67d6badf622204827f56230ffa98b78918da`.
- `WestlakeLauncher` now emits:
  - `MCD_JUSTFLIP_BASKET_FLAG_SEED` for `maxQtyOnBasket` and
    `maxItemQuantity` diagnostics;
  - `MCD_PDP_GENERATED_BINDING_CLICK` for direct generated-binding entry;
  - richer `MCD_PDP_STOCK_VIEW_CLICK` error summaries with message and top
    stack frame;
  - `MCD_PDP_VIEWMODEL_Z_GATE` as a diagnostic fallback that invokes the
    stock `OrderPDPViewModel.Z()` decision method if the generated binding
    still fails before producing Add LiveData.

Acceptance for the next run:

- Green only if the same artifact shows dashboard -> PDP -> Add in Westlake,
  generated binding entry, and either:
  - no generated-binding exception and `normalAdd`/`editAdd`/`productLimit`
    becomes true through the app path; or
  - the fallback `MCD_PDP_VIEWMODEL_Z_GATE` proves the next blocker is strictly
    Realm/storage observer commit, with no UI/input ambiguity left.
- Still red if the only passing path is a visual/projected counter or if the
  proof requires direct phone ART McDonald's execution.

## Two-Day Rally Supervisor Update - 2026-05-02 14:25 PT

Latest proof after the JustFlip config patch:

- Artifact:
  `artifacts/real-mcd/20260502_141628_mcd_48h_justflip_config_cart_gate/`.
- Runtime pushed to phone:
  `aosp-shim.dex=5d1ac39cd71f2c7f348b4cffa1a700ce0931e3056a8e9d1903b2cdb0251f69f2`.
- New runtime change:
  `WestlakeActivityThread.seedMcdonaldsJustFlipState()` now returns portable
  JSON config values for the app's JustFlip `maxQtyOnBasket` and
  `maxItemQuantity` config flags instead of returning `null`.

What moved forward:

- The previous `AppCoreUtils.getMaxQtyOnBasket()` /
  `Integer.intValue()` failure is closed. Proof marker:
  `MCD_JUSTFLIP_BASKET_FLAG_SEED ... maxQtyBefore=99 ... maxItemBefore=99`.
- The stock dashboard still opens real
  `OrderProductDetailsActivity` through the real dashboard adapter path.
- Real PDP XML still renders and the stock Add view binding remains installed.
- The proof now reaches `BasketAPIHandler.A1(...)`,
  `BaseStorage.<init>(...)`, and Realm pseudo-native calls from the stock Add
  path. Checker result:
  `PASS mcd_pdp_downstream_basket_commit_reached count=3`.

Current red gate:

- `gate_status=FAIL` because the VM receives SIGBUS in the downstream
  Realm/BaseStorage basket commit path before cart/bag mutation is observable.
- The relevant checker line is
  `FAIL mcd_full_app_cart_mutation_gate ... basketCommit=3 sigbus=1
  next_gap=downstream_basket_commit_or_final_cart_count`.
- The proof still uses the McD projected PDP Add route; generic physical
  bottom-bar hit testing remains open.

Execution order for the next rally slice:

1. Stop re-entering Add after a storage-bound click has already reached
   `BasketAPIHandler.A1(...)`, so diagnostics do not create duplicate Realm
   commit attempts.
2. Make Realm/BaseStorage pseudo-native semantics stable enough for the McD
   cart path: shared realm lifetime, schema info, table refs, refresh/closed/
   frozen/transaction answers, key-path mapping, query/result row handles, and
   cart row mutation.
3. Add checker acceptance for the new frontier: `maxQtyBefore=99`, basket
   commit reached, no SIGBUS, then cart count or Add LiveData mutation.
4. Only after storage is stable, replace the McD projected PDP Add route with
   generic View hit testing and physical/ADB touch acceptance.

## Two-Day Rally Supervisor Command Board - 2026-05-02 14:30 PT

The contract workstreams now converge on one final product proof: real stock
McDonald's dashboard -> PDP -> Add -> cart/bag state inside Westlake child
`dalvikvm`, with no direct phone ART McDonald's process and no mock UI counted
as success.

Active gap split:

- **PF-625 storage/runtime is the critical path.** The JustFlip config failure
  is solved; do not spend time reseeding `maxQtyOnBasket`. The open failure is
  Realm/BaseStorage/BasketAPI after `BasketAPIHandler.A1(...)`. First action is
  to suppress duplicate Add re-entry after a storage-bound click, then harden
  Realm pseudo-native handles and table/result semantics.
- **PF-624 stock Add/cart remains red until app state mutates.** Passing
  markers must show `MCD_JUSTFLIP_BASKET_FLAG_SEED` with `99`, stock Add entry,
  downstream basket commit reached, no fatal signal, and either cart count,
  bag screen, or app Add LiveData mutation.
- **PF-623 generic input is required for final acceptance but is second after
  storage.** The projected McD Add route is still diagnostic. Final phone proof
  needs physical/host touch -> generic View hit test -> `pdpAddToBagButton`.
- **PF-622 lifecycle remains a quality gate.** Soft PDP lifecycle recovery can
  be used to find the storage frontier, but the final success claim requires a
  normal enough Fragment/DataBinding/observer path that Add works without a
  soft-state-only bridge.
- **PF-626 proof automation owns false-positive control.** The checker must
  keep the full gate failed until subprocess purity, streamed logcat,
  dashboard/PDP evidence, stock Add, storage nonfatality, and cart/bag state
  all appear in one artifact.

48-hour execution cadence:

1. **Hour 0-4:** build and run the re-entry suppression patch; verify the next
   phone artifact no longer performs multiple generated-binding/model fallback
   commits after a storage-bound Add error. Expected new marker:
   `MCD_PDP_ADD_REENTRY_SUPPRESSED`.
2. **Hour 4-16:** patch Realm pseudo-native storage in
   `$HOME/art-latest/patches/runtime/interpreter/interpreter_common.cc`.
   Required stable surfaces are `OsSharedRealm`, `OsSchemaInfo`,
   `OsRealmConfig`, `Table`, `TableQuery`, `OsResults`, `UncheckedRow`, and
   key-path mapping for `class_BaseCart`, `class_KeyValueStore`, and
   `class_Configuration`.
3. **Hour 16-28:** prove stock Add reaches the downstream basket path without
   SIGBUS. If cart still does not mutate, the artifact must show a precise
   stock-app rejection marker rather than a VM/runtime crash.
4. **Hour 28-38:** make generic input own dashboard item, PDP Add, quantity,
   customize, back, and bag/cart entry taps. Projected fallbacks stay present
   only as labeled rescue paths.
5. **Hour 38-48:** run repeated clean phone gates and update OHOS southbound
   docs with every storage, input, network, and lifecycle dependency needed to
   reproduce the same contract on musl/OHOS.

Canonical proof command after each patch:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_storage_cart_gate
```

Rally stop condition:

- Stop only for a user decision if the real stock app requires credentials,
  production secrets, or a legal/API choice outside this repo.
- Otherwise keep converting every failure into one of: runtime patch, shim
  patch, proof harness patch, or southbound OHOS contract update.

## Two-Day Rally Proof Update - 2026-05-02 14:49 PT

Latest proof:

- Artifact:
  `artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/`.
- Phone/runtime hashes:
  - `aosp-shim.dex=a9bef5897ced463563451bb2df5e126ab3889fd7a2bf0bb8533378ceb53c6d37`
  - `dalvikvm=09dd2955f5b51a233e7c28c2c25e297b5d423e175f0f4c6f7b347f099274423e`
- Checker rerun:
  `check-real-mcd-proof-rerun.txt`.

What moved forward:

- `proof_phone_runtime_hashes_current` now passes after the checker strips CRLF
  from ADB shell hash output.
- Dashboard -> PDP -> stock Add entry remains reproducible with subprocess
  purity and no direct McDonald's ART process.
- JustFlip config remains fixed:
  `MCD_JUSTFLIP_BASKET_FLAG_SEED ... maxQtyBefore=99 ... maxItemBefore=99`.
- Downstream basket storage is reached once:
  `PASS mcd_pdp_downstream_basket_commit_reached count=1`.
- The duplicate Add re-entry/SIGBUS path is stabilized:
  `MCD_PDP_ADD_REENTRY_SUPPRESSED ... reason=downstream_storage_entered`, and
  the checker reports no fatal signal and `sigbus=0`.

Current red gate:

- The first app-owned Add attempt throws
  `IllegalStateException: Telemetry not initialized` after entering
  `BasketAPIHandler.A1(...)`.
- Cart state remains unchanged:
  `CartInfo.totalBagCount=0` and `cartSizeWithoutOffers=0`.
- `normalAdd`, `editAdd`, and `productLimit` LiveData remain null after the
  storage-entered click.
- Network bridge still returns 599 for McD GraphQL endpoints in this run.
- Generic PDP input and normal Fragment lifecycle remain open:
  `mcd_full_app_generic_pdp_input_gate=FAIL`,
  `mcd_full_app_lifecycle_gate=FAIL`.

Next execution order:

1. Add a targeted telemetry boundary fix for McD core/domain telemetry so
   `Telemetry not initialized` becomes a nonfatal no-op or initialized state,
   not the basket continuation blocker.
2. Rerun the same phone gate and require:
   - no SIGBUS;
   - no `Telemetry not initialized` abort after `BasketAPIHandler.A1`;
   - `normalAdd`, `editAdd`, cart count, or precise stock rejection changes.
3. If telemetry is closed and cart still does not mutate, return to
   Realm/BaseCart row mutation semantics with the current one-shot Add proof.

## Two-Day Rally Command Board - 2026-05-02 14:55 PT

This is the active execution board for the 48-hour rally. The goal is not a
mock McDonald's screen and not direct phone ART execution. The goal is a real
stock McDonald's APK dashboard/PDP/Add/cart path running inside the Westlake
guest `dalvikvm` subprocess, with proof that can be replayed on the Android
phone and translated into the OHOS southbound contract.

Current baseline artifact:

```text
artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/
```

Current accepted baseline:

- real dashboard, popular section, promotions section, and PDP XML enhancement
  are visible through the Westlake path;
- Westlake subprocess purity passes: `direct_mcd_processes=0`;
- stock PDP Add enters `BasketAPIHandler.A1(...)` once;
- duplicate Add re-entry is suppressed after storage entry, so the normal gate
  no longer SIGBUSes;
- phone runtime hash proof passes after CRLF-safe ADB hash parsing.

Current P0 blockers, in order:

1. **PF-624 telemetry/cart continuation**
   - Symptom: first stock Add attempt throws
     `IllegalStateException: Telemetry not initialized`.
   - Execution detail: seed or no-op McD core telemetry through the app
     classloader before stock Add, then rerun the same gate.
   - Success marker: `MCD_TELEMETRY_MANAGER_SEED` present, no
     `Telemetry_not_initialized` abort after `BasketAPIHandler.A1`, and either
     Add LiveData/cart mutates or the next exact stock blocker is exposed.
   - Do not count: synthetic cart count, skipping `BasketAPIHandler.A1(...)`,
     or declaring success from `MCD_PDP_ADD_REENTRY_SUPPRESSED` alone.

2. **PF-625 Realm/BaseCart mutation fidelity**
   - Symptom after telemetry is fixed: cart may still remain
     `totalBagCount=0` and `cartSizeWithoutOffers=0`.
   - Execution detail: preserve pseudo-native Realm object identity and row
     state across shared realm, schema, table, query, result, row, and key-path
     handles. Focus tables are `class_BaseCart`, `class_KeyValueStore`, and
     `class_Configuration`.
   - Success marker: real app cart state changes or the stock app emits a
     precise nonfatal rejection without SIGBUS.
   - Do not count: one-shot storage-entry proof without cart/LiveData mutation.

3. **PF-623 generic PDP input**
   - Symptom: projected fallback reaches Add, but
     `mcd_full_app_generic_pdp_input_gate count=0`.
   - Execution detail: physical ADB/host touch must route through root
     hit-testing into `pdpAddToBagButton` and invoke app listener through
     normal View dispatch.
   - Success markers: `WESTLAKE_GENERIC_TOUCH_SYNTH_DOWN`,
     `WESTLAKE_VIEW_TOUCH_LIFECYCLE`, and `WESTLAKE_VIEW_PERFORM_CLICK` before
     the McD Add marker.
   - Do not count: a final proof that depends on
     `MCD_ORDER_PDP_PROJECTED_FALLBACK`.

4. **PF-622 lifecycle/observer readiness**
   - Symptom: `mcd_full_app_lifecycle_gate` fails with
     `fragment_resumed=0` and soft resume recovery markers only.
   - Execution detail: make AndroidX Fragment/LifecycleRegistry state reach
     STARTED/RESUMED generically, so generated binding observers can activate
     without McD-only direct invocation.
   - Success marker: normal Fragment resume and LiveData observer dispatch
     markers with no hard-resume SIGBUS.

5. **PF-626 proof closure**
   - Symptom: checker still reports full gate failure even when dashboard/PDP
     subproofs pass.
   - Execution detail: every run must stream logcat, collect screenshot, collect
     phone/local hashes, and rerun checker from the artifact directory.
   - Success marker: full gate only turns green when subprocess purity,
     dashboard/PDP, telemetry, generic input, lifecycle, and cart mutation or
     exact stock rejection are all proven.

Swarm ownership for this rally:

- Supervisor/local: `shim/java/com/westlake/engine/WestlakeLauncher.java`,
  especially telemetry seeding and Add-path marker discipline.
- Storage/native agent: `$HOME/art-latest/patches/runtime/interpreter/`
  and Realm/BaseStorage pseudo-native state.
- Input agent: `shim/java/android/view/*`, `Window*`, hit testing, and generic
  host touch dispatch.
- Lifecycle agent: `shim/java/androidx/fragment/*`,
  `shim/java/androidx/lifecycle/*`, and generic observer readiness.
- Proof agent: `scripts/check-real-mcd-proof.sh`,
  `scripts/run-real-mcd-phone-gate.sh`, artifact validation, and proof docs.

Next command after each patch:

```bash
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_telemetry_cart_gate
```

## Two-Day Rally Proof Update - 2026-05-02 15:28 PT

This update supersedes the 14:55 PT command board for PF-623 through PF-625.

Accepted normal-gate artifact:

```text
artifacts/real-mcd/20260502_150027_mcd_48h_telemetry_cart_gate/
```

Key proof deltas:

- `PF-624` telemetry is no longer the active Add blocker. The proof records
  `MCD_TELEMETRY_MANAGER_SEED count=2`, `BasketAPIHandler.A1(...) count=4`,
  and `telemetryAbortAfter=0`.
- `PF-623` generic PDP input is green for the current McD Add route. The proof
  records `mcd_full_app_generic_pdp_input_gate count=5`.
- The stock Add click reaches the app listener and mutates Add LiveData:
  `normalAdd=MutableLiveData_value_true...` and
  `mcd_pdp_livedata_mutation_status mutated=5`.
- Cart still does not mutate: `cartSizeWithoutOffers=0`,
  `totalBagCount=0`, and `mcd_full_app_cart_mutation_gate` remains red.
- Lifecycle still does not close generically: `fragment_resumed=0` with soft
  resume recovery only.
- Network is still not production-successful for McD GraphQL splash endpoints:
  bridge attempts return status `599`.

Unsafe-storage frontier artifact:

```text
artifacts/real-mcd/20260502_151742_mcd_48h_true_unsafe_cart_commit_probe/
```

The unsafe probe used explicit phone flag files for model/storage/observer
dispatch opt-in. It is not a success artifact. It is the current crash map.

- Observer dispatch is allowed in this run:
  `MCD_PDP_OBSERVER_DISPATCH_GATE allowed=true
  reason=unsafe_observer_dispatch_opt_in`.
- Telemetry remains seeded and nonfatal:
  `MCD_TELEMETRY_MANAGER_SEED count=1`,
  `basketA1=2 telemetryAbortAfter=0`.
- The child VM crashes only after the Realm/BaseCart path is entered:
  `Fatal signal 7 (SIGBUS), code 1 (BUS_ADRALN)
  fault addr 0xfffffffffffffb17`.
- The proof classifies the active blocker as
  `type=realm_sigbus realmNative=6735 sigbus=1`.
- The observed Realm sequence includes `class_BaseCart`,
  `TableQuery.nativeRawPredicate("_maxAge < $0")`,
  `TableQuery.nativeRawPredicate("_maxAge != $0")`,
  repeated `cartStatus = $0` predicates,
  `TableQuery.nativeFind(...)`, `OsSharedRealm.nativeBeginTransaction(...)`,
  and shared-realm close/transaction behavior before the SIGBUS.

Current execution order:

1. **PF-625 is the critical path.** Implement targeted portable Realm/BaseCart
   table/query/result/row/transaction semantics so the unsafe observer/storage
   route no longer SIGBUSes and either cart count mutates or the app emits a
   stock nonfatal rejection.
2. **PF-622 remains P0 but is second.** Normal Fragment/LifecycleRegistry
   resume must replace the soft-resume marker before final full-app closure.
3. **PF-624 must stay green.** Keep telemetry manager seeding/no-op publisher
   in place and reject any regression that brings back
   `Telemetry not initialized`.
4. **PF-623 should be generalized, not reworked blindly.** The current proof
   accepts generic PDP input; workers should harden hit dispatch while avoiding
   McD-only projected fallback as final success.
5. **PF-626 proof closure must keep unsafe and normal runs separate.** Unsafe
   flag-file probes are for crash isolation only. The final PF-621 claim must
   pass with unsafe flags removed.

Next proof loops:

```bash
# Normal acceptance loop, unsafe flags off.
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=420 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_realm_cart_fix_normal_gate
```

```bash
# Crash-isolation loop, only after a Realm/storage patch.
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 shell \
  "mkdir -p /sdcard/Android/data/com.westlake.host/files; \
   printf 1 > /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_model_commit.txt; \
   printf 1 > /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_storage_commit.txt; \
   printf 1 > /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_observer_dispatch.txt"

env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_GATE_SLEEP=360 WESTLAKE_GATE_INTERACT=1 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_realm_cart_fix_unsafe_probe

/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 shell \
  "rm -f /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_model_commit.txt \
         /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_storage_commit.txt \
         /sdcard/Android/data/com.westlake.host/files/westlake_mcd_unsafe_observer_dispatch.txt"
```

## Two-Day Rally Proof Update - 2026-05-02 15:47 PT

This update supersedes the 15:28 PT unsafe crash map. The q7 observer route is
not the best current PF-625 proof because the corrected run below left observer
dispatch disabled and still reproduced the storage crash:

```text
artifacts/real-mcd/20260502_153758_mcd_48h_model_storage_no_observer_true_probe/
```

Corrected proof facts:

- Phone/local runtime hashes match. The active shim hash is
  `6c656673dd396b9ec699ef51e3ebc578837e8a3a37c62eef03959b48fc803f4d`.
- Unsafe model/storage flag files were enabled only for this crash-isolation
  run; unsafe observer dispatch was deliberately missing:
  `westlake_mcd_unsafe_observer_dispatch.txt=missing`.
- The app still reaches the stock Add path, seeds telemetry, and mutates Add
  LiveData before storage:
  `MCD_TELEMETRY_MANAGER_SEED count=1`, `basketA1=4 telemetryAbortAfter=0`,
  and `mcd_pdp_livedata_mutation_status mutated=1`.
- Observer dispatch remains blocked by policy:
  `MCD_PDP_OBSERVER_DISPATCH_GATE allowed=false
  reason=observer_dispatch_opt_in_required`.
- The model/storage route is the crash frontier:
  `MCD_PDP_A7_GATE invoked=true allowed=true`,
  `MCD_PDP_STORAGE_SAFETY_GATE allowed=true
  reason=unsafe_storage_commit_opt_in`, followed by
  `Fatal signal 7 (SIGBUS), code 1 (BUS_ADRALN)
  fault addr 0xfffffffffffffb17`.
- The last cart marker before the crash is
  `MCD_PDP_CART_GATE phase=..._before_model_x` with
  `cartSizeWithoutOffers=0 totalBagCount=0`.

New PF-625 execution target:

1. Implement a deterministic `class_BaseCart` row in the portable Realm shim.
   It must survive shared-realm, table, query, result, row, begin transaction,
   commit/cancel, refresh, and close calls.
2. Make observed BaseCart predicates return stable row handles instead of
   empty results:
   `_maxAge < $0`, `_maxAge != $0`, and `cartStatus = $0` with status values
   observed as `1`, `2`, `5`, and `3`.
3. Expand the BaseCart column map to the observed McD schema, including
   `nickName`, `orderPaymentId`, `deliveryFee`, `productionResponse`,
   `confirmationNeeded`, `estimatedDeliveryTime`, `isLargeOrder`,
   `isSpotNumberRequired`, `orderValue`, `tenderType`, `totalEnergy`,
   `randomCode`, `barCode`, `checkInCode`, `payments`,
   `estimatedInStoreDeliveryTime`, `cartOffers`, `cartPromotions`,
   `cartProducts`, `deposits`, `fees`, `cumulatedTaxInfo`, `savings`,
   `validationType`, `operationMode`, `options`, `isTpOrder`, `priceType`,
   `lastValidatedTime`, `resultCode`, and `offersValidation`.
4. Add row setter coverage only when reached by the next proof. Do not invent a
   synthetic cart count in `WestlakeLauncher`; the app must either mutate
   through the stock path or emit its own exact stock rejection.
5. After every storage patch, run the model/storage no-observer unsafe loop
   first. Only when SIGBUS is gone should workers run the normal no-unsafe
   acceptance loop.

The current two-day rally order is now:

- P0: PF-625 Realm/BaseCart row/query/result/transaction semantics.
- P1: PF-622 normal Fragment/LifecycleRegistry RESUMED state, because final
  observer correctness cannot rely on soft resume recovery.
- P1: PF-626 proof hardening so unsafe flags, projected-only input, and mock UI
  cannot accidentally satisfy PF-621.
- P2: McD network `599` and broader REST/GraphQL coverage after cart storage
  stops crashing.

Crash-isolation loop, with observer dispatch off:

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
  ./scripts/run-real-mcd-phone-gate.sh mcd_48h_model_storage_no_observer_true_probe
```

## Two-Day Rally Proof Update - 2026-05-02 16:27 PT

This update supersedes the 15:47 PT crash frontier. The latest no-observer
unsafe probe is:

```text
artifacts/real-mcd/20260502_161942_mcd_48h_skip_t2_after_model_x_no_observer_probe/
```

What moved:

- Phone/local runtime hashes match. The active runtime hash is
  `15143258367ff1363e52991dd176153e5f7d6dbf8bbbbc9f5958a52ddc96791f`;
  the active shim hash is
  `b47d63b69e339ed003f5a9a6099e86b8bdf9b6ed5a57ca2ea8bebdd92cebf0e9`.
- Real McD is running as a Westlake guest `dalvikvm`; subprocess purity and
  `proof_real_mcd_guest_dalvikvm` now pass in the unsafe probe.
- The process is nonfatal after Add: `no_fatal_failed_requirement count=0`,
  `sigbus=0`, and `pf626_current_downstream_blocker type=none`.
- The PDP Add path reaches stock `OrderPDPViewModel.X(...)` and downstream
  basket code: `mcd_pdp_downstream_basket_commit_reached count=12`.
- The optional post-`model_x` `T2()` analytics call is now gated off because it
  was the previous `CartProduct.getChoices()` NPE source. With that gate,
  `choicesNullNpe=0`.
- BaseCart active predicates now return rows:
  `_maxAge < $0`, `_maxAge != $0`, and `cartStatus = $0` all pass the proof.
- Stock Add LiveData mutates: `mcd_pdp_livedata_mutation_status mutated=5`.
- Generic PDP input is green in this probe:
  `mcd_full_app_generic_pdp_input_gate count=4`.

What is still red:

- This was an unsafe model/storage probe, so it cannot close PF-621:
  `unsafeOff=0`.
- Cart/bag state still does not mutate:
  `cartSizeWithoutOffers=0 totalBagCount=0` and
  `mcd_full_app_cart_mutation_gate cartOrBagMutated=0`.
- Product stock/quantity data is still suspect: every cart-product proof shows
  `maxQttyZero=14`, and the visible cart marker shows `maxQtty=0`.
- Lifecycle is still not normal AndroidX correctness:
  `mcd_full_app_lifecycle_gate soft_resume_recovery=6 fragment_resumed=0`,
  with `lifecycleState=null`.
- Network remains a separate platform gap:
  `network_bridge_or_urlconnection` fails with McD GraphQL responses at status
  `599`.
- The proof script must be updated to count the new
  `MCD_PDP_REALMLIST_HYDRATE` marker as cart-product list hydration; the old
  checker reports `markers=0` even though the log contains RealmList hydration
  markers and `choicesNullNpe=0`.

New two-day rally execution order:

1. **PF-625A cart persistence after nonfatal model_x.**
   Own the Realm row/list/write path and the PDP product-stock hydration path.
   The next proof movement must be either `cartOrBagMutated=1` or a stock app
   exact rejection marker, not a synthetic cart count in `WestlakeLauncher`.
2. **PF-625B product stock limits.**
   Hydrate the real PDP product so `getMaxQttyAllowedPerOrder()` no longer
   reports `0` when the Add button is enabled. Prefer real catalog/PDP model
   fields first; only use deterministic offline defaults when the app has no
   live network-backed value.
3. **PF-622 lifecycle.**
   Make Fragment/view lifecycle owners report RESUMED through normal AndroidX
   state so stock observers can run without unsafe q7 dispatch.
4. **PF-626 proof hardening.**
   Accept `MCD_PDP_REALMLIST_HYDRATE` as list hydration, keep unsafe probes out
   of final success, and require subprocess-pure guest `dalvikvm`.
5. **PF-620 network.**
   Close McD REST/GraphQL status `599` after cart mutation/rejection is no
   longer blocked.

## Two-Day Rally Proof Update - 2026-05-02 16:39 PT

Current best proof:

```text
artifacts/real-mcd/20260502_162934_mcd_48h_product_stock_hydrate_no_observer_probe/
```

What moved since 16:27 PT:

- Product stock is no longer the active PDP Add blocker. The proof now shows
  `MCD_PDP_PRODUCT_STOCK_HYDRATE` and
  `MCD_PDP_CART_GATE ... quantity=1 productType=PRODUCT maxQtty=99`.
- CartProduct RealmList hydration is green after proof-script correction:
  `MCD_PDP_REALMLIST_HYDRATE ... seen=10 present=10` is counted as
  `mcd_pdp_cartproduct_choices_hydrated markers=3 choicesNullNpe=0`.
- The downstream Add path is still nonfatal:
  `mcd_pdp_downstream_basket_commit_reached count=6`,
  `mcd_pdp_downstream_basket_commit_crash sigbus=0`, and
  `telemetryAbortAfter=0`.
- The remaining cart failure is narrower:
  `cartSizeWithoutOffers=0 totalBagCount=0` after stock basket commit. This is
  now a Realm read-after-write/list-readback or stock basket state propagation
  gap, not an input, product quantity, telemetry, or
  `CartProduct.getChoices()` gap.

New runtime candidate entering the rally:

```text
$HOME/art-latest/build-bionic-arm64/bin/dalvikvm
sha256: 83aceaf740cab758cd8871cf6e0d02414f5ccebde668d807f41e2126698d629b
```

This runtime adds persistent Realm link-list storage and new
`list-create ... size=...` markers. The 16:29 phone proof was still on the
older runtime hash `151432...`, so the next run must deploy the candidate
before judging cart readback.

Immediate execution details:

1. Copy the `83acea...` runtime into `ohos-deploy/arm64-a15/dalvikvm`.
2. Rebuild the shim to pick up current lifecycle/input/product-stock patches.
3. Sync phone runtime through Windows ADB and confirm local/phone hashes match.
4. Run the no-observer unsafe model/storage gate and require:
   - `PFCUT-REALM-WRITE list-create ... column=cartProducts ... size=1`;
   - `PFCUT-REALM-STATE list-get-row ... target=class_CartProduct`;
   - `MCD_PDP_CART_GATE ... totalBagCount=[1-9]` or a stock app rejection.
5. If list readback is green but counters stay zero, instrument stock basket
   propagation through `BasketAPIHandler.A1`, `BaseCartRealmProxy`,
   `CartInfo`, and `CartViewModel.setCartInfo`. Do not fake success counters
   in launcher code.

Current red gates after proof correction:

```text
FAIL proof_unsafe_flags_off unsafe_probe=1
FAIL mcd_full_app_lifecycle_gate soft_resume_recovery=4 fragment_resumed=0
FAIL mcd_full_app_cart_mutation_gate cartOrBagMutated=0 ... maxQttyZero=0 basketCommit=6 sigbus=0
FAIL pf621_final_acceptance_gate unsafeOff=0 subprocess=1 guestDalvikvm=1 telemetry=1 genericInput=1 lifecycle=0 cartProductLists=1 cartOrStockResolution=0
```

## Two-Day Rally Proof Update - 2026-05-02 17:00 PT

Current best deployed-phone proof:

```text
artifacts/real-mcd/20260502_165132_mcd_48h_basecart_row0_lifecycle_probe/
runtime sha256: b1c0b65da3279fd2183a200fa8f5385c94936bbee0e31b82055d567c706b632c
shim sha256: 6c47dbceac413fd86b32658f6602d948754235a54266f3a1db3e3b2d55e0be60
```

What moved:

- Realm `class_BaseCart` row identity is now stable. Active BaseCart queries
  return `row=0`, matching the generated-proxy write target.
- `OsList` link-list readback is green for the observed `BaseCart.cartProducts`
  path:
  `nativeAddRow ... column=cartProducts row=0 size=1`, followed by
  `list-create ... column=cartProducts ... size=1`.
- Product stock remains green in the diagnostic path:
  `MCD_PDP_CART_GATE ... quantity=1 productType=PRODUCT maxQtty=99`.
- CartProduct RealmList hydration remains green:
  `mcd_pdp_cartproduct_choices_hydrated markers=3 choicesNullNpe=0`.

What is still red:

- This remains an unsafe model/storage diagnostic probe, not final acceptance.
- Cart/bag projection is still zero above the native Realm list layer:
  `MCD_PDP_CART_GATE ... cartSizeWithoutOffers=0 totalBagCount=0`.
- AndroidX Fragment/view lifecycle is still not normal:
  `MCD_PDP_LIVEDATA_STATE ... lifecycleState=null` and
  `mcd_full_app_lifecycle_gate ... fragment_resumed=0`.
- McD network remains red with status `599`.

New execution details:

1. Build and deploy the staged runtime patch for
   `OsObjectBuilder.nativeAddObjectList(...)`.
   - Required marker:
     `PFCUT-REALM-WRITE builder-add method=nativeAddObjectList ... listCount=[1-9]`.
   - Required marker:
     `PFCUT-REALM-WRITE builder-commit-list table=class_BaseCart row=0 column=cartProducts count=[1-9]`.
2. Rerun the unsafe diagnostic gate as
   `mcd_48h_builder_object_list_probe`.
   - If `MCD_PDP_CART_GATE totalBagCount` moves above zero, immediately run the
     same route with unsafe flags removed.
   - If native builder/list markers are green but cart counters stay zero,
     instrument stock `BasketAPIHandler.p2()`, `CartInfo`, and
     `CartViewModel.setCartInfo(...)` after `OrderPDPViewModel.X(...)`.
3. Lifecycle worker should stop chasing soft resume only. The next accepted
   movement is a real AndroidX state marker:
   `MCD_PDP_LIVEDATA_STATE ... lifecycleState=STARTED|RESUMED`.
4. Network worker should keep McD REST/GraphQL on the board, but cart closure
   has priority unless the next proof proves cart projection depends on a live
   server response.

## Two-Day Rally Proof Update - 2026-05-02 17:08 PT

Builder-object-list runtime deployed and tested:

```text
artifacts/real-mcd/20260502_170205_mcd_48h_builder_object_list_probe/
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 6c47dbceac413fd86b32658f6602d948754235a54266f3a1db3e3b2d55e0be60
```

Result:

- Real guest/subprocess remains green:
  `proof_real_mcd_guest_dalvikvm` and `westlake_subprocess_purity` pass.
- PDP/dashboard/UI path remains green enough for the rally:
  dashboard sections, real hero/menu/promo/popular fragments, PDP XML, stock
  binding listener, and strict PDP frame pass.
- Product stock and CartProduct lists remain green:
  `maxQtty=99`, `maxQttyZero=0`,
  `mcd_pdp_cartproduct_choices_hydrated markers=3 choicesNullNpe=0`.
- Downstream basket still executes without fatal runtime crash:
  `mcd_pdp_downstream_basket_commit_reached count=6`, `sigbus=0`.
- Direct Realm list readback remains green:
  `list-row method=nativeAddRow ... column=cartProducts row=0 size=1`,
  followed by `list-create ... column=cartProducts ... size=1`.
- No `nativeAddObjectList`/`builder-commit-list` markers appeared on this
  route, so builder object-list support is not the active observed blocker in
  the current Add path.

Remaining red:

- Cart projection remains zero:
  `MCD_PDP_CART_GATE ... cartSizeWithoutOffers=0 totalBagCount=0`.
- Lifecycle marker remains red:
  `lifecycleState=null`, `fragment_resumed=0`; worker analysis says this is at
  least partly a marker/API-shape issue because app AndroidX exposes current
  state as `d()` and fragment `mState=7` is already the resumed state.
- Network remains red:
  McD GraphQL calls return status `599`.
- Final PF-621 remains red because this is still an unsafe diagnostic run.

Next execution details:

1. Add deferred cart projection readback after
   `OrderPDPViewModel.X(cartProduct)` in `WestlakeLauncher.java`.
   - Probe at `250ms`, `1000ms`, and `2500ms`.
   - Marker:
     `MCD_PDP_CARTINFO_READBACK phase=... route=basket_api_p2 stockTotalBagCount=<n> stockCartProductQuantity=<n> vmTotalBagCount=<n>`.
   - If stock `BasketAPIHandler.p2()` is positive and `CartViewModel` is still
     zero, call `CartViewModel.setCartInfo(stockCartInfo)` and emit
     `MCD_PDP_CARTINFO_SET_BRIDGE ... applied=true`.
2. Fix lifecycle proof shape without unsafe observer dispatch:
   - marker should read `getCurrentState()`, then `d()`, then `b()/e()`;
   - marker/gate should accept `fragmentState=7` or `isResumed()==true` instead
     of requiring a non-existent `mResumed` field in the app Fragment build.
3. Rebuild shim, sync phone, rerun the diagnostic gate as
   `mcd_48h_cartinfo_projection_probe`.
4. If `MCD_PDP_CART_GATE` moves above zero, immediately rerun without unsafe
   files. If it stays zero while `stockTotalBagCount=0`, return to PF-625
   runtime storage and inspect `BasketAPIHandler.x1(BaseCart)` list/quantity
   reads.

## Two-Day Rally Proof Update - 2026-05-02 17:30 PT

Current best final-local proof:

```text
artifacts/real-mcd/20260502_172158_mcd_48h_no_unsafe_cartinfo_final_probe/
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 213b34be4483d1f2a200f071b669e4d7d24ca44db46969a5a6c09986c153b1dd
```

What moved:

- `PF-621` final-local acceptance is now green after fixing the proof
  checker's false positive on artifact names containing `no_unsafe`.
- The artifact has `markers=0 flags=0`; unsafe model/storage/observer files
  are absent and no unsafe markers appear in logcat.
- Real McD remains in the Westlake guest child process:
  `proof_real_mcd_guest_dalvikvm` and `westlake_subprocess_purity` pass.
- The dashboard, real child fragments, PDP XML, physical/generic PDP input,
  lifecycle compatibility marker, product stock hydration, Realm
  `CartProduct` lists, downstream basket commit, and CartInfo projection all
  pass.
- The no-unsafe run reaches a positive stock cart readback and projects it into
  the McD `CartViewModel`:
  `MCD_PDP_CARTINFO_SET_BRIDGE ... afterVmTotalBagCount=1 applied=true`.
- Final PF-621 line now passes:
  `unsafeOff=1 subprocess=1 guestDalvikvm=1 telemetry=1 genericInput=1 lifecycle=1 cartProductLists=1 cartOrStockResolution=1`.

What is still red:

- Complete stock-runtime `gate_status` remains `FAIL` because live network is red:
  `network_bridge_or_urlconnection ... status=599 ... unexpected end of stream`.
- The root cause found at 17:30 PT is harness-level and actionable: the gate
  writes `http://127.0.0.1:18080` to the phone and configures `adb reverse`,
  but it did not start `scripts/westlake-dev-http-proxy.py`. Direct WSL curl to
  the McD GraphQL endpoint returns HTTP 200.
- Dashboard item-row XML is still incomplete as a generic rendering surface:
  section shell XML is green, but `layout_home_promotion_item` and
  `layout_home_popular_item_adapter` still show zero in the strict proof.
- The CartInfo projection bridge is acceptable as a proof locator, not the
  final architecture. The next app-quality step is to make the stock lifecycle
  observer/model propagation route update the same `CartViewModel` without a
  McD-specific bridge.

Execution details for the next 2-day rally loop:

1. `PF-620 network` is immediate P0.
   - Patch deployed locally: `scripts/run-real-mcd-phone-gate.sh` now
     auto-starts `scripts/westlake-dev-http-proxy.py` for
     `127.0.0.1:<port>` / `localhost:<port>` when no listener exists.
   - Smoke gate:
     `mcd_48h_proxy_autostart_network_smoke`.
   - Full gate:
     `mcd_48h_proxy_autostart_full_app`.
   - Required proof:
     `network_bridge_or_urlconnection` passes with `PFCUT-MCD-NET ... status=2xx`
     or `WestlakeHttp ... code=2xx`, while PF-621 remains green.
2. `PF-628 model propagation` is P1.
   - Replace the McD-specific `CartInfo.setCartInfo` bridge with a generic
     lifecycle/LiveData observer dispatch fix if the stock app can propagate
     `BasketAPIHandler.p2()` naturally after lifecycle is resumed.
   - Required proof: `MCD_PDP_CARTINFO_READBACK stockTotalBagCount=1` and
     `vmTotalBagCount=1` without `MCD_PDP_CARTINFO_SET_BRIDGE`.
3. `PF-627 generic item XML/polish` is P1.
   - Inflate real dashboard item XML rows, not only the section shells.
   - Required proof:
     `mcd_real_promotion_item_xml_inflated count>0` and
     `mcd_real_popular_item_xml_inflated count>0`.
4. `PF-629 deeper app route` is P1/P2.
   - After network passes, drive from dashboard to PDP to cart/bag and then to
     the next stock screen available without login/payment.
   - Required proof: no direct `com.mcdonalds.app` ART process, no fatal, and
     a visible stock screen transition beyond PDP/cart.

## Two-Day Rally Reset - 2026-05-02 18:10 PT

Current accepted phone gate:

```text
artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 51ba606bc829ab4cf57c759cc2b65f5f71e51dd8d0bbe304df4602ebe5572fbe
gate_status=PASS
```

Accepted in one artifact:

- no unsafe flags or unsafe markers;
- stock McD APK in Westlake guest `dalvikvm`, not a phone ART McD process;
- live McD network 2xx through the Westlake HTTP bridge/proxy;
- real dashboard section XML plus real promotion/popular item-row XML;
- dashboard -> PDP -> stock PDP/basket route;
- Realm cart-product list hydration and downstream basket commit;
- positive `CartInfo` readback and app-visible cart/bag count after Add;
- lifecycle, generic PDP input, subprocess, and guest-DalvikVM proof gates.

Remaining blockers before claiming "full McD app runs":

1. `PF-628` no-bridge model propagation.
   - Current gap: `MCD_PDP_CARTINFO_SET_BRIDGE` still repairs
     `CartViewModel` when stock `BasketAPIHandler.p2()` is positive and the
     singleton is stale.
   - Execution:
     - keep the generic `FragmentViewLifecycleOwner` and
       `MiniActivityManager` lifecycle dispatch patch;
     - add markers for app observer activation after generated binding entry:
       `normalAdd active_1`, observer callback invoked, and no blocked
       `MCD_PDP_OBSERVER_DISPATCH_GATE`;
     - run a no-bridge gate with
       `MCD_PDP_CARTINFO_READBACK stockTotalBagCount=1 vmTotalBagCount=1`
       and zero `MCD_PDP_CARTINFO_SET_BRIDGE`.
   - Acceptance artifact: no unsafe flags, live network still green, PF-621
     still green without the CartInfo bridge.

2. `PF-630` long-soak Realm close/finalizer stability.
   - Current gap: unbounded artifact
     `20260502_174634_mcd_48h_network_pf621_full_app_final` reaches the
     success path and later crashes with `Fatal signal 7 (SIGBUS)` near
     `OsSharedRealm.nativeCloseSharedRealm`.
   - Execution:
     - instrument close/finalizer paths for shared-realm handle ownership,
       thread ownership, double-close, and misaligned handle reuse;
     - make `nativeCloseSharedRealm`, transaction close, and finalizer cleanup
       idempotent for the in-memory Realm subset;
     - preserve table/query/list handles until all generated proxy objects are
       done reading;
     - run a 10-minute post-cart soak gate with repeated dashboard/PDP/cart
       frame publication.
   - Acceptance artifact: no fatal signal, live guest process still present,
     no direct McD ART process, and PF-621 remains green after soak.

3. `PF-629` deeper real stock route coverage.
   - Current gap: accepted proof stops at PDP/cart-state proof, not full
     customer flow coverage.
   - Execution:
     - drive bag/cart screen after Add, then edit/remove/re-add;
     - drive plus/minus and customize from the stock PDP controls;
     - drive back navigation and bottom-nav screen changes;
     - explore location/login-safe screens without requiring credentials or
       payment;
     - record every new Android API miss as `PF-632` or southbound API delta.
   - Acceptance artifact: visible stock screen transition beyond PDP/cart, no
     fatal, no unsafe flags, subprocess purity intact.

4. `PF-632` missing Android framework/API compatibility.
   - Current gap: the bounded proof exposes
     `NoSuchMethodError android.app.AlertDialog$Builder(Context,int)` during
     one stock generated PDP click path.
   - Execution:
     - add the constructor and minimal theme/context plumbing;
     - cover dialog/show/dismiss/click listeners as generic Android APIs;
     - add a controlled probe and then rerun the real McD PDP click path.
   - Acceptance artifact: no AlertDialog constructor error and stock dialog
     APIs either render or no-op with explicit nonfatal semantics.

5. `PF-626/PF-608` OHOS portability lane.
   - Current gap: Android-phone proof is green, but OHOS adapter execution is
     not proven for the same network, Realm, input, lifecycle, and render
     contracts.
   - Execution:
     - keep Android guest-facing APIs unchanged;
     - map HTTP bridge, file/storage, pointer input, frame publication,
       lifecycle dispatch, and Realm in-memory persistence to OHOS/musl
       services;
     - build the OHOS/musl runtime from the same runtime source patch and pass
       symbol gates after every runtime change.
   - Acceptance artifact: the same stock McD proof route runs on OHOS or fails
     at a named southbound API boundary with no phone-only dependency hidden in
     the proof.

Supervisor execution cadence:

- Run one phone proof after every accepted code slice; do not wait for all
  workstreams to batch.
- Keep `scripts/check-real-mcd-proof.sh` strict. A new subproof can be added,
  but `PF-621` stays green only when unsafe, subprocess, network, lifecycle,
  input, dashboard XML, PDP, and cart conditions are all true in one artifact.
- Keep McD-specific bridges labeled and targeted for deletion. They are proof
  locators, not the final architecture.
- Prefer generic Android/OHOS southbound APIs over launcher special cases when
  the same blocker can affect non-McD APKs.
