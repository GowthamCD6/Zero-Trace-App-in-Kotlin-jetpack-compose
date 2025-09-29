package com.zerotrace.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import java.util.*

object BackgroundAppChecker {
    fun checkBackgroundApps(context: Context) {
        if (!hasUsageStatsPermission(context)) {
            requestUsageStatsPermission(context)
            return
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 5 // Last 5 minutes

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        // Filter apps used in the last 5 minutes (excluding your own app)
        val runningApps = stats.filter { it.lastTimeUsed > startTime && it.packageName != context.packageName }
            .map { it.packageName }
        if (runningApps.isEmpty()) {
            Toast.makeText(context, "No external apps running recently.", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(context)
                .setTitle("Background Apps Detected")
                .setMessage("The following apps were running in the background:\n\n${runningApps.joinToString("\n")}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission(context: Context) {
        Toast.makeText(context, "Please grant Usage Access permission", Toast.LENGTH_LONG).show()
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
}