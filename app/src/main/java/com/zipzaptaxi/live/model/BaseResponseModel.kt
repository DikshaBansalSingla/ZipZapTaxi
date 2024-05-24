package com.zipzaptaxi.live.model

data class BaseResponseModel(
    val code: Int,
    val message: String,
    val success: Boolean
)