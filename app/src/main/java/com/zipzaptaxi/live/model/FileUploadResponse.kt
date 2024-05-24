package com.zipzaptaxi.live.model

data class FileUploadResponse(
    val code: Int,
    val data: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
       val image_path:String,
       val image_name:String
    )
}