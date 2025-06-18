package com.zipzaptaxi.live.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.model.EndRideResponseModel
import com.zipzaptaxi.live.utils.extensionfunctions.isGone

fun showAlert(context: Context,message: String, buttonText: String, onClick: () -> Unit) {

    var dialog = AlertDialog.Builder(context)
    dialog.setMessage(message)
    dialog.setCancelable(false)
    dialog.setPositiveButton(buttonText) { dialog, which ->
        onClick()
        dialog.dismiss()
    }

    dialog.show()

}

fun showAlertWithCancel(context: Context,message: String, buttonText: String, negativeText: String, onClick: () -> Unit, onNegativeClick: ()->Unit) {

    var dialog = AlertDialog.Builder(context)
    dialog.setTitle("Permission Required")
    dialog.setMessage(message)
    dialog.setCancelable(false)
    dialog.setPositiveButton(buttonText) { dialog, which ->
        onClick()
        dialog.dismiss()
    }

    dialog.setNegativeButton(negativeText){  dialog, which ->
        onNegativeClick()
        dialog.dismiss()
    }
    dialog.show()

}


fun showCustomAlert(
    context: Context,
    message: String,
    buttonText: String,
    onClick: () -> Unit
) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.dialog_error)
    val body = dialog.findViewById(R.id.tvTitleDialog) as TextView
    body.text = message
    val yesBtn = dialog.findViewById(R.id.btnYes) as TextView
    yesBtn.text= buttonText
    val nobtn = dialog.findViewById(R.id.btnNo) as TextView
    nobtn.isGone()
    yesBtn.setOnClickListener {
        onClick()
        dialog.dismiss()
    }
    dialog.show()
}



fun showCompleteAlert(
    context: Context,
    data: EndRideResponseModel.Data,
    buttonText: String,
    onClick: () -> Unit
) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.dialog_complete)


    /*
     binding.tvOtherCharges?.text = "₹" + data.other_charges
        binding.tvBalAmount.text = "₹" + data.balance
        binding.tvPenalty.text = "₹" + data.penalty
        binding.tvTotalPrice?.text = "₹" + data.total_price_with_extra_km

        binding.tvExtraKm?.text = "₹" + data.extra_km_price
        binding.tvExtraMin?.text = "₹" + data.extra_min_price
        binding.txtExtraKm?.text ="Extra Km Driven("+data.extra_km_driven +"):"
        binding.txtExtraMin?.text ="Extra Minutes("+data.extra_min +"):"
     */
    val otherCharges = dialog.findViewById(R.id.tvOtherCharges) as TextView
    val cashCollect = dialog.findViewById(R.id.tvBalAmount) as TextView
    val totalPrice = dialog.findViewById(R.id.tvTotalPrice) as TextView
    val txtExtraKm = dialog.findViewById(R.id.txtExtraKm) as TextView
    val tvExtraKm = dialog.findViewById(R.id.tvExtraKm) as TextView
    val txtExtraMin = dialog.findViewById(R.id.txtExtraMin) as TextView
    val extraMin = dialog.findViewById(R.id.tvExtraMin) as TextView

    otherCharges.text= "₹" + data.other_charges
    cashCollect.text= "₹" + data.cash_colletion
    totalPrice.text= "₹" + data.total
    tvExtraKm.text= "₹" + data.extra_km_charge
    extraMin.text= "₹" + data.extra_min_price
    txtExtraKm.text= "Extra Km Driven("+data.extra_km +"):"
    txtExtraMin.text= "Extra Minutes("+data.extra_min +"):"



    val yesBtn = dialog.findViewById(R.id.btnYes) as TextView
    yesBtn.text= buttonText
    yesBtn.setOnClickListener {
        onClick()
        dialog.dismiss()
    }
    dialog.show()
}


fun showCustomAlertWithCancel(
    context: Context,
    message: String,
    buttonText: String, negativeText: String,
    onClick: () -> Unit,
    onNegativeClick: () -> Unit
) {

    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.dialog_error)
    val body = dialog.findViewById(R.id.tvTitleDialog) as TextView
    body.text = message
    val yesBtn = dialog.findViewById(R.id.btnYes) as TextView
    val nobtn = dialog.findViewById(R.id.btnNo) as TextView
    yesBtn.text= buttonText
    nobtn.text= negativeText
    yesBtn.setOnClickListener {
        onClick()
        dialog.dismiss()
    }
    nobtn.setOnClickListener {
        onNegativeClick()
        dialog.dismiss()
    }
    dialog.show()

}