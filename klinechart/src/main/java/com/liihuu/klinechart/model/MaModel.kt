package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 收盘价均线
 *
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/1/8-16:28
 */
@Parcelize
data class MaModel(
    /**
     * 5日均线
     */
    val ma5: Double,

    /**
     * 10日均线
     */
    val ma10: Double,

    /**
     * 20日均线
     */
    val ma20: Double,

    /**
     * 60日均线
     */
    val ma60: Double
): Parcelable