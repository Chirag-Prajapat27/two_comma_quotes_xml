package com.app.twocommaquotes.repository

import com.app.twocommaquotes.api.ApiService
import com.app.twocommaquotes.model.QuoteModel
import com.google.gson.JsonObject
import javax.inject.Inject

class MainAppRepository  @Inject constructor(private val service : ApiService) : BaseRepository() {


    suspend fun getQuotesList() = safeApiCall {
        service.getQuotesNormal() }



/*//    suspend fun getQuotesList() = safeApiCall { service.getQuotesList() }

//    suspend fun getQuotesList() : Flow<QuoteModel> = flow {
//        val response = service.getQuotesList()
//        emit(response)
//    }.flowOn(Dispatchers.IO)
//
//    suspend fun getQuotesListResult() : Flow<NetworkResult<QuoteModel>>  {
//       return  flow {
//           emit(NetworkResult.Loading())
//           val response = service.getQuotesList()
//           emit(NetworkResult.Success(response))
//       }.flowOn(Dispatchers.IO)
//    }
//
//    suspend fun getNormalQuotes() = safeApiCall {
//        service.getQuotesNormal()
//    }*/
}