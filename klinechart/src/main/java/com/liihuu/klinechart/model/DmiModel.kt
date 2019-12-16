package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:38
 */
@Parcelize
data class DmiModel(
    val pdi: Double?,
    val mdi: Double?,
    val adx: Double?,
    val adxr: Double?
): Parcelable