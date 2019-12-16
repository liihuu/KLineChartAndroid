package com.liihuu.kline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liihuu.klinechart.component.Indicator
import kotlinx.android.synthetic.main.activity_indicator_setting.*
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-24-17:30
 */
class IndicatorSettingActivity : BasicKLineActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView(
            rv_main_indicator,
            listOf(
                Indicator.Type.NO, Indicator.Type.MA,
                Indicator.Type.BOLL, Indicator.Type.SAR
            ),
            true
        )
        initRecyclerView(
            rv_sub_indicator,
            listOf(
                Indicator.Type.NO, Indicator.Type.MACD,
                Indicator.Type.MA, Indicator.Type.VOL,
                Indicator.Type.BOLL, Indicator.Type.BIAS,
                Indicator.Type.RSI, Indicator.Type.KDJ,
                Indicator.Type.KD, Indicator.Type.BRAR,
                Indicator.Type.CR, Indicator.Type.CCI,
                Indicator.Type.DMI, Indicator.Type.DMA,
                Indicator.Type.SAR, Indicator.Type.MTM,
                Indicator.Type.TRIX, Indicator.Type.PSY,
                Indicator.Type.EMV, Indicator.Type.OBV,
                Indicator.Type.WR, Indicator.Type.VR
            )
        )
    }

    private fun initRecyclerView(recyclerView: RecyclerView, dataList: List<String>, isMainIndicator: Boolean = false) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        val indicatorAdapter = IndicatorAdapter(dataList)
        indicatorAdapter.onItemClick = {
            if (isMainIndicator) {
                k_line_chart.setMainIndicatorType(it)
            } else {
                k_line_chart.setSubIndicatorType(it)
            }

        }
        recyclerView.adapter = indicatorAdapter
    }

    override fun generatedLayoutId(): Int = R.layout.activity_indicator_setting

    /**
     * 指标adapter
     * @property indicatorDataList List<String>
     * @property onItemClick Function1<[@kotlin.ParameterName] String, Unit>?
     * @property selectedIndex Int
     * @constructor
     */
    inner class IndicatorAdapter(private val indicatorDataList: List<String>) : RecyclerView.Adapter<IndicatorViewHolder>() {
        var onItemClick: ((indicatorType: String) -> Unit)? = null
        private var selectedIndex = 1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_horizontal, parent, false)
            return IndicatorViewHolder(itemView)
        }

        override fun getItemCount(): Int = indicatorDataList.size

        override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
            val value = indicatorDataList[position]
            (holder.itemView as? TextView)?.apply {
                if (selectedIndex == position) {
                    setTextColor(resources.getColor(android.R.color.holo_blue_light))
                } else {
                    setTextColor(resources.getColor(R.color.colorAccent))
                }
                text = value
            }
            holder.itemView.setOnClickListener {
                onItemClick?.let {
                    selectedIndex = position
                    it(value)
                    notifyDataSetChanged()
                }
            }
        }

    }

    /**
     * 指标ViewHolder
     * @constructor
     */
    inner class IndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}