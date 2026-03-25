package com.westlake.host

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dalvik.system.DexClassLoader
import java.io.*
import java.util.zip.ZipFile

/**
 * Runs a real APK inside the Westlake engine:
 * 1. Extracts classes.dex + resources.arsc from APK
 * 2. Loads classes via DexClassLoader
 * 3. Parses resources.arsc
 * 4. Instantiates the launcher Activity
 * 5. Calls onCreate → gets the content View
 * 6. Displays it inside our Compose UI
 */
object ApkRunner {
    private const val TAG = "ApkRunner"

    data class RunResult(
        val steps: List<String>,
        val contentView: View? = null,
        val error: String? = null
    )

    fun runApk(activity: WestlakeActivity, apkPath: String, launcherActivity: String): RunResult {
        val steps = mutableListOf<String>()

        try {
            // Step 1: Extract DEX + resources from APK
            steps.add("✅ APK: $apkPath (${File(apkPath).length() / 1024}KB)")

            val cacheDir = File(activity.cacheDir, "apk_run").apply { mkdirs() }
            val zip = ZipFile(apkPath)

            // Extract classes.dex
            val dexFile = File(cacheDir, "classes.dex")
            zip.getEntry("classes.dex")?.let { entry ->
                zip.getInputStream(entry).use { input ->
                    FileOutputStream(dexFile).use { output ->
                        input.copyTo(output)
                    }
                }
                steps.add("✅ classes.dex: ${dexFile.length() / 1024}KB")
            } ?: run {
                return RunResult(steps, error = "No classes.dex in APK")
            }

            // Parse resources.arsc
            var resourceData: ByteArray? = null
            zip.getEntry("resources.arsc")?.let { entry ->
                resourceData = zip.getInputStream(entry).readBytes()
                steps.add("✅ resources.arsc: ${resourceData!!.size / 1024}KB")
            }
            zip.close()

            // Step 2: Load classes via DexClassLoader
            val optDir = File(cacheDir, "oat").apply { mkdirs() }
            val dexLoader = DexClassLoader(
                dexFile.absolutePath, optDir.absolutePath,
                activity.applicationInfo.nativeLibraryDir,
                activity.classLoader
            )
            steps.add("✅ DexClassLoader created")

            // Step 3: Load the Activity class
            val actClass = dexLoader.loadClass(launcherActivity)
            steps.add("✅ Loaded: ${actClass.simpleName}")
            steps.add("  Superclass: ${actClass.superclass?.name}")

            // Step 4: Parse resources and create a Context with them
            // Use the engine's resource parser via reflection
            val engineCl = activity.engineClassLoader
            if (engineCl != null && resourceData != null) {
                try {
                    val apkResLoader = engineCl.loadClass("android.content.res.ApkResourceLoader")
                    val loadMethod = apkResLoader.getMethod("loadFromApk", String::class.java)
                    val resources = loadMethod.invoke(null, apkPath)
                    val resClass = resources!!.javaClass
                    val getStringCount = resClass.getMethod("getResourceTable")
                    val table = getStringCount.invoke(resources)
                    if (table != null) {
                        val strCount = table.javaClass.getMethod("getStringCount").invoke(table) as Int
                        val intCount = table.javaClass.getMethod("getIntegerCount").invoke(table) as Int
                        steps.add("✅ Resources parsed: $strCount strings, $intCount integers")
                    }
                } catch (e: Exception) {
                    steps.add("⚠️ Resource parsing: ${e.message}")
                }
            }

            // Step 5: Create the Activity with proper Context
            steps.add("⏳ Instantiating ${actClass.simpleName}...")

            try {
                // Create a Context for the APK using createPackageContext
                // This gives the Activity access to the APK's resources
                val apkPkg = zip.let {
                    // Read package name from manifest
                    val manifest = ZipFile(apkPath).use { z ->
                        z.getEntry("AndroidManifest.xml")?.let { entry ->
                            z.getInputStream(entry).readBytes()
                        }
                    }
                    // Try to get package from the class name
                    launcherActivity.substringBeforeLast(".")
                        .substringBeforeLast(".") // go up two levels
                }

                // Try createPackageContext for resource access
                var apkContext: android.content.Context = activity
                try {
                    apkContext = activity.createPackageContext(
                        launcherActivity.substringBeforeLast(".").substringBeforeLast("."),
                        android.content.Context.CONTEXT_INCLUDE_CODE or
                            android.content.Context.CONTEXT_IGNORE_SECURITY
                    )
                    steps.add("✅ Package context created")
                } catch (e: Exception) {
                    steps.add("⚠️ createPackageContext: ${e.message}")
                    // Use host activity as fallback context
                }

                // Create Activity instance
                val actInstance = actClass.newInstance()
                steps.add("✅ Activity instance created")

                // Set up the Activity manually instead of calling attach()
                // attach() has too many framework dependencies (token, ActivityThread, etc.)
                // Instead: attachBaseContext + create Window + set fields directly
                try {
                    // 1. attachBaseContext
                    val attachBase = android.content.ContextWrapper::class.java
                        .getDeclaredMethod("attachBaseContext", android.content.Context::class.java)
                    attachBase.isAccessible = true
                    attachBase.invoke(actInstance, apkContext)
                    steps.add("✅ attachBaseContext")

                    // 2. Set mComponent
                    val compField = android.app.Activity::class.java.getDeclaredField("mComponent")
                    compField.isAccessible = true
                    compField.set(actInstance, android.content.ComponentName(
                        launcherActivity.substringBeforeLast(".").substringBeforeLast("."),
                        launcherActivity))
                    steps.add("✅ mComponent set")

                    // 3. Set mApplication
                    val appField = android.app.Activity::class.java.getDeclaredField("mApplication")
                    appField.isAccessible = true
                    appField.set(actInstance, activity.application)

                    // 4. Create PhoneWindow and set mWindow
                    val phoneWindowClass = Class.forName("com.android.internal.policy.PhoneWindow")
                    val pw = phoneWindowClass.getConstructor(android.content.Context::class.java)
                        .newInstance(apkContext)
                    val windowField = android.app.Activity::class.java.getDeclaredField("mWindow")
                    windowField.isAccessible = true
                    windowField.set(actInstance, pw)
                    steps.add("✅ PhoneWindow created")

                    // 5. Set window callback to the activity
                    (pw as android.view.Window).setCallback(actInstance as android.view.Window.Callback)

                    // 6. Set mUiThread
                    try {
                        val uiField = android.app.Activity::class.java.getDeclaredField("mUiThread")
                        uiField.isAccessible = true
                        uiField.set(actInstance, Thread.currentThread())
                    } catch (_: Exception) {}

                    // 7. Set mToken from host activity
                    try {
                        val tokenField = android.app.Activity::class.java.getDeclaredField("mToken")
                        tokenField.isAccessible = true
                        val hostTokenField = android.app.Activity::class.java.getDeclaredField("mToken")
                        hostTokenField.isAccessible = true
                        tokenField.set(actInstance, hostTokenField.get(activity))
                    } catch (_: Exception) {}

                    // 8. Set mIntent
                    try {
                        val intentField = android.app.Activity::class.java.getDeclaredField("mIntent")
                        intentField.isAccessible = true
                        intentField.set(actInstance, android.content.Intent())
                    } catch (_: Exception) {}

                    // 9. Set mActivityInfo (FragmentActivity reads parentActivityName from it)
                    try {
                        val aiField = android.app.Activity::class.java.getDeclaredField("mActivityInfo")
                        aiField.isAccessible = true
                        val actInfo = android.content.pm.ActivityInfo()
                        actInfo.packageName = launcherActivity.substringBeforeLast(".").substringBeforeLast(".")
                        actInfo.name = launcherActivity
                        actInfo.parentActivityName = ""
                        actInfo.taskAffinity = actInfo.packageName
                        actInfo.launchMode = 0
                        actInfo.theme = android.R.style.Theme
                        aiField.set(actInstance, actInfo)
                    } catch (_: Exception) {}

                    steps.add("✅ Activity fully initialized")

                } catch (e: Exception) {
                    val cause = e.cause ?: e
                    steps.add("❌ Init failed: ${cause.javaClass.simpleName}: ${cause.message?.take(150)}")
                    Log.e(TAG, "Activity init failed", cause)
                }

                // Try calling onCreate
                try {
                    val onCreate = actClass.getDeclaredMethod("onCreate", Bundle::class.java)
                    onCreate.isAccessible = true
                    onCreate.invoke(actInstance, null as Bundle?)
                    steps.add("✅ onCreate() called")

                    // Try to get the content view
                    try {
                        val getWindow = actClass.getMethod("getWindow")
                        val window = getWindow.invoke(actInstance)
                        if (window != null) {
                            val getDecor = window.javaClass.getMethod("getDecorView")
                            val decorView = getDecor.invoke(window) as? View
                            if (decorView != null) {
                                steps.add("✅ Got content View: ${decorView.javaClass.simpleName}")
                                // Detach from any parent
                                (decorView.parent as? ViewGroup)?.removeView(decorView)
                                return RunResult(steps, contentView = decorView)
                            }
                        }
                    } catch (e: Exception) {
                        steps.add("⚠️ getWindow: ${e.javaClass.simpleName}: ${e.cause?.message ?: e.message}")
                    }
                } catch (e: Exception) {
                    val cause = e.cause ?: e
                    steps.add("❌ onCreate failed: ${cause.javaClass.simpleName}")
                    steps.add("  ${cause.message?.take(200)}")
                    // Log full stack
                    Log.e(TAG, "onCreate failed", cause)
                }
            } catch (e: Exception) {
                steps.add("❌ Instance failed: ${e.javaClass.simpleName}: ${e.message}")
            }

            return RunResult(steps)

        } catch (e: Exception) {
            steps.add("❌ Fatal: ${e.javaClass.simpleName}: ${e.message}")
            return RunResult(steps, error = e.message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkRunnerScreen(apkPath: String, launcherActivity: String, appName: String) {
    val activity = WestlakeActivity.instance ?: return
    var result by remember { mutableStateOf<ApkRunner.RunResult?>(null) }

    LaunchedEffect(apkPath) {
        // Run on main thread since we need UI context for Views
        result = try {
            ApkRunner.runApk(activity, apkPath, launcherActivity)
        } catch (e: Exception) {
            ApkRunner.RunResult(listOf("❌ Crash: ${e.javaClass.simpleName}: ${e.message}"))
        }
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Running: $appName", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { activity.showHome() }) {
                            Text("←", fontSize = 20.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A2E),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Diagnostic steps
                result?.let { r ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(r.steps.size) { i ->
                            Text(r.steps[i], fontSize = 13.sp,
                                color = if (r.steps[i].startsWith("❌")) Color.Red
                                else if (r.steps[i].startsWith("⚠")) Color.Yellow
                                else Color.White.copy(0.8f))
                        }
                    }

                    // Show the APK's content View if we got one
                    r.contentView?.let { view ->
                        AndroidView(
                            factory = { view },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                } ?: run {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(32.dp),
                        color = Color(0xFF90CAF9)
                    )
                }
            }
        }
    }
}
