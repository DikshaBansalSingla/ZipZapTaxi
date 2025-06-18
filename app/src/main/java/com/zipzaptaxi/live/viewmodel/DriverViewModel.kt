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
class DriverViewModel: ViewModel() {
    private val restApiInterface = AppController.getInstance().provideAuthService()
    var mResponse: MutableLiveData<RestObservable> = MutableLiveData()

    fun driverListApi(
        activity: Activity, showLoader: Boolean, userType:String

    ) {
        restApiInterface.getDrivers(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }


    fun addUpdateDriverApi(
        activity: Activity, showLoader: Boolean, hashMap: HashMap<String,String>

    ) {
        restApiInterface.addUpdateDriver(hashMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun driverDetailApi(
        activity: Activity, showLoader: Boolean, id:Int, userType: String

    ) {
        restApiInterface.getDriverDetail(id,userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun deleteDriverApi(
        activity: Activity, showLoader: Boolean, id:String, userType: String

    ) {
        restApiInterface.deleteDriver(id,userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
}


