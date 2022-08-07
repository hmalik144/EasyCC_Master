@file:Suppress("DEPRECATION")

package com.appttude.h_mal.easycc.ui.main

import androidx.test.rule.ActivityTestRule
import com.appttude.h_mal.easycc.application.modules.MockRepository
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.di.AppModule
import com.appttude.h_mal.easycc.robots.currencyRobot
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object TestAppModule {
        @Provides
        fun provideRepository(impl: MockRepository): Repository {
            return impl
        }
    }

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
