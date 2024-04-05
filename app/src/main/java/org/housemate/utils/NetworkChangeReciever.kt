package org.housemate.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkChangeReceiver : BroadcastReceiver() {

    private val _isNetworkConnected = MutableStateFlow(false)
    val isNetworkConnected = _isNetworkConnected

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            _isNetworkConnected.value = networkInfo != null && networkInfo.isConnected
        }
    }
}