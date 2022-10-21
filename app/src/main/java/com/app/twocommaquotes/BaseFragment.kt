package com.app.twocommaquotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var binding: ViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflater.invoke(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(savedInstanceState, binding as VB)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    protected abstract fun onViewCreated(instance: Bundle?, viewBinding: VB)

    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
}