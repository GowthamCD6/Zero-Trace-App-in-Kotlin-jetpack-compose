package com.zerotrace.utils

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.widget.Toast
import java.io.File

object StorageUtils {

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
                Toast.makeText(context, "External storage detected ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Please connect a USB OTG drive or microSD card ⚠️",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return isExternalConnected
    }
}
