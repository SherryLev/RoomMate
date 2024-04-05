package org.housemate

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.theme.HousemateTheme
import org.housemate.utils.RootNavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import org.housemate.utils.NetworkChangeReceiver

@Composable
fun NetworkErrorDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Network Error")
        },
        text = {
            Text(text = "Please connect to the internet to use HouseMate. Reconnect and open the app again! Goodbye.sh")
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(text = "OK")
            }
        }
    )
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val networkChangeReceiver = NetworkChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HousemateTheme {
                val isNetworkConnected by networkChangeReceiver.isNetworkConnected.collectAsState()
                if (isNetworkConnected) {
                    RootNavigationGraph(navController = rememberNavController())
                } else {
                    NetworkErrorDialog(onDismiss = { finish() })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(networkChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkChangeReceiver)
    }
}

