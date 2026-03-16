# WS3: APK Loader — End-to-End APK Execution Pipeline

## Mission

Complete the APK loading pipeline so a real `.apk` file can be loaded, its resources partially resolved, its native libraries extracted, and its classes loaded across multiple DEX files — enabling the "demo moment" where a real APK runs on the engine.

## Current State (as of ad8e25c)

### Already Implemented (DO NOT REWRITE)

| Component | File | What It Does |
|-----------|------|-------------|
| ApkLoader | `shim/java/android/app/ApkLoader.java` | ZIP extraction, DEX discovery, `load(apkPath)` → ApkInfo |
| BinaryXmlParser | `shim/java/android/app/BinaryXmlParser.java` | AXML manifest parser (activities, services, permissions, launcher, app class) |
| ApkInfo | `shim/java/android/app/ApkInfo.java` | Data class: packageName, activities, services, dexPaths, launcherActivity, applicationClassName |
| MiniServer.loadApk() | `shim/java/android/app/MiniServer.java` | Orchestrator: parse → register components → instantiate Application → launch |
| MiniServer.loadAndLaunch() | `shim/java/android/app/MiniServer.java` | Convenience: loadApk + start launcher activity |
| Handler/Looper/MessageQueue | `shim/java/android/os/` | Queue-based message pump with time-ordered delivery |
| Resources (stub) | `shim/java/android/content/res/Resources.java` | Returns defaults (density=2.0, 1080x1920), no real resource loading |
| ContentResolver wiring | `shim/java/android/content/ContentResolver.java` | Routes query/insert/update/delete to ContentProviders |
| AssetManager (stub) | `shim/java/android/content/res/AssetManager.java` | Throws IOException on open(), returns empty array on list() |
| DexClassLoader (stub) | `shim/java/dalvik/system/DexClassLoader.java` | Empty constructor, no loadClass() logic |

### Test Baseline: 860 pass, 0 failures

### What's Missing

The core pipeline works for **headless testing** but not for **real APK execution**:

1. **resources.arsc parser** — apps crash on `R.string.*`, `R.layout.*`, `R.drawable.*` references
2. **Asset extraction** — apps crash on `getAssets().open("config.json")`
3. **Native library (.so) extraction** — apps with JNI crash on `System.loadLibrary()`
4. **DexClassLoader/PathClassLoader** — multi-DEX runtime loading (dynamic feature modules, plugins)
5. **On-device Dalvik integration** — verify classpath string → Dalvik actually loads all DEX files
6. **Resource-backed View inflation** — `setContentView(R.layout.activity_main)` returns null

---

## Task 1: resources.arsc Parser (ResourceTable)

**Goal:** Parse the binary `resources.arsc` file from APK to resolve `R.string.*`, `R.color.*`, `R.dimen.*`, and `R.layout.*` constants into usable values.

**Why:** ~90% of real apps reference resources via `R.` constants. Without this, `getString(R.string.app_name)` returns `"string_2131034113"` instead of `"My App"`.

**File:** `shim/java/android/content/res/ResourceTable.java` (NEW)

### Background: resources.arsc Format

The `resources.arsc` file is a binary format containing:
- **String pool** — all resource string values
- **Package chunks** — one per package (usually just one)
- **Type spec chunks** — resource type definitions (string, layout, drawable, etc.)
- **Type chunks** — actual resource entries mapping ID → value

Resource IDs follow the format `0xPPTTEEEE`:
- `PP` = package ID (0x7f for app resources)
- `TT` = type ID (01=attr, 02=drawable, 03=layout, 04=string, 05=color, 06=dimen, etc.)
- `EEEE` = entry index within the type

### Implementation

