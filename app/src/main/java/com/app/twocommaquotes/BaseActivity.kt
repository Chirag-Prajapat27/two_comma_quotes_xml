package com.app.twocommaquotes

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var binding: ViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding?.root)
        onCreate(savedInstanceState, binding as VB)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    protected abstract fun onCreate(instance: Bundle?, viewBinding: VB)

    protected abstract val bindingInflater: (LayoutInflater) -> VB
}