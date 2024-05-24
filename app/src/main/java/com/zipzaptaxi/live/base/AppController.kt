package com.zipzaptaxi.live.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.razorpay.Checkout
import com.zipzaptaxi.live.data.RestApiInterface
import com.zipzaptaxi.live.data.ServiceGenerator

class AppController : Application(), AppLifecycleHandler.AppLifecycleDelegates {

    var preferences: SharedPreferences? = null
    var prefToken: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var editorToken: SharedPreferences.Editor? = null

    companion object {
        var context: Context? = null
        @get:Synchronized
        lateinit var mInstance: AppController

        private const val PREF_NAME = "MyPref"
        const val PREF_TOKEN = "TruTraits"

        fun getInstance(): AppController {
            return mInstance
        }
    }

    private var lifecycleHandler: AppLifecycleHandler? = null
    var restApiInterface: RestApiInterface? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateConfiguration(base))
    }

    private fun updateConfiguration(context: Context): Context {
        val configuration = context.resources.configuration
        configuration.fontScale = 1.0f // No scaling
        return context.createConfigurationContext(configuration)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Checkout.preload(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        mInstance = this
        context = this
        initializePreferences()
        initializePreferencesToken()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        lifecycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifecycleHandler!!)
    }

    private fun registerLifecycleHandler(lifecycleHandler: AppLifecycleHandler) {
        registerActivityLifecycleCallbacks(lifecycleHandler)
        registerComponentCallbacks(lifecycleHandler)
    }

    override fun onAppForegrounded() {
        // Your code here
    }

    override fun onAppBackgrounded() {
        // Your code here
    }

    fun provideAuthService(): RestApiInterface {
        if (restApiInterface == null) {
            restApiInterface = ServiceGenerator.createService(RestApiInterface::class.java)
        }
        return restApiInterface!!
    }

    fun checkIfHasNetwork(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Initialize shared preferences
    private fun initializePreferences() {
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        editor = preferences!!.edit()
    }

    // Initialize shared preferences for token
    private fun initializePreferencesToken() {
        prefToken = getSharedPreferences(PREF_TOKEN, Context.MODE_PRIVATE)
        editorToken = prefToken!!.edit()
    }

    fun getString(key: String?): String? {
        return preferences!!.getString(key, "")
    }

    fun clearData() {
        editor!!.clear().apply()
    }
}
