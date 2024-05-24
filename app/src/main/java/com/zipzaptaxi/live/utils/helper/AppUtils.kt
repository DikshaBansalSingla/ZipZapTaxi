package com.zipzaptaxi.live.utils.helper

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tapadoo.alerter.Alerter
import com.zipzaptaxi.live.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AppUtils {


    companion object {
        //--------------------------Keyboard Hide ----------------------//
        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0
            )
        }

        //--------------------------Date ----------------------//
        fun dateInString(date: String, format: String): String {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            return sdf.format(date)
        }

        //--------------------------Layout blur ----------------------//
        fun applyDim(parent: ViewGroup, dimAmount: Float) {
            val dim: Drawable = ColorDrawable(Color.BLACK)
            dim.setBounds(0, 0, parent.width, parent.height)
            dim.alpha = (255 * dimAmount).toInt()
            val overlay = parent.overlay
            overlay.add(dim)
        }


        //--------------------------Layout clear blur----------------------//
        fun clearDim(parent: ViewGroup) {
            val overlay = parent.overlay
            overlay.clear()
        }
        @JvmStatic
        fun placePicker(activity: Activity,AUTOCOMPLETE_REQUEST_CODE:Int){
            val fields = listOf(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME)

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(activity)
            activity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        fun getDateTest(timeStamp: Long): String? {
            return try {
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val netDate = Date(timeStamp * 1000L)
                sdf.format(netDate)
            } catch (ex: java.lang.Exception) {
                "xx"
            }
        }

        fun getDatFromTimeStamp(timeStamp: Long): String? {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val netDate = Date(timeStamp)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun getDateMonthStamp(timeStamp: Long): String? {
            return try {
                val sdf = SimpleDateFormat("dd MMM")
                val netDate = Date(timeStamp)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }


        fun getDateTime(time: String?,format: String): String? {
            var time = time
            val orignalformat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault())
            var date: Date? = null
            try {
                date = orignalformat.parse(time)
                time = SimpleDateFormat(format, Locale.getDefault()).format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return time
        }

        fun milliseconds(date: String?): Long {
            //String date_ = date;
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            try {
                val mDate = sdf.parse(date)
                val timeInMilliseconds = mDate.time
                println("Date in milli :: $timeInMilliseconds")
                return timeInMilliseconds
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
        fun convertTimeToNotification(timestamp: Long): String? {
            val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp * 1000
            val outputFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            //outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return outputFormat.format(cal.time)
        }
        fun convertDateToTestTimeStamp(strDate: String):Long {

            val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = formatter.parse(strDate) as Date
            System.out.println("Today is " + date.time)
            return date.time/1000


//            val sdf = SimpleDateFormat("yyyy-MM-dd")
//            sdf.timeZone=TimeZone.getTimeZone("UTC")
//            val dateInString = strDate
//            val date = sdf.parse(dateInString)
//            System.out.println(date)
//            return (date.time/1000)
        }



        @JvmStatic
        fun showAlert(
            context: Activity,
            title: String,
            message: String,
            backgroundColorRes: Int,
            onHideListener: (() -> Unit)? = null
        ) {
            val alerter = Alerter.create(context)
                .setTitle(title)
                .setText(message)
                .setTextAppearance(R.style.AlertTextAppearanceText)
                .setBackgroundColorRes(backgroundColorRes)


            onHideListener?.let {
                alerter.setOnHideListener(it)
            }

            alerter.show()
        }

        @JvmStatic
        fun showErrorAlert(
            context: Activity,
            msg: String,
            onDismissAction: (() -> Unit)? = null
        ) {
            Alerter.create(context)
                .setTitle(context.getString(R.string.error_))
                .setTitleAppearance(R.style.AlertTextAppearanceTitle)
                .setText(msg)
                .setTextAppearance(R.style.AlertTextAppearanceText)
                .setBackgroundColorRes(android.R.color.holo_red_light)
                .setOnHideListener {
                    onDismissAction?.invoke()
                }
                .show()
        }


        @JvmStatic
        fun showSuccessAlert(
            context: Activity,
            msg: String,
            onBackPressed: Boolean = true
        ) {
            // Show the success alert message
             Alerter.create(context)
                .setTitle(context.getString(R.string.success))
                .setTitleAppearance(R.style.AlertTextAppearanceTitle)
                .setText(msg)
                .setTextAppearance(R.style.AlertTextAppearanceText)
                .setBackgroundColorRes(R.color.colorPrimary)
                .show()

            // Immediately invoke the back press action if needed
            if (onBackPressed) {
                context.onBackPressed()
            }

        }




        /* @JvmStatic
         fun showErrorAlert(context: Activity, msg: String) {
             Alerter.create(context)
                 .setTitle(context.getString(R.string.error_))
                 .setTitleAppearance(R.style.AlertTextAppearanceTitle)
                 .setText(msg)
                 .setTextAppearance(R.style.AlertTextAppearanceText)
                 .setBackgroundColorRes(android.R.color.holo_red_light)
                 .show()
         }

         @JvmStatic
         fun showSuccessAlert(context: Activity, msg: String) {
             Alerter.create(context)
                 .setTitle(context.getString(R.string.success))
                 .setTitleAppearance(R.style.AlertTextAppearanceTitle)
                 .setText(msg)
                 .setTextAppearance(R.style.AlertTextAppearanceText)
                 .setBackgroundColorRes(R.color.colorPrimary)
                 .show()
         }*/


    }


}