package com.appttude.h_mal.easycc.data.network.api

import com.appttude.h_mal.easycc.data.network.response.ResponseObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit Network class to currency api calls
 */
interface CurrencyApi : Api {

    // Get rate from server with arguments passed in Repository
    @GET("convert?")
    suspend fun getCurrencyRate(@Query("q") currency: String): Response<ResponseObject>

}