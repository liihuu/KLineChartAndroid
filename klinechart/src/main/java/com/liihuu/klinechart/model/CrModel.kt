package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:46
 */
@Parcelize
data class CrModel(
    val cr: Double?,
    val ma1: Double?,
    val ma2: Double?,
    val ma3: Double?,
    val ma4: Double?
): Parcelable