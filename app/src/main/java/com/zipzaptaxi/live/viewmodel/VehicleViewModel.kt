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
class VehicleViewModel: ViewModel() {
    private val restApiInterface = AppController.getInstance().provideAuthService()
    var mResponse: MutableLiveData<RestObservable> = MutableLiveData()

    fun vehicleListApi(
        activity: Activity, showLoader: Boolean, userType:String

    ) {
        restApiInterface.getVehicles(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun vehicleDetailApi(
        activity: Activity, showLoader: Boolean, id:Int, userType: String
    ) {
        restApiInterface.getCabDetail(id, userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }


    fun addUpdateVehicleApi(
        activity: Activity, showLoader: Boolean, hashMap: HashMap<String, String>

    ) {
        restApiInterface.addUpdateCab(hashMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun deleteVehicleApi(
        activity: Activity, showLoader: Boolean, id:String, userType: String

    ) {
        restApiInterface.deleteVehicle(id, userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
}