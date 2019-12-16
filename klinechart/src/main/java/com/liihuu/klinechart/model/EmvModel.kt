package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-05-14-21:13
 */
@Parcelize
data class EmvModel(
    val emv: Double?,
    val maEmv: Double?
): Parcelable