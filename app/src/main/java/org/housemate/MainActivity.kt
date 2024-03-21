package org.housemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.theme.HousemateTheme
import org.housemate.utils.RootNavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.housemate.data.AuthRepositoryImpl
import org.housemate.data.firestore.UserRepositoryImpl
import org.housemate.domain.repositories.AuthRepository
import org.housemate.domain.repositories.UserRepository
import org.housemate.utils.Graph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HousemateTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }
}

