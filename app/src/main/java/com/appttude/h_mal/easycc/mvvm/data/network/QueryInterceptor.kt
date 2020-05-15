package com.appttude.h_mal.easycc.mvvm.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class QueryInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
                .addQueryParameter("apikey", "a4f93cc2ff05dd772321")
                .build()

        // Add amended Url back to request
        val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)

        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }

}