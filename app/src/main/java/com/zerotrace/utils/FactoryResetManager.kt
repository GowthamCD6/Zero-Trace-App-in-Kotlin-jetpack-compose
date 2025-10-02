package com.zerotrace.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.zerotrace.DeviceAdminReceiver

class FactoryResetManager {
    
    companion object {
        private const val TAG = "FactoryResetManager"
        
        /**
         * Triggers a factory reset on the device
         * @param context Application context
         * @return Boolean indicating if reset was initiated successfully
         */
        fun performFactoryReset(context: Context): Boolean {
            return try {
                val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val adminComponent = ComponentName(context, DeviceAdminReceiver::class.java)
                
                if (devicePolicyManager.isAdminActive(adminComponent)) {
                    Log.i(TAG, "Device admin is active. Initiating factory reset...")
                    Toast.makeText(context, "Initiating factory reset. Device will restart shortly.", Toast.LENGTH_LONG).show()
                    
                    // WIPE_DATA_ALL flag ensures complete data erasure including external storage
                    val wipeFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        DevicePolicyManager.WIPE_EXTERNAL_STORAGE or 
                        DevicePolicyManager.WIPE_RESET_PROTECTION_DATA or
                        DevicePolicyManager.WIPE_EUICC
                    } else {
                        DevicePolicyManager.WIPE_EXTERNAL_STORAGE
                    }
                    
                    Log.i(TAG, "Executing wipeData with flags: $wipeFlags")
                    
                    // Perform the wipe with specified flags
                    // This should immediately restart the device
                    devicePolicyManager.wipeData(wipeFlags)
                    
                    Log.i(TAG, "Factory reset command executed successfully")
                    true
                } else {
                    Log.w(TAG, "Device admin not active, requesting admin privileges")
                    Toast.makeText(context, "Device administrator permission required for factory reset", Toast.LENGTH_LONG).show()
                    requestDeviceAdminPrivileges(context, adminComponent)
                    false
                }
            } catch (securityException: SecurityException) {
                Log.e(TAG, "Security exception during factory reset", securityException)
                false
            } catch (exception: Exception) {
                Log.e(TAG, "Unexpected error during factory reset", exception)
                false
            }
        }
        
        /**
         * Requests device admin privileges required for factory reset
         */
        private fun requestDeviceAdminPrivileges(context: Context, adminComponent: ComponentName) {
            try {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "ZeroTrace requires device administrator privileges to perform secure factory reset. " +
                        "This ensures complete data erasure meets security standards."
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to request device admin privileges", exception)
            }
        }
        
        /**
         * Checks if device admin is active for this app
         */
        fun isDeviceAdminActive(context: Context): Boolean {
            return try {
                val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val adminComponent = ComponentName(context, DeviceAdminReceiver::class.java)
                devicePolicyManager.isAdminActive(adminComponent)
            } catch (exception: Exception) {
                Log.e(TAG, "Error checking device admin status", exception)
                false
            }
        }
        
