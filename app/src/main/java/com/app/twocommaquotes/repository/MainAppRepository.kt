package com.app.twocommaquotes.repository

import com.app.twocommaquotes.api.ApiService
import com.app.twocommaquotes.model.QuoteModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainAppRepository  @Inject constructor(private val service : ApiService) : BaseRepository() {

//    suspend fun getQuotesList() = safeApiCall { service.getQuotesList() }

    suspend fun getQuotesList() : Flow<QuoteModel> = flow {
        val response = service.getQuotesList()
        emit(response)
    }
}