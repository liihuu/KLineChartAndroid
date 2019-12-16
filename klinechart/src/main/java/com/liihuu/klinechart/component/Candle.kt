package com.liihuu.klinechart.component

import android.graphics.*
import com.liihuu.klinechart.internal.utils.Utils

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/30-09:42
 */
class Candle {
    /**
     * 自定义绘制蜡烛图上的价格标记
     */
    interface DrawPriceMarkListener {
        companion object {
            /**
             * 最高价
             */
            const val HIGHEST = 0

            /**
             * 最低价
             */
            const val LOWEST = 1

            /**
             * 最新价
             */
            const val LAST = 2
        }

        /**
         * 绘制
         * @param canvas Canvas
         * @param paint Paint
         * @param drawType Int
         * @param point PointF
         * @param chartRect RectF
         * @param candle Candle
         * @param price Double
         */
        fun draw(canvas: Canvas, paint: Paint, drawType: Int, point: PointF, chartRect: RectF, candle: Candle, price: Double)
    }

    /**
     * 蜡烛图样式
     */
    class CandleStyle {
        companion object {
            /**
             * 全实心
             */
            const val SOLID = 0

            /**
             * 全空心
             */
            const val STROKE = 1

            /**
             * 涨空心
             */
            const val INCREASING_STROKE = 2

            /**
             * 跌空心
             */
            const val DECREASING_STROKE = 3

            /**
             * 美国线
             */
            const val OHLC = 4
        }
    }

    /**
     * 蜡烛图风格
     */
    class ChartStyle {
        companion object {
            /**
             * 蜡烛图
             */
            const val CANDLE = 0

            /**
             * 分时线
             */
            const val TIME_LINE = 1
        }
    }

    /**
     * 值格式化接口
     */
    interface ValueFormatter {
        /**
         * 格式化
         * @param value String?
         * @return String
         */
        fun format(value: String?): String
    }

    /**
     * 上涨颜色
     */
    var increasingColor = Color.parseColor("#5DB300")

    /**
     * 下跌颜色
     */
    var decreasingColor = Color.parseColor("#FF4A4A")

    /**
     * 蜡烛图样式
     */
    var candleStyle = CandleStyle.SOLID

    /**
     * 图类型
     */
    var chartStyle = ChartStyle.CANDLE

    /**
     * 是否显示最大价格标记
     */
    var displayHighestPriceMark = true

    /**
     * 是否显示最低价格标记
     */
    var displayLowestPriceMark = true

    /**
     * 最低最高价格标记文字颜色
     */
    var lowestHighestPriceMarkTextColor = Color.parseColor("#898989")

    /**
     * 最低最高价格标记文字大小
     */
    var lowestHighestPriceMarkTextSize = Utils.convertDpToPixel(10f)

    /**
     * 是否显示最新价标记
     */
    var displayLastPriceMark = true

    /**
     * 最新价标记线样式
     */
    var lastPriceMarkLineStyle = Component.LineStyle.DASH

    /**
     * 最新价标记线虚线值
     */
    var lastPriceMarkLineDashValues = floatArrayOf(15f, 10f)

    /**
     * 最新价标记线尺寸
     */
    var lastPriceMarkLineSize = Utils.convertDpToPixel(1f)

    /**
     * 最新价标记线颜色
     */
    var lastPriceMarkLineColor = Color.parseColor("#B9B9B9")

    /**
     * 分时线尺寸
     */
    var timeLineSize = Utils.convertDpToPixel(1f)

    /**
     * 分时线颜色
     */
    var timeLineColor = Color.parseColor("#D8D8D8")

    /**
     * 分时线填充色
     */
    var timeLineFillColor = Color.parseColor("#20D8D8D8")

    /**
     * 分时均线颜色
     */
    var timeAverageLineColor = Color.parseColor("#F5A623")

    /**
     * 自定义最新价格标记
     */
    var drawPriceMarkListener: DrawPriceMarkListener? = null

    /**
     * 值格式化
     */
    var valueFormatter: ValueFormatter? = null
}