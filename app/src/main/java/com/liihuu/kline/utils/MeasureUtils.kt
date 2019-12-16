package com.liihuu.kline.utils

import android.graphics.Paint
import android.graphics.Rect

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-29-22:27
 */
object MeasureUtils {
    /**
     * 测量文字宽度
     * @param paint Paint
     * @param demoText String
     * @return Float
     */
    fun measureTextWidth(paint: Paint, demoText: String): Float {
        return paint.measureText(demoText)
    }

    /**
     * 测量文字高度
     * @param paint Paint
     * @param demoText String
     * @return Int
     */
    fun measureTextHeight(paint: Paint, demoText: String): Float {
        val r = Rect()
        paint.getTextBounds(demoText, 0, demoText.length, r)
        return r.width().toFloat()
    }
}