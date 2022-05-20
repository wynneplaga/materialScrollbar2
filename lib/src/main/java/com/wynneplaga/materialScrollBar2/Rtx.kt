package com.wynneplaga.materialScrollBar2

import android.content.Context
import android.content.res.Resources
import android.util.LayoutDirection
import android.util.TypedValue
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

fun Int.dp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        context.resources.displayMetrics
    ).roundToInt()
}

fun Context.isRightToLeft(): Boolean {
    return resources.configuration.layoutDirection == LayoutDirection.RTL
}

fun Resources.Theme.resolveAttribute(resid: Int): TypedValue {
    val result = TypedValue()
    resolveAttribute(resid, result, true)
    return result
}

@ColorInt
fun Context.getAccentColor(): Int {
    return theme.resolveAttribute(androidx.appcompat.R.attr.colorAccent).data
}

operator fun StringBuilder.plusAssign(string: String) {
    append(string)
}
