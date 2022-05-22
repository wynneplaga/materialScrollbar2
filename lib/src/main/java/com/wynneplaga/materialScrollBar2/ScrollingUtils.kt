/*
 *  Copyright © 2016-2018, Turing Technologies, an unincorporated organisation of Wynne Plaga
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.wynneplaga.materialScrollBar2

import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil
import kotlin.math.roundToInt

/*
 * Lots of complicated maths taken mostly from Google.
 */
internal class ScrollingUtilities(private val materialScrollBar: MaterialScrollBar) {

    private val adapter: RecyclerView.Adapter<*>
        get() = materialScrollBar.recyclerView.adapter ?: throw IllegalStateException("Adapter must be set for scroll")
    private val scrollPosState = ScrollPositionState()
    private var scrollDepthPx = 0

    private inner class ScrollPositionState {
        // The index of the first visible row
        var rowIndex = 0

        // The offset of the first visible row
        var rowTopOffset = 0

        // The height of a given row (they are currently all the same height)
        var rowHeight = 0
        var indicatorPosition = 0
    }

    fun updateScrollOfHandleAndIndicator() {
        val localAdapter = adapter
        updateScrollPos()
        scrollDepthPx = if (localAdapter is ICustomScroller) {
            localAdapter.getDepthForItem(
                materialScrollBar.recyclerView.getChildAdapterPosition(
                    materialScrollBar.recyclerView.getChildAt(0)
                )
            )
        } else {
            scrollPosState.rowHeight * scrollPosState.rowIndex
        }
        scrollDepthPx += materialScrollBar.recyclerView.paddingTop
        materialScrollBar.handleThumb.y = adjustedScrollDepthPx
        materialScrollBar.handleThumb.invalidate()
        materialScrollBar.indicator?.let {
            val element = if (materialScrollBar.recyclerView.layoutManager is GridLayoutManager) {
                scrollPosState.rowIndex * (materialScrollBar.recyclerView.layoutManager as GridLayoutManager).spanCount
            } else {
                scrollPosState.indicatorPosition
            }
            it.currentPosition = element
            it.setScrollDepth(adjustedScrollDepthPx)
        }
    }

    private val adjustedScrollDepthPx: Float
        get() {
            val scrollY: Int = materialScrollBar.paddingTop + scrollDepthPx - scrollPosState.rowTopOffset
            val scrollHeight = availableScrollHeight
            val barHeight = availableScrollBarHeight
            return scrollY.toFloat() / scrollHeight * barHeight
        }

    private val totalRowCount: Int
        get() {
            var rowCount: Int = materialScrollBar.recyclerView.layoutManager!!.itemCount
            if (materialScrollBar.recyclerView.layoutManager is GridLayoutManager) {
                val spanCount =
                    (materialScrollBar.recyclerView.layoutManager as GridLayoutManager).spanCount
                rowCount = ceil(rowCount.toDouble() / spanCount).toInt()
            }
            return rowCount
        }

    /**
     * Returns the available scroll bar height:
     * AvailableScrollBarHeight = Total height of the visible view - thumb height
     */
    private val availableScrollBarHeight: Int
        get() = materialScrollBar.height - materialScrollBar.handleThumb.height

    /**
     * Scrolls to the specified fraction of the RV
     *
     * @param touchFraction the fraction of the RV to scroll through
     * @return the distance traveled by the RV in the transformation applied by this method.
     * + is downward, - upward.
     */
    fun scrollToPositionAtProgress(touchFraction: Float) {
        val exactItemPos: Int
        val localAdapter = adapter
        if (localAdapter !is ICustomScroller) {
            var spanCount = 1
            if (materialScrollBar.recyclerView.layoutManager is GridLayoutManager) {
                spanCount = (materialScrollBar.recyclerView.layoutManager as GridLayoutManager).spanCount
            }

            // Stop the scroller if it is scrolling
            materialScrollBar.recyclerView.stopScroll()

            updateScrollPos()

            //The exact position of our desired item
            exactItemPos = (availableScrollHeight * touchFraction).toInt()

            //Scroll to the desired item. The offset used here is kind of hard to explain.
            //If the position we wish to scroll to is, say, position 10.5, we scroll to position 10,
            //and then offset by 0.5 * rowHeight. This is how we achieve smooth scrolling.
            val layoutManager = materialScrollBar.recyclerView.layoutManager as LinearLayoutManager
            try {
                layoutManager.scrollToPositionWithOffset(
                    spanCount * exactItemPos / scrollPosState.rowHeight,
                    -(exactItemPos % scrollPosState.rowHeight)
                )
            } catch (e: ArithmeticException) { /* Avoids issues where children of RV have not yet been laid out */ }
        } else {
            exactItemPos = localAdapter.getItemIndexForScroll(touchFraction)
            val offset =
                (localAdapter.getDepthForItem(exactItemPos) - touchFraction * availableScrollHeight).roundToInt()
            (materialScrollBar.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(exactItemPos, offset)
        }
        updateScrollOfHandleAndIndicator()
    }

    internal val availableScrollHeight: Int
        get() {
            updateScrollPos()
            val visibleHeight: Int = materialScrollBar.recyclerView.height
            val localAdapter = adapter
            val scrollHeight = if (localAdapter is ICustomScroller) {
                materialScrollBar.recyclerView.paddingTop + localAdapter.getTotalDepth() + materialScrollBar.recyclerView.paddingBottom
            } else {
                materialScrollBar.recyclerView.paddingTop + totalRowCount * scrollPosState.rowHeight + materialScrollBar.recyclerView.paddingBottom
            }
            return scrollHeight - visibleHeight
        }

    // Return early if there are no items
    private fun updateScrollPos() {
        scrollPosState.rowIndex = -1
        scrollPosState.rowTopOffset = -1
        scrollPosState.rowHeight = -1
        if (materialScrollBar.recyclerView.adapter == null) {
            Log.e(
                "MaterialScrollBarLib", "The adapter for your recyclerView has not been set; " +
                        "skipping layout."
            )
            return
        }

        // Return early if there are no items
        if (adapter.itemCount == 0) {
            return
        }
        val child: View = materialScrollBar.recyclerView.getChildAt(0)
        scrollPosState.rowIndex = materialScrollBar.recyclerView.getChildAdapterPosition(child)
        scrollPosState.indicatorPosition = scrollPosState.rowIndex
        if (materialScrollBar.recyclerView.layoutManager is GridLayoutManager) {
            scrollPosState.rowIndex =
                scrollPosState.rowIndex / (materialScrollBar.recyclerView.layoutManager as GridLayoutManager).spanCount
        }
        scrollPosState.rowTopOffset = materialScrollBar.recyclerView.layoutManager!!.getDecoratedTop(child)
        scrollPosState.rowHeight = child.height
        if (child.layoutParams is MarginLayoutParams) {
            scrollPosState.rowHeight += (child.layoutParams as MarginLayoutParams).topMargin
            scrollPosState.rowHeight += (child.layoutParams as MarginLayoutParams).bottomMargin
        }
    }

}