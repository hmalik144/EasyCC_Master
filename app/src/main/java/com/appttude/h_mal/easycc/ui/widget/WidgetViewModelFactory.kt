package com.appttude.h_mal.easycc.ui.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl

@Suppress("UNCHECKED_CAST")
class WidgetViewModelFactory(
    private val repository: RepositoryImpl
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WidgetViewModel(repository) as T
    }
}