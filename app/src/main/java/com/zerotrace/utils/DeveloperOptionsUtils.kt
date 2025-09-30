package com.zerotrace.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast

object DeveloperOptionsUtils {

    /** Check if Developer Options are enabled */
    fun isDeveloperOptionsEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    /** Open Developer Options screen (works for most devices) */
    fun openDeveloperOptionsScreen(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback → open general Settings if Dev Options not directly accessible
            try {
                val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (e2: Exception) {
                Toast.makeText(context, "Unable to open Settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Show correct behavior based on current state */
    fun handleDeveloperOptionsAction(context: Context) {
        if (isDeveloperOptionsEnabled(context)) {
            // Developer Options are ON → just open Developer Options screen
            openDeveloperOptionsScreen(context)
        } else {
            // Developer Options are OFF → guide user to enable it
            Toast.makeText(
                context,
                "Developer Options are OFF. Please enable them in settings.",
                Toast.LENGTH_LONG
            ).show()
            openDeveloperOptionsScreen(context)
        }
    }
}
