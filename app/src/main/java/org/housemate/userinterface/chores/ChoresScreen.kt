package org.housemate.userinterface.chores

import android.widget.GridLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt


@Composable
fun personalButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Personal")
    }
}
@Composable
fun houseButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("House")
    }
}

@Composable
fun MainLayout() {
    Box(
        Modifier.fillMaxSize()
            .padding(15.dp)) {
        Row(
            modifier = Modifier.align(Alignment.TopCenter)
        ){
            Button(
                onClick = { println("pressed")},
                shape = CutCornerShape(percent = 0),
                enabled = true
            )
            {
                Text("Personal")
            }
            Button(
                onClick = { println(" pressed")},
                shape = CutCornerShape(percent = 0),
                enabled = false
            )
            {
                Text("House")
            }
        }
        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { println("+ pressed")}
        )
        {
            Text("+ Create Chore")
        }

    }
}