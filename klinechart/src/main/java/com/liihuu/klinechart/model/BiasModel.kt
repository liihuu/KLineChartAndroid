package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:43
 */
@Parcelize
data class BiasModel(
    val bias1: Double?,
    val bias2: Double?,
    val bias3: Double?
): Parcelable