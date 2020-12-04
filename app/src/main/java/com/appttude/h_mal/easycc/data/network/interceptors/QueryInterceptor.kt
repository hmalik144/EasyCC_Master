package com.appttude.h_mal.easycc.data.network.interceptors

import android.content.Context
import com.appttude.h_mal.easycc.BuildConfig
import com.appttude.h_mal.easycc.R
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor used in CurrencyApi
 * Adds apiKey to query parameters
 */
class QueryInterceptor(
    val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
                .addQueryParameter("apiKey", context.getString(R.string.apiKey))
                .build()

        // Add amended Url back to request
        val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)

        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }

}