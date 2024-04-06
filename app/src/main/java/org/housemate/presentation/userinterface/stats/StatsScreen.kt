package org.housemate.presentation.userinterface.stats


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
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
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.purple_primary
import org.housemate.theme.pretty_purple
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters


data class Task(val name: String)

@Composable fun DisplayEachChore(currentUserChores: List<Chore>, index: Int, rating: Float){
    val roundedTotalAverageRating = if (rating != 0.0f) {
        BigDecimal(rating.toDouble())
            .setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    } else {
        "No ratings yet"
    }

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
            text = roundedTotalAverageRating,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (rating != 0.0f) Color.DarkGray else Color.LightGray
        )
    }
}
@Composable
fun MainLayout(navController: NavController, choresViewModel: ChoresViewModel = hiltViewModel(), expenseViewModel: ExpenseViewModel = hiltViewModel()) {
    LaunchedEffect(key1 = "fetchUserIdandchores") {
        choresViewModel.fetchCurrentUserId()
        choresViewModel.getAllChores()
        choresViewModel.fetchCurrentUser()
    }

    val chores by choresViewModel.chores.collectAsState()

    val currentUserID by choresViewModel.userId.collectAsState()
    val currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val currentWeekEnd = currentWeekStart.plusDays(6)

    val currentWeekUserChores = chores.filter { chore ->
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

    val averageRatings = currentWeekUserChores.map { chore ->
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
                    .padding(16.dp)
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
                "Your chore ratings for this week:",
                modifier = Modifier
                    .padding(top = 8.dp)
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
                            DisplayEachChore(currentWeekUserChores, index, averageRatings[index])
                        }
                    }
                }
            }
            Text(
                "Your average chore rating is:",
                modifier = Modifier
                    .padding(top = 40.dp)
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
