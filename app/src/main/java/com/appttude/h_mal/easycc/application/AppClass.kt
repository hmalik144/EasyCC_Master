package com.appttude.h_mal.easycc.application

import android.app.Application
import com.appttude.h_mal.easycc.data.network.api.BackupCurrencyApi
import com.appttude.h_mal.easycc.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.data.network.interceptors.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.data.network.interceptors.QueryInterceptor
import com.appttude.h_mal.easycc.data.network.interceptors.loggingInterceptor
import com.appttude.h_mal.easycc.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.helper.CurrencyDataHelper
import com.appttude.h_mal.easycc.helper.WidgetHelper
import com.appttude.h_mal.easycc.ui.main.MainViewModelFactory
import com.appttude.h_mal.easycc.ui.widget.WidgetViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class AppClass : Application(), KodeinAware {

    // KODEIN DI components declaration
    override val kodein by Kodein.lazy {
        import(androidXModule(this@AppClass))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { loggingInterceptor() }
        bind() from singleton { QueryInterceptor(instance()) }
        bind() from singleton { CurrencyApi(instance(), instance(), instance()) }
        bind() from singleton { BackupCurrencyApi(instance(),instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { RepositoryImpl(instance(), instance(), instance()) }
        bind() from singleton { CurrencyDataHelper(instance()) }
        bind() from singleton { WidgetHelper(instance(), instance()) }
        bind() from provider { MainViewModelFactory(instance(), instance()) }
        bind() from provider { WidgetViewModelFactory(instance()) }
    }

}