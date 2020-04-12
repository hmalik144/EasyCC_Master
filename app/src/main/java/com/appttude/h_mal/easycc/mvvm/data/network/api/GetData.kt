package com.appttude.h_mal.easycc.mvvm.data.network.api

import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.data.network.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface GetData {

    companion object{
        operator fun invoke(
                networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : GetData{

            val okkHttpclient = OkHttpClient.Builder()
                    .addNetworkInterceptor(networkConnectionInterceptor)
                    .build()

            return Retrofit.Builder()
                    .client(okkHttpclient)
                    .baseUrl("https://free.currencyconverterapi.com/api/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GetData::class.java)
        }
    }

    @GET("convert?")
    suspend fun getCurrencyRate(@Query("q") currency: String, @Query("apiKey") api: String): Response<ResponseObject>

}