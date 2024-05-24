package com.zipzaptaxi.live.model

data class GetTransactionsModel(
    val code: Int,
    val data: ArrayList<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val amount: String,
        val created_at: String,
        val status: String
    )
}