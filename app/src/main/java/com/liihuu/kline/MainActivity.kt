package com.liihuu.kline

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liihuu.kline.model.UiInfoModel
import com.liihuu.kline.utils.DataUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-23-20:14
 */
class MainActivity : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarHomeAsUpEnabled(false)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view.layoutManager = layoutManager
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = UiInfoAdapter()
    }

    override fun generatedLayoutId(): Int = R.layout.activity_main

    /**
     * ui列表Adapter
     * @property uiInfoList MutableList<UiInfoModel>
     */
    inner class UiInfoAdapter : RecyclerView.Adapter<UiInfoViewHolder>() {
        private var uiInfoList = mutableListOf<UiInfoModel>()
        init {
            uiInfoList = DataUtils.generatedUiInfoList(this@MainActivity)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UiInfoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_main, parent, false)
            return UiInfoViewHolder(itemView)
        }

        override fun getItemCount(): Int = uiInfoList.size

        override fun onBindViewHolder(holder: UiInfoViewHolder, position: Int) {
            val uiInfo = uiInfoList[position]
            (holder.itemView as? TextView)?.apply {
                text = uiInfo.name

                setOnClickListener {
                    val intent = Intent(this@MainActivity, uiInfo.c)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * ui列表ViewHolder
     * @constructor
     */
    inner class UiInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}