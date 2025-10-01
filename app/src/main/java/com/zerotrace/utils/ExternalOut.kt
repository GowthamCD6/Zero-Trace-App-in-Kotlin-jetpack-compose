package com.zerotrace.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.io.File

object ExternalOut {

    /**
     * Checks if USB OTG or microSD is connected.
     * Shows a success message if none, warning if present.
     */
    fun checkExternalStorage(context: Context) {
        val isExternalConnected = isExternalStorageConnected(context)

        if (isExternalConnected) {
            // Show warning - external storage detected
            showWarningDialog(context, "External storage detected! Please remove it for safety.")
        } else {
            // Show success - no external storage found
            showSuccessMessage(context, "No external storage detected")
        }
    }

    /**
     * Checks if external storage has been safely removed for Step 7
     * Returns true if no external storage is detected (safe to proceed)
     */
    fun isExternalStorageRemoved(context: Context): Boolean {
        val isExternalConnected = isExternalStorageConnected(context)
        
        if (isExternalConnected) {
            // External storage still connected - show warning
            showWarningDialog(context, "Please remove USB drive before proceeding with wipe!")
            return false
        } else {
            // No external storage - safe to proceed
            showSuccessMessage(context, "USB drive safely removed")
            return true
        }
    }

    /**
     * Detects removable storage (microSD / USB OTG)
     */
    private fun isExternalStorageConnected(context: Context): Boolean {
        // Android N+ → StorageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageVolumes = storageManager.storageVolumes
            for (volume in storageVolumes) {
                if (volume.isRemovable && volume.state == Environment.MEDIA_MOUNTED) {
                    return true
                }
            }
        } else {
            // Older versions → getExternalFilesDirs
            val externalDirs: Array<File?> = context.getExternalFilesDirs(null)
            for (file in externalDirs) {
                if (file != null && Environment.isExternalStorageRemovable(file)) {
                    val state = Environment.getExternalStorageState(file)
                    if (state == Environment.MEDIA_MOUNTED) {
                        return true
                    }
                }
            }
        }

        return false
    }

    /**
     * Shows professional success message when USB is safely removed
     */
    private fun showSuccessMessage(context: Context, message: String) {
        val activity = context as? Activity ?: return
        
        activity.runOnUiThread {
            val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
            
            val snackbarContainer = FrameLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM
                    setMargins(24, 0, 24, 80)
                }
            }

            val snackbarCard = CardView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                radius = 16f
                cardElevation = 8f
                setCardBackgroundColor(Color.parseColor("#4CAF50"))
            }

            val contentLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(40, 28, 40, 28)
            }

            val icon = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(56, 56).apply {
                    rightMargin = 24
                }
                setImageResource(android.R.drawable.checkbox_on_background)
                setColorFilter(Color.WHITE)
            }

            val text = TextView(context).apply {
                text = message
                textSize = 15f
                setTextColor(Color.WHITE)
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 500, false)
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
            rootView.addView(snackbarContainer)

            snackbarContainer.alpha = 0f
            snackbarContainer.translationY = 100f
            snackbarContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
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
            }, 3000)
        }
    }

    /**
     * Shows professional warning dialog modal when external storage is detected
     */
    private fun showWarningDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createWarningDialogView(context, message)
        
        builder.setView(dialogView)
        builder.setCancelable(true)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set up button click listener to dismiss dialog
        val button = dialogView.findViewById<Button>(2001)
        button?.setOnClickListener { 
            dialog.dismiss() 
        }
        
        dialog.show()
        
        // Subtle fade-in animation
        dialogView.alpha = 0f
        dialogView.animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    /**
     * Creates professional warning dialog view (styled like ExternalIn)
     */
    private fun createWarningDialogView(context: Context, message: String): View {
        val container = CardView(context).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(48, 48, 48, 48)
            }
            radius = 20f
            cardElevation = 12f
            setCardBackgroundColor(Color.WHITE)
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(56, 56, 56, 56)
        }

        // Warning icon with circular background
        val iconContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                gravity = Gravity.CENTER
                bottomMargin = 28
            }
        }

        val iconBackground = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(120, 120)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#FFF3E0"))
            }
        }

        val icon = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(72, 72).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(android.R.drawable.ic_dialog_alert)
            setColorFilter(Color.parseColor("#FF9800"))
        }

        iconContainer.addView(iconBackground)
        iconContainer.addView(icon)

        // Title
        val title = TextView(context).apply {
            text = "External Storage Detected"
            textSize = 22f
            setTextColor(Color.parseColor("#1A1A1A"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
        }

        // Message
        val messageText = TextView(context).apply {
            text = message
            textSize = 15f
            setTextColor(Color.parseColor("#424242"))
            gravity = Gravity.CENTER
            setLineSpacing(6f, 1f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 36
            }
        }

        // Button
        val button = Button(context).apply {
            id = 2001
            text = "OK"
            textSize = 16f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            )
            setPadding(48, 24, 48, 24)
            isAllCaps = false
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#FF9800"))
                cornerRadius = 70f
            }
            
            stateListAnimator = null
            elevation = 0f
        }

        layout.addView(iconContainer)
        layout.addView(title)
        layout.addView(messageText)
        layout.addView(button)
        
        container.addView(layout)
        return container
    }
}