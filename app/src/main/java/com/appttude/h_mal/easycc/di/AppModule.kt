package com.appttude.h_mal.easycc.di

import com.appttude.h_mal.easycc.data.network.api.BackupCurrencyApi
import com.appttude.h_mal.easycc.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.data.network.api.RemoteDataSource
import com.appttude.h_mal.easycc.data.network.interceptors.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.data.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()

    @Provides
    fun provideOkHttpclient(
        interceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
    ) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addNetworkInterceptor(networkConnectionInterceptor)
        .build()

    @Provides
    fun provideCurrencyApi(
        remoteDataSource: RemoteDataSource,
        okHttpClient: OkHttpClient
    ): CurrencyApi {
        return remoteDataSource.buildApi(
            okHttpClient,
            "https://free.currencyconverterapi.com/api/v3/",
            CurrencyApi::class.java
        )
    }

    @Provides
    fun provideBackupCurrencyApi(
        remoteDataSource: RemoteDataSource,
        okHttpClient: OkHttpClient
    ): BackupCurrencyApi {
        return remoteDataSource.buildApi(
            okHttpClient,
            "https://api.frankfurter.app/",
            BackupCurrencyApi::class.java
        )
    }

    @Provides
    fun provideRepository(impl: RepositoryImpl): Repository {
        return impl
    }
}