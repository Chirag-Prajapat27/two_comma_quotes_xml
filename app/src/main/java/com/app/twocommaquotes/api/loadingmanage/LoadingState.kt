package com.app.twocommaquotes.api.loadingmanage

sealed class LoadingState<out T> : ErrorGetable {

    object Loading : LoadingState<Nothing>()

    class Loaded<T>(val value: T) : LoadingState<T>()

    class Error<T>(val error: Throwable) : LoadingState<T>()

    val isLoading get() = this is Loading

    fun getValueOrNull() : T? = if (this is Loaded<T>) value else null

    override fun getErrorIfExists() = if (this is Error)  error else null

}

sealed class LoadingSatate : ErrorGetable {
    object Loading : LoadingSatate()
    object Loaded : LoadingSatate()
    class Error(var e: Throwable) : LoadingSatate()

    val isLoading get() = this is Loading

    override fun getErrorIfExists() = if (this is Error)  e else null
}

interface ErrorGetable {

    fun getErrorIfExists() : Throwable?
}