package com.app.twocommaquotes.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.app.twocommaquotes.BaseApplication
import com.app.twocommaquotes.api.Resource
import com.app.twocommaquotes.api.loadingmanage.NetworkResult
import com.app.twocommaquotes.model.QuoteModel
import com.app.twocommaquotes.repository.MainAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository : MainAppRepository) : ViewModel() {

    init {
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

    fun testData()   {
        Log.d("MyValue","testDeta")
        viewModelScope.launch {
            repository.getQuotesListResult()
                .onStart {
                    Log.d("MyValueNew","VM : on start ")
                    _userResponseLiveData.postValue(NetworkResult.Loading())
                }.catch { exception ->
                    Log.d("MyValueNew","VM Error : ${exception.message}")
                    _userResponseLiveData.postValue(NetworkResult.Error(exception.message))
                }.collectLatest {
                    Log.d("MyValueNew","VM : $it")
                    _userResponseLiveData.postValue(it)
//                    emit(it)
            }
        }
    }

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
//    val getCartProductResponse: LiveData<Resource<List<QuoteModel>>> = getQuoteResponse

}