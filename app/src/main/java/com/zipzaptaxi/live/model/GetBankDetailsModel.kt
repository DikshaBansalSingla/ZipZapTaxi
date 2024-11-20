package com.zipzaptaxi.live.model

data class GetBankDetailsModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val account_no: String,
        val bank_name: String,
        val ifsc_code: String,
        val name: String,
        val upi: String,
        val pan_card_front: String,
        val bank_passbook_front: String,
        val id: Int
    )
}