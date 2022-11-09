package com.app.twocommaquotes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.app.twocommaquotes.adapter.QuoteAdapter
import com.app.twocommaquotes.api.Resource
import com.app.twocommaquotes.api.loadingmanage.NetworkResult
import com.app.twocommaquotes.databinding.ActivityMainBinding
import com.app.twocommaquotes.utility.Utility
import com.app.twocommaquotes.utility.errorSnack
import com.app.twocommaquotes.viewmodels.DashboardViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var viewBinding: ActivityMainBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val listQuote = ArrayList<String>()

    @Inject
    lateinit var utility : Utility

    override fun onCreate(instance: Bundle?, viewBinding: ActivityMainBinding) {
        this.viewBinding = viewBinding

        viewBinding.clRoot.background = this.getDrawable(R.drawable.ic_black_iphone_bg)
//        viewModel.getData()
        addQuotes()
//        callApi()
        callApiFlow()

        viewBinding.rvText.setHasFixedSize(true)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(viewBinding.rvText)
        val adapter = QuoteAdapter()
        viewBinding.rvText.adapter = adapter
        adapter.submitList(listQuote)

    }

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private fun callApiFlow() {

        viewModel.userLiveData.observe(this){ response ->
            Log.d("Myvalue","Data :: ${response.data}")
            when(response) {
                is NetworkResult.ConnectionError -> {
                    utility.showNoInternetDialog(this) {
//                        viewModel.getData()
                    }
                }
                is NetworkResult.Error -> {
                    viewBinding.clRoot.errorSnack(response.message!!, Snackbar.LENGTH_LONG)
                    Log.d("MyValueErr","Error ${response.message}")
                }
                is NetworkResult.Loading -> {
                    Log.d("MyValueLoa","Loading")
                }
                is NetworkResult.Success -> {
                    response.data.let {

//                        viewBinding.rvText.setHasFixedSize(true)
//                        val adapter = QuoteAdapter()
//                        viewBinding.rvText.adapter = adapter
                        Log.d("Myvalue","Data :: $it")
//                        adapter.submitList(it?.results)
                    }
                }
            }
        }
    }

    private fun addQuotes() {
        listQuote.add("tere Bin Jiya")
        listQuote.add("tu hi meri")
        listQuote.add("na tum jano na hum")
        listQuote.add("kuch bhi")
        listQuote.add("man ki bat")
        listQuote.add("japnaam")

    }

    fun callNormalApiCall() {

        viewModel.getQuotesResult.observe(this) { response ->
            when(response) {
                is Resource.ConnectionError -> {
                    utility.showNoInternetDialog(this) {
                        viewModel.getQuoteNormal()
                    }
                }
                is Resource.Error -> {
                    viewBinding.clRoot.errorSnack(response.message!!, Snackbar.LENGTH_LONG)
                    Log.d("MyValueErrNormal","Error ${response.message}")
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    response.data.let {
                        Log.d("MyData", "${it?.results}")
//                        val snapHelper: SnapHelper = PagerSnapHelper()
//                        snapHelper.attachToRecyclerView(viewBinding.rvText)
//                        val adapter = QuoteAdapter()
//                        viewBinding.rvText.adapter = adapter
//                        adapter.submitList(it.results)
                    }
                }
            }

        }
    }

/*    private fun callApi() {
        viewModel.getCartProductResponse.observe(this) { response ->
            Log.d("Myvalue","Reponse :: ${response.data}")
            when (response) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    response.data.let {
                        viewBinding.rvText.setHasFixedSize(true)
                        val adapter = QuoteAdapter()
                        viewBinding.rvText.adapter = adapter
                        Log.d("Myvalue","Data :: $it")
                        it?.forEach {
                            adapter.submitList(it.results)
                        }
                    }
                }
                is Resource.ConnectionError -> {
                    utility.showNoInternetDialog(this) {
                        viewModel.getQuoteList()
                    }
                }
                is Resource.Error -> {
                    viewBinding.clRoot.errorSnack(response.message!!, Snackbar.LENGTH_LONG)
                }
            }
        }
    }*/

}
