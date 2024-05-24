package com.zipzaptaxi.live.wallet

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.cache.getUser
import com.zipzaptaxi.live.data.RestObservable
import com.zipzaptaxi.live.data.Status
import com.zipzaptaxi.live.databinding.ActivityAddMoneyBinding
import com.zipzaptaxi.live.databinding.ActivityLogin2Binding
import com.zipzaptaxi.live.databinding.FragmentTermsCondBinding
import com.zipzaptaxi.live.databinding.LayoutToolbarBinding
import com.zipzaptaxi.live.model.BaseResponseModel
import com.zipzaptaxi.live.utils.MyProgressDialog
import com.zipzaptaxi.live.utils.extensionfunctions.showToast
import com.zipzaptaxi.live.utils.helper.AppConstant
import com.zipzaptaxi.live.utils.helper.AppUtils
import com.zipzaptaxi.live.utils.showCustomAlert
import com.zipzaptaxi.live.viewmodel.WalletViewModel
import org.json.JSONObject

class AddMoneyActivity : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener,
    DialogInterface.OnClickListener,
    Observer<RestObservable> {

    private lateinit var alertDialogBuilder: AlertDialog.Builder

    private lateinit var binding: ActivityAddMoneyBinding
    private lateinit var toolbarBinding: LayoutToolbarBinding
    private val checkout = Checkout()

    val viewModel: WalletViewModel by lazy {
        WalletViewModel()
    }

    var amount = 0
    var mProgressDialog: MyProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoneyBinding.inflate(layoutInflater)
        toolbarBinding = binding.appToolbar

        setContentView(binding.root)

        alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Payment Result")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Ok", this)
        Checkout.preload(applicationContext)
        mProgressDialog = MyProgressDialog(Dialog(this, R.style.CustomDialogTheme))
        setToolbar()
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.btnSubmit.setOnClickListener {
            if (binding.etAddMoney.text.toString().trim().isNullOrEmpty()) {
                AppUtils.showErrorAlert(this, "Please enter amount")
            } else {
                startPayment()
            }
        }
    }

    private fun startPayment() {
        mProgressDialog!!.show(this)

        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

      //  co.setKeyID("rzp_test_jyn3PaMKThlTXx")
         co.setKeyID("rzp_live_4PZpiiRau2gahT")

        try {
            val options = JSONObject()

            options.put("name", "ZipZap Taxi")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", R.drawable.app_logo)
            options.put("currency", "INR")
            amount = binding.etAddMoney.text.toString().toInt()
            options.put("amount", amount * 100)
            options.put("send_sms_hash", true)

            val prefill = JSONObject()
            prefill.put("email", getUser(this).email)
            prefill.put("contact", getUser(this).phone)

            options.put("prefill", prefill)

            mProgressDialog!!.hide()
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try {
            val transactionId = p1?.paymentId ?: ""
            addMoney(binding.etAddMoney.text.toString(), "success", transactionId)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Payment successful but failed to process response: ${e.message}")
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            val transactionId = p2?.paymentId ?: ""
            addMoney(binding.etAddMoney.text.toString(), "failure", transactionId)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Payment failed: ${e.message}")
        }
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try {
            alertDialogBuilder.setMessage("External wallet was selected : Payment Data: ${p1?.data}")
            alertDialogBuilder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setToolbar() {
        toolbarBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbarBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbarBinding.toolbarTitle.text = getString(R.string.add_money)
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {

    }

    private fun addMoney(amount: String, status: String, transactionId: String) {
        val map = HashMap<String, String>()
        map["amount"] = amount
        map["status"] = status
        map["transactionId"] = transactionId
        viewModel.addMoneyApi(this, true, map)
        viewModel.mResponse.observe(this, this)
    }

    override fun onChanged(value: RestObservable) {
        when (value.status) {
            Status.SUCCESS -> {
                if (value.data is BaseResponseModel) {
                    val data: BaseResponseModel = value.data
                    if (data.code == AppConstant.success_code) {
                        showCustomAlert(this, "Payment Successful", "OK") {
                            finish()
                        }
                    } else {
                        showCustomAlert(this, "Payment failed. Try again", "OK") {
                            finish()
                        }
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
