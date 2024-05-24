package com.zipzaptaxi.live.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val myFormat = "dd-MM-yyyy" // mention the format you need
//val myFormat = "yyyy-MM-dd" // mention the format you need
val sdf = SimpleDateFormat(myFormat)

fun formatDate(date: Date, formatPattern: String = myFormat): String {
    sdf.applyPattern(formatPattern)

    return sdf.format(date)
}


fun convertDateFormat( date: String): String{
    var spf = SimpleDateFormat("dd-MM-yyyy")
    val newDate = spf.parse(date)
    spf = SimpleDateFormat("MM dd yyyy")
    var dateChanged = spf.format(newDate)

    return dateChanged


}

fun String.extractTimeIn12HourFormat(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date: Date = dateFormat.parse(this) ?: Date()
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return timeFormat.format(date)
}
