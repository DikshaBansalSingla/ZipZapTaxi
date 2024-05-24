package com.zipzaptaxi.live.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zipzaptaxi.live.R
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