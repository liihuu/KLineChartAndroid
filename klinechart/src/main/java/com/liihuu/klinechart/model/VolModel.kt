package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:32
 */
@Parcelize
data class VolModel(
    /**
     * 成交量
     */
    val num: Double,

    /**
     * 成交量5日均线
     */
    val ma5: Double?,

    /**
     * 成交量10日均线
     */
    val ma10: Double?,

    /**
     * 成交量20日均线
     */
    val ma20: Double?
): Parcelable