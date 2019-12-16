package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:42
 */
@Parcelize
data class RsiModel(
    val rsi1: Double?,
    val rsi2: Double?,
    val rsi3: Double?
): Parcelable