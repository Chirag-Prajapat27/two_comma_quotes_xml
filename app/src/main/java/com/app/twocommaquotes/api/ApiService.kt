package com.app.twocommaquotes.api

import com.app.twocommaquotes.model.BaseModel
import com.app.twocommaquotes.model.QuoteModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

//    @Headers("Accept:application/json")
//    @POST("register")
//    suspend fun createUser(
//        @Header("Content-Type") contentType: String,
//        @Body multipartBody: MultipartBody
//    ): Response<BaseModel<String>>

    @GET("quotes")
    suspend fun getQuotesNormal(): Response<BaseModel<QuoteModel>>

/*    @GET("quotes")
    suspend fun getQuotesList() : Response<BaseModel<List<QuoteModel>>>*/

    @GET("quotes")
    suspend fun getQuotesList() : QuoteModel

    @GET("quotes")
    suspend fun getQuotes() : QuoteModel


}
