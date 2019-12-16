package com.liihuu.klinechart.chart

import android.graphics.*
import com.liihuu.klinechart.KLineChartView
import com.liihuu.klinechart.R
import com.liihuu.klinechart.component.*
import com.liihuu.klinechart.internal.DataProvider
import com.liihuu.klinechart.internal.ViewPortHandler
import com.liihuu.klinechart.internal.utils.Utils
import com.liihuu.klinechart.internal.utils.defaultFormatDate
import com.liihuu.klinechart.internal.utils.defaultFormatDecimal
import com.liihuu.klinechart.model.KLineModel
import kotlin.math.max

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/30-15:09
 */
internal class TooltipChart(
    private val candleChart: CandleChart,
    private val volChart: IndicatorChart,
    private val indicatorChart: IndicatorChart,
    private val tooltip: Tooltip,
    private val candle: Candle,
    private val indicator: Indicator,
    private val yAxis: YAxis,
    private val dataProvider: DataProvider,
    private val viewPortHandler: ViewPortHandler
) : Chart() {
    /**
     * 自定义指标提示label
     */
    var tooltipLabels: KLineChartView.CustomIndicatorListener.TooltipLabels? = null

    /**
     * 自定义指标提示value
     */
    var tooltipValues: KLineChartView.CustomIndicatorListener.TooltipValues? = null

    /**
     * 默认的基础数据label
     */
    private val defaultGeneralDataLabels: MutableList<String> = mutableListOf(
        "${Utils.getResourceString(R.string.time)}: ",
        "${Utils.getResourceString(R.string.open_price)}: ",
        "${Utils.getResourceString(R.string.close_price)}: ",
        "${Utils.getResourceString(R.string.highest_price)}: ",
        "${Utils.getResourceString(R.string.lowest_price)}: ",
        "${Utils.getResourceString(R.string.change)}: ",
        "${Utils.getResourceString(R.string.chg)}: ",
        "${Utils.getResourceString(R.string.volume)}: "
    )

    /**
     * 十字光标线path
     */
    private val crossLinePath = Path()

    private val yAxisLabelStrokePathPoints = arrayListOf(PointF(), PointF(), PointF(), PointF(), PointF())
    
    private val dp20ToPx = Utils.convertDpToPixel(20f)
    private val dp50ToPx = Utils.convertDpToPixel(50f)
    private val dp5ToPx = Utils.convertDpToPixel(5f)
    private val dp2ToPx = Utils.convertDpToPixel(2f)
    private val dp3ToPx = Utils.convertDpToPixel(3f)
    private val dp8ToPx = Utils.convertDpToPixel(8f)

    override fun draw(canvas: Canvas) {
        if (this.dataProvider.currentTipDataPos < this.dataProvider.dataList.size) {
            val kLineModel = this.dataProvider.dataList[this.dataProvider.currentTipDataPos]
            val displayCross = this.dataProvider.crossPoint.y >= 0

            if (this.tooltip.indicatorDisplayRule == Tooltip.IndicatorDisplayRule.ALWAYS ||
                (this.tooltip.indicatorDisplayRule == Tooltip.IndicatorDisplayRule.FOLLOW_CROSS && displayCross)) {
                this.paint.apply {
                    textSize = tooltip.indicatorTextSize
                    style = Paint.Style.FILL
                }
                val textHeight = Utils.calcTextHeight(this.paint, "0")
                val startX = this.viewPortHandler.contentLeft() + this.dp3ToPx
                if (this.candle.chartStyle != Candle.ChartStyle.TIME_LINE) {
                    // 绘制主图的指标提示文字
                    drawIndicatorTooltip(
                        canvas, startX,
                        this.candleChart.offsetTop + this.dp2ToPx + textHeight,
                        kLineModel,
                        this.candleChart.indicatorType
                    )
                }
                // 绘制成交量指标提示文字
                drawIndicatorTooltip(
                    canvas, startX,
                    this.volChart.offsetTop + this.dp2ToPx + textHeight,
                    kLineModel, this.volChart.indicatorType
                )
                // 绘制副图指标提示文字
                drawIndicatorTooltip(
                    canvas, startX,
                    this.indicatorChart.offsetTop + this.dp2ToPx + textHeight,
                    kLineModel, this.indicatorChart.indicatorType
                )
            }

            if (displayCross) {
                this.dataProvider.crossPoint.x = this.viewPortHandler.contentLeft() +
                        this.dataProvider.dataSpace * (this.dataProvider.currentTipDataPos - this.dataProvider.visibleDataMinPos) +
                        this.dataProvider.dataSpace * (1f - DataProvider.DATA_SPACE_RATE) / 2f

                this.paint.textSize = this.tooltip.crossTextSize

                drawCrossHorizontalLine(canvas)
                drawCrossVerticalLine(canvas, kLineModel)
                drawGeneralDataTooltip(canvas, kLineModel)
            }
        }
    }

    /**
     * 获取十字光标y轴上的文字
     */
    private fun getCrossYAxisLabel(): String? {
        val crossPointY = this.dataProvider.crossPoint.y
        if (crossPointY > this.viewPortHandler.contentTop() &&
            crossPointY < this.viewPortHandler.contentBottom()) {

            val candleChartContentTop = this.candleChart.offsetTop
            val volIndicatorChartContentTop = this.volChart.offsetTop

            val yAxisChart: YAxisChart
            val indicatorType: String

            when {
                crossPointY > candleChartContentTop && crossPointY < (this.candleChart.height + candleChartContentTop) -> {
                    yAxisChart = this.candleChart.yAxisChart
                    indicatorType = this.candleChart.indicatorType
                }
                crossPointY > volIndicatorChartContentTop && crossPointY < this.volChart.height + volIndicatorChartContentTop -> {
                    yAxisChart = this.volChart.yAxisChart
                    indicatorType = Indicator.Type.VOL
                }
                else -> {
                    yAxisChart = this.indicatorChart.yAxisChart
                    indicatorType = this.indicatorChart.indicatorType
                }
            }

            val yData = yAxisChart.getValue(crossPointY)
            val text = if (indicatorType == Indicator.Type.VOL) "${yData.toInt()}" else yData.defaultFormatDecimal()
            return this.tooltip.valueFormatter?.format(
                Tooltip.ValueFormatter.Y_AXIS,
                indicatorType, "$yData"
            ) ?: text
        }
        return null
    }

    /**
     * 绘制水平线
     * @param canvas Canvas
     */
    private fun drawCrossHorizontalLine(canvas: Canvas) {
        val yAxisDataLabel = getCrossYAxisLabel() ?: return
        val crossPointX = this.dataProvider.crossPoint.x
        val crossPointY = this.dataProvider.crossPoint.y
        val isDrawYAxisTextOutside = this.yAxis.yAxisTextPosition == YAxis.TextPosition.OUTSIDE

        val yAxisDataLabelSize = Utils.calcTextSize(this.paint, yAxisDataLabel)
        val yAxisDataLabelWidth = yAxisDataLabelSize.width()
        val halfLabelHeight = yAxisDataLabelSize.height() / 2f
        val labelStartX: Float
        val labelStartY = crossPointY + halfLabelHeight

        var lineStartX = this.viewPortHandler.contentLeft()
        var lineEndX = this.viewPortHandler.contentRight()

        val centerPoint = this.viewPortHandler.getContentCenter()
        if (isDrawYAxisTextOutside) {
            labelStartX = if (this.yAxis.yAxisPosition == YAxis.AxisPosition.LEFT) {
                lineStartX - this.tooltip.crossTextRectStrokeLineSize - this.tooltip.crossTextMarginSpace * 2 - yAxisDataLabelWidth
            } else {
                lineEndX + this.tooltip.crossTextRectStrokeLineSize + this.tooltip.crossTextMarginSpace
            }
        } else {
            if (crossPointX > centerPoint.x) {
                // 左边
                lineStartX = this.viewPortHandler.contentLeft() +
                        this.tooltip.crossTextRectStrokeLineSize * 2 +
                        this.tooltip.crossTextMarginSpace * 3 +
                        yAxisDataLabelWidth
                labelStartX = this.viewPortHandler.contentLeft() +
                        this.tooltip.crossTextRectStrokeLineSize +
                        this.tooltip.crossTextMarginSpace
            } else {
                lineEndX = this.viewPortHandler.contentRight() -
                        this.tooltip.crossTextRectStrokeLineSize * 2 -
                        this.tooltip.crossTextMarginSpace * 3 -
                        yAxisDataLabelWidth
                labelStartX = lineEndX +
                        this.tooltip.crossTextRectStrokeLineSize +
                        this.tooltip.crossTextMarginSpace * 2
            }
        }

        if ((!isDrawYAxisTextOutside && crossPointX > centerPoint.x) ||
            (isDrawYAxisTextOutside && this.yAxis.yAxisPosition == YAxis.AxisPosition.LEFT)) {
            // 左边
            this.yAxisLabelStrokePathPoints[0].set(lineStartX, crossPointY)
            this.yAxisLabelStrokePathPoints[1].set(
                lineStartX - this.tooltip.crossTextMarginSpace,
                crossPointY - halfLabelHeight - this.tooltip.crossTextMarginSpace
            )
            this.yAxisLabelStrokePathPoints[2].set(
                lineStartX - this.tooltip.crossTextMarginSpace * 3 - yAxisDataLabelSize.width(),
                this.yAxisLabelStrokePathPoints[1].y
            )
            this.yAxisLabelStrokePathPoints[3].set(
                this.yAxisLabelStrokePathPoints[2].x,
                crossPointY + halfLabelHeight + this.tooltip.crossTextMarginSpace
            )
            this.yAxisLabelStrokePathPoints[4].set(
                this.yAxisLabelStrokePathPoints[1].x,
                this.yAxisLabelStrokePathPoints[3].y
            )

        } else {
            // 右边
            this.yAxisLabelStrokePathPoints[0].set(lineEndX, crossPointY)
            this.yAxisLabelStrokePathPoints[1].set(
                lineEndX + this.tooltip.crossTextMarginSpace,
                crossPointY - halfLabelHeight - this.tooltip.crossTextMarginSpace
            )
            this.yAxisLabelStrokePathPoints[2].set(
                lineEndX + this.tooltip.crossTextMarginSpace * 3 + yAxisDataLabelSize.width(),
                this.yAxisLabelStrokePathPoints[1].y
            )
            this.yAxisLabelStrokePathPoints[3].set(
                this.yAxisLabelStrokePathPoints[2].x,
                crossPointY + halfLabelHeight + this.tooltip.crossTextMarginSpace
            )
            this.yAxisLabelStrokePathPoints[4].set(
                this.yAxisLabelStrokePathPoints[1].x,
                this.yAxisLabelStrokePathPoints[3].y
            )
        }

        // 绘制十字光标垂直线
        this.paint.apply {
            strokeWidth = tooltip.crossLineSize
            style = Paint.Style.STROKE
            color = tooltip.crossLineColor
        }
        if (this.tooltip.crossLineStyle == Component.LineStyle.DASH) {
            this.paint.pathEffect = DashPathEffect(this.tooltip.crossLineDashValues, 0f)
        }
        this.crossLinePath.apply {
            reset()
            moveTo(lineStartX, crossPointY)
            lineTo(lineEndX, crossPointY)
        }
        canvas.drawPath(this.crossLinePath, this.paint)
        this.paint.pathEffect = null

        // 绘制y轴文字外的边框

        this.paint.apply {
            style = Paint.Style.FILL
            color = tooltip.crossTextRectFillColor
        }
        this.crossLinePath.apply {
            reset()
            moveTo(yAxisLabelStrokePathPoints[0].x, yAxisLabelStrokePathPoints[0].y)
        }
        for (i in 1 until this.yAxisLabelStrokePathPoints.size) {
            this.crossLinePath.lineTo(this.yAxisLabelStrokePathPoints[i].x, this.yAxisLabelStrokePathPoints[i].y)
        }
        this.crossLinePath.close()
        canvas.drawPath(this.crossLinePath, this.paint)


        this.paint.apply {
            strokeWidth = tooltip.crossTextRectStrokeLineSize
            style = Paint.Style.STROKE
            color = tooltip.crossTextRectStrokeLineColor
        }
        this.crossLinePath.apply {
            reset()
            moveTo(yAxisLabelStrokePathPoints[0].x, yAxisLabelStrokePathPoints[0].y)
        }
        for (i in 1 until this.yAxisLabelStrokePathPoints.size) {
            this.crossLinePath.lineTo(this.yAxisLabelStrokePathPoints[i].x, this.yAxisLabelStrokePathPoints[i].y)
        }
        this.crossLinePath.close()
        canvas.drawPath(this.crossLinePath, this.paint)

        this.paint.apply {
            color = tooltip.crossTextColor
            style = Paint.Style.FILL
        }
        canvas.drawText(yAxisDataLabel, labelStartX, labelStartY, this.paint)
    }

    /**
     * 绘制十字光标垂直线
     * @param canvas Canvas
     * @param kLineModel KLineModel
     */
    private fun drawCrossVerticalLine(canvas: Canvas, kLineModel: KLineModel) {
        val crossPointX = this.dataProvider.crossPoint.x
        this.paint.apply {
            strokeWidth = tooltip.crossLineSize
            style = Paint.Style.STROKE
            color = tooltip.crossLineColor
        }
        if (this.tooltip.crossLineStyle == Component.LineStyle.DASH) {
            this.paint.pathEffect = DashPathEffect(this.tooltip.crossLineDashValues, 0f)
        }
        // 绘制十字光标垂直线
        this.crossLinePath.apply {
            reset()
            moveTo(crossPointX, viewPortHandler.contentTop())
            lineTo(crossPointX, viewPortHandler.contentBottom())
        }
        canvas.drawPath(this.crossLinePath, this.paint)
        this.paint.pathEffect = null

        val timestamp = kLineModel.timestamp
        var label = kLineModel.timestamp.defaultFormatDate("yyyy-MM-dd HH:mm")
        label = this.tooltip.valueFormatter?.format(
            Tooltip.ValueFormatter.X_AXIS,
            null,
            "$timestamp"
        ) ?: label
        val labelSize = Utils.calcTextSize(this.paint, label)
        var xAxisLabelX = crossPointX - labelSize.width() / 2

        // 保证整个x轴上的提示文字总是完全显示
        if (xAxisLabelX < this.viewPortHandler.contentLeft() + this.tooltip.crossTextMarginSpace + this.tooltip.crossTextRectStrokeLineSize) {
            xAxisLabelX = this.viewPortHandler.contentLeft()
        } else if (xAxisLabelX > this.viewPortHandler.contentRight() - labelSize.width() - this.tooltip.crossTextRectStrokeLineSize) {
            xAxisLabelX = this.viewPortHandler.contentRight() - labelSize.width() - this.tooltip.crossTextRectStrokeLineSize
        }

        val rectLeft = xAxisLabelX - this.tooltip.crossTextRectStrokeLineSize - this.tooltip.crossTextMarginSpace
        val rectTop = this.viewPortHandler.contentBottom()
        val rectRight = xAxisLabelX + labelSize.width() + this.tooltip.crossTextMarginSpace + this.tooltip.crossTextRectStrokeLineSize
        val rectBottom = this.viewPortHandler.contentBottom() + labelSize.height() + this.tooltip.crossTextRectStrokeLineSize + this.tooltip.crossTextMarginSpace * 2
        this.paint.apply {
            style = Paint.Style.FILL
            color = tooltip.crossTextRectFillColor
        }
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, this.paint)

        this.paint.apply {
            strokeWidth = tooltip.crossTextRectStrokeLineSize
            style = Paint.Style.STROKE
            color = tooltip.crossTextRectStrokeLineColor
        }
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, this.paint)

        // 绘制轴上的提示文字
        this.paint.apply {
            color = tooltip.crossTextColor
            style = Paint.Style.FILL
        }
        canvas.drawText(
            label,
            xAxisLabelX,
            this.viewPortHandler.contentBottom() + labelSize.height() + this.tooltip.crossTextRectStrokeLineSize + this.tooltip.crossTextMarginSpace,
            this.paint
        )
    }

    /**
     * 绘制基础数据提示
     * @param canvas Canvas
     * @param kLineModel KLineModel
     */
    private fun drawGeneralDataTooltip(canvas: Canvas, kLineModel: KLineModel) {
        val drawGeneralDataListener = this.tooltip.drawGeneralDataListener
        if (drawGeneralDataListener != null) {
            drawGeneralDataListener.draw(
                canvas, paint,
                this.dataProvider.crossPoint,
                this.tooltip,
                this.viewPortHandler.contentRect,
                kLineModel)
        } else {
            this.paint.textSize = this.tooltip.generalDataTextSize
            var labels = defaultGeneralDataLabels
            val values: MutableList<String>
            val generalDataFormatter = this.tooltip.generalDataFormatter
            if (generalDataFormatter != null) {
                labels = generalDataFormatter.generatedLabels()
                values = generalDataFormatter.generatedValues(kLineModel)
            } else {
                val change = kLineModel.closePrice - kLineModel.openPrice
                val chg = if (kLineModel.openPrice == 0.0) "--" else "${(change / kLineModel.openPrice * 100).defaultFormatDecimal()}%"
                values = mutableListOf(
                    kLineModel.timestamp.defaultFormatDate(),
                    kLineModel.openPrice.defaultFormatDecimal(),
                    kLineModel.closePrice.defaultFormatDecimal(),
                    kLineModel.highPrice.defaultFormatDecimal(),
                    kLineModel.lowPrice.defaultFormatDecimal(),
                    change.defaultFormatDecimal(),
                    chg,
                    "${kLineModel.volume.toInt()}"
                )
            }

            var maxLabelWidth = Int.MIN_VALUE
            val labelHeight = Utils.calcTextHeight(this.paint, "0")
            val labelSize = labels.size
            val valueSize = values.size
            for (i in 0 until labelSize) {
                val value = if (i < valueSize) values[i] else "--"
                val width = Utils.calcTextWidth(this.paint, "${labels[i]}${value}")
                maxLabelWidth = max(maxLabelWidth, width)
            }

            val rectStartX: Float
            val rectEndX: Float
            val rectStartY = this.viewPortHandler.contentTop() + this.dp20ToPx
            val rectEndY = rectStartY + this.tooltip.generalDataRectStrokeLineSize * 2 + labelSize * labelHeight + (labelSize - 1) * this.dp8ToPx + this.dp5ToPx * 2
            val centerPoint = this.viewPortHandler.getContentCenter()
            if (this.dataProvider.crossPoint.x < centerPoint.x) {
                rectStartX = this.viewPortHandler.contentRight() - this.dp50ToPx - this.tooltip.generalDataRectStrokeLineSize * 2 - this.dp3ToPx * 2 - maxLabelWidth
                rectEndX = this.viewPortHandler.contentRight() - this.dp50ToPx
            } else {
                rectStartX = this.viewPortHandler.contentLeft() + this.dp50ToPx
                rectEndX = rectStartX + this.tooltip.generalDataRectStrokeLineSize * 2 + this.dp3ToPx * 2 + maxLabelWidth
            }

            this.paint.apply {
                style = Paint.Style.FILL
                color = tooltip.generalDataRectFillColor
            }
            val rect = RectF(rectStartX, rectStartY, rectEndX, rectEndY)
            canvas.drawRoundRect(rect, this.dp3ToPx, this.dp3ToPx, this.paint)

            this.paint.apply {
                style = Paint.Style.STROKE
                color = tooltip.generalDataRectStrokeLineColor
            }
            canvas.drawRoundRect(rect, this.dp3ToPx, this.dp3ToPx, this.paint)

            this.paint.apply {
                style = Paint.Style.FILL
                color = tooltip.generalDataTextColor
            }
            val labelStartX = rectStartX + this.tooltip.generalDataRectStrokeLineSize + this.dp3ToPx
            var textStartY = rectStartY + this.tooltip.generalDataRectStrokeLineSize + this.dp5ToPx + labelHeight
            for (i in 0 until labelSize) {
                this.paint.apply {
                    color = tooltip.generalDataTextColor
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText(
                    labels[i],
                    labelStartX, textStartY, this.paint
                )
                val valueStartX = rectEndX - this.tooltip.crossTextRectStrokeLineSize - this.dp3ToPx
                if (generalDataFormatter != null) {
                    generalDataFormatter.generatedStyle(this.paint, kLineModel, this.tooltip, i)
                } else {
                    if (i == 5 || i == 6) {
                        when {
                            kLineModel.closePrice > kLineModel.openPrice -> this.paint.color = this.tooltip.generalDataIncreasingColor
                            kLineModel.closePrice < kLineModel.openPrice -> this.paint.color = this.tooltip.generalDataDecreasingColor
                            else -> this.paint.color = this.tooltip.generalDataTextColor
                        }
                    } else {
                        this.paint.color = this.tooltip.generalDataTextColor
                    }
                }
                this.paint.textAlign = Paint.Align.RIGHT
                val value = if (i < valueSize) values[i] else "--"
                canvas.drawText(value, valueStartX, textStartY, this.paint)
                textStartY += labelHeight + this.dp8ToPx
            }
        }
        this.paint.textAlign = Paint.Align.LEFT
    }

    /**
     * 绘制指标提示文字
     * @param canvas Canvas
     * @param startX Float
     * @param startY Float
     * @param kLineModel KLineModel
     * @param indicatorType String
     */
    private fun drawIndicatorTooltip(
        canvas: Canvas, startX: Float, startY: Float,
        kLineModel: KLineModel, indicatorType: String
    ) {
        when (indicatorType) {
            Indicator.Type.NO -> {}
            Indicator.Type.MA -> {
                val maData = kLineModel.ma
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(maData?.ma5, maData?.ma10, maData?.ma20, maData?.ma60),
                    mutableListOf("MA5", "MA10", "MA20", "MA60"),
                    indicatorType
                )
            }
            Indicator.Type.MACD -> {
                val macdData = kLineModel.macd
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(macdData?.diff, macdData?.dea, macdData?.macd),
                    mutableListOf("DIFF", "DEA", "MACD"),
                    indicatorType
                )
            }
            Indicator.Type.VOL -> {
                val volData = kLineModel.vol
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(volData?.ma5, volData?.ma10, volData?.ma20, volData?.num),
                    mutableListOf("MA5", "MA10", "MA20", "VOLUME"),
                    indicatorType
                )
            }
            Indicator.Type.BOLL -> {
                val bollData = kLineModel.boll
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(bollData?.up, bollData?.mid, bollData?.dn),
                    mutableListOf("UP", "MID", "DN"),
                    indicatorType
                )
            }
            Indicator.Type.BIAS -> {
                val biasData = kLineModel.bias
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(biasData?.bias1, biasData?.bias2, biasData?.bias3),
                    mutableListOf("BIAS6", "BIAS12", "BIAS24"),
                    indicatorType
                )
            }
            Indicator.Type.BRAR -> {
                val brarData = kLineModel.brar
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(brarData?.br, brarData?.ar),
                    mutableListOf("BR", "AR"),
                    indicatorType
                )
            }
            Indicator.Type.CCI -> {
                val cciData = kLineModel.cci
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(cciData?.cci),
                    mutableListOf("CCI"),
                    indicatorType
                )
            }
            Indicator.Type.CR -> {
                val crData = kLineModel.cr
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(crData?.cr, crData?.ma1, crData?.ma2, crData?.ma3, crData?.ma4),
                    mutableListOf("CR", "MA1", "MA2", "MA3", "MA4"),
                    indicatorType
                )
            }
            Indicator.Type.DMA -> {
                val dmaData = kLineModel.dma
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(dmaData?.dif, dmaData?.difMa),
                    mutableListOf("DIF", "DIFMA"),
                    indicatorType
                )
            }
            Indicator.Type.DMI -> {
                val dmiData = kLineModel.dmi
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(dmiData?.mdi, dmiData?.pdi, dmiData?.adx, dmiData?.adxr),
                    mutableListOf("MDI", "PDI", "ADX", "ADXR"),
                    indicatorType
                )
            }
            Indicator.Type.KDJ -> {
                val kdjData = kLineModel.kdj
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(kdjData?.k, kdjData?.d, kdjData?.j),
                    mutableListOf("K", "D", "J"),
                    indicatorType
                )
            }
            Indicator.Type.KD -> {
                val kdjData = kLineModel.kdj
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(kdjData?.k, kdjData?.d),
                    mutableListOf("K", "D"),
                    indicatorType
                )
            }
            Indicator.Type.RSI -> {
                val rsiData = kLineModel.rsi
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(rsiData?.rsi1, rsiData?.rsi2, rsiData?.rsi3),
                    mutableListOf("RSI6", "RSI12", "RSI24"),
                    indicatorType
                )
            }
            Indicator.Type.PSY -> {
                val psyData = kLineModel.psy
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(psyData?.psy),
                    mutableListOf("PSY"),
                    indicatorType
                )
            }
            Indicator.Type.TRIX -> {
                val trixData = kLineModel.trix
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(trixData?.trix, trixData?.maTrix),
                    mutableListOf("TRIX", "MATRIX"),
                    indicatorType
                )
            }
            Indicator.Type.OBV -> {
                val obvData = kLineModel.obv
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(obvData?.obv, obvData?.maObv),
                    mutableListOf("OBV", "OBVMA"),
                    indicatorType
                )
            }
            Indicator.Type.VR -> {
                val vrModel = kLineModel.vr
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(vrModel?.vr, vrModel?.maVr),
                    mutableListOf("VR", "VRMA"),
                    indicatorType
                )
            }
            Indicator.Type.WR -> {
                val wrModel = kLineModel.wr
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(wrModel?.wr1, wrModel?.wr2, wrModel?.wr3),
                    mutableListOf("WR1", "WR2", "WR3"),
                    indicatorType
                )
            }
            Indicator.Type.MTM -> {
                val mtmModel = kLineModel.mtm
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(mtmModel?.mtm, mtmModel?.mtmMa),
                    mutableListOf("MTM", "MTMMA"),
                    indicatorType
                )
            }

            Indicator.Type.EMV -> {
                val emvModel = kLineModel.emv
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(emvModel?.emv, emvModel?.maEmv),
                    mutableListOf("EMV", "EMVMA"),
                    indicatorType
                )
            }

            Indicator.Type.SAR -> {
                val sarModel = kLineModel.sar
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    mutableListOf(sarModel?.sar),
                    mutableListOf("SAR"),
                    indicatorType
                )
            }
            
            else -> {
                drawIndicatorTooltipLabels(
                    canvas, startX, startY,
                    this.tooltipValues?.tooltipValues(indicatorType, kLineModel.customIndicator) ?: mutableListOf(),
                    this.tooltipLabels?.tooltipLabels(indicatorType) ?: mutableListOf(),
                    indicatorType
                )
            }
        }
    }

    /**
     * 绘制指标提示文字
     * @param canvas Canvas
     * @param startX Float
     * @param startY Float
     * @param values MutableList<Double?>
     * @param labels MutableList<String>
     * @param indicatorType String
     */
    private fun drawIndicatorTooltipLabels(
        canvas: Canvas, startX: Float, startY: Float,
        values: MutableList<Double?>,
        labels: MutableList<String>,
        indicatorType: String
    ) {
        var labelX = startX
        val valueSize = values.size
        val lineColorSize = this.indicator.lineColors.size
        for (i in 0 until valueSize) {
            val value = if (values[i] == null) null else "${values[i]}"
            var valueStr = "--"
            if (value != null) {
                valueStr = if (indicatorType == Indicator.Type.VOL) {
                    "${values[i]?.toInt() ?: "--"}"
                } else {
                    values[i].defaultFormatDecimal()
                }
            }
            valueStr = this.tooltip.valueFormatter?.format(
                Tooltip.ValueFormatter.CHART,
                indicatorType,
                "${values[i]}"
            ) ?: valueStr
            val text = "${labels[i]}: $valueStr"
            val textWidth = Utils.calcTextWidth(this.paint, text)
            this.paint.color = this.indicator.lineColors[i % lineColorSize]
            canvas.drawText(text, labelX, startY, this.paint)
            labelX += this.dp8ToPx + textWidth
        }
    }
}