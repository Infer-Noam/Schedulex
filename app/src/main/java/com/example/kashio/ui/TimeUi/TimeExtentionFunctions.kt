package com.example.kashio.ui.TimeUi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Calendar

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