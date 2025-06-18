package com.zipzaptaxi.live.model

data class LoginResponseModel(
    val code: Int,
    val data: Data,
    val message: String?,
    val success: Boolean
) {
    data class Data(
        val access_token: String? ="",
        val device_token: String?,
        val device_type: String?,
        val email: String?,
        val id: Int,
        val is_car_detailed_uploaded: String?,
        val is_document_uploaded: String?,
        val name: String?,
        val otp: String? = "1244",
        val phone: String?,
        val complete_address: String?,
        val address: String?,
        val profile_pic: String?,
        val type: String?= "login",
        val user_type: String?,
        val refered_by: Any?
        ="",
        val self_referal_code: String?,
        val wallet_balance: Int
    )
}