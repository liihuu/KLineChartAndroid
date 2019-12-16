package com.liihuu.klinechart.internal

import android.graphics.PointF
import com.liihuu.klinechart.KLineChartView
import com.liihuu.klinechart.model.KLineModel
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/5/7-15:12
 */
internal class DataProvider(private val viewPortHandler: ViewPortHandler) {
    companion object {
        const val DATA_SPACE_RATE = 0.22f
    }

    /**
     * 数据集合
     */
    var dataList = mutableListOf<KLineModel>()

    /**
     * 可见数据的最小索引
     */
    var visibleDataMinPos = 0

    /**
     * 可见数据的个数
     */
    var visibleDataCount = 120

    /**
     * 每条数据绘制所占的空间
     */
    var dataSpace = 0f

    /**
     * 可见的数据的最大个数
     */
    var maxVisibleDataCount = 180

    /**
     * 可见数据的最小个数
     */
    var minVisibleDataCount = 30

    /**
     * 光标中心点位置坐标
     */
    var crossPoint = PointF(0f, -1f)

    /**
     * 当前需要显示的数据的索引
     */
    var currentTipDataPos = 0

    /**
     * 是否正在加载
     */
    var isLoading = false

    /**
     * 添加数据
     * @param kLineModel KLineModel
     * @param pos Int
     */
    fun addData(kLineModel: KLineModel, pos: Int) {
        if (pos > -1) {
            if (pos >= this.dataList.size) {
                this.dataList.add(kLineModel)
            } else {
                this.dataList[pos] = kLineModel
            }
            if (this.visibleDataMinPos + this.visibleDataCount >= this.dataList.size - 1) {
                moveToLast()
            }
        }
    }

    /**
     * 添加数据
     * @param list MutableList<KLineModel>
     * @param pos Int
     */
    fun addData(list: MutableList<KLineModel>, pos: Int) {
        if (this.dataList.size == 0) {
            this.dataList.addAll(list)
            moveToLast()
        } else {
            dataList.addAll(pos, list)
            visibleDataMinPos += list.size
        }
    }

    /**
     * 移动到最后一条数据
     */
    private fun moveToLast() {
        this.visibleDataMinPos = if (this.dataList.size > this.visibleDataCount) {
            this.dataList.size - this.visibleDataCount
        } else {
            0
        }
        this.currentTipDataPos = this.dataList.size - 1
        if (this.currentTipDataPos < 0) {
            this.currentTipDataPos = 0
        }
    }

    /**
     * 获取柱状图之间的间隙
     */
    fun space() {
        this.dataSpace = this.viewPortHandler.contentWidth() / this.visibleDataCount
    }

    /**
     * 计算当前数据的索引
     * @param x Float
     */
    fun calcCurrentDataIndex(x: Float) {
        val range = ceil((x.toDouble() - this.viewPortHandler.contentLeft()) / this.dataSpace).toInt()
        this.currentTipDataPos = min(this.visibleDataMinPos + range - 1, this.dataList.size - 1)
        if (this.currentTipDataPos < 0) {
            this.currentTipDataPos = 0
        }
    }

    /**
     * 计算缩放
     * @param scaleX Float
     * @param touchRange Int
     * @param touchStartMinPos Int
     * @return Boolean
     */
    fun calcZoom(scaleX: Float, touchRange: Int, touchStartMinPos: Int): Boolean {
        // 是否缩小
        val isZoomingOut = scaleX < 1
        if (isZoomingOut) {

            if (this.visibleDataCount >= this.maxVisibleDataCount) {
                // 无法继续缩小
                return false
            }
        } else {
            if (this.visibleDataCount <= this.minVisibleDataCount) {
                // 无法继续放大
                return false
            }
        }

        // 计算缩放后的range大小
        this.visibleDataCount = (touchRange / scaleX).toInt()
        this.visibleDataCount = min(max(this.visibleDataCount, this.minVisibleDataCount), this.maxVisibleDataCount)
        val minPos = touchStartMinPos + touchRange - this.visibleDataCount
        when {
            minPos + this.visibleDataCount > this.dataList.size -> this.visibleDataMinPos = 0
            minPos < 0 -> this.visibleDataMinPos = 0
            else -> this.visibleDataMinPos = minPos
        }
        return true
    }

    /**
     * 计算拖拽
     * @param moveDist Float
     * @param touchMovePoint PointF
     * @param eventX Float
     * @return Boolean
     */
    fun calcDrag(
        moveDist: Float, touchMovePoint: PointF,
        eventX: Float, noMore: Boolean,
        loadMoreListener: KLineChartView.LoadMoreListener?
    ): Boolean {
        val dataSize = this.dataList.size
        when {
            moveDist < 0 - this.dataSpace / 2 -> {

                if (this.visibleDataMinPos + this.visibleDataCount == dataSize || dataSize < this.visibleDataCount) {
                    return false
                }

                touchMovePoint.x = eventX

                var moveRange = abs(moveDist / this.dataSpace).toInt()
                if (moveRange == 0) {
                    moveRange = 1
                }

                this.visibleDataMinPos += moveRange
                if (this.visibleDataMinPos > dataSize - this.visibleDataCount) {
                    this.visibleDataMinPos = dataSize - this.visibleDataCount
                }

                return true
            }

            moveDist > this.dataSpace / 2 -> {
                if (this.visibleDataMinPos == 0 || dataSize < this.visibleDataCount) {
                    return false
                }

                touchMovePoint.x = eventX

                var moveRange = abs(moveDist / this.dataSpace).toInt()
                if (moveRange == 0) {
                    moveRange = 1
                }

                this.visibleDataMinPos -= moveRange
                if (this.visibleDataMinPos < 0) {
                    this.visibleDataMinPos = 0
                }

                if (this.visibleDataMinPos == 0 &&
                    !noMore &&
                    !this.isLoading &&
                    loadMoreListener != null) {
                    this.isLoading = true
                    loadMoreListener.loadMore()
                }
                return true
            }
        }
        return false
    }
}