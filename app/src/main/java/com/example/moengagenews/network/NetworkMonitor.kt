package com.example.moengagenews.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.MutableLiveData


/***
 * Network Monitor object to listen to Network changes
 */object NetworkMonitor {
     private val TAG = this::class.simpleName
    val networkState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    /***
     * Network request builder function
     * @return [NetworkRequest] with [TRANSPORT_WIFI] and [TRANSPORT_CELLULAR]
     */
    fun getNetworkRequest(): NetworkRequest {
        Log.d(TAG, "Calling getNetworkRequest()")
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    /***
     * Callback function to listen to network updates
     *
     */
    fun getNetworkCallBack(): ConnectivityManager.NetworkCallback {
        Log.d(TAG, "Calling getNetworkCallBack()")
        return object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {    //when Wifi/cellular is on
                super.onAvailable(network)
                Log.d(TAG, "onAvailable")
                networkState.postValue(true)
            }

            override fun onLost(network: Network) {    //when Wifi/cellular 【turns off】
                super.onLost(network)
                Log.d(TAG, "onLost")
                networkState.postValue(false)
            }
        }
    }

}