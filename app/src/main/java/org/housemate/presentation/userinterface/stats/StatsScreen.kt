package org.housemate.presentation.userinterface.stats


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.Chore
import org.housemate.presentation.viewmodel.ChoresViewModel
import org.housemate.theme.pretty_purple
import org.housemate.utils.AppScreenRoutes
import java.time.LocalDate
import java.time.ZoneId


data class Task(val name: String)

@Composable fun displayEachChore(currentUserChores: List<Chore>, index: Int, rating: Float){
    val roundedTotalAverageRating = String.format("%.2f", rating)
    Row(
        modifier = Modifier
            .padding(start = 40.dp, bottom = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currentUserChores[index].choreName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = roundedTotalAverageRating,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun MainLayout(navController: NavController, choresViewModel: ChoresViewModel = hiltViewModel()) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    LaunchedEffect(key1 = "fetchUserIdandchores") {
        choresViewModel.fetchCurrentUserId()
        choresViewModel.getAllChores()
        choresViewModel.fetchCurrentUser()
    }

    val chores by choresViewModel.chores.collectAsState()
    val currentUserID by choresViewModel.userId.collectAsState()
    val currentUser by choresViewModel.currentUser.collectAsState()
    val currentDate = LocalDate.now()
    val currentUserChores = chores.filter { it.assigneeId == currentUserID &&
            it.dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() == currentDate}


    val averageRatings = currentUserChores.map { chore ->
        val ratings = chore.userRating.values
        if (ratings.isNotEmpty()) {
            println("sum: " + ratings.sum())
            println("size: " + ratings.size)
            ratings.sum() / ratings.size
        } else {
            0f
        }
    }

    val totalAverageRating = if (averageRatings.isNotEmpty()) {
        averageRatings.sum() / averageRatings.size
    } else {
        0f
    }

    /*averageRatings.forEachIndexed { index, rating ->
        println("Chore ${currentUserChores[index].choreName}: $rating")
    }*/


    val roundedTotalAverageRating = String.format("%.2f", totalAverageRating)
    //val rating = 4.3
    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        ) {

            Text(
                "Statistics",
                modifier = Modifier
                    .padding(top = 20.dp, start = 6.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "View your spending history below",
                modifier = Modifier
                    .padding(top = 30.dp, start = 6.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp
            )


            Text(
                "Your Average Chore Rating is:",
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(150.dp)
                    .width(300.dp)
            ) {
                LazyColumn {
                    items(averageRatings.size) { index ->
                        displayEachChore(currentUserChores, index, averageRatings[index])
                    }
                }
            }
            Text(
                "Your Average Chore Rating is:",
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
               verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = pretty_purple,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(

                    text = "$roundedTotalAverageRating",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, // Increase the font size
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = pretty_purple,
                    modifier = Modifier.size(40.dp)
                )
            }

        }
    }
}



@Composable
fun StatsScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}
