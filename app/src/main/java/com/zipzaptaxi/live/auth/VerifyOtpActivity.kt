package com.zipzaptaxi.live.auth

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chaos.view.PinView
import com.zipzaptaxi.live.R
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.autofill.AutofillManager
import androidx.annotation.RequiresApi
import com.zipzaptaxi.live.cache.getDeviceToken
import com.zipzaptaxi.live.cache.saveToken
import com.zipzaptaxi.live.cache.saveUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityVerifyOtpBinding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.LoginReqModel
import com.zipzaptaxi.live.model.LoginResponseModel
import com.zipzaptaxi.live.model.OtpResponseModel
import com.zipzaptaxi.live.model.VerifyOtpReqModel
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.isGone
import com.zipzaptaxi.live.utils.extensionfunctions.isVisible
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showErrorAlert
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showSuccessAlert
import com.zipzaptaxi.live.viewmodel.AuthViewModel

class VerifyOtpActivity : AppCompatActivity(), Observer<RestObservable> {

    private lateinit var mValidationClass: ValidationsClass

    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }

    lateinit var otpEditText: PinView
    private lateinit var otpReceiver: OtpReceiver
    private lateinit var autofillManager: AutofillManager


    private var countDownTimer:CountDownTimer?=null
    lateinit var binding: ActivityVerifyOtpBinding
    private var timeLeftInMilliseconds = 300000L // 300 seconds
    var phone=""
    var user_type=""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mValidationClass= ValidationsClass.getInstance()
        phone= intent.getStringExtra("phone").toString()
        user_type= intent.getStringExtra("user_type").toString()
        startTimer()

        setOnClicks()

        otpReceiver= OtpReceiver(this)

        otpEditText = findViewById(R.id.pinView) // Replace with your OTP EditText ID

        // Request SMS permissions if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), SMS_PERMISSION_CODE)
        } else {
            // Permission already granted, register the receiver
            val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            registerReceiver(otpReceiver, intentFilter)
        }
    }

    private fun setOnClicks() {

        binding.tvResend.setOnClickListener {
            val data= LoginReqModel()
            data.phone= phone
            data.device_type="0"
            data.device_token= getDeviceToken(this).toString()
            data.user_type= user_type
            viewModel.resendOtpApi(this,true,data)
            viewModel.mResponse.observe(this,this)
        }



        binding.btnSubmit.setOnClickListener{

            if(!mValidationClass.isNetworkConnected){
                showErrorAlert(this,resources.getString(R.string.no_internet))
            }else if(binding.pinView.text.toString().trim().length<4){
                showErrorAlert(this,resources.getString(R.string.valid_otp))
            }else{
               val data= VerifyOtpReqModel()
                data.phone= phone
                data.otp=binding.pinView.text.toString()
                data.device_type="0"
                data.device_token= getDeviceToken(this).toString()
                data.user_type= user_type

                viewModel.verifyOtpApi(this, true, data)
                viewModel.mResponse.observe(this, this)
            }
        }
    }

    private fun startTimer() {
        binding.txtOtpExpired.isVisible()
        binding.tvOtpExpired.isVisible()
         countDownTimer = object : CountDownTimer(timeLeftInMilliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMilliseconds = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                binding.txtOtpExpired.isGone()
                binding.tvOtpExpired.isGone()
            }
        }.start()
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMilliseconds / 1000) / 60
        val seconds = (timeLeftInMilliseconds / 1000) % 60

        val timeLeftFormatted = "$minutes:$seconds"
        binding.tvOtpExpired.text = timeLeftFormatted
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.onFinish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        unregisterReceiver(otpReceiver)
    }


    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is LoginResponseModel) {
                    val data: LoginResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        showSuccessAlert(this,"Otp Verified Successfully", onBackPressed = false)
                        saveToken(this,data.data.access_token.toString())
                        saveUser(this,data.data)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                    }else{
                        showErrorAlert(this,data.message!!)
                        binding.pinView.text?.clear()
                    }
                }

                else if (value.data is OtpResponseModel) {
                    val data: OtpResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        showSuccessAlert(this,"Otp Resent Successfully", onBackPressed = false)
                        binding.pinView.text?.clear()

                    }else{
                        showErrorAlert(this,data.message)
                    }
                }
            }
            Status.ERROR -> {
                if (value.data != null) {
                    showToast(value.data as String)
                } else {
                    showToast(value.error!!.toString())
                }
            }
            Status.LOADING -> {

            }
            else -> {}
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted

                val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
                registerReceiver(otpReceiver, intentFilter)
            } else {
                // Permission denied
            }
        }
    }

    companion object {
        private const val SMS_PERMISSION_CODE = 123
    }
}