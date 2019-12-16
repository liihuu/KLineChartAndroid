package com.liihuu.kline

import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.liihuu.klinechart.component.Indicator
import kotlinx.android.synthetic.main.kline_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2018/12/28-20:48
 */
class StyleSettingActivity : BasicKLineActivity(){
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_kchart_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.grid_line -> {
                k_line_chart.grid.displayGridLine = !k_line_chart.grid.displayGridLine
                k_line_chart.invalidate()
            }

            R.id.show_hide_vol_index -> {
                k_line_chart.setShowVolIndicatorChart(!k_line_chart.isDisplayVolChart())
                k_line_chart.invalidate()
            }

            R.id.show_hide_index -> {
                val indexType = if (k_line_chart.isDisplayIndicatorChart()) Indicator.Type.NO else Indicator.Type.MACD
                k_line_chart.setSubIndicatorType(indexType)
                k_line_chart.invalidate()
            }

            R.id.candle_style -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.candle_style))
                AlertDialog.Builder(this)
                    .setTitle(R.string.candle_style)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.candle.candleStyle = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.main_chart_type -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.main_chart_style))
                AlertDialog.Builder(this)
                    .setTitle(R.string.main_chart_type)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.candle.chartStyle = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.mark_highest_price -> {
                k_line_chart.candle.displayHighestPriceMark = !k_line_chart.candle.displayHighestPriceMark
                k_line_chart.invalidate()
            }

            R.id.mark_lowest_price -> {
                k_line_chart.candle.displayLowestPriceMark = !k_line_chart.candle.displayLowestPriceMark
                k_line_chart.invalidate()
            }

            R.id.mark_last_price -> {
                k_line_chart.candle.displayLastPriceMark = !k_line_chart.candle.displayLastPriceMark
                k_line_chart.invalidate()
            }

            R.id.mark_last_price_style -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.line_style))
                AlertDialog.Builder(this)
                    .setTitle(R.string.mark_last_price_style)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.candle.lastPriceMarkLineStyle = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.cross_line_style -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.line_style))
                AlertDialog.Builder(this)
                    .setTitle(R.string.cross_line_style)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.tooltip.crossLineStyle = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.index_tooltip_text_display_rule -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.index_tooltip_display_rule))
                AlertDialog.Builder(this)
                    .setTitle(R.string.index_tooltip_text_display_rule)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.tooltip.indicatorDisplayRule = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.show_hide_x_axis_line -> {
                k_line_chart.xAxis.displayAxisLine = !k_line_chart.xAxis.displayAxisLine
                k_line_chart.invalidate()
            }

            R.id.show_hide_x_axis_text -> {
                k_line_chart.xAxis.displayTickText = !k_line_chart.xAxis.displayTickText
                k_line_chart.invalidate()
            }

            R.id.show_hide_x_axis_tick_line -> {
                k_line_chart.xAxis.displayTickLine = !k_line_chart.xAxis.displayTickLine
                k_line_chart.invalidate()
            }

            R.id.show_hide_x_axis_separator_line -> {
                k_line_chart.xAxis.displaySeparatorLine = !k_line_chart.xAxis.displaySeparatorLine
                k_line_chart.invalidate()
            }

            R.id.x_axis_separator_line_style -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.line_style))
                AlertDialog.Builder(this)
                    .setTitle(R.string.x_axis_separator_line_style)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.xAxis.separatorLineStyle = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.y_axis_position -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.y_axis_postion))
                AlertDialog.Builder(this)
                    .setTitle(R.string.y_axis_position)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.yAxis.yAxisPosition = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.show_hide_y_axis_line -> {
                k_line_chart.yAxis.displayAxisLine = !k_line_chart.yAxis.displayAxisLine
                k_line_chart.invalidate()
            }

            R.id.show_hide_y_axis_text -> {
                k_line_chart.yAxis.displayTickText = !k_line_chart.yAxis.displayTickText
                k_line_chart.invalidate()
            }

            R.id.y_axis_text_position -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.y_axis_text_position))
                AlertDialog.Builder(this)
                    .setTitle(R.string.y_axis_text_position)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.yAxis.yAxisTextPosition = which
                        k_line_chart.calcOffsets()
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }

            R.id.show_hide_y_axis_tick_line -> {
                k_line_chart.yAxis.displayTickLine = !k_line_chart.yAxis.displayTickLine
                k_line_chart.invalidate()
            }

            R.id.show_hide_y_axis_separator_line -> {
                k_line_chart.yAxis.displaySeparatorLine = !k_line_chart.yAxis.displaySeparatorLine
                k_line_chart.invalidate()
            }

            R.id.y_axis_separator_line_style -> {
                val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.line_style))
                AlertDialog.Builder(this)
                    .setTitle(R.string.y_axis_separator_line_style)
                    .setAdapter(arrayAdapter) {
                            dialog, which ->
                        k_line_chart.yAxis.separatorLineStyle = which
                        k_line_chart.invalidate()
                        dialog.dismiss()
                    }.create().show()
            }
        }
        return true
    }
}
