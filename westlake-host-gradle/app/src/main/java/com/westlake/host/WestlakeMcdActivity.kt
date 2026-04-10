package com.westlake.host

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A real Activity (shown on screen via WindowManager) that inflates
 * the MCD app's real layout using MCD's Resources.
 *
 * This proves the Westlake engine can render real MCD UI using the
 * real Android framework — same approach works on OHOS.
 */
class WestlakeMcdActivity : Activity() {
    companion object {
        private const val TAG = "WestlakeMcd"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate — inflating real MCD layout")

        try {
            // Get MCD's context with real Resources
            val mcdCtx = createPackageContext("com.mcdonalds.app",
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
            Log.i(TAG, "MCD resources: ${mcdCtx.resources}")

            // Get MCD's layout inflater (uses MCD's Resources for layout/drawable resolution)
            val mcdInflater = LayoutInflater.from(mcdCtx)

            // Inflate the real MCD splash layout
            val layoutId = mcdCtx.resources.getIdentifier(
                "activity_splash_screen", "layout", "com.mcdonalds.app")
            Log.i(TAG, "Layout ID: 0x${Integer.toHexString(layoutId)}")

            if (layoutId != 0) {
                // Apply MCD's theme so resource references (drawables, colors) resolve correctly
                val themeId = mcdCtx.resources.getIdentifier("AppTheme", "style", "com.mcdonalds.app")
                if (themeId != 0) {
                    mcdCtx.setTheme(themeId)
                    Log.i(TAG, "MCD theme applied: 0x${Integer.toHexString(themeId)}")
                }

                val mcdView = mcdInflater.inflate(layoutId, null)

                // Also try inflating the home dashboard into the fragment container
                val dashId = mcdCtx.resources.getIdentifier("activity_home_dashboard", "layout", "com.mcdonalds.app")
                if (dashId != 0) {
                    val fragContainer = mcdView.findViewById<ViewGroup>(0x7f0b17b3)  // fragment container
                    if (fragContainer != null) {
                        try {
                            val dashView = mcdInflater.inflate(dashId, fragContainer, false)
                            fragContainer.addView(dashView)
                            Log.i(TAG, "Dashboard layout inflated into fragment container!")
                        } catch (e: Throwable) { Log.w(TAG, "Dashboard inflate: ${e.message}") }
                    }
                }

                setContentView(mcdView)
                Log.i(TAG, "Real MCD layout inflated and set!")
                dumpViewTree(mcdView, 0)
            } else {
                // Fallback: try base_layout or activity_home_dashboard
                val altIds = listOf(
                    "base_layout", "activity_home_dashboard", "activity_main"
                )
                for (name in altIds) {
                    val id = mcdCtx.resources.getIdentifier(name, "layout", "com.mcdonalds.app")
                    if (id != 0) {
                        val view = mcdInflater.inflate(id, null)
                        setContentView(view)
                        Log.i(TAG, "Inflated $name!")
                        break
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error: ${e.message}", e)
            // Show error on screen
            val tv = android.widget.TextView(this)
            tv.text = "Westlake Engine\n\nError: ${e.message}"
            tv.textSize = 16f
            tv.setPadding(32, 64, 32, 32)
            setContentView(tv)
        }
    }

    private fun dumpViewTree(view: View, depth: Int) {
        val indent = "  ".repeat(depth)
        val id = if (view.id != View.NO_ID) "0x${Integer.toHexString(view.id)}" else ""
        Log.i(TAG, "$indent${view.javaClass.simpleName} $id")
        if (view is ViewGroup && depth < 4) {
            for (i in 0 until view.childCount) {
                dumpViewTree(view.getChildAt(i), depth + 1)
            }
        }
    }
}
