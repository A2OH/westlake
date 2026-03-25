package com.westlake.host

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Method

/**
 * Westlake Engine Host — Compose-native Activity.
 *
 * Extends ComponentActivity (real AndroidX lifecycle) so Compose works natively.
 * Loads app.dex + aosp-shim.dex via child-first DexClassLoader for custom apps.
 * Can also host real APK Activities via startActivity().
 */
class WestlakeActivity : ComponentActivity() {

    companion object {
        private const val TAG = "Westlake"

        @JvmField
        var instance: WestlakeActivity? = null

        @JvmField
        var shimRootView: View? = null

        // For native bridge compatibility
        @JvmStatic var currentCanvas: android.graphics.Canvas? = null
        @JvmStatic var currentHolder: android.view.SurfaceHolder? = null
        @JvmStatic var canvasWidth = 0
        @JvmStatic var canvasHeight = 0
        @JvmStatic var paintPool = arrayOfNulls<android.graphics.Paint>(256)
        @JvmStatic var pathPool = arrayOfNulls<android.graphics.Path>(256)
        @JvmStatic var paintNext = 1
        @JvmStatic var pathNext = 1
        @JvmStatic var shimActivity: Any? = null
        @JvmStatic var shimDispatchTouch: Method? = null
    }

    var engineClassLoader: ClassLoader? = null

