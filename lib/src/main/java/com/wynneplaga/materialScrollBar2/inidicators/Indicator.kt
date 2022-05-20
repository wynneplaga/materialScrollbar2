package com.wynneplaga.materialScrollBar2.inidicators

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.wynneplaga.materialScrollBar2.MaterialScrollBar
import com.wynneplaga.materialScrollBar2.R
import com.wynneplaga.materialScrollBar2.dp

sealed class Indicator(
    context: Context,
    @ColorInt backgroundColor: Int,
    @ColorInt private val textColor: Int
): RelativeLayout(context) {

    protected abstract val textSize: Float
    protected abstract val indicatorHeight: Int
    protected abstract val indicatorWidth: Int

    internal var touchDownY: Float = 0f
    private val textView: TextView = TextView(context)

    init {
        val background = ContextCompat.getDrawable(
            context,
            R.drawable.indicator
        ) as GradientDrawable
        background.setColor(backgroundColor)
        ViewCompat.setBackground(
            this,
            background
        )
    }

    override fun onAttachedToWindow() {
        removeAllViews()

        layoutParams = LayoutParams(indicatorWidth, indicatorHeight).apply {
            addRule(ALIGN_PARENT_END)
            marginEnd = (parent as MaterialScrollBar).trackWidth + 10.dp(context)
        }

        textView.apply {
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, this@Indicator.textSize)
        }
        val lp = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            addRule(CENTER_IN_PARENT)
        }
        addView(textView, lp)

        super.onAttachedToWindow()
    }

    fun setScrollDepth(scrollDepth: Float) {
        val dy = scrollDepth - 75.dp(context) + touchDownY - indicatorHeight / 2

        y = dy.coerceAtLeast(5.dp(context).toFloat())
    }

    internal var currentPosition: Int = 0
        set(value) {
            if (field == value) return

            field = value
            textView.text = textForElement(currentPosition)

            if (indicatorWidth == -1) {
                textView.measure(0, 0)
                updateLayoutParams {
                    width = textView.measuredWidth + 50.dp(context)
                }
            }
        }

    abstract fun textForElement(index: Int): String

}
