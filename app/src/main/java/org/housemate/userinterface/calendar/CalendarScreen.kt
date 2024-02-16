package org.housemate.userinterface.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



data class Task(val name: String)
@Composable
fun MainLayout(navController: NavController) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 15.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {

        }

        // Drawing circles as placeholders for days in the week and weekdays as single letter text above
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomEnd) // Aligns the Canvas to the bottom-right corner of the Box
                .offset(
                    (-285).dp,
                    (-450).dp
                )
                .size(150.dp)
        ) {// Sunday circle
            drawCircle(
                color = Color.Black,
                radius = 20.dp.toPx()

            )


            val secondCircleColor = Color.Black
            val secondCircleRadius = 20.dp.toPx()
            val secondCircleCenter = Offset(x = 26 * size.width / 30, y = size.height / 2)
            // Monday circle
            drawCircle(
                color = secondCircleColor,
                radius = secondCircleRadius,
                center = secondCircleCenter
            )

            val thirdCircleColor = Color.Black
            val thirdCircleRadius = 20.dp.toPx()
            val thirdCircleCenter = Offset(x = 37 * size.width / 30, y = size.height / 2)
            // Tuesday circle
            drawCircle(
                color = thirdCircleColor,
                radius = thirdCircleRadius,
                center = thirdCircleCenter
            )

            val fourthCircleColor = Color.Black
            val fourthCircleRadius = 20.dp.toPx()
            val fourthCircleCenter = Offset(x = 48 * size.width / 30, y = size.height / 2)
            // Wednesday circle
            drawCircle(
                color = fourthCircleColor,
                radius = fourthCircleRadius,
                center = fourthCircleCenter
            )

            val fifthCircleColor = Color.Black
            val fifthCircleRadius = 20.dp.toPx()
            val fifthCircleCenter = Offset(x = 59 * size.width / 30, y = size.height / 2)
            // Thursday circle
            drawCircle(
                color = fifthCircleColor,
                radius = fifthCircleRadius,
                center = fifthCircleCenter
            )

            val sixthCircleColor = Color.Black
            val sixthCircleRadius = 20.dp.toPx()
            val sixthCircleCenter = Offset(x = 70 * size.width / 30, y = size.height / 2)
            // Friday circle
            drawCircle(
                color = sixthCircleColor,
                radius = sixthCircleRadius,
                center = sixthCircleCenter
            )

            val seventhCircleColor = Color.Black
            val seventhCircleRadius = 20.dp.toPx()
            val seventhCircleCenter = Offset(x = 81 * size.width / 30, y = size.height / 2)
            // Saturday circle
            drawCircle(
                color = seventhCircleColor,
                radius = seventhCircleRadius,
                center = seventhCircleCenter
            )
        }

        Text(
            text = "S", // Sunday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-353).dp, y = (-550).dp)
        )

        Text(
            text = "M", // Monday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-293).dp, y = (-550).dp)
        )

        Text(
            text = "T", // Tuesday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-243).dp, y = (-550).dp)
        )

        Text(
            text = "W", // Wednesday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-183).dp, y = (-550).dp)
        )

        Text(
            text = "T", // Thursday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-133).dp, y = (-550).dp)
        )

        Text(
            text = "F", // Friday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-76).dp, y = (-550).dp)
        )

        Text(
            text = "S", // Saturday
            color = Color.Red,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-23).dp, y = (-550).dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = 16.dp, top = 5.dp, end = 16.dp, bottom = 18.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { println("pressed") },
                shape = CutCornerShape(percent = 0),
                enabled = true,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Personal")
            }
            Button(
                onClick = { println("pressed") },
                shape = CutCornerShape(percent = 0),
                enabled = false,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("House")
            }
        }


    }
}


@Composable
fun CalendarScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}