```java
package android.content.res;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses resources.arsc binary format and resolves resource IDs to values.
 *
 * Supports: strings, colors, dimensions, integers, booleans.
 * Does NOT support: layouts (XML inflation), drawables (images), styles.
 */
public class ResourceTable {
    // Resource ID → string value (for string, layout filenames, etc.)
    private final Map<Integer, String> mStrings = new HashMap<>();
    // Resource ID → integer value (for colors, dimensions, integers, booleans)
    private final Map<Integer, Integer> mIntegers = new HashMap<>();

    // String pool from resources.arsc
    private String[] mGlobalStrings;

    // Chunk type constants (from ResourceTypes.h)
    private static final int RES_STRING_POOL_TYPE = 0x0001;
    private static final int RES_TABLE_TYPE = 0x0002;
    private static final int RES_TABLE_PACKAGE_TYPE = 0x0200;
    private static final int RES_TABLE_TYPE_SPEC_TYPE = 0x0202;
    private static final int RES_TABLE_TYPE_TYPE = 0x0201;

    /** Parse a resources.arsc byte stream. */
    public void parse(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        // Read table header
        int tableType = buf.getShort() & 0xFFFF;    // RES_TABLE_TYPE
        int headerSize = buf.getShort() & 0xFFFF;
        int totalSize = buf.getInt();
        int packageCount = buf.getInt();

        // Parse chunks sequentially
        while (buf.position() < data.length) {
            int chunkStart = buf.position();
            if (buf.remaining() < 8) break;
            int chunkType = buf.getShort() & 0xFFFF;
            int chunkHeaderSize = buf.getShort() & 0xFFFF;
            int chunkSize = buf.getInt();
            if (chunkSize < 8) break;

            switch (chunkType) {
                case RES_STRING_POOL_TYPE:
                    if (mGlobalStrings == null) {
                        mGlobalStrings = parseStringPool(buf, chunkStart, chunkSize);
                    }
                    break;
                case RES_TABLE_PACKAGE_TYPE:
                    parsePackage(buf, chunkStart, chunkHeaderSize, chunkSize);
                    break;
            }
            buf.position(chunkStart + chunkSize);
        }
    }

    // parseStringPool, parsePackage, parseTypeChunk — implement per ResourceTypes.h spec

    /** Look up a string resource by ID. Returns null if not found. */
    public String getString(int resId) {
        return mStrings.get(resId);
    }

    /** Look up an integer/color/boolean resource by ID. Returns defValue if not found. */
    public int getInteger(int resId, int defValue) {
        Integer v = mIntegers.get(resId);
        return v != null ? v : defValue;
    }

    /** Check if a resource ID is known. */
    public boolean hasResource(int resId) {
        return mStrings.containsKey(resId) || mIntegers.containsKey(resId);
    }
}
```

### Wire to Resources.java

Modify `shim/java/android/content/res/Resources.java`:

```java
private ResourceTable mTable;  // loaded from resources.arsc

public void loadResourceTable(ResourceTable table) {
    mTable = table;
}

@Override
public String getString(int id) {
    if (mTable != null) {
        String s = mTable.getString(id);
        if (s != null) return s;
    }
    return "string_" + id;  // fallback
}

@Override
public int getColor(int id) {
    if (mTable != null) {
        return mTable.getInteger(id, 0xFF000000);
    }
    return 0xFF000000;
}
```

### Wire to ApkLoader

Modify `shim/java/android/app/ApkLoader.java` — extract `resources.arsc` and parse it:

```java
// In load() method, after extracting DEX files:
ZipEntry resEntry = zip.getEntry("resources.arsc");
if (resEntry != null) {
    try (InputStream in = zip.getInputStream(resEntry)) {
        byte[] resData = readAllBytes(in);
        ResourceTable table = new ResourceTable();
        table.parse(resData);
        info.resourceTable = table;
    }
}
```

Add to `ApkInfo.java`:
```java
public transient ResourceTable resourceTable;  // not serializable
```

### Reference

The `resources.arsc` format is documented in AOSP `frameworks/base/include/androidfw/ResourceTypes.h`. Key structures:
- `ResTable_header` — file header
- `ResStringPool_header` — string pool
- `ResTable_package` — package chunk
- `ResTable_typeSpec` — type spec
- `ResTable_type` — type chunk with entries
- `ResTable_entry` — individual resource entry
- `Res_value` — typed value (string ref, int, color, dimension, etc.)

### Tests

```java
static void testResourceTable() {
    section("ResourceTable");

    // Test 1: Parse a hand-crafted resources.arsc
    // (Build a minimal binary resources.arsc with known string + color entries)

    // Test 2: Round-trip resource lookup
    ResourceTable table = new ResourceTable();
    // ... parse test data ...
    // check("getString returns app_name", "My App".equals(table.getString(0x7f040001)));
    // check("getColor returns red", table.getInteger(0x7f050001, 0) == 0xFFFF0000);

    // Test 3: Resources fallback
    android.content.res.Resources res = new android.content.res.Resources();
    check("getString without table returns fallback", res.getString(999).startsWith("string_"));
}
```

**Complexity:** LARGE — binary format parsing is tricky. Expect ~400-600 lines.
**Priority:** HIGH — blocks most real apps.

---

## Task 2: Asset Extraction and Loading

**Goal:** `context.getAssets().open("fonts/custom.ttf")` returns an InputStream reading from the APK's `assets/` directory.

**Why:** Many apps bundle configuration files, fonts, HTML, ML models in `assets/`. Without this, apps crash with IOException.

**File:** Modify `shim/java/android/content/res/AssetManager.java`

