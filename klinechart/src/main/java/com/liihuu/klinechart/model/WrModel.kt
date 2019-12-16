package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-05-14-19:13
 */
@Parcelize
data class WrModel(
    val wr1: Double?,
    val wr2: Double?,
    val wr3: Double?
): Parcelable