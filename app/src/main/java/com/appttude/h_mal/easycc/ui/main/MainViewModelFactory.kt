package com.appttude.h_mal.easycc.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.helper.CurrencyDataHelper

/**
 * Viewmodel factory for [MainViewModel]
 * inject repository into viewmodel
 */
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory (
        private val repository: RepositoryImpl,
        private val helper: CurrencyDataHelper
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(helper, repository) as T
    }
}