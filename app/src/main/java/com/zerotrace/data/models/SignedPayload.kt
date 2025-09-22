package com.zerotrace.data.models

data class SignedPayload(
    val wipeId: String,
    val json: String,
    val signature: String
)
