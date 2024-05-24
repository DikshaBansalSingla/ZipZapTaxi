package com.zipzaptaxi.live.model

data class TermsPrivacyResponseModel(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val terms_and_conditions: String,
        val privacy_policy: String
    )
}