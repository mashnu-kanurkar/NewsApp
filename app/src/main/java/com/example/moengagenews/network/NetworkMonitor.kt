package com.example.moengagenews.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData


object NetworkMonitor {
    val networkState: MutableLiveData<NetworkState> by lazy {
        MutableLiveData<NetworkState>()
    }

    fun getNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }
    fun getNetworkCallBack(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {    //when Wifi/cellular is on
                super.onAvailable(network)
                networkState.postValue(NetworkState.OnAvailable)
            }

            override fun onLost(network: Network) {    //when Wifi/cellular 【turns off】
                super.onLost(network)
                networkState.postValue(NetworkState.OnLost)
            }
        }
    }
}

sealed class NetworkState{
    object OnAvailable: NetworkState()
    object OnLost: NetworkState()
}