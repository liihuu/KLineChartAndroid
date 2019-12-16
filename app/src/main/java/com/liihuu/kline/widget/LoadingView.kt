package com.liihuu.kline.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.liihuu.kline.utils.DimensionUtils

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-24-19:55
 */
class LoadingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr), Animatable {
    private val radius = DimensionUtils.dpToPx(context, 20f)

    private var isRunning = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var interpolatedTime = 0f

    private val anim: Animation = object: Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            this@LoadingView.interpolatedTime = interpolatedTime
            invalidate()
        }
    }

    init {
        paint.style = Paint.Style.FILL
        this.anim.apply {
            duration = 500L
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.parseColor("#10000000"))
        paint.color = Color.parseColor("#5164e6")
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, radius * interpolatedTime, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun isRunning(): Boolean = this.isRunning

    override fun start() {
        this.isRunning = true
        startAnimation(anim)
    }

    override fun stop() {
        this.isRunning = false
        clearAnimation()
    }

    override fun onDetachedFromWindow() {
        clearAnimation()
        super.onDetachedFromWindow()
    }
}