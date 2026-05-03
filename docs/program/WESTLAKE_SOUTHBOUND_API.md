# Westlake Southbound API Contract

Last updated: 2026-05-02 PT

This document defines the portability boundary Westlake must preserve while
moving from Android-phone proof runs to an OHOS port.

The core rule is:

```text
stock APK / DEX / app JNI expectations
        |
        v
Android-shaped northbound contracts exposed by Westlake
        |
        v
Westlake southbound APIs and compatibility services
        |
        v
Android host adapter for phone proof, or OHOS/musl adapter for port
```

Westlake should not make OHOS `musl` pretend to be Android `bionic` globally.
Instead, Westlake owns the Android-facing contract and translates the required
runtime, framework, JNI, storage, rendering, networking, and device services
into explicit southbound APIs that can be backed by Android during phone proof
and by OHOS during the port.

## Boundary Policy

- The APK must see Android-shaped APIs: framework classes, resource semantics,
  Java/Kotlin runtime behavior, JNI signatures, lifecycle, storage, network,
  rendering, input, and device service facades.
- The host OS must only see Westlake-owned southbound calls. The OHOS port
  should not depend on Android `app_process`, phone ART, Android framework
  services, or arbitrary Android system shared libraries.
- The runtime is allowed to have separate build targets, for example Android
  bionic ARM64 for phone proof and OHOS musl ARM64 for the port. Both builds
  must come from the same source patches and pass the runtime symbol gate.
- No unresolved bionic-only assumption may be counted as portable. If an
  Android proof uses a phone-only delegate, the corresponding OHOS adapter gap
  must remain open in the issue map.

## Current McD Phone Frontier - 2026-05-02 12:46 PT

Current latest stock Add view/data-binding artifact:
`artifacts/real-mcd/20260502_123759_mcd_48h_livedata_seed_stock_click_probe/`.

Latest guarded cart-gate baseline:
`artifacts/real-mcd/20260502_120714_mcd_48h_guarded_model_commit_baseline/`.

Unsafe downstream commit crash artifact:
`artifacts/real-mcd/20260502_115607_mcd_48h_model_x_commit_probe/`.

The current phone proof runs the real McDonald's APK inside the Westlake guest
subprocess and reaches stock PDP Add entry without a direct phone-ART McD
process. It proves:

- dashboard popular item physical tap -> real `HomePopularItemsAdapter`
  listener -> real `OrderProductDetailsActivity`;
- real PDP XML enhancement through Westlake:
  `MCD_REAL_XML_PDP_ENHANCED productInfo=3 scroll=ScrollView normalized=true`;
- PDP binding-field hydration from the inflated tree:
  `MCD_PDP_FIELD_HYDRATE plusOk=true minusOk=true quantityOk=true`;
- stock Add handler entry:
  `MCD_ORDER_PDP_STOCK_ACTION control=add_to_order route=fragment_j8 invoked=true`;
- stock view/data-binding Add entry:
  `MCD_PDP_STOCK_BINDING_PREP ... listenerInstalled=true`,
  `MCD_PDP_STOCK_LIVEDATA_PREP ... after=true`, and
  `MCD_PDP_STOCK_VIEW_CLICK ... route=performClick invoked=true`;
- non-null cart gate before and after `j8`:
  `MCD_PDP_CART_GATE ... cartInfo=CartInfo`;
- hydrated cart product quantity:
  `MCD_PDP_CART_PRODUCT_PREP ... quantitySet=true ... afterQuantity=1`;
- explicit gated downstream commit:
  `MCD_PDP_A7_GATE ... reason=realm_storage_sigbus_risk` and
  `MCD_PDP_STOCK_ADD_COMMIT ... route=model_x_gated_realm_storage`;
- no fatal signal or SIGBUS in the guarded baseline.

This is not yet an OHOS portability claim for the full app. Open southbound
gaps exposed by this proof:

- Fragment lifecycle: current proof uses a labeled soft-state PDP resume bridge
  because the hard `performResume()` path previously SIGBUSed in stock
  listener initialization. OHOS needs the same AndroidX lifecycle contract, not
  a phone-only workaround.
- Input: physical dashboard tap works, but the strongest PDP Add proof still
  reaches projected fallback after generic dispatch reports `handled=false`.
  Accepted full proof needs generic host input -> Westlake dispatch -> Android
  View/Fragment handler parity.
- Data binding/LiveData observer dispatch: Westlake can now install and invoke
  the generated PDP button binding listener, but the observer/cart continuation
  after that generated listener is not proven. OHOS needs the same
  Android-shaped lifecycle and LiveData contract, not a McD-specific direct
  method shortcut.
- Cart state: `CartInfo` is non-null and `CartProduct.quantity` is now
  hydrated to `1`, but `totalBagCount` and `cartSizeWithoutOffers` remain zero
  after stock `j8`; `maxQtty` is still zero. The current downstream blocker is
  the Realm/storage/basket boundary reached by unsafe `s7()` or
  `OrderPDPViewModel.X(CartProduct)` probes. The southbound storage, Realm,
  network, and app-state contracts must support real cart mutation or produce
  an exact stock-app blocker without crashing.
- Proof automation: add a PDP/Add/cart-gate substatus while keeping the full
  gate failed until lifecycle, cart, and generic input are proven.

## Current McD Boundary Update - 2026-05-02 13:07 PT

The two-day rally now treats these as explicit southbound contract surfaces:

- **AndroidX lifecycle/observer dispatch**
  - Android-facing contract: Fragment/Activity lifecycle owners must deliver
    `ON_START` and `ON_RESUME` to app-bundled AndroidX `LifecycleRegistry`
    observers so `LiveData.observe(...)` callbacks become active.
  - Westlake boundary: lifecycle dispatch is owned by the shim and must work for
    generic AndroidX owners, not only McDonald's fragments.
  - OHOS adapter: Ability/XComponent lifecycle events must be translated into
    the same Android-shaped lifecycle events before guest observers are used.
  - Proof markers: `MCD_PDP_OBSERVER_BRIDGE*`, stock observer callback markers,
    and no soft-state-only success claim.

- **Generic input stream ownership**
  - Android-facing contract: a guest `ACTION_UP` click is only valid when the
    same View tree received a matching down/target sequence.
  - Westlake boundary: host pointer/touch-file packets must be normalized into a
    complete Android `MotionEvent` stream and dispatched through decor/root
    hit-testing before any projected fallback.
  - OHOS adapter: pointer events from OHOS input must preserve down/up ordering,
    coordinates, display scale, and target ownership.
  - Proof markers: `WESTLAKE_GENERIC_TOUCH_SYNTH_DOWN`,
    `WESTLAKE_VIEW_TOUCH_LIFECYCLE`, `WESTLAKE_VIEW_PERFORM_CLICK`, and no
    final-proof reliance on `MCD_ORDER_PDP_PROJECTED_FALLBACK`.

- **Generated data-binding and LiveData continuation**
  - Android-facing contract: generated binding listeners, `MutableLiveData`
    values, observer activation, and fragment callback methods must all execute
    as app code.
  - Westlake boundary: diagnostics may seed missing values for a focused proof,
    but the long-term API is lifecycle/data-binding compatibility, not a
    McDonald's direct method shortcut.
  - OHOS adapter: no special OHOS API is required beyond lifecycle, main-thread
    dispatch, and input; the guest-facing Java behavior must stay Android-like.
  - Proof markers: generated binding entry, `j8(true)`, `Z/V`, `q7/Y7`,
    `r7/s7/v8/o7/A7`, and `CartViewModel.setCartInfo`.

- **Realm/BaseStorage/BasketAPI safety gate**
  - Android-facing contract: the basket path may call Realm-backed storage and
    McDonald's basket APIs from `OrderPDPViewModel.X(CartProduct)`.
  - Westlake boundary: the default path must remain nonfatal. Unsafe model proof
    and unsafe storage commit are separate opt-ins until Realm/BaseStorage is a
    portable contract.
  - OHOS adapter: implement the needed Realm table/query/result/row behavior on
    top of Westlake storage primitives or an OHOS storage backend; do not depend
    on Android bionic-only Realm native behavior.
  - Proof markers: `MCD_PDP_STORAGE_SAFETY_GATE`,
    `MCD_PDP_STOCK_ADD_COMMIT`, no `Fatal signal 7`, and eventually a real
    cart-size/bag-count delta.

## Current McD Boundary Update - 2026-05-02 13:18 PT

The latest proof failure exposed one more southbound boundary that must be
tracked explicitly for OHOS portability:

