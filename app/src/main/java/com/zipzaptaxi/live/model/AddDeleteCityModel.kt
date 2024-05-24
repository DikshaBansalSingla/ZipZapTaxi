package com.zipzaptaxi.live.model

data class AddDeleteCityModel(
    val code: Int,
    val data: ArrayList<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val city: String,
        val created_at: String,
        val id: Int,
        val updated_at: String,
        val vendor_id: Int
    )
}