package com.zipzaptaxi.live.data

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.auth.LoginActivity
import com.zipzaptaxi.live.base.AppController
import com.zipzaptaxi.live.cache.clearAllData
import com.zipzaptaxi.live.utils.MyProgressDialog
import com.zipzaptaxi.live.utils.SharedPrefUtil
import com.zipzaptaxi.live.utils.helper.AppConstant
import okhttp3.ResponseBody
import java.io.IOException


class RestObservable(
    val status: Status,
    val data: Any?,
    val error: Any?) {


    companion object {
        var mProgressDialog: MyProgressDialog? = null


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun loading(activity: Activity, showLoader: Boolean): RestObservable {
            if (showLoader) {
                mProgressDialog = MyProgressDialog(Dialog(activity, R.style.CustomDialogTheme))
                mProgressDialog!!.show(activity)
            }

            Log.e("REST", "Loading")
            return RestObservable(Status.LOADING, null, null)
        }

        fun success(data: Any): RestObservable {
            if (mProgressDialog != null)
                mProgressDialog!!.hide()
            Log.e("REST", "Success")
            return RestObservable(Status.SUCCESS, data, null)
        }

        fun error(activity: Activity, error: Throwable): RestObservable {
            Log.e("REST", "Error")
            if (mProgressDialog != null)
                mProgressDialog!!.hide()
            try {
                // We had non-200 http error
                if (error is HttpException) {
                    val httpException = error
                    val response = httpException.response()
                    Log.i(TAG, error.message() + " / " + error.javaClass)
                    return RestObservable(
                        Status.ERROR,
                        null,
                        callErrorMethod(activity, response.errorBody())
                    )
                }
                // A network error happened
                if (error is IOException) {
                    Log.i(TAG, error.message + " / " + error.javaClass)
                    Log.e("Errror123", error.message!!)
                    Log.e("StatusERROR", Status.ERROR.toString())
                    Log.e("StatusJAVA", error.javaClass.toString())


//                    return RestObservable(Status.ERROR, null, error)
                    return RestObservable(Status.ERROR, null, "Server not responding")
                }

                Log.i(TAG, error.message + " / " + error.javaClass)
            } catch (e: Exception) {
                Log.i(TAG, e.message!!)
                return RestObservable(Status.ERROR, null, error)
            }

            return RestObservable(Status.ERROR, null, error)
        }

        fun callErrorMethod(activity: Activity, responseBody: ResponseBody?): String {

            val converter = ServiceGenerator.getRetrofit()
                .responseBodyConverter<RestError>(
                    RestError::class.java,
                    arrayOfNulls<Annotation>(0)
                )
            try {
                val errorResponse = converter.convert(responseBody!!)
                val error_message = errorResponse!!.message

                if (errorResponse.code == AppConstant.errorCode) {

                        AppController.getInstance()
                            .clearData()
                        AppController.mInstance.clearData()
                        SharedPrefUtil.getInstance().clear()
                        SharedPrefUtil.getInstance().isLogin = false
                       clearAllData(AppController.context!!)
                        val intent = Intent(activity, LoginActivity::class.java)
                        activity.startActivity(intent)
                        activity.finishAffinity()

                        Toast.makeText(
                            activity,
                            "Session Expired !Login Again ",
                            Toast.LENGTH_LONG
                        ).show()



                }

                return error_message!!
            } catch (e: IOException) {
                return "Server not responding"
//                return e.toString()
            }

        }
    }
}