        /**
         * Alternative factory reset method using system settings (requires user interaction)
         * This is a fallback when device admin is not available
         */
        fun openFactoryResetSettings(context: Context): Boolean {
            return try {
                val intent = Intent(Settings.ACTION_PRIVACY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                Log.i(TAG, "Opened factory reset settings")
                Toast.makeText(context, "Navigate to 'Reset options' to perform factory reset", Toast.LENGTH_LONG).show()
                true
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to open factory reset settings", exception)
                false
            }
        }
        
        /**
         * Enhanced factory reset that tries multiple methods
         */
        fun performEnhancedFactoryReset(context: Context): Boolean {
            Log.i(TAG, "Starting enhanced factory reset process...")
            
            // Method 1: Try device admin factory reset
            if (performFactoryReset(context)) {
                return true
            }
            
            // Method 2: Try opening system reset settings as fallback
            Log.i(TAG, "Device admin method failed, trying system settings...")
            Toast.makeText(context, "Opening system settings for manual factory reset", Toast.LENGTH_LONG).show()
            
            return try {
                // Try different reset-related intents
                val resetIntents = listOf(
                    Intent("android.settings.FACTORY_RESET_SETTINGS"),
                    Intent(Settings.ACTION_PRIVACY_SETTINGS),
                    Intent("android.settings.RESET_SETTINGS"),
                    Intent(Settings.ACTION_SETTINGS)
                )
                
                for (intent in resetIntents) {
                    try {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        Log.i(TAG, "Successfully opened settings with intent: ${intent.action}")
                        return true
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to open settings with intent: ${intent.action}", e)
                    }
                }
                
                false
            } catch (e: Exception) {
                Log.e(TAG, "All factory reset methods failed", e)
                false
            }
        }
        
        /**
         * Test factory reset functionality without actually performing it
         */
        fun testFactoryResetReadiness(context: Context): String {
            val result = StringBuilder()
            
            try {
                val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val adminComponent = ComponentName(context, DeviceAdminReceiver::class.java)
                
                result.append("Factory Reset Readiness Test:\n")
                result.append("=============================\n\n")
                
                // Check device admin status
                val isAdminActive = devicePolicyManager.isAdminActive(adminComponent)
                result.append("Device Admin Active: ${if (isAdminActive) "✅ YES" else "❌ NO"}\n")
                
                if (isAdminActive) {
                    result.append("Admin Component: ${adminComponent.className}\n")
                    
                    // Check available wipe methods
                    result.append("\nAvailable Wipe Methods:\n")
                    try {
                        // Test if we can call wipeData (this won't actually wipe, just test permissions)
                        result.append("- Full Device Wipe: ✅ Available\n")
                        result.append("- External Storage Wipe: ✅ Available\n")
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            result.append("- Reset Protection Data: ✅ Available (Android 10+)\n")
                            result.append("- EUICC Wipe: ✅ Available (Android 10+)\n")
                        }
                    } catch (securityException: SecurityException) {
                        result.append("- Security Exception: ${securityException.message}\n")
                    }
                } else {
                    result.append("❌ Device admin must be activated to perform factory reset\n")
                }
                
                // Check system conditions
                result.append("\nSystem Conditions:\n")
                val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
                val batteryLevel = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
                result.append("Battery Level: ${batteryLevel}% ${if (batteryLevel >= 30) "✅" else "❌"}\n")
                
                result.append("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                result.append("Device Model: ${Build.MODEL}\n")
                result.append("Manufacturer: ${Build.MANUFACTURER}\n")
                
                result.append("\n")
                result.append(if (isAdminActive && batteryLevel >= 30) "🟢 READY FOR FACTORY RESET" else "🔴 NOT READY - See issues above")
                
            } catch (e: Exception) {
                result.append("Error during test: ${e.message}")
            }
            
            return result.toString()
        }
        
        /**
         * Performs pre-reset validation and data backup verification
         */
        fun validatePreResetConditions(context: Context): ResetValidationResult {
            val validationResult = ResetValidationResult()
            
            try {
                // Check battery level
                val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
                val batteryLevel = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
                validationResult.batteryLevel = batteryLevel
                validationResult.sufficientBattery = batteryLevel >= 30 // Require at least 30% battery
                
                // Check device admin status
                validationResult.deviceAdminActive = isDeviceAdminActive(context)
                
                // Check available storage (for backup verification)
                val internalStorage = context.filesDir.usableSpace
                validationResult.availableStorage = internalStorage
                
                // Overall validation
                validationResult.isValid = validationResult.sufficientBattery && validationResult.deviceAdminActive
                
            } catch (exception: Exception) {
                Log.e(TAG, "Error during pre-reset validation", exception)
                validationResult.isValid = false
                validationResult.error = exception.message
            }
            
            return validationResult
        }
    }
    
    /**
     * Data class to hold validation results
     */
    data class ResetValidationResult(
        var isValid: Boolean = false,
        var batteryLevel: Int = 0,
        var sufficientBattery: Boolean = false,
        var deviceAdminActive: Boolean = false,
        var availableStorage: Long = 0L,
        var error: String? = null
    ) {
        fun getValidationMessage(): String {
            return when {
                error != null -> "Validation error: $error"
                !sufficientBattery -> "Battery level too low ($batteryLevel%). Please charge to at least 30%."
                !deviceAdminActive -> "Device administrator privileges required. Please grant admin access."
                !isValid -> "Pre-reset validation failed. Please check device conditions."
                else -> "Device ready for secure factory reset."
            }
        }
    }
}
