package com.appttude.h_mal.easycc.utils

sealed class ViewState {
    object HasStarted : ViewState()
    class HasData<T : Any>(val data: Event<T>) : ViewState()
    class HasError(val error: Event<String>) : ViewState()
}