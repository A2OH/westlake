package com.westlake.host

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Westlake VM Display — spawns dalvikvm as subprocess,
 * displays rendered PNG output, forwards touch input.
 *
 * Pipeline:
 *   dalvikvm → OHBridge.surfaceFlush() → /sdcard/westlake_frame.png
 *   This screen → polls PNG → displays via Compose Image
 *   This screen → touch event → /sdcard/westlake_touch.dat → dalvikvm reads
 */
object WestlakeVM {
    private const val TAG = "WestlakeVM"
    // Use app's external files dir (accessible by both app and subprocess)
    val PNG_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_frame.png"
    val TOUCH_PATH: String get() = WestlakeActivity.instance?.getExternalFilesDir(null)?.absolutePath + "/westlake_touch.dat"
    private const val DALVIKVM_DIR = "/data/local/tmp/westlake"

    var process: Process? = null
    var touchSeq = 0

    fun start(): List<String> {
        val log = mutableListOf<String>()
        val activity = WestlakeActivity.instance ?: return listOf("No activity")

        // Kill any existing
        process?.destroyForcibly()
        try { File(PNG_PATH).delete() } catch (_: Exception) {}

        // Copy dalvikvm to app's private dir (SELinux allows execute there)
        val vmDir = File(activity.filesDir, "vm").apply { mkdirs() }
        val srcDir = File(DALVIKVM_DIR)
        for (name in listOf("dalvikvm","core-oj.jar","core-libart.jar","core-icu4j.jar","aosp-shim.dex","app.dex")) {
            val src = File(srcDir, name)
            val dst = File(vmDir, name)
            if (src.exists() && (!dst.exists() || dst.length() != src.length())) {
                src.copyTo(dst, overwrite = true)
                if (name == "dalvikvm") dst.setExecutable(true, false)
                log.add("Copied $name")
            }
        }

        val dvm = "${vmDir.absolutePath}/dalvikvm"
        val bcp = "${vmDir}/core-oj.jar:${vmDir}/core-libart.jar:${vmDir}/core-icu4j.jar:${vmDir}/aosp-shim.dex"
        val cmd = arrayOf(dvm, "-Xbootclasspath:$bcp", "-Xnoimage-dex2oat", "-Xverify:none",
            "-classpath", "${vmDir}/app.dex", "com.example.mockdonalds.MockDonaldsApp")

        log.add("Starting from ${vmDir.absolutePath}...")
        try {
            val pb = ProcessBuilder(*cmd)
            pb.directory(vmDir)
            pb.environment()["ANDROID_DATA"] = vmDir.absolutePath
            pb.environment()["ANDROID_ROOT"] = vmDir.absolutePath
            pb.environment()["WESTLAKE_FRAME"] = PNG_PATH
            pb.environment()["WESTLAKE_TOUCH"] = TOUCH_PATH
            pb.redirectErrorStream(true)
            process = pb.start()
            log.add("VM process started")

            // Read first few lines of output in background
            Thread {
                try {
                    val reader = process!!.inputStream.bufferedReader()
                    var line = reader.readLine()
                    var count = 0
                    while (line != null && count < 100) {
                        if (!line.contains("nullptr") && !line.contains("ziparchive") &&
                            !line.contains("hidden_api") && !line.contains("DexFile") &&
                            line.isNotBlank()) {
                            Log.i(TAG, "VM: $line")
                        }
                        line = reader.readLine()
                        count++
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Reader error: $e")
                }
            }.start()

        } catch (e: Exception) {
            log.add("Failed: ${e.message}")
            Log.e(TAG, "Start failed", e)
        }

        return log
    }

    fun sendTouch(action: Int, x: Float, y: Float) {
        try {
            touchSeq++
            val buf = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
            buf.putInt(action) // 0=DOWN, 1=UP, 2=MOVE
            buf.putInt(x.toInt())
            buf.putInt(y.toInt())
            buf.putInt(touchSeq)
            FileOutputStream(TOUCH_PATH).use { it.write(buf.array()) }
        } catch (e: Exception) {
            Log.w(TAG, "Touch write: $e")
        }
    }

    fun stop() {
        process?.destroyForcibly()
        process = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WestlakeVMScreen() {
    val activity = WestlakeActivity.instance ?: return
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf(listOf("Waiting...")) }
    var frameBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var frameCount by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    // Start VM
    LaunchedEffect(Unit) {
        logs = WestlakeVM.start()
        isRunning = true
    }

    // Poll for PNG frames
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(200) // 5fps polling
            try {
                val file = File(WestlakeVM.PNG_PATH)
                if (file.exists() && file.length() > 100) {
                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                    if (bmp != null) {
                        frameBitmap = bmp
                        frameCount++
                    }
                }
            } catch (_: Exception) {}
        }
    }

    // Cleanup on leave
    DisposableEffect(Unit) {
        onDispose {
            isRunning = false
            WestlakeVM.stop()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1B5E20)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isRunning = false
                    WestlakeVM.stop()
                    activity.showHome()
                }) { Text("←", fontSize = 20.sp, color = Color.White) }
                Text("Westlake VM", fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Frame: $frameCount", fontSize = 12.sp, color = Color.White.copy(0.6f))
            }

            // Rendered frame display
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Scale touch to 480x800 virtual surface
                            val scaleX = 480f / size.width
                            val scaleY = 800f / size.height
                            val vx = offset.x * scaleX
                            val vy = offset.y * scaleY
                            WestlakeVM.sendTouch(1, vx, vy) // UP = click
                            Log.i("WestlakeVM", "Touch: (${vx.toInt()}, ${vy.toInt()})")
                        }
                    }
            ) {
                frameBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Westlake VM Output",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(16.dp))
                        Text("Westlake VM starting...", color = Color.White.copy(0.6f), fontSize = 14.sp)
                        Text("(interpreter mode — first boot is slow)", color = Color.White.copy(0.4f), fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        logs.takeLast(3).forEach { line ->
                            Text(line, fontSize = 11.sp, color = Color.White.copy(0.5f))
                        }
                    }
                }
            }

            // Status bar
            Text(
                "dalvikvm ARM64 | OHBridge → PNG → Compose | Touch → file → VM",
                fontSize = 10.sp, color = Color(0xFF4CAF50),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(4.dp)
            )
        }
    }
}
