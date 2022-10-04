package com.app.twocommaquotes.repository

import com.app.twocommaquotes.api.ApiService
import javax.inject.Inject

class MainAppRepository  @Inject constructor(val service : ApiService) : BaseRepository() {

suspend fun getQuotesList() = safeApiCall { service.getQuotesList() }

}