package com.lalifa.yyf.weight

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lalifa.extension.dp
import com.lalifa.yyf.R

class CircleProgressView : View {
    private var arcWith = 4.dp
    private var arcColor = Color.WHITE
    private var progressColor = Color.RED
    private lateinit var paint: Paint
    private val rectF by lazy { RectF() }
    private val rect by lazy { Rect() }
    private var max = 100
    private var progress: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)
        arcColor = typedArray.getColor(R.styleable.CircleProgressView_cpv_stroke_color, Color.WHITE)
        progressColor =
            typedArray.getColor(R.styleable.CircleProgressView_cpv_progress_color, Color.RED)
        arcWith = typedArray.getInt(R.styleable.CircleProgressView_cpv_stroke_with, 4.dp)
        progress = typedArray.getInt(R.styleable.CircleProgressView_cpv_progress, 0)
        paint = Paint()
        paint.isAntiAlias = true
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (progress <= 0) {
            paint.apply {
                style = Paint.Style.STROKE
                color = arcColor
                strokeWidth = arcWith.toFloat()
            }
            val bigRadio = measuredWidth.toFloat() / 2
            canvas?.drawCircle(bigRadio, bigRadio, bigRadio - arcWith, paint)
        } else {
            paint.apply {
                style = Paint.Style.STROKE
                color = arcColor
                strokeWidth = arcWith.toFloat()
                //透明度30%
                alpha = 0x4D
            }
            val bigRadio = measuredWidth.toFloat() / 2
            canvas?.drawCircle(bigRadio, bigRadio, bigRadio - arcWith, paint)
            paint.apply {
                color = progressColor
                alpha = 0x255
            }
            rectF.set(
                arcWith.toFloat(), arcWith.toFloat(),
                measuredWidth.toFloat() - arcWith, measuredWidth.toFloat() - arcWith
            )
            canvas?.drawArc(rectF, 270f, progress * 360f / max, false, paint)

            paint.apply {
                style = Paint.Style.FILL
                strokeWidth = 0f
            }
            canvas?.drawCircle(bigRadio, bigRadio, bigRadio - arcWith - 10.dp, paint)
        }
    }
}