### Implementation

```java
package android.content.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssetManager {
    private File mAssetDir;  // extracted assets root directory

    /** Set the root directory where APK assets were extracted. */
    public void setAssetDir(String path) {
        mAssetDir = new File(path);
    }

    public InputStream open(String fileName) throws IOException {
        if (mAssetDir != null) {
            File f = new File(mAssetDir, fileName);
            if (f.exists() && f.isFile()) {
                return new FileInputStream(f);
            }
        }
        throw new IOException("Asset not found: " + fileName);
    }

    public InputStream open(String fileName, int accessMode) throws IOException {
        return open(fileName);  // ignore accessMode in engine
    }

    public String[] list(String path) throws IOException {
        if (mAssetDir != null) {
            File dir = new File(mAssetDir, path);
            if (dir.exists() && dir.isDirectory()) {
                String[] names = dir.list();
                return names != null ? names : new String[0];
            }
        }
        return new String[0];
    }

    public void close() {
        // no-op — assets stay extracted for app lifetime
    }
}
```

### Wire to ApkLoader

Modify `shim/java/android/app/ApkLoader.java` — extract `assets/` directory:

```java
// In load(), after DEX extraction:
Enumeration<? extends ZipEntry> entries2 = zip.entries();
while (entries2.hasMoreElements()) {
    ZipEntry entry = entries2.nextElement();
    String name = entry.getName();
    if (name.startsWith("assets/") && !entry.isDirectory()) {
        File assetOut = new File(extractDir, name);
        extractEntry(zip, entry, assetOut);
    }
}
info.assetDir = new File(extractDir, "assets").getAbsolutePath();
```

Add to `ApkInfo.java`:
```java
public String assetDir;  // path to extracted assets/ directory
```

### Wire to Context

In `MiniServer.loadApk()`, after loading:
```java
AssetManager am = mApplication.getAssets();
if (am != null && info.assetDir != null) {
    am.setAssetDir(info.assetDir);
}
```

### Tests

```java
static void testAssetManager() {
    section("AssetManager");

    // Create a temp directory with test assets
    File tmpDir = new File(System.getProperty("java.io.tmpdir"), "test-assets-" + System.currentTimeMillis());
    File assetsDir = new File(tmpDir, "assets");
    File subDir = new File(assetsDir, "fonts");
    subDir.mkdirs();

    // Write a test file
    try {
        java.io.FileWriter fw = new java.io.FileWriter(new File(subDir, "test.txt"));
        fw.write("hello assets");
        fw.close();
    } catch (Exception e) { /* skip */ }

    AssetManager am = new AssetManager();
    am.setAssetDir(assetsDir.getAbsolutePath());

    // Test open
    try {
        InputStream in = am.open("fonts/test.txt");
        byte[] buf = new byte[100];
        int n = in.read(buf);
        in.close();
        check("open reads file", new String(buf, 0, n).equals("hello assets"));
    } catch (Exception e) {
        check("open reads file", false);
    }

    // Test list
    try {
        String[] files = am.list("fonts");
        check("list returns files", files.length > 0);
    } catch (Exception e) {
        check("list returns files", false);
    }

    // Test missing file
    try {
        am.open("nonexistent.txt");
        check("missing file throws", false);
    } catch (IOException e) {
        check("missing file throws", true);
    }

    // Cleanup
    new File(subDir, "test.txt").delete();
    subDir.delete();
    assetsDir.delete();
    tmpDir.delete();
}
```

**Complexity:** SMALL — straightforward file I/O.
**Priority:** MEDIUM — blocks apps with bundled config/fonts but not all apps.

---

## Task 3: Native Library (.so) Extraction

**Goal:** Extract `lib/<abi>/*.so` files from APK so `System.loadLibrary("foo")` can find `libfoo.so`.

**Why:** Apps with JNI (camera SDKs, crypto, ML inference) will crash without this. TikTok, Instagram, Zoom all use native code extensively.

**File:** Modify `shim/java/android/app/ApkLoader.java`

### Implementation

APKs contain native libraries in `lib/<abi>/` directories:
```
lib/arm64-v8a/libfoo.so
lib/armeabi-v7a/libfoo.so
lib/x86_64/libfoo.so
```

The loader should:
1. Detect host ABI (from system property or environment)
2. Extract matching `lib/<abi>/*.so` files to a known directory
3. Set `java.library.path` or provide a path for `Runtime.loadLibrary()`