- **Readiness-driven input and frame publication**
  - Android-facing contract: app UI is touchable only after the guest has
    published the active frame and enabled host input polling for that frame.
  - Westlake boundary: proof and production input dispatch must be synchronized
    to guest frame readiness, not host wall-clock sleeps. A touch packet written
    before dashboard/PDP ownership is established can be consumed by the wrong
    Activity/frame and must not be treated as app evidence.
  - OHOS adapter: OHOS pointer delivery must be coupled to the same Westlake
    frame-publication state. The adapter should not inject pointer events into a
    stale Android guest root.
  - Proof markers: dashboard readiness
    (`MCD_DASH_SECTIONS_READY` or `Host input polling enabled for dashboard
    frame`), PDP readiness (`MCD_ORDER_PDP_READY` or
    `MCD_ORDER_PDP_RENDER_ROOT`), generic touch lifecycle, generated binding
    continuation, and no final reliance on projected fallback.

This boundary is separate from McDonald's business logic. It is a core engine
requirement for stock APKs on OHOS: frame ownership, event targeting, and input
ordering must remain deterministic across Activity transitions.

### New Boundary Exposures From The PDP/Add Cart-Gate Proof

The 48-hour PDP/Add proof adds several boundary surfaces that must stay
explicit. They are accepted only as Android-phone proof surfaces until the
OHOS adapter path below is implemented or tracked as an open gap.

- **Dashboard-to-PDP app route cache**
  - Guest API: stock `HomePopularItemsAdapter` listener passes a McDonald's
    SDK `Product` into `OrderProductDetailsActivity`.
  - Westlake boundary: preserve the app-originated product identity and route
    metadata across the dashboard -> PDP transition without creating a fake
    UI-only product.
  - Android proof adapter: the phone proof records
    `MCD_DASH_SEMANTIC_POPULAR_CLICK ... product=...Product` and later uses
    that product to synthesize the stock SDK `CartProduct`.
  - OHOS adapter: keep route/product state in Westlake-owned app-session
    storage that is independent of Android Activity globals or phone process
    state.
  - Open gap: synthesized `CartProduct` now has `quantity=1`, but `maxQtty=0`
    remains incomplete at the cart gate.

- **PDP XML/include binding hydration**
  - Guest API: stock PDP data binding expects inflated
    `fragment_order_pdp` plus included bottom/quantity layouts to populate
    `G0`, plus/minus views, quantity view, and add button targets.
  - Westlake boundary: generic resource/XML inflation, include tag repair,
    ID lookup, and binding-root field hydration from the guest View tree.
  - Android proof adapter: `MCD_REAL_XML_PDP_ENHANCED` and
    `MCD_PDP_FIELD_HYDRATE plusOk=true minusOk=true quantityOk=true`.
  - OHOS adapter: implement the same compiled-resource and View-tree binding
    behavior over Westlake resource streams and OHOS-hosted frame/input
    surfaces, not Android host Views.
  - Open gap: this is still partly a compatibility bridge; full AndroidX data
    binding and Fragment lifecycle parity are not proven.

- **PDP lifecycle compatibility bridge**
  - Guest API: AndroidX Fragment start/resume and stock
    `OrderPDPFragment` listener initialization.
  - Westlake boundary: lifecycle state transitions, attached view state, and
    listener readiness exposed to stock app code.
  - Android proof adapter: current proof uses
    `MCD_PDP_FRAGMENT_RESUME_RECOVERY mode=soft_state` and reaches
    `MCD_ORDER_PDP_READY ... ready=true`, but `resumed=false` remains visible.
  - OHOS adapter: implement real FragmentManager/start/resume semantics in
    Westlake so OHOS does not rely on a phone-only soft-state workaround.
  - Failure behavior: soft-state recovery is diagnostic progress only; full
    app proof must fail until normal lifecycle readiness is proven.

- **Stock PDP Add invocation**
  - Guest API: stock `OrderPDPFragment.j8(true)` and its downstream chain
    through `A7`, `OrderingManager`, basket use cases, cart API handler, and
    `CartViewModel`.
  - Westlake boundary: route a guest input/action to the real stock fragment
    method after `E0/G0/t0` are ready, and record before/after state.
  - Android proof adapter: `MCD_ORDER_PDP_STOCK_ACTION
    control=add_to_order route=fragment_j8 invoked=true` with no fatal signal.
  - OHOS adapter: route the same action through the Westlake input and
    lifecycle contracts; do not special-case Android host callbacks.
  - Open gap: downstream cart mutation is not accepted yet. Unsafe direct
    model commit is opt-in only until the Realm/storage boundary can run
    without SIGBUS.

- **PDP generated data-binding Add listener**
  - Guest API: stock `OrderPdpButtonLayoutBindingImpl` installs a generated
    `OnClickListener` on `pdpAddToBagButton` and reads
    `OrderPDPViewModel.R1()` before dispatching the click.
  - Westlake boundary: data-binding setters, invalidation/execution, generated
    listener installation, and `LiveData` value availability must behave like
    Android enough for the app-owned listener to run.
  - Android proof adapter: latest proof logs
    `MCD_PDP_STOCK_BINDING_PREP ... listenerInstalled=true`,
    `MCD_PDP_STOCK_LIVEDATA_PREP ... before=null after=true set=true`, and
    `MCD_PDP_STOCK_VIEW_CLICK ... route=performClick invoked=true`.
  - OHOS adapter: implement the same data-binding and lifecycle-triggered
    observer contract over Westlake View and lifecycle services; do not replace
    it with direct reflective calls into McD basket methods.
  - Open gap: generated listener invocation is proven, but app observer/cart
    continuation is not. The active next proof must show observer callback,
    `A7`/basket path, cart mutation, or a precise stock-app rejection.

- **Cart-state read probe**
  - Guest API: stock McDonald's cart objects:
    `CartProduct`, `CartInfo`, and `CartViewModel`.
  - Westlake boundary: read and report product quantity/max quantity,
    cart-size, and total-bag-count state before and after stock Add.
  - Android proof adapter: `MCD_PDP_CART_GATE phase=fragment_j8_before/after
    ... quantity=1 cartInfo=...CartInfo cartSizeWithoutOffers=0
    totalBagCount=0`, plus `MCD_PDP_CART_PRODUCT_PREP ... quantitySet=true`.
  - OHOS adapter: back cart state through the portable Realm/storage/network
    contracts, with deterministic object and row state rather than Android
    native Realm loading.
  - Open gap: current guarded proof proves non-null cart and quantity but no
    real cart mutation.
  - Open gap: non-null cart state and `CartProduct.quantity=1` are proven, but
    final cart mutation is not; current `CartInfo.totalBagCount`,
    `cartSizeWithoutOffers`, and `CartProduct.maxQtty` remain zero.

- **Downstream basket commit boundary**
  - Guest API: stock downstream Add path after `OrderPDPFragment.j8(true)`,
    including `OrderPDPFragment.A7(...)`, `OrderPDPViewModel`, basket use
    cases, and `BasketAPIHandler`.
  - Westlake boundary: stock app calls must be able to cross from PDP action
    handling into basket/cart storage and network state without runtime
    alignment faults or phone-only native assumptions.
  - Android proof adapter: the latest proof reaches
    `BasketAPIHandler.A1(...)` after `A7(...)`, then logs
    `Fatal signal 7 (SIGBUS), code 1 (BUS_ADRALN)`.
  - OHOS adapter: the basket commit path must use portable Westlake storage,
    Realm, JNI, and network contracts; a bionic/native Realm shortcut is not
    portable.
  - Failure behavior: SIGBUS in the basket commit path is a hard full-app gate
    failure even though PDP Add reachability and cart-product hydration are
    now useful subproofs.

- **PDP input/projection distinction**
  - Guest API: physical or host-injected touch should dispatch through
    Android-shaped `MotionEvent`, View hit testing, and stock listeners.
  - Westlake boundary: ordered touch packets, coordinate mapping, generic hit
    dispatch, and optional diagnostic projection markers.
  - Android proof adapter: projected Add reports
    `MCD_ORDER_PDP_PROJECTED_HIT ... stockClick=true handled=true`, but the
    current strongest PDP Add path is still not accepted as generic physical
    input parity.
  - OHOS adapter: deliver pointer events through the same Westlake input packet
    contract into the guest View tree.
  - Open gap: full proof must show generic/physical PDP Add, quantity,
    customize, bag/cart, and back navigation.

## Native Library Policy

