package com.ct.mycameralibray

import kotlin.jvm.Synchronized
import android.graphics.drawable.Drawable
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar

class VerticalSeekBar : AppCompatSeekBar {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(c: Canvas) {
        c.rotate(-90f)
        c.translate(-height.toFloat(), 0f)
        drawThumb(c) //redrawing thumb
        super.onDraw(c)
    }

    private fun drawThumb(canvas: Canvas) {
        val thumb: Drawable? = thumb
        if (thumb != null) {
            val thumbBounds = thumb.bounds
            canvas.save()
            canvas.rotate(90f, thumbBounds.exactCenterX(), thumbBounds.exactCenterY())
            thumb.draw(canvas)
            canvas.restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                progress = max - (max * event.y / height).toInt()
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_CANCEL -> {}
        }
        return true
    }

    override fun setThumb(thumb: Drawable) {
        super.setThumb(thumb)
        thumb.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    override fun setProgressDrawable(d: Drawable) {
        super.setProgressDrawable(d)
        d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }
}