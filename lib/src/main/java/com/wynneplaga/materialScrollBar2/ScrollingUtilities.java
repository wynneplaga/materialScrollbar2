///*
// *  Copyright © 2016-2018, Turing Technologies, an unincorporated organisation of Wynne Plaga
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//package com.wynneplaga.materialScrollBar2;
//
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
///*
// * Lots of complicated maths taken mostly from Google.
// */
//class ScrollingUtilities {
//
//    private final MaterialScrollBar materialScrollBar;
//
//    ScrollingUtilities(MaterialScrollBar msb) {
//        materialScrollBar = msb;
//    }
//
//    ICustomScroller customScroller;
//
//    private final ScrollPositionState scrollPosState = new ScrollPositionState();
//
//    private int constant;
//
//    private LinearLayoutManager layoutManager;
//
//    private static class ScrollPositionState {
//        // The index of the first visible row
//        private int rowIndex;
//        // The offset of the first visible row
//        private int rowTopOffset;
//        // The height of a given row (they are currently all the same height)
//        private int rowHeight;
//        private int indicatorPosition;
//    }
//
//    void scrollHandleAndIndicator() {
//        int scrollBarY;
//        getCurScrollState();
//        if(customScroller != null) {
//            constant = customScroller.getDepthForItem(materialScrollBar.recyclerView.getChildAdapterPosition(materialScrollBar.recyclerView.getChildAt(0)));
//        } else {
//            constant = scrollPosState.rowHeight * scrollPosState.rowIndex;
//        }
//        constant += + materialScrollBar.recyclerView.getPaddingTop();
//        scrollBarY = (int) getScrollPosition();
//        materialScrollBar.handleThumb.setY(scrollBarY);
//        materialScrollBar.handleThumb.invalidate();
//        if(materialScrollBar.indicator != null) {
//            int element;
//            if(materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//                element = scrollPosState.rowIndex * ((GridLayoutManager)materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
//            } else {
//                element = scrollPosState.indicatorPosition;
//            }
//            materialScrollBar.indicator.setText(element);
//
//            materialScrollBar.indicator.setScroll(scrollBarY + materialScrollBar.getTop());
//        }
//    }
//
//    private float getScrollPosition() {
//        getCurScrollState();
//        int scrollY = materialScrollBar.getPaddingTop() + constant - scrollPosState.rowTopOffset;
//        int scrollHeight = getAvailableScrollHeight();
//        int barHeight = getAvailableScrollBarHeight();
//        return ((float) scrollY / scrollHeight) * barHeight;
//    }
//
//    private int getRowCount() {
//        int rowCount = materialScrollBar.recyclerView.getLayoutManager().getItemCount();
//        if(materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//            int spanCount = ((GridLayoutManager) materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
//            rowCount = (int) Math.ceil((double) rowCount / spanCount);
//        }
//        return rowCount;
//    }
//
//    /**
//     * Returns the available scroll bar height:
//     * AvailableScrollBarHeight = Total height of the visible view - thumb height
//     */
//    int getAvailableScrollBarHeight() {
//        return materialScrollBar.getHeight() - materialScrollBar.handleThumb.getHeight();
//    }
//
//    /**
//     * Scrolls to the specified fraction of the RV
//     *
//     * @param touchFraction the fraction of the RV to scroll through
//     * @return the distance traveled by the RV in the transformation applied by this method.
//     * + is downward, - upward.
//     */
//    int scrollToPositionAtProgress(float touchFraction) {
//        int priorPosition = materialScrollBar.recyclerView.computeVerticalScrollOffset();
//        int exactItemPos;
//        if(customScroller == null) {
//            int spanCount = 1;
//            if(materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//                spanCount = ((GridLayoutManager) materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
//            }
//
//            // Stop the scroller if it is scrolling
//            materialScrollBar.recyclerView.stopScroll();
//
//            getCurScrollState();
//
//            //The exact position of our desired item
//            exactItemPos = (int) (getAvailableScrollHeight() * touchFraction);
//
//            //Scroll to the desired item. The offset used here is kind of hard to explain.
//            //If the position we wish to scroll to is, say, position 10.5, we scroll to position 10,
//            //and then offset by 0.5 * rowHeight. This is how we achieve smooth scrolling.
//            LinearLayoutManager layoutManager = ((LinearLayoutManager) materialScrollBar.recyclerView.getLayoutManager());
//            try {
//                layoutManager.scrollToPositionWithOffset(spanCount * exactItemPos / scrollPosState.rowHeight,
//                        -(exactItemPos % scrollPosState.rowHeight));
//            } catch (ArithmeticException e) { /* Avoids issues where children of RV have not yet been laid out */ }
//        } else {
//            if(layoutManager == null) {
//                layoutManager = ((LinearLayoutManager) materialScrollBar.recyclerView.getLayoutManager());
//            }
//            exactItemPos = customScroller.getItemIndexForScroll(touchFraction);
//            int offset = (int) (customScroller.getDepthForItem(exactItemPos) - touchFraction * getAvailableScrollHeight());
//            layoutManager.scrollToPositionWithOffset(exactItemPos, offset);
//            return 0;
//        }
//        return exactItemPos - priorPosition;
//    }
//
//    int getAvailableScrollHeight() {
//        int visibleHeight = materialScrollBar.recyclerView.getHeight();
//        int scrollHeight;
//        if(customScroller != null) {
//            scrollHeight = materialScrollBar.recyclerView.getPaddingTop() + customScroller.getTotalDepth() + materialScrollBar.recyclerView.getPaddingBottom();
//        } else {
//            scrollHeight = materialScrollBar.recyclerView.getPaddingTop() + getRowCount() * scrollPosState.rowHeight + materialScrollBar.recyclerView.getPaddingBottom();
//        }
//        return scrollHeight - visibleHeight;
//    }
//
//    void getCurScrollState() {
//        scrollPosState.rowIndex = -1;
//        scrollPosState.rowTopOffset = -1;
//        scrollPosState.rowHeight = -1;
//
//        if  (materialScrollBar.recyclerView.getAdapter() == null) {
//            Log.e("MaterialScrollBarLib", "The adapter for your recyclerView has not been set; " +
//                    "skipping layout.");
//            return;
//        }
//
//        int itemCount = materialScrollBar.recyclerView.getAdapter().getItemCount();
//
//        // Return early if there are no items
//        if(itemCount == 0) {
//            return;
//        }
//
//        View child = materialScrollBar.recyclerView.getChildAt(0);
//
//        scrollPosState.rowIndex = materialScrollBar.recyclerView.getChildAdapterPosition(child);
//        scrollPosState.indicatorPosition = getIndicatorPosition();
//
//        if(materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//            scrollPosState.rowIndex = scrollPosState.rowIndex / ((GridLayoutManager) materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
//        }
//        if(child == null) {
//            scrollPosState.rowTopOffset = 0;
//            scrollPosState.rowHeight = 0;
//        } else {
//            scrollPosState.rowTopOffset = materialScrollBar.recyclerView.getLayoutManager().getDecoratedTop(child);
//            scrollPosState.rowHeight = child.getHeight();
//            if (child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
//                scrollPosState.rowHeight += ((ViewGroup.MarginLayoutParams)child.getLayoutParams()).topMargin;
//                scrollPosState.rowHeight += ((ViewGroup.MarginLayoutParams)child.getLayoutParams()).bottomMargin;
//            }
//        }
//    }
//
//    private int getIndicatorPosition(){
//        if(materialScrollBar.scrollMode == MaterialScrollBar.ScrollMode.FIRST_VISIBLE) {
//            return scrollPosState.rowIndex;
//        } else {
//            int itemCount = materialScrollBar.recyclerView.getAdapter().getItemCount();
//            int itemIndex = ((int) (itemCount * materialScrollBar.currentScrollPercent));
//            return itemIndex > 0 ? itemIndex - 1 : itemIndex;
//        }
//    }
//}