```java
// In ApkLoader.load(), after asset extraction:

// Determine target ABI
String abi = System.getProperty("os.arch", "");
String abiDir;
if (abi.contains("aarch64") || abi.contains("arm64")) {
    abiDir = "lib/arm64-v8a/";
} else if (abi.contains("arm")) {
    abiDir = "lib/armeabi-v7a/";
} else if (abi.contains("amd64") || abi.contains("x86_64")) {
    abiDir = "lib/x86_64/";
} else {
    abiDir = "lib/x86/";
}

// Extract matching .so files
File nativeDir = new File(extractDir, "native-libs");
Enumeration<? extends ZipEntry> entries3 = zip.entries();
while (entries3.hasMoreElements()) {
    ZipEntry entry = entries3.nextElement();
    String name = entry.getName();
    if (name.startsWith(abiDir) && name.endsWith(".so") && !entry.isDirectory()) {
        String soName = name.substring(name.lastIndexOf('/') + 1);
        File soOut = new File(nativeDir, soName);
        extractEntry(zip, entry, soOut);
        info.nativeLibPaths.add(soOut.getAbsolutePath());
    }
}
info.nativeLibDir = nativeDir.getAbsolutePath();
```

Add to `ApkInfo.java`:
```java
public String nativeLibDir;
public final List<String> nativeLibPaths = new ArrayList<>();
```

### Wire System.loadLibrary()

This requires bridging to the actual runtime's library loading mechanism. For now, store the path so Dalvik's `Runtime.loadLibrary0()` can use it:

```java
// In MiniServer.loadApk(), after loading:
if (info.nativeLibDir != null) {
    // Set system property so Runtime.loadLibrary can find libs
    System.setProperty("app.native.lib.dir", info.nativeLibDir);
}
```

**Note:** Full `System.loadLibrary()` integration depends on Dalvik's `Runtime.nativeLoad()` which loads `.so` via `dlopen()`. The extraction step here ensures the files are on disk; the Dalvik VM handles the actual loading.

### Tests

```java
static void testNativeLibExtraction() {
    section("Native lib extraction");

    // Create a test APK-like ZIP with lib/ entries
    File tmpZip = createTestApk(new String[]{"lib/x86_64/libtest.so"}, new byte[][]{new byte[]{1,2,3}});

    try {
        ApkInfo info = ApkLoader.load(tmpZip.getAbsolutePath());
        // On x86_64 host, should extract lib/x86_64/libtest.so
        String arch = System.getProperty("os.arch", "");
        if (arch.contains("amd64") || arch.contains("x86_64")) {
            check("native lib extracted", info.nativeLibPaths.size() > 0);
            check("nativeLibDir set", info.nativeLibDir != null);
        } else {
            check("no matching ABI", info.nativeLibPaths.size() == 0);
        }
    } catch (Exception e) {
        check("native lib extraction", false);
    }
}
```

**Complexity:** SMALL-MEDIUM — extraction is easy, but `System.loadLibrary()` bridging is complex.
**Priority:** HIGH for real apps, LOW for headless demos.

---

## Task 4: DexClassLoader / PathClassLoader Implementation

**Goal:** `new DexClassLoader(dexPath, ..., parent).loadClass("com.foo.Bar")` works for runtime class loading (plugins, dynamic features, multidex compat library).

**Why:** Apps using the Android multidex support library, dynamic feature modules, or plugin architectures create ClassLoaders at runtime. Without this, `Class.forName()` works (uses boot classpath) but `DexClassLoader.loadClass()` does not.

### Background

Android has three ClassLoader types:
- **PathClassLoader** — loads from installed APK's DEX files (system default)
- **DexClassLoader** — loads from arbitrary DEX/JAR paths (plugins, downloads)
- **BaseDexClassLoader** — common base with `DexPathList` for multi-path loading

In the engine model, since we run on Dalvik, the real class loading happens in Dalvik's C++ runtime. But apps that create `DexClassLoader` at runtime need a Java-level shim that delegates to the right mechanism.

### Files to Modify

**`shim/java/dalvik/system/BaseDexClassLoader.java`:**

```java
package dalvik.system;

public class BaseDexClassLoader extends ClassLoader {
    private final String dexPath;
    private final String optimizedDirectory;
    private final String librarySearchPath;

    public BaseDexClassLoader(String dexPath, java.io.File optimizedDirectory,
                               String librarySearchPath, ClassLoader parent) {
        super(parent);
        this.dexPath = dexPath;
        this.optimizedDirectory = optimizedDirectory != null ?
                optimizedDirectory.getAbsolutePath() : null;
        this.librarySearchPath = librarySearchPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // In the engine model, all DEX files are on the boot classpath.
        // Try Class.forName with the parent loader first.
        try {
            return Class.forName(name, true, getParent());
        } catch (ClassNotFoundException e) {
            // Fall through
        }
        // If not found via parent, try system class loader
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(
                "Class not found in dexPath (" + dexPath + "): " + name, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[dexPath=" + dexPath + "]";
    }
}
```

