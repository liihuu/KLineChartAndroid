package com.liihuu.klinechart.internal

import android.graphics.PointF
import android.graphics.RectF

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/17-16:52
 *
 * 图表视图参数持有类
 */
internal class ViewPortHandler {

    /**
     * 绘制数据的矩形区域
     */
    val contentRect = RectF()

    fun setDimens(left: Float, top: Float, right: Float, bottom: Float) {
        this.contentRect.set(left, top, right, bottom)
    }

    fun contentTop() = this.contentRect.top

    fun contentLeft() = this.contentRect.left

    fun contentRight() = this.contentRect.right

    fun contentBottom() = this.contentRect.bottom

    fun contentWidth() = this.contentRect.width()

    fun contentHeight() = this.contentRect.height()

    fun getContentCenter() = PointF(this.contentRect.centerX(), this.contentRect.centerY())
}