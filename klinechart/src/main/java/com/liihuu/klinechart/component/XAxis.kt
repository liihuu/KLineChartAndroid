package com.liihuu.klinechart.component

import com.liihuu.klinechart.internal.utils.Utils
import kotlin.math.max
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/28-22:02
 */
class XAxis: Axis() {
    /**
     * 值格式化
     */
    interface ValueFormatter {
        /**
         * 格式化
         * @param value String
         */
        fun format(value: Long): String
    }

    /**
     * x轴最大高度
     */
    var xAxisMaxHeight = Utils.convertDpToPixel(20f)

    /**
     * x轴最小高度
     */
    var xAxisMinHeight = Utils.convertDpToPixel(20f)

    /**
     * 值格式化
     */
    var valueFormatter: ValueFormatter? = null

    /**
     * 计算x轴需要的高度
     * @return Float
     */
    fun getRequiredHeightSpace(): Float {

        paint.textSize = tickTextSize

        var height = Utils.calcTextHeight(paint, "T") + textMarginSpace
        if (displayTickLine) {
            height += tickLineSize
        }
        if (displayAxisLine) {
            height += axisLineSize
        }
        val maxHeight = if (xAxisMaxHeight > 0f) xAxisMaxHeight else height
        height = max(xAxisMinHeight, min(height, maxHeight))

        return height
    }
}