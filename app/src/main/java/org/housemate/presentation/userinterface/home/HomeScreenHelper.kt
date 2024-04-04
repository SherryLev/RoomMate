package org.housemate.presentation.userinterface.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import com.google.firebase.Timestamp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.Chore
import org.housemate.presentation.viewmodel.ChoresViewModel
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.green
import org.housemate.theme.light_purple
import org.housemate.theme.md_theme_light_error
import org.housemate.theme.pretty_purple
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.ExperimentalTime

@Composable
fun textShow(chore: Chore) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .height(60.dp),
        elevation = 5.dp,
        color = light_purple
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
        ) {
            Text(
                text = chore.choreName,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun TodaysChores(chores: List<Chore>) {
    LazyColumn(modifier = Modifier.padding(start = 16.dp, top = 10.dp)) {
        items(chores) { chore ->
            textShow(chore)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
fun filterChoresForCurrentUserAndToday(chores: List<Chore>, currentUser: String): List<Chore> {
    val currentDate = LocalDate.now()
    println("Current User: " + currentUser)
    println("Current Date: " + currentDate)
//    println("Chore Assignee: " + chores[0].assignee)
    //println("Chore due date: " + chores[0].dueDate)
    return chores.filter { chore ->
        chore.assignee == currentUser &&
                chore.dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() == currentDate
    }

}
@Composable
fun HomeScreenHelper(
    navController: NavHostController = rememberNavController(),
    onNavigateToSettingsScreen: () -> Unit,expenseViewModel: ExpenseViewModel = hiltViewModel(),
    choresViewModel: ChoresViewModel = hiltViewModel()) {
    LaunchedEffect(key1 = "fetchUserIdandchores") {
        choresViewModel.fetchCurrentUserId()
        choresViewModel.getAllChores()
    }
    val chores by choresViewModel.chores.collectAsState()
    val currentUserID by choresViewModel.userId.collectAsState()
   // println("Num" + chores[0].dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate())

    println("Current User Id: " + currentUserID)
    val fileredChores = currentUserID?.let { filterChoresForCurrentUserAndToday(chores, it) }

    val name = "Alice"
    val totalTasks = fileredChores?.size
    //val totalTasks =  chores.size
    val totalAmountOwedToYou by expenseViewModel.totalAmountOwedToYou.collectAsState()
    val totalAmountYouOwe by expenseViewModel.totalAmountYouOwe.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.TopEnd),
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()) {
                Text(
                    text = "Hey $name!",
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        onNavigateToSettingsScreen()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Today's Chores",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$totalTasks Chores Left",
                style = MaterialTheme.typography.body1.copy(
                    fontSize = 18.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ){
                /*if(chores.isNotEmpty()){
                    TodaysChores(chores)
                }*/
                if(totalTasks != null && totalTasks > 0) {
                    if (fileredChores != null) {
                        TodaysChores(fileredChores)
                    }
                }
                else{
                    Text(
                        text = "You have no chores on this day!",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.LightGray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
//                Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Expense Status",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
               shape = RoundedCornerShape(8.dp),
               elevation = 4.dp,
               modifier = Modifier
                   .padding(start = 20.dp)
                   .width((LocalConfiguration.current.screenWidthDp * 0.80).dp)
                   .height(100.dp),

               backgroundColor = Color.White
           ) {
               Row(
                   modifier = Modifier
                       .fillMaxHeight()
                       .height(IntrinsicSize.Min),
                   verticalAlignment = Alignment.CenterVertically,
                   horizontalArrangement = Arrangement.Center
               ) {
                   Column(
                       modifier = Modifier
                           .weight(1f)
                           .padding(16.dp),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally
                   ) {
                       Text(
                           "You are owed",
                           fontWeight = FontWeight.Bold, color = Color.DarkGray
                       )
                       // instead of hard coded values, use calculated amounts from viewmodel
                       Text("$${"%.2f".format(totalAmountOwedToYou)}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = green)
                   }
                   Column(
                       modifier = Modifier
                   ) {
                       Divider(
                           color = Color.LightGray,
                           modifier = Modifier
                               .height(45.dp)
                               .width(1.dp)
                       )
                   }
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
                           color = Color.DarkGray,
                           textAlign = TextAlign.Center
                       )
                       // instead of hard coded values, use calculated amounts from viewmodel
                       Text("$${"%.2f".format(totalAmountYouOwe)}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (totalAmountYouOwe != BigDecimal.ZERO) md_theme_light_error else green)
                   }
               }
           }
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = pretty_purple,
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You are doing great!",
                    style = MaterialTheme.typography.h5,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

