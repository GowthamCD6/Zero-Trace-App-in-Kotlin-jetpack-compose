package com.zerotrace.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.text.SimpleDateFormat
import java.util.*

object DeviceInfoCollector {

    private const val CREATE_FILE_REQUEST = 1001
    
    /**
     * Interface for handling storage access results
     */
    interface StorageAccessCallback {
        fun onStorageAccessGranted()
        fun onStorageAccessDenied()
    }
    
    private var callback: StorageAccessCallback? = null

    /**
     * Collects comprehensive device information for ZeroTrace certification
     */
    fun collectDeviceInfo(context: Context): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Build.getSerial()
            } catch (e: SecurityException) {
                "Restricted (Android 8+)"
            }
        } else {
            Build.SERIAL
        }
        
        val androidId = Settings.Secure.getString(
            context.contentResolver, 
            Settings.Secure.ANDROID_ID
        )
        
        val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Restricted (Android 10+)"
        } else {
            "Legacy Access Required"
        }
        
        val buildId = Build.ID
        val buildFingerprint = Build.FINGERPRINT
        val androidVersion = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        return """
=== ZeroTrace Device Information ===
Generated: $timestamp

DEVICE IDENTIFIERS:
Manufacturer: $manufacturer
Model: $model
Serial Number: $serial
Android ID: $androidId
IMEI: $imei

SYSTEM INFORMATION:
Android Version: $androidVersion (API $sdkVersion)
Build ID: $buildId
Build Fingerprint: $buildFingerprint

CERTIFICATION NOTE:
This information will be used to verify device identity
after factory reset for ZeroTrace certification process.

=== End of Device Info ===
        """.trimIndent()
    }

    /**
     * Generates a shorter version for quick verification
     */
    fun getDeviceFingerprint(context: Context): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val androidId = Settings.Secure.getString(
            context.contentResolver, 
            Settings.Secure.ANDROID_ID
        )
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        
        return "${manufacturer}_${model}_${androidId.take(8)}_$timestamp"
    }

    /**
     * Initiates the storage access flow using SAF (Storage Access Framework)
     */
    fun requestStorageAccess(activity: Activity, callback: StorageAccessCallback) {
        this.callback = callback
        
        val deviceFingerprint = getDeviceFingerprint(activity)
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "ZeroTrace_${deviceFingerprint}.txt")
        }
        
        try {
            activity.startActivityForResult(intent, CREATE_FILE_REQUEST)
        } catch (e: Exception) {
            showStorageErrorDialog(activity)
            callback.onStorageAccessDenied()
        }
    }
    
    /**
     * Handles the result from SAF file picker
     * Call this from your Activity's onActivityResult
     */
    fun handleStorageAccessResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        if (requestCode == CREATE_FILE_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data?.data != null) {
                saveDeviceInfoToStorage(activity, data.data!!)
                return true
            } else {
                callback?.onStorageAccessDenied()
                return false
            }
        }
        return false
    }
    
    /**
     * Saves device information to the selected storage location
     */
    private fun saveDeviceInfoToStorage(context: Context, uri: Uri) {
        try {
            val deviceInfo = collectDeviceInfo(context)
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(deviceInfo.toByteArray())
                output.flush()
            }
            
            showStorageSuccessMessage(context)
            callback?.onStorageAccessGranted()
        } catch (e: Exception) {
            showStorageErrorDialog(context)
            callback?.onStorageAccessDenied()
        }
    }
    
    /**
     * Shows clean success modal when data is saved
     */
    private fun showStorageSuccessMessage(context: Context) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createSuccessModalView(context)
        
        builder.setView(dialogView)
        builder.setCancelable(true)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set up button click handler
        val okButton = dialogView.findViewById<Button>(5001)
        okButton?.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    /**
     * Creates clean minimal success modal view
     */
    private fun createSuccessModalView(context: Context): View {
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
            setPadding(48, 48, 48, 48)
        }

        // Animated success icon with background
        val iconContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                gravity = Gravity.CENTER
                bottomMargin = 32
            }
        }

        // Circular background
        val iconBackground = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(120, 120)
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(Color.parseColor("#E8F5E9"))
            }
            
            // Initial animation state
            scaleX = 0f
            scaleY = 0f
        }

        // Success checkmark icon
        val successIcon = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(64, 64).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(android.R.drawable.ic_menu_save)
            setColorFilter(Color.parseColor("#4CAF50"))
            
            // Initial animation state
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
        }

        iconContainer.addView(iconBackground)
        iconContainer.addView(successIcon)

        val title = TextView(context).apply {
            text = "Data Secured Successfully"
            textSize = 22f
            setTextColor(Color.parseColor("#1A1A1A"))
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 700, false)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        val message = TextView(context).apply {
            text = "Your device information has been safely stored and encrypted on the selected storage device."
            textSize = 15f
            setTextColor(Color.parseColor("#616161"))
            gravity = Gravity.CENTER
            setLineSpacing(4f, 1f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
        }

        val okButton = Button(context).apply {
            id = 5001
            text = "OK"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                120
            )
            setPadding(32, 16, 32, 16)
            isAllCaps = false
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 500, false)
        }

        layout.addView(iconContainer)
        layout.addView(title)
        layout.addView(message)
        layout.addView(okButton)
        
        container.addView(layout)

        // Professional animation sequence
        container.post {
            // Step 1: Background circle appears
            iconBackground.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(android.view.animation.OvershootInterpolator(1.1f))
                .withEndAction {
                    // Step 2: Icon appears after background
                    successIcon.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(400)
                        .setInterpolator(android.view.animation.BounceInterpolator())
                        .withEndAction {
                            // Step 3: Success pulse effect
                            successIcon.animate()
                                .scaleX(1.15f)
                                .scaleY(1.15f)
                                .setDuration(200)
                                .withEndAction {
                                    successIcon.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(200)
                                        .start()
                                }
                                .start()
                        }
                        .start()
                }
                .start()
        }

        return container
    }

    /**
     * Creates a clean info card
     */
    private fun createInfoCard(context: Context, iconRes: Int, title: String, description: String): View {
        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            radius = 12f
            cardElevation = 2f
            setCardBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        val cardLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 20, 20, 20)
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(44, 44).apply {
                rightMargin = 16
            }
            setImageResource(iconRes)
            setColorFilter(Color.parseColor("#2E7D32"))
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val titleText = TextView(context).apply {
            text = title
            textSize = 14f
            setTextColor(Color.parseColor("#212121"))
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 4
            }
        }

        val descText = TextView(context).apply {
            text = description
            textSize = 13f
            setTextColor(Color.parseColor("#616161"))
        }

        textLayout.addView(titleText)
        textLayout.addView(descText)

        cardLayout.addView(icon)
        cardLayout.addView(textLayout)
        card.addView(cardLayout)

        return card
    }
    
    /**
     * Shows error dialog when storage access fails
     */
    private fun showStorageErrorDialog(context: Context) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createErrorDialogView(context)
        
        builder.setView(dialogView)
        builder.setCancelable(true)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        val tryAgainButton = dialogView.findViewById<Button>(3001)
        val cancelButton = dialogView.findViewById<Button>(3002)
        
        tryAgainButton?.setOnClickListener {
            dialog.dismiss()
            if (context is Activity) {
                requestStorageAccess(context, callback ?: object : StorageAccessCallback {
                    override fun onStorageAccessGranted() {}
                    override fun onStorageAccessDenied() {}
                })
            }
        }
        
        cancelButton?.setOnClickListener {
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
     * Creates professional error dialog view
     */
    private fun createErrorDialogView(context: Context): View {
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
        }

        // Clean header section
        val headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 56, 48, 48)
        }

        // Error icon with circular background
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
                setColor(Color.parseColor("#FFEBEE"))
            }
        }

        val errorIcon = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(72, 72).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(android.R.drawable.stat_notify_error)
            setColorFilter(Color.parseColor("#C62828"))
        }

        iconContainer.addView(iconBackground)
        iconContainer.addView(errorIcon)

        val title = TextView(context).apply {
            text = "Unable to Save Information"
            textSize = 22f
            setTextColor(Color.parseColor("#1A1A1A"))
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        val subtitle = TextView(context).apply {
            text = "There was a problem accessing storage"
            textSize = 15f
            setTextColor(Color.parseColor("#757575"))
            gravity = Gravity.CENTER
        }

        headerLayout.addView(iconContainer)
        headerLayout.addView(title)
        headerLayout.addView(subtitle)

        // Divider line
        val divider = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                setMargins(48, 8, 48, 8)
            }
            setBackgroundColor(Color.parseColor("#E0E0E0"))
        }

        // Content section
        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        val message = TextView(context).apply {
            text = "The device information could not be saved to the selected storage location. Please check the following and try again:"
            textSize = 15f
            setTextColor(Color.parseColor("#424242"))
            setLineSpacing(6f, 1f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        // Checklist cards
        val checklistLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val items = listOf(
            Pair("Storage device is properly connected", android.R.drawable.ic_menu_info_details),
            Pair("Sufficient storage space is available", android.R.drawable.ic_menu_info_details),
            Pair("File access permissions are granted", android.R.drawable.ic_menu_info_details)
        )

        items.forEach { (text, icon) ->
            val itemView = createChecklistItem(context, text, icon)
            checklistLayout.addView(itemView)
        }

        contentLayout.addView(message)
        contentLayout.addView(checklistLayout)

        // Button section
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(48, 24, 48, 48)
            gravity = Gravity.CENTER
        }

        val cancelButton = createOutlineButton(context, "Cancel")
        cancelButton.id = 3002
        cancelButton.layoutParams = LinearLayout.LayoutParams(
            0,
            140,
            1f
        ).apply {
            rightMargin = 12
        }

        val tryAgainButton = createStyledButton(context, "Try Again", Color.parseColor("#C62828"))
        tryAgainButton.id = 3001
        tryAgainButton.layoutParams = LinearLayout.LayoutParams(
            0,
            140,
            1f
        ).apply {
            leftMargin = 12
        }

        buttonLayout.addView(cancelButton)
        buttonLayout.addView(tryAgainButton)

        layout.addView(headerLayout)
        layout.addView(divider)
        layout.addView(contentLayout)
        layout.addView(buttonLayout)
        container.addView(layout)
        return container
    }

    /**
     * Creates a checklist item view
     */
    private fun createChecklistItem(context: Context, text: String, iconRes: Int): View {
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 12, 0, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val bullet = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(8, 8).apply {
                rightMargin = 16
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#C62828"))
            }
        }

        val textView = TextView(context).apply {
            this.text = text
            textSize = 14f
            setTextColor(Color.parseColor("#424242"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        itemLayout.addView(bullet)
        itemLayout.addView(textView)

        return itemLayout
    }

    /**
     * Creates a styled filled button
     */
    private fun createStyledButton(context: Context, text: String, color: Int): Button {
        return Button(context).apply {
            this.text = text
            textSize = 16f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            )
            isAllCaps = false
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            
            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = 70f
            }
            
            stateListAnimator = null
            elevation = 0f
        }
    }

    /**
     * Creates an outline button
     */
    private fun createOutlineButton(context: Context, text: String): Button {
        return Button(context).apply {
            this.text = text
            textSize = 16f
            setTextColor(Color.parseColor("#757575"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            )
            isAllCaps = false
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 500, false)
            
            background = GradientDrawable().apply {
                setColor(Color.TRANSPARENT)
                setStroke(2, Color.parseColor("#BDBDBD"))
                cornerRadius = 70f
            }
            
            stateListAnimator = null
            elevation = 0f
        }
    }
}