package com.zerotrace.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.BatteryManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Utility object for battery-related operations including percentage checking,
 * low battery warnings, and battery status dialogs.
 */
object BatteryUtils {

    /**
     * Returns the current battery percentage (0-100), or -1 if unavailable
     */
    fun getBatteryPercentage(context: Context): Int {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        
        return if (level >= 0 && scale > 0) {
            (level * 100) / scale
        } else {
            -1
        }
    }

    /**
     * Checks if battery level is sufficient for operation
     * @param context Application context
     * @param minPercent Minimum required battery percentage (default: 50)
     * @param showMessage Whether to show UI feedback (default: true)
     * @return true if battery is sufficient, false otherwise
     */
    fun isBatterySufficient(context: Context, minPercent: Int = 50, showMessage: Boolean = true): Boolean {
        val batteryPct = getBatteryPercentage(context)

        return if (batteryPct > minPercent) {
            if (showMessage) {
                showBatterySufficientMessage(context, batteryPct)
            }
            true
        } else {
            if (showMessage) {
                showLowBatteryDialog(context, batteryPct, minPercent)
            }
            false
        }
    }

    /**
     * Shows a professional success message when battery is sufficient
     */
    private fun showBatterySufficientMessage(context: Context, batteryPct: Int) {
        val activity = context as? Activity ?: return
        
        activity.runOnUiThread {
            val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
            
            // Create snackbar container
            val snackbarContainer = FrameLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM
                    setMargins(32, 0, 32, 64)
                }
            }

            // Create card for snackbar
            val snackbarCard = CardView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                radius = 12f
                cardElevation = 6f
                setCardBackgroundColor(Color.parseColor("#4CAF50"))
            }

            // Create content layout
            val contentLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(48, 32, 48, 32)
            }

            // Icon
            val icon = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                    rightMargin = 32
                }
                setImageResource(android.R.drawable.ic_lock_idle_charging)
                setColorFilter(Color.WHITE)
            }

            // Text
            val text = TextView(context).apply {
                text = "Battery level: $batteryPct%"
                textSize = 15f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            contentLayout.addView(icon)
            contentLayout.addView(text)
            snackbarCard.addView(contentLayout)
            snackbarContainer.addView(snackbarCard)

            // Add to root view
            rootView.addView(snackbarContainer)

            // Animate and remove after delay
            snackbarContainer.alpha = 0f
            snackbarContainer.translationY = 100f
            snackbarContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start()

            snackbarContainer.postDelayed({
                snackbarContainer.animate()
                    .alpha(0f)
                    .translationY(100f)
                    .setDuration(300)
                    .withEndAction {
                        rootView.removeView(snackbarContainer)
                    }
                    .start()
            }, 2500)
        }
    }

    /**
     * Shows a professional warning dialog modal when battery is low
     */
    private fun showLowBatteryDialog(context: Context, batteryPct: Int, minPercent: Int) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createLowBatteryDialogView(context, batteryPct, minPercent)
        
        builder.setView(dialogView)
        builder.setCancelable(false)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set up button click listener to dismiss dialog
        val button = dialogView.findViewById<android.widget.Button>(2001)
        button.setOnClickListener { dialog.dismiss() }
        
        dialog.show()
    }

    /**
     * Creates the custom low battery dialog view programmatically
     */
    private fun createLowBatteryDialogView(context: Context, batteryPct: Int, minPercent: Int): View {
        val container = CardView(context).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(48, 48, 48, 48)
            }
            radius = 16f
            cardElevation = 8f
            setCardBackgroundColor(Color.WHITE)
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                gravity = Gravity.CENTER
                bottomMargin = 24
            }
            setImageResource(android.R.drawable.ic_dialog_alert)
            setColorFilter(Color.parseColor("#F44336"))
        }

        val batteryDisplay = TextView(context).apply {
            text = "$batteryPct%"
            textSize = 36f
            setTextColor(Color.parseColor("#F44336"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val title = TextView(context).apply {
            text = "Low Battery Warning"
            textSize = 20f
            setTextColor(Color.parseColor("#212121"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val message = TextView(context).apply {
            text = "Your battery is below the required $minPercent% threshold. Please charge your device before continuing to ensure uninterrupted operation."
            textSize = 14f
            setTextColor(Color.parseColor("#757575"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
        }

        val button = android.widget.Button(context).apply {
            id = 2001
            text = "OK, I'll Charge"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#F44336"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            ).apply {
                setMargins(0, 16, 0, 0)
            }
            setPadding(48, 24, 48, 24)
            isAllCaps = false
        }

        layout.addView(icon)
        layout.addView(batteryDisplay)
        layout.addView(title)
        layout.addView(message)
        layout.addView(button)
        
        container.addView(layout)
        return container
    }
}