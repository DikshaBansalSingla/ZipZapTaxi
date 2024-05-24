package com.zipzaptaxi.live.model

data class EnterOtpResModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val otp: Int
    )
}