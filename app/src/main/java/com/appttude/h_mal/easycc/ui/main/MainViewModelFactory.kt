package com.appttude.h_mal.easycc.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl

/**
 * Viewmodel factory for [MainViewModel]
 * inject repository into viewmodel
 */
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory (
        private val repository: RepositoryImpl
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}