There are three different native cases and they must not be conflated.

| Case | Westlake policy | OHOS implication |
| --- | --- | --- |
| Westlake runtime native code | Build from source for both bionic and musl | Portable when the OHOS build links and passes the symbol gate |
| Stable platform JNI owned by Westlake | Implement the JNI methods directly in Westlake or OHBridge | Portable when the implementation uses only southbound APIs |
| APK-bundled Android `.so` libraries | Do not assume direct `dlopen` on OHOS musl works | Requires a per-library compatibility contract, source rebuild, or a bionic compatibility capsule |

Directly loading stock APK native libraries on OHOS is not a valid default
strategy. Android `.so` files may depend on bionic ABI details, Android dynamic
linker behavior, Android namespaces, Android system libraries, private symbols,
and Android filesystem/service assumptions.

For McDonald's, Realm is the active example. The preferred portable route is
not to load the Android `librealm-jni.so` on OHOS. The preferred route is to
implement the Realm JNI/storage behavior McD actually needs as a Westlake
southbound storage contract, backed by portable state or an OHOS storage
adapter.

## HTTP And Image Transport Contract

Status on 2026-05-01 17:45 PT: accepted on phone for the real McD dashboard
projection path. The stock McD route runs inside the Westlake guest subprocess,
uses the Westlake user-space HTTP bridge, inflates real dashboard adapter row
XML, and publishes a dense strict display-list frame. This is not yet full
generic stock Android View drawing or generic Glide/ImageView parity.

### Guest API

Observed McD path:

- `McDRequestManager.e(RequestProvider, McDListener)` starts the async request;
- `McDRequestManager.a(RequestProvider)` and
  `McDRequestManager.d(RequestProvider)` produce
  `com.mcdonalds.mcdcoreapp.network.Response`;
- `RequestProvider.b()` supplies the URL;
- `RequestProvider.a()` supplies timeout;
- `RequestProvider.c()` supplies the Gson response class;
- response success requires `Response.b` to be a 2xx status and `Response.d`
  to contain the raw JSON string.

Observed image path in the current proof is partially generic and partially
diagnostic:

- decoded network images preserve original PNG/JPEG bytes in
  `Bitmap.mImageData`;
- `Bitmap.createBitmap(Bitmap)` preserves those bytes across bitmap copies;
- `Bitmap.compress(...)` writes the original bytes when available;
- the strict McD dashboard renderer must not perform fresh network fetches or
  expensive bitmap compression during frame publication; it now uses already
  cached image bytes or already-ready `ImageView` drawable bytes when present.

The stock app's fully generic Glide/OkHttp/ImageView image path remains open,
especially for promotion/popular row images that still render as placeholder or
hash-style tiles in the latest phone screenshot.

### Westlake Boundary

Westlake exposes a host-owned HTTP operation:

```text
bridgeHttpGetBytes(url, maxBytes, timeoutMs) -> byte[]
bridgeHttpLastStatus() -> int
bridgeHttpLastError() -> String
```

The generic URLConnection/`com.android.okhttp` bridge path now permits up to
4 MiB response bodies so stock Glide image fetches are not silently truncated.

Runtime JSON/text payloads must be decoded as UTF-8 at the boundary. The
2026-05-01 05:25 PT proof fixed the previous byte-to-char shortcut that
rendered `McCafé` as mojibake.

The runtime-side McD provider hook converts successful responses into the
stock `Response` object and lets stock Gson/listener code continue. The next
local bridge slice shadows the decoded APK's public
`McDRequestManager`/`McDHttpClient`/`Request`/`Response` surface so calls to
`McDRequestManager.d(RequestProvider)`, `McDRequestManager.e(...)`, and
`McDHttpClient.c/d/e(...)` can route through `bridgeHttpRequest(...)` without
entering the unstable native Realm-backed request cache. The shim surface also
includes the decoded inner enum/class shapes for `Request.Method`,
`RequestProvider.MethodType`, `RequestProvider.RequestType`, and
`McDHttpClient.AutoDisconnectInputStream`.

Phone proof adapter:

- VM writes HTTP requests to the Westlake bridge directory;
- Android host/proxy performs the actual GET through user-space networking;
- VM reads status/body files back;
- proof markers include `PFCUT-MCD-NET] bridge response status=200` and
  `WestlakeHttp GET response code=200`;
- the real McD dashboard projection proof currently reports
  `network_attempt_markers=15`, `network_success_markers=9`,
  `network_error_markers=0`, and `westlake_bridge=9`.

OHOS adapter target:

- implement the same request/response file or message contract inside the
  OHOS Ability process;
- use OHOS networking APIs from the host side, not Android framework
  `HttpURLConnection`;
- preserve timeout, max-body, status, and error semantics;
- support binary image bytes as well as JSON.

### Current Evidence

- `artifacts/real-mcd/20260430_232634_mcd_bridge_menu_recycler_items/`
  fetched McD hero, marketing, and menu JSON through the provider bridge.
- `artifacts/real-mcd/20260430_234616_mcd_live_menu_images_overlay/`
  fetched live S3 category images through the same bridge.
- `artifacts/real-mcd/20260501_093158_mcd_clickable_text_button_count_full_gate/`
  proves live network plus row images with
  `rowImages=7 rowImageBytes=197065`, `network_success_markers=10`,
  `buttons=1`, and `gate_status=PASS`.
