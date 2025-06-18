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
class OthersViewModel: ViewModel() {
    private val restApiInterface = AppController.getInstance().provideAuthService()
    var mResponse: MutableLiveData<RestObservable> = MutableLiveData()


    fun getSupportDataApi(
        activity: Activity, showLoader: Boolean, userType: String
    ) {
        restApiInterface.getSupportData(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }


    fun getTermsDataApi(
        activity: Activity, showLoader: Boolean, userType: String?
    ) {
        restApiInterface.getTermsData(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun getNotificationListApi(
        activity: Activity, showLoader: Boolean,userType:String
    ) {
        restApiInterface.getNotificationList(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun getCitiesListApi(
        activity: Activity, showLoader: Boolean, userType: String
    ) {
        restApiInterface.getHomeCityList(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun addDeleteHomeCityApi(
        activity: Activity, showLoader: Boolean,map:HashMap<String,Any>
    ) {
        restApiInterface.addDeleteHomeCity(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun addBankAccApi(
        activity: Activity, showLoader: Boolean,map:HashMap<String,String>
    ) {
        restApiInterface.addBankAcc(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun getBankDetailApi(
        activity: Activity, showLoader: Boolean,userType: String
    ) {
        restApiInterface.getBankDetails(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

}