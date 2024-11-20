package com.zipzaptaxi.live.utils.extensionfunctions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.base.AppController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun ImageView.loadImage(path: Any) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 8f
    circularProgressDrawable.centerRadius = 40f
    circularProgressDrawable.setColorSchemeColors(
        ContextCompat.getColor(
            this.context,
            R.color.teal_200
        )
    )
    circularProgressDrawable.start()
    Glide.with(this)
        .load(path)
        .placeholder(circularProgressDrawable)
        .error(R.drawable.placeholder)
        .into(this)

}


fun ImageView.loadImageForList(path: Any) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 8f
    circularProgressDrawable.centerRadius = 40f
    circularProgressDrawable.setColorSchemeColors(
        ContextCompat.getColor(
            this.context,
            R.color.teal_200
        )
    )
    circularProgressDrawable.start()
    Glide.with(this)
        .load(path)
        .placeholder(circularProgressDrawable)
        .error(R.drawable.placeholder_list)
        .into(this)

}


fun toBody(string: String): RequestBody {
    return string.toRequestBody("multipart/form-data".toMediaTypeOrNull())

}

fun prepareMultiPart(partName: String, image: Any?): MultipartBody.Part {

    /*    var   imageFileBody = MultipartBody.Part.createFormData(partName, "image_"+".jpg", requestBody);
          imageArrayBody.add(imageFileBody);*/
    var requestFile: RequestBody? = null
    if (image is File) {

        requestFile = image
            .asRequestBody("*/*".toMediaTypeOrNull())
    } else if (image is ByteArray) {
        requestFile = image
            .toRequestBody(
                "*/*".toMediaTypeOrNull(),
                0, image.size
            )
    }
    if (image is String) {
        val attachmentEmpty = "".toRequestBody("text/plain".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, "", attachmentEmpty)
    } else
        return MultipartBody.Part.createFormData(partName, (image as File).name, requestFile!!)
}


/*
fun Activity.showErrorAlert(msg: String) {
    Alerter.create(this)
        .setTitle(getString(R.string.error_))
        .setTitleAppearance(R.style.AlertTextAppearanceTitle)
        .setText(msg)
        .setTextAppearance(R.style.AlertTextAppearanceText)
        .setBackgroundColorRes(android.R.color.holo_red_light)
        .show()
}

fun Activity.showNoInternetAlert(msg: String, listener: OnNoInternetConnectionListener) {
    Alerter.create(this)
        .setTitle(getString(R.string.error_))
        .setTitleAppearance(R.style.AlertTextAppearanceTitle)
        .setText(msg)
        .setTextAppearance(R.style.AlertTextAppearanceText)
        .setBackgroundColorRes(android.R.color.holo_red_light)
        .addButton(getString(R.string.retry), R.style.AlertButton) {
            listener.onRetryApi()
        }
        .show()
}
*/


fun toast(message: String) {
    Toast.makeText(AppController.mInstance, message, Toast.LENGTH_SHORT).show()
}

fun Context.checkIfHasNetwork(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

/**
 * Method for Opening Images
 */
fun openImagePopUp(imageRes: String?, ctx: Context) {
    val popup: View
    val layoutInflater: LayoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    if (layoutInflater != null) {
        popup = layoutInflater.inflate(R.layout.porflioeimage_popup, null)
        val popupWindow = PopupWindow(
            popup,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow.showAtLocation(popup, Gravity.CENTER, 0, 0)
        popupWindow.isTouchable = false
        popupWindow.isOutsideTouchable = false
        val headImagePopUp = popup.findViewById<PhotoView>(R.id.headImagePopUp)
        val backPress = popup.findViewById<ImageView>(R.id.backpress)
        backPress.setOnClickListener {
            popupWindow.dismiss()
        }

        Glide.with(ctx).load(imageRes).into(headImagePopUp)

    }
}

fun String.firstCap() = this.lowercase().replaceFirstChar {
    it.uppercase()
}
fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
fun Activity.showToast(message: String){
    Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
}
fun View.isVisible() {
    visibility = View.VISIBLE
}

fun Double.toRupeeWithBigSymbol(): SpannableStringBuilder {
    val rupeeSymbol = "₹"
    val amount = "%.2f".format(this)

    val spannable = SpannableStringBuilder(rupeeSymbol + amount)
    spannable.setSpan(RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // Make the rupee symbol 1.5x larger

    return spannable
}

fun Int.toRupeeWithBigSymbol(): SpannableStringBuilder {
    return this.toDouble().toRupeeWithBigSymbol()
}
fun View.isGone() {
    visibility = View.GONE
}

fun View.isInvisible() {
    visibility = View.INVISIBLE
}



