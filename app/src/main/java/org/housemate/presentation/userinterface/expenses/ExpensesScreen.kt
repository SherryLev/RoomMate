package org.housemate.presentation.userinterface.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.Divider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import org.housemate.theme.green
import org.housemate.theme.md_theme_light_error
import org.housemate.theme.md_theme_light_primaryContainer

@Composable
fun ExpensesScreen(navController: NavHostController = rememberNavController()) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Top title
            Box(
                modifier = Modifier
                    .fillMaxWidth(),

                ) {
                Text(
                    text = "House Expenses",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .padding(bottom = 8.dp)
                )
            }

            Box (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(8.dp)
                        .width((LocalConfiguration.current.screenWidthDp * 0.80).dp)
                        .align(Alignment.Center),
                    backgroundColor = Color.White
                ) {
                    // Left and right sides of the box with a divider
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Left side of the box: "You are owed $2"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "You are owed",
                                fontWeight = FontWeight.Bold
                            )
                            Text("$2", fontSize = 24.sp, color = green)
                        }
                        Column(
                            modifier = Modifier
                        ) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .height(45.dp)
                                    .width(1.dp)
                            )
                        }
                        // Right side of the box: "You owe $200"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "You owe",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text("$200", fontSize = 24.sp, color = md_theme_light_error)
                        }
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        buildAnnotatedString {
                            append("Sally owes you ")

                            withStyle(
                                style = SpanStyle(
                                    color = green,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("$2")
                            }
                        },
                        modifier = Modifier
                            .padding(6.dp)
                    )

                    Text(
                        buildAnnotatedString {
                            append("You owe Bob ")

                            withStyle(
                                style = SpanStyle(
                                    color = md_theme_light_error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("$100")
                            }
                        },
                        modifier = Modifier
                            .padding(6.dp)
                    )

                    Text(
                        buildAnnotatedString {
                            append("You owe Mike ")

                            withStyle(
                                style = SpanStyle(
                                    color = md_theme_light_error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("$100")
                            }
                        },
                        modifier = Modifier
                            .padding(6.dp)
                    )

                    Button(
                        onClick = { /* Handle settle debts */ },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp)
                    ) {
                        Text("Settle debts", fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            ) {
                Text(
                    text = "Expense history",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
        item {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    repeat(50) {
                        Text("Expense Item $it", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 15.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {}
            ) {
                Text("+ Add Expense")
            }
        }
    }
}