package com.wynneplaga.materialScrollBar2

interface ICustomScroller {
    /**
     * @param index The index of the relevant element.
     * @return An integer in pixels representing the depth of the item within the recyclerView.
     * Usually just the sum of the height of all elements which appear above it in the recyclerView.
     */
    fun getDepthForItem(index: Int): Int

    /**
     * @return An integer representing the index of the item which should be scrolled to when the
     * user clicks at the specified length down the bar. For example, if "progress" is 0.5F then you
     * should return the index of the item which is half-way down the recyclerView.
     */
    fun getItemIndexForScroll(progress: Float): Int

    /**
     * @return The sum of the heights of all the views in the recyclerView.
     */
    fun getTotalDepth(): Int
}