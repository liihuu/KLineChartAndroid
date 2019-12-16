package com.liihuu.kline.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 格式化时间
 * @receiver Long?
 * @param pattern String
 * @return String
 */
fun Long?.formatDate(pattern: String = "yyyy-MM-dd HH:mm"): String = this?.run {
    val date = Date(this)
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    formatter.format(date)
} ?: "--"

/**
 * 格式化小数位数
 * @receiver Number?
 * @param decimal Int
 * @return String
 */
fun Number?.formatDecimal(decimal: Int = 4): String = this?.run {
    String.format("%.${decimal}f", this)
} ?: "--"