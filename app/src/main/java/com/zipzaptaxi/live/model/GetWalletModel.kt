package com.zipzaptaxi.live.model

data class GetWalletModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val balance: String,
        val note_balance: String,
        val bookings: ArrayList<Booking>,
        val collect_amount: String,
        val paid: String,
        val panelty: String,
        val vendor_amount: String
    ) {
        data class Booking(
            val airport_charge: String,
            val alternate_no: String,
            val assigned_cab: Int,
            val assigned_driver: Int,
            val assigned_to_me: Int,
            val assigned_to_other: Int,
            val booking_id: Int,
            val bags: String,
            val balance: String,
            val booking_status: String,
            val booking_unique_id: String,
            val cab_image: String,
            val cab_model: String,
            val cancellation_time: String,
            val car_type: String,
            val destination: String,
            val distance: String,
            val driver_allowance: String,
            val dropLoc: String,
            val drop_charges: String,
            val email: String,
            val extra_charge: String,
            val extra_km_driven: Int,
            val extra_time_charge: String,
            val get_otp: Int,
            val gst_no: String,
            val id: Int,
            val mobile: String,
            val name: String,
            val night_charges: String,
            val night_drop: String,
            val night_pick: String,
            val other_comments: String,
            val parking: String,
            val passengers: String,
            val penalty: Int,
            val pickLoc: String,
            val pickup_charges: String,
            val price: String,
            val ride_date: String,
            val ride_end_date: String,
            val ride_end_time: String,
            val ride_time: String,
            val roofTop: String,
            val roof_charges: String,
            val source: String,
            val state_tax: String,
            val status: String,
            val stops: Stops,
            val time_to_cover: String,
            val toll_tax: String,
            val total_price_with_extra_km: Int,
            val trip: String,
            val vendor_amount: String
        ) {
            data class Stops(
                val `0`: String
            )
        }
    }
}