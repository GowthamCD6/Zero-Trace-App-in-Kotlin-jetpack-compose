package com.zerotrace.utils

import android.app.Activity
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.util.*

object BackgroundAppChecker {
    
    fun checkBackgroundApps(context: Context) {
        if (!hasUsageStatsPermission(context)) {
            showPermissionRequiredDialog(context)
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
            showNoAppsMessage(context)
        } else {
            showBackgroundAppsDialog(context, runningApps)
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

    /**
     * Shows permission request dialog - Professional enterprise style
     */
    private fun showPermissionRequiredDialog(context: Context) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createPermissionDialogView(context)
        
        builder.setView(dialogView)
        builder.setCancelable(true)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set up button click handler
        val grantButton = dialogView.findViewById<Button>(1001)
        val cancelButton = dialogView.findViewById<Button>(1002)
        
        grantButton?.setOnClickListener {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            dialog.dismiss()
        }
        
        cancelButton?.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    /**
     * Creates professional permission request dialog
     */
    private fun createPermissionDialogView(context: Context): View {
        val container = CardView(context).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(48, 48, 48, 48)
            }
            radius = 24f
            cardElevation = 16f
            setCardBackgroundColor(Color.WHITE)
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 0)
        }

        // Header with blue background
        val headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#1976D2"))
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(88, 88).apply {
                gravity = Gravity.CENTER
                bottomMargin = 20
            }
            setImageResource(android.R.drawable.ic_lock_lock)
            setColorFilter(Color.WHITE)
        }

        val title = TextView(context).apply {
            text = "Permission Required"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 700, false)
        }

        headerLayout.addView(icon)
        headerLayout.addView(title)

        // Content section
        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 40, 48, 40)
        }

        val message = TextView(context).apply {
            text = "To monitor background applications and ensure system integrity, ZeroTrace requires Usage Access permission.\n\nThis permission allows the app to:\n• Detect running applications\n• Monitor system activity\n• Enhance security measures"
            textSize = 15f
            setTextColor(Color.parseColor("#424242"))
            setLineSpacing(6f, 1f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
        }

        contentLayout.addView(message)

        // Button section
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(48, 0, 48, 48)
            gravity = Gravity.CENTER
        }

        val cancelButton = createOutlineButton(context, "Cancel")
        cancelButton.id = 1002
        cancelButton.layoutParams = LinearLayout.LayoutParams(
            0,
            140,
            1f
        ).apply {
            rightMargin = 16
        }

        val grantButton = createFilledButton(context, "Grant Permission", Color.parseColor("#1976D2"))
        grantButton.id = 1001
        grantButton.layoutParams = LinearLayout.LayoutParams(
            0,
            140,
            1f
        ).apply {
            leftMargin = 16
        }

        buttonLayout.addView(cancelButton)
        buttonLayout.addView(grantButton)

        layout.addView(headerLayout)
        layout.addView(contentLayout)
        layout.addView(buttonLayout)
        container.addView(layout)
        return container
    }

    /**
     * Shows success message when no apps are running
     */
    private fun showNoAppsMessage(context: Context) {
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
                setCardBackgroundColor(Color.parseColor("#2E7D32"))
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
                text = "System Secure - No background apps detected"
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
     * Shows professional background apps dialog
     */
    private fun showBackgroundAppsDialog(context: Context, packageNames: List<String>) {
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
        val dialogView = createBackgroundAppsDialogView(context, packageNames)
        
        builder.setView(dialogView)
        builder.setCancelable(true)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set up button click handler
        val acknowledgeButton = dialogView.findViewById<Button>(2001)
        acknowledgeButton?.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    /**
     * Creates professional enterprise-grade background apps dialog
     */
    private fun createBackgroundAppsDialogView(context: Context, packageNames: List<String>): View {
        val container = CardView(context).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(40, 60, 40, 60)
            }
            radius = 24f
            cardElevation = 16f
            setCardBackgroundColor(Color.WHITE)
        }

        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Header section with professional security theme
        val headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 40)
            setBackgroundColor(Color.parseColor("#1E3A8A"))
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(92, 92).apply {
                gravity = Gravity.CENTER
                bottomMargin = 20
            }
            setImageResource(android.R.drawable.stat_sys_warning)
            setColorFilter(Color.WHITE)
        }

        val title = TextView(context).apply {
            text = "Background Activity Detected"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 700, false)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        val subtitle = TextView(context).apply {
            text = "${packageNames.size} application${if (packageNames.size > 1) "s" else ""} running in background"
            textSize = 14f
            setTextColor(Color.parseColor("#DBEAFE"))
            gravity = Gravity.CENTER
        }

        headerLayout.addView(icon)
        headerLayout.addView(title)
        headerLayout.addView(subtitle)

        // Alert message section
        val alertSection = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 24)
            setBackgroundColor(Color.parseColor("#EFF6FF"))
        }

        val alertText = TextView(context).apply {
            text = "For optimal security and performance, it is recommended to close unnecessary background applications before proceeding."
            textSize = 14f
            setTextColor(Color.parseColor("#1E40AF"))
            setLineSpacing(4f, 1f)
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 500, false)
        }

        alertSection.addView(alertText)

        // Scrollable apps list section
        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isVerticalScrollBarEnabled = false
        }

        val appsLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        // Section title
        val listTitle = TextView(context).apply {
            text = "DETECTED APPLICATIONS"
            textSize = 12f
            setTextColor(Color.parseColor("#757575"))
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 0, 16, 16)
            }
            letterSpacing = 0.08f
        }
        appsLayout.addView(listTitle)

        // Add each app
        val pm = context.packageManager
        packageNames.forEachIndexed { index, packageName ->
            val appItem = createProfessionalAppItem(context, packageName, pm, index + 1)
            appsLayout.addView(appItem)
        }

        scrollView.addView(appsLayout)

        // Footer with action button
        val footerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 48)
        }

        val button = createFilledButton(context, "Acknowledge & Continue", Color.parseColor("#1E3A8A"))
        button.id = 2001

        footerLayout.addView(button)

        mainLayout.addView(headerLayout)
        mainLayout.addView(alertSection)
        mainLayout.addView(scrollView)
        mainLayout.addView(footerLayout)
        container.addView(mainLayout)
        return container
    }

    /**
     * Creates professional app list item with numbering
     */
    private fun createProfessionalAppItem(context: Context, packageName: String, pm: PackageManager, index: Int): View {
        val itemCard = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }
            radius = 12f
            cardElevation = 2f
            setCardBackgroundColor(Color.parseColor("#FAFAFA"))
        }

        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 18, 20, 18)
        }

        // Number badge
        val numberBadge = TextView(context).apply {
            text = index.toString()
            textSize = 14f
            setTextColor(Color.parseColor("#FFFFFF"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                rightMargin = 16
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#3B82F6"))
            }
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 700, false)
        }

        // App icon
        val appIcon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                rightMargin = 16
            }
            try {
                setImageDrawable(pm.getApplicationIcon(packageName))
            } catch (e: Exception) {
                setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        // App info layout
        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        // App name
        val appName = TextView(context).apply {
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                text = pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                text = packageName
            }
            textSize = 15f
            setTextColor(Color.parseColor("#212121"))
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 4
            }
        }

        // Package name
        val packageText = TextView(context).apply {
            text = packageName
            textSize = 11f
            setTextColor(Color.parseColor("#9E9E9E"))
        }

        textLayout.addView(appName)
        textLayout.addView(packageText)
        
        itemLayout.addView(numberBadge)
        itemLayout.addView(appIcon)
        itemLayout.addView(textLayout)
        
        itemCard.addView(itemLayout)
        return itemCard
    }

    /**
     * Creates a filled button with rounded corners
     */
    private fun createFilledButton(context: Context, text: String, color: Int): Button {
        return Button(context).apply {
            this.text = text
            textSize = 16f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                152
            )
            isAllCaps = false
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 600, false)
            
            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = 76f
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
                152
            )
            isAllCaps = false
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, 500, false)
            
            background = GradientDrawable().apply {
                setColor(Color.TRANSPARENT)
                setStroke(3, Color.parseColor("#E0E0E0"))
                cornerRadius = 76f
            }
            
            stateListAnimator = null
            elevation = 0f
        }
    }
}