package com.app.twocommaquotes.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.twocommaquotes.api.loadingmanage.NetworkResult
import com.app.twocommaquotes.api.loadingmanage.toLoadingState
import com.app.twocommaquotes.model.QuoteModel
import com.app.twocommaquotes.repository.MainAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository : MainAppRepository) : ViewModel() {

    private val _userResponseLiveData : MutableLiveData<NetworkResult<QuoteModel>> = MutableLiveData()

    fun getData() = liveData(Dispatchers.IO) {
        repository.getQuotesList().toLoadingState()
            .catch {
                _userResponseLiveData.postValue(NetworkResult.Error(it.message))
            }.collectLatest {
                Log.d("MyValueNew","VM : ${it}")
                _userResponseLiveData.postValue(NetworkResult.Loading())
                _userResponseLiveData.postValue(NetworkResult.Success(it.getValueOrNull()))
                emit(it)
            }
    }

    val userLiveData : LiveData<NetworkResult<QuoteModel>>
        get() = _userResponseLiveData

//    private val getQuoteResponse: MutableLiveData<Resource<List<QuoteModel>>> = MutableLiveData()
//
//    init {
//        getQuoteList()
//    }
//
//     fun getQuoteList() {
//        viewModelScope.launch {
//            if (BaseApplication.getInstance().isConnectionAvailable()) {
//                getQuoteResponse.postValue(Resource.Loading())
//                getQuoteResponse.postValue(repository.getQuotesList())
//            } else {
//                getQuoteResponse.postValue(Resource.ConnectionError())
//            }
//        }
//    }
//
//    val getCartProductResponse: LiveData<Resource<List<QuoteModel>>> = getQuoteResponse

}