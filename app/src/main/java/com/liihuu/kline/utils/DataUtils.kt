package com.liihuu.kline.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.liihuu.kline.model.UiInfoModel
import com.liihuu.klinechart.model.KLineModel
import kotlin.math.roundToInt

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-03-10:53
 */
object DataUtils {
    /**
     * 生成k线数据
     * @param dataCount Int
     * @return MutableList<KLineModel>
     */
    fun generatedKLineDataList(dataCount: Int = 200, basePrice: Double = (Math.random() * 2 + 1) * 6000, isAppend: Boolean = true): MutableList<KLineModel> {
        val dataList: MutableList<KLineModel> = ArrayList()
        var timestamp = System.currentTimeMillis() - 12 * 24 * 3600
        var baseValue = basePrice
        val prices = DoubleArray(4)
        for (i in 0 until dataCount) {
            for (j in 0..3) {
                prices[j] = (Math.random() - 0.5) * 100 + baseValue
            }
            prices.sort()
            val volume = Math.random() * 100 + 1

            timestamp += 60 * 1000
            var openPriceIndex = (Math.random() * 2).roundToInt()
            val closePriceIndex = (Math.random() * 3).roundToInt()
            if (openPriceIndex == closePriceIndex) {
                ++openPriceIndex
            }
            val openPrice = prices[openPriceIndex]
            val closePrice = prices[closePriceIndex]
            if (isAppend) {
                val kLineModel = KLineModel(
                    openPrice, prices[0], prices[3],
                    closePrice, volume, timestamp,
                    closePrice * volume
                )
                dataList.add(kLineModel)
                baseValue = closePrice
            } else {
                val kLineModel = KLineModel(
                    openPrice, prices[0], prices[3],
                    closePrice, volume, timestamp,
                    baseValue * volume
                )
                dataList.add(0, kLineModel)
                baseValue = openPrice
            }
        }
        return dataList
    }

    /**
     * 生成activity数据
     * @param context Context
     */
    fun generatedUiInfoList(context: Context): MutableList<UiInfoModel> {
        val uiInfoList = mutableListOf<UiInfoModel>()
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        if (packageInfo.activities != null) {
            for (activityInfo in packageInfo.activities) {
                val c = Class.forName(activityInfo.name)
                if (c.simpleName != "MainActivity" && Activity::class.java.isAssignableFrom(c)) {
                    val uiName = context.resources.getString(activityInfo.labelRes)
                    uiInfoList.add(UiInfoModel(uiName, c))
                }
            }
        }
        return uiInfoList
    }
}