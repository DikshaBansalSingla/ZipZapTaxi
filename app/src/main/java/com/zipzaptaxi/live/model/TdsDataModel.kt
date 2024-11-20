package com.zipzaptaxi.live.model

data class TdsDataModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val available_balance: String,
        val security: String,
        val tds_applicable: String,
        val tds_deducted: String,
        val transactions: ArrayList<Transaction>,
        val withdrable_amount: String
    ) {
        data class Transaction(
            val amount: String,
            val created_at: String,
            val status: String,
            val transaction_id: String,
            val aspects_of_transaction: String
        )
    }
}