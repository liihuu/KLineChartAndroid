package com.liihuu.kline.utils

import android.content.Context

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-24-21:43
 */
object DimensionUtils {
    /**
     * dp转px
     * @param context Context
     * @param dp Float
     * @return Float
     */
    fun dpToPx(context: Context, dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }
}