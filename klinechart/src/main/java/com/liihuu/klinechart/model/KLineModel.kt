package com.liihuu.klinechart.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/28-17:46
 */
@Parcelize
data class KLineModel @JvmOverloads constructor(
    /**
     * 开盘价
     */
    var openPrice: Double = 0.0,

    /**
     * 最低价
     */
    var lowPrice: Double = 0.0,

    /**
     * 最高价
     */
    var highPrice: Double = 0.0,

    /**
     * 收盘价
     */
    var closePrice: Double = 0.0,

    /**
     * 成交量
     */
    var volume: Double = 0.0,

    /**
     * 时间戳
     */
    var timestamp: Long = System.currentTimeMillis(),

    /**
     * 成交额
     */
    var turnover: Double = 0.0,

    /**
     * 均价，用于分时图均线绘制
     */
    var averagePrice: Double = 0.0,

    /**
     * 成交量指标
     */
    var vol: VolModel? = null,

    /**
     * 乖离率（BIAS）是测量股价偏离均线大小程度的指标
     */
    var bias: BiasModel? = null,

    /**
     * 布林指标
     */
    var boll: BollModel? = null,

    /**
     * 情绪指标（BRAR）(人气意愿指标)
     */
    var brar: BrarModel? = null,

    /**
     * 顺势指标
     */
    var cci: CciModel? = null,

    /**
     * 能量指标
     */
    var cr: CrModel? = null,

    /**
     * 动向指标
     */
    var dmi: DmiModel? = null,

    /**
     * 平行线差指标
     */
    var dma: DmaModel? = null,

    /**
     * kdj
     */
    var kdj: KdjModel? = null,

    /**
     * 指数平滑异同平均线（MACD指标）
     */
    var macd: MacdModel? = null,

    /**
     * 收盘价均线
     */
    var ma: MaModel? = null,

    /**
     * OBV指标（统计成交量变动的趋势来推测股价趋势）
     */
    var obv: ObvModel? = null,

    /**
     * 心理线（PSY）指标是研究投资者对股市涨跌产生心理波动的情绪指标
     */
    var psy: PsyModel? = null,

    /**
     * 强弱指标
     */
    var rsi: RsiModel? = null,

    /**
     * 停损转向操作点指标
     */
    var sar: SarModel? = null,

    /**
     * 三重指数平滑平均线（TRIX）属于长线指标
     */
    var trix: TrixModel? = null,

    /**
     * 成交量变异率指标
     */
    var vr: VrModel? = null,

    /**
     * 威廉超买超卖指标
     */
    var wr: WrModel? = null,

    /**
     * 动量指标
     */
    var mtm: MtmModel? = null,

    /**
     * 简易波动指标
     */
    var emv: EmvModel? = null
): Parcelable {
    /**
     * 扩展数据
     */
    @IgnoredOnParcel
    var extensionData: Any? = null

    /**
     * 自定义指标数据
     */
    @IgnoredOnParcel
    var customIndicator: Any? = null

    override fun toString(): String {
        return "open: $openPrice, low: $lowPrice, high: $highPrice, close: $closePrice"
    }
}