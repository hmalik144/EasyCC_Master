package com.appttude.h_mal.easycc.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.easycc.utils.Event
import com.appttude.h_mal.easycc.utils.ViewState

abstract class BaseViewModel : ViewModel() {
    open val uiState: MutableLiveData<ViewState> = MutableLiveData()

    fun onStart() {
        uiState.postValue(ViewState.HasStarted)
    }

    fun <T : Any> onSuccess(result: T) {
        uiState.postValue(ViewState.HasData(Event(result)))
    }

    protected fun onError(error: String) {
        uiState.postValue(ViewState.HasError(Event(error)))
    }
}