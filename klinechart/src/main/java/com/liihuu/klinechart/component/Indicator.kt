package com.liihuu.klinechart.component

import android.graphics.Color
import com.liihuu.klinechart.internal.utils.Utils

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/29-18:21
 */
class Indicator {
    /**
     * 指标类型
     */
    class Type {
        companion object {
            /**
             * 没有设置任何指标
             */
            const val NO = "no"

            /**
             * 均线
             */
            const val MA = "ma"

            /**
             * 成交量
             */
            const val VOL = "vol"

            /**
             * 指数平滑异同平均线（MACD指标）
             */
            const val MACD = "macd"

            /**
             * 布林指标
             */
            const val BOLL = "boll"

            /**
             * 随机指标(KDJ)
             */
            const val KDJ = "kdj"

            /**
             * 随机指标(KD)，同KDJ，只输出KD
             */
            const val KD = "kd"

            /**
             * 强弱指标
             */
            const val RSI = "rsi"

            /**
             * 乖离率（BIAS）是测量股价偏离均线大小程度的指标
             */
            const val BIAS = "bias"

            /**
             * 情绪指标（BRAR）也称为人气意愿指标
             */
            const val BRAR = "brar"

            /**
             * 顺势指标
             */
            const val CCI = "cci"

            /**
             * 动向指标
             */
            const val DMI = "dmi"

            /**
             * 能量指标
             */
            const val CR = "cr"

            /**
             * 心理线（PSY）指标是研究投资者对股市涨跌产生心理波动的情绪指标
             */
            const val PSY = "psy"

            /**
             * 平行线差指标
             */
            const val DMA = "dma"

            /**
             * 三重指数平滑平均线（TRIX）属于长线指标
             */
            const val TRIX = "trix"

            /**
             * 平衡交易量指标
             */
            const val OBV = "obv"

            /**
             * 成交量变异率
             */
            const val VR = "vr"

            /**
             * 威廉超买超卖指标
             */
            const val WR = "wr"

            /**
             * 动量指标
             */
            const val MTM = "mtm"

            /**
             * 简易波动指标
             */
            const val EMV = "emv"

            /**
             * 停损转向操作点指标
             */
            const val SAR = "sar"
        }
    }

    /**
     * 线的尺寸
     */
    var lineSize = Utils.convertDpToPixel(1f)

    /**
     * 指标涨颜色
     */
    var increasingColor = Color.parseColor("#5DB300")

    /**
     * 指标跌颜色
     */
    var decreasingColor = Color.parseColor("#FF4A4A")

    /**
     * 指标线颜色集合
     */
    var lineColors = intArrayOf(
        Color.parseColor("#898989"),
        Color.parseColor("#F5A623"),
        Color.parseColor("#F601FF"),
        Color.parseColor("#1587DD"),
        Color.parseColor("#50A300")
    )
}