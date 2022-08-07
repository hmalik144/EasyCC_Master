package com.appttude.h_mal.easycc.robots

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.appttude.h_mal.easycc.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

fun currencyRobot(func: CurrencyRobot.() -> Unit) = CurrencyRobot()
    .apply { func() }

class CurrencyRobot {

    fun clickOnTopList() {
        Espresso.onView(ViewMatchers.withId(R.id.currencyOne)).perform(ViewActions.click())
    }

    fun clickOnBottomList() {
        Espresso.onView(ViewMatchers.withId(R.id.currencyTwo)).perform(ViewActions.click())
    }

    fun searchInCurrencyList(search: String) {
        Espresso.onView(ViewMatchers.withId(R.id.search_text))
            .perform(ViewActions.replaceText(search), ViewActions.closeSoftKeyboard())
    }

    fun enterValueInTopEditText(text: String) {
        Espresso.onView(ViewMatchers.withId(R.id.topInsertValue))
            .perform(ViewActions.replaceText(text), ViewActions.closeSoftKeyboard())
    }

    fun selectItemInCurrencyList() {
        Espresso.onData(Matchers.anything())
            .inAdapterView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.list_view),
                    childAtPosition(
                        ViewMatchers.withClassName(Matchers.`is`("androidx.cardview.widget.CardView")),
                        0
                    )
                )
            )
            .atPosition(0)
            .perform(ViewActions.click())
    }

    fun assertTextInTop(text: String) {
        Espresso.onView(
            ViewMatchers.withId(R.id.topInsertValue)

        ).check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    fun assertTextInBottom(text: String) {
        Espresso.onView(
            ViewMatchers.withId(R.id.bottomInsertValues)

        ).check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}