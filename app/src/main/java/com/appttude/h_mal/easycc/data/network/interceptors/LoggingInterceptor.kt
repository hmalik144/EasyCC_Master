package com.appttude.h_mal.easycc.data.network.interceptors

import okhttp3.logging.HttpLoggingInterceptor

fun loggingInterceptor() = run {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.apply {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }
}