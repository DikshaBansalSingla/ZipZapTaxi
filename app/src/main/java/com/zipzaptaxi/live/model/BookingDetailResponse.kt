package com.zipzaptaxi.live.model

data class BookingDetailResponse(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val airport_charge: String,
        val alternate_no: String,
        val assigned_cab: String,
        val assigned_driver: String,
        val assigned_to_me: Int,
        val assigned_to_other: Int,
        val on_the_way_hide: Int,
        val bags: String,
        val booking_status: String,
        val booking_unique_id: String,
        val cab_image: String,
        val cab_model: String,
        val cabs: ArrayList<Cab>,
        val car_type: String,
        val can_cancel: String,
        val can_update: String,
        val destination: String,
        val distance: String,
        val driver_allowance: String,
        val drivers: ArrayList<Driver>,
        val dropLoc: String,
        val drop_charges: String,
        val email: String,
        val extra_charge: String,
        val extra_time_charge: String,
        val get_otp: Int,
        val gst_no: String,
        val id: Int,
        val mobile: String,
        val name: String,
        val night_pick_charges: String,
        val night_drop_charges: String,
        val night_drop: String,
        val night_pick: String,
        val other_comments: String,
        val parking: String,
        val passengers: String,
        val pickLoc: String,
        val pickup_charges: String,
        val price: String,
        val vendor_amount: Int,
        val balance: Int,
        val penalty: Int,
        val ride_date: String,
        val ride_end_date: String,
        val ride_end_time: String,
        val ride_time: String,
        val roofTop: String,
        val roof_charges: String,
        val source: String,
        val state_tax: String,
        val status: String,
        val stops: ArrayList<Stop>,
        val time_to_cover: String,
        val wallet_balance: Int,
        val home_city: Int,
        val extra_km_driven: Int,
        val total_price_with_extra_km: Int,
        val documents: Int,
        val toll_tax: String,
        val trip: String
    ) {
        data class Cab(
            val id: Int,
            val number: String,
            val vehicle_model: String
        )
        data class Driver(
            val id: Int,
            val name: String,
            val phone: String
        )
            data class Stop(
                val value: String
            )

    }
}