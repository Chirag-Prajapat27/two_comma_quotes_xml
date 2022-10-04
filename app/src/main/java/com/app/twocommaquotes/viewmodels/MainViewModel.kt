package com.app.twocommaquotes.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.twocommaquotes.BaseApplication
import com.app.twocommaquotes.api.Resource
import com.app.twocommaquotes.model.QuoteModel
import com.app.twocommaquotes.repository.MainAppRepository
import com.app.twocommaquotes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository : MainAppRepository) : ViewModel() {

    @Inject
    lateinit var utility: Utility

    private val getQuoteResponse: MutableLiveData<Resource<QuoteModel>> = MutableLiveData()

     fun getQuoteList(context : Activity) {
        viewModelScope.launch {
            if (BaseApplication.getInstance().isConnectionAvailable()) {
                getQuoteResponse.postValue(Resource.Loading())
                getQuoteResponse.postValue(repository.getQuotesList())
            } else {
                getQuoteResponse.postValue(Resource.ConnectionError())
            }
        }
    }

    val getCartProductResponse: LiveData<Resource<QuoteModel>> = getQuoteResponse
}