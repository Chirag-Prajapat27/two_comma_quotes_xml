package com.app.twocommaquotes.api

sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T? = null, message: String) : Resource<T>(data, message)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
    class ConnectionError<T> : Resource<T>()
}