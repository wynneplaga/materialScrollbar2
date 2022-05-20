package com.wynneplaga.materialScrollBar2.inidicators

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.wynneplaga.materialScrollBar2.dp
import com.wynneplaga.materialScrollBar2.getAccentColor

@SuppressLint("ViewConstructor")
class CustomIndicator(
    context: Context,
    private val adapter: ICustomAdapter,
    @ColorInt backgroundColor: Int = context.getAccentColor(),
    @ColorInt textColor: Int = Color.WHITE
): Indicator(context, backgroundColor, textColor) {

    interface ICustomAdapter {
        /**
         * @param element of the adapter that should be titled.
         * @return Any string.
         */
        fun getCustomStringForElement(element: Int): String
    }

    override val textSize = 65f
    override val indicatorHeight = 75.dp(context)
    override val indicatorWidth: Int = -1

    override fun textForElement(index: Int) = adapter.getCustomStringForElement(index)

}