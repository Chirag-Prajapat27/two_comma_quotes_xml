package com.app.twocommaquotes.api

import com.app.twocommaquotes.model.QuoteModelNew
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("quotes")
    suspend fun getQuotesNormal(): Response<List<QuoteModelNew>>


// Below all api call is older version technique Practice for other api call methods

/*    *//*    @GET("quotes")
        suspend fun getQuotesList() : Response<BaseModel<List<QuoteModel>>>*//*

    //    @Headers("Accept:application/json")
//    @POST("register")
//    suspend fun createUser(
//        @Header("Content-Type") contentType: String,
//        @Body multipartBody: MultipartBody
//    ): Response<BaseModel<String>>

    @GET("quotes")
    suspend fun getQuotesList(): QuoteModel

    @GET("quotes")
    suspend fun getQuotes(): QuoteModel*/

}
