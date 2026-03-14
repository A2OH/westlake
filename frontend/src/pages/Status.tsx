export default function Status() {
  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      <h1 className="text-2xl font-bold">Project Status</h1>

      {/* Hero banner */}
      <div className="bg-gradient-to-r from-green-900/40 to-blue-900/40 border border-green-700/50 rounded-xl p-6">
        <div className="text-green-400 text-sm font-semibold uppercase tracking-wide mb-1">Architecture: Android-as-Engine</div>
        <div className="text-2xl font-bold mb-2">Run Unmodified Android APKs on OpenHarmony</div>
        <p className="text-gray-300 text-sm">
          Like Flutter on OH — Android framework runs as a self-contained engine.
          Views render via Skia/OH_Drawing to XComponent. Only ~15 system boundaries need bridging.
          94% of the "API gap" is handled by the engine runtime itself.
        </p>
      </div>

      {/* Architecture diagram */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-3">
        <h2 className="text-lg font-semibold">Engine Architecture</h2>
        <div className="bg-gray-800 rounded-lg p-4 font-mono text-xs text-gray-300 overflow-x-auto whitespace-pre">{`┌─────────────────────────────────────────────────────────┐
│                    Android APK (.apk)                   │
│  DEX bytecode  |  Resources  |  NDK .so  |  Manifest   │
└───────────────────────┬─────────────────────────────────┘
                        │
   ┌────────────────────▼────────────────────────────────┐
   │         ANDROID ENGINE (like Flutter Engine)         │
   │                                                      │
   │  ┌────────────────────────────────────────────────┐  │
   │  │  Android Framework (ported from AOSP)          │  │
   │  │  View/Widget/Canvas + Activity/Service/Intent  │  │
   │  │  [runs in Dalvik — NOT shimmed to ArkUI]       │  │
   │  └────────────────────┬───────────────────────────┘  │
   │  ┌────────────────────▼───────────────────────────┐  │
   │  │  MiniServer (~2000 lines Java)                 │  │
   │  │  MiniActivityManager | MiniWindowManager       │  │
   │  │  MiniPackageManager  | MiniContentResolver     │  │
   │  └────────────────────┬───────────────────────────┘  │
   │  ┌────────────────────▼───────────────────────────┐  │
   │  │  Dalvik VM (64-bit, portable C interpreter)    │  │
   │  │  DEX execution | GC | JNI | Class loading      │  │
   │  └────────────────────┬───────────────────────────┘  │
   │  ┌────────────────────▼───────────────────────────┐  │
   │  │  Platform Bridges (~15 boundaries)             │  │
   │  │  Canvas→OH_Drawing | Audio→OH Audio | Input    │  │
   │  │  Camera | Network | Location | Bluetooth       │  │
   │  └────────────────────┬───────────────────────────┘  │
   └────────────────────────┼────────────────────────────┘
                            │
   ┌────────────────────────▼────────────────────────────┐
   │              OpenHarmony OS                          │
   │  XComponent | OH_Drawing | OpenGL ES | System APIs  │
   └──────────────────────────────────────────────────────┘`}</div>
      </div>

      {/* Workstream progress */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-4">
        <h2 className="text-lg font-semibold">Execution Plan: 4 Workstreams</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <WsCard
            id="WS1"
            title="Canvas Rendering Bridge"
            status="not-started"
            description="Canvas.draw*() → OH_Drawing_Canvas NDK. ~30 JNI methods for rect, text, path, bitmap, transforms, clip."
            tasks={[
              { text: 'OH_Drawing native methods in OHBridge.java', done: false },
              { text: 'Rust bridge (oh_drawing.rs)', done: false },
              { text: 'Wire Canvas.java → OHBridge drawing calls', done: false },
              { text: 'Wire Paint.java → OH_Drawing_Pen/Brush', done: false },
              { text: 'Surface/XComponent integration', done: false },
              { text: 'Test: render shapes + text to bitmap', done: false },
            ]}
          />
          <WsCard
            id="WS2"
            title="MiniServer"
            status="not-started"
            description="Replace SystemServer with ~2000-line Java class. Activity stack, lifecycle dispatch, View tree, manifest resolution."
            tasks={[
              { text: 'MiniActivityManager: stack + lifecycle', done: false },
              { text: 'Wire Activity.startActivity() routing', done: false },
              { text: 'Wire Activity.setContentView() → View tree', done: false },
              { text: 'Wire Context.getSystemService()', done: false },
              { text: 'MiniPackageManager: manifest + resolution', done: false },
              { text: 'Test: headless lifecycle round-trip', done: false },
            ]}
          />
          <WsCard
            id="WS3"
            title="APK Loader"
            status="not-started"
            description="Load real .apk files: unzip, parse binary AndroidManifest.xml, multi-DEX class loading."
            tasks={[
              { text: 'APK unzip (ZIP extraction)', done: false },
              { text: 'Binary AndroidManifest.xml parser', done: false },
              { text: 'Multi-DEX class loading in Dalvik', done: false },
              { text: 'Resource table parser (basic)', done: false },
              { text: 'Test: load APK, print manifest activities', done: false },
            ]}
          />
          <WsCard
            id="WS4"
            title="Input Bridge"
            status="not-started"
            description="OH touch/key events → Android MotionEvent/KeyEvent → View.dispatchTouchEvent()."
            tasks={[
              { text: 'MotionEvent implementation', done: false },
              { text: 'KeyEvent implementation', done: false },
              { text: 'XComponent touch → View tree dispatch', done: false },
              { text: 'Back button → Activity.onBackPressed()', done: false },
              { text: 'Test: touch reaches View.onTouchEvent()', done: false },
            ]}
          />
        </div>
      </div>

      {/* Milestones */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-3">
        <h2 className="text-lg font-semibold">Milestones</h2>
        <div className="space-y-3">
          <MilestoneItem id="M1" title="Headless Activity Lifecycle" target="2 weeks" status="pending"
            description="MiniActivityManager manages stack. startActivity/finish works. startActivityForResult round-trip." />
          <MilestoneItem id="M2" title="Canvas Rendering on Linux" target="3 weeks" status="pending"
            description="Canvas → OH_Drawing bridge. Draw rectangles, text, circles to bitmap. Save as PNG." />
          <MilestoneItem id="M3" title="APK Loading" target="4 weeks" status="pending"
            description="Load real .apk, parse binary manifest, multi-DEX class loading. Activity lifecycle runs headlessly." />
          <MilestoneItem id="M4" title="Hello Android Demo" target="6 weeks" status="pending"
            description="Simple APK with Button + TextView. Loads, renders, responds to touch on OHOS aarch64." />
        </div>
      </div>

      {/* Platform cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <ArchCard arch="x86_64" label="Linux Native" status="running"
          details="Dalvik VM + Activity lifecycle. Headless testing." />
        <ArchCard arch="aarch64" label="OpenHarmony" status="running"
          details="Static binary via QEMU. Dalvik executes DEX." />
        <ArchCard arch="ARM32" label="OpenHarmony" status="running"
          details="EABI native JNI call bridge. Dalvik + app lifecycle." />
      </div>

      {/* Key numbers */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        <NumCard label="Shim Classes" value="1,968" sub="100% clean compile" />
        <NumCard label="Tests Passing" value="535" sub="headless + UI" />
        <NumCard label="Android APIs" value="57,289" sub="in api_compat.db" />
        <NumCard label="JNI Bridge Methods" value="106" sub="Rust + C++ bridge" />
        <NumCard label="Platform Bridges" value="~15" sub="system boundaries" />
      </div>

      {/* Dalvik VM */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-4">
        <h2 className="text-lg font-semibold">Dalvik VM Port</h2>
        <p className="text-gray-400 text-sm">
          KitKat-era Dalvik VM ported to 64-bit. First-ever 64-bit Dalvik —
          Android switched to ART before shipping 64-bit. Custom <code className="text-blue-400">dreg_t</code> (= uintptr_t)
          replaces u4 register slots. 50+ fixes for 64-bit correctness.
        </p>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <CheckItem text="VM boots and loads ~4,000 core classes" />
          <CheckItem text="Activity lifecycle (onCreate → onDestroy)" />
          <CheckItem text="Concurrent GC with 64-bit bitmap ops" />
          <CheckItem text="JNI call bridge (x86_64 FFI + ARM32 EABI assembly)" />
          <CheckItem text="Daemon threads (Finalizer, GC, ReferenceQueue)" />
          <CheckItem text="Libcore bridge (System, Posix, ICU, OsConstants)" />
          <CheckItem text="dexopt child process verification" />
          <CheckItem text="Static linking for OHOS (musl libc)" />
        </div>
      </div>

      {/* JNI Bridge */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-4">
        <h2 className="text-lg font-semibold">JNI Bridge (Java → Rust → C++ → OH)</h2>
        <p className="text-gray-400 text-sm">
          106 native methods bridging Android shims to OpenHarmony APIs via a three-layer
          JNI architecture: Java → Rust (jni-rs) → C++ wrapper → OH NDK/inner APIs.
        </p>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-sm">
          <BridgeCard name="Preferences" count={14} status="wired" />
          <BridgeCard name="RdbStore/SQLite" count={8} status="wired" />
          <BridgeCard name="ArkUI Nodes" count={17} status="wired" />
          <BridgeCard name="MediaPlayer" count={11} status="wired" />
          <BridgeCard name="Logging" count={4} status="wired" />
          <BridgeCard name="Network/HTTP" count={3} status="wired" />
          <BridgeCard name="DeviceInfo" count={4} status="wired" />
          <BridgeCard name="Audio" count={4} status="wired" />
          <BridgeCard name="Notification" count={3} status="wired" />
          <BridgeCard name="Location" count={2} status="wired" />
          <BridgeCard name="Telephony" count={6} status="wired" />
          <BridgeCard name="WiFi" count={6} status="wired" />
          <BridgeCard name="Canvas/Drawing" count={0} status="planned" />
          <BridgeCard name="Input Events" count={0} status="planned" />
          <BridgeCard name="Camera" count={0} status="planned" />
          <BridgeCard name="Sensors" count={0} status="planned" />
        </div>
      </div>

      {/* Shim Layer */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-4">
        <h2 className="text-lg font-semibold">Java Shim Layer</h2>
        <p className="text-gray-400 text-sm">
          1,968 Android API stub classes. ~50 have real implementations (Bundle, Intent,
          SharedPreferences, SQLiteDatabase, Notification, ContentProvider, etc.).
          Serves as compile target and runtime fallback for classes where AOSP source is too complex.
        </p>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div className="bg-gray-800 rounded-lg p-3 text-sm">
            <div className="text-gray-500 text-xs mb-2">Implemented (real logic)</div>
            <div className="text-xs text-gray-400 space-y-0.5">
              <div>Bundle, Intent, ContentValues, ComponentName, Uri</div>
              <div>SharedPreferences, SQLiteDatabase, SQLiteOpenHelper</div>
              <div>Notification, JobInfo, Settings, ContentProvider</div>
              <div>Log, SparseArray, ArrayMap, LruCache, Base64</div>
              <div>Color, Rect, RectF, Point, PointF, Matrix</div>
              <div>TextUtils, SpannableString, SpannableStringBuilder</div>
            </div>
          </div>
          <div className="bg-gray-800 rounded-lg p-3 text-sm">
            <div className="text-gray-500 text-xs mb-2">Tier Breakdown (from DB)</div>
            <div className="space-y-1">
              <TierBar label="A: Pure Java" count={314} total={5318} color="bg-blue-500" />
              <TierBar label="B: I/O Fallback" count={946} total={5318} color="bg-yellow-500" />
              <TierBar label="C: System (OHBridge)" count={3445} total={5318} color="bg-orange-500" />
              <TierBar label="D: UI (ArkUI)" count={613} total={5318} color="bg-red-500" />
            </div>
          </div>
        </div>
      </div>

      {/* APK Analysis */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-3">
        <h2 className="text-lg font-semibold">Real APK Analysis (13 Apps)</h2>
        <p className="text-gray-400 text-sm">
          Analyzed 13 top apps (2.3B+ MAU). Key finding: 94% of the "unmapped API gap"
          is handled by the engine runtime. Only 6% needs real bridge work. DRM is 0.1%.
        </p>
        <div className="overflow-x-auto">
          <table className="w-full text-xs">
            <thead>
              <tr className="text-gray-500 border-b border-gray-800">
                <th className="py-1 px-2 text-left">App</th>
                <th className="py-1 px-2 text-right">APIs Used</th>
                <th className="py-1 px-2 text-right">Engine Covers</th>
                <th className="py-1 px-2 text-left">Key Bridges</th>
              </tr>
            </thead>
            <tbody className="text-gray-400">
              <AppRow app="PayPal" apis="516" coverage="32.8%" bridges="Network, Security, NFC" />
              <AppRow app="Facebook" apis="3,669" coverage="30.0%" bridges="WebView, Network" />
              <AppRow app="TikTok" apis="18,225" coverage="17.2%" bridges="Camera, Media, Sensors" />
              <AppRow app="Instagram" apis="18,531" coverage="17.0%" bridges="Camera, Media, Location" />
              <AppRow app="Uber" apis="1,384" coverage="13.5%" bridges="Location, Network" />
              <AppRow app="Netflix" apis="22,988" coverage="11.1%" bridges="Media, DRM(!)" />
              <AppRow app="Spotify" apis="23,496" coverage="10.7%" bridges="Audio, Bluetooth" />
              <AppRow app="YouTube" apis="26,957" coverage="9.9%" bridges="Media, DRM(!)" />
              <AppRow app="Google Maps" apis="31,838" coverage="8.9%" bridges="Location, Sensors" />
              <AppRow app="Amazon" apis="27,576" coverage="7.1%" bridges="WebView, Network" />
              <AppRow app="Duolingo" apis="76,997" coverage="3.5%" bridges="Audio, Notifications" />
              <AppRow app="Grab" apis="111,675" coverage="3.0%" bridges="Location, Network" />
              <AppRow app="Zoom" apis="160,519" coverage="1.9%" bridges="Camera, Audio, Network" />
            </tbody>
          </table>
        </div>
      </div>

      {/* Gap decomposition */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800 space-y-3">
        <h2 className="text-lg font-semibold">Tier 4 Gap Decomposition</h2>
        <p className="text-gray-400 text-sm mb-3">
          The headline "67-83% unmapped" is misleading. With the engine architecture:
        </p>
        <div className="space-y-2">
          <GapBar label="Noise (proprietary libs, AndroidX)" pct={70.1} color="bg-green-500" action="Engine runtime" />
          <GapBar label="UI (View/Widget/Animation)" pct={11.7} color="bg-green-500" action="Engine View pipeline" />
          <GapBar label="OH has capability (DB mapping gap)" pct={11.8} color="bg-yellow-500" action="Platform bridges" />
          <GapBar label="Java runtime (java.*/javax.*)" pct={6.3} color="bg-green-500" action="Dalvik runtime" />
          <GapBar label="True platform gap (DRM)" pct={0.1} color="bg-red-500" action="Container fallback" />
        </div>
      </div>
    </div>
  );
}

// ─── Sub-components ─────────────────────────────────────────────────────────

function ArchCard({ arch, label, status, details }: { arch: string; label: string; status: string; details: string }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
      <div className="flex items-center gap-2 mb-2">
        <span className={`inline-block w-2 h-2 rounded-full ${status === 'running' ? 'bg-green-400' : 'bg-yellow-400'}`} />
        <span className="font-semibold">{arch}</span>
        <span className="text-gray-500 text-sm">({label})</span>
      </div>
      <div className="text-sm text-gray-400">{details}</div>
    </div>
  );
}

function NumCard({ label, value, sub }: { label: string; value: string; sub: string }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
      <div className="text-xs text-gray-500 uppercase tracking-wide">{label}</div>
      <div className="text-2xl font-bold mt-1">{value}</div>
      <div className="text-xs text-gray-500 mt-0.5">{sub}</div>
    </div>
  );
}

function CheckItem({ text }: { text: string }) {
  return (
    <div className="flex items-center gap-2 text-sm text-gray-300">
      <span className="text-green-400 shrink-0">+</span>
      <span>{text}</span>
    </div>
  );
}

function WsCard({ id, title, status, description, tasks }: {
  id: string; title: string; status: string; description: string;
  tasks: { text: string; done: boolean }[];
}) {
  const done = tasks.filter(t => t.done).length;
  const statusColor = status === 'done' ? 'bg-green-500' : status === 'in-progress' ? 'bg-yellow-500 animate-pulse' : 'bg-gray-600';
  const statusText = status === 'done' ? 'Complete' : status === 'in-progress' ? 'In Progress' : 'Not Started';
  return (
    <div className="bg-gray-800 rounded-xl p-4 space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-xs font-mono text-blue-400 bg-blue-900/40 px-2 py-0.5 rounded">{id}</span>
          <span className="font-semibold text-sm">{title}</span>
        </div>
        <span className={`inline-flex items-center gap-1 text-xs px-2 py-0.5 rounded ${statusColor} text-white`}>
          {statusText}
        </span>
      </div>
      <p className="text-xs text-gray-400">{description}</p>
      <div className="space-y-1">
        {tasks.map((t, i) => (
          <div key={i} className="flex items-center gap-2 text-xs">
            <span className={t.done ? 'text-green-400' : 'text-gray-600'}>{t.done ? '+' : '-'}</span>
            <span className={t.done ? 'text-gray-400 line-through' : 'text-gray-300'}>{t.text}</span>
          </div>
        ))}
      </div>
      <div className="h-1.5 bg-gray-700 rounded-full overflow-hidden">
        <div className="h-full bg-green-500 transition-all" style={{ width: `${tasks.length > 0 ? (done / tasks.length) * 100 : 0}%` }} />
      </div>
    </div>
  );
}

function MilestoneItem({ id, title, target, status, description }: {
  id: string; title: string; target: string; status: string; description: string;
}) {
  const dot = status === 'done' ? 'bg-green-400' : status === 'in-progress' ? 'bg-yellow-400 animate-pulse' : 'bg-gray-600';
  return (
    <div className="flex items-start gap-3">
      <span className={`mt-1.5 w-2.5 h-2.5 rounded-full shrink-0 ${dot}`} />
      <div>
        <div className="flex items-center gap-2">
          <span className="text-xs font-mono text-purple-400 bg-purple-900/40 px-1.5 py-0.5 rounded">{id}</span>
          <span className="font-semibold text-sm">{title}</span>
          <span className="text-xs text-gray-500">{target}</span>
        </div>
        <p className="text-xs text-gray-400 mt-0.5">{description}</p>
      </div>
    </div>
  );
}

function BridgeCard({ name, count, status }: { name: string; count: number; status: string }) {
  return (
    <div className={`rounded-lg p-2 ${status === 'wired' ? 'bg-green-900/20 border border-green-800/50' : 'bg-gray-800 border border-gray-700/50'}`}>
      <div className="flex items-center justify-between">
        <span className="text-xs">{name}</span>
        <span className={`text-xs font-mono ${status === 'wired' ? 'text-green-400' : 'text-gray-500'}`}>
          {count > 0 ? count : '--'}
        </span>
      </div>
    </div>
  );
}

function TierBar({ label, count, total, color }: { label: string; count: number; total: number; color: string }) {
  return (
    <div className="flex items-center gap-2 text-xs">
      <span className="text-gray-400 w-36">{label}</span>
      <div className="flex-1 h-2 bg-gray-700 rounded-full overflow-hidden">
        <div className={`h-full ${color} rounded-full`} style={{ width: `${(count / total) * 100}%` }} />
      </div>
      <span className="text-gray-500 w-12 text-right">{count}</span>
    </div>
  );
}

function GapBar({ label, pct, color, action }: { label: string; pct: number; color: string; action: string }) {
  return (
    <div className="flex items-center gap-3 text-xs">
      <div className="flex-1">
        <div className="flex justify-between mb-0.5">
          <span className="text-gray-300">{label}</span>
          <span className="text-gray-500">{pct}%</span>
        </div>
        <div className="h-2 bg-gray-800 rounded-full overflow-hidden">
          <div className={`h-full ${color} rounded-full`} style={{ width: `${pct}%` }} />
        </div>
      </div>
      <span className="text-gray-500 w-28 text-right">{action}</span>
    </div>
  );
}

function AppRow({ app, apis, coverage, bridges }: { app: string; apis: string; coverage: string; bridges: string }) {
  return (
    <tr className="border-b border-gray-800/50">
      <td className="py-1.5 px-2">{app}</td>
      <td className="py-1.5 px-2 text-right font-mono">{apis}</td>
      <td className="py-1.5 px-2 text-right font-mono">{coverage}</td>
      <td className="py-1.5 px-2">{bridges}</td>
    </tr>
  );
}
