package com.westlake.host

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ContextThemeWrapper
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
import dalvik.system.PathClassLoader
import java.io.File

/**
 * Loads a real published APK and runs its launcher Activity in-process
 * using the phone's real Android framework (not our shim).
 *
 * The APK's Views are real Android Views rendered by Skia/GPU at 60fps.
 */
object NativeApkLoader {
    private const val TAG = "NativeApkLoader"

    data class Result(
        val steps: List<String>,
        val contentView: View? = null
    )

    fun load(host: WestlakeActivity, packageName: String, activityName: String): Result {
        val steps = mutableListOf<String>()
        try {
            // Step 1: Get APK path
            val appInfo = host.packageManager.getApplicationInfo(packageName, 0)
            val apkPath = appInfo.sourceDir
            steps.add("✅ APK: $apkPath (${File(apkPath).length() / 1024}KB)")

            // Step 2: Create context with APK's resources
            val apkContext = host.createPackageContext(packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            steps.add("✅ Package context created")

            // Step 3: Load Activity class using the APK's classloader
            val apkClassLoader = apkContext.classLoader
            val actClass = apkClassLoader.loadClass(activityName)
            steps.add("✅ Loaded: ${actClass.simpleName}")
            steps.add("  Super: ${actClass.superclass?.name}")

            // Step 4: Create Activity via Instrumentation (proper framework way)
            val actThread = Class.forName("android.app.ActivityThread")
                .getMethod("currentActivityThread").invoke(null)
            val instr = actThread.javaClass.getMethod("getInstrumentation").invoke(actThread)
                as android.app.Instrumentation
            val act = instr.newActivity(apkClassLoader, activityName,
                android.content.Intent(android.content.Intent.ACTION_MAIN)) as Activity
            steps.add("✅ Instance created (via Instrumentation)")

            // Step 5: Call Activity.attach() with proper parameters
            // This sets up mWindow, mIntent, mActivityInfo, mApplication, etc.
            try {
                val attachMethod = Activity::class.java.getDeclaredMethod("attach",
                    Context::class.java,                                    // context
                    Class.forName("android.app.ActivityThread"),             // aThread
                    Class.forName("android.app.Instrumentation"),           // instr
                    android.os.IBinder::class.java,                         // token
                    Int::class.javaPrimitiveType,                           // ident
                    android.app.Application::class.java,                    // application
                    android.content.Intent::class.java,                     // intent
                    android.content.pm.ActivityInfo::class.java,            // info
                    CharSequence::class.java,                               // title
                    Activity::class.java,                                   // parent
                    String::class.java,                                     // id
                    Any::class.java,                                        // lastNonConfigInstance
                    android.content.res.Configuration::class.java,          // config
                    String::class.java,                                     // referrer
                    Class.forName("com.android.internal.app.IVoiceInteractor"), // voiceInteractor
                    Class.forName("android.view.Window")                    // window
                )
                attachMethod.isAccessible = true

                val actThread = Class.forName("android.app.ActivityThread")
                    .getMethod("currentActivityThread").invoke(null)
                val instr = actThread.javaClass.getMethod("getInstrumentation").invoke(actThread)
                val actInfo = host.packageManager.getActivityInfo(
                    android.content.ComponentName(packageName, activityName), 0)
                actInfo.applicationInfo = appInfo

                // Get host Activity's window token for WindowManager registration
                val hostToken = try {
                    val tokenField = Activity::class.java.getDeclaredField("mToken")
                    tokenField.isAccessible = true
                    tokenField.get(host) as? android.os.IBinder
                } catch (e: Exception) { null }
                // Use the host's existing window so the MCD Activity renders into it
                val hostWindow = host.window
                attachMethod.invoke(act,
                    apkContext, actThread, instr, hostToken, 0,
                    host.application,
                    android.content.Intent(android.content.Intent.ACTION_MAIN),
                    actInfo, packageName, null, null, null,
                    host.resources.configuration, null, null, hostWindow)
                steps.add("✅ Activity.attach() done")
            } catch (e: Exception) {
                steps.add("⚠️ attach: ${e.cause?.message ?: e.message}")
                // Fallback: set fields manually
                try {
                    for (fname in listOf("mIntent", "mApplication", "mActivityInfo")) {
                        val f = Activity::class.java.getDeclaredField(fname)
                        f.isAccessible = true
                        when (fname) {
                            "mIntent" -> f.set(act, android.content.Intent(android.content.Intent.ACTION_MAIN))
                            "mApplication" -> f.set(act, host.application)
                            "mActivityInfo" -> {
                                val ai = host.packageManager.getActivityInfo(
                                    android.content.ComponentName(packageName, activityName), 0)
                                ai.applicationInfo = appInfo
                                f.set(act, ai)
                            }
                        }
                    }
                    // Still need a window
                    val pwClass = Class.forName("com.android.internal.policy.PhoneWindow")
                    val pw = pwClass.getConstructor(Context::class.java).newInstance(apkContext)
                    Activity::class.java.getDeclaredField("mWindow").apply { isAccessible = true }.set(act, pw)
                    // Set mBase — use apkContext (has MCD resources) wrapped to also provide system services
                    try {
                        // apkContext has MCD resources but may lack system services
                        // Try calling the real attachBaseContext first
                        val attachBase = android.content.ContextWrapper::class.java.getDeclaredMethod("attachBaseContext", Context::class.java)
                        attachBase.isAccessible = true
                        attachBase.invoke(act, apkContext)
                    } catch (_: Exception) {
                        try {
                            val mBase = android.content.ContextWrapper::class.java.getDeclaredField("mBase")
                            mBase.isAccessible = true
                            mBase.set(act, apkContext)
                        } catch (_: Exception) {}
                    }
                    // Set mResources (Activity.getResources() uses this)
                    try {
                        val mRes = android.content.ContextWrapper::class.java.getDeclaredField("mResources")
                        mRes.isAccessible = true
                        mRes.set(act, apkContext.resources)
                    } catch (_: Exception) {
                        try {
                            // Try on ContextThemeWrapper
                            val mRes = android.view.ContextThemeWrapper::class.java.getDeclaredField("mResources")
                            mRes.isAccessible = true
                            mRes.set(act, apkContext.resources)
                        } catch (_: Exception) {}
                    }
                    // Set mTheme
                    try {
                        val mTheme = android.view.ContextThemeWrapper::class.java.getDeclaredField("mTheme")
                        mTheme.isAccessible = true
                        mTheme.set(act, apkContext.theme)
                    } catch (_: Exception) {}
                    // Set Window's callback to this Activity
                    try { (pw as android.view.Window).callback = act } catch (_: Exception) {}
                    steps.add("✅ Fallback field setup done (mBase+mResources+mWindow)")
                } catch (e2: Exception) {
                    steps.add("⚠️ Fallback: ${e2.message?.take(60)}")
                }
            }

            // Step 6.5: Apply AppCompat theme from MCD APK
            try {
                // Get the theme ID from the APK's manifest
                val actInfo2 = host.packageManager.getActivityInfo(
                    android.content.ComponentName(packageName, activityName), 0)
                var themeId = actInfo2.themeResource
                if (themeId == 0) themeId = actInfo2.applicationInfo.theme
                if (themeId != 0) {
                    act.setTheme(themeId)
                    steps.add("✅ Theme applied: 0x${Integer.toHexString(themeId)}")
                } else {
                    // Fallback: use a standard AppCompat theme
                    act.setTheme(android.R.style.Theme_Material_Light_NoActionBar)
                    steps.add("✅ Fallback theme: Material.Light.NoActionBar")
                }
            } catch (e: Exception) {
                try { act.setTheme(android.R.style.Theme_Material_Light_NoActionBar) } catch (_: Exception) {}
                steps.add("⚠️ Theme: ${e.message?.take(40)}")
            }

            // Step 7: Call onCreate via Instrumentation
            try {
                instr.callActivityOnCreate(act, null)
                steps.add("✅ onCreate() called")
            } catch (e: Exception) {
                steps.add("⚠️ onCreate: ${e.cause?.message ?: e.message}")
            }

            // Step 8: Get the content view
            try {
                val window = act.window
                if (window != null) {
                    val decorView = window.decorView
                    if (decorView != null) {
                        // Find the content frame (android.R.id.content)
                        val content = decorView.findViewById<ViewGroup>(android.R.id.content)
                        if (content != null && content.childCount > 0) {
                            val rootView = content.getChildAt(0)
                            steps.add("✅ Content view: ${rootView.javaClass.simpleName}")
                            (rootView.parent as? ViewGroup)?.removeView(rootView)
                            return Result(steps, rootView)
                        }
                        steps.add("✅ Decor view: ${decorView.javaClass.simpleName}")
                        (decorView.parent as? ViewGroup)?.removeView(decorView)
                        return Result(steps, decorView)
                    }
                }
                steps.add("⚠️ No window/decorView")
            } catch (e: Exception) {
                steps.add("⚠️ getDecorView: ${e.cause?.message ?: e.message}")
            }

            return Result(steps)

        } catch (e: Exception) {
            steps.add("❌ ${e.javaClass.simpleName}: ${e.message?.take(100)}")
            Log.e(TAG, "Load failed", e)
            return Result(steps)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeApkScreen(packageName: String, activityName: String, appName: String) {
    val activity = WestlakeActivity.instance ?: return
    var result by remember { mutableStateOf<NativeApkLoader.Result?>(null) }

    LaunchedEffect(packageName) {
        result = NativeApkLoader.load(activity, packageName, activityName)
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(appName, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { activity.showHome() }) {
                            Text("←", fontSize = 20.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE91E63),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                result?.let { r ->
                    // Show diagnostic steps
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(r.steps.size) { i ->
                            Text(r.steps[i], fontSize = 12.sp,
                                color = when {
                                    r.steps[i].startsWith("❌") -> Color.Red
                                    r.steps[i].startsWith("⚠") -> Color.Yellow
                                    else -> Color.White.copy(0.8f)
                                })
                        }
                    }

                    // Display the APK's content View
                    r.contentView?.let { view ->
                        AndroidView(
                            factory = {
                                (view.parent as? ViewGroup)?.removeView(view)
                                view
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                } ?: CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp),
                    color = Color(0xFFE91E63)
                )
            }
        }
    }
}
