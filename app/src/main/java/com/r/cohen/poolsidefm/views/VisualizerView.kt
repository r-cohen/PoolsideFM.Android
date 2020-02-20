package com.r.cohen.poolsidefm.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.databinding.BindingAdapter


class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0): View(context, attrs, defStyleAttr)
{
    private var mBytes: ByteArray? = null
    private var mPoints: FloatArray? = null
    private val mRect: Rect = Rect()
    private val mForePaint: Paint = Paint()

    init {
        mBytes = null
        mForePaint.strokeWidth = dpToPx(2).toFloat()
        mForePaint.isAntiAlias = true
        mForePaint.color = Color.rgb(255, 255, 255)
    }

    fun updateVisualizer(bytes: ByteArray?) {
        mBytes = bytes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBytes == null) {
            return
        }
        if (mPoints == null || mPoints!!.size < mBytes!!.size * 4) {
            mPoints = FloatArray(mBytes!!.size * 4)
        }
        mRect.set(0, 0, width, height)
        for (i in 0 until mBytes!!.size - 1) {
            mPoints!![i * 4] = (mRect.width() * i / (mBytes!!.size - 1)).toFloat()
            mPoints!![i * 4 + 1] = ((mRect.height() / 2
                    + (mBytes!![i] + 128).toByte() * (mRect.height() / 2) / 128).toFloat())
            mPoints!![i * 4 + 2] = (mRect.width() * (i + 1) / (mBytes!!.size - 1)).toFloat()
            mPoints!![i * 4 + 3] = ((mRect.height() / 2
                    + (mBytes!![i + 1] + 128).toByte() * (mRect.height() / 2) / 128).toFloat())
        }
        mPoints?.let {
            canvas.drawLines(it, mForePaint)
        }
    }

    private fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()
}

@BindingAdapter("bytes")
fun VisualizerView.setBytes(bytes: ByteArray?) = updateVisualizer(bytes)