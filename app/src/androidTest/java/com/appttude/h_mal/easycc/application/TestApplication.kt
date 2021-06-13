package com.appttude.h_mal.easycc.application

import android.app.Application
import androidx.test.espresso.idling.CountingIdlingResource
import com.appttude.h_mal.easycc.application.modules.MockRepository
import com.appttude.h_mal.easycc.helper.CurrencyDataHelper
import com.appttude.h_mal.easycc.ui.main.MainViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class TestApplication : Application(), KodeinAware {

    companion object {
        val idlingResources = CountingIdlingResource("Data_loader")
    }

    // KODEIN DI components declaration
    override val kodein by Kodein.lazy {
        import(androidXModule(this@TestApplication))

        bind() from singleton { MockRepository() }
        bind() from singleton { CurrencyDataHelper(instance()) }
        bind() from provider { MainViewModelFactory(instance(), instance()) }
    }

}