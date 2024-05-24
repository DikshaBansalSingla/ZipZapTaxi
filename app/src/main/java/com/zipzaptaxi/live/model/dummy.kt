data class dummy(
    val code: Int,
    val `data`: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val address: String,
        val complete_address: String,
        val created_at: String,
        val device_token: String,
        val device_type: String,
        val email: String,
        val id: Int,
        val is_car_detailed_uploaded: String,
        val is_document_uploaded: String,
        val name: String,
        val password: String,
        val phone: String,
        val profile_pic: String,
        val refered_by: String,
        val self_referal_code: String,
        val updated_at: String,
        val user_type: String,
        val wallet_balance: String
    )
}