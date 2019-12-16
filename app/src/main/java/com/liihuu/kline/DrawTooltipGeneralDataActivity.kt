package com.liihuu.kline

import android.graphics.*
import android.os.Bundle
import com.liihuu.kline.utils.DimensionUtils
import com.liihuu.kline.utils.MeasureUtils
import com.liihuu.kline.utils.formatDate
import com.liihuu.kline.utils.formatDecimal
import com.liihuu.klinechart.component.Tooltip
import com.liihuu.klinechart.model.KLineModel
import kotlinx.android.synthetic.main.kline_layout.*
import kotlin.math.max

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-28-15:30
 */
class DrawTooltipGeneralDataActivity : BasicKLineActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textSize = DimensionUtils.dpToPx(this, 10f)
        val dp5ToPx = DimensionUtils.dpToPx(this, 5f)
        val dp3ToPx = DimensionUtils.dpToPx(this, 3f)
        k_line_chart.tooltip.drawGeneralDataListener = object: Tooltip.DrawGeneralDataListener {
            override fun draw(
                canvas: Canvas,
                paint: Paint,
                point: PointF,
                tooltip: Tooltip,
                chartRect: RectF,
                kLineModel: KLineModel
            ) {
                if (point.y > chartRect.bottom) {
                    return
                }
                val labels = resources.getStringArray(R.array.tooltip_general_data_labels).toMutableList()
                val values = mutableListOf(
                    kLineModel.timestamp.formatDate(),
                    kLineModel.openPrice.formatDecimal(),
                    kLineModel.closePrice.formatDecimal(),
                    kLineModel.highPrice.formatDecimal(),
                    kLineModel.lowPrice.formatDecimal(),
                    kLineModel.volume.formatDecimal(0)
                )
                paint.apply {
                    this.textSize = textSize
                    style = Paint.Style.FILL
                    color = Color.parseColor("#000000")
                }
                val textHeight = MeasureUtils.measureTextHeight(paint, "T")
                val labelSize = labels.size
                var labelValueMaxWidth = Float.NEGATIVE_INFINITY
                for (i in 0 until labelSize) {
                    labelValueMaxWidth = max(labelValueMaxWidth, MeasureUtils.measureTextWidth(paint, "${labels[i]}${values[i]}"))
                }

                val rectWidth = labelValueMaxWidth + dp3ToPx * 2
                val rectHeight = textHeight * labelSize + (labelSize + 1) * dp5ToPx
                val startX = if (point.x + rectWidth > chartRect.right) {
                    point.x - rectWidth
                } else {
                    point.x
                }
                val startY = if (point.y + rectHeight > chartRect.bottom) {
                    point.y - rectHeight
                } else {
                    point.y
                }
                val rect = RectF(startX, startY, startX + rectWidth, startY + rectHeight)
                canvas.drawRoundRect(rect, dp3ToPx, dp3ToPx, paint)

                val paintTextAlign = paint.textAlign
                val textStartX = startX + dp3ToPx
                var textStartY = startY + dp5ToPx + textHeight
                paint.color = Color.parseColor("#ffffff")
                for (i in 0 until labelSize) {
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText(labels[i], textStartX, textStartY, paint)
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(values[i], startX + rectWidth - dp3ToPx, textStartY, paint)
                    textStartY += dp5ToPx + textHeight
                }
                paint.textAlign = paintTextAlign
            }
        }
    }
}