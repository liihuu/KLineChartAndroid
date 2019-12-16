package com.liihuu.klinechart.chart

import android.graphics.Canvas
import android.graphics.Path
import com.liihuu.klinechart.component.Indicator
import kotlin.math.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/19 09:36
 */
internal abstract class AxisChart : Chart() {

    /**
     * label值数组
     */
    var labelValues = DoubleArray(0)

    /**
     * 值个数
     */
    var valueCount = 0

    /**
     * 轴上文字小数点
     */
    var axisValueDecimals = 0

    /**
     * 分割线path
     */
    val separatorLinePath = Path()

    /**
     * 绘制坐标轴线
     * @param canvas Canvas
     */
    abstract fun drawAxisLine(canvas: Canvas)

    /**
     * 绘制坐标轴文字
     * @param canvas Canvas
     * @param indicatorType String
     */
    abstract fun drawAxisLabels(canvas: Canvas, indicatorType: String = Indicator.Type.NO)

    /**
     * 绘制坐标轴分割线
     * @param canvas Canvas
     */
    abstract fun drawSeparatorLines(canvas: Canvas)

    /**
     * 绘制tick线
     * @param canvas Canvas
     */
    abstract fun drawTickLines(canvas: Canvas)

    /**
     * 计算显示的区间
     * @param min Float
     * @param max Float
     * @return Float
     */
    abstract fun calcRange(min: Float, max: Float): Float

    /**
     * 计算轴上要显示的分割的间距
     */
    abstract fun computeAxis(labelCount: Int)

    /**
     * 是否数据会超过整个绘制区域
     * @return Boolean
     */
    open fun isFillChart(): Boolean = true

    /**
     * 计算轴上需要显示的label
     * @param min Float
     * @param max Float
     * @param labelCount Int
     */
    open fun computeAxisValues(min: Float, max: Float, labelCount: Int) {
        val range = calcRange(min, max)
        if (labelCount == 0 || range <= 0 || range.isInfinite()) {
            this.labelValues = doubleArrayOf()
            this.valueCount = 0
            return
        }

        if (isFillChart()) {
            val rawInterval = range / labelCount
            var interval = roundToNextSignificant(rawInterval.toDouble())
            val intervalMagnitude = roundToNextSignificant(10.0.pow(log10(interval)))
            val intervalSigDigit = (interval / intervalMagnitude).toInt()
            if (intervalSigDigit > 5) {
                interval = floor(10.0 * intervalMagnitude)
            }

            var n = 0

            val first = if (interval == 0.0) 0.0 else (ceil(min / interval) * interval)

            val last = if (interval == 0.0) 0.0 else (floor(max / interval) * interval).nextUp()
            var f: Double = first

            if (interval != 0.0) {
                while (f <= last) {
                    ++n
                    f += interval
                }
            }
            this.valueCount = n
            this.labelValues = DoubleArray(n)

            var i = 0
            f = first
            while (i < n) {
                if (f == 0.0) {
                    f = 0.0
                }
                this.labelValues[i] = f
                f += interval
                ++i
            }
            this.axisValueDecimals = if (interval < 1) {
                ceil(-log10(interval)).toInt()
            } else {
                0
            }
        } else {
            fixComputeAxisValues()
        }
    }

    open fun fixComputeAxisValues() {}

    private fun roundToNextSignificant(v: Double): Double {
        if (v == Double.POSITIVE_INFINITY ||
            v == Double.NEGATIVE_INFINITY ||
            v.isNaN() ||
            v == 0.0) {
            return 0.0
        }
        var n = v
        if (n < 0.0) {
            n = -n
        }
        val d = ceil(log10(n))
        val pw = 1 - d.toInt()
        val magnitude = 10.0.pow(pw)
        val shifted = (v * magnitude).roundToLong()
        return shifted / magnitude
    }
}