**`shim/java/dalvik/system/DexClassLoader.java`:**

```java
package dalvik.system;

public class DexClassLoader extends BaseDexClassLoader {
    public DexClassLoader(String dexPath, String optimizedDirectory,
                           String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory != null ? new java.io.File(optimizedDirectory) : null,
              librarySearchPath, parent);
    }
}
```

**`shim/java/dalvik/system/PathClassLoader.java`:**

```java
package dalvik.system;

public class PathClassLoader extends BaseDexClassLoader {
    public PathClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, null, null, parent);
    }

    public PathClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, null, librarySearchPath, parent);
    }
}
```

### Tests

```java
static void testClassLoaders() {
    section("DexClassLoader / PathClassLoader");

    // Test PathClassLoader can load a known class
    dalvik.system.PathClassLoader pcl = new dalvik.system.PathClassLoader(
            ".", ClassLoader.getSystemClassLoader());
    try {
        Class<?> cls = pcl.loadClass("java.lang.String");
        check("PathClassLoader loads String", cls == String.class);
    } catch (Exception e) {
        check("PathClassLoader loads String", false);
    }

    // Test DexClassLoader
    dalvik.system.DexClassLoader dcl = new dalvik.system.DexClassLoader(
            "/tmp/fake.dex", "/tmp", null, ClassLoader.getSystemClassLoader());
    try {
        Class<?> cls = dcl.loadClass("java.util.ArrayList");
        check("DexClassLoader loads ArrayList", cls == java.util.ArrayList.class);
    } catch (Exception e) {
        check("DexClassLoader loads ArrayList", false);
    }

    // Test ClassNotFoundException for unknown class
    try {
        pcl.loadClass("com.nonexistent.FakeClass12345");
        check("unknown class throws CNFE", false);
    } catch (ClassNotFoundException e) {
        check("unknown class throws CNFE", true);
    }

    // Test toString
    check("DexClassLoader toString", dcl.toString().contains("/tmp/fake.dex"));
}
```

**Complexity:** MEDIUM — the shim delegates to Java's ClassLoader; real DEX loading is in Dalvik C++.
**Priority:** MEDIUM — blocks apps using multidex compat lib, plugin architectures.

---

## Task 5: Resource-Backed View Inflation (Basic)

**Goal:** `setContentView(R.layout.activity_main)` inflates a basic View tree from the APK's compiled layout XML.

**Why:** This is how ~90% of apps set up their UI. Without this, only apps using `setContentView(new MyView())` work.

### Background

Layout resources are stored as compiled binary XML in the APK:
```
res/layout/activity_main.xml  → binary AXML (same format as AndroidManifest.xml)
```

The resource ID `R.layout.activity_main` (e.g., `0x7f030000`) maps via `resources.arsc` to the filename `res/layout/activity_main.xml`.

Inflation flow:
1. `Activity.setContentView(R.layout.activity_main)` → calls LayoutInflater
2. LayoutInflater looks up resource ID → finds `res/layout/activity_main.xml`
3. Parse the binary XML → create View tree
4. For each `<TextView>`, `<Button>`, etc. → instantiate the Java class
5. For each attribute → call the setter (`setText()`, `setBackgroundColor()`, etc.)

### Implementation Scope (Phase 1 — Minimal)

Full layout inflation is very complex (styles, themes, includes, merges, custom view factories). For Phase 1, implement just enough to not crash:

**File:** Modify `shim/java/android/view/LayoutInflater.java`

```java
/**
 * Inflate a resource layout. Phase 1: extract the layout XML from the APK,
 * parse it with BinaryXmlParser, and create a basic View tree.
 *
 * Supported: LinearLayout, FrameLayout, RelativeLayout, TextView, Button,
 *            ImageView, View — with id, layout_width, layout_height.
 * NOT supported: styles, themes, includes, merges, data binding, custom views.
 */
public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
    // 1. Resolve resource ID → layout filename via ResourceTable
    // 2. Read binary XML from extracted APK res/ directory
    // 3. Parse AXML to build View tree
    // 4. Return root View

    // Fallback: return a FrameLayout with the resource ID as tag
    android.widget.FrameLayout fallback = new android.widget.FrameLayout(mContext);
    fallback.setTag("layout:0x" + Integer.toHexString(resource));
    if (attachToRoot && root != null) {
        root.addView(fallback);
    }
    return fallback;
}
```

**File:** NEW `shim/java/android/content/res/LayoutParser.java` (~200 lines)

