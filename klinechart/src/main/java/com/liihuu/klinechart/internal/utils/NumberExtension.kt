@file:JvmName("NumberUtils")

package com.liihuu.klinechart.internal.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-12-18:16
 */

/**
 * 默认格式化两位小数
 * @receiver Number?
 * @return String
 */
@JvmSynthetic
internal fun Number?.defaultFormatDecimal(): String = this?.run {
    String.format("%.2f", this)
} ?: "--"

/**
 * 默认将时间戳转换为格式化时间
 * @receiver Long?
 * @return String
 */
@JvmSynthetic
internal fun Long?.defaultFormatDate(pattern: String = "MM-dd HH:mm"): String = this?.run {
    val date = Date(this)
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    formatter.format(date)
} ?: "--"
