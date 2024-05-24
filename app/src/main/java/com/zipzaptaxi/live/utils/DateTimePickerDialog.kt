package com.zipzaptaxi.live.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getTime(textView: TextView, context: Context){

    val cal = Calendar.getInstance()

    val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)

        textView.text = SimpleDateFormat("HH:mm").format(cal.time)
    }

    textView.setOnClickListener {
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }
}

fun getDate(textView: TextView, context: Context) {
    val cal = Calendar.getInstance()

    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        textView.text = formatDate(cal.time)
    }

    textView.setOnClickListener {
        // Create DatePickerDialog with minimum date after today
        val dialog = DatePickerDialog(
            context, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // Set minimum date after today
        cal.add(Calendar.DAY_OF_MONTH, 1) // Add one day to the current date
        dialog.datePicker.minDate = cal.timeInMillis // Set minimum date

        // Show dialog
        dialog.show()
    }
}



fun getCurrentMaxDate(textView: TextView, context: Context) {
    val cal = Calendar.getInstance()

    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        textView.text = formatDate(cal.time)
    }

    textView.setOnClickListener {
        // Create DatePickerDialog with maximum date as today
        val dialog = DatePickerDialog(
            context, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // Set maximum date as today
        dialog.datePicker.maxDate = cal.timeInMillis // Set maximum date

        // Show dialog
        dialog.show()
    }
}



fun getCurrentTimeStamp(timeValue: String): Long {
    var formatter: DateFormat = SimpleDateFormat("MM dd yyyy HH:mm")

    var date = formatter.parse(timeValue) as Date
    var output = date.time / 1000L
    var str = java.lang.Long.toString(output)
    //var timestamp = java.lang.Long.parseLong(str) * 1000
    var timestamp = java.lang.Long.parseLong(str)
    return timestamp
}
