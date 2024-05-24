package com.zipzaptaxi.live.model

data class OtpResponseModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val device_token: String,
        val device_type: String,
        val email: String,
        val id: Int,
        val name: String,
        val otp: String,
        val phone: String,
        val refered_by: Any,
        val self_referal_code: String
    )
}