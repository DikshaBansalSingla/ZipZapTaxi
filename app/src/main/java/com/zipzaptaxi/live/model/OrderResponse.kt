package com.zipzaptaxi.live.model

data class OrderResponse(
    val id: String, // Order ID
    val amount: Int,
    val currency: String,
)
