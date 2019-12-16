package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:47
 */
@Parcelize
data class DmaModel(
    val dif: Double?,
    val difMa: Double?
): Parcelable