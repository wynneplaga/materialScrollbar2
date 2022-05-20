package com.wynneplaga.materialscrollbar2

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.wynneplaga.materialScrollBar2.MaterialScrollBar
import com.wynneplaga.materialScrollBar2.inidicators.Indicator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MaterialScrollBarIntegrationTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun barDragDoesNotFail() {
        onView(isAssignableFrom(Indicator::class.java))
            .check { view, _ -> assert(view.alpha == 0f) }
        onView(isAssignableFrom(MaterialScrollBar::class.java))
            .perform(GeneralSwipeAction(Swipe.SLOW, GeneralLocation.TOP_RIGHT, GeneralLocation.BOTTOM_RIGHT, Press.FINGER))
        onView(isAssignableFrom(Indicator::class.java))
            .check { view, _ -> assert(view.alpha > 0f) }
        Thread.sleep(500)
        onView(isAssignableFrom(Indicator::class.java))
            .check { view, _ -> assert(view.alpha == 0f) }
    }

    @Test
    fun dragOnRecyclerViewShowsBar() {
        onView(isAssignableFrom(MaterialScrollBar::class.java))
            .check { view, _ -> !(view as MaterialScrollBar).isExpanded }
        onView(isAssignableFrom(RecyclerView::class.java))
            .perform(GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER, GeneralLocation.TOP_CENTER, Press.FINGER))
        onView(isAssignableFrom(MaterialScrollBar::class.java))
            .check { view, _ -> (view as MaterialScrollBar).isExpanded }
        Thread.sleep(500)
        onView(isAssignableFrom(MaterialScrollBar::class.java))
            .check { view, _ -> !(view as MaterialScrollBar).isExpanded }
    }

}