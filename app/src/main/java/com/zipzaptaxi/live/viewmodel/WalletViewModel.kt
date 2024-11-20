package com.zipzaptaxi.live.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zipzaptaxi.live.base.AppController
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.model.OrderRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class WalletViewModel : ViewModel() {
    private val restApiInterface = AppController.getInstance().provideAuthService()
    var mResponse: MutableLiveData<RestObservable> = MutableLiveData()


    fun getWalletDataApi(
        activity: Activity, showLoader: Boolean,userType:String
    ) {
        restApiInterface.getWalletData(userType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun addMoneyApi(
        activity: Activity, showLoader: Boolean,map:HashMap<String,String>
    ) {
        restApiInterface.addMoney(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }


    fun sendRequestApi(
        activity: Activity, showLoader: Boolean,
        map: HashMap<String, String>
    ) {
        restApiInterface.sendRequest(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

    fun getTransactionsApi(
        activity: Activity, showLoader: Boolean
    ) {
        restApiInterface.getTransactions()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }
    fun getTdsDataApi(
        activity: Activity, showLoader: Boolean
    ) {
        restApiInterface.getTdsData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mResponse.value = RestObservable.loading(activity, showLoader) }
            .subscribe(
                { mResponse.value = RestObservable.success(it) },
                { mResponse.value = RestObservable.error(activity, it) }
            )
    }

}