Maps element names to View classes:
```java
private static final Map<String, String> VIEW_MAP = new HashMap<>();
static {
    VIEW_MAP.put("LinearLayout", "android.widget.LinearLayout");
    VIEW_MAP.put("FrameLayout", "android.widget.FrameLayout");
    VIEW_MAP.put("RelativeLayout", "android.widget.RelativeLayout");
    VIEW_MAP.put("TextView", "android.widget.TextView");
    VIEW_MAP.put("Button", "android.widget.Button");
    VIEW_MAP.put("ImageView", "android.widget.ImageView");
    VIEW_MAP.put("View", "android.view.View");
    VIEW_MAP.put("ScrollView", "android.widget.ScrollView");
    VIEW_MAP.put("RecyclerView", "androidx.recyclerview.widget.RecyclerView");
}

public View parseLayout(byte[] layoutXml, Context context) {
    // Use BinaryXmlParser-style AXML parsing
    // For each element: instantiate View class via reflection
    // For each attribute: set basic properties (id, text, visibility)
    // Build parent-child tree
}
```

### Wire to ApkLoader

Extract `res/` directory from APK:
```java
// In ApkLoader.load():
if (name.startsWith("res/") && !entry.isDirectory()) {
    File resOut = new File(extractDir, name);
    extractEntry(zip, entry, resOut);
}
info.resDir = new File(extractDir, "res").getAbsolutePath();
```

Add to `ApkInfo.java`:
```java
public String resDir;  // path to extracted res/ directory
```

### Tests

```java
static void testLayoutInflation() {
    section("Layout inflation (basic)");

    Context ctx = new Context();
    LayoutInflater inflater = LayoutInflater.from(ctx);
    check("inflater non-null", inflater != null);

    // Test inflate with resource ID (returns fallback FrameLayout)
    View v = inflater.inflate(0x7f030000, null, false);
    check("inflate returns non-null", v != null);
    check("inflate returns ViewGroup", v instanceof android.view.ViewGroup);
}
```

**Complexity:** LARGE — full inflation is a rabbit hole. Keep Phase 1 minimal (fallback FrameLayout).
**Priority:** HIGH conceptually but can be deferred — apps that crash here need a graceful fallback, not a full implementation.

---

## Task 6: On-Device Dalvik Integration Test

**Goal:** Verify that `ApkLoader.buildClasspath()` → Dalvik VM `-classpath` flag actually loads classes from multiple DEX files.

**Why:** The entire pipeline is untested end-to-end on a real Dalvik VM. We need proof that:
1. Multi-DEX extraction works (classes.dex + classes2.dex)
2. Dalvik loads both DEX files
3. `Class.forName("com.example.ClassFromDex2")` succeeds

### Test Plan

```bash
# Step 1: Build a test APK with 2 DEX files
# classes.dex contains: com.example.Main
# classes2.dex contains: com.example.Helper

# Step 2: Run on Dalvik
dalvik-port/build/dalvikvm \
    -Xbootclasspath:dalvik-port/core-android-x86.jar \
    -classpath /tmp/apk-test/classes.dex:/tmp/apk-test/classes2.dex \
    com.example.Main

# Step 3: In Main.java, do:
#   Class.forName("com.example.Helper")
#   → must succeed (proves multi-DEX works)
```

### Create Test APK Script

**File:** `test-apps/create-test-apk.sh` (NEW)

```bash
#!/bin/bash
# Creates a minimal test APK with:
# - Binary AndroidManifest.xml (pre-built, checked into test fixtures)
# - classes.dex (Main class)
# - classes2.dex (Helper class)
# - assets/config.json (test asset)
# - res/layout/activity_main.xml (test layout)
# - resources.arsc (test resource table)

OUTDIR=$(mktemp -d)

# Compile test Java files
javac -source 8 -target 8 -d "$OUTDIR/classes1" test-fixtures/Main.java
javac -source 8 -target 8 -d "$OUTDIR/classes2" test-fixtures/Helper.java

# Convert to DEX (requires d8 or dx)
# d8 --output "$OUTDIR" "$OUTDIR/classes1/com/example/Main.class"
# d8 --output "$OUTDIR" "$OUTDIR/classes2/com/example/Helper.class"

# Build ZIP
cd "$OUTDIR"
zip -r test.apk AndroidManifest.xml classes.dex classes2.dex \
    assets/config.json res/layout/activity_main.xml resources.arsc

echo "Test APK: $OUTDIR/test.apk"
```

### Dalvik-Level Verification

**File:** `test-apps/test-multidex.sh` (NEW)

