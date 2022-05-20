package com.wynneplaga.materialScrollBar2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.NestedScrollingParent
import androidx.core.view.doOnAttach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.wynneplaga.materialScrollBar2.inidicators.AlphabeticIndicator
import com.wynneplaga.materialScrollBar2.inidicators.CustomIndicator
import com.wynneplaga.materialScrollBar2.inidicators.DateTimeIndicator
import com.wynneplaga.materialScrollBar2.inidicators.Indicator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import viewScope

@SuppressLint("ClickableViewAccessibility")
class MaterialScrollBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
): RelativeLayout(context, attributeSet, defStyleAttr, defStyleRes), NestedScrollingParent {

    private var handleTrack: View
    internal lateinit var handleThumb: Handle

    override fun isNestedScrollingEnabled() = true

    override fun startNestedScroll(axes: Int): Boolean {
        return true
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return super.dispatchNestedScroll(
            dxUnconsumed,
            dyUnconsumed,
            0,
            0,
            offsetInWindow
        )
    }

    var indicator: Indicator? = null
        set(value) {
            if (field != null)
                throw IllegalStateException("May not specify an indicator if one is already set")

            field = value
            if (value == null)
                return

            field?.alpha = 0f
            addView(field)
        }

    internal val trackWidth = 14.dp(context)

    private val hideAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.ABSOLUTE, if (context.isRightToLeft()) -trackWidth.toFloat() / 2f else trackWidth.toFloat() / 2f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    ).apply {
        duration = 150
        fillAfter = true
        setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                viewScope.launch {
                    delay(animation.duration / 3)
                    handleThumb.expanded = false
                }
            }
            override fun onAnimationEnd(animation: Animation?) { }
            override fun onAnimationRepeat(animation: Animation?) { }
        })
    }

    private val showAnimation = TranslateAnimation(
        Animation.ABSOLUTE, if (context.isRightToLeft()) -trackWidth.toFloat() / 2f else trackWidth.toFloat() / 2f,
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    ).apply {
        duration = 150
        fillAfter = true
        setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                handleThumb.expanded = true
            }
            override fun onAnimationEnd(animation: Animation?) { }
            override fun onAnimationRepeat(animation: Animation?) { }
        })
    }

    internal lateinit var recyclerView: RecyclerView

    private val scrollingUtilities = ScrollingUtilities(this)

    private val scrollListener = ScrollListener()

    /**
     * Allows for programmatic instantiation
     */
    constructor(
        context: Context,
        recyclerView: RecyclerView,
        @ColorInt handleColor: Int = context.getAccentColor()
    ): this(context) {
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(scrollListener)
        handleThumb.handleColor = handleColor
    }

    init {
        var rvId = -1
        var indicatorType = -1
        var handleColor = context.getAccentColor()
        var textColor = Color.WHITE
        attributeSet?.let {
            context.obtainStyledAttributes(it, R.styleable.MaterialScrollBar).apply {
                rvId = getResourceId(R.styleable.MaterialScrollBar_msb_recyclerView, -1)
                indicatorType = getInt(R.styleable.MaterialScrollBar_msb_indicatorType, -1)
                handleColor = getColor(R.styleable.MaterialScrollBar_msb_handleColor, context.getAccentColor())
                textColor = getColor(R.styleable.MaterialScrollBar_msb_textColor, Color.WHITE)
            }.recycle()
        }

        doOnAttach {
            if (rvId != -1) {
                recyclerView = (parent as ViewGroup).findViewById(rvId)
                recyclerView.addOnScrollListener(scrollListener)
            }

            if (indicator == null) {
                indicator = when (indicatorType) {
                    0 -> AlphabeticIndicator(
                        context = context,
                        backgroundColor = handleColor,
                        textColor = textColor,
                        adapter = recyclerView.adapter as AlphabeticIndicator.INameableAdapter
                    )
                    1 -> DateTimeIndicator(context,
                        includeYear = true,
                        includeMonth = true,
                        includeDay = true,
                        includeTime = false,
                        backgroundColor = handleColor,
                        textColor = textColor,
                        adapter = recyclerView.adapter as DateTimeIndicator.IDateableAdapter
                    )
                    2 -> DateTimeIndicator(context,
                        includeYear = false,
                        includeMonth = false,
                        includeDay = false,
                        includeTime = true,
                        backgroundColor = handleColor,
                        textColor = textColor,
                        adapter = recyclerView.adapter as DateTimeIndicator.IDateableAdapter
                    )
                    3 -> DateTimeIndicator(context,
                        includeYear = true,
                        includeMonth = true,
                        includeDay = true,
                        includeTime = true,
                        backgroundColor = handleColor,
                        textColor = textColor,
                        adapter = recyclerView.adapter as DateTimeIndicator.IDateableAdapter
                    )
                    4 -> CustomIndicator(
                        context = context,
                        backgroundColor = handleColor,
                        textColor = textColor,
                        adapter = recyclerView.adapter as CustomIndicator.ICustomAdapter
                    )
                    else -> null
                }
            }
        }

        handleTrack = View(context).apply {
            val lp = LayoutParams(trackWidth, LayoutParams.MATCH_PARENT).apply {
                addRule(ALIGN_PARENT_END)
            }
            background = ContextCompat.getColor(context, android.R.color.darker_gray).toDrawable()
            alpha = 0.4f
            addView(this, lp)
        }

        handleThumb = Handle(context, handleColor).apply {
            val lp = LayoutParams(
                18.dp(context),
                72.dp(context)
            )
            lp.addRule(ALIGN_PARENT_END)

            setOnTouchListener(object: OnTouchListener {

                var dY: Float = 0f

                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            dY = view.y - event.rawY + 15.dp(context)
                            scrollListener.onScrollStateChanged(recyclerView, SCROLL_STATE_DRAGGING)

                            indicator?.touchDownY = event.y
                            indicator?.animate()?.alpha(1f)?.start()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val top = handleThumb.height / 2
                            val bottom: Int = recyclerView.height - 72.dp(context)
                            val boundedY = (event.rawY + dY).coerceIn(top.toFloat(), bottom.toFloat())

                            scrollingUtilities.scrollToPositionAtProgress((boundedY - top) / (bottom - top))
                        }
                        MotionEvent.ACTION_UP -> {
                            scrollListener.onScrollStateChanged(recyclerView, SCROLL_STATE_IDLE)
                            indicator?.animate()?.alpha(0f)?.start()
                        }
                        else -> return false
                    }
                    return true
                }
            })

            addView(this, lp)
        }

        //Hides the view
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.ABSOLUTE, if (context.isRightToLeft()) -trackWidth.toFloat() / 2f else trackWidth.toFloat() / 2f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        ).apply {
            duration = 0
            fillAfter = true
            startAnimation(this)
        }
    }

    private inner class ScrollListener: RecyclerView.OnScrollListener() {

        private var hideJob: Job? = null
        private var isIdle = true

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                SCROLL_STATE_IDLE -> {
                    hideJob = viewScope.launch {
                        delay(300)
                        startAnimation(hideAnimation)
                        isIdle = true
                    }
                }
                SCROLL_STATE_DRAGGING -> {
                    hideJob?.cancel()

                    if(isIdle)
                        startAnimation(showAnimation)

                    isIdle = false
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            scrollingUtilities.updateScrollOfHandleAndIndicator()
        }

    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    val isExpanded: Boolean
        get() = handleThumb.expanded

}
