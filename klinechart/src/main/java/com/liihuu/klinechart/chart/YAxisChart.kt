package com.liihuu.klinechart.chart

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.liihuu.klinechart.KLineChartView
import com.liihuu.klinechart.component.Component
import com.liihuu.klinechart.component.Indicator
import com.liihuu.klinechart.component.YAxis
import com.liihuu.klinechart.internal.DataProvider
import com.liihuu.klinechart.internal.ViewPortHandler
import com.liihuu.klinechart.internal.utils.Utils
import com.liihuu.klinechart.model.KLineModel
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/29-22:41
 */
internal class YAxisChart(
    private val axis: YAxis,
    private val dataProvider: DataProvider,
    private val viewPortHandler: ViewPortHandler
) : AxisChart() {

    /**
     * 轴上要显示最小值
     */
    var axisMinimum = 0f

    /**
     * 轴上要显示的最大值
     */
    var axisMaximum = 0f

    /**
     * 轴上显示的最大与最小的间距
     */
    var axisRange = 0f

    /**
     * 自定义指标计算y轴上最大最小值
     */
    var calcYAxisMinMax: KLineChartView.CustomIndicatorListener.CalcYAxisMinMax? = null

    override fun draw(canvas: Canvas) {
        throw UnsupportedOperationException("Unsupported operation")
    }

    override fun drawAxisLine(canvas: Canvas) {
        if (!this.axis.displayAxisLine) {
            return
        }
        this.paint.apply {
            strokeWidth = axis.axisLineSize
            color = axis.axisLineColor
        }
        val endY = this.offsetTop + this.height
        if (this.axis.yAxisPosition == YAxis.AxisPosition.LEFT) {
            canvas.drawLine(this.viewPortHandler.contentLeft(), this.offsetTop, this.viewPortHandler.contentLeft(), endY, this.paint)
        } else {
            canvas.drawLine(this.viewPortHandler.contentRight(), this.offsetTop, this.viewPortHandler.contentRight(), endY, this.paint)
        }
    }

    /**
     * 绘制y轴上文字
     * @param canvas Canvas
     */
    override fun drawAxisLabels(canvas: Canvas, indicatorType: String) {
        if (!this.axis.displayTickText) {
            return
        }

        val initX: Float
        if (this.axis.yAxisPosition == YAxis.AxisPosition.LEFT) {
            initX = if (this.axis.yAxisTextPosition == YAxis.TextPosition.OUTSIDE) {
                if (this.axis.displayTickLine) {
                    viewPortHandler.contentLeft() - this.axis.tickLineSize - this.axis.textMarginSpace
                } else {
                    viewPortHandler.contentLeft() - this.axis.textMarginSpace
                }

            } else {
                if (this.axis.displayTickLine) {
                    this.viewPortHandler.contentLeft() + this.axis.tickLineSize + this.axis.textMarginSpace
                } else {
                    this.viewPortHandler.contentLeft() + this.axis.textMarginSpace
                }
            }
        } else {
            initX = if (this.axis.yAxisTextPosition == YAxis.TextPosition.OUTSIDE) {
                if (this.axis.displayTickLine) {
                    this.viewPortHandler.contentRight() + this.axis.tickLineSize + this.axis.textMarginSpace
                } else {
                    this.viewPortHandler.contentRight() + this.axis.textMarginSpace
                }

            } else {
                if (this.axis.displayTickLine) {
                    this.viewPortHandler.contentRight() - this.axis.tickLineSize - this.axis.textMarginSpace
                } else {
                    this.viewPortHandler.contentRight() - this.axis.textMarginSpace
                }
            }
        }

        this.paint.apply {
            textSize = axis.tickTextSize
            color = axis.tickTextColor
            style = Paint.Style.FILL
        }
        for (i in this.labelValues.indices) {
            val labelY = getY(this.labelValues[i])
            var label = BigDecimal(this.labelValues[i]).setScale(this.axisValueDecimals, BigDecimal.ROUND_DOWN).toString()
            label = this.axis.valueFormatter?.format(
                indicatorType, this.labelValues[i]
            ) ?: label
            val labelHeight = Utils.calcTextHeight(this.paint, label)
            val halfLabelHeight = labelHeight / 2
            if (checkShowLabel(labelY, labelHeight)) {
                if ((this.axis.yAxisPosition == YAxis.AxisPosition.LEFT && this.axis.yAxisTextPosition == YAxis.TextPosition.OUTSIDE) ||
                    (this.axis.yAxisPosition == YAxis.AxisPosition.RIGHT && this.axis.yAxisTextPosition != YAxis.TextPosition.OUTSIDE)) {
                    this.paint.textAlign = Paint.Align.RIGHT
                } else {
                    this.paint.textAlign = Paint.Align.LEFT
                }
                val startY = labelY + halfLabelHeight
                canvas.drawText(label, initX, startY, this.paint)
            }
        }
    }

    /**
     * 绘制y轴
     * @param canvas Canvas
     */
    override fun drawSeparatorLines(canvas: Canvas) {
        if (!this.axis.displaySeparatorLine) {
            return
        }
        this.paint.apply {
            strokeWidth = axis.separatorLineSize
            color = axis.separatorLineColor
            style = Paint.Style.STROKE
            textSize = axis.tickTextSize
        }

        val labelHeight = Utils.calcTextHeight(this.paint, "0")

        if (this.axis.separatorLineStyle == Component.LineStyle.DASH) {
            this.paint.pathEffect = DashPathEffect(this.axis.separatorLineDashValues, 0f)
        }

        for (value in this.labelValues) {
            val y = getY(value)
            if (checkShowLabel(y, labelHeight)) {
                this.separatorLinePath.apply {
                    reset()
                    moveTo(viewPortHandler.contentLeft(), y)
                    lineTo(viewPortHandler.contentRight(), y)
                }
                canvas.drawPath(this.separatorLinePath, this.paint)
            }
        }
        this.paint.pathEffect = null
    }

    /**
     * 绘制tick线
     * @param canvas Canvas
     */
    override fun drawTickLines(canvas: Canvas) {
        if (!this.axis.displayTickLine) {
           return
        }
        this.paint.apply {
            strokeWidth = 1f
            color = axis.axisLineColor
            style = Paint.Style.STROKE
            textSize = axis.tickTextSize
        }

        val labelHeight = Utils.calcTextHeight(this.paint, "0")
        val startX: Float
        val endX: Float
        if (this.axis.yAxisPosition == YAxis.AxisPosition.LEFT) {
            startX = this.viewPortHandler.contentLeft()
            endX = if (this.axis.yAxisTextPosition == YAxis.TextPosition.OUTSIDE) {
                startX - this.axis.tickLineSize
            } else {
                startX + this.axis.tickLineSize
            }
        } else {
            startX = this.viewPortHandler.contentRight()
            endX = if (this.axis.yAxisTextPosition == YAxis.TextPosition.OUTSIDE) {
                startX + this.axis.tickLineSize
            } else {
                startX - this.axis.tickLineSize
            }
        }
        for (value in this.labelValues) {
            val y = getY(value)
            if (checkShowLabel(y, labelHeight)) {
                canvas.drawLine(startX, y, endX, y, this.paint)
            }
        }
    }

    /**
     * 检查是否需要真正显示label及tick线 分割线
     * @param y Float
     * @param labelHeight Float
     * @return Boolean
     */
    private fun checkShowLabel(y: Float, labelHeight: Int) = y > this.offsetTop + labelHeight && y < this.offsetTop + this.height - labelHeight

    /**
     * 计算y轴数据的最大最小值
     * @param indicatorType String
     * @param isMainYAxis Boolean
     * @param isTimeLine Boolean
     */
    fun getYAxisDataMinMax(indicatorType: String, isMainYAxis: Boolean, isTimeLine: Boolean) {
        val dataList = this.dataProvider.dataList
        val min = this.dataProvider.visibleDataMinPos
        val max = min(min + this.dataProvider.visibleDataCount, dataList.size)
        val minMaxArray = doubleArrayOf(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
        if (isTimeLine) {
            for (i in min until max) {
                val model = dataList[i]
                minMaxArray[0] = min(model.averagePrice, minMaxArray[0])
                minMaxArray[0] = min(model.closePrice, minMaxArray[0])
                minMaxArray[1] = max(model.averagePrice, minMaxArray[1])
                minMaxArray[1] = max(model.closePrice, minMaxArray[1])
            }
        } else {
            for (i in min until max) {
                val kLineModel = dataList[i]
                calcIndexMinMax(indicatorType, kLineModel, minMaxArray)
                if (isMainYAxis) {
                    minMaxArray[0] = min(kLineModel.lowPrice, minMaxArray[0])
                    minMaxArray[1] = max(kLineModel.highPrice, minMaxArray[1])
                }
            }
        }

        if (minMaxArray[0] != Double.POSITIVE_INFINITY && minMaxArray[1] != Double.NEGATIVE_INFINITY) {
            this.axisMinimum = minMaxArray[0].toFloat()
            this.axisMaximum = minMaxArray[1].toFloat()
        }
    }

    /**
     * 计算指标值的最大最小值
     * @param indicatorType String
     * @param kLineModel KLineModel
     * @param minMaxArray DoubleArray
     * @return DoubleArray
     */
    private fun calcIndexMinMax(indicatorType: String, kLineModel: KLineModel, minMaxArray: DoubleArray): DoubleArray {
        when (indicatorType) {
            Indicator.Type.NO -> {}
            Indicator.Type.MA -> {
                minMaxArray[0] = min(kLineModel.ma?.ma5 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.ma?.ma10 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.ma?.ma20 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.ma?.ma60 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.ma?.ma5 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.ma?.ma10 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.ma?.ma20 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.ma?.ma60 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.MACD -> {
                minMaxArray[0] = min(kLineModel.macd?.dea ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.macd?.diff ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.macd?.macd ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.macd?.dea ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.macd?.diff ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.macd?.macd ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.VOL -> {
                minMaxArray[0] = min(kLineModel.vol?.ma5 ?: 0.0, 0.0)
                minMaxArray[0] = min(kLineModel.vol?.ma10 ?: 0.0, 0.0)
                minMaxArray[0] = min(kLineModel.vol?.ma20 ?: 0.0, 0.0)
                minMaxArray[0] = min(kLineModel.vol?.num ?: 0.0, 0.0)
                minMaxArray[1] = max(kLineModel.vol?.ma5 ?: 0.0, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.vol?.ma10 ?: 0.0, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.vol?.ma20 ?: 0.0, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.vol?.num ?: 0.0, minMaxArray[1])
            }
            Indicator.Type.BOLL -> {
                minMaxArray[0] = min(kLineModel.boll?.up ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.boll?.mid ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.boll?.dn ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.lowPrice, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.boll?.up ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.boll?.mid ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.boll?.dn ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.highPrice, minMaxArray[1])
            }
            Indicator.Type.BIAS -> {
                minMaxArray[0] = min(kLineModel.bias?.bias1 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.bias?.bias2 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.bias?.bias3 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.bias?.bias1 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.bias?.bias2 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.bias?.bias3 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.BRAR -> {
                minMaxArray[0] = min(kLineModel.brar?.br ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.brar?.ar ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.brar?.br ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.brar?.ar ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.CCI -> {
                minMaxArray[0] = min(kLineModel.cci?.cci ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.cci?.cci ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.CR -> {
                minMaxArray[0] = min(kLineModel.cr?.cr ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.cr?.ma1 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.cr?.ma2 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.cr?.ma3 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.cr?.ma4 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.cr?.cr ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.cr?.ma1 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.cr?.ma2 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.cr?.ma3 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.cr?.ma4 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.DMA -> {
                minMaxArray[0] = min(kLineModel.dma?.dif ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.dma?.difMa ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.dma?.dif ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.dma?.difMa ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.DMI -> {
                minMaxArray[0] = min(kLineModel.dmi?.pdi ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.dmi?.mdi ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.dmi?.adx ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.dmi?.adxr ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.dmi?.pdi ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.dmi?.mdi ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.dmi?.adx ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.dmi?.adxr ?: Double.NEGATIVE_INFINITY, minMaxArray[1])

            }
            Indicator.Type.KDJ -> {
                minMaxArray[0] = min(kLineModel.kdj?.k ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.kdj?.d ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.kdj?.j ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.kdj?.k ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.kdj?.d ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.kdj?.j ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.KD -> {
                minMaxArray[0] = min(kLineModel.kdj?.k ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.kdj?.d ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.kdj?.k ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.kdj?.d ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.RSI -> {
                minMaxArray[0] = min(kLineModel.rsi?.rsi1 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.rsi?.rsi2 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.rsi?.rsi3 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.rsi?.rsi1 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.rsi?.rsi2 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.rsi?.rsi3 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.PSY -> {
                minMaxArray[0] = min(kLineModel.psy?.psy ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.psy?.psy ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.TRIX -> {
                minMaxArray[0] = min(kLineModel.trix?.trix ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.trix?.maTrix ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.trix?.trix ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.trix?.maTrix ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.OBV -> {
                minMaxArray[0] = min(kLineModel.obv?.obv ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.obv?.maObv ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.obv?.obv ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.obv?.maObv ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.VR -> {
                minMaxArray[0] = min(kLineModel.vr?.vr ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.vr?.maVr ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.vr?.vr ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.vr?.maVr ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.WR -> {
                minMaxArray[0] = min(kLineModel.wr?.wr1 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.wr?.wr2 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.wr?.wr3 ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.wr?.wr1 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.wr?.wr2 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.wr?.wr3 ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.MTM -> {
                minMaxArray[0] = min(kLineModel.mtm?.mtm ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.mtm?.mtmMa ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.mtm?.mtm ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.mtm?.mtmMa ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.EMV -> {
                minMaxArray[0] = min(kLineModel.emv?.emv ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[0] = min(kLineModel.emv?.maEmv ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.emv?.emv ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[1] = max(kLineModel.emv?.maEmv ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
            }
            Indicator.Type.SAR -> {
                minMaxArray[0] = min(kLineModel.sar?.sar ?: Double.POSITIVE_INFINITY, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.sar?.sar ?: Double.NEGATIVE_INFINITY, minMaxArray[1])
                minMaxArray[0] = min(kLineModel.lowPrice, minMaxArray[0])
                minMaxArray[1] = max(kLineModel.highPrice, minMaxArray[1])
            }
            else -> {
                calcYAxisMinMax?.calcYAxisMinMax(indicatorType, kLineModel, minMaxArray)
            }
        }
        return minMaxArray
    }

    override fun calcRange(min: Float, max: Float): Float = abs(max - min)

    override fun computeAxis(labelCount: Int) {
        var min = this.axisMinimum
        var max = this.axisMaximum
        var range = abs(max - min)

        if (range == 0f) {
            max += 1f
            min -= 1f
            range = abs(max - min)
        }

        this.axisMinimum = min - (range / 100f) * 10
        this.axisMaximum = max + (range / 100f) * 20

        this.axisRange = abs(this.axisMaximum - this.axisMinimum)

        computeAxisValues(this.axisMinimum, this.axisMaximum, labelCount)
    }

    /**
     * 获取y点坐标
     * @param value Float
     * @return Float
     */
    fun getY(value: Double): Float {
        return (this.offsetTop + (1f - (value - this.axisMinimum) / this.axisRange) * this.height).toFloat()
    }

    /**
     * 获取y点坐标对应的值
     * @param y Float
     * @return Float
     */
    fun getValue(y: Float): Float {
        return (1f - (y - this.offsetTop) / this.height) * (this.axisRange) + this.axisMinimum
    }
}