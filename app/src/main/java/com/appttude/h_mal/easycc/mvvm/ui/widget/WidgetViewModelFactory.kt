package com.appttude.h_mal.easycc.mvvm.ui.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appttude.h_mal.easycc.mvvm.data.Repository.Repository

@Suppress("UNCHECKED_CAST")
class WidgetViewModelFactory (
        private val repository: Repository
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WidgetViewModel(repository) as T
    }
}