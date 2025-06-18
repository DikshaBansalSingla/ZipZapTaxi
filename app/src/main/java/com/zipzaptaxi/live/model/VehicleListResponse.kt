package com.zipzaptaxi.live.model

data class VehicleListResponse(
    val code: Int,
    val data: ArrayList<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val bags: String,
        val car_back_image: String,
        val car_front_image: String,
        val car_left_image: String,
        val car_right_image: String,
        val color: String,
        val fitness_expiry_date: String,
        val fitness_image: String,
        val img: String,
        val has_roof_top: String,
        val id: Int,
        val insurance_expiry_date: String,
        val insurance_image: String,
        val is_cng: String,
        val is_diesel: String,
        val is_electric: String,
        val is_petrol: String,
        val number_plate: String,
        val passenger: String,
        val permit_expiry_date: String,
        val permit_image: String,
        val pollution_expiry_date: String,
        val pollution_image: String,
        val rc_back_image: String,
        val rc_front_image: String,
        val registration_end_date: String,
        val tax_paid_until_date: String,
        val vehicle_model: String,
        val vehicle_owner_name: String,
        val vehicle_owner_surname: String,
        val cab_type: String,
        val vehicle_year: String,
        val verification: String,
        val status: String
    )
}