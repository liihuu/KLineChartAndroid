package com.liihuu.kline

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import com.liihuu.kline.utils.formatDate
import com.liihuu.kline.utils.formatDecimal
import com.liihuu.klinechart.component.Tooltip
import com.liihuu.klinechart.model.KLineModel
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-30-10:43
 */
class CustomTooltipGeneralDataActivity : BasicKLineActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k_line_chart.tooltip.generalDataFormatter = object: Tooltip.GeneralDataFormatter {
            override fun generatedLabels(): MutableList<String> = resources.getStringArray(R.array.tooltip_general_data_labels).toMutableList()

            override fun generatedValues(kLineModel: KLineModel): MutableList<String> {
                return mutableListOf(
                    kLineModel.timestamp.formatDate(),
                    kLineModel.openPrice.formatDecimal(),
                    kLineModel.closePrice.formatDecimal(),
                    kLineModel.highPrice.formatDecimal(),
                    kLineModel.lowPrice.formatDecimal(),
                    kLineModel.volume.formatDecimal(0)
                )
            }

            override fun generatedStyle(
                paint: Paint,
                kLineModel: KLineModel,
                tooltip: Tooltip,
                labelPos: Int
            ) {
                when(labelPos) {
                    0 -> paint.color = Color.parseColor("#ff00ff")
                    3 -> paint.color = Color.parseColor("#0000ff")
                    else -> paint.color = tooltip.generalDataTextColor
                }
            }
        }
    }
}