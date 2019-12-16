package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:37
 */
@Parcelize
data class KdjModel(
    val k: Double?,
    val d: Double?,
    val j: Double?
): Parcelable