```bash
#!/bin/bash
# Verify multi-DEX loading on actual Dalvik VM
set -e

APK="$1"
if [ -z "$APK" ]; then
    echo "Usage: $0 <apk-path>"
    exit 1
fi

# Extract APK
EXTRACT_DIR=$(mktemp -d)
unzip -o "$APK" -d "$EXTRACT_DIR"

# Build classpath from all DEX files
CLASSPATH=""
for dex in $(ls "$EXTRACT_DIR"/classes*.dex 2>/dev/null | sort); do
    [ -n "$CLASSPATH" ] && CLASSPATH="$CLASSPATH:"
    CLASSPATH="$CLASSPATH$dex"
done

echo "Classpath: $CLASSPATH"

# Run on Dalvik
./dalvik-port/build/dalvikvm \
    -Xbootclasspath:dalvik-port/core-android-x86.jar \
    -classpath "$CLASSPATH" \
    com.example.Main
```

**Complexity:** MEDIUM — mostly build tooling and test scripting.
**Priority:** CRITICAL for confidence — proves the architecture actually works end-to-end.

---

## Task 7: Wire Everything Together in MiniServer

**Goal:** After all tasks above are implemented, `MiniServer.loadApk()` should set up the full runtime context: DEX classpath, resources, assets, native libs.

**File:** Modify `shim/java/android/app/MiniServer.java`

### Updated loadApk()

```java
public ApkInfo loadApk(String apkPath) throws IOException {
    ApkInfo info = ApkLoader.load(apkPath);

    // Update package info
    mPackageName = info.packageName;

    // --- NEW: Wire resources ---
    if (info.resourceTable != null) {
        mApplication.getResources().loadResourceTable(info.resourceTable);
    }

    // --- NEW: Wire assets ---
    if (info.assetDir != null) {
        mApplication.getAssets().setAssetDir(info.assetDir);
    }

    // --- NEW: Set native lib path ---
    if (info.nativeLibDir != null) {
        System.setProperty("app.native.lib.dir", info.nativeLibDir);
    }

    // If manifest declares a custom Application class, instantiate it
    if (info.applicationClassName != null) {
        try {
            Class<?> appCls = Class.forName(info.applicationClassName);
            mApplication = (Application) appCls.newInstance();
        } catch (Exception e) {
            // fallback to default Application
        }
    }
    mApplication.setPackageName(info.packageName);

    // Register all activities from manifest
    for (String activityName : info.activities) {
        mPackageManager.addActivity(activityName);
    }

    // Register launcher activity with MAIN/LAUNCHER filter
    if (info.launcherActivity != null) {
        IntentFilter launcherFilter = new IntentFilter(Intent.ACTION_MAIN);
        launcherFilter.addCategory(Intent.CATEGORY_LAUNCHER);
        mPackageManager.addActivity(info.launcherActivity, launcherFilter);
    }

    // Register services
    for (String serviceName : info.services) {
        mPackageManager.addService(serviceName);
    }

    // Call Application.onCreate after all wiring
    mApplication.onCreate();

    return info;
}
```

### Tests

```java
static void testFullApkPipeline() {
    section("Full APK pipeline");

    // Create a test APK with resources.arsc, assets/, and lib/
    // Load via MiniServer
    // Verify: resources resolve, assets readable, native lib dir set
    // Verify: activities registered, launcher detected
    // Verify: Application.onCreate called

    // This is the integration test that proves WS3 is complete
}
```

**Complexity:** SMALL — glue code wiring existing components.
**Priority:** HIGH — this is the capstone integration.

---

## Execution Order

```
Task 4: DexClassLoader (SMALL, unblocks multidex apps)
  ↓
Task 2: Asset extraction (SMALL, unblocks config/font apps)
  ↓
Task 3: Native lib extraction (SMALL, extraction only — loading is Dalvik's job)
  ↓
Task 1: resources.arsc parser (LARGE, core feature)
  ↓
Task 5: Layout inflation (LARGE, deferred — fallback is acceptable)
  ↓
Task 7: Wire everything in MiniServer (SMALL, integration)
  ↓
Task 6: On-device Dalvik test (MEDIUM, validation)
```

**Recommended parallel splits:**
- **Agent A:** Task 4 (ClassLoaders) + Task 2 (Assets) + Task 3 (Native libs) — small, independent
- **Agent B:** Task 1 (ResourceTable) — large, standalone
- **Agent C:** Task 6 (Dalvik test scripts) — independent tooling
- After A+B complete: Task 7 (integration wiring)
- Task 5 (layout inflation) can be deferred to a follow-up workstream

---

## Verification

