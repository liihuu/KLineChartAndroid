package com.liihuu.kline

import android.graphics.*
import android.os.Bundle
import com.liihuu.kline.utils.DimensionUtils
import com.liihuu.klinechart.component.Candle
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-24-21:26
 */
class DrawPriceMarkActivity : BasicKLineActivity() {
    private val lastPricePath = Path()

    private var priceTextSize: Float = 0f

    private var dp1ToPx: Float = 0f
    private var dp2ToPx: Float = 0f

    private var dp6ToPx: Float = 0f
    private var dp4ToPx: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        priceTextSize = DimensionUtils.dpToPx(this@DrawPriceMarkActivity, 12f)
        dp1ToPx = DimensionUtils.dpToPx(this, 1f)
        dp2ToPx = DimensionUtils.dpToPx(this, 2f)
        dp4ToPx = DimensionUtils.dpToPx(this, 4f)
        dp6ToPx = DimensionUtils.dpToPx(this, 5f)
        k_line_chart.candle.drawPriceMarkListener = object : Candle.DrawPriceMarkListener {
            override fun draw(
                canvas: Canvas,
                paint: Paint,
                drawType: Int,
                point: PointF,
                chartRect: RectF,
                candle: Candle,
                price: Double
            ) {
                when (drawType) {
                    Candle.DrawPriceMarkListener.HIGHEST, Candle.DrawPriceMarkListener.LOWEST -> {
                        drawHighestLowestPriceMark(canvas, paint, point, price)
                    }
                    Candle.DrawPriceMarkListener.LAST -> {
                        drawLastPriceMark(canvas, paint, point, chartRect, price)
                    }
                }
            }
        }
    }

    private fun drawHighestLowestPriceMark(
        canvas: Canvas,
        paint: Paint,
        point: PointF,
        price: Double
    ) {
        val priceLabel = String.format("%.2f", price)
        paint.apply {
            textSize = priceTextSize
            color = Color.parseColor("#505050")
            style = Paint.Style.STROKE
            strokeWidth = dp1ToPx
        }
        val labelWidth = paint.measureText(priceLabel)

        canvas.drawCircle(
            point.x + dp2ToPx + labelWidth / 2f,
            point.y,
            labelWidth / 2 + dp2ToPx,
            paint
        )

        paint.style = Paint.Style.FILL
        canvas.drawText(priceLabel, point.x + dp2ToPx * 2, point.y + priceTextSize / 2, paint)
    }

    private fun drawLastPriceMark(
        canvas: Canvas,
        paint: Paint,
        point: PointF,
        chartRect: RectF,
        price: Double
    ) {
        paint.apply {
            textSize = priceTextSize
            color = Color.parseColor("#505050")
            style = Paint.Style.FILL
            strokeWidth = dp1ToPx
        }
        lastPricePath.reset()
        lastPricePath.apply {
            moveTo(chartRect.right, point.y)
            lineTo(chartRect.right - dp6ToPx, point.y + dp4ToPx)
            lineTo(chartRect.right - dp6ToPx, point.y - dp4ToPx)
            close()
        }
        canvas.drawPath(lastPricePath, paint)

        canvas.drawLine(
            chartRect.right - dp6ToPx, point.y,
            chartRect.right - dp6ToPx * 6, point.y,
            paint
        )
        val priceLabel = String.format("%.2f", price)
        val labelWidth = paint.measureText(priceLabel)
        canvas.drawText(
            priceLabel,
            chartRect.right - dp6ToPx * 6 - dp2ToPx - labelWidth,
            point.y + priceTextSize / 2,
            paint
        )
    }
}