package com.zerotrace.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import java.io.File

object ExternalIn {

    /**
     * Detects if either a microSD card or USB OTG drive is connected & mounted.
     * Supports old and new Android versions.
     */
    fun checkExternalStorage(context: Context, showToast: Boolean = true): Boolean {
        var isExternalConnected = false

        // 1. For Android Nougat and above → StorageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageVolumes = storageManager.storageVolumes
            for (volume in storageVolumes) {
                if (volume.isRemovable && volume.state == Environment.MEDIA_MOUNTED) {
                    isExternalConnected = true
                    break
                }
            }
        } else {
            // 2. For older versions → getExternalFilesDirs
            val externalDirs: Array<File?> = context.getExternalFilesDirs(null)
            for (file in externalDirs) {
                if (file != null && Environment.isExternalStorageRemovable(file)) {
                    val state = Environment.getExternalStorageState(file)
                    if (state == Environment.MEDIA_MOUNTED) {
                        isExternalConnected = true
                        break
                    }
                }
            }
        }

        // 3. Fallback → check if a USB device is connected (not always mounted as storage)
        if (!isExternalConnected) {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            if (usbManager.deviceList.isNotEmpty()) {
                isExternalConnected = true
            }
        }

        if (showToast) {
            if (isExternalConnected) {
                showSuccessSnackbar(context, "External storage detected")
            } else {
                showWarningDialog(context)
            }
        }

        return isExternalConnected
    }

    /**
     * Shows a professional success message as a bottom snackbar with icon
     */
    private fun showSuccessSnackbar(context: Context, message: String) {
        try {
            val activity = context as? androidx.appcompat.app.AppCompatActivity
            val rootView: View? = activity?.findViewById(android.R.id.content)

            if (rootView != null) {
                val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
                val snackbarView = snackbar.view

                // Set background color - professional green
                snackbarView.setBackgroundColor(Color.parseColor("#4CAF50"))

                // Get the TextView and customize it
                val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                textView.textSize = 14f
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_dialog_info, 0, 0, 0
                )
                textView.compoundDrawablePadding = 16

                snackbar.show()
            } else {
                // ✅ Fallback if not in an Activity (like ApplicationContext)
                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows a professional warning dialog modal when external storage is not detected
     */
    private fun showWarningDialog(context: Context) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createWarningDialogView(context)
        
        builder.setView(dialogView)
        builder.setCancelable(true)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set up button click listener to dismiss dialog
        val button = dialogView.findViewById<android.widget.Button>(1001)
        button.setOnClickListener { dialog.dismiss() }
        
        dialog.show()
    }

    /**
     * Creates the custom warning dialog view programmatically
     */
    private fun createWarningDialogView(context: Context): View {
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
            setPadding(48, 48, 48, 48)
        }

        val layout = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val icon = ImageView(context).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(120, 120).apply {
                gravity = Gravity.CENTER
                bottomMargin = 24
            }
            setImageResource(android.R.drawable.ic_dialog_alert)
            setColorFilter(Color.parseColor("#FF9800"))
        }

        val title = TextView(context).apply {
            text = "External Storage Required"
            textSize = 20f
            setTextColor(Color.parseColor("#212121"))
            gravity = Gravity.CENTER
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val message = TextView(context).apply {
            text = "Please connect a USB OTG drive or insert a microSD card to continue."
            textSize = 14f
            setTextColor(Color.parseColor("#757575"))
            gravity = Gravity.CENTER
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
        }

        val button = android.widget.Button(context).apply {
            id = 1001
            text = "OK"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2196F3"))
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                140
            ).apply {
                setMargins(0, 16, 0, 0)
            }
            setPadding(48, 24, 48, 24)
            isAllCaps = false
        }

        layout.addView(icon)
        layout.addView(title)
        layout.addView(message)
        layout.addView(button)
        
        container.addView(layout)
        return container
    }
}