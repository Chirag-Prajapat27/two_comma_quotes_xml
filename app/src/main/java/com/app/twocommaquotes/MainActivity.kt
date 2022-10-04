package com.app.twocommaquotes

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import com.app.twocommaquotes.adapter.QuoteAdapter
import com.app.twocommaquotes.api.Resource
import com.app.twocommaquotes.databinding.ActivityMainBinding
import com.app.twocommaquotes.utility.Utility
import com.app.twocommaquotes.utility.errorSnack
import com.app.twocommaquotes.viewmodels.DashboardViewModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Response
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var viewBinding: ActivityMainBinding
    val viewModel: DashboardViewModel by viewModels()

    @Inject
    lateinit var utility : Utility

    override fun onCreate(instance: Bundle?, viewBinding: ActivityMainBinding) {
        this.viewBinding = viewBinding

        viewBinding.clRoot.background = this.getDrawable(R.drawable.ic_black_iphone_bg)
        callApi()
    }

    private fun callApi() {
        viewModel.getCartProductResponse.observe(this,
            Observer { response ->
                when(response) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        response.data.let {
                            viewBinding.rvText.setHasFixedSize(true)
                            val adapter= QuoteAdapter()
                            viewBinding.rvText.adapter = adapter
                            adapter.submitList(it?.results)
                        }
                    }
                    is Resource.ConnectionError -> {
                        utility.showNoInternetDialog(this) {
                            viewModel.getQuoteList(this)
                        }
                    }
                    is Resource.Error -> {
                        viewBinding.clRoot.errorSnack(response.message!!, Snackbar.LENGTH_LONG)
                    }
                }
            }
        )
    }

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate



}