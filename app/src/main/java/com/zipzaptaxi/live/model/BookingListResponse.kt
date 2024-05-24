data class BookingListResponse(
    val code: Int,
    val data: ArrayList<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val airport_charge: String,
        val alternate_no: String,
        val bags: String,
        val booking_status: String,
        val booking_unique_id: String,
        val cab_image: String,
        val wallet_balance: Int?,
        val cab_model: String,
        val car_type: String,
        val destination: String,
        val distance: String,
        val driver_allowance: String,
        val dropLoc: String,
        val drop_charges: String,
        val email: String,
        val extra_charge: String,
        val extra_time_charge: String,
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
        val stops: ArrayList<Stop>,
        val time_to_cover: String,
        val toll_tax: String,
        val trip: String
    ) {
        data class Stop(
            val value: String
        )
    }
}