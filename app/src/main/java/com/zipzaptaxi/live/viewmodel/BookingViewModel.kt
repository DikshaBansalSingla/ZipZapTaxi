package com.zipzaptaxi.live.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zipzaptaxi.live.base.AppController
import com.zipzaptaxi.live.data.RestObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class BookingViewModel: ViewModel() {
    private val restApiInterface = AppController.getInstance().provideAuthService()
    var mResponse: MutableLiveData<RestObservable> = MutableLiveData()

    fun bookingListApi(
        activity: Activity, showLoader: Boolean,userType: String

    ) {
        restApiInterface.getBookings(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }


    fun bookingDetailApi(
        activity: Activity, showLoader: Boolean,
        booking_id:Int,userType:String

    ) {
        restApiInterface.getBookingDetail(booking_id,userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun assignBookingApi(
        activity: Activity, showLoader: Boolean,
        hashMap:HashMap<String,String>

    ) {
        restApiInterface.assignBooking(hashMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun getAssignedBookingApi(
        activity: Activity, showLoader: Boolean,
        status:String,userType: String

    ) {
        restApiInterface.getAssignedList(status,userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun enterOtpApi(
        activity: Activity, showLoader: Boolean,
        id:Int, userType: String
    ) {
        restApiInterface.enterOtp(id,userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
    fun startRideApi(
        activity: Activity, showLoader: Boolean,
        map:HashMap<String,String>
    ) {
        restApiInterface.startRide(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
    fun endRideApi(
        activity: Activity, showLoader: Boolean,
        map:HashMap<String,String>
    ) {
        restApiInterface.endRide(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
    fun cancelRideApi(
        activity: Activity, showLoader: Boolean,
        id:Int,userType: String
    ) {
        restApiInterface.cancelRide(id,userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
}