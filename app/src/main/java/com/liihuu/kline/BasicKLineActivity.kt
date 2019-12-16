package com.liihuu.kline

import android.os.Bundle
import com.liihuu.kline.utils.DataUtils
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-23-23:23
 */
open class BasicKLineActivity : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataList = DataUtils.generatedKLineDataList(generatedKLineDataCount())
        k_line_chart.addData(dataList)
    }

    override fun generatedLayoutId(): Int = R.layout.activity_basic_kline

    /**
     * 获取需要生成k线数据的个数
     * @return Int
     */
    open fun generatedKLineDataCount(): Int = 2000
}