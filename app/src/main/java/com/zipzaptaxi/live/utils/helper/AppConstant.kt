package com.zipzaptaxi.live.utils.helper

object AppConstant {



    const val BASE_URL = "https://zipzaptaxi.com/api/"
    const val BASE_URL_File_Upload = "https://app.gooutfrance.com/common_api/"

    val success_code = 200
    val errorCode = 401
    val AuthKey = "Authorization"
    //*********************urlS******************************

   // const val FileUpload = BASE_URL_File_Upload + "file_upload"
    const val SignUp = BASE_URL + "vendor/register"
    const val VerifyOtp = BASE_URL + "vendor/verify-otp"
    const val CheckAppVersion = BASE_URL + "auth/check_app_version"
    const val Login = BASE_URL + "vendor/login"
    const val SocialLogin = BASE_URL + "vendor/socailLogin"
    const val Logout = BASE_URL + "vendor/logout"
    const val ResendOtp = BASE_URL + "vendor/resend-otp"
    const val FileUpload = BASE_URL + "vendor/upload-images"
    const val UploadDocs = BASE_URL + "vendor/upload-documents"
    const val BookingList = BASE_URL + "vendor/bookings"
    const val BookingDetail = BASE_URL + "vendor/detailed-booking"
    const val UpdateProfile = BASE_URL + "vendor/update-profile"
    const val GetDrivers = BASE_URL + "vendor/drivers"
    const val AddUpdateDriver = BASE_URL + "vendor/add-update-drivers"
    const val DriverDetail = BASE_URL + "vendor/driver-detail"
    const val CabDetail = BASE_URL + "vendor/cab-detail"
    const val DeleteDriver = BASE_URL + "vendor/delete-driver"
    const val VehicleList = BASE_URL + "vendor/cabs"
    const val AddUpdateCab = BASE_URL + "vendor/add-update-cabs"
    const val GetDocuments = BASE_URL + "vendor/uploaded-documents"
    const val AssignBooking = BASE_URL + "vendor/assign-booking"
    const val AssignedBookingList = BASE_URL + "vendor/assigned-vendors-bookings"
    const val EnterOTP = BASE_URL + "vendor/start-ride-with-otp"
    const val StartRide = BASE_URL + "vendor/otp-verify-to-start-ride"
    const val EndRide = BASE_URL + "vendor/end-ride"
    const val GetCabFree = BASE_URL + "vendor/cab-free"
    const val PostCabFree = BASE_URL + "vendor/cab-free-store"
    const val GetWalletData = BASE_URL + "vendor/wallet"
    const val CancelRide = BASE_URL + "vendor/cancel-ride"
    const val GetSupportData = BASE_URL + "vendor/support"
    const val TermsPolicyData = BASE_URL + "vendor/terms-and-conditions"
    const val AddDeleteHomeCity = BASE_URL + "vendor/add-delete-home-city"
    const val GetHomeCity = BASE_URL + "vendor/add-delete-home-city"
    const val NotificationList = BASE_URL + "vendor/notifications"
    const val AddMoney = BASE_URL + "vendor/transactions"
    const val AddBankAcc = BASE_URL + "vendor/add-update-bank-details"
    const val GetBankDetails = BASE_URL + "vendor/bank-details"
    const val GetTransactions = BASE_URL + "vendor/get-transactions"
    const val RazorPayOrder = "https://api.razorpay.com/" + "v1/orders"


}