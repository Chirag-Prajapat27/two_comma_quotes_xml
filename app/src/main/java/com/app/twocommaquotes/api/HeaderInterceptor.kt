package com.app.twocommaquotes.api

import okhttp3.Interceptor
import okhttp3.Response

internal class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
        return chain.proceed(builder.build())
    }
}