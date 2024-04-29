package com.example.moengagenews

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.moengagenews.network.NetworkMonitor

class NewsApplication: Application() {

    private val TAG = this::class.simpleName

    private fun getConnectivityManager() = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called");
        getConnectivityManager().registerNetworkCallback(NetworkMonitor.getNetworkRequest(), NetworkMonitor.getNetworkCallBack())
    }

}