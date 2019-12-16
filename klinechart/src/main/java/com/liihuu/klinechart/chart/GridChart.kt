package com.liihuu.klinechart.chart

import android.graphics.Canvas
import android.graphics.Paint
import com.liihuu.klinechart.component.Grid
import com.liihuu.klinechart.internal.ViewPortHandler

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/5/9-17:13
 */
internal class GridChart(
    private val grid: Grid,
    private val viewPortHandler: ViewPortHandler
): Chart() {
    override fun draw(canvas: Canvas) {
        if (!this.grid.displayGridLine) {
            return
        }
        this.paint.apply {
            style = Paint.Style.STROKE
            color = grid.lineColor
            strokeWidth = grid.lineSize
        }

        canvas.apply {
            drawLine(viewPortHandler.contentLeft(), viewPortHandler.contentTop(), viewPortHandler.contentRight(), viewPortHandler.contentTop(), paint)
            drawLine(viewPortHandler.contentLeft(), viewPortHandler.contentBottom(), viewPortHandler.contentRight(), viewPortHandler.contentBottom(), paint)
            drawLine(viewPortHandler.contentLeft(), viewPortHandler.contentTop(), viewPortHandler.contentLeft(), viewPortHandler.contentBottom(), paint)
            drawLine(viewPortHandler.contentRight(), viewPortHandler.contentTop(), viewPortHandler.contentRight(), viewPortHandler.contentBottom(), paint)
        }
    }
}