package org.housemate.presentation.userinterface.stats


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.Chore
import org.housemate.domain.model.User
import org.housemate.presentation.viewmodel.ChoresViewModel
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.purple_primary
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters


fun calculateRoundedAverageRating(rating: Float): String {
    return if (rating != 0.0f) {
        BigDecimal(rating.toDouble())
            .setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    } else {
        "No ratings yet"
    }
}

@Composable fun DisplayEachChore(currentUserChores: List<Chore>, index: Int, rating: Float){
    val roundedAvg = calculateRoundedAverageRating(rating)
    Row(
        modifier = Modifier
            .padding(start = 50.dp, bottom = 16.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currentUserChores[index].choreName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$roundedAvg",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (rating != 0.0f) Color.DarkGray else Color.LightGray
        )
    }
}




@Composable
fun WeeklyChoreRateSurface(users: List<User>, chores: List<Chore>) {
    val currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val currentWeekEnd = currentWeekStart.plusDays(6)
    val userAverageRatings = calculateUserAverageRatings(users, chores, currentWeekStart, currentWeekEnd)
    val sortedUserAverageRatings = userAverageRatings.toList().sortedByDescending { it.second }
    Column {
        Text(
            text = "Chore ratings leaderboard",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 56.dp, top = 24.dp, end = 48.dp)
                .height(200.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ){
                items(sortedUserAverageRatings) { (userName, averageRating) ->
                    UserChoreRateItem(userName, averageRating)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun UserChoreRateItem(userName: String, averageRating: Float) {
    val roundedAvg= calculateRoundedAverageRating(averageRating)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = userName,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        if (roundedAvg != "No ratings yet") {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = purple_primary,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "$roundedAvg",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
            )
        } else{
            Text(
                text = "$roundedAvg",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = Color.LightGray
            )
        }
    }

}
fun getUserDisplayName(userId: String, users: List<User>): String {
    val user = users.find { it.uid == userId }
    return user?.username ?: "Unknown User"
}
@Composable
fun calculateUserAverageRatings(
    users: List<User>,
    chores: List<Chore>,
    currentWeekStart: LocalDate,
    currentWeekEnd: LocalDate
): Map<String, Float> {
    val userAverageRatings = mutableMapOf<String, Float>()
    users.forEach { user ->
        val currentWeekUserChoresForUser = chores.filter { chore ->
            chore.assigneeId == user.uid &&
                    chore.dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()!! in currentWeekStart..currentWeekEnd
        }
        val averageRatings = currentWeekUserChoresForUser.mapNotNull { chore ->
            chore.userRating.values.takeIf { it.isNotEmpty() }?.average()?.toFloat()
        }
        val totalAverageRating = if (averageRatings.isNotEmpty()) {
            averageRatings.average().toFloat()
        } else {
            0f
        }
        val userName = getUserDisplayName(user.uid, users)
        userAverageRatings[userName] = totalAverageRating
    }
    return userAverageRatings
}




@Composable
fun MainLayout(navController: NavController, choresViewModel: ChoresViewModel = hiltViewModel(), expenseViewModel: ExpenseViewModel = hiltViewModel()) {
    LaunchedEffect(key1 = "fetchUserIdandchores") {
        choresViewModel.fetchAllHousemates()
        choresViewModel.fetchCurrentUserId()
        choresViewModel.getAllChores()
        choresViewModel.fetchCurrentUser()

    }

    val chores by choresViewModel.chores.collectAsState()

    val currentUserID by choresViewModel.userId.collectAsState()
    val currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val currentWeekEnd = currentWeekStart.plusDays(6)

    val currentWeekUserChoresForUser = chores.filter { chore ->
        chore.assigneeId == currentUserID &&
                chore.dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() in currentWeekStart..currentWeekEnd
    }

    val expenses by expenseViewModel.expenseItems.collectAsState()
    val payments by expenseViewModel.paymentItems.collectAsState()

    // Filter expenses where the payerId matches the current user's ID
    val expensesPaidByUser = expenses.filter { it.payerId == currentUserID }

    val filteredExpenses = expensesPaidByUser.filter { expense ->
        val owingAmountForCurrentUser = expense.owingAmounts[currentUserID]
        owingAmountForCurrentUser != null
    }
    val filteredPayments = payments.filter { payment ->
        payment.payeeId == currentUserID
    }
    val users by choresViewModel.housemates.collectAsState()

    val currentDate = LocalDate.now()
    val startOfMonth = currentDate.withDayOfMonth(1)
    val endOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth())

    val youPaidThisMonth = filteredPayments
        .sumOf { BigDecimal.valueOf(it.amount) }

    val youSpentThisMonth = filteredExpenses
        .filter { expense ->
            val expenseDate = expense.timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            expenseDate in startOfMonth..endOfMonth
        }
        .sumOf { (it.owingAmounts[currentUserID]?.toBigDecimal() ?: BigDecimal.ZERO) }

    val totalYouSpentThisMonth = youPaidThisMonth + youSpentThisMonth

    val houseSpentThisMonth = expenses
        .filter { expense ->
            val expenseDate = expense.timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            expenseDate in startOfMonth..endOfMonth
        }
        .sumOf { BigDecimal.valueOf(it.amount) }


    val averageRatings = currentWeekUserChoresForUser.map { chore ->
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


    val roundedTotalAverageRating = BigDecimal(totalAverageRating.toDouble())
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()



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
                "Your spending history this month:",
                modifier = Modifier
                    .padding(top = 52.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .height(150.dp)
                    .width(300.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Row to display spending by you this month
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, top = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = purple_primary
                        )
                        Text(
                            text = "You spent:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(90.dp))
                        val scaledTotalYouSpentThisMonth = totalYouSpentThisMonth.setScale(2, RoundingMode.HALF_EVEN)

                        Text(
                            text = "$$scaledTotalYouSpentThisMonth",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    // Row to display spending by the house this month

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = purple_primary
                        )

                        Text(
                            text = "Household spent:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        val scaledHouseSpentThisMonth = houseSpentThisMonth.setScale(2, RoundingMode.HALF_EVEN)

                        Text(
                            text = "$$scaledHouseSpentThisMonth",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Text(
                "Your chore ratings this week:",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(130.dp)
                    .width(300.dp)
            ) {
                if (averageRatings.isEmpty()) {
                    Text(
                        "You have no chore ratings yet.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 16.dp, start = 60.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                } else {
                    LazyColumn {
                        items(averageRatings.size) { index ->
                            DisplayEachChore(currentWeekUserChoresForUser, index, averageRatings[index])
                        }
                    }
                }
            }
            WeeklyChoreRateSurface(users, chores)

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
