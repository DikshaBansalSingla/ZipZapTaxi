package com.zipzaptaxi.live.model

data class DriverListResponse(
    val code: Int,
    val data: ArrayList<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val aadhar_card_back: String,
        val aadhar_card_front: String,
        val aadhar_card_number: String,
        val created_at: String,
        val driving_license_back: String,
        val driving_license_front: String,
        val driving_license_number: String,
        val id: Int,
        val image: String,
        val name: String,
        val phone: String,
        val updated_at: String,
        val verification: String
    )
}