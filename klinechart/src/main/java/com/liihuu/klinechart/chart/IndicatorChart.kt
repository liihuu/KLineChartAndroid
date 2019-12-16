package com.liihuu.klinechart.chart

import android.graphics.*
import com.liihuu.klinechart.KLineChartView
import com.liihuu.klinechart.component.Indicator
import com.liihuu.klinechart.component.XAxis
import com.liihuu.klinechart.component.YAxis
import com.liihuu.klinechart.internal.DataProvider
import com.liihuu.klinechart.internal.ViewPortHandler
import com.liihuu.klinechart.model.KLineModel
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/28-18:51
 */
internal open class IndicatorChart(
    private val indicator: Indicator,
    private val xAxis: XAxis,
    yAxis: YAxis,
    private val dataProvider: DataProvider,
    private val viewPortHandler: ViewPortHandler
) : Chart() {

    /**
     * y轴视图
     */
    val yAxisChart = YAxisChart(yAxis, dataProvider, viewPortHandler)

    /**
     * 指标类型
     */
    var indicatorType = Indicator.Type.MACD

    /**
     * 图表高度比例
     */
    var chartHeightScale = -1f

    /**
     * 自定义指标绘制
     */
    var drawCustomIndicator: KLineChartView.CustomIndicatorListener.DrawIndicator? = null

    private val barBuffers = FloatArray(4)

    private val linePaths = arrayListOf(Path(), Path(), Path(), Path(), Path())
    private val pathHasMoves = booleanArrayOf(false, false, false, false, false)

    private val clipRect = RectF()

    override fun setChartDimens(height: Float, offsetTop: Float) {
        super.setChartDimens(height, offsetTop)
        this.yAxisChart.setChartDimens(height, offsetTop)
    }

    override fun draw(canvas: Canvas) {
        if (isDraw()) {
            val isMainIndicator = isMain()
            val labelCount = if (isMainIndicator) 6 else 3
            drawChartHorizontalSeparatorLine(canvas)
            this.yAxisChart.apply {
                getYAxisDataMinMax(indicatorType, isMainIndicator, isTimeLine())
                computeAxis(labelCount)
                drawSeparatorLines(canvas)
                drawAxisLine(canvas)
            }

            drawChart(canvas)

            this.yAxisChart.apply {
                drawTickLines(canvas)
                drawAxisLabels(canvas, indicatorType)
            }
        }
    }

    /**
     * 绘制整个图
     * @param canvas Canvas
     */
    open fun drawChart(canvas: Canvas) {
        drawIndicator(canvas)
    }

    /**
     * 绘制各图表水平分割线
     * @param canvas Canvas
     */
    private fun drawChartHorizontalSeparatorLine(canvas: Canvas) {
        if (isDrawSeparatorLine()) {
            this.paint.apply {
                strokeWidth = xAxis.axisLineSize
                color = xAxis.axisLineColor
                style = Paint.Style.STROKE
            }
            canvas.drawLine(
                this.viewPortHandler.contentLeft(),
                this.offsetTop,
                this.viewPortHandler.contentRight(),
                this.offsetTop,
                this.paint
            )
        }
    }

    /**
     * 绘制指标
     * @param canvas Canvas
     */
    fun drawIndicator(canvas: Canvas) {
        for (i in 0 until this.linePaths.size) {
            this.pathHasMoves[i] = false
            this.linePaths[i].reset()
        }
        var isDraw = true
        var onDrawing: ((i: Int, x: Float, halfBarSpace: Float, kLineModel: KLineModel) -> Unit) = { _, _, _, _ -> }
        var lineNumber = 0
        this.paint.strokeWidth = indicator.lineSize
        when (this.indicatorType) {
            Indicator.Type.NO -> {
                isDraw = false
            }

            Indicator.Type.MA -> {
                lineNumber = 4
                val isMainIndicator = isMain()
                val dataList = this.dataProvider.dataList
                onDrawing = { i, x, halfBarSpace, kLineModel ->
                    val ma = kLineModel.ma
                    preparePath(listOf(ma?.ma5, ma?.ma10, ma?.ma20, ma?.ma60), x)
                    drawIndicatorOhlc(canvas, kLineModel, isMainIndicator, dataList, i, halfBarSpace, x)
                }
            }

            Indicator.Type.MACD -> {
                val dataList = this.dataProvider.dataList
                lineNumber = 2
                onDrawing = { i, x, halfBarSpace, kLineModel ->
                    preparePath(listOf(kLineModel.macd?.diff, kLineModel.macd?.dea), x)
                    var refKLineModel: KLineModel? = null
                    if (i > 0) {
                        refKLineModel = dataList[i - 1]
                    }
                    val macd = kLineModel.macd?.macd ?: -1.0
                    val refMacd = refKLineModel?.macd?.macd
                    if (macd > 0) {
                        this.paint.color = indicator.increasingColor
                    } else {
                        this.paint.color = indicator.decreasingColor
                    }
                    if (refMacd != null && macd > refMacd) {
                        this.paint.style = Paint.Style.STROKE
                    } else {
                        this.paint.style = Paint.Style.FILL
                    }
                    drawBars(canvas, x, halfBarSpace, macd)
                }
            }

            Indicator.Type.VOL -> {
                val dataList = this.dataProvider.dataList
                lineNumber = 3
                onDrawing = { i, x, halfBarSpace, kLineModel ->
                    val vol = kLineModel.vol
                    preparePath(listOf(vol?.ma5, vol?.ma10, vol?.ma20), x)

                    var refKLineModel: KLineModel? = null
                    if (i > 0) {
                        refKLineModel = dataList[i - 1]
                    }
                    val refClosePrice = refKLineModel?.closePrice ?: Double.NEGATIVE_INFINITY
                    this.paint.style = Paint.Style.FILL
                    if (kLineModel.closePrice > refClosePrice) {
                        this.paint.color = indicator.increasingColor
                    } else {
                        this.paint.color = indicator.decreasingColor
                    }
                    drawBars(canvas, x, halfBarSpace, vol?.num)
                }
            }

            Indicator.Type.BOLL -> {
                val isMainIndicator = isMain()
                val dataList = this.dataProvider.dataList
                lineNumber = 3
                onDrawing = { i, x, halfBarSpace, kLineModel ->
                    val boll = kLineModel.boll
                    preparePath(listOf(boll?.up, boll?.mid, boll?.dn), x)
                    drawIndicatorOhlc(canvas, kLineModel, isMainIndicator, dataList, i, halfBarSpace, x)
                }
            }

            Indicator.Type.BIAS -> {
                lineNumber = 3
                onDrawing = { _, x, _, kLineModel ->
                    val bias = kLineModel.bias
                    preparePath(listOf(bias?.bias1, bias?.bias2, bias?.bias3), x)
                }
            }

            Indicator.Type.BRAR -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val brar = kLineModel.brar
                    preparePath(listOf(brar?.br, brar?.ar), x)
                }
            }

            Indicator.Type.CCI -> {
                lineNumber = 1
                onDrawing = { _, x, _, kLineModel ->
                    val cci = kLineModel.cci
                    preparePath(listOf(cci?.cci), x)
                }
            }

            Indicator.Type.CR -> {
                lineNumber = 5
                onDrawing = { _, x, _, kLineModel ->
                    val cr = kLineModel.cr
                    preparePath(listOf(cr?.cr, cr?.ma1, cr?.ma2, cr?.ma3, cr?.ma4), x)
                }
            }

            Indicator.Type.DMA -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val dma = kLineModel.dma
                    preparePath(listOf(dma?.dif, dma?.difMa), x)
                }
            }

            Indicator.Type.DMI -> {
                lineNumber = 4
                onDrawing = { _, x, _, kLineModel ->
                    val dmi = kLineModel.dmi
                    preparePath(listOf(dmi?.mdi, dmi?.pdi, dmi?.adx, dmi?.adxr), x)
                }
            }

            Indicator.Type.KDJ -> {
                lineNumber = 3
                onDrawing = { _, x, _, kLineModel ->
                    val kdj = kLineModel.kdj
                    preparePath(listOf(kdj?.k, kdj?.d, kdj?.j), x)
                }
            }

            Indicator.Type.KD -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val kdj = kLineModel.kdj
                    preparePath(listOf(kdj?.k, kdj?.d), x)
                }
            }

            Indicator.Type.RSI -> {
                lineNumber = 3
                onDrawing = { _, x, _, kLineModel ->
                    val rsi = kLineModel.rsi
                    preparePath(listOf(rsi?.rsi1, rsi?.rsi2, rsi?.rsi3), x)
                }
            }

            Indicator.Type.PSY -> {
                lineNumber = 1
                onDrawing = { _, x, _, kLineModel ->
                    val psy = kLineModel.psy
                    preparePath(listOf(psy?.psy), x)
                }
            }

            Indicator.Type.TRIX -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val trix = kLineModel.trix
                    preparePath(listOf(trix?.trix, trix?.maTrix), x)
                }
            }

            Indicator.Type.OBV -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val obv = kLineModel.obv
                    preparePath(listOf(obv?.obv, obv?.maObv), x)
                }
            }

            Indicator.Type.VR -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val vr = kLineModel.vr
                    preparePath(listOf(vr?.vr, vr?.maVr), x)
                }
            }

            Indicator.Type.WR -> {
                lineNumber = 3
                onDrawing = { _, x, _, kLineModel ->
                    val wr = kLineModel.wr
                    preparePath(listOf(wr?.wr1, wr?.wr2, wr?.wr3), x)
                }
            }

            Indicator.Type.MTM -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val mtm = kLineModel.mtm
                    preparePath(listOf(mtm?.mtm, mtm?.mtmMa), x)
                }
            }

            Indicator.Type.EMV -> {
                lineNumber = 2
                onDrawing = { _, x, _, kLineModel ->
                    val emv = kLineModel.emv
                    preparePath(listOf(emv?.emv, emv?.maEmv), x)
                }
            }

            Indicator.Type.SAR -> {
                val isMainIndicator = isMain()
                val dataList = this.dataProvider.dataList
                this.paint.apply {
                    style = Paint.Style.STROKE
                    strokeWidth = indicator.lineSize
                }
                onDrawing = { i, x, halfBarSpace, kLineModel ->
                    drawIndicatorOhlc(canvas, kLineModel, isMainIndicator, dataList, i, halfBarSpace, x)
                    val data = kLineModel.sar
                    val sar = data?.sar
                    if (sar != null) {
                        val dataY = this.yAxisChart.getY(sar)
                        if (sar < (kLineModel.highPrice + kLineModel.lowPrice) / 2) {
                            this.paint.color = indicator.increasingColor
                        } else {
                            this.paint.color = indicator.decreasingColor
                        }
                        canvas.drawCircle(x, dataY, halfBarSpace, this.paint)
                    }
                }
            }

            else -> {
                isDraw = false
                val lastPos = min(this.dataProvider.visibleDataMinPos + this.dataProvider.visibleDataCount, this.dataProvider.dataList.size)
                val dataList = this.dataProvider.dataList.subList(this.dataProvider.visibleDataMinPos, lastPos)
                val chartValueRate = this.height / this.yAxisChart.axisRange
                this.drawCustomIndicator?.draw(
                    canvas, this.paint, this.indicator,
                    PointF(this.viewPortHandler.contentLeft(), this.offsetTop),
                    this.yAxisChart.axisMaximum, chartValueRate,
                    this.dataProvider.dataSpace, DataProvider.DATA_SPACE_RATE,
                    dataList, this.indicatorType
                )
            }
        }
        if (isDraw) {
            drawGraphs(canvas, onDrawing) {
                drawLines(canvas, lineNumber)
            }
        }
    }

    /**
     * 绘制图形
     * @param canvas Canvas
     * @param onDrawing Function4<[@kotlin.ParameterName] Int, [@kotlin.ParameterName] Float, [@kotlin.ParameterName] Float, [@kotlin.ParameterName] KLineModel, Unit>
     * @param onDrawEnd Function0<Unit>
     */
    inline fun drawGraphs(
        canvas: Canvas,
        onDrawing: (i: Int, x: Float, halfBarSpace: Float, kLineModel: KLineModel) -> Unit,
        onDrawEnd: () -> Unit
    ) {
        val clipRestoreCount = canvas.save()
        this.clipRect.set(
            this.viewPortHandler.contentLeft(), this.offsetTop,
            this.viewPortHandler.contentRight(), this.offsetTop + this.height
        )
        canvas.clipRect(this.clipRect)
        var startX = this.viewPortHandler.contentLeft()
        val dataSpace = this.dataProvider.dataSpace * (1f - DataProvider.DATA_SPACE_RATE)
        val halfBarSpace = dataSpace / 2
        val firstPos = this.dataProvider.visibleDataMinPos
        val lastPos = min(this.dataProvider.dataList.size, this.dataProvider.visibleDataMinPos + this.dataProvider.visibleDataCount)
        for (i in firstPos until lastPos) {
            val endX = startX + dataSpace
            val x = (startX + endX) / 2f
            val kLineModel = this.dataProvider.dataList[i]

            onDrawing(i, x, halfBarSpace, kLineModel)

            startX += this.dataProvider.dataSpace
        }
        onDrawEnd()
        canvas.restoreToCount(clipRestoreCount)
    }

    /**
     * 绘制指标ohlc线
     * @param canvas Canvas
     * @param kLineModel KLineModel
     * @param isMainIndicator Boolean
     * @param dataList MutableList<KLineModel>
     * @param i Int
     * @param halfBarSpace Float
     * @param x Float
     */
    private fun drawIndicatorOhlc(
        canvas: Canvas, kLineModel: KLineModel,
        isMainIndicator:Boolean, dataList: MutableList<KLineModel>,
        i: Int, halfBarSpace: Float, x: Float
    ) {
        if (!isMainIndicator) {
            var refKLineModel: KLineModel? = null
            if (i > 0) {
                refKLineModel = dataList[i - 1]
            }
            val refClosePrice = refKLineModel?.closePrice ?: Double.NEGATIVE_INFINITY
            drawOhlc(
                canvas, x, halfBarSpace, refClosePrice,
                kLineModel.openPrice, kLineModel.closePrice,
                kLineModel.highPrice, kLineModel.lowPrice,
                this.indicator.increasingColor, this.indicator.decreasingColor
            )
        }
    }

    /**
     * 绘制ohlc
     * @param canvas Canvas
     * @param x Float
     * @param halfBarSpace Float
     * @param refClosePrice Double
     * @param openPrice Double
     * @param closePrice Double
     * @param highPrice Double
     * @param lowPrice Double
     * @param increasingColor Int
     * @param decreasingColor Int
     */
    internal fun drawOhlc(
        canvas: Canvas, x: Float, halfBarSpace: Float,
        refClosePrice: Double,
        openPrice: Double, closePrice:
        Double, highPrice: Double, lowPrice: Double,
        increasingColor: Int, decreasingColor: Int) {
        val priceY = getPriceY(
            openPrice, closePrice,
            highPrice, lowPrice
        )
        if (closePrice > refClosePrice) {
            this.paint.color = increasingColor
        } else {
            this.paint.color = decreasingColor
        }
        canvas.apply {
            drawLine(x, priceY[2], x, priceY[3], paint)
            drawLine(x - halfBarSpace, priceY[0], x, priceY[0], paint)
            drawLine(x + halfBarSpace, priceY[1], x, priceY[1], paint)
        }
    }

    /**
     * 准备path
     * @param values MutableList<Double?>
     * @param x Float
     */
    private fun preparePath(values: List<Double?>, x:Float) {
        for (i in values.indices) {
            val value = values[i]
            if (value != null) {
                val y = this.yAxisChart.getY(value)
                if (this.pathHasMoves[i]) {
                    this.linePaths[i].lineTo(x, y)
                } else {
                    this.linePaths[i].moveTo(x, y)
                    this.pathHasMoves[i] = true
                }
            }
        }
    }

    private fun drawLines(canvas: Canvas, lineNumber: Int) {
        this.paint.style = Paint.Style.STROKE
        val lineColorSize = this.indicator.lineColors.size
        for (k in 0 until lineNumber) {
            this.paint.color = this.indicator.lineColors[k % lineColorSize]
            canvas.drawPath(this.linePaths[k], this.paint)
        }
    }

    private fun drawBars(canvas: Canvas, x: Float, halfBarSpace: Float, barData: Double?) {
        if (barData != null) {
            this.barBuffers[0] = x - halfBarSpace
            this.barBuffers[2] = x + halfBarSpace
            val dataY = this.yAxisChart.getY(barData)
            val zeroY = this.yAxisChart.getY(0.0)
            this.barBuffers[1] = dataY
            this.barBuffers[3] = zeroY

            canvas.drawRect(this.barBuffers[0], this.barBuffers[1], this.barBuffers[2], this.barBuffers[3], this.paint)
        }
    }

    /**
     * 获取价格对应的y点
     * @param openPrice Double
     * @param closePrice Double
     * @param highPrice Double
     * @param lowPrice Double
     * @return FloatArray
     */
    fun getPriceY(
        openPrice: Double, closePrice: Double,
        highPrice: Double, lowPrice: Double
    ): FloatArray = floatArrayOf(
        this.yAxisChart.getY(openPrice),
        this.yAxisChart.getY(closePrice),
        this.yAxisChart.getY(highPrice),
        this.yAxisChart.getY(lowPrice)
    )

    /**
     * 是否显示指标视图
     * @return Boolean
     */
    fun isDisplayIndicatorChart(): Boolean = this.indicatorType != Indicator.Type.NO

    /**
     * 是否绘制
     * @return Boolean
     */
    open fun isDraw(): Boolean = isDisplayIndicatorChart() && this.height > 0f

    /**
     * 是否是主图
     * @return Boolean
     */
    open fun isMain(): Boolean = false

    /**
     * 是否是分时线
     * @return Boolean
     */
    open fun isTimeLine(): Boolean = false

    /**
     * 是否绘制图直接的分割线
     * @return Boolean
     */
    open fun isDrawSeparatorLine(): Boolean = true
}