- `artifacts/real-mcd/20260501_100855_mcd_two_step_category_navigation_clean_proof/`
  keeps the bridge live through two UI transitions with
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`, and `gate_status=PASS`.
- `artifacts/real-mcd/20260501_172943_mcd_real_dashboard_long_gate_fast_row_images/`
  is the accepted real McD image-prefetch projection proof. It shows stock
  `HomeDashboardActivity`, `fallback=false`, real `layout_home_promotion_item`
  and `layout_home_popular_item_adapter` inflation, real
  `HomePromotionAdapter`/`HomePopularItemsAdapter` rows, and a strict frame of
  `bytes=126064 views=90 texts=14 buttons=3 images=1 rows=8 rowImages=5
  rowImageBytes=331544 overlays=0`.
- `artifacts/real-mcd/20260501_183146_mcd_real_dashboard_projection_scroll_probe_after_patch/`
  is the current accepted interactive projection proof. It keeps the stock
  `HomeDashboardActivity` root with `fallback=false`, proves live Westlake
  network (`westlake_bridge=7`, no network errors), and after an ADB swipe
  changes the phone screenshot hash from
  `50187c3fcfd0858ec8795f6c86533d2f1fb19f316a60d8062f582eb716c6ddea`
  to `d63d41a034ad6e3efb38bf920ffe48d6be480f6523724f8f1b2280b4a641b8d4`.
  Logs include `MCD_DASH_TOUCH_ROUTE phase=down/move/up` and
  `MCD_DASH_SCROLL ... projectionAfter=370 moved=true`.

### Open Gaps

- Stock Glide/OkHttp/ImageView image loading is not generically accepted yet.
  Bitmap byte preservation is in place, but the latest visible McD rows still
  have incomplete promotion/popular image fidelity.
- Stock McD resource/XML progress advanced on 2026-05-01:
  `artifacts/real-mcd/20260501_131109_mcd_real_popular_section_xml_probe/`
  proves all four dashboard child section roots inflate from real McDonald's
  AXML through the generic parser: `fragment_home_dashboard_hero_section.xml`
  resource `0x7f0e0282`, `home_menu_guest_user.xml` resource `0x7f0e0366`,
  `fragment_promotion_section.xml` resource `0x7f0e030e`, and
  `fragment_popular_section.xml` resource `0x7f0e0305`. The same path also
  exposed a portability rule: resource loaders should stream file contents
  through normal read loops and avoid relying on `File.length()` /
  `UnixFileSystem.getLength`, which SIGBUSed in the standalone runtime probe.
- The accepted 17:45 projection path proves real adapter row XML, but the
  older Hero/Menu child-fragment markers and `MCD_DASH_REAL_VIEW_ATTACHED`
  section markers are missing in that path. Treat those as a higher-tier
  stock-fragment parity gap, not as evidence that the current phone screen is
  the old mock fallback.
- This contract is portable in shape, but OHOS networking adapter code is not
  implemented yet.

## Southbound API Families

Each family below has an Android-facing contract, a Westlake-owned southbound
contract, and an adapter implementation for Android proof and OHOS port.

| Family | Guest-facing Android contract | Southbound contract | Current status |
| --- | --- | --- | --- |
| Process/runtime launch | APK runs in Westlake `dalvikvm` subprocess, not phone ART | spawn, env, classpath, app data roots, stdout/stderr, exit/status | Android proof active; OHOS runtime links, host adapter still open |
| Resources/assets | `AssetManager`, `Resources`, `resources.arsc`, XML layout inflation | APK asset reads, binary resource table, file-backed resource streams | Controlled apps accepted; stock McD section-root AXML accepted in the older child-fragment tier; current projection tier accepts real promotion/popular adapter item XML; broader theme fidelity open |
| UI/rendering | `View`, widgets, layout, invalidation, draw, text, image, Material-shaped controls | frame serialization or drawing commands, text/image surfaces, invalidation clock | Stock McD dashboard projection visible and touch-scrollable on phone; post-scroll proof has `views=96 rows=4`; full generic stock View drawing, Material/AppCompat fidelity, and image fidelity still open |
| Input | touch, click, scroll, back, focus, text input | host input packet injection, coordinate mapping, dispatch result | Host touch packets reach the McD projection and visibly move it via explicit projection scroll state; generic ScrollView/RecyclerView dispatch, bottom-nav, and text input remain open |
| Networking/images | Java/libcore HTTP(S), REST APIs, image download, status/errors | HTTP method, URL, headers, body, timeout, redirect, stream/image bytes, UTF-8 text decode | Android host bridge accepted for the real McD projection path; OHOS network adapter parity and generic Glide/ImageView completion open |
| Storage/files | app data dir, cache, shared prefs, file locks, SQLite, cursor windows | key-value, file, lock, database, cursor-window primitives | SharedPreferences/file slices exist; Realm and broad database behavior open |
| Realm/native persistence | Realm Java API expects native JNI table/query/result/row behavior | portable table handles, property/column keys, queries, result sets, rows | Active McD blocker; currently diagnostic/no-op for many methods |
| Device/system services | package info, build, locale, timezone, telephony, location, connectivity | stable provider calls for metadata and device state | Many McD startup providers stubbed; OHOS provider mapping must be explicit |
| Crypto/TLS/security | Conscrypt-like provider, certificates, HTTPS trust behavior | TLS socket/cert store/random/crypto primitives | Partial shim exists; production trust/cert parity remains open |
| Scheduling/background | `Looper`, `Handler`, WorkManager, jobs, alarms, services, receivers | timers, task queue, background service policy, network constraints | Startup survival improved; full lifecycle/background parity open |
| Native loading/JNI | `System.loadLibrary`, JNI registration, stable platform native methods | controlled loader policy, symbol resolution, per-library contracts | Westlake-owned JNI works in slices; arbitrary APK native libraries not solved |
| Diagnostics/evidence | app logs, crash markers, frame stats, boundary traces | structured logs, hashes, screenshots, symbol gates, artifact manifests | Required for every accepted proof |

## Required Southbound Interface Shape

Every new southbound exposure should be documented with this shape before it
is counted as contract progress:

- **Guest API:** the Android class, method, JNI symbol, resource behavior, or
  framework lifecycle entry the APK uses.
- **Semantics:** what a real Android environment promises to the app.
- **Westlake boundary:** the narrow operation Westlake exposes to the host or
  portable service.
- **Android proof adapter:** how the phone proof currently implements it.
- **OHOS adapter:** the intended OHOS Ability/XComponent/musl implementation.
- **Failure behavior:** exception, error code, empty value, timeout, or fallback
  semantics.
- **Evidence:** command, artifact path, hashes, logs, screenshots, and markers
  that prove the behavior.

Do not accept a new broad stub as a southbound API. A broad stub is only a
diagnostic cutout until it has a semantic contract and an OHOS implementation
plan.

## Bionic Vs Musl Reconciliation

Westlake reconciles Android bionic compatibility with OHOS musl portability by
moving compatibility up to explicit contracts:

1. Java/Kotlin bytecode runs under the Westlake runtime. This does not create a
   bionic problem unless execution crosses into native code.
2. Android framework calls are implemented by Westlake shims and southbound
   adapters, not by OHOS pretending to be Android framework services.
3. Westlake-owned native runtime code is dual-built for Android/bionic and
   OHOS/musl from source.
4. Stable platform JNI methods are implemented by Westlake in portable native
   code or by an OHBridge-style adapter.
5. APK-bundled Android native libraries require explicit handling:
   per-library compatibility, source rebuild, or a much larger bionic
   compatibility capsule.

The default McD path should be case 4, not case 5, for Realm. Implement the
Realm JNI behavior as a portable Westlake storage contract first. A future
bionic compatibility capsule can be evaluated later, but it is not the
shortest path to a portable OHOS proof.

## McDonald's Current Southbound Frontier

The current real-McD proof reaches `HomeDashboardActivity` inside the Westlake
guest subprocess, with no latest `Failed requirement` startup failure. The
latest accepted phone proof also renders and interacts with a McD-shaped
dashboard/menu/category-detail boundary harness. That harness proves
Westlake-owned frame, input, HTTP/image, and subprocess boundaries; it is not
yet full stock McD UI parity because the visible menu/detail surfaces still
come from McD-specific layout builders rather than generic upstream XML/order
module execution.

The active frontier is the Realm/storage family:

- tables observed: `class_KeyValueStore`, `class_BaseCart`;
- predicates observed: `_maxAge < $0`, `_maxAge != $0`, `key = $0`,
  `cartStatus = $0`;
- row/result calls observed:
  `OsSharedRealm.nativeGetTableRef(...)`, `Table.nativeWhere(...)`,
  `TableQuery.nativeRawPredicate(...)`, `OsResults.nativeCreateResults(...)`,
  `OsResults.nativeSize(...)`, `Table.nativeGetRowPtr(...)`,
  `UncheckedRow.nativeGetLong(...)`, `UncheckedRow.nativeGetString(...)`;
- current gap: table handles, property/column keys, query/result handles, row
  handles, row cardinality, and row values are still mostly diagnostic no-op or
  zero/empty behavior.

Next implementation target:

1. Track Realm table handles from `OsSharedRealm.nativeGetTableRef(...)`.
2. Track schema property handles and stable column keys.
3. Track query handles and predicates without forcing global result counts.
4. Return targeted `KeyValueStore` and `BaseCart` result sizes and rows.
5. Implement row getters using a portable backing store.
6. Keep the implementation source-built and compatible with both the Android
   bionic and OHOS musl runtime builds.

This closes a real southbound storage contract. It is more valuable than adding
another McD-specific fallback UI frame.

## Display-List Frame Transport Contract

Status on 2026-05-01 10:12 PT: implemented for the Android phone proof and
accepted by artifact
`artifacts/real-mcd/20260501_100855_mcd_two_step_category_navigation_clean_proof/`.

### Guest API

The guest runtime emits Westlake display-list frames using the existing binary
frame protocol:

- `DLST` magic as little-endian `0x444C5354`;
- little-endian frame payload size;
- display-list payload with draw ops such as rect, text, image, and bitmap.

For real McD phone proof, the host exports `WESTLAKE_FRAME_PATH`. When present,
the guest writes strict dashboard frames to that file instead of stdout. This
keeps binary frame data out of stderr/stdout text logging and makes the transport
portable to a host surface implementation that is not Android-specific.

### Android Phone Adapter

The Android host:

- creates `westlake_frames.dlst` in the host app private VM directory;
- sets `WESTLAKE_FRAME_PATH` in the guest process environment;
- drains guest stdout separately as text;
- tails the frame file through `TailFileInputStream`;
- feeds frames to `DisplayListFrameView`;
- closes old readers on restart, binds the tail reader to the exact child
  process, handles truncation, and resyncs after bad frame sizes.

Accepted proof markers:

- `Frame: ... bytes -> View`;
- `STRICT_IMAGE_ROW ... bytes=... real=true`;
- `Strict dashboard frame ... views=51 rows=4 rowImages=4 rowImageBytes=123608 overlays=0`;
- `gate_status=PASS`.

### OHOS Adapter Requirement

The OHOS host must provide the same guest-facing contract:

- a writable frame sink path or equivalent byte stream;
- a display-list consumer that replays Westlake draw ops into the OHOS visual
  surface, such as an XComponent/canvas equivalent;
- lifecycle cleanup so old readers and old guest processes cannot consume new
  frames;
- resync/error handling equivalent to the Android host proof.

Do not count Android `SurfaceView`, Compose, or phone-specific stdout behavior
as the contract. The contract is the Westlake frame stream and draw-op replay.

## Window And Display Service Contract

Status on 2026-05-02 01:25 PT: implemented for the Android phone proof slice
needed by the real McD PDP path. This is a guest-facing Android API surface
backed by Westlake shims, not by returning phone framework objects across the
child-first classloader boundary.

### Guest API

The stock APK can call these Android-shaped APIs:

- `Activity.getWindowManager(): android.view.WindowManager`
- `Context.getSystemService(Context.WINDOW_SERVICE)`
- `Window.getWindowManager()`
- `WindowManager.getDefaultDisplay()`
- `WindowManager.getCurrentWindowMetrics()`
- `WindowManager.getMaximumWindowMetrics()`
- `WindowMetrics.getWindowInsets(): WindowInsets`

The current exposed slice is intentionally small: seven guest-facing
window/display entry points plus no-op `ViewManager` add/update/remove methods
for popup/overlay callers.

### Westlake Boundary

Westlake must return shim-owned objects for this contract. Returning the phone
host's real `WindowManager` is not portable and can create classloader
mismatches because the guest APK resolves `android.view.WindowManager` from the
Westlake shim dex.

Accepted proof:

- artifact:
  `artifacts/real-mcd/20260502_011431_mcd_windowmanager_bridge_pdp_guard_probe/`
- local and phone shim hash:
  `fe2c190e8d8b8f66061c6326e87b979ad84ec7d2c60ea7e43d64f3e3191bbeb2`
- prior failure was:
  `NoSuchMethodError: getWindowManager()Landroid/view/WindowManager;`
- current focused tail has zero
  `NoSuchMethodError|getWindowManager|SIGBUS|SIGSEGV|Failed requirement`.

### OHOS Adapter Requirement

OHOS does not need to expose Android's real window manager. It must provide the
same Westlake guest semantics:

- default logical display metrics;
- current and maximum window bounds;
- stable empty or real insets;
- no-op or mapped popup/window add-update-remove calls;
- all returned objects loaded from the Westlake guest/shim classpath.

If OHOS later has a native window/display service, it should feed values into
the Westlake shim objects rather than leaking OHOS or Android host objects into
guest bytecode.

## Input Transport Contract

Status on 2026-05-01 10:12 PT: implemented for Android phone proof for taps,
scroll, root-aware generic click, Start Order menu-surface navigation, and one
category-card detail navigation. Broader widget navigation still needs work.

### Guest API

The stock APK sees Android-shaped input:

- `MotionEvent` down/move/up packets;
- `Activity.dispatchTouchEvent(...)`;
- `ViewGroup`/`View` dispatch;
- `View.performClick()` and `OnClickListener` execution for clickable targets.

### Westlake Boundary

Westlake owns the host-to-guest input packet contract:

```text
action:int32, x:int32, y:int32, seq:int32
```

The Android host writes packets into the `WESTLAKE_TOUCH` path. The guest
render loop reads only new sequence numbers, dispatches `MotionEvent`s, and for
accepted McD dashboard proof now performs root-aware generic hit testing over
the same selected render root used by the strict frame path.

Current accepted markers:

- `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView ... handled=true`;
- `MCD_ORDER_NAV_OPENED source=start_order_tile_menu`;
- `GENERIC_HIT_CLICK target=android.widget.LinearLayout leaf=android.widget.ImageView ... handled=true`;
- `MCD_CATEGORY_NAV_OPENED label=Extra_Value_Meals source=category_detail`;
- screenshot hashes prove dashboard -> menu -> detail transitions:
  `fe12e38e867038b3dc866fa71c867f31685f95fd14b47a59abcfdb794491b36e`
  -> `a404b1c815f6dc578e1fe382e4fb8f04c73d20a82060815f02db5611a4d7cc3c`
  -> `12f2c6078d46f2cfb613e0cde6118e684c8f6281acbd1159096c13429c2f0466`;
- `Dashboard fallback direct touch routed` and `MCD_DASH_ACTION` are both
  absent in the accepted navigation artifact.

### OHOS Adapter Requirement

The OHOS host must provide the same packet or message semantics: ordered
action/x/y/sequence events, coordinate mapping into the Westlake surface, and a
guest-visible input source that does not depend on Android `InputManager`,
Compose, or phone-specific ADB behavior.

## Definition Of Done For An OHOS-Portable Southbound Slice

A southbound slice is not complete until all of these are true:

- Android proof runs the stock or controlled APK inside Westlake guest
  `dalvikvm`, not phone ART app execution.
- The same source builds for the OHOS/musl runtime target.
- The Android proof records artifact path, screenshot or functional marker,
  host APK hash, shim hash, runtime hash, and relevant boundary logs.
- The OHOS adapter behavior is either implemented or explicitly listed as an
  open issue with the same guest-facing contract.
- The implementation does not depend on arbitrary Android system libraries
  unless the issue explicitly declares a bionic compatibility-capsule scope.

## Open Risks

- A full bionic compatibility capsule is large: Android linker namespaces,
  libc/libm/libdl behavior, TLS/thread primitives, system properties, asset
  paths, SELinux-like expectations, and Android system library symbols.
- Loading APK native libraries without such a capsule can pass on Android-phone
  proof and fail immediately on OHOS.
- Overbroad noops can move the app forward while hiding the real contract.
  Realm cardinality is the known recent example: global fake result sizes
  regressed before the dashboard.
- UI proof without storage/network/data proof can become a mock. For McD,
  dashboard density must be fed by real-enough app state, not a direct frame
  fallback.

## Current McD Southbound Boundary - 2026-05-02 13:51 PT

The newest McD full-app frontier adds one data/config boundary and one proof
boundary:

- JustFlip/config flags are part of the app data contract, not UI. The real
  McD Add path calls `AppCoreUtils.getMaxQtyOnBasket()` through
  `OrderHelper.getMaxBasketQuantity()`. On the Android phone proof this path
  currently logs a null `maxQtyOnBasket` value and then throws before Add
  LiveData is produced. OHOS must either support the same JustFlip/config flag
  resolution path or Westlake must provide a portable app-data adapter with the
  same Java/Kotlin object contract.
- Generated DataBinding click dispatch is now a required proof marker:
  `OrderPdpButtonLayoutBindingImpl.a(2, View)` must be observable before any
  McD-specific fallback is accepted.
- A diagnostic fallback to stock `OrderPDPViewModel.Z()` is allowed only to
  separate the app Add decision boundary from the Realm/storage cart commit
  boundary. It is not a final substitute for generic input or generated
  binding compatibility.
- Realm/BaseStorage/BasketAPI remains a separate southbound storage contract.
  The normal full gate must keep unsafe cart commit disabled until stable table,
  transaction, refresh, row, and object-lifetime semantics are implemented for
  the observed McD cart path.

OHOS adapter implications:

- Provide app-visible configuration storage for remote/config flags, including
  stable JSON/config parsing and nullable-default behavior matching Android.
- Preserve generated DataBinding listener invocation semantics through
  `View.performClick`, `View.callOnClick`, and `dispatchTouchEvent`.
- Provide portable diagnostics equivalent to streamed logcat for proof
  artifacts, because late-only log collection already hid real dashboard
  success on Android.

## Current McD Southbound Boundary - 2026-05-02 14:25 PT

The JustFlip/config boundary is now implemented for the observed Add path:

- Westlake provides app-visible config values through the McD JustFlip
  `FlagResolver` contract for:
  - `maxQtyOnBasket -> {"maxQtyOnBasket":99}`
  - `maxItemQuantity -> {"maxItemQuantity":99}`
- Phone proof shows the app deserializes those values and the
  `AppCoreUtils.getMaxQtyOnBasket()` null path is no longer the blocker.

The new southbound boundary is Realm/BaseStorage:

- The stock Add path enters McD SDK storage through
  `BasketAPIHandler.A1(...)` and `BaseStorage.<init>(...)`.
- Realm calls observed in the failing proof include shared realm creation,
  schema info, auto-refresh, frozen/closed/transaction checks, schema callback
  registration, key-path mapping, table/query/result setup, and refresh.
- A portable OHOS adapter does not need native Realm, but it must expose stable
  semantics to the guest Java/Kotlin API:
  - deterministic pseudo-handles with object identity;
  - schema/table/column lookup for `class_BaseCart`, `class_KeyValueStore`,
    and `class_Configuration`;
  - query/result row handles with size/find/get/set behavior;
  - refresh/closed/frozen/transaction answers that do not deadlock or crash;
  - safe callback/noop behavior for schema and notifier hooks.

The current Android phone failure is SIGBUS inside that storage frontier. A
full McD app claim is blocked until the same proof reaches basket commit
without SIGBUS and observes cart/bag state through the app path.

## Two-Day Rally Southbound Scope - 2026-05-02 14:30 PT

The next 48-hour rally must keep Android-phone proof and OHOS portability tied
to the same southbound boundary. The accepted boundary is not "whatever works
on the phone"; it is a Westlake-owned contract that can be implemented on
OHOS/musl.

Required southbound exposures for the current McD frontier:

- **Config/feature flags:** implemented for the observed JustFlip keys through
  portable JSON values. Required keys are `maxQtyOnBasket` and
  `maxItemQuantity`.
- **Realm/storage:** open P0. Required guest-visible behavior includes stable
  pseudo-handles, schema/table lookup, query/result/row identity, scalar get/set
  behavior, refresh/closed/frozen/transaction answers, and callback-safe noops.
- **Input transport:** open for final proof. Required behavior is ordered
  down/move/up packets mapped into the Westlake surface and dispatched through
  Android-shaped `View` hit testing before app-specific fallbacks.
- **Network/data:** still must be portable. Android phone network bridge
  failures may use fixtures only when the app parser/model path consumes the
  data; direct UI seeding is not a southbound success.
- **Lifecycle/observer dispatch:** open quality gate. Fragment/DataBinding and
  LiveData observer semantics must be Android-shaped in the guest and not rely
  on Android host runtime objects.

OHOS implementation expectation:

- Realm does not need native Realm on OHOS. A deterministic in-process store is
  acceptable if it preserves the Java/Kotlin object contract observed by the
  stock app.
- Bionic-specific behavior must stay inside a compatibility capsule or be
  translated to musl/OHOS primitives behind the Westlake southbound API.
- Any future phone-only shortcut must be documented here as an explicit open
  issue before it is used in a proof claim.

## McD Telemetry Boundary - 2026-05-02 14:49 PT

The current stock Add path exposes a new southbound app-service boundary:
McDonald's telemetry must be app-visible as initialized or safely inert.

Observed proof:

- Artifact:
  `artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/`.
- The app reaches `BasketAPIHandler.A1(...)`.
- It then throws `IllegalStateException: Telemetry not initialized` before cart
  mutation or Add LiveData mutation.
- Westlake suppresses duplicate Add re-entry, so the failure is now nonfatal
  and isolated.

Guest contract:

- Calls into McD core telemetry, validation telemetry, telemetry data provider,
  performance analytics, and New Relic wrappers must not throw when the host
  environment has no production telemetry backend.
- Methods returning telemetry publisher/manager/helper objects must return
  app-shaped inert objects when the app expects an object.
- Methods returning validation results, telemetry params, or Rx/coroutine
  wrappers must preserve the caller's success path or return a precise
  nonfatal no-op value.

OHOS adapter expectation:

- OHOS does not need to send McD production telemetry.
- The adapter must expose a deterministic initialized/no-throw telemetry
  service behind Westlake so stock app business logic can continue.
- Any telemetry payloads captured for debugging must be local-only and must not
  require secrets, production credentials, or external analytics services.

## Two-Day Rally Boundary Delta - 2026-05-02 14:55 PT

The latest real-McD proof moved the active boundary from duplicate
Realm/storage crash handling to app-service initialization and cart
continuation. The current accepted baseline is
`artifacts/real-mcd/20260502_144202_mcd_48h_reentry_suppression_retry_gate/`.

New or sharpened southbound surfaces:

- **Telemetry service initialization**
  - Android-facing contract: app code may call McD core telemetry during order
    mutation and must observe an initialized telemetry manager or an inert
    no-throw publisher.
  - Westlake boundary: Westlake may provide a no-op telemetry publisher for
    phone/OHOS proof, but must not let `Telemetry not initialized` abort the
    stock order path.
  - OHOS adapter: no production McD telemetry transport is required for this
    milestone. The adapter must preserve the app-visible initialized/no-throw
    behavior and route any optional telemetry output to a disabled or local
    sink.
  - Proof marker: `MCD_TELEMETRY_MANAGER_SEED`, followed by stock Add
    continuation without `Telemetry_not_initialized`.

- **Cart mutation/storage state**
  - Android-facing contract: Realm/BaseCart-backed cart objects must preserve
    identity and row values across query/result/row handles long enough for the
    stock app to mutate bag count or emit a stock rejection.
  - Westlake boundary: pseudo-native Realm handles must be stable app-visible
    objects, not unrelated placeholder handles.
  - OHOS adapter: persistence can be an OHOS-backed local store, but it must
    implement the same guest-visible Realm table/query/result semantics for
    McD-critical tables.

- **Generic input and lifecycle as portability blockers**
  - Android-facing contract: a complete click is host pointer down/up through
    Android View dispatch, with Fragment lifecycle owners in STARTED/RESUMED.
  - Westlake boundary: projected McD fallback remains a diagnostic bridge only.
  - OHOS adapter: Ability/XComponent input and lifecycle must map to the same
    Android-shaped MotionEvent and AndroidX lifecycle contracts before PF-621
    can close.

## Realm/BaseCart Southbound Delta - 2026-05-02 15:28 PT

The latest normal and unsafe McD proofs split the southbound boundary clearly:
telemetry and current PDP input are no longer the immediate blocker. The
portable storage contract is.

Proof references:

- Normal accepted proof:
  `artifacts/real-mcd/20260502_150027_mcd_48h_telemetry_cart_gate/`.
- Unsafe storage crash map:
  `artifacts/real-mcd/20260502_151742_mcd_48h_true_unsafe_cart_commit_probe/`.

New boundary conclusion:

- McD Add can enter `BasketAPIHandler.A1(...)`, seed telemetry, and mutate Add
  LiveData under Westlake.
- The guest-visible cart remains unchanged because Realm/BaseCart storage does
  not yet implement enough table/query/result/row/transaction behavior.
- When unsafe observer/storage dispatch is enabled to force the stock commit
  path, the child `dalvikvm` SIGBUSes after Realm `class_BaseCart` operations.

Required Westlake southbound API exposure:

- Realm shared realm handle:
  stable identity for `OsSharedRealm.nativeGetSharedRealm(...)`,
  `nativeGetSchemaInfo(...)`, `nativeIsClosed(...)`,
  `nativeIsInTransaction(...)`, `nativeBeginTransaction(...)`, close, refresh,
  and auto-refresh.
- Realm table/query/result handles:
  stable identity and ownership for `OsSharedRealm.nativeGetTableRef(...)`,
  `Table.nativeWhere(...)`, `TableQuery.nativeRawPredicate(...)`,
  `TableQuery.nativeFind(...)`, `TableQuery.nativeValidateQuery(...)`,
  `OsResults.nativeCreateResults(...)`, `OsResults.nativeSize(...)`, and row
  accessors reached from McD generated Realm proxies.
- McD cart model semantics:
  enough deterministic `BaseCart`/`CartInfo`/cart-status state to let the app
  observe either a mutated bag/cart count or its own stock rejection path.
- Transaction semantics:
  begin/commit/cancel/close must be coherent even when backed by an in-process
  OHOS store rather than native Realm.

OHOS portability rule:

- Do not depend on Android bionic or native Realm for this milestone. The OHOS
  adapter may implement a compact Westlake Realm-compatible store, but it must
  preserve the Java/Kotlin guest contract for the observed McD calls.
- A direct native Realm load is acceptable only as a temporary Android-phone
  comparison probe and cannot close the OHOS-portable PF-625 issue.

Proof markers required before this boundary can close:

- unsafe probe: no `Fatal signal 7`, no `realm_sigbus`, observer dispatch can
  run to a stock nonfatal result;
- normal gate: no unsafe flag files, subprocess purity passes, cart/bag state
  mutates or the app emits a precise stock rejection;
- docs: every newly implemented Realm native entrypoint must be listed here
  with the guest-visible behavior and OHOS backing primitive.

## Realm/BaseCart Southbound Delta - 2026-05-02 15:47 PT

The corrected no-observer unsafe proof tightens the southbound boundary:

```text
artifacts/real-mcd/20260502_153758_mcd_48h_model_storage_no_observer_true_probe/
```

Only model/storage unsafe flags were enabled. Observer dispatch stayed disabled.
Therefore, PF-625 is no longer blocked on q7 observer invocation; it is blocked
on guest-visible Realm/BaseCart behavior reached through the stock model commit
path.

New required API exposures:

- **BaseCart row seed**
  - Guest-visible contract: `class_BaseCart` queries can find at least one
    current cart row with stable object identity and deterministic default
    values.
  - OHOS backing: an in-process compact table is acceptable; it must not expose
    host pointers or bionic/Realm-native addresses to guest code.

- **BaseCart query semantics**
  - Guest-visible contract: observed predicates `_maxAge < $0`,
    `_maxAge != $0`, and `cartStatus = $0` must produce deterministic result
    sets and `nativeFind(...)` must return a stable row key when the seeded cart
    matches.
  - OHOS backing: a small predicate evaluator is enough for the observed McD
    subset; unsupported predicates must fail closed with empty results, not
    crash-shaped handles.

- **BaseCart schema coverage**
  - Guest-visible contract: generated Realm proxies can resolve all observed
    BaseCart property column keys, including cart lists and payment/status
    fields.
  - OHOS backing: list columns may initially return empty list handles, but the
    handles must be valid and stable.

- **Row mutation and transaction safety**
  - Guest-visible contract: setters reached by the stock basket path must
    update the seeded row during a coherent transaction. Reads after commit must
    see the same row identity with updated values.
  - OHOS backing: transactions can be process-local with a mutex and rollback
    snapshot. Full durable persistence is not required for this two-day gate.

Southbound success bar for PF-625:

- unsafe model/storage no-observer proof reaches `sigbus=0`;
- the next normal no-unsafe proof is subprocess-pure and nonfatal;
- the app either mutates McD cart/bag state through its own model path or emits
  its own exact stock rejection marker;
- no Android bionic, native Realm binary, or direct phone ART dependency is
  required for the proof.

## Realm/Cart Persistence Delta - 2026-05-02 16:27 PT

Latest proof:

```text
artifacts/real-mcd/20260502_161942_mcd_48h_skip_t2_after_model_x_no_observer_probe/
```

The southbound boundary has moved. `class_BaseCart` active queries no longer
SIGBUS and the real McD guest stays alive after `OrderPDPViewModel.X(...)`.
The remaining southbound gap is read-after-write cart persistence and stock
quantity data.

New required API exposures:

- **Realm link-list persistence**
  - Guest-visible contract: when the app writes `BaseCart.cartProducts` or any
    related `CartProduct` link list during a transaction, later generated Realm
    proxy reads must return the same list handles and row identities.
  - OHOS backing: process-local vectors keyed by `(table,row,column)` are
    enough for this milestone, but they must survive result/row handle churn.

- **Realm read-after-commit**
  - Guest-visible contract: values written through `Table.nativeSet*`,
    `UncheckedRow.nativeSet*`, `OsObjectBuilder`, or `OsList.nativeAddRow`
    must be visible after `OsSharedRealm.nativeCommitTransaction(...)`.
  - OHOS backing: a mutex-protected in-memory store is acceptable; rollback can
    be a snapshot for the observed subset.

- **CartProduct list schema**
  - Guest-visible contract: generated proxies for
    `class_CartProduct` must resolve `components`, `choices`,
    `customizations`, `selectedChoices`, and the observed auxiliary RealmList
    fields to valid empty or populated lists. Null `getChoices()` is no longer
    acceptable.
  - OHOS backing: object-list fields should use target table
    `class_CartProduct`; primitive/object auxiliary lists may start empty but
    must have valid handles.

- **Product stock fields**
  - Guest-visible contract: the PDP product backing an enabled Add button must
    expose a positive `maxQttyAllowedPerOrder` or the app must produce its own
    exact stock rejection. The current proof still sees `maxQtty=0`.
  - OHOS backing: offline deterministic catalog defaults are acceptable until
    live network succeeds, but they must be attached to the real McD model
    object, not rendered as a launcher-side fake.

Current success bar:

- diagnostic unsafe probe: `sigbus=0`, `choicesNullNpe=0`, real guest
  `dalvikvm=1`, and downstream basket commit reached;
- normal gate: unsafe flag files absent, lifecycle RESUMED, and cart/bag state
  mutates or exact stock rejection is emitted;
- network: McD GraphQL/REST must move from status `599` to usable responses
  before this can be called a complete stock-app runtime.

## Realm Link-List Readback Delta - 2026-05-02 16:39 PT

The latest stock PDP Add proof no longer fails on product stock or
CartProduct null lists. The current southbound/native boundary is now list
readback and cart state propagation after a successful Realm write.

Required API exposure:

- **`OsList.nativeCreate`**
  - Guest-visible contract: creating a new list handle for an existing
    `(ownerTable, ownerRow, column)` must rehydrate the rows previously added
    to that owner/column.
  - OHOS backing: process-local storage keyed by `(table,row,column)` is
    sufficient for this milestone. The proof marker must expose
    `list-create ... column=cartProducts ... size=1` after a prior add.

- **`OsList.nativeAddRow` / `nativeInsertRow`**
  - Guest-visible contract: row additions must persist beyond the transient
    list handle and survive generated proxy churn.
  - OHOS backing: update the owner row template's link-list vector on every
    add/insert, while preserving row identity.

- **`OsList.nativeGetRow`**
  - Guest-visible contract: reading from a persisted object list must return a
    valid row handle for the target Realm table, not the raw row id or zero.
  - OHOS backing: create/reuse a row handle for the target table inferred from
    the owner table/column pair. For this McD path,
    `BaseCart.cartProducts -> class_CartProduct`.

- **Cart state propagation after commit**
  - Guest-visible contract: after stock basket commit, app-visible
    `CartInfo`, `CartViewModel`, and generated Realm proxy reads must agree on
    the cart item count or emit a stock app rejection.
  - OHOS backing: if Realm list readback is green but `totalBagCount` remains
    zero, the next southbound gap is not a native crash; it is missing model
    state propagation through stock Java objects.

Candidate runtime carrying this API shape:

```text
$HOME/art-latest/build-bionic-arm64/bin/dalvikvm
sha256: 83aceaf740cab758cd8871cf6e0d02414f5ccebde668d807f41e2126698d629b
```

Do not mark PF-625 complete until a phone artifact proves this runtime is
actually deployed and shows cart mutation/rejection under Westlake.

## Realm Builder Object-List Delta - 2026-05-02 17:00 PT

The `20260502_165132_mcd_48h_basecart_row0_lifecycle_probe` artifact proves
that direct `OsList` readback for `BaseCart.cartProducts` is now working:
the same `class_BaseCart` row `0` is used for active queries and writes, and a
new list handle can rehydrate `size=1`.

The next southbound API exposure is `OsObjectBuilder.nativeAddObjectList(...)`.
Generated Realm proxy code can use this path when it copies a managed
`BaseCart` object during `insertOrUpdate(...)`, so `OsList.nativeAddRow(...)`
alone is not enough.

Required API exposure:

- **`OsObjectBuilder.nativeAddObjectList(builderPtr, columnKey, rowHandles)`**
  - Guest-visible contract: a generated proxy can provide a Java `long[]` of
    Realm row handles for an object-list column; the builder must persist those
    target rows into the destination row's link-list storage when
    `nativeCreateOrUpdate(...)` commits.
  - Required proof marker:
    `PFCUT-REALM-WRITE builder-add method=nativeAddObjectList table=class_BaseCart column=cartProducts listCount=[1-9]`.
  - Required proof marker:
    `PFCUT-REALM-WRITE builder-commit-list table=class_BaseCart row=0 column=cartProducts count=[1-9]`.
  - OHOS backing: decode the primitive `long[]` inside the Westlake runtime,
    normalize row handles into portable row ids, and store the resulting vector
    under `(table,row,column)` in the same mutex-protected Realm state used by
    `OsList`.

If builder object-list persistence is green but the app-visible cart remains
zero, the southbound boundary moves above native Realm storage:

- **Cart projection after storage commit**
  - Guest-visible contract: stock Java `BasketAPIHandler.p2()`,
    `CartInfo.getTotalBagCount()`, and `CartViewModel.getCartInfo()` must agree
    after `OrderPDPViewModel.X(cartProduct)` returns, or the app must emit an
    exact stock rejection.
  - OHOS backing: no native system API should be needed; this is Java model
    propagation over the already portable Realm storage contract.

## CartInfo Projection Delta - 2026-05-02 17:08 PT

The builder-object-list runtime is now deployed and phone-tested. The current
route did not invoke `OsObjectBuilder.nativeAddObjectList(...)`; direct
`OsList` persistence remains the only observed list write and it is green.

The active boundary is now Java model projection over the southbound Realm
store:

- **Stock basket readback API**
  - Guest-visible contract: after `OrderPDPViewModel.X(cartProduct)`, stock
    `BasketAPIHandler.p2()` should be able to project a `CartInfo` from the
    persisted `BaseCart`.
  - Required marker:
    `MCD_PDP_CARTINFO_READBACK route=basket_api_p2 stockTotalBagCount=[0-9]+ stockCartProductQuantity=[0-9]+ vmTotalBagCount=[0-9]+`.
  - Interpretation:
    - `stockTotalBagCount>0`, `vmTotalBagCount=0`: Java singleton/view-model
      propagation gap.
    - `stockTotalBagCount=0` while `BaseCart.cartProducts size=1`: Realm
      generated-proxy read semantics still incomplete inside
      `BasketAPIHandler.x1(BaseCart)`.

- **CartViewModel propagation bridge**
  - Guest-visible contract: if stock `BasketAPIHandler.p2()` produces a
    positive `CartInfo`, the app singleton `CartViewModel` should expose the
    same object or equivalent totals.
  - Temporary bridge accepted only under diagnostic marker:
    `MCD_PDP_CARTINFO_SET_BRIDGE ... applied=true afterVmTotalBagCount=[1-9]`.
  - OHOS backing: this is app-space Java state propagation. It should not add a
    new native/OHOS API; it validates whether the remaining gap is observer
    lifecycle or Realm projection.

Lifecycle note for southbound acceptance:

- The app AndroidX build can expose current lifecycle state through obfuscated
  `LifecycleRegistry.d()` instead of canonical `getCurrentState()`.
- Fragment resumed proof should use app-compatible signals such as
  `mState=7` or `isResumed()==true`; a missing `mResumed` field is not a
  southbound runtime failure by itself.

## Portable HTTP Bridge Delta - 2026-05-02 17:30 PT

The current McD full-app proof has moved the southbound boundary to network.
Local UI/input/lifecycle/cart passes under no-unsafe Westlake, but live McD
GraphQL still fails with status `599` in the phone gate.

Observed root cause in the Android proof harness:

- The guest writes V2 bridge requests under `WESTLAKE_BRIDGE_DIR` as
  `http_requests.log` lines:
  `seq|V2|method|maxBytes|timeoutMs|followRedirects|headersBase64|bodyBase64|url`.
- The host app consumes those files, then calls `HttpURLConnection`.
- The gate advertises `http://127.0.0.1:18080` to the phone and configures
  `adb reverse tcp:18080 tcp:18080`, but previously did not start the local
  proxy process.
