package com.gvkorea.gvktune.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {

    var nowTime: Long = 0
    lateinit var date: Date

    fun getTime(): String {
        nowTime = System.currentTimeMillis()
        date = Date(nowTime)
        val sdf = SimpleDateFormat("hh_mm", Locale.KOREA)
        return sdf.format(date)
    }

    fun getDate(): String {
        nowTime = System.currentTimeMillis()
        date = Date(nowTime)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        return sdf.format(date)
    }
}