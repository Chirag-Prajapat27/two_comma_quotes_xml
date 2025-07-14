package com.app.twocommaquotes.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.app.twocommaquotes.BaseApplication
import com.app.twocommaquotes.api.Resource
import com.app.twocommaquotes.api.loadingmanage.NetworkResult
import com.app.twocommaquotes.model.QuoteModel
import com.app.twocommaquotes.model.QuoteModelNew
import com.app.twocommaquotes.repository.MainAppRepository
import com.app.twocommaquotes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository: MainAppRepository) :
    ViewModel() {

    private val _getQuotesResponseState =
        MutableStateFlow<NetworkResult<List<QuoteModelNew>>>(NetworkResult.Loading())
    val getQuotesResponseState: StateFlow<NetworkResult<List<QuoteModelNew>>> =
        _getQuotesResponseState

    fun getQuotesNormal() {
        viewModelScope.launch(Dispatchers.IO) {
            _getQuotesResponseState.value = NetworkResult.Loading()
            if (BaseApplication.getInstance().isConnectionAvailable()) {
                try {
                    val result = repository.getQuotesList()
                    Log.d("APIDATA", "getQuotesNormal: $result")
                    _getQuotesResponseState.value = NetworkResult.Success(result.data)
                } catch (e: Exception) {
                    _getQuotesResponseState.value = NetworkResult.Error(e.message)
                }
            } else {
                _getQuotesResponseState.value = NetworkResult.ConnectionError()
            }
        }
    }


    /**  Below all api call is older version technique
     * LIVE DATA
     * Flow
     * Normal coroutine */

    /*   init {
           Log.d("MyValueLoa","init")
           getQuoteNormal()
   //        getData()
   //        testData()
       }
       private val _userResponseLiveData : MutableLiveData<NetworkResult<QuoteModel>> = MutableLiveData()

   //    fun getData() = liveData(Dispatchers.IO) {
   //        Log.d("MyValue","getData")
   //        viewModelScope.launch {
   //            repository.getQuotesList().toLoadingState()
   //                .catch {
   //                    _userResponseLiveData.postValue(NetworkResult.Error(it.message))
   //                }.collectLatest {
   //                    Log.d("MyValueNew", "VM : ${it}")
   //                    _userResponseLiveData.postValue(NetworkResult.Loading())
   //                    _userResponseLiveData.postValue(NetworkResult.Success(it.getValueOrNull()))
   //                    emit(it)
   //                }
   //        }
   //    }

   //    fun testData()   {
   //        Log.d("MyValue","testDeta")
   //        viewModelScope.launch {
   //            repository.getQuotesListResult()
   //                .onStart {
   //                    Log.d("MyValueNew","VM : on start ")
   //                    _userResponseLiveData.postValue(NetworkResult.Loading())
   //                }.catch { exception ->
   //                    Log.d("MyValueNew","VM Error : ${exception.message}")
   //                    _userResponseLiveData.postValue(NetworkResult.Error(exception.message))
   //                }.collectLatest {
   //                    Log.d("MyValueNew","VM : $it")
   //                    _userResponseLiveData.postValue(it)
   ////                    emit(it)
   //            }
   //        }
   //    }

       val userLiveData : LiveData<NetworkResult<QuoteModel>>
           get() = _userResponseLiveData

       private val getQuoteNormalResponse: MutableLiveData<Resource<QuoteModel>> = MutableLiveData()

       fun getQuoteNormal() {
           viewModelScope.launch {
               if (BaseApplication.getInstance().isConnectionAvailable()) {
                   getQuoteNormalResponse.postValue(Resource.Loading())
                   getQuoteNormalResponse.postValue(repository.getNormalQuotes())
               } else {
                   getQuoteNormalResponse.postValue(Resource.ConnectionError())
               }
           }
       }
       val getQuotesResult : LiveData<Resource<QuoteModel>> = getQuoteNormalResponse

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
   //    val getCartProductResponse: LiveData<Resource<List<QuoteModel>>> = getQuoteResponse*/

}