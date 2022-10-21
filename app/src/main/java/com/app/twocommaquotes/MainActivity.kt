package com.app.twocommaquotes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.app.twocommaquotes.adapter.QuoteAdapter
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

    @Inject
    lateinit var utility : Utility

    override fun onCreate(instance: Bundle?, viewBinding: ActivityMainBinding) {
        this.viewBinding = viewBinding

        viewBinding.clRoot.background = this.getDrawable(R.drawable.ic_black_iphone_bg)
        viewModel.getData()
//        callApi()
        callApiFlow()
    }

    private fun callApiFlow() {

        viewModel.userLiveData.observe(this){ response ->
            Log.d("Myvalue","Data :: ${response.data}")
            when(response) {
                is NetworkResult.ConnectionError -> {
                    utility.showNoInternetDialog(this) {
                        viewModel.getData()
                    }
                }
                is NetworkResult.Error -> {
                    viewBinding.clRoot.errorSnack(response.message!!, Snackbar.LENGTH_LONG)
                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Success -> {
                    response.data.let {
                        viewBinding.rvText.setHasFixedSize(true)
                        val adapter = QuoteAdapter()
                        viewBinding.rvText.adapter = adapter
                        Log.d("Myvalue","Data :: $it")
                        adapter.submitList(it?.results)
                    }
                }
            }
        }

    }

//    private fun callApi() {
//        viewModel.getCartProductResponse.observe(this) { response ->
//            Log.d("Myvalue","Reponse :: ${response.data}")
//            when (response) {
//                is Resource.Loading -> {
//                }
//                is Resource.Success -> {
//                    response.data.let {
//                        viewBinding.rvText.setHasFixedSize(true)
//                        val adapter = QuoteAdapter()
//                        viewBinding.rvText.adapter = adapter
//                        Log.d("Myvalue","Data :: $it")
//                        it?.forEach {
//                            adapter.submitList(it.results)
//                        }
//                    }
//                }
//                is Resource.ConnectionError -> {
//                    utility.showNoInternetDialog(this) {
//                        viewModel.getQuoteList()
//                    }
//                }
//                is Resource.Error -> {
//                    viewBinding.clRoot.errorSnack(response.message!!, Snackbar.LENGTH_LONG)
//                }
//            }
//        }
//    }

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

}
