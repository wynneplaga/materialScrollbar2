package com.wynneplaga.materialScrollBar2.inidicators

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import androidx.annotation.ColorInt
import com.wynneplaga.materialScrollBar2.dp
import com.wynneplaga.materialScrollBar2.getAccentColor
import com.wynneplaga.materialScrollBar2.plusAssign
import java.text.DateFormatSymbols
import java.util.*

@SuppressLint("ViewConstructor")
class DateTimeIndicator(
    context: Context,
    private val adapter: IDateableAdapter,
    private val includeYear: Boolean,
    private val includeMonth: Boolean,
    private val includeDay: Boolean,
    private val includeTime: Boolean,
    @ColorInt backgroundColor: Int = context.getAccentColor(),
    @ColorInt textColor: Int = Color.WHITE
): Indicator(context, backgroundColor, textColor) {

    interface IDateableAdapter {
        /**
         * @param element of the adapter that should be titled.
         * @return The date that the DateIndicator should display for the corresponding element.
         */
        fun getDateForElement(element: Int): Date
    }

    override val textSize = 65f
    override val indicatorHeight = 75.dp(context)
    override val indicatorWidth: Int
        get() {
            var width = 155
            if (includeYear) {
                if (includeDay) {
                    width += 35
                }
                width += 140
            }
            if (includeMonth) {
                width += 108
            }
            if (includeDay) {
                width += 70
            }
            if (includeTime) {
                width += if (DateFormat.is24HourFormat(context)) {
                    175
                } else {
                    287
                }
            }
            return width
        }

    private val months = DateFormatSymbols().months

    override fun textForElement(index: Int): String {
        val date: Date = adapter.getDateForElement(index)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val text = StringBuilder()
        if (includeTime) {
            text += DateFormat.getTimeFormat(context).format(date)
        }
        if (includeMonth) {
            text += " " + months[calendar[Calendar.MONTH]].substring(0, 3)
        }
        if (includeDay) {
            val day = calendar[Calendar.DAY_OF_MONTH]
            text += if (day.toString().length == 1) {
                " 0$day"
            } else {
                " $day"
            }
        }
        if (includeYear) {
            if (includeDay) {
                text += ","
            }
            text += " " + calendar[Calendar.YEAR]
        }
        return text.trim().toString()
    }

}