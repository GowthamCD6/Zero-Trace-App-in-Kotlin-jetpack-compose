package com.zerotrace.data.models

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val serial: String,
    val time: Long
)
