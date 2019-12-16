package com.liihuu.klinechart.internal

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Build
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AnimationUtils
import com.liihuu.klinechart.KLineChartView
import com.liihuu.klinechart.internal.utils.Utils
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019/4/19-11:41
 */
internal class TouchEvent(
    private val chart: KLineChartView,
    private val dataProvider: DataProvider,
    private val viewPortHandler: ViewPortHandler
) : View.OnTouchListener {
    private companion object {
        /**
         * 无
         */
        const val TOUCH_NO = 0

        /**
         * 拖拽
         */
        const val TOUCH_DRAG = 1

        /**
         * 缩放
         */
        const val TOUCH_ZOOM = 2

        /**
         *
         */
        const val TOUCH_POST_ZOOM = 3

        /**
         * 十字光标
         */
        const val TOUCH_CROSS = 4

        /**
         * 十字光标取消
         */
        const val TOUCH_CROSS_CANCEL = 5

        /**
         * 缩放响应的最小距离
         */
        const val ZOOM_MIN_DIST = 10f

        /**
         * 十字光标事件的最小响应半径
         */
        const val CROSS_EVENT_MIN_RADIUS = 30f
    }

    /**
     * 触摸类型
     */
    private var touchMode = TOUCH_NO

    /**
     * 触摸起点
     */
    private var touchStartPoint = PointF()

    /**
     * 触摸移动点
     */
    private var touchMovePoint = PointF()

    /**
     * 触摸十字光标点
     */
    private var touchCrossPoint = PointF()

    /**
     * 两个触摸点之间的直线距离
     */
    private var savedDist = 1f

    /**
     * 两个触摸点之间水平直线距离
     */
    private var savedXDist = 1f

    /**
     * 缩放响应的最小距离
     */
    private val minScalePointerDistance = Utils.convertDpToPixel(3.5f)

    /**
     * 拖拽尺寸
     */
    private val dragTriggerDist = Utils.convertDpToPixel(3f)

    /**
     * 触摸时柱状图数量
     */
    private var touchRange = 120

    /**
     * 触摸开始时数据可见最小位置
     */
    private var touchStartDataVisibleMinPos = 0

    /**
     * 快速手势事件检测者
     */
    private var velocityTracker: VelocityTracker? = null

    /**
     * 记录减速度运行过程中的位移值
     */
    private var decelerationVelocityX = 0f

    /**
     * 减速度运行过程中的当前坐标x位置
     */
    private var decelerationCurrentX = 0f

    /**
     *  减速度运行上一次时间戳
     */
    private var decelerationLastTime = 0L

    private val runnable = Runnable {
        if (this.touchMode == TOUCH_NO || this.touchMode == TOUCH_CROSS_CANCEL) {
            this.touchMode = TOUCH_CROSS
            this.touchCrossPoint.set(this.touchStartPoint.x, this.touchStartPoint.y)
            this.dataProvider.calcCurrentDataIndex(this.touchCrossPoint.x)
            this.dataProvider.crossPoint.y = this.touchCrossPoint.y
            this.chart.invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (this.dataProvider.dataList.size == 0) {
            return false
        }
        if (this.chart.decelerationEnable) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain()
            }
            velocityTracker?.addMovement(event)
        }
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                this.touchStartPoint.set(event.x, event.y)
                this.touchMovePoint.set(event.rawX, event.rawY)
                if (!checkEventAvailability()) {
                    return false
                }
                this.decelerationVelocityX = 0f
                if (this.touchMode == TOUCH_CROSS) {
                    val crossRadius = distance(event.x, this.touchCrossPoint.x, event.y, this.touchCrossPoint.y)
                    if (crossRadius < CROSS_EVENT_MIN_RADIUS) {
                        return performCross(event)
                    } else {
                        this.touchMode = TOUCH_CROSS_CANCEL
                        this.dataProvider.crossPoint.y = -1f
                        this.chart.invalidate()
                    }
                } else {
                    this.touchMode = TOUCH_NO
                }

                this.chart.parent?.requestDisallowInterceptTouchEvent(true)
                this.chart.removeCallbacks(this.runnable)
                this.chart.postDelayed(this.runnable, 200)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!checkEventAvailability()) {
                    return false
                }
                if (event.pointerCount >= 2) {
                    if (this.touchMode != TOUCH_CROSS) {
                        this.chart.parent?.requestDisallowInterceptTouchEvent(true)
                        this.savedDist = spacing(event)
                        this.savedXDist = getXDist(event)
                        if (this.savedDist > ZOOM_MIN_DIST) {
                            this.touchMode = TOUCH_ZOOM
                        }
                        this.touchRange = this.dataProvider.visibleDataCount
                        this.touchStartDataVisibleMinPos = this.dataProvider.visibleDataMinPos
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!checkEventAvailability()) {
                    return false
                }
                // 子控件相对于父布局若达到滚动条件，则让父布局拦截触摸事件
                if (abs(event.rawY - this.touchMovePoint.y) > 50 &&
                    abs(event.rawX - this.touchMovePoint.x) < 150 &&
                    (this.touchMode == TOUCH_NO || this.touchMode == TOUCH_CROSS_CANCEL)) {
                    this.chart.parent?.requestDisallowInterceptTouchEvent(false)
                }

                when (this.touchMode) {
                    TOUCH_ZOOM -> {
                        this.chart.parent?.requestDisallowInterceptTouchEvent(true)
                        return performZoom(event)
                    }
                    TOUCH_DRAG -> {
                        this.chart.parent?.requestDisallowInterceptTouchEvent(true)
                        return performDrag(event)
                    }
                    TOUCH_CROSS -> {
                        this.chart.parent?.requestDisallowInterceptTouchEvent(true)
                        return performCross(event)
                    }
                    TOUCH_CROSS_CANCEL -> {
                        this.chart.removeCallbacks(this.runnable)
                    }
                    TOUCH_NO -> {
                        val distance = abs(distance(event.x, this.touchStartPoint.x, event.y, this.touchStartPoint.y))
                        if (distance > this.dragTriggerDist) {
                            val distanceX = abs(event.x - this.touchStartPoint.x)
                            val distanceY = abs(event.y - this.touchStartPoint.y)
                            if (distanceY <= distanceX) {
                                this.dataProvider.crossPoint.y = -1f
                                this.touchMode = TOUCH_DRAG
                                this.chart.invalidate()
                            }
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (!checkEventAvailability()) {
                    return false
                }
                this.chart.removeCallbacks(this.runnable)
                if (abs(this.touchStartPoint.x - event.x) < 30 && abs(this.touchStartPoint.y - event.y) < 30) {
                    if (this.touchMode == TOUCH_NO) {
                        this.chart.parent?.requestDisallowInterceptTouchEvent(true)
                        this.touchMode = TOUCH_CROSS
                        return performCross(event)
                    }
                }

                if (this.touchMode == TOUCH_DRAG) {
                    velocityTracker?.let {
                        val pointerId = event.getPointerId(0)
                        it.computeCurrentVelocity(1000, 8000f)
                        val velocityX = it.getXVelocity(pointerId)
                        if (abs(velocityX) > 50) {

                            this.decelerationLastTime = AnimationUtils.currentAnimationTimeMillis()

                            this.decelerationCurrentX = event.x

                            this.decelerationVelocityX = velocityX

                            postInvalidateOnAnimation()
                        }

                    }
                }
                recycleVelocityTracker()

                if (this.touchMode != TOUCH_CROSS) {
                    // 拿起
                    this.touchMode = TOUCH_NO
                    this.dataProvider.crossPoint.y = -1f
                    this.chart.parent?.requestDisallowInterceptTouchEvent(false)

                    this.chart.invalidate()
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (!checkEventAvailability()) {
                    return false
                }
                velocityTrackerPointerUpCleanUpIfNecessary(event)
                if (this.touchMode == TOUCH_CROSS) {
                    return performCross(event)
                } else {
                    this.touchMode = TOUCH_POST_ZOOM
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                recycleVelocityTracker()
            }
        }
        return true
    }

    /**
     * 处理拖拽视图事件。
     *
     * @param event
     * @return
     */
    private fun performDrag(event: MotionEvent): Boolean {
        val moveDist = event.x - this.touchMovePoint.x
        val isConsume = this.dataProvider.calcDrag(moveDist, this.touchMovePoint, event.x, this.chart.noMore, this.chart.loadMoreListener)
        if (isConsume) {
            this.chart.invalidate()
        }
        return isConsume
    }

    /**
     * 处理缩放
     * @param event MotionEvent
     */
    private fun performZoom(event: MotionEvent): Boolean {
        if (event.pointerCount >= 2) {
            val totalDist = spacing(event)
            if (totalDist > this.minScalePointerDistance)  {
                val xDist = getXDist(event)
                // x轴方向 scale
                val scaleX = xDist / this.savedXDist
                val isConsume = this.dataProvider.calcZoom(scaleX, this.touchRange, this.touchStartDataVisibleMinPos)
                if (isConsume) {
                    this.chart.invalidate()
                }
                return isConsume
            }
        }
        return true
    }

    /**
     * 处理移动光标
     * @param event MotionEvent
     */
    private fun performCross(event: MotionEvent): Boolean {
        this.touchCrossPoint.set(event.x, event.y)
        this.dataProvider.calcCurrentDataIndex(this.touchCrossPoint.x)
        this.dataProvider.crossPoint.y = this.touchCrossPoint.y
        this.chart.invalidate()
        return true
    }

    /**
     * 处理惯性滚动
     */
    fun computeScroll() {
        if (this.decelerationVelocityX == 0f) { return }

        val currentTime = AnimationUtils.currentAnimationTimeMillis()
        this.decelerationVelocityX *= 0.9f

        val timeInterval = (currentTime - this.decelerationLastTime) / 1000f
        val distanceX = this.decelerationVelocityX * timeInterval

        this.decelerationCurrentX += distanceX

        val event = MotionEvent.obtain(
            currentTime, currentTime, MotionEvent.ACTION_MOVE,
            this.decelerationCurrentX, 0f, 0
        )

        performDrag(event)

        event.recycle()

        this.decelerationLastTime = currentTime

        if (abs(this.decelerationVelocityX) >= 1) {
            postInvalidateOnAnimation()
        } else {
            this.decelerationVelocityX = 0f
        }
    }

    /**
     * 释放速率监听
     */
    private fun recycleVelocityTracker() {
        this.velocityTracker?.apply {
            recycle()
            velocityTracker = null
        }
    }

    /**
     * 判断是否要清除快速滑动
     * @param ev MotionEvent
     */
    private fun velocityTrackerPointerUpCleanUpIfNecessary(ev: MotionEvent) {
        this.velocityTracker?.let {
            val upIndex = ev.actionIndex
            val id1 = ev.getPointerId(upIndex)
            it.computeCurrentVelocity(1000, 8000f)
            val x1 = it.getXVelocity(id1)
            val y1 = it.getYVelocity(id1)

            for (i in 0 until ev.pointerCount) {
                if (i != upIndex) {
                    val id2 = ev.getPointerId(i)
                    val x = x1 * it.getXVelocity(id2)
                    val y = y1 * it.getYVelocity(id2)

                    val dot = x + y
                    if (dot < 0) {
                        it.clear()
                        break
                    }
                }
            }
        }
    }

    private fun postInvalidateOnAnimation() {
        if (Build.VERSION.SDK_INT >= 16) {
            this.chart.postInvalidateOnAnimation()
        } else {
            this.chart.postInvalidateDelayed(10)
        }
    }

    /**
     * 检查事件有效性
     */
    private fun checkEventAvailability(): Boolean {
        return !(this.touchStartPoint.x < this.viewPortHandler.contentLeft() ||
                this.touchStartPoint.x > this.viewPortHandler.contentRight() ||
                this.touchStartPoint.y < this.viewPortHandler.contentTop() ||
                this.touchStartPoint.y > this.viewPortHandler.contentBottom())
    }

    /**
     * 计算移动距离
     *
     * @param event
     * @return
     */
    private fun spacing(event: MotionEvent): Float {
        if (event.pointerCount < 2) {
            return 0f
        }
        val x = abs(event.getX(event.getPointerId(0)) - event.getX(event.getPointerId(1))).toDouble()
        val y = abs(event.getY(event.getPointerId(0)) - event.getY(event.getPointerId(1))).toDouble()
        return sqrt(x * x + y * y).toFloat()
    }

    /**
     * 获取两点间x的距离
     * @param event MotionEvent
     * @return Float
     */
    private fun getXDist(event: MotionEvent): Float {
        return abs(event.getX(0) - event.getX(1))
    }

    /**
     * 两点之间的距离
     * @param eventX Float
     * @param startX Float
     * @param eventY Float
     * @param startY Float
     * @return Float
     */
    private fun distance(eventX: Float, startX: Float, eventY: Float, startY: Float): Float {
        val dx = eventX - startX
        val dy = eventY - startY
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}