- Direct WSL network to the McD endpoint is good; the missing proxy made the
  phone-side bridge fail before it could use WSL networking.

Required southbound API contract:

- **HTTP request execution**
  - Inputs: method, URL, request headers, optional body, timeout, redirect
    policy, and max response bytes.
  - Outputs: status code, response headers, response body, final URL, and
    truncation flag.
  - Android proof adapter: `scripts/westlake-dev-http-proxy.py` plus
    `adb reverse` when the phone should use host/WSL networking.
  - OHOS backing: implement the same contract with OHOS native networking
    APIs, not Android ART networking. The guest should still see only the
    Java/Westlake bridge contract.

- **Proxy lifecycle**
  - The proof harness must start the local proxy automatically when
    `WESTLAKE_HTTP_PROXY_BASE` points at `127.0.0.1:<port>` or
    `localhost:<port>` and no listener exists.
  - Patched script:
    `scripts/run-real-mcd-phone-gate.sh`.
  - Required artifact files for failed network probes:
    `http-proxy.out`, `http-proxy.err`, `logcat-stream.txt`,
    `proof_grep.txt`, and bridge meta/body files under the host app VM bridge
    directory if captured.

Acceptance:

- `network_bridge_or_urlconnection` must pass with a real 2xx response marker:
  `PFCUT-MCD-NET ... status=2xx` or `WestlakeHttp ... code=2xx`.
