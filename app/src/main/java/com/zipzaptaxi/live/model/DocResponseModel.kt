package com.zipzaptaxi.live.model

data class DocResponseModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val aadhar_card_front: String?,
        val aadhar_card_back: String?,
        val pan_card_front: String?,
        val pan_card_back: String?,
        val driving_license_front: String?,
        val driving_license_back: String?,
        val police_verification: String?,
        val vendor_id: Int?,
        val id: Int?,
        val verification:String?
    )
}