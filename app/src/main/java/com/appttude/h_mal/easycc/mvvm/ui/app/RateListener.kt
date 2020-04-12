package com.appttude.h_mal.easycc.mvvm.ui.app

interface RateListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}