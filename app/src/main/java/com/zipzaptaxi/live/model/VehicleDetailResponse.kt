package com.zipzaptaxi.live.model

data class VehicleDetailResponse(
    val code: Int,
    val data: VehicleListResponse.Data,
    val message: String,
    val success: Boolean
)
