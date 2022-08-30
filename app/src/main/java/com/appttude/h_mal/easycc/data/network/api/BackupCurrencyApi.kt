package com.appttude.h_mal.easycc.data.network.api

import com.appttude.h_mal.easycc.data.network.response.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit Network class to currency api calls
 */
interface BackupCurrencyApi : Api {

    @GET("latest?")
    suspend fun getCurrencyRate(
        @Query("from") currencyFrom: String,
        @Query("to") currencyTo: String
    ): Response<CurrencyResponse>

}