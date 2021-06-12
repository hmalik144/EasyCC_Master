package com.appttude.h_mal.easycc.data.network.api

import com.appttude.h_mal.easycc.data.network.interceptors.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.data.network.response.CurrencyResponse
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
interface BackupCurrencyApi {

    @GET("latest?")
    suspend fun getCurrencyRate(
        @Query("from") currencyFrom: String,
        @Query("to") currencyTo: String
    ): Response<CurrencyResponse>

    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor,
            interceptor: HttpLoggingInterceptor
        ): BackupCurrencyApi {

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(networkConnectionInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl("https://api.frankfurter.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BackupCurrencyApi::class.java)
        }
    }

}