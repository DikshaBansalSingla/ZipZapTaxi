package com.zipzaptaxi.live.model

 data class EndRideResponseModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val cash_colletion: Int,
        val extra_km: Int,
        val extra_km_charge: Int,
        val extra_min: Int,
        val extra_min_price: Int,
        val other_charges: Int,
        val total: Int
    )
}