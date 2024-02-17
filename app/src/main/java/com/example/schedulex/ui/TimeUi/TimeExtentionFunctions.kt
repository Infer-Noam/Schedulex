package com.example.schedulex.ui.TimeUi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    return "${calendar[Calendar.DAY_OF_MONTH]}".padStart(2, '0') + "-" +
            "${(calendar[Calendar.MONTH] + 1)}".padStart(2, '0') + "-" +
            "${calendar[Calendar.YEAR]}".padStart(4, '0')
}

fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    return "${calendar[Calendar.HOUR_OF_DAY]}".padStart(2, '0') + ":" +
            "${(calendar[Calendar.MINUTE])}".padStart(2, '0')
}

fun Color.toHex(): String {
    return String.format("#%08X", this.toArgb())
}

fun getLength(endTime: String, startTime: String): Int{
    return ((endTime.substring(0,2).toInt() * 60 + endTime.substring(3,5).toInt()) -
            (startTime.substring(0,2).toInt() * 60 + startTime.substring(3,5).toInt()))
}

fun getTime(time: String): Int{ //return the time in minutes 02:10 -> 2 * 60 + 10
    return (time.substring(0,2).toInt() * 60 + time.substring(3,5).toInt())
}

fun rgbToHsl(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val l = (max + min) / 2

    val h: Float
    val s: Float

    if (max == min) {
        h = 0f
        s = 0f
    } else {
        val d = max - min

        s = if (l > 0.5) d / (2 - max - min) else d / (max + min)

        h = when (max) {
            r -> (g - b) / d + (if (g < b) 6 else 0)
            g -> (b - r) / d + 2
            else -> (r - g) / d + 4
        }
    }

    return floatArrayOf(h, s, l)
}


fun adjustDateTime(dateTime: String, minutes: Int): String {
    // Define the date time format
    val formatter = DateTimeFormatter.ofPattern("HH-mm-dd-MM-yyyy")

    // Parse the date time string to a LocalDateTime object
    val parsedDateTime = LocalDateTime.parse(dateTime, formatter)

    // Add the minutes to the date time
    val adjustedDateTime = parsedDateTime.plusMinutes(minutes.toLong())

    // Format the adjusted date time back to a string

    return adjustedDateTime.format(formatter)
}

fun dateTimeToLocalDateTime(dateTime: String): LocalDateTime {
    // Define the date time format
    val formatter = DateTimeFormatter.ofPattern("HH-mm-dd-MM-yyyy")

    // Parse the date time string to a LocalDateTime object

    return LocalDateTime.parse(dateTime, formatter)
}

fun calculateDifference(date1: String, date2: String): Long {
    if (date1.isEmpty() || date2.isEmpty()) {
        return -1 // not valid
    }

    val format = SimpleDateFormat("HH-mm-dd-MM-yyyy", Locale.getDefault())

    val d1 = format.parse(date1)
    val d2 = format.parse(date2)

    val diff = d2.time - d1.time

    return TimeUnit.MILLISECONDS.toMinutes(diff)
}


fun notificationMessage(timeInMinutes: Int): String{
    val hours = (timeInMinutes / 60)
    val minutes = (timeInMinutes % 60)

    val hoursText = when(hours){0 -> ""; 1,-1 -> "1 hour"; else -> "${hours.absoluteValue} hours"}
    val minutesText = when(minutes){0 -> ""; 1,-1 -> "1 minute"; else -> "${minutes.absoluteValue} minutes"}
    val combinedTime = if(hoursText == "" && minutesText != ""){minutesText} else if(hoursText != "" && minutesText == ""){hoursText}
    else if(hoursText != ""){"$hoursText and $minutesText" } else{""}

    return if(hours * 60 + minutes > 0) { "Starts in $combinedTime" } else if(hours * 60 + minutes < 0) { "Started $combinedTime ago" } else { "Is starting"}

}