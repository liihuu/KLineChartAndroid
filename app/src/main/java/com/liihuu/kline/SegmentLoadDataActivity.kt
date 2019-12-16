package com.liihuu.kline

import android.os.Bundle
import android.os.Handler
import com.liihuu.kline.utils.DataUtils
import com.liihuu.klinechart.KLineChartView
import kotlinx.android.synthetic.main.kline_layout.*


/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-24-18:51
 */
class SegmentLoadDataActivity : BasicKLineActivity() {
    companion object {
        const val MESSAGE_LOAD_MORE = 0x001
        const val MESSAGE_DELAY_TIME = 2 * 1000L
    }
    private val h = Handler {
        when (it.what) {
            MESSAGE_LOAD_MORE -> {
                val chartDataList = k_line_chart.getDataList()
                val dataList = DataUtils.generatedKLineDataList(200, chartDataList[0].openPrice, false)
                k_line_chart.loadComplete()
                k_line_chart.addData(dataList)
            }
        }
        return@Handler true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k_line_chart.loadMoreListener = object: KLineChartView.LoadMoreListener {
            override fun loadMore() {
                h.sendEmptyMessageDelayed(MESSAGE_LOAD_MORE, MESSAGE_DELAY_TIME)
            }
        }
    }

    override fun generatedKLineDataCount(): Int = 200

    override fun onDestroy() {
        h.removeMessages(MESSAGE_LOAD_MORE)
        super.onDestroy()
    }
}