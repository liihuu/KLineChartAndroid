package com.liihuu.klinechart.component

import com.liihuu.klinechart.internal.utils.Utils
import kotlin.math.max
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/28-21:54
 */
class YAxis : Axis() {
    /**
     * y轴位置
     */
    class AxisPosition {
        companion object {
            /**
             * 左边
             */
            const val LEFT = 0

            /**
             * 右边
             */
            const val RIGHT = 1
        }
    }

    /**
     * 文字位置
     */
    class TextPosition {
        companion object {
            /**
             * 外部
             */
            const val OUTSIDE = 0

            /**
             * 内部
             */
            const val INSIDE = 1
        }
    }

    /**
     * 值格式化
     */
    interface ValueFormatter {
        /**
         * 格式化
         * @param indicatorType
         * @param value String
         */
        fun format(indicatorType: String, value: Double): String
    }

    /**
     * 是否绘制文字在轴线外
     */
    var yAxisTextPosition = TextPosition.INSIDE

    /**
     * y轴位置
     */
    var yAxisPosition = AxisPosition.RIGHT

    /**
     * y轴最大宽度
     */
    var yAxisMaxWidth = Utils.convertDpToPixel(20f)

    /**
     * y轴最小宽度
     */
    var yAxisMinWidth = 0f

    /**
     * 值格式
     */
    var valueFormatter: ValueFormatter? = null

    /**
     * 是否需要留间距绘制y轴
     * @return Boolean
     */
    fun needsOffset() = ((displayTickText || displayTickLine || textMarginSpace > 0f) && yAxisTextPosition == TextPosition.OUTSIDE) || displayAxisLine

    /**
     * 获取y轴需要的宽度
     * @return Float
     */
    fun getRequiredWidthSpace(): Float {

        paint.textSize = tickTextSize

        var width = 0f
        if (yAxisTextPosition == TextPosition.OUTSIDE) {
            width += Utils.calcTextWidth(paint, "0000000") + textMarginSpace
            if (displayTickLine) {
                width += tickLineSize
            }
        }

        if (displayAxisLine) {
            width += axisLineSize
        }

        val maxWidth = if (yAxisMaxWidth > 0f) yAxisMaxWidth else width
        width = min(maxWidth, max(width, yAxisMinWidth))
        return width
    }
}