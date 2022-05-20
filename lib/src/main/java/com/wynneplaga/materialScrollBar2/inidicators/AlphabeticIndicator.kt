package com.wynneplaga.materialScrollBar2.inidicators

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.wynneplaga.materialScrollBar2.dp
import com.wynneplaga.materialScrollBar2.getAccentColor

@SuppressLint("ViewConstructor")
class AlphabeticIndicator(
    context: Context,
    private val adapter: INameableAdapter,
    @ColorInt backgroundColor: Int = context.getAccentColor(),
    @ColorInt textColor: Int = Color.WHITE
): Indicator(context, backgroundColor, textColor) {

    interface INameableAdapter {
        /**
         * @param element of the adapter that should be titled.
         * @return The character that the AlphabetIndicator should display for the corresponding element.
         */
        fun getCharacterForElement(element: Int): Char
    }

    override val textSize: Float = 130f
    override val indicatorHeight = 75.dp(context)
    override val indicatorWidth = 75.dp(context)

    override fun textForElement(index: Int) = adapter.getCharacterForElement(index).toString()


}