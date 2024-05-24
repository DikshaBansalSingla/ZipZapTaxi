package com.zipzaptaxi.live.model

data class OrderRequest(
    val amount: Int,
    val currency: String,
    val receipt: String
)