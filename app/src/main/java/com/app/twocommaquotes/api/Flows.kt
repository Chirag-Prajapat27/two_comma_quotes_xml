package com.app.twocommaquotes.api

import kotlinx.coroutines.flow.*

fun <T> Flow<T>.toLoadingState(): Flow<Resource<T>> {
    return map<T, Resource<T>> {
        Resource.Success(it, it.toString())
    }.onStart {
        @Suppress("UNCHECKED_CAST")
        emit(Resource.Loading<T>() as Resource<T>)
    }.onEach {
        Resource.Loading<T>()
    }.catch { e ->
        emit(Resource.Error(e.message!!))
    }
}