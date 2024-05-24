package com.zipzaptaxi.live.model

data class CabFreeData(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val cabs: ArrayList<Cab>,
        val drivers: ArrayList<Driver>,
        val time: ArrayList<String>
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
    }
}