- Network success cannot bypass subprocess purity or unsafe checks. The full
  McD acceptance bar remains: real guest DalvikVM, no direct phone ART McD
  process, no unsafe flags/markers, lifecycle green, generic input green, and
  cart mutation or stock rejection green.

## Accepted Phone Boundary Update - 2026-05-02 18:10 PT

The bounded full-app phone gate now passes:

```text
artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/
gate_status=PASS
runtime sha256: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
shim sha256: 51ba606bc829ab4cf57c759cc2b65f5f71e51dd8d0bbe304df4602ebe5572fbe
```

Accepted Android-phone southbound surfaces in this artifact:

- **HTTP bridge/proxy**
  - Guest-facing contract: Java app code can issue McD REST/GraphQL/image
    requests and receive real 2xx responses through the Westlake bridge.
  - Android backing: host proxy plus `adb reverse`.
  - OHOS backing requirement: implement the same request/response contract with
    OHOS networking APIs. No phone-only proxy may be counted as OHOS support.

- **Dashboard/PDP resource inflation**
  - Guest-facing contract: compiled McD XML resources, including dashboard
    section shells and item-row layouts, inflate into Westlake View trees.
  - Android backing: shim resource loader and current View/layout subset.
  - OHOS backing requirement: the same resource table, XML parser, drawable,
    text, image, and layout semantics must be exposed above the host surface.

