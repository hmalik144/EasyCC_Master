package com.appttude.h_mal.easycc.mvvm.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appttude.h_mal.easycc.mvvm.data.Repository.Repository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory (
        private val repository: Repository
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}