    /** Launch a custom app from the engine DEX by calling its init+show methods */
    fun launchCustomApp(className: String, initMethod: String?, showMethod: String) {
        val cl = engineClassLoader
        if (cl == null) {
            Log.e(TAG, "launchCustomApp: engineClassLoader is null!")
            return
        }
        Log.i(TAG, "launchCustomApp: $className init=$initMethod show=$showMethod")
        try {
            val appClass = cl.loadClass(className)
            if (initMethod != null) {
                try {
                    appClass.getMethod(initMethod, android.content.Context::class.java).invoke(null, this)
                    Log.i(TAG, "  init() called OK")
                } catch (_: NoSuchMethodException) {
                    Log.w(TAG, "  no init(Context) method")
                }
            }
            try {
                appClass.getMethod(showMethod, android.content.Context::class.java).invoke(null, this)
                Log.i(TAG, "  show(Context) called OK")
            } catch (_: NoSuchMethodException) {
                try {
                    appClass.getMethod(showMethod).invoke(null)
                    Log.i(TAG, "  show() called OK")
                } catch (e2: Exception) {
                    Log.e(TAG, "  show failed: $e2")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "launchCustomApp $className: $e", e)
        }
    }

    /** Return to Compose home */
    fun showHome() {
        setContent { WestlakeHome() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        Log.i(TAG, "WestlakeActivity onCreate (ComponentActivity + Compose)")

        // Show Compose UI immediately
        setContent {
            WestlakeHome()
        }

        // Load engine DEX files in background
        Thread(Runnable { loadEngine() }, "WestlakeEngine").start()
    }

    private fun loadEngine() {
        android.os.Looper.prepare()
        try {
            Log.i(TAG, "Extracting DEX files...")
            val cacheDir = cacheDir

            val shimDex = extractAsset("aosp-shim.dex", cacheDir)
            val appDex = extractAsset("app.dex", cacheDir)

            if (shimDex == null || appDex == null) {
                Log.e(TAG, "Failed to extract DEX files")
                return
            }

            val optDir = File(cacheDir, "oat").apply { mkdirs() }
            val dexPath = "${shimDex.absolutePath}:${appDex.absolutePath}"
            val nativeLibDir = applicationInfo.nativeLibraryDir

            val dexLoader = DexClassLoader(dexPath, optDir.absolutePath, nativeLibDir, classLoader)

            // Child-first wrapper
            val findClassMethod = ClassLoader::class.java.getDeclaredMethod("findClass", String::class.java)
            findClassMethod.isAccessible = true

            val childFirst = object : ClassLoader(classLoader) {
                override fun loadClass(name: String, resolve: Boolean): Class<*> {
                    findLoadedClass(name)?.let { return it }
                    if (name.startsWith("java.") || name.startsWith("javax.") ||
                        name.startsWith("sun.") || name.startsWith("dalvik.system.")) {
                        return super.loadClass(name, resolve)
                    }
                    try {
                        val c = findClassMethod.invoke(dexLoader, name) as? Class<*>
                        if (c != null) return c
                    } catch (_: Exception) {}
                    return super.loadClass(name, resolve)
                }
            }

            engineClassLoader = childFirst
            Thread.currentThread().contextClassLoader = childFirst

            Log.i(TAG, "Engine DEX loaded, launching MockDonaldsApp...")
            val appClass = childFirst.loadClass("com.example.mockdonalds.MockDonaldsApp")
            val main = appClass.getMethod("main", Array<String>::class.java)
            main.invoke(null, arrayOf<String>())
            Log.i(TAG, "Engine finished")

        } catch (e: Exception) {
            Log.e(TAG, "Engine error: ${e.message}", e)
        }
    }

    private fun extractAsset(name: String, dir: File): File? {
        return try {
            val input = assets.open(name)
            val out = File(dir, name)
            FileOutputStream(out).use { fos ->
                val buf = ByteArray(8192)
                var n: Int
                while (input.read(buf).also { n = it } > 0) fos.write(buf, 0, n)
            }
            input.close()
            out
        } catch (e: Exception) {
            Log.e(TAG, "Extract $name: ${e.message}")
            null
        }
    }
}

/**
 * Compose home screen — app gallery with Material Design 3.
 */
@Composable
fun WestlakeHome() {
    val apps = remember {
        listOf(
            AppInfo("MockDonalds", "Restaurant ordering", Color(0xFFDA291C), "com.example.mockdonalds.MockApp", "init", "showMenu"),
            AppInfo("Dialer", "Phone dialer", Color(0xFF1565C0), "com.example.dialer.DialerEntry", null, "launch"),
            AppInfo("Social Feed", "Social media", Color(0xFF1877F2), "com.example.socialfeed.SocialFeedApp", "init", "showFeed"),
            AppInfo("Huawei Calc", "Calculator (real APK)", Color(0xFF6A1B9A), "com.example.mockdonalds.XmlTestHelper", null, "loadHuaweiCalculator"),
        )
    }

    val realApps = remember {
        listOf(
            RealAppInfo("Calculator", "com.huawei.calculator", "com.huawei.calculator.Calculator", Color(0xFF4CAF50)),
            RealAppInfo("Clock", "com.android.deskclock", "com.android.deskclock.AlarmsMainActivity", Color(0xFF2196F3)),
            RealAppInfo("Settings", "com.android.settings", "com.android.settings.HWSettings", Color(0xFF607D8B)),
            RealAppInfo("Calendar", "com.android.calendar", "com.android.calendar.AllInOneActivity", Color(0xFFE91E63)),
        )
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF90CAF9),
            surface = Color(0xFF1A1A2E),
            background = Color(0xFF1A1A2E),
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                // Header
                Text(
                    "Westlake Apps",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.padding(20.dp, 40.dp, 20.dp, 4.dp)
                )
                Text(
                    "Android apps on Westlake Engine + Jetpack Compose",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Text("Custom Apps", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp))
                    }

                    items(apps) { app ->
                        AppCard(app)
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        Text("Installed Apps (Real APKs)", fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp))
                    }

                    items(realApps) { app ->
                        RealAppCard(app)
                    }
                }
            }
        }
    }
}

@Composable
fun AppCard(app: AppInfo) {
    val activity = WestlakeActivity.instance
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                activity?.launchCustomApp(app.className, app.initMethod, app.showMethod)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(app.color),
                contentAlignment = Alignment.Center
            ) {
                Text(app.name.first().toString(), fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text(app.description, fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Text("▶", fontSize = 18.sp, color = app.color)
        }
    }
}

@Composable
fun RealAppCard(app: RealAppInfo) {
    val activity = WestlakeActivity.instance
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                activity?.let {
                    try {
                        val intent = android.content.Intent()
                        intent.setClassName(app.packageName, app.activityName)
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("Westlake", "Launch ${app.name}: $e")
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D44))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(app.color),
                contentAlignment = Alignment.Center
            ) {
                Text(app.name.first().toString(), fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text(app.packageName, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
            }
            Text("▶", fontSize = 18.sp, color = app.color)
        }
    }
}

data class AppInfo(val name: String, val description: String, val color: Color, val className: String, val initMethod: String? = "init", val showMethod: String = "showMenu")
data class RealAppInfo(val name: String, val packageName: String, val activityName: String, val color: Color)
