package com.liihuu.klinechart.chart

import android.graphics.*
import com.liihuu.klinechart.component.*
import com.liihuu.klinechart.internal.DataProvider
import com.liihuu.klinechart.internal.ViewPortHandler
import com.liihuu.klinechart.internal.utils.Utils
import com.liihuu.klinechart.internal.utils.defaultFormatDecimal
import com.liihuu.klinechart.model.KLineModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/28-19:51
 */
internal class CandleChart(
    private val candle: Candle,
    indicator: Indicator,
    xAxis: XAxis,
    yAxis: YAxis,
    private val dataProvider: DataProvider,
    private val viewPortHandler: ViewPortHandler
) : IndicatorChart(indicator, xAxis, yAxis, dataProvider, viewPortHandler) {

    /**
     * 最高最低价标记类
     * @property x Float
     * @property price Double
     * @constructor
     */
    private data class HighLowPriceMark(
        val x: Float,
        val price: Double
    )

    /**
     * 线的尺寸
     */
    private val candleLineSize = Utils.convertDpToPixel(1f)

    private val shadowBuffers = FloatArray(8)
    private val bodyBuffers = FloatArray(4)

    private val markLinePoints = FloatArray(12)

    /**
     * 分时线路径
     */
    private val linePath = Path()

    /**
     * 分时线区域路径
     */
    private val timeLineAreaPath = Path()

    /**
     * 分时均线路径
     */
    private val timeAverageLinePath = Path()

    /**
     * 最高价
     */
    private var highestPriceMark: HighLowPriceMark? = null

    /**
     * 最低价
     */
    private var lowestPriceMark: HighLowPriceMark? = null

    private val dp3ToPx = Utils.convertDpToPixel(3f)
    private val dp2ToPx = Utils.convertDpToPixel(2f)
    private val dp6ToPx = Utils.convertDpToPixel(6f)

    override fun drawChart(canvas: Canvas) {
        if (this.candle.chartStyle != Candle.ChartStyle.TIME_LINE) {
            drawCandle(canvas)
            drawIndicator(canvas)
            drawHighestPriceMark(canvas)
            drawLowestPriceMark(canvas)
        } else {
            drawTimeLine(canvas)
        }
        drawLastPriceMark(canvas)
    }

    /**
     * 绘制最高价标记
     * @param canvas Canvas
     */
    private fun drawHighestPriceMark(canvas: Canvas) {
        if (!this.candle.displayHighestPriceMark) {
            return
        }

        val x = this.highestPriceMark?.x ?: return
        if (x < 0) {
            return
        }

        val price = this.highestPriceMark?.price ?: return
        if (price == Double.MAX_VALUE) {
            return
        }

        drawLowestHighestPriceMark(canvas, x, price, Candle.DrawPriceMarkListener.HIGHEST)
    }

    /**
     * 绘制最低价标记
     * @param canvas Canvas
     */
    private fun drawLowestPriceMark(canvas: Canvas) {
        if (!this.candle.displayLowestPriceMark) {
            return
        }

        val x = this.lowestPriceMark?.x ?: return
        if (x < 0) {
            return
        }

        val price = this.lowestPriceMark?.price ?: return
        if (price == Double.MAX_VALUE) {
            return
        }

        drawLowestHighestPriceMark(canvas, x, price, Candle.DrawPriceMarkListener.LOWEST)
    }

    /**
     * 绘制最低最高价格标记
     * @param canvas Canvas
     * @param x Float
     * @param price Double
     */
    private fun drawLowestHighestPriceMark(canvas: Canvas, x: Float, price: Double, type: Int) {
        this.paint.apply {
            color = candle.lowestHighestPriceMarkTextColor
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        val priceY = this.yAxisChart.getY(price)
        val drawPriceMarkListener = this.candle.drawPriceMarkListener
        if (drawPriceMarkListener != null) {
            drawPriceMarkListener.draw(
                canvas, paint, type,
                PointF(x, priceY),
                this.viewPortHandler.contentRect,
                this.candle, price
            )
        } else {
            val start = x + this.dp3ToPx
            this.markLinePoints[0] = start
            this.markLinePoints[1] = priceY
            this.markLinePoints[2] = start + dp6ToPx
            this.markLinePoints[3] = priceY

            this.markLinePoints[4] = start
            this.markLinePoints[5] = priceY
            this.markLinePoints[6] = start + this.dp3ToPx
            this.markLinePoints[7] = priceY - this.dp2ToPx

            this.markLinePoints[8] = start
            this.markLinePoints[9] = priceY
            this.markLinePoints[10] = start + this.dp3ToPx
            this.markLinePoints[11] = priceY + this.dp2ToPx

            canvas.drawLines(this.markLinePoints, this.paint)

            this.paint.apply {
                style = Paint.Style.FILL
                textSize = candle.lowestHighestPriceMarkTextSize
            }
            val priceText = this.candle.valueFormatter?.format(price.toString()) ?: price.defaultFormatDecimal()
            val priceTextHeight = Utils.calcTextHeight(this.paint, priceText)
            canvas.drawText(priceText, this.markLinePoints[2] + this.dp3ToPx, priceY + priceTextHeight / 2, this.paint)
        }
    }

    /**
     * 绘制最新价标记
     * @param canvas Canvas
     */
    private fun drawLastPriceMark(canvas: Canvas) {
        val dataList = this.dataProvider.dataList
        val dataSize = dataList.size
        if (!this.candle.displayLastPriceMark || dataSize == 0) {
            return
        }
        this.linePath.reset()
        this.paint.apply {
            strokeWidth = candle.lastPriceMarkLineSize
            color = candle.lastPriceMarkLineColor
            style = Paint.Style.STROKE
        }
        val lastPrice = dataList[dataSize - 1].closePrice
        var priceY = this.yAxisChart.getY(lastPrice)
        priceY = max(this.offsetTop + this.height * 0.05f, min(priceY, this.offsetTop + this.height * 0.98f))
        val drawPriceMarkListener = this.candle.drawPriceMarkListener
        if (drawPriceMarkListener != null) {
            drawPriceMarkListener.draw(
                canvas, this.paint, Candle.DrawPriceMarkListener.LAST,
                PointF(this.viewPortHandler.contentLeft(), priceY),
                this.viewPortHandler.contentRect,
                this.candle, lastPrice
            )
        } else {
            if (this.candle.lastPriceMarkLineStyle == Component.LineStyle.DASH) {
                this.paint.pathEffect = DashPathEffect(this.candle.lastPriceMarkLineDashValues, 0f)
            }
            this.linePath.apply {
                moveTo(viewPortHandler.contentLeft(), priceY)
                lineTo(viewPortHandler.contentRight(), priceY)
            }
            canvas.drawPath(this.linePath, this.paint)
            this.paint.pathEffect = null
        }
    }

    /**
     * 绘制分时线
     * @param canvas Canvas
     */
    private fun drawTimeLine(canvas: Canvas) {
        this.linePath.reset()
        this.timeAverageLinePath.reset()
        this.timeLineAreaPath.reset()
        this.paint.apply {
            strokeWidth = candle.timeLineSize
            color = candle.timeLineColor
            style = Paint.Style.STROKE
        }
        this.timeLineAreaPath.moveTo(this.viewPortHandler.contentLeft(), this.offsetTop + this.height)
        val visibleDataMinPos = this.dataProvider.visibleDataMinPos
        val visibleDataCount = this.dataProvider.visibleDataCount
        val dataSize = this.dataProvider.dataList.size
        val onDrawing: (i: Int, x: Float, halfBarSpace: Float, kLineModel: KLineModel) -> Unit = { i, x, _, kLineModel ->
            val closeY = this.yAxisChart.getY(kLineModel.closePrice)
            val averagePrice = kLineModel.averagePrice
            val averagePriceY = this.yAxisChart.getY(averagePrice)
            when (i) {
                visibleDataMinPos -> {
                    this.linePath.moveTo(x, closeY)
                    if (averagePrice != 0.0) {
                        this.timeAverageLinePath.moveTo(x, averagePriceY)
                    }
                    this.timeLineAreaPath.apply {
                        lineTo(viewPortHandler.contentLeft(), closeY)
                        lineTo(x, closeY)
                    }
                }

                visibleDataMinPos + visibleDataCount - 1 -> {
                    this.linePath.lineTo(x, closeY)
                    if (averagePrice != 0.0) {
                        this.timeAverageLinePath.lineTo(x, averagePriceY)
                    }
                    this.timeLineAreaPath.apply {
                        lineTo(x, closeY)
                        lineTo(viewPortHandler.contentRight(), closeY)
                        lineTo(viewPortHandler.contentRight(), offsetTop + height)
                    }
                }

                dataSize - 1 -> {
                    this.linePath.lineTo(x, closeY)
                    if (averagePrice != 0.0) {
                        this.timeAverageLinePath.lineTo(x, averagePriceY)
                    }
                    this.timeLineAreaPath.apply {
                        lineTo(x, closeY)
                        lineTo(x, offsetTop + height)
                    }
                }

                else -> {
                    this.linePath.lineTo(x, closeY)
                    if (averagePrice != 0.0) {
                        this.timeAverageLinePath.lineTo(x, averagePriceY)
                    }
                    this.timeLineAreaPath.lineTo(x, closeY)
                }
            }
        }

        this.drawGraphs(canvas, onDrawing) {
            // 绘制分时线
            this.timeLineAreaPath.close()
            canvas.drawPath(this.linePath, this.paint)

            // 绘制分时线填充区域
            this.paint.apply {
                style = Paint.Style.FILL
                color = candle.timeLineFillColor
            }
            canvas.drawPath(this.timeLineAreaPath, this.paint)
            this.paint.shader = null

            // 绘制均线
            this.paint.apply {
                style = Paint.Style.STROKE
                color = candle.timeAverageLineColor
            }
            canvas.drawPath(this.timeAverageLinePath, this.paint)
        }
    }

    /**
     * 绘制蜡烛图
     * @param canvas Canvas
     */
    private fun drawCandle(canvas: Canvas) {
        this.paint.strokeWidth = this.candleLineSize
        val increasingColor = candle.increasingColor
        val decreasingColor = candle.decreasingColor
        var highestPrice = Double.MIN_VALUE
        var lowestPrice = Double.MAX_VALUE
        var highestPriceX = -1f
        var lowestPriceX = -1f
        val dataList = this.dataProvider.dataList
        val onDrawing: (i: Int, x: Float, halfBarSpace: Float, kLineModel: KLineModel) -> Unit = { i, x, halfBarSpace, kLineModel ->
            var refKLineModel: KLineModel? = null
            if (i > 0) {
                refKLineModel = dataList[i - 1]
            }
            val refClosePrice = refKLineModel?.closePrice ?: Double.NEGATIVE_INFINITY
            val closePrice = kLineModel.closePrice
            val highPrice = kLineModel.highPrice
            val lowPrice = kLineModel.lowPrice
            if (closePrice > refClosePrice) {
                this.paint.color = increasingColor
            } else {
                this.paint.color = decreasingColor
            }
            drawCandleItem(
                canvas, x, halfBarSpace, refClosePrice,
                kLineModel.openPrice, closePrice,
                highPrice, lowPrice
            )

            if (highestPrice < highPrice) {
                highestPrice = highPrice
                highestPriceX = x
            }

            if (lowPrice < lowestPrice) {
                lowestPrice = lowPrice
                lowestPriceX = x
            }
        }

        drawGraphs(canvas, onDrawing, {})

        this.highestPriceMark = HighLowPriceMark(highestPriceX, highestPrice)
        this.lowestPriceMark = HighLowPriceMark(lowestPriceX, lowestPrice)
    }


    /**
     * 绘制每一条蜡烛数据
     * @param canvas Canvas
     * @param x Float
     * @param halfBarSpace Float
     * @param refClosePrice Double
     * @param openPrice Double
     * @param closePrice Double
     * @param highPrice Double
     * @param lowPrice Double
     */
    private fun drawCandleItem(
        canvas: Canvas, x: Float,
        halfBarSpace: Float, refClosePrice: Double,
        openPrice: Double, closePrice: Double,
        highPrice: Double, lowPrice: Double) {
        when (candle.candleStyle) {
            Candle.CandleStyle.SOLID -> {
                this.paint.style = Paint.Style.FILL
                drawCandleReact(
                    canvas, x, halfBarSpace,
                    openPrice, closePrice,
                    highPrice, lowPrice
                )
            }
            Candle.CandleStyle.STROKE -> {
                this.paint.style = Paint.Style.STROKE
                drawCandleReact(
                    canvas, x, halfBarSpace,
                    openPrice, closePrice,
                    highPrice, lowPrice
                )
            }
            Candle.CandleStyle.INCREASING_STROKE -> {
                if (closePrice > refClosePrice) {
                    this.paint.style = Paint.Style.STROKE
                } else {
                    this.paint.style = Paint.Style.FILL
                }
                drawCandleReact(
                    canvas, x, halfBarSpace,
                    openPrice, closePrice,
                    highPrice, lowPrice
                )
            }
            Candle.CandleStyle.DECREASING_STROKE -> {
                if (closePrice > refClosePrice) {
                    this.paint.style = Paint.Style.FILL
                } else {
                    this.paint.style = Paint.Style.STROKE
                }
                drawCandleReact(
                    canvas, x, halfBarSpace,
                    openPrice, closePrice,
                    highPrice, lowPrice
                )
            }
            Candle.CandleStyle.OHLC -> {
                drawOhlc(
                    canvas, x, halfBarSpace, refClosePrice,
                    openPrice, closePrice, highPrice, lowPrice,
                    candle.increasingColor, candle.decreasingColor
                )
            }
            else -> {}
        }
    }

    /**
     * 绘制蜡烛矩形
     * @param canvas Canvas
     */
    private fun drawCandleReact(
        canvas: Canvas, x: Float, halfBarSpace: Float,
        openPrice: Double, closePrice:
        Double, highPrice: Double, lowPrice: Double) {
        val priceY = getPriceY(openPrice, closePrice, highPrice, lowPrice)
        this.shadowBuffers[0] = x
        this.shadowBuffers[2] = x
        this.shadowBuffers[4] = x
        this.shadowBuffers[6] = x
        when {
            closePrice < openPrice -> {
                this.shadowBuffers[1] = priceY[2]
                this.shadowBuffers[3] = priceY[0]
                this.shadowBuffers[5] = priceY[3]
                this.shadowBuffers[7] = priceY[1]
            }
            closePrice > openPrice -> {
                this.shadowBuffers[1] = priceY[2]
                this.shadowBuffers[3] = priceY[1]
                this.shadowBuffers[5] = priceY[3]
                this.shadowBuffers[7] = priceY[0]
            }
            else -> {
                this.shadowBuffers[1] = priceY[2]
                this.shadowBuffers[3] = priceY[0]
                this.shadowBuffers[5] = priceY[3]
                this.shadowBuffers[7] = this.shadowBuffers[3]
            }
        }

        this.bodyBuffers[0] = x - halfBarSpace
        this.bodyBuffers[1] = priceY[1]
        this.bodyBuffers[2] = x + halfBarSpace
        this.bodyBuffers[3] = priceY[0]

        if (this.bodyBuffers[1] == this.bodyBuffers[3] ||
            abs(this.bodyBuffers[1] - this.bodyBuffers[3]) < this.candleLineSize) {
            this.paint.strokeWidth = this.candleLineSize
            canvas.drawLine(
                this.bodyBuffers[0], this.bodyBuffers[1],
                this.bodyBuffers[2], this.bodyBuffers[1],
                this.paint)
        } else {
            canvas.drawRect(
                this.bodyBuffers[0], this.bodyBuffers[1],
                this.bodyBuffers[2], this.bodyBuffers[3],
                this.paint)
        }

        canvas.drawLines(this.shadowBuffers, this.paint)
    }

    override fun isMain(): Boolean = true

    override fun isTimeLine(): Boolean = candle.chartStyle == Candle.ChartStyle.TIME_LINE

    override fun isDrawSeparatorLine(): Boolean = false

    override fun isDraw(): Boolean = true
}