package com.liihuu.kline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.app_bar_layout.*

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-23-20:36
 */
abstract class BasicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(generatedLayoutId())
        setSupportActionBar(toolbar)
        setToolbarHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * 生成布局id
     * @return Int
     */
    abstract fun generatedLayoutId(): Int

    /**
     * 设置返回按钮是否可用
     * @param enabled Boolean
     */
    fun setToolbarHomeAsUpEnabled(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
    }
}