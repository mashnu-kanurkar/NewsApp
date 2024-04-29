package com.example.moengagenews.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.moengagenews.R
import com.example.moengagenews.network.NetworkMonitor

class NoNetworkActivity : AppCompatActivity() {

    private val TAG = this::class.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network)
        NetworkMonitor.networkState.observe(this, activeNetworkStateObserver)
    }
    private val activeNetworkStateObserver: Observer<Boolean> =
        Observer<Boolean> { value ->
            Log.d(TAG, "Network changed: $value")
            changeUIForNetworkChange(value)
        }

    private fun changeUIForNetworkChange(isConnected: Boolean){
        if (isConnected){
            finish()
        }
    }
}