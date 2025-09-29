package com.zerotrace.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object BatteryUtils {

    // Returns battery percentage (0-100), or -1 if unavailable
    fun getBatteryPercentage(context: Context): Int {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) {
            (level * 100) / scale
        } else {
            -1
        }
    }

    // Returns true if battery > minPercent, else shows modal
    fun isBatterySufficient(context: Context, minPercent: Int = 50): Boolean {
        val batteryPct = getBatteryPercentage(context)

        return if (batteryPct > minPercent) {
            true
        } else {
            showLowBatteryDialog(context, batteryPct)
            false
        }
    }

    // Show modal when battery is low
    private fun showLowBatteryDialog(context: Context, batteryPct: Int) {
        AlertDialog.Builder(context)
            .setTitle("Low Battery ⚠️")
            .setMessage("Your battery is at $batteryPct%. Please charge your device before continuing.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
