package com.liihuu.klinechart.chart

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.liihuu.klinechart.component.XAxis
import com.liihuu.klinechart.component.Component
import com.liihuu.klinechart.internal.DataProvider
import com.liihuu.klinechart.internal.ViewPortHandler
import com.liihuu.klinechart.internal.utils.Utils
import com.liihuu.klinechart.internal.utils.defaultFormatDate
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/29-16:11
 */
internal class XAxisChart(
    private val axis: XAxis,
    private val dataProvider: DataProvider,
    private val viewPortHandler: ViewPortHandler
) : AxisChart() {

    private var valuePoints = FloatArray(0)

    init {
        this.paint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        computeAxis(5)
        drawAxisLine(canvas)
        drawAxisLabels(canvas)
        drawSeparatorLines(canvas)
        drawTickLines(canvas)
    }

    override fun drawAxisLine(canvas: Canvas) {
        if (!this.axis.displayAxisLine) {
            return
        }
        this.paint.apply {
            strokeWidth = axis.axisLineSize
            color = axis.axisLineColor
        }
        canvas.drawLine(this.viewPortHandler.contentLeft(), this.offsetTop, this.viewPortHandler.contentRight(), this.offsetTop, this.paint)
    }

    /**
     * 绘制坐标轴上的文字
     * @param canvas Canvas
     */
    override fun drawAxisLabels(canvas: Canvas, indicatorType: String) {
        if (!this.axis.displayTickText) {
            return
        }
        this.paint.apply {
            textSize = axis.tickTextSize
            color = axis.tickTextColor
            style = Paint.Style.FILL
        }
        val labelHeight = Utils.calcTextHeight(this.paint, "T")
        var startY = this.viewPortHandler.contentBottom() + this.axis.textMarginSpace + labelHeight
        if (this.axis.displayTickLine) {
            startY += axis.tickLineSize
        }

        for (i in this.valuePoints.indices) {
            val x = this.valuePoints[i]
            val kLineModel = this.dataProvider.dataList[this.labelValues[i].toInt()]
            val timestamp = kLineModel.timestamp
            var label = timestamp.defaultFormatDate()
            label = this.axis.valueFormatter?.format(timestamp) ?: label

            canvas.drawText(label, x, startY, this.paint)
        }
    }

    /**
     * 绘制分割线
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
        }
        if (this.axis.separatorLineStyle == Component.LineStyle.DASH) {
            this.paint.pathEffect = DashPathEffect(this.axis.separatorLineDashValues, 0f)
        }

        for (i in this.valuePoints.indices) {
            val x = this.valuePoints[i]
            this.separatorLinePath.apply {
                reset()
                moveTo(x, viewPortHandler.contentTop())
                lineTo(x, viewPortHandler.contentBottom())
            }
            canvas.drawPath(this.separatorLinePath, this.paint)
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
        }
        val startY = this.viewPortHandler.contentBottom()
        val endY = startY + this.axis.tickLineSize

        for (i in this.valuePoints.indices) {
            val x = this.valuePoints[i]
            canvas.drawLine(x, startY, x, endY, this.paint)
        }
    }

    /**
     * 获取值对应的坐标点值
     * @return FloatArray
     */
    private fun pointValuesToPixel() {
        val offsetLeft = this.viewPortHandler.contentLeft()
        this.valuePoints = FloatArray(valueCount)
        for (i in this.valuePoints.indices) {
            val pos = this.labelValues[i]
            this.valuePoints[i] = offsetLeft + ((pos - this.dataProvider.visibleDataMinPos) * this.dataProvider.dataSpace  + this.dataProvider.dataSpace * (1 - DataProvider.DATA_SPACE_RATE) / 2).toFloat()
        }
    }

    override fun computeAxis(labelCount: Int) {
        val max = min(this.dataProvider.visibleDataMinPos + this.dataProvider.visibleDataCount - 1, this.dataProvider.dataList.size - 1)
        computeAxisValues(this.dataProvider.visibleDataMinPos.toFloat(), max.toFloat(), labelCount)
        pointValuesToPixel()
    }

    override fun fixComputeAxisValues() {
        val dataSize = this.dataProvider.dataList.size
        if (dataSize > 0) {
            this.paint.textSize = this.axis.tickTextSize
            val defaultLabelWidth = Utils.calcTextWidth(this.paint, "0000-00-00 00:00:00")
            var startPos = ceil(defaultLabelWidth / 2.0 / this.dataProvider.dataSpace) - 1
            if (startPos > dataSize - 1) {
                startPos = dataSize - 1.0
            }
            val barCount = ceil(defaultLabelWidth / (this.dataProvider.dataSpace * (1 + DataProvider.DATA_SPACE_RATE))) + 1
            if (dataSize > barCount) {
                this.valueCount = floor((dataSize - startPos) / barCount).toInt() + 1
            } else {
                this.valueCount = 1
            }
            this.labelValues = DoubleArray(this.valueCount)
            this.labelValues[0] = startPos
            for (i in 1 until this.valueCount) {
                this.labelValues[i] = startPos + i * (barCount - 1)
            }
        } else {
            this.valueCount = 0
            this.labelValues = doubleArrayOf()
        }
    }

    override fun calcRange(min: Float, max: Float): Float = if (max < 0f) 0f else abs(max - min) + 1f

    override fun isFillChart(): Boolean = this.dataProvider.dataList.size > this.dataProvider.visibleDataCount
}