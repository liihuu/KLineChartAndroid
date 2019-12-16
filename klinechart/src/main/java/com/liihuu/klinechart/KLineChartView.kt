package com.liihuu.klinechart

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.liihuu.klinechart.chart.*
import com.liihuu.klinechart.component.*
import com.liihuu.klinechart.internal.DataProvider
import com.liihuu.klinechart.internal.TouchEvent
import com.liihuu.klinechart.internal.ViewPortHandler
import com.liihuu.klinechart.internal.utils.CalcIndicatorUtils
import com.liihuu.klinechart.internal.utils.Utils
import com.liihuu.klinechart.model.KLineModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/28-17:30
 */
class KLineChartView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0
) : View(
    context,
    attributeSet,
    defStyleAttr
) {
    /**
     * 自定义指标绘制监听
     */
    interface CustomIndicatorListener {

        /**
         * 计算指标接口
         */
        interface CalcIndicator {
            /**
             * 计算指标数据
             * @param indicatorType String 指标类型
             * @param dataList MutableList<KLineModel>
             * @return MutableList<KLineModel>
             */
            fun calcIndicator(indicatorType: String, dataList: MutableList<KLineModel>): MutableList<KLineModel>
        }

        /**
         * 计算y轴上最大最小值接口
         */
        interface CalcYAxisMinMax {
            /**
             * 计算y轴上的最大最小值
             * @param indicatorType String 指标类型
             * @param kLineModel Any?
             * @param minMaxArray DoubleArray
             */
            fun calcYAxisMinMax(indicatorType: String, kLineModel: KLineModel, minMaxArray: DoubleArray)
        }

        /**
         * 获取提示框指标文字接口
         */
        interface TooltipLabels {
            /**
             * 获取指标提示文字
             * @param indicatorType String 指标类型
             * @return MutableList<String>
             */
            fun tooltipLabels(indicatorType: String): MutableList<String>
        }

        /**
         * 获取提示框指标值接口
         */
        interface TooltipValues {
            /**
             * 获取指标提示值
             * @param indicatorType String 指标类型
             * @param indicatorData KLineModel
             * @return DoubleArray
             */
            fun tooltipValues(indicatorType: String, indicatorData: Any?): MutableList<Double?>
        }

        /**
         * 指标绘制接口
         */
        interface DrawIndicator {
            /**
             * 绘制指标
             * @param canvas Canvas
             * @param paint Paint
             * @param indicator Indicator 指标图配置
             * @param startPoint PointF  开始绘制点的位置
             * @param yMax Float y轴上最大值
             * @param chartValueRate Float 值和图表高度的比例
             * @param dataSpace Float 一条数据的宽度
             * @param spaceRate Float 数据与数据之间的间距比例
             * @param drawDataList MutableList<KLineModel> 需要绘制的数据
             * @param indicatorType String 指标类型
             */
            fun draw(
                canvas: Canvas, paint: Paint,
                indicator: Indicator,
                startPoint: PointF,
                yMax: Float, chartValueRate: Float,
                dataSpace: Float, spaceRate: Float,
                drawDataList: MutableList<KLineModel>,
                indicatorType: String
            )
        }
    }

    /**
     * 加载更多监听
     */
    interface LoadMoreListener {
        /**
         * 加载更多
         */
        fun loadMore()
    }

    /**
     * 边框线的一些配置
     */
    lateinit var grid: Grid
    private set

    /**
     * x轴上的一些配置
     */
    lateinit var xAxis: XAxis
    private set

    /**
     * y轴上的一些配置
     */
    lateinit var yAxis: YAxis
    private set

    /**
     * 蜡烛图的一些配置
     */
    lateinit var candle: Candle
    private set

    /**
     * 指标的一些配置
     */
    lateinit var indicator: Indicator
    private set

    /**
     * 提示的一些配置
     */
    lateinit var tooltip: Tooltip
    private set

    /**
     * 图表高度尺寸类型
     */
    var chartHeightSizeType = Component.ChartHeightSizeType.FIXED

    /**
     * 是否需要快速滚动效果
     */
    var decelerationEnable = true

    /**
     * 是否还有更多
     */
    var noMore = false

    /**
     * 加载更多监听
     */
    var loadMoreListener: LoadMoreListener? = null

    /**
     * 视图持有者
     */
    private lateinit var viewPortHandler: ViewPortHandler

    /**
     * 蜡烛图
     */
    private lateinit var candleChart: CandleChart

    /**
     * 成交量图
     */
    private lateinit var volChart: IndicatorChart

    /**
     * 指标图
     */
    private lateinit var indicatorChart: IndicatorChart

    /**
     * 用于绘制提示相关的图
     */
    private lateinit var tooltipChart: TooltipChart

    /**
     * x轴视图
     */
    private lateinit var xAxisChart: XAxisChart

    /**
     * 边框线图
     */
    private lateinit var gridChart: GridChart

    /**
     * 事件处理
     */
    private lateinit var touchEvent: TouchEvent

    /**
     * 用来存储数据，和显示数据的界限
     */
    private lateinit var dataProvider: DataProvider

    /**
     * 自定义指标计算监听
     */
    private var calcIndicator: CustomIndicatorListener.CalcIndicator? = null

    /**
     * 协程
     */
    private val mainScope = MainScope()

    init {
        initializeConfig()
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.KLineChartView)
        initializeChartAttrs(typedArray)
        initializeGridAttrs(typedArray)
        initializeCandleAttrs(typedArray)
        initializeIndicatorAttrs(typedArray)
        initializeTooltipAttrs(typedArray)
        initializeXAxisAttrs(typedArray)
        initializeYAxisAttrs(typedArray)
        typedArray.recycle()
    }

    /**
     * 初始化一些配置
     */
    private fun initializeConfig() {
        Utils.init(context)
        this.candle = Candle()
        this.xAxis = XAxis()
        this.yAxis = YAxis()
        this.indicator = Indicator()
        this.tooltip = Tooltip()
        this.grid = Grid()
        this.viewPortHandler = ViewPortHandler()
        this.dataProvider = DataProvider(this.viewPortHandler)
        this.candleChart = CandleChart(
            this.candle, this.indicator, this.xAxis, this.yAxis,
            this.dataProvider, this.viewPortHandler
        )
        this.volChart = IndicatorChart(
            this.indicator, this.xAxis, this.yAxis,
            this.dataProvider, this.viewPortHandler
        )
        this.indicatorChart = IndicatorChart(
            this.indicator, this.xAxis, this.yAxis,
            this.dataProvider, this.viewPortHandler
        )
        this.tooltipChart = TooltipChart(
            this.candleChart, this.volChart, this.indicatorChart,
            this.tooltip, this.candle, this.indicator, this.yAxis,
            this.dataProvider, this.viewPortHandler
        )

        this.xAxisChart = XAxisChart(this.xAxis, this.dataProvider, this.viewPortHandler)
        this.gridChart = GridChart(this.grid, this.viewPortHandler)
        this.touchEvent = TouchEvent(this, this.dataProvider, this.viewPortHandler)
    }

    /**
     * 初始化图表属性
     * @param typedArray TypedArray
     */
    private fun initializeChartAttrs(typedArray: TypedArray) {
        this.candleChart.indicatorType = typedArray.getString(R.styleable.KLineChartView_mainIndicatorType) ?: Indicator.Type.MA

        val displayVolIndicatorChart = typedArray.getBoolean(R.styleable.KLineChartView_displayVolIndicatorChart, true)
        if (displayVolIndicatorChart) {
            this.volChart.indicatorType = Indicator.Type.VOL
        } else {
            this.volChart.indicatorType = Indicator.Type.NO
        }

        this.indicatorChart.indicatorType = typedArray.getString(R.styleable.KLineChartView_subIndicatorType) ?: Indicator.Type.MACD

        this.chartHeightSizeType = typedArray.getString(R.styleable.KLineChartView_chartHeightSizeType) ?: Component.ChartHeightSizeType.FIXED
        val volChartHeight: Float
        val indicatorChartHeight: Float
        if (this.chartHeightSizeType == Component.ChartHeightSizeType.SCALE) {
            volChartHeight = typedArray.getFloat(R.styleable.KLineChartView_volChartHeight, -1f)
            indicatorChartHeight = typedArray.getFloat(R.styleable.KLineChartView_indicatorChartHeight, -1f)
        } else {
            volChartHeight = typedArray.getDimension(R.styleable.KLineChartView_volChartHeight, -1f)
            indicatorChartHeight = typedArray.getDimension(R.styleable.KLineChartView_indicatorChartHeight, -1f)
        }
        setVolChartHeight(volChartHeight)
        setIndicatorChartHeight(indicatorChartHeight)
        this.decelerationEnable = typedArray.getBoolean(R.styleable.KLineChartView_decelerationEnable, this.decelerationEnable)
    }

    /**
     * 初始化边框线属性
     * @param typedArray TypedArray
     */
    private fun initializeGridAttrs(typedArray: TypedArray) {
        this.grid.apply {
            displayGridLine = typedArray.getBoolean(R.styleable.KLineChartView_grid_displayLine, displayGridLine)
            lineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_grid_lineSize, lineSize.toInt()).toFloat()
            lineColor = typedArray.getColor(R.styleable.KLineChartView_grid_lineColor, lineColor)
        }
    }

    /**
     * 初始化蜡烛图属性
     * @param typedArray TypedArray
     */
    private fun initializeCandleAttrs(typedArray: TypedArray) {
        this.candle.apply {
            increasingColor = typedArray.getColor(R.styleable.KLineChartView_candle_increasingColor, increasingColor)
            decreasingColor = typedArray.getColor(R.styleable.KLineChartView_candle_decreasingColor, decreasingColor)
            candleStyle = typedArray.getInt(R.styleable.KLineChartView_candle_style, candleStyle)
            chartStyle = typedArray.getInt(R.styleable.KLineChartView_candle_chartStyle, chartStyle)
            displayHighestPriceMark = typedArray.getBoolean(R.styleable.KLineChartView_candle_displayHighestPriceMark, displayHighestPriceMark)
            displayLowestPriceMark = typedArray.getBoolean(R.styleable.KLineChartView_candle_displayLowestPriceMark, displayLowestPriceMark)
            lowestHighestPriceMarkTextColor = typedArray.getColor(R.styleable.KLineChartView_candle_lowestHighestPriceMarkTextColor, lowestHighestPriceMarkTextColor)
            lowestHighestPriceMarkTextSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_candle_lowestHighestPriceMarkTextSize, lowestHighestPriceMarkTextSize.toInt()).toFloat()
            displayLastPriceMark = typedArray.getBoolean(R.styleable.KLineChartView_candle_displayLastPriceMark, displayLastPriceMark)
            lastPriceMarkLineStyle = typedArray.getInt(R.styleable.KLineChartView_candle_lastPriceMarkLineStyle, lastPriceMarkLineStyle)
            lastPriceMarkLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_candle_lastPriceMarkLineSize, lastPriceMarkLineSize.toInt()).toFloat()
            lastPriceMarkLineColor = typedArray.getColor(R.styleable.KLineChartView_candle_lastPriceMarkLineColor, lastPriceMarkLineColor)
            timeLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_candle_timeLineSize, timeLineSize.toInt()).toFloat()
            timeLineColor = typedArray.getColor(R.styleable.KLineChartView_candle_timeLineColor, timeLineColor)
            timeLineFillColor = typedArray.getColor(R.styleable.KLineChartView_candle_timeLineFillColor, timeLineFillColor)
            timeAverageLineColor = typedArray.getColor(R.styleable.KLineChartView_candle_timeAverageLineColor, timeAverageLineColor)
        }
    }

    /**
     * 初始化指标属性
     * @param typedArray TypedArray
     */
    private fun initializeIndicatorAttrs(typedArray: TypedArray) {
        this.indicator.apply {
            lineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_indicator_lineSize, lineSize.toInt()).toFloat()
            increasingColor = typedArray.getColor(R.styleable.KLineChartView_indicator_increasingColor, increasingColor)
            decreasingColor = typedArray.getColor(R.styleable.KLineChartView_indicator_decreasingColor, decreasingColor)
        }
    }

    /**
     * 初始化提示框属性
     * @param typedArray TypedArray
     */
    private fun initializeTooltipAttrs(typedArray: TypedArray) {
        this.tooltip.apply {
            crossLineStyle = typedArray.getInt(R.styleable.KLineChartView_tooltip_crossLineStyle, crossLineStyle)
            crossLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_crossLineSize, crossLineSize.toInt()).toFloat()
            crossLineColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_crossLineColor, crossLineColor)
            crossTextRectStrokeLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_crossTextRectStrokeLineSize, crossTextRectStrokeLineSize.toInt()).toFloat()
            crossTextRectStrokeLineColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_crossTextRectStrokeLineColor, crossTextRectStrokeLineColor)
            crossTextRectFillColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_crossTextRectFillColor, crossTextRectFillColor)
            crossTextColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_crossTextColor, crossTextColor)
            crossTextSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_crossTextSize, crossTextSize.toInt()).toFloat()
            crossTextMarginSpace = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_crossTextMarginSpace, crossTextMarginSpace.toInt()).toFloat()
            generalDataRectStrokeLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_generalDataRectStrokeLineSize, generalDataRectStrokeLineSize.toInt()).toFloat()
            generalDataRectStrokeLineColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_generalDataRectStrokeLineColor, generalDataRectStrokeLineColor)
            generalDataRectFillColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_generalDataRectFillColor, generalDataRectFillColor)
            generalDataTextSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_generalDataTextSize, generalDataTextSize.toInt()).toFloat()
            generalDataTextColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_generalDataTextColor, generalDataTextColor)
            generalDataIncreasingColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_generalDataIncreasingColor, generalDataIncreasingColor)
            generalDataDecreasingColor = typedArray.getColor(R.styleable.KLineChartView_tooltip_generalDataDecreasingColor, generalDataDecreasingColor)
            indicatorDisplayRule = typedArray.getInt(R.styleable.KLineChartView_tooltip_indicatorDisplayRule, indicatorDisplayRule)
            indicatorTextSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_tooltip_indicatorTextSize, indicatorTextSize.toInt()).toFloat()
        }
    }

    /**
     * 初始化x轴属性
     * @param typedArray TypedArray
     */
    private fun initializeXAxisAttrs(typedArray: TypedArray) {
        this.xAxis.apply {
            displayAxisLine = typedArray.getBoolean(R.styleable.KLineChartView_xaxis_displayAxisLine, displayAxisLine)
            axisLineColor = typedArray.getColor(R.styleable.KLineChartView_xaxis_axisLineColor, axisLineColor)
            axisLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_axisLineSize, axisLineSize.toInt()).toFloat()
            displayTickText = typedArray.getBoolean(R.styleable.KLineChartView_xaxis_displayTickText, displayTickText)
            tickTextColor = typedArray.getColor(R.styleable.KLineChartView_xaxis_tickTextColor, tickTextColor)
            tickTextSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_tickTextSize, tickTextSize.toInt()).toFloat()
            displayTickLine = typedArray.getBoolean(R.styleable.KLineChartView_xaxis_displayTickLine, true)
            tickLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_tickLineSize, tickLineSize.toInt()).toFloat()
            displaySeparatorLine = typedArray.getBoolean(R.styleable.KLineChartView_xaxis_displaySeparatorLine, displaySeparatorLine)
            separatorLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_separatorLineSize, separatorLineSize.toInt()).toFloat()
            separatorLineColor = typedArray.getColor(R.styleable.KLineChartView_xaxis_separatorLineColor, separatorLineColor)
            separatorLineStyle = typedArray.getInt(R.styleable.KLineChartView_xaxis_separatorLineStyle, separatorLineStyle)
            textMarginSpace = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_textMarginSpace, textMarginSpace.toInt()).toFloat()
            xAxisMaxHeight = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_axisMaxHeight, xAxisMaxHeight.toInt()).toFloat()
            xAxisMinHeight = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_xaxis_axisMinHeight, xAxisMinHeight.toInt()).toFloat()
        }
    }

    /**
     * 初始化y轴属性
     * @param typedArray TypedArray
     */
    private fun initializeYAxisAttrs(typedArray: TypedArray) {
        this.yAxis.apply {
            displayAxisLine = typedArray.getBoolean(R.styleable.KLineChartView_yaxis_displayAxisLine, false)
            axisLineColor = typedArray.getColor(R.styleable.KLineChartView_yaxis_axisLineColor, axisLineColor)
            axisLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_axisLineSize, axisLineSize.toInt()).toFloat()
            displayTickText = typedArray.getBoolean(R.styleable.KLineChartView_yaxis_displayTickText, displayTickText)
            tickTextColor = typedArray.getColor(R.styleable.KLineChartView_yaxis_tickTextColor, tickTextColor)
            tickTextSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_tickTextSize, tickTextSize.toInt()).toFloat()
            displayTickLine = typedArray.getBoolean(R.styleable.KLineChartView_yaxis_displayTickLine, false)
            tickLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_tickLineSize, tickLineSize.toInt()).toFloat()
            displaySeparatorLine = typedArray.getBoolean(R.styleable.KLineChartView_yaxis_displaySeparatorLine, displaySeparatorLine)
            separatorLineSize = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_separatorLineSize, separatorLineSize.toInt()).toFloat()
            separatorLineColor = typedArray.getColor(R.styleable.KLineChartView_yaxis_separatorLineColor, separatorLineColor)
            separatorLineStyle = typedArray.getInt(R.styleable.KLineChartView_yaxis_separatorLineStyle, separatorLineStyle)
            textMarginSpace = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_textMarginSpace, textMarginSpace.toInt()).toFloat()
            yAxisTextPosition = typedArray.getInt(R.styleable.KLineChartView_yaxis_textPosition, yAxisTextPosition)
            yAxisPosition = typedArray.getInt(R.styleable.KLineChartView_yaxis_axisPosition, yAxisPosition)
            yAxisMaxWidth = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_axisMaxWidth, yAxisMaxWidth.toInt()).toFloat()
            yAxisMinWidth = typedArray.getDimensionPixelSize(R.styleable.KLineChartView_yaxis_axisMinWidth, yAxisMinWidth.toInt()).toFloat()

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Utils.convertDpToPixel(50f).toInt()
        setMeasuredDimension(
            max(suggestedMinimumWidth, resolveSize(size, widthMeasureSpec)),
            max(suggestedMinimumHeight, resolveSize(size, heightMeasureSpec))
        )
        calcChartHeight()
        calcOffsets()
    }

    /**
     * 计算各个图表高度
     */
    private fun calcChartHeight() {
        val xChartHeight = this.xAxis.getRequiredHeightSpace()
        val totalChartHeight = measuredHeight - paddingBottom - paddingTop - xChartHeight
        val isDisplayVolChart = isDisplayVolChart()
        val isDisplayIndicatorChart = isDisplayIndicatorChart()
        var volChartHeight = this.volChart.height
        var indicatorChartHeight = this.indicatorChart.height

        when {
            isDisplayVolChart && isDisplayIndicatorChart -> {
                val defaultChartHeight = totalChartHeight * 0.2f
                if (this.chartHeightSizeType == Component.ChartHeightSizeType.SCALE) {
                    volChartHeight = totalChartHeight * this.volChart.chartHeightScale
                    indicatorChartHeight = totalChartHeight * this.indicatorChart.chartHeightScale
                }
                volChartHeight = fixChartHeight(totalChartHeight, volChartHeight, defaultChartHeight)
                indicatorChartHeight = fixChartHeight(totalChartHeight, indicatorChartHeight, defaultChartHeight)

                if (totalChartHeight < volChartHeight + indicatorChartHeight) {
                    volChartHeight = defaultChartHeight
                    indicatorChartHeight = defaultChartHeight
                }
            }
            isDisplayVolChart && !isDisplayIndicatorChart -> {
                val defaultChartHeight = totalChartHeight * 0.3f
                if (this.chartHeightSizeType == Component.ChartHeightSizeType.SCALE) {
                    volChartHeight = totalChartHeight * this.volChart.chartHeightScale
                }
                volChartHeight = fixChartHeight(totalChartHeight, volChartHeight, defaultChartHeight)
                indicatorChartHeight = -1f
            }
            !isDisplayVolChart && isDisplayIndicatorChart -> {
                val defaultChartHeight = totalChartHeight * 0.3f
                if (this.chartHeightSizeType == Component.ChartHeightSizeType.SCALE) {
                    indicatorChartHeight = totalChartHeight * this.indicatorChart.chartHeightScale
                }
                indicatorChartHeight = fixChartHeight(totalChartHeight, indicatorChartHeight, defaultChartHeight)
                volChartHeight = -1f
            }
        }
        val candleChartHeight = totalChartHeight - volChartHeight - indicatorChartHeight
        var contentTop = paddingTop.toFloat()
        this.candleChart.setChartDimens(candleChartHeight, contentTop)

        contentTop += candleChartHeight
        this.volChart.setChartDimens(volChartHeight, contentTop)

        contentTop += volChartHeight
        this.indicatorChart.setChartDimens(indicatorChartHeight, contentTop)

        contentTop += indicatorChartHeight
        this.xAxisChart.setChartDimens(xChartHeight, contentTop)
    }

    /**
     * 修复图表高度，防止高度超出整个view的高度
     * @param totalChartHeight Float
     * @param chartHeight Float
     * @param defaultHeight Float
     * @return Float
     */
    private fun fixChartHeight(totalChartHeight: Float, chartHeight: Float, defaultHeight: Float): Float {
        if (chartHeight < 0 || totalChartHeight < chartHeight) {
            return defaultHeight
        }
        return chartHeight
    }

    override fun onDraw(canvas: Canvas) {
        this.dataProvider.space()
        this.gridChart.draw(canvas)
        this.candleChart.draw(canvas)
        this.volChart.draw(canvas)
        this.indicatorChart.draw(canvas)
        this.xAxisChart.draw(canvas)
        this.tooltipChart.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return this.touchEvent.onTouch(this, event)
    }

    override fun computeScroll() {
        this.touchEvent.computeScroll()
    }

    /**
     * 计算不包括x轴y轴的绘制区域的尺寸
     */
    fun calcOffsets() {
        var offsetLeft = paddingLeft.toFloat()
        var offsetRight = paddingRight.toFloat()
        val offsetTop = paddingTop.toFloat()
        var offsetBottom = paddingBottom.toFloat()

        if (this.yAxis.needsOffset()) {
            // 计算y轴最大宽度
            val yAxisRequireWidthSpace = this.yAxis.getRequiredWidthSpace()
            if (this.yAxis.yAxisPosition == YAxis.AxisPosition.LEFT) {
                offsetLeft += yAxisRequireWidthSpace
            } else {
                offsetRight += yAxisRequireWidthSpace
            }
        }

        val requireXAxisHeight = this.xAxis.getRequiredHeightSpace()
        offsetBottom += requireXAxisHeight

        this.viewPortHandler.setDimens(
            offsetLeft, offsetTop,
            measuredWidth - offsetRight,
            measuredHeight - offsetBottom
        )
    }

    /**
     * 计算指标数据
     * @param indicatorType String
     */
    private fun calcIndicator(indicatorType: String) {
        this.mainScope.launch {
            when (indicatorType) {
                Indicator.Type.NO -> {
                }

                Indicator.Type.MA -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcMa(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.MACD -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcMacd(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.VOL -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcVol(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.BOLL -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcBoll(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.BIAS -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcBias(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.BRAR -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcBrar(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.CCI -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcCci(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.CR -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcCr(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.DMA -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcDma(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.DMI -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcDmi(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.KDJ -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcKdj(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.KD -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcKdj(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.RSI -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcRsi(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.PSY -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcPsy(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.TRIX -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcTrix(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.OBV -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcObv(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.VR -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcVr(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.WR -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcWr(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.MTM -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcMtm(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.EMV -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcEmv(this@KLineChartView.dataProvider.dataList)
                }
                Indicator.Type.SAR -> {
                    this@KLineChartView.dataProvider.dataList =
                        CalcIndicatorUtils.calcSar(this@KLineChartView.dataProvider.dataList)
                }
                else -> {
                    this@KLineChartView.dataProvider.dataList =
                        this@KLineChartView.calcIndicator?.calcIndicator(
                            indicatorType,
                            this@KLineChartView.dataProvider.dataList
                        ) ?: this@KLineChartView.dataProvider.dataList
                }
            }
            invalidate()
        }
    }

    /**
     * 计算图表指标
     */
    private fun calcChartIndicator() {
        if (this.candleChart.isDisplayIndicatorChart()) {
            calcIndicator(this.candleChart.indicatorType)
        }
        if (this.volChart.isDisplayIndicatorChart()) {
            calcIndicator(Indicator.Type.VOL)
        }
        if (this.indicatorChart.isDisplayIndicatorChart()) {
            calcIndicator(this.indicatorChart.indicatorType)
        }
    }

    /**
     * 添加单个数据
     * @param kLineModel KLineModel
     * @param pos Int
     */
    @JvmOverloads
    @Synchronized
    fun addData(kLineModel: KLineModel, pos: Int = getDataList().size) {
        this.dataProvider.addData(kLineModel, pos)
        calcChartIndicator()
    }

    /**
     * 添加数据集合
     * @param list MutableList<KLineModel>
     * @param pos Int
     */
    @JvmOverloads
    @Synchronized
    fun addData(list: MutableList<KLineModel>, pos: Int = 0) {
        val dataSize = list.size
        if (dataSize > 0) {
            this.dataProvider.addData(list, pos)
            calcChartIndicator()
        }
    }

    /**
     * 设置主指标
     * @param indicatorType String
     */
    fun setMainIndicatorType(indicatorType: String) {
        if (this.candleChart.indicatorType != indicatorType) {
            this.candleChart.indicatorType = indicatorType
            calcIndicator(indicatorType)
        }
    }

    /**
     * 当前主图指标类型
     * @return Int
     */
    fun getMainIndicatorType() = this.candleChart.indicatorType

    /**
     * 设置是否显示vol指标
     * @param isShow Boolean
     */
    fun setShowVolIndicatorChart(isShow: Boolean) {
        if (isDisplayVolChart() != isShow) {
            if (isShow) {
                this.volChart.indicatorType = Indicator.Type.VOL
                calcIndicator(Indicator.Type.VOL)
            } else {
                this.volChart.indicatorType = Indicator.Type.NO
            }
            calcChartHeight()
        }
    }

    /**
     * 是否显示成交量指标图
     * @return Boolean
     */
    fun isDisplayVolChart() = this.volChart.isDisplayIndicatorChart()

    /**
     * 设置副指标
     * @param indicatorType String
     */
    fun setSubIndicatorType(indicatorType: String) {
        if (this.indicatorChart.indicatorType != indicatorType) {
            val shouldCalcChartHeight = indicatorType == Indicator.Type.NO || this.indicatorChart.indicatorType == Indicator.Type.NO
            this.indicatorChart.indicatorType = indicatorType
            if (shouldCalcChartHeight) {
                calcChartHeight()
            }
            calcIndicator(indicatorType)
        }
    }

    /**
     * 是否显示副图指标图
     * @return Boolean
     */
    fun isDisplayIndicatorChart() = this.indicatorChart.isDisplayIndicatorChart()

    /**
     * 当前副图指标类型
     * @return Int
     */
    fun getSubIndicatorType() = this.indicatorChart.indicatorType

    /**
     * 设置自定义指标实现监听
     * @param calcIndicator CalcIndicator?
     * @param calcYAxisMinMax CalcYAxisMinMax?
     * @param tooltipLabels TooltipLabels?
     * @param tooltipValues TooltipValues?
     * @param drawIndicator DrawIndicator?
     */
    fun setCustomIndicatorListener(
        calcIndicator: CustomIndicatorListener.CalcIndicator?,
        calcYAxisMinMax: CustomIndicatorListener.CalcYAxisMinMax?,
        tooltipLabels: CustomIndicatorListener.TooltipLabels?,
        tooltipValues: CustomIndicatorListener.TooltipValues?,
        drawIndicator: CustomIndicatorListener.DrawIndicator?
    ) {
        this.calcIndicator = calcIndicator
        this.indicatorChart.yAxisChart.calcYAxisMinMax = calcYAxisMinMax
        this.indicatorChart.drawCustomIndicator = drawIndicator
        this.tooltipChart.tooltipLabels = tooltipLabels
        this.tooltipChart.tooltipValues = tooltipValues
    }

    /**
     * 获取线图数据
     * @return MutableList<KLineModel>
     */
    fun getDataList() = this.dataProvider.dataList

    /**
     * 设置成交量图高度
     * @param height Float
     */
    fun setVolChartHeight(height: Float) {
        if (this.chartHeightSizeType == Component.ChartHeightSizeType.SCALE) {
            this.volChart.chartHeightScale = height
        } else {
            this.volChart.height = Utils.convertDpToPixel(height)
        }
    }

    /**
     * 设置指标图高度
     * @param height Float
     */
    fun setIndicatorChartHeight(height: Float) {
        if (this.chartHeightSizeType == Component.ChartHeightSizeType.SCALE) {
            this.indicatorChart.chartHeightScale = height
        } else {
            this.indicatorChart.height = Utils.convertDpToPixel(height)
        }
    }

    /**
     * 加载完成
     */
    fun loadComplete() {
        this.dataProvider.isLoading = false
    }

    /**
     * 清空数据
     */
    fun clearDataList() {
        this.dataProvider.dataList.clear()
    }

    override fun onDetachedFromWindow() {
        // 关闭线程池，移除所有消息
        this.mainScope.cancel()
        super.onDetachedFromWindow()
    }
}