package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:39
 */
@Parcelize
data class BollModel(
    val up: Double?,
    val mid: Double?,
    val dn: Double?
): Parcelable