```bash
# Compile and run all tests
cd test-apps && ./run-local-tests.sh headless

# MUST:
# - Compile cleanly (no new errors)
# - All existing 860 tests still pass
# - New WS3 tests pass
# Target: 900+ tests passing after all tasks
```

---

## Files Summary

### New Files

| File | Task | Lines (est.) |
|------|------|-------------|
| `shim/java/android/content/res/ResourceTable.java` | 1 | 400-600 |
| `shim/java/android/content/res/LayoutParser.java` | 5 | 200 |
| `test-apps/create-test-apk.sh` | 6 | 50 |
| `test-apps/test-multidex.sh` | 6 | 30 |

### Modified Files

| File | Task | Changes |
|------|------|---------|
| `shim/java/android/content/res/AssetManager.java` | 2 | Add setAssetDir(), real open()/list() |
| `shim/java/android/content/res/Resources.java` | 1 | Wire to ResourceTable |
| `shim/java/android/app/ApkLoader.java` | 1,2,3 | Extract resources.arsc, assets/, lib/*.so |
| `shim/java/android/app/ApkInfo.java` | 1,2,3 | Add resourceTable, assetDir, nativeLibDir, resDir |
| `shim/java/android/app/MiniServer.java` | 7 | Wire resources, assets, native libs in loadApk() |
| `shim/java/dalvik/system/BaseDexClassLoader.java` | 4 | Real findClass() with parent delegation |
| `shim/java/dalvik/system/DexClassLoader.java` | 4 | Wire constructor to BaseDexClassLoader |
| `shim/java/dalvik/system/PathClassLoader.java` | 4 | Wire constructor to BaseDexClassLoader |
| `shim/java/android/view/LayoutInflater.java` | 5 | Resource-backed inflate() with fallback |
| `test-apps/02-headless-cli/src/HeadlessTest.java` | ALL | Add testResourceTable, testAssetManager, testClassLoaders, testFullApkPipeline |

---

## Key Rules

- **Don't break existing tests**: 860 pass, 0 fail — no regressions allowed
- **Build on existing code**: ApkLoader, BinaryXmlParser, MiniServer are solid — extend, don't rewrite
- **Match AOSP signatures**: method signatures must match what real Android apps expect
- **Pure Java for Tier A/B tasks**: no JNI, no OHBridge — everything is in-process Java
- **Self-validating**: every new feature must include tests proving it works
- **Read existing files FIRST**: understand what's already there before modifying
- **Graceful degradation**: if a resource can't be resolved, return a reasonable default — don't crash
- **Keep resources.arsc parser minimal**: only parse what's needed (strings, colors, dimensions, layout filenames). Skip styles, themes, nine-patches, state lists.

---

## Reference: resources.arsc Binary Format

```
resources.arsc structure:
┌─────────────────────────────┐
│ ResTable_header             │  type=0x0002
│   packageCount              │
├─────────────────────────────┤
│ ResStringPool               │  Global string pool (all values)
│   stringCount, strings[]    │
├─────────────────────────────┤
│ ResTable_package            │  type=0x0200
│   id (0x7f for app)         │
│   name ("com.example.app")  │
│ ┌───────────────────────────┤
│ │ Type string pool          │  Type names: "attr", "drawable", "layout", "string", ...
│ ├───────────────────────────┤
│ │ Key string pool           │  Key names: "app_name", "hello_world", ...
│ ├───────────────────────────┤
│ │ ResTable_typeSpec (0x0202)│  For each type: flags per entry
│ ├───────────────────────────┤
│ │ ResTable_type (0x0201)    │  For each config (default, hdpi, zh, ...):
│ │   entryCount              │    Array of ResTable_entry
│ │   entries[]               │    Each entry: flags, key, Res_value
│ │     Res_value: {          │
│ │       size, type, data    │  type: 0x03=string, 0x10=int, 0x1c=color
│ │     }                     │
│ └───────────────────────────┤
└─────────────────────────────┘

Resource ID format: 0xPPTTEEEE
  PP = package (0x7f)
  TT = type index (01=attr, 02=drawable, 03=layout, 04=string, ...)
  EEEE = entry index
```

## Reference: AXML Layout Format

Layout XML files in `res/layout/` are compiled to the same binary AXML format as AndroidManifest.xml. The existing `BinaryXmlParser` can parse them — just need to interpret the elements as View classes instead of manifest components.

Key differences from manifest parsing:
- Element names are View class names (`LinearLayout`, `TextView`, `Button`)
- Attributes are View properties (`android:text`, `android:id`, `android:layout_width`)
- `android:id` values are resource IDs that map to `R.id.*` constants
- `android:layout_width` / `android:layout_height` can be `match_parent` (-1), `wrap_content` (-2), or dp values
