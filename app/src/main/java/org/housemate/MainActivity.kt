package org.housemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.housemate.theme.CleandesktopuiTheme
import org.housemate.model.UserModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userModel = UserModel()

        setContent {
            CleandesktopuiTheme {
            }
        }
    }
}
