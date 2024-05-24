package com.zipzaptaxi.live.model

data class SupportResponseModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val email: String,
        val phone: String
    )
}