package com.zipzaptaxi.live.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.zipzaptaxi.live.R


class MyProgressDialog(val dialog:Dialog?=null) {

    fun show(context: Context): Dialog? {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog_view, null)
        dialog?.setContentView(view)
        dialog?.setCancelable(false)
        dialog?.show()
        return dialog
    }

    fun hide(){
        dialog?.dismiss()
    }
}
