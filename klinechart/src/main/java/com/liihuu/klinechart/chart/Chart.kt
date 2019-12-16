package com.liihuu.klinechart.chart

import android.graphics.Canvas
import android.graphics.Paint

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/28-18:50
 */
internal abstract class Chart {

    /**
     * 图表高度
     */
    var height = -1f

    /**
     * 图表内容距离顶部高度
     */
    var offsetTop = 0f

    /**
     * 全局画笔
     */
    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    abstract fun draw(canvas: Canvas)

    /**
     * 设置图表高度和上偏移位置
     * @param height Float
     */
    open fun setChartDimens(height: Float, offsetTop: Float) {
        this.height = height
        this.offsetTop = offsetTop
    }
}