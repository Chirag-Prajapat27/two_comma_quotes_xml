package com.app.twocommaquotes.api.loadingmanage

import kotlinx.coroutines.flow.*

fun <T> Flow<T>.toLoadingState() : Flow<LoadingState<T>> {

    return map<T,LoadingState<T>>{
        LoadingState.Loaded(it)
    }.onStart {
        emit(LoadingState.Loading as LoadingState<T>)
    }.onEach {
        LoadingState.Loading
    }.catch {
        emit(LoadingState.Error<T>(it))
    }

}