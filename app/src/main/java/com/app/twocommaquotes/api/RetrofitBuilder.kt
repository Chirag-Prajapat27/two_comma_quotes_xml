package com.app.twocommaquotes.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class RetrofitBuilder {

    @Provides
    fun providesBaseUrl() : String = "https://api.quotable.io/"

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun okHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(NetworkInterceptor())
    }

    @Provides
    fun retrofitBuilder(baseUrl : String, okHttpClient: OkHttpClient.Builder) : Retrofit =

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

    @Provides
    fun apiService(retrofit : Retrofit) : ApiService = retrofit.create(ApiService::class.java)

}
