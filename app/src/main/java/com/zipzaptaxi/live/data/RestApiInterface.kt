package com.zipzaptaxi.live.data

import BookingListResponse
import com.zipzaptaxi.live.model.AddDeleteCityModel
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.model.BookingDetailResponse
import com.zipzaptaxi.live.model.CabFreeData
import com.zipzaptaxi.live.model.DocResponseModel
import com.zipzaptaxi.live.model.DriverDetailResponse
import com.zipzaptaxi.live.model.DriverListResponse
import com.zipzaptaxi.live.model.EndRideResponseModel
import com.zipzaptaxi.live.model.EnterOtpResModel
import com.zipzaptaxi.live.model.FileUploadResponse
import com.zipzaptaxi.live.model.GetBankDetailsModel
import com.zipzaptaxi.live.model.GetTransactionsModel
import com.zipzaptaxi.live.model.GetWalletModel
import com.zipzaptaxi.live.model.LoginReqModel
import com.zipzaptaxi.live.model.LoginResponseModel
import com.zipzaptaxi.live.model.NotificationListModel
import com.zipzaptaxi.live.model.OrderRequest
import com.zipzaptaxi.live.model.OrderResponse
import com.zipzaptaxi.live.model.OtpResponseModel
import com.zipzaptaxi.live.model.SignUpReqModel
import com.zipzaptaxi.live.model.StartRideResponseModel
import com.zipzaptaxi.live.model.SupportResponseModel
import com.zipzaptaxi.live.model.TermsPrivacyResponseModel
import com.zipzaptaxi.live.model.VehicleDetailResponse
import com.zipzaptaxi.live.model.VehicleListResponse
import com.zipzaptaxi.live.model.VerifyOtpReqModel
import com.zipzaptaxi.live.utils.helper.AppConstant
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RestApiInterface {


    @POST(AppConstant.SignUp)
    fun signUp(@Body request: SignUpReqModel): Observable<LoginResponseModel>

    @POST(AppConstant.VerifyOtp)
    fun verifyOtp(@Body request: VerifyOtpReqModel): Observable<LoginResponseModel>

    @POST(AppConstant.ResendOtp)
    fun resendOtp(@Body request: LoginReqModel): Observable<OtpResponseModel>

    @POST(AppConstant.Login)
    fun login(@Body request: LoginReqModel): Observable<LoginResponseModel>

    @FormUrlEncoded
    @POST(AppConstant.SocialLogin)
    fun socialLogin(@FieldMap map:HashMap<String,String>): Observable<LoginResponseModel>

    @Multipart
    @POST(AppConstant.FileUpload)
    fun fileUpload(
        @PartMap map: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?
    ): Observable<FileUploadResponse>

    @FormUrlEncoded
    @POST(AppConstant.UploadDocs)
    fun uploadDocs(
        @FieldMap map: HashMap<String, String>
//       @Field("driving_license_front")  param1:String,
//       @Field("driving_license_back")  param2:String
    ): Observable<BaseResponseModel>


    @GET(AppConstant.BookingList)
    fun getBookings(): Observable<BookingListResponse>

//    @FormUrlEncoded
//    @POST(AppConstant.BookingDetail)
//    fun getBookingDetail(@Field ("id") booking_id:Int): Observable<BookingDetailResponse>

    @GET(AppConstant.BookingDetail)
    fun  getBookingDetail(@Query("id") id:Int,
                          @Query("user_type") userType:String): Observable<BookingDetailResponse>


    @FormUrlEncoded
    @POST(AppConstant.UpdateProfile)
    fun updateProfile(@FieldMap map:HashMap<String,String>): Observable<LoginResponseModel>

    @GET(AppConstant.GetDrivers)
    fun getDrivers(): Observable<DriverListResponse>

    @FormUrlEncoded
    @POST(AppConstant.AddUpdateDriver)
    fun addUpdateDriver(
        @FieldMap map: HashMap<String, String>
    ): Observable<BaseResponseModel>

    @FormUrlEncoded
    @POST(AppConstant.AddUpdateCab)
    fun addUpdateCab(
        @FieldMap map: HashMap<String, String>
    ): Observable<BaseResponseModel>

    @GET(AppConstant.DriverDetail)
    fun getDriverDetail(@Query("id") id:Int): Observable<DriverDetailResponse>


    @GET(AppConstant.CabDetail)
    fun getCabDetail(@Query("id") id:Int): Observable<VehicleDetailResponse>


    @GET(AppConstant.VehicleList)
    fun getVehicles(): Observable<VehicleListResponse>

    //delete driver
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "vendor/delete-driver", hasBody = true)
    fun deleteDriver(
        @Field("id") id:String
    ): Observable<BaseResponseModel>


    //delete cab
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "vendor/delete-cab", hasBody = true)
    fun deleteVehicle(
        @Field("id") id:String
    ): Observable<BaseResponseModel>

    @GET(AppConstant.GetDocuments)
    fun geAllDocs(): Observable<DocResponseModel>

    @POST(AppConstant.Logout)
    fun logoutApp(): Observable<BaseResponseModel>

    @FormUrlEncoded
    @POST(AppConstant.AssignBooking)
    fun assignBooking(
        @FieldMap map: HashMap<String, String>
    ): Observable<BaseResponseModel>

    @GET(AppConstant.AssignedBookingList)
    fun  getAssignedList(@Query("status") status:String,
                         @Query("user_type") userType:String): Observable<BookingListResponse>

    @FormUrlEncoded
    @POST(AppConstant.EnterOTP)
    fun enterOtp(@Field ("id") id:Int): Observable<EnterOtpResModel>

    @FormUrlEncoded
    @POST(AppConstant.StartRide)
    fun startRide(@FieldMap map: HashMap<String,String>): Observable<StartRideResponseModel>

    @FormUrlEncoded
    @POST(AppConstant.EndRide)
    fun endRide(@FieldMap map: HashMap<String,String>): Observable<EndRideResponseModel>

    @GET(AppConstant.GetCabFree)
    fun getCabFreeData(): Observable<CabFreeData>

    @FormUrlEncoded
    @POST(AppConstant.PostCabFree)
    fun postCabFree(@FieldMap map: HashMap<String,String>): Observable<BaseResponseModel>

    @FormUrlEncoded
    @POST(AppConstant.CancelRide)
    fun cancelRide(@Field("id") id: Int): Observable<BaseResponseModel>

    @GET(AppConstant.GetWalletData)
    fun getWalletData(): Observable<GetWalletModel>

    @GET(AppConstant.GetSupportData)
    fun getSupportData(): Observable<SupportResponseModel>

    @GET(AppConstant.TermsPolicyData)
    fun getTermsData(): Observable<TermsPrivacyResponseModel>

    @POST(AppConstant.RazorPayOrder)
    fun createOrder(@Body map: OrderRequest): Observable<OrderResponse>

    @GET(AppConstant.NotificationList)
    fun getNotificationList(@Query("user_type") user_type:String): Observable<NotificationListModel>

    @GET(AppConstant.GetHomeCity)
    fun getHomeCityList(): Observable<AddDeleteCityModel>

    @GET(AppConstant.AddDeleteHomeCity)
    fun addDeleteHomeCity(@QueryMap map: HashMap<String,Any>): Observable<AddDeleteCityModel>

    @GET(AppConstant.GetBankDetails)
    fun getBankDetails(@Query("user_type") user_type:String): Observable<GetBankDetailsModel>

    @GET(AppConstant.GetTransactions)
    fun getTransactions(): Observable<GetTransactionsModel>

    @FormUrlEncoded
    @POST(AppConstant.AddMoney)
    fun addMoney(@FieldMap map: HashMap<String,String>): Observable<BaseResponseModel>

    @FormUrlEncoded
    @POST(AppConstant.AddBankAcc)
    fun addBankAcc(@FieldMap map: HashMap<String,String>): Observable<BaseResponseModel>

}

