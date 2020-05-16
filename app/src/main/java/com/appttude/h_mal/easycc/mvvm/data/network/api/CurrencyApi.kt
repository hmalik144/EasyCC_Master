package com.appttude.h_mal.easycc.mvvm.data.network.api

import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.data.network.interceptors.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.mvvm.data.network.interceptors.QueryInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface CurrencyApi {

    companion object{
        operator fun invoke(
                networkConnectionInterceptor: NetworkConnectionInterceptor,
                queryInterceptor: QueryInterceptor
        ) : CurrencyApi{

            val okkHttpclient = OkHttpClient.Builder()
                    .addNetworkInterceptor(networkConnectionInterceptor)
                    .addInterceptor(queryInterceptor)
                    .build()

            return Retrofit.Builder()
                    .client(okkHttpclient)
                    .baseUrl("https://free.currencyconverterapi.com/api/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(CurrencyApi::class.java)
        }
    }

    @GET("convert?")
    suspend fun getCurrencyRate(@Query("q") currency: String): Response<ResponseObject>

}