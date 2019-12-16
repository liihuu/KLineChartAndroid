package com.liihuu.klinechart.component

import android.graphics.Color
import com.liihuu.klinechart.internal.utils.Utils

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/5/9-17:15
 */
class Grid {
    /**
     * 是否绘制边框线
     */
    var displayGridLine = false

    /**
     * 边框线尺寸
     */
    var lineSize = Utils.convertDpToPixel(1f)

    /**
     * 边框线颜色
     */
    var lineColor = Color.parseColor("#707070")
}