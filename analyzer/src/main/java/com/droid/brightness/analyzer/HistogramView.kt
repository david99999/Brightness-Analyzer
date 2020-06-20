package com.droid.brightness.analyzer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Custom view that displays a Histogram for a 256 IntArray with values like
 *
 * [0]  -> 0
 * [1]  -> 0
 * [2]  -> 0
 * ...
 * [255]-> 2000
 */
class HistogramView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var barsPaint: Paint? = null

    init {
        barsPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }
    }

    var barWidth = 0f
    var barsPath: Path? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        barWidth = measuredWidth / 256f
    }

    fun setData(data: IntArray) {
        if (barsPath == null) {
            barsPath = Path()
        }
        val max = data.max()!! * 1f
        barsPath?.apply {
            reset()
            moveTo(0f, height.toFloat())
            for (index in data.indices) {
                lineTo(index * barWidth, height - data[index] / max * height) // left bar side
                lineTo((index + 1) * barWidth, height - data[index] / max * height) // top bar side
                lineTo((index + 1) * barWidth, height.toFloat()) // right bar side
            }
            close()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (barsPath != null && barsPaint != null) canvas.drawPath(barsPath!!, barsPaint!!)
    }
}