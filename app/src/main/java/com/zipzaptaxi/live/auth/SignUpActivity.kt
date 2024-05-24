package com.zipzaptaxi.live.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.saveDeviceToken
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivitySignUpBinding
import com.zipzaptaxi.live.model.LoginResponseModel
import com.zipzaptaxi.live.model.SignUpReqModel
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showErrorAlert
import com.zipzaptaxi.live.viewmodel.AuthViewModel
import kotlin.math.log


class SignUpActivity : AppCompatActivity(), Observer<RestObservable> {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mValidationClass: ValidationsClass
    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }
    private var deviceToken=""
    private var name=""
    private var email=""
    private var user_type=""
    private var loginfor="normal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mValidationClass=ValidationsClass.getInstance()
        clickSpanText()

        getIntentData()
        setOnClicks()
    }

    private fun getIntentData() {
        loginfor= intent.extras?.getString("loginfor").toString()
        if(loginfor=="socialLogin"){
            name= intent.extras?.getString("name").toString()
            email= intent.extras?.getString("email").toString()
            binding.etName.setText(name)
            binding.etEmail.setText(email)
        }
    }

    private fun setOnClicks() {
        binding.btnSignUp.setOnClickListener {
            signUp()
        }

        binding.relLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp() {
        if(isValid()){

            val data= SignUpReqModel()
            data.name= binding.etName.text.toString().trim()
            data.email= binding.etEmail.text.toString().trim()
            data.phone= "+91"+binding.etPhone.text.toString().trim()
            data.device_type="0"
            data.type= loginfor
            data.device_token=deviceToken
            data.refered_by= binding.etReferCode.text.toString().trim()

            viewModel.signUpApi(this,true,data)
            viewModel.mResponse.observe(this, this)

        }
    }

    private fun addClickableText(
        ssb: SpannableStringBuilder,
        startPos: Int,
        clickableText: String,

        ) {
        ssb.append(clickableText)
        ssb.setSpan(
            MyClickableSpan(this),
            startPos,
            ssb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    private fun clickSpanText(){
        val text = findViewById<TextView>(R.id.tvTermsCondition)
        val ssb = SpannableStringBuilder(getString(R.string.terms_conditions))
        addClickableText(ssb, ssb.length, " Terms and conditions")

        text.movementMethod = LinkMovementMethod.getInstance() // make our spans selectable
        text.isClickable=true
        text.text = ssb
    }

    internal class MyClickableSpan(var context: Context) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = true // get rid of underlining
            ds.color = ContextCompat.getColor(context, R.color.colorPrimary)// make links red
        }

        override fun onClick(view: View) {


            ContextCompat.startActivity(context, Intent(context, TermsActivity::class.java),null
            )

        }
    }


    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            showErrorAlert(this, resources.getString(R.string.no_internet))
        else if (mValidationClass.checkStringNull(binding.etName.text.toString()))
            showErrorAlert(this, resources.getString(R.string.error_name))
        else if (mValidationClass.checkStringNull(binding.etPhone.text.toString()))
            showErrorAlert(this, resources.getString(R.string.error_phone))
        else if (!mValidationClass.validatePhoneNumber(binding.etPhone.text.toString()))
            showErrorAlert(this, resources.getString(R.string.error_valid_phone))
        else if (mValidationClass.checkStringNull(binding.etEmail.text.toString().trim()))
            showErrorAlert(this, resources.getString(R.string.error_email))
        else if (!mValidationClass.isValidEmail(binding.etEmail.text.toString().trim()))
            showErrorAlert(this, resources.getString(R.string.error_validemail))
        else if (!binding.checkBox.isChecked)
            showErrorAlert(this, resources.getString(R.string.error_terms_conditions))


        else
            check = true
        return check
    }

    override fun onResume() {
        super.onResume()
        AppUtils.hideSoftKeyboard(this)
        getDeviceToken()
    }

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("DEVICE TOKEN", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.e("DEVICE TOKEN", token.toString())
            deviceToken = token.toString()
            saveDeviceToken(this,token.toString())
        })
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if(value.data is LoginResponseModel){
                    val registerResponse: LoginResponseModel = value.data
                    if (registerResponse.code == AppConstant.success_code) {
                        showToast(registerResponse.message.toString())
                        val intent = Intent(this,VerifyOtpActivity::class.java)
                        intent.putExtra("phone",registerResponse.data.phone)
                        intent.putExtra("otp",registerResponse.data.otp)
                        startActivity(intent)
                    }else{
                        showErrorAlert(this, registerResponse.message!!)

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
}