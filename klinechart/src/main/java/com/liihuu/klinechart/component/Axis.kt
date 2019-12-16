package com.liihuu.klinechart.component

import android.graphics.Color
import android.graphics.Paint
import com.liihuu.klinechart.internal.utils.Utils

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/28-21:48
 */
abstract class Axis {

    /**
     * 是否显示轴线
     */
    var displayAxisLine = true

    /**
     * 轴线的颜色
     */
    var axisLineColor = Color.parseColor("#707070")

    /**
     * 轴线的尺寸
     */
    var axisLineSize = 1f

    /**
     * 是否显示轴线上的文字
     */
    var displayTickText = true

    /**
     * 分割文字颜色
     */
    var tickTextColor = Color.parseColor("#707070")

    /**
     * 分割文字的大小
     */
    var tickTextSize = Utils.convertDpToPixel(10f)

    /**
     * 是否显示tick线
     */
    var displayTickLine = true

    /**
     * 轴线上文字线的高度
     */
    var tickLineSize = Utils.convertDpToPixel(3f)

    /**
     * 是否显示分割线
     */
    var displaySeparatorLine = false

    /**
     * 分割线的尺寸
     */
    var separatorLineSize = 1f

    /**
     * 分割线颜色
     */
    var separatorLineColor = Color.parseColor("#B8B8B8")

    /**
     * 分割线类型
     */
    var separatorLineStyle = Component.LineStyle.DASH

    /**
     * 分割线虚线值
     */
    var separatorLineDashValues = floatArrayOf(15f, 10f)

    /**
     * 文字的margin
     */
    var textMarginSpace = Utils.convertDpToPixel(3f)

    /**
     * 用于测量的画笔
     */
    val paint = Paint()
}