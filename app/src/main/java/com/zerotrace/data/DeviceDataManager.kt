package com.zerotrace.data

import android.content.Context
import android.os.Build
import com.zerotrace.data.models.DeviceInfo

object DeviceDataManager {
    fun collectDeviceInfo(context: Context): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            serial = Build.getSerial(),
            time = System.currentTimeMillis()
        )
    }
}
