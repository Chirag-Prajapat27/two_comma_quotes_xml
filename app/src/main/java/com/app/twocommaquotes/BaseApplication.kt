package com.app.twocommaquotes

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import com.app.twocommaquotes.utility.CheckInternetConnection
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    private lateinit var cld: CheckInternetConnection
    private var isConnected: Boolean = false
//    lateinit var productCartCount: ObservableInt
//    lateinit var totalCartQTY: ObservableInt

    //any written in this companion object is static you can access this variable using ApplicationLoader.REQUEST_TIMEOUT
    companion object {
        lateinit var appInstance: BaseApplication
        @Synchronized
        fun getInstance(): BaseApplication {
            return appInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
//        productCartCount = ObservableInt(0)
//        totalCartQTY = ObservableInt(0)

        val connectionLiveData = CheckInternetConnection(this)
        cld = connectionLiveData

        isConnected = connectionLiveData.getCurrentConnectionStatus()

        observeConnectionStatus()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun isConnectionAvailable(): Boolean {
        return isConnected
    }

    private fun observeConnectionStatus() {
        cld.observeForever { connected ->
            isConnected = connected ?: false
        }
    }
}