- **Input/frame ownership**
  - Guest-facing contract: host input reaches the current dashboard/PDP frame
    after Westlake frame readiness is published.
  - Android backing: current host touch bridge plus Westlake dispatch markers.
  - OHOS backing requirement: OHOS pointer events must preserve down/up
    ordering, scale, current-frame ownership, and target stability.

- **Realm cart subset**
  - Guest-facing contract: observed Realm table/query/row/list operations for
    `BaseCart` and `CartProduct` persist enough state for stock basket commit
    and positive `CartInfo` readback.
  - Android backing: Westlake runtime in-memory Realm subset.
  - OHOS backing requirement: the same in-memory or persistent store must be
    implemented in the OHOS/musl runtime without relying on Android bionic
    Realm natives.

The accepted phone proof does not close the full OHOS contract. These are the
new required southbound gaps for the two-day rally:

- **Realm close/finalizer stability (`PF-630`)**
  - Trigger: unbounded artifact
    `20260502_174634_mcd_48h_network_pf621_full_app_final` reaches the success
    path and later crashes with `SIGBUS` near
    `OsSharedRealm.nativeCloseSharedRealm`.
  - Guest-facing contract: closing a Realm handle, ending a transaction, or
    running object finalizers must be idempotent and must not invalidate handles
    that generated proxies can still read.
  - OHOS backing: handle ownership, refcounts or generation ids, and cleanup
    must be implemented inside Westlake runtime storage. Do not depend on
    Android signal behavior or bionic alignment accidents.
  - Acceptance: 10-minute post-cart soak with no fatal signal and guest VM pid
    still present.

- **Observer/model propagation without McD bridge (`PF-628`)**
  - Trigger: bounded proof still uses
    `MCD_PDP_CARTINFO_SET_BRIDGE ... applied=true`.
  - Guest-facing contract: app-bundled AndroidX lifecycle and LiveData
    observers must propagate stock `CartInfo` to `CartViewModel` without a
    launcher-side McD-specific bridge.
  - OHOS backing: this is not a new native system service. It must be solved by
    correct lifecycle, main-thread dispatch, and data-binding behavior in the
    Android-shaped northbound layer.
  - Acceptance: positive stock cart readback and `vmTotalBagCount=1` with zero
    `MCD_PDP_CARTINFO_SET_BRIDGE` markers.

- **Dialog/framework API parity (`PF-632`)**
  - Trigger: stock PDP click path reports missing
    `android.app.AlertDialog$Builder(Context,int)`.
  - Guest-facing contract: common framework constructors and listener APIs
    used by stock app dialogs must exist and be nonfatal.
  - OHOS backing: dialogs may initially render through Westlake surfaces or
    use a controlled no-op where visual dialog behavior is not needed, but the
    guest API and callback semantics must be explicit.
  - Acceptance: controlled dialog probe plus real McD rerun without the
    constructor `NoSuchMethodError`.

- **Deeper stock navigation (`PF-629`)**
  - Guest-facing contract: bag/cart screen, edit/remove/re-add, quantity,
    customize, back/bottom navigation, and credential-safe location/auth routes
    must run through stock handlers or fail at named stock blockers.
  - OHOS backing: every new system-facing dependency discovered by these
    routes must be added here before it is counted as portable.
