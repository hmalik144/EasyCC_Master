@file:Suppress("DEPRECATION")

package com.appttude.h_mal.easycc.ui.main


import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.appttude.h_mal.easycc.robots.currencyRobot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        currencyRobot {
            clickOnTopList()
            searchInCurrencyList("AUD")
            selectItemInCurrencyList()
            clickOnBottomList()
            searchInCurrencyList("GBP")
            selectItemInCurrencyList()
            enterValueInTopEditText("1")
            assertTextInBottom("0.55")
        }
    }
}
