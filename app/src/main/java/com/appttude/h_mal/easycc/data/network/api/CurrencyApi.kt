package com.appttude.h_mal.easycc.data.network.api

import com.appttude.h_mal.easycc.data.network.interceptors.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.data.network.interceptors.QueryInterceptor
import com.appttude.h_mal.easycc.data.network.response.ResponseObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit Network class to currency api calls
 */
interface CurrencyApi {

    // Get rate from server with arguments passed in Repository
    @GET("convert?")
    suspend fun getCurrencyRate(@Query("q") currency: String): Response<ResponseObject>

    // interface invokation to be used in application class
    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor,
            queryInterceptor: QueryInterceptor,
            interceptor: HttpLoggingInterceptor
        ): CurrencyApi {

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(queryInterceptor)
                .addNetworkInterceptor(networkConnectionInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl("https://free.currencyconverterapi.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CurrencyApi::class.java)
        }
    }
}