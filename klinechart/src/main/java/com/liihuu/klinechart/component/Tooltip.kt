package com.liihuu.klinechart.component

import android.graphics.*
import com.liihuu.klinechart.internal.utils.Utils
import com.liihuu.klinechart.model.KLineModel

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/29-10:56
 */
class Tooltip {
    /**
     * 指标提示显示规则
     */
    class IndicatorDisplayRule {
        companion object {
            /**
             * 总是显示
             */
            const val ALWAYS = 0

            /**
             * 跟随十字光标显示
             */
            const val FOLLOW_CROSS = 1

            /**
             * 一直不显示
             */
            const val NONE = 2
        }
    }

    /**
     * 基础数据提示自定义绘制
     *
     * @Author lihu hu_li888@foxmail.com
     * @Date 2019-05-14-15:03
     */
    interface DrawGeneralDataListener {
        /**
         * 绘制
         * @param canvas Canvas 画布
         * @param paint Paint 画笔
         * @param kLineModel KLineModel 当前数据
         * @param point PointF 当前点
         * @param tooltip Tooltip 配置
         * @param chartRect RectF 矩形绘制区域
         */
        fun draw(canvas: Canvas, paint: Paint, point: PointF, tooltip: Tooltip, chartRect: RectF, kLineModel: KLineModel)
    }

    /**
     * 基础数据格式化
     */
    interface GeneralDataFormatter {
        /**
         * 生成label
         * @return MutableList<String>
         */
        fun generatedLabels(): MutableList<String>

        /**
         * 生成值
         * @return MutableList<String>
         */
        fun generatedValues(kLineModel: KLineModel): MutableList<String>

        /**
         * 生成样式
         * @param paint Paint
         */
        fun generatedStyle(paint: Paint, kLineModel: KLineModel, tooltip: Tooltip, labelPos: Int)
    }

    /**
     * 值格式化接口
     */
    interface ValueFormatter {
        companion object {
            /**
             * 值的位置  x轴
             */
            const val X_AXIS = 0
            /**
             * 值的位置  y轴
             */
            const val Y_AXIS = 1
            /**
             * 值的位置  图上
             */
            const val CHART = 2
        }

        /**
         * 格式化
         * @param seat Int
         * @param indicatorType String?
         * @param value String
         * @return String
         */
        fun format(seat: Int, indicatorType: String?, value: String): String
    }

    /**
     * 线样式
     */
    var crossLineStyle = Component.LineStyle.SOLID

    /**
     * 线虚线值
     */
    var crossLineDashValues = floatArrayOf(8f, 6f)

    /**
     * 十字光标线尺寸
     */
    var crossLineSize = 1f

    /**
     * 十字光标线颜色
     */
    var crossLineColor = Color.parseColor("#505050")

    /**
     * 十字光标显示的文字框的线尺寸
     */
    var crossTextRectStrokeLineSize = Utils.convertDpToPixel(1f)

    /**
     * 十字光标显示的文字框的线颜色
     */
    var crossTextRectStrokeLineColor = Color.parseColor("#EDEDED")

    /**
     * 十字光标显示的文字框的填充颜色
     */
    var crossTextRectFillColor = Color.parseColor("#505050")

    /**
     * 十字光标文字颜色
     */
    var crossTextColor = Color.parseColor("#EDEDED")

    /**
     * 十字光标文字四周边距
     */
    var crossTextMarginSpace = Utils.convertDpToPixel(3f)

    /**
     * 十字光标文字大小
     */
    var crossTextSize = Utils.convertDpToPixel(10f)

    /**
     * 基础数据提示框边框线尺寸
     */
    var generalDataRectStrokeLineSize = Utils.convertDpToPixel(1f)

    /**
     * 基础数据提示框边框线颜色
     */
    var generalDataRectStrokeLineColor = Color.parseColor("#505050")

    /**
     * 基础数据提示框填充颜色
     */
    var generalDataRectFillColor = Color.parseColor("#99000000")

    /**
     * 基础数据文字大小
     */
    var generalDataTextSize = Utils.convertDpToPixel(10f)

    /**
     * 基础数据文字颜色
     */
    var generalDataTextColor = Color.parseColor("#EDEDED")

    /**
     * 基础数据上涨颜色
     */
    var generalDataIncreasingColor = Color.parseColor("#5DB300")

    /**
     * 基础数据下跌颜色
     */
    var generalDataDecreasingColor = Color.parseColor("#FF4A4A")

    /**
     * 基础数据格式化
     */
    var generalDataFormatter: GeneralDataFormatter? = null

    /**
     * 指标提示规则
     */
    var indicatorDisplayRule = IndicatorDisplayRule.ALWAYS

    /**
     * 指标提示文字大小
     */
    var indicatorTextSize = Utils.convertDpToPixel(10f)

    /**
     * 自定义绘制常规数据提示
     */
    var drawGeneralDataListener: DrawGeneralDataListener? = null

    /**
     * 数据格式化
     */
    var valueFormatter: ValueFormatter? = null
}