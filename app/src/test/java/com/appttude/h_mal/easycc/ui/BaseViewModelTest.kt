package com.appttude.h_mal.easycc.ui

import androidx.lifecycle.MutableLiveData
import com.appttude.h_mal.easycc.utils.ViewState

abstract class BaseViewModelTest<V : BaseViewModel> {

    abstract val viewModel: V?

    open fun setUp() {
        viewModel?.uiState?.observeForever {
            when (it) {
                is ViewState.HasStarted -> Unit
                is ViewState.HasData<*> -> dataPost.postValue(it.data.getContentIfNotHandled())
                is ViewState.HasError -> errorPost.postValue(it.error.getContentIfNotHandled())
            }
        }
    }

    var dataPost: MutableLiveData<Any> = MutableLiveData()
    var errorPost: MutableLiveData<String> = MutableLiveData()
}