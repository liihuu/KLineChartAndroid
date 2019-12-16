package com.liihuu.kline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.os.Bundle
import com.liihuu.kline.model.MaModel
import com.liihuu.klinechart.KLineChartView
import com.liihuu.klinechart.component.Indicator
import com.liihuu.klinechart.model.KLineModel
import kotlinx.android.synthetic.main.activity_custom_indicator.*
import kotlinx.android.synthetic.main.kline_layout.*
import kotlin.math.max
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-23-23:36
 */
class CustomIndicatorActivity : BasicKLineActivity(),
    KLineChartView.CustomIndicatorListener.CalcIndicator,
    KLineChartView.CustomIndicatorListener.CalcYAxisMinMax,
    KLineChartView.CustomIndicatorListener.TooltipLabels,
    KLineChartView.CustomIndicatorListener.TooltipValues,
    KLineChartView.CustomIndicatorListener.DrawIndicator
{
    private val linePath = Path()

    private val MA5 = "MA5"
    private val MA10 = "MA10"
    private val MA20 = "MA20"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k_line_chart.setCustomIndicatorListener(this, this, this, this, this)
        k_line_chart.setSubIndicatorType(MA5)
        btn_indicator_ma5.setOnClickListener {
            k_line_chart.setSubIndicatorType(MA5)
        }
        btn_indicator_ma10.setOnClickListener {
            k_line_chart.setSubIndicatorType(MA10)
        }
        btn_indicator_ma20.setOnClickListener {
            k_line_chart.setSubIndicatorType(MA20)
        }
    }

    override fun generatedLayoutId(): Int = R.layout.activity_custom_indicator

    override fun calcIndicator(indicatorType: String, dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        return when (indicatorType) {
            MA5 -> {
                calcMa(5)
            }
            MA10 -> {
                calcMa(10)
            }
            MA20 -> {
                calcMa(20)
            }
            else -> {
                dataList
            }
        }
    }

    override fun calcYAxisMinMax(indicatorType: String, kLineModel: KLineModel, minMaxArray: DoubleArray) {
        val indicatorData = kLineModel.customIndicator
        (indicatorData as? MaModel)?.apply {
            minMaxArray[0] = min(minMaxArray[0], ma)
            minMaxArray[1] = max(minMaxArray[1], ma)
        }
    }

    override fun tooltipLabels(indicatorType: String): MutableList<String> {
        return when (indicatorType) {
            MA5 -> {
                mutableListOf("MA5")
            }
            MA10 -> {
                mutableListOf("MA10")
            }
            MA20 -> {
                mutableListOf("MA20")
            }
            else -> {
                mutableListOf()
            }
        }
    }

    override fun tooltipValues(indicatorType: String, indicatorData: Any?): MutableList<Double?> {
        if (indicatorData is MaModel) {
            return mutableListOf(indicatorData.ma)
        }
        return mutableListOf()
    }

    override fun draw(
        canvas: Canvas,
        paint: Paint,
        indicator: Indicator,
        startPoint: PointF,
        yMax: Float,
        chartValueRate: Float,
        dataSpace: Float,
        spaceRate: Float,
        drawDataList: MutableList<KLineModel>,
        indicatorType: String
    ) {
        linePath.reset()
        var startX = startPoint.x
        for (i in 0 until drawDataList.size) {
            val endX = startX + dataSpace - spaceRate * dataSpace
            val x = (startX + endX) / 2f
            val indicatorData = drawDataList[i].customIndicator
            if (indicatorData is MaModel) {
                val y = (startPoint.y + chartValueRate * (yMax - indicatorData.ma)).toFloat()
                if (i == 0) {
                    linePath.moveTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                }
            }
            startX += dataSpace
        }
        canvas.drawPath(linePath, paint)
    }

    private fun calcMa(params: Int): MutableList<KLineModel> {
        val dataList = k_line_chart.getDataList()
        val dataSize = dataList.size
        var maSum = 0.0
        var ma: Double
        for (i in 0 until dataSize) {
            val closePrice = dataList[i].closePrice
            maSum += closePrice
            if (i < params) {
                ma = maSum / (i + 1)
            } else {
                maSum -= dataList[i - params].closePrice
                ma = maSum / params
            }

            dataList[i].customIndicator = MaModel(ma)
        }
        return dataList
    }
}