package com.zipzaptaxi.live.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.saveDeviceToken
import com.zipzaptaxi.live.cache.saveToken
import com.zipzaptaxi.live.cache.saveUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityLogin2Binding
import com.zipzaptaxi.live.home.MainActivity
import com.zipzaptaxi.live.model.LoginReqModel
import com.zipzaptaxi.live.model.LoginResponseModel
import com.zipzaptaxi.live.utils.ValidationsClass
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.helper.AppUtils.Companion.showErrorAlert
import com.zipzaptaxi.live.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity(), Observer<RestObservable> {

    private lateinit var mValidationClass: ValidationsClass

    private lateinit var binding: ActivityLogin2Binding

    private val viewModel: AuthViewModel
            by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }
    private var deviceToken=""
    var RC_SIGN_IN = 0
    private var socialLoginType = ""
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        mValidationClass= ValidationsClass.getInstance()

        setOnClicks()

        //for google

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun setOnClicks() {

        binding.btnSignIn.setOnClickListener {
            loginApi()
        }

        binding.relSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("loginfor","signup")
            startActivity(intent)
            finish()
        }

        binding.btnSignInGoogle.setOnClickListener {
            googlesignIn()
        }
    }

    private fun loginApi() {
        if(isValid()){
            val data= LoginReqModel()

            data.phone= "+91"+binding.etMobile.text.toString().trim()
            data.device_type="0"
            data.device_token=deviceToken

            viewModel.loginApi(this,true,data)
            viewModel.mResponse.observe(this, this)
        }
    }

    // for google
    private fun googlesignIn() {
        val intentGoogle = mGoogleSignInClient!!.signInIntent
        startActivityForResult(intentGoogle, RC_SIGN_IN)
    }

    // for google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    // for google
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Login", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }
    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {

            val personName = account.displayName
            val email = account.email
            val id = account.id

            if (mGoogleSignInClient == null) {
//                Toast.makeText(this, "Khali", Toast.LENGTH_SHORT).show()
            } else {
                socialLoginType = "Google"
                val map = HashMap<String, String>()

                map["name"] = personName.toString()
                map["email"] = email.toString()
                map["device_type"] = "0"
                map["device_token"] = deviceToken
                map["social_id"] = id.toString()
                viewModel.socialLoginApi(this, true, map)
                viewModel.mResponse.observe(this, this)

            }
        } else {
            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_LONG).show()
        }
    }


    private fun isValid(): Boolean {
        var check = false
        if (!mValidationClass.isNetworkConnected)
            showErrorAlert(this,resources.getString(R.string.no_internet))
        else if (mValidationClass.checkStringNull(binding.etMobile.text.toString()))
            showErrorAlert(this,resources.getString(R.string.error_phone))
        else if (!mValidationClass.validatePhoneNumber(binding.etMobile.text.toString()))
            showErrorAlert(this,resources.getString(R.string.error_valid_phone))

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
        when (value .status) {
            Status.SUCCESS -> {
                if(value.data is LoginResponseModel){
                    val registerResponse: LoginResponseModel = value.data
                    if (registerResponse.code == AppConstant.success_code) {
                        if(registerResponse.data.type=="register"){
                            val intent = Intent(this,SignUpActivity::class.java)
                            intent.putExtra("name",registerResponse.data.name)
                            intent.putExtra("loginfor","socialLogin")
                            intent.putExtra("email",registerResponse.data.email)
                            startActivity(intent)
                        }else if(registerResponse.data.type=="login"){
                            saveToken(this,registerResponse.data.access_token.toString())
                            saveUser(this,registerResponse.data)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }
                        else{
                            showToast(registerResponse.message.toString())
                            val intent = Intent(this,VerifyOtpActivity::class.java)
                            intent.putExtra("phone",registerResponse.data.phone)
                            intent.putExtra("otp",registerResponse.data.otp)
                            intent.putExtra("user_type",registerResponse.data.user_type)
                            startActivity(intent)
                        }

                    }
                    else {
                        showErrorAlert(this, registerResponse.message!!)
                        binding.etMobile.text.clear()
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