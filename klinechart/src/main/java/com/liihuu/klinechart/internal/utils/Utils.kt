package com.liihuu.klinechart.internal.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.util.DisplayMetrics

internal object Utils {
    private lateinit var metrics: DisplayMetrics

    private lateinit var resources: Resources

    private val calcTextSizeRect = Rect()

    /**
     * 初始化
     * @param context Context
     */
    fun init(context: Context) {
        resources = context.resources
        metrics = resources.displayMetrics
    }

    /**
     * dp转px
     * @param dp Float
     * @return Float
     */
    fun convertDpToPixel(dp: Float): Float {
        return dp * metrics.density
    }

    /**
     * 测量文字的宽度
     * @param paint Paint
     * @param demoText String
     * @return Int
     */
    fun calcTextWidth(paint: Paint, demoText: String): Int {
        return paint.measureText(demoText).toInt()
    }

    /**
     * 测量文字高度
     * @param paint Paint
     * @param demoText String
     * @return Int
     */
    fun calcTextHeight(paint: Paint, demoText: String): Int = getTextRect(
        paint,
        demoText
    ).height()

    /**
     * 测量文字尺寸
     * @param paint Paint
     * @param demoText String
     * @return Rect
     */
    fun calcTextSize(paint: Paint, demoText: String): Rect =
        getTextRect(paint, demoText)

    /**
     * 获取文字的rect
     * @param paint Paint
     * @param demoText String
     * @return Rect
     */
    private fun getTextRect(paint: Paint, demoText: String): Rect {
        val r = calcTextSizeRect
        r.set(0,0,0,0)
        paint.getTextBounds(demoText, 0, demoText.length, r)
        return r
    }

    /**
     * 获取Resources字符串
     * @param id
     * @return
     */
    fun getResourceString(id: Int): String {
        return resources.getString(id)
    }
}