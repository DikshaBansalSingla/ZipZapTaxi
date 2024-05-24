package com.zipzaptaxi.live.cache

import android.content.Context
import com.zipzaptaxi.live.model.LoginResponseModel
import com.zipzaptaxi.live.utils.Prefs


fun getDeviceToken(context: Context): String? {
    return Prefs.with(context).getString(CacheConstants.DEVICE_TOKEN, "")
}

fun saveDeviceToken(context: Context, token: String?) {
    Prefs.with(context).save(CacheConstants.DEVICE_TOKEN, token)
}

fun getSaveString(context: Context,keyName:String?): String? {
    return Prefs.with(context).getString(keyName, "")
}

fun saveString(context: Context, keyName:String,value: String?) {
    Prefs.with(context).save(keyName,value)
}

fun getUser(context: Context): LoginResponseModel.Data {
    return Prefs.with(context).getObject(CacheConstants.USER_DATA, LoginResponseModel.Data::class.java)
}

fun saveUser(context: Context, user: LoginResponseModel.Data) {
    Prefs.with(context).save(CacheConstants.USER_DATA, user)
}

fun getToken(context: Context): String? {
    return Prefs.with(context).getString(CacheConstants.USER_TOKEN, "")
}

fun saveToken(context: Context, token: String?) {
    Prefs.with(context).save(CacheConstants.USER_TOKEN, token)
}

fun saveIsDialogOpen(context: Context, value: Boolean) {
    Prefs.with(context).save("isOpen", value)
}

fun getIsDialogOpen(context: Context): Boolean {
    return Prefs.with(context).getBoolean("isOpen", false)
}

fun clearAllData(context: Context) {
    Prefs.with(context).removeAll()
}
