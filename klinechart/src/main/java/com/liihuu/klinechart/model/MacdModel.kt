package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:35
 */
@Parcelize
data class MacdModel(
    val diff: Double?,
    val dea: Double?,
    val macd: Double?
): Parcelable