package com.liihuu.klinechart.component

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/28-21:51
 */
class Component {

    /**
     * 图表高度尺寸类型
     */
    class ChartHeightSizeType {
        companion object {
            /**
             * 比例值
             */
            const val SCALE = "scale"

            /**
             * 固定值
             */
            const val FIXED = "fixed"
        }
    }

    /**
     * 线的样式
     */
    class LineStyle {
        companion object {
            /**
             * 虚线
             */
            const val DASH = 0

            /**
             * 实线
             */
            const val SOLID = 1
        }
    }
}