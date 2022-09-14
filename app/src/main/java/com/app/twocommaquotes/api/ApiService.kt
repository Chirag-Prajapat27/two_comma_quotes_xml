package com.app.twocommaquotes.api

import com.app.twocommaquotes.model.BaseModel
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

//    @Headers("Accept:application/json")
//    @POST("register")
//    suspend fun createUser(
//        @Header("Content-Type") contentType: String,
//        @Body multipartBody: MultipartBody
//    ): Response<BaseModel<String>>

    @GET("logout")
    suspend fun logout(): Response<BaseModel<String>>


}
