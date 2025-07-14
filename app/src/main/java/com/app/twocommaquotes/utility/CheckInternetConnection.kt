package com.app.twocommaquotes.utility

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData

class CheckInternetConnection(private val connectivityManager: ConnectivityManager) :
    LiveData<Boolean>() {

    constructor(appContext: Application) : this(
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    companion object {
        const val TAG = "ConnectionCheckLog"
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(TAG, "onAvailable: Network is available")
            postValue(true)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Log.d(TAG, "onUnavailable: Network is not available")
            postValue(false)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {

            val isInternet =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            Log.d(TAG, "networkCapabilities: $network $networkCapabilities")
            val isValidated =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (isValidated) {
                Log.d(TAG, "hasCapability: $network $networkCapabilities")
            } else {
                Log.d(TAG, "Network has No Connection Capability: $network $networkCapabilities")
            }

            postValue(isInternet && isValidated)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(TAG, "Network onLost: $network")
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(), networkCallback
        )
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun getCurrentConnectionStatus(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val isValidated = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        } else {
            true
        }
        return hasInternet && isValidated
    }


}