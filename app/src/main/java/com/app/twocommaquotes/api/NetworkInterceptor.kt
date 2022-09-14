package com.app.twocommaquotes.api

import com.app.twocommaquotes.BaseApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class NetworkInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BaseApplication.appInstance.isConnectionAvailable()) {
            throw NoInternetException()
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

internal class NoInternetException : IOException() {
    override val message: String
        get() = "Please check your internet connection"
}
