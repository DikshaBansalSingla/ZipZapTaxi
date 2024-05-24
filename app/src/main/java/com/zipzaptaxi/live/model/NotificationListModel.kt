package com.zipzaptaxi.live.model

data class NotificationListModel(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val booking_id: String,
        val created_at: String,
        val message: String,
        val redirect: String,
        val title: String
    )
}