package com.appttude.h_mal.easycc.data.network.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RemoteDataSource @Inject constructor() {

    fun <Api> buildApi(
        okkHttpclient: OkHttpClient,
        baseUrl: String,
        api: Class<Api>
    ): Api {

        return Retrofit.Builder()
            .client(okkHttpclient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}