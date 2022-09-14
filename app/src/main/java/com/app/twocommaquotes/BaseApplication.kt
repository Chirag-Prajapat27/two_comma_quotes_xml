package com.app.twocommaquotes

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import com.app.twocommaquotes.utility.CheckInternetConnection

class BaseApplication : Application() {

    private lateinit var cld: LiveData<Boolean>
    private var isConnected: Boolean = false
    lateinit var productCartCount: ObservableInt
    lateinit var totalCartQTY: ObservableInt
    var cartScreenUpdate : Boolean = false

    //any written in this companion object is static you can access this variable using ApplicationLoader.REQUEST_TIMEOUT
    companion object {
        var enterAnimationFragment: Int = 0
        var exitAnimationFragment: Int = 0
        var enterAnimationActivity: Int = 0
        var exitAnimationActivity: Int = 0
        lateinit var appInstance: BaseApplication
        var byteArray: ByteArray? = null
        @Synchronized
        fun getInstance(): BaseApplication {
            return appInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        productCartCount = ObservableInt(0)
        totalCartQTY = ObservableInt(0)
        cld = CheckInternetConnection(this)
        observeConnectionStatus()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun isConnectionAvailable(): Boolean {
        return isConnected
    }

    private fun observeConnectionStatus() {
        cld.observeForever { t ->
            isConnected = t!!
        }
    }
}