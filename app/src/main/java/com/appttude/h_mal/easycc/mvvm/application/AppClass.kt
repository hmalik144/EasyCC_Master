package com.appttude.h_mal.easycc.mvvm.application

import android.app.Application
import com.appttude.h_mal.easycc.mvvm.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.mvvm.data.network.interceptors.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.mvvm.data.network.interceptors.QueryInterceptor
import com.appttude.h_mal.easycc.mvvm.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.mvvm.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.mvvm.ui.app.MainViewModelFactory
import com.appttude.h_mal.easycc.mvvm.ui.widget.WidgetViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class AppClass : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(androidXModule(this@AppClass))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { QueryInterceptor() }
        bind() from singleton { CurrencyApi(instance(),instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { RepositoryImpl(instance(), instance(), instance()) }
        bind() from provider { MainViewModelFactory(instance()) }
        bind() from provider { WidgetViewModelFactory(instance()) }
    }

}