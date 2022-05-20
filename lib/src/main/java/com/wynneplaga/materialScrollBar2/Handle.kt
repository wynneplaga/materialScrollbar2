package com.wynneplaga.materialScrollBar2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt

@SuppressLint("ViewConstructor")
class Handle(
    context: Context,
    @ColorInt handleColor: Int
): View(context) {

    private val arcWidth: Float = 4.dp(context).toFloat()
    private val holdWidth: Float = 14.dp(context).toFloat()
    private var handleArc = RectF()
    private var handleHold = RectF()
    internal var expanded: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var paint = Paint().apply {
        flags += Paint.ANTI_ALIAS_FLAG
        color = handleColor
    }

    @ColorInt
    var handleColor: Int = handleColor
        set(value) {
            field = value
            paint.color = value
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!changed) return

        if (context.isRightToLeft()) {
            handleArc.set(holdWidth, 0f, holdWidth + arcWidth * 2 + 1, height.toFloat())
            handleHold.set(0f, 0f, holdWidth, height.toFloat())
        } else {
            handleArc.set(0f, 0f, arcWidth * 2 + 1, height.toFloat())
            handleHold.set(arcWidth, 0f, arcWidth + holdWidth, height.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(handleHold, paint)

        if (!expanded)
            canvas.drawArc(handleArc, if (context.isRightToLeft()) 270f else 90f, 180f, false, paint)
    }

}