package com.appttude.h_mal.easycc.data.network.interceptors

import com.appttude.h_mal.easycc.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor used in CurrencyApi
 * Adds apiKey to query parameters
 */
class QueryInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
                .addQueryParameter("apiKey", BuildConfig.CC_API_KEY)
                .build()

        // Add amended Url back to request
        val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)

        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }

}