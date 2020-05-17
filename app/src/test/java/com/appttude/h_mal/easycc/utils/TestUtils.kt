package com.appttude.h_mal.easycc.utils

import androidx.lifecycle.LiveData
import com.appttude.h_mal.easycc.OneTimeObserver

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}