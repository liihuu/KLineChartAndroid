package com.liihuu.kline

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.liihuu.kline.utils.DataUtils
import com.liihuu.klinechart.KLineChartView
import kotlinx.android.synthetic.main.activity_segment_load_data_with_anim.*
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-24-19:09
 */
class SegmentLoadDataWithAnimActivity : BasicKLineActivity() {
    private val h = Handler {
        when (it.what) {
            SegmentLoadDataActivity.MESSAGE_LOAD_MORE -> {
                loading_view.stop()
                loading_view.visibility = View.GONE
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
                loading_view.visibility = View.VISIBLE
                loading_view.start()
                h.sendEmptyMessageDelayed(
                    SegmentLoadDataActivity.MESSAGE_LOAD_MORE,
                    SegmentLoadDataActivity.MESSAGE_DELAY_TIME
                )
            }
        }
    }

    override fun generatedLayoutId(): Int = R.layout.activity_segment_load_data_with_anim

    override fun generatedKLineDataCount(): Int = 200
}