package com.liihuu.kline

import android.os.Bundle
import com.liihuu.kline.utils.formatDate
import com.liihuu.kline.utils.formatDecimal
import com.liihuu.klinechart.component.*
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-30-11:05
 */
class ValueFormatActivity : BasicKLineActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k_line_chart.candle.valueFormatter = object: Candle.ValueFormatter {
            override fun format(value: String?): String = value?.format("%5.f") ?: "--"
        }
        k_line_chart.tooltip.valueFormatter = object: Tooltip.ValueFormatter {
            override fun format(seat: Int, indicatorType: String?, value: String): String {
                when (seat) {
                    Tooltip.ValueFormatter.CHART -> {
                        return when (indicatorType) {
                            Indicator.Type.MA -> value.toDouble().formatDecimal(3)
                            Indicator.Type.MACD -> value.toDouble().formatDecimal(5)
                            else -> value.toDouble().formatDecimal(1)
                        }
                    }
                    Tooltip.ValueFormatter.X_AXIS -> {
                        val timestamp = value.toLong()
                        return timestamp.formatDate("HH:mm")
                    }
                    Tooltip.ValueFormatter.Y_AXIS -> return value.toDouble().formatDecimal(1)
                }
                return "--"
            }
        }
        k_line_chart.xAxis.valueFormatter = object: XAxis.ValueFormatter {
            override fun format(value: Long): String = value.formatDate("HH:mm")
        }
        k_line_chart.yAxis.valueFormatter = object: YAxis.ValueFormatter {
            override fun format(indicatorType: String, value: Double): String = value.formatDecimal(1)
        }
    }
}