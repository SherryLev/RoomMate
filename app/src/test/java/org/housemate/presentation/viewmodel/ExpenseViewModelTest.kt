import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.housemate.domain.model.Expense
import org.housemate.domain.model.Group
import org.housemate.domain.model.Payment
import org.housemate.domain.model.User
import org.housemate.domain.repositories.ExpenseRepository
import org.housemate.domain.repositories.GroupRepository
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import org.housemate.domain.repositories.UserRepository
import org.junit.After


class FakeExpenseRepository : ExpenseRepository {
    private val expenses = mutableListOf<Expense>()
    private val payments = mutableListOf<Payment>()

    override suspend fun getExpenses(): List<Expense> {
        return expenses.toList()
    }

    override suspend fun addExpense(expense: Expense) {
        expenses.add(expense)
    }

    override suspend fun deleteExpenseById(expenseId: String) {
        expenses.removeAll { it.id == expenseId }
    }

    override suspend fun updateExpenseById(expenseId: String, updatedExpense: Expense) {
        val index = expenses.indexOfFirst { it.id == expenseId }
        if (index != -1) {
            expenses[index] = updatedExpense
        }
    }

    override suspend fun getPayments(): List<Payment> {
        return payments.toList()
    }

    override suspend fun addPayment(payment: Payment) {
        payments.add(payment)
    }

    override suspend fun updatePaymentById(paymentId: String, updatedPayment: Payment) {
        val index = payments.indexOfFirst { it.id == paymentId }
        if (index != -1) {
            payments[index] = updatedPayment
        }
    }

    override suspend fun deletePaymentById(paymentId: String) {
        payments.removeAll { it.id == paymentId }
    }
}

class FakeGroupRepository : GroupRepository {
    private val groups = mutableListOf<Group>()

    override suspend fun createGroup(group: Group): Boolean {
        groups.add(group)
        return true
    }

    override suspend fun addMemberToGroup(groupCode: String, memberId: String): Boolean {
        val group = groups.find { it.groupCode == groupCode }
        if (group != null && memberId !in group.members) {
            group.members = group.members.toMutableList().apply { add(memberId) }
            return true
        }
        return false
    }

    override suspend fun getGroupByCode(groupCode: String): Group? {
        return groups.find { it.groupCode == groupCode }
    }

    override suspend fun removeMemberFromGroup(groupCode: String, userId: String): Boolean {
        val group = groups.find { it.groupCode == groupCode }
        if (group != null && userId in group.members) {
            group.members = group.members.toMutableList().apply { remove(userId) }
            return true
        }
        return false
    }

    override suspend fun fetchAllGroupMembers(userId: String): List<User>? {
        val userRepository = FakeUserRepository()
        return groups.flatMap { it.members }
            .distinct()
            .mapNotNull { memberId ->
                memberId?.let { userRepository.getUserById(it) }
            }
    }

    override suspend fun isCreator(userId: String, groupCode: String): Boolean {
        val group = groups.find { it.groupCode == groupCode }
        return group?.creatorId == userId
    }

    override suspend fun createGroupName(groupCode: String, groupName: String): Boolean {
        val group = groups.find { it.groupCode == groupCode }
        if (group != null) {
            group.groupName = groupName
            return true
        }
        return false
    }

    override suspend fun getGroupName(groupCode: String): String? {
        return groups.find { it.groupCode == groupCode }?.groupName
    }
}

class FakeUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()
    private var currentUserId: String? = null

    override suspend fun addUser(user: User): Boolean {
        users[user.uid] = user
        return true
    }

    override suspend fun getUserById(userId: String): User? {
        return users[userId]
    }

    override suspend fun getCurrentUserId(): String? {
        return currentUserId
    }

    override suspend fun deleteUserById(userId: String): Boolean {
        users.remove(userId)
        return true
    }

    override suspend fun getGroupCodeForUser(userId: String): String? {
        return users[userId]?.groupCode
    }

    override suspend fun updateUserGroupCode(userId: String, newGroupCode: String): Boolean {
        users[userId]?.groupCode = newGroupCode
        return true
    }

    fun setUser(user: User) {
        currentUserId = user.uid
        users[user.uid] = user
    }
}



class ExpenseViewModelTest {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseRepository: FakeExpenseRepository
    private lateinit var groupRepository: FakeGroupRepository
    private lateinit var userRepository: FakeUserRepository

    private val testDispatcher = TestCoroutineDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        expenseRepository = FakeExpenseRepository()
        groupRepository = FakeGroupRepository()
        userRepository = FakeUserRepository()

        viewModel = ExpenseViewModel(expenseRepository, groupRepository, userRepository)
    }

    @Test
    fun `fetch current user`() = runBlocking {
        // Given
        val userId = "user123"
        val user = User(userId, "John Doe", "john@example.com")
        userRepository.setUser(user)

        // When
        viewModel.fetchCurrentUser()

        // Then
        val currentUser = viewModel.currentUser.first()
        assertEquals(user, currentUser)
    }

    @Test
    fun `add expense`() = runBlocking {
        // Given
        val expenseId = "expense123"
        val payer = "payer123"
        val description = "Expense description"
        val amount = BigDecimal("50.0")
        val owingAmounts = mapOf("user1" to BigDecimal("20.00"), "user2" to BigDecimal("30.00"))

        // When
        viewModel.addExpense(expenseId, payer, payer, description, amount, owingAmounts)

        // Then
        val expenses = expenseRepository.getExpenses()
        assertEquals(1, expenses.size)
        val addedExpense = expenses.first()
        assertEquals(expenseId, addedExpense.id)
        assertEquals(payer, addedExpense.payerId)
        assertEquals(description, addedExpense.description)
        assertEquals(amount, addedExpense.amount.toBigDecimal())
    }

    @Test
    fun `delete expense by id`() = runBlocking {
        // Given
        val expenseId = "expense123"
        val expense = Expense(expenseId, "payerName", "payer123", "description", 50.00, emptyMap())
        expenseRepository.addExpense(expense)

        // When
        viewModel.deleteExpenseById(expenseId)

        // Then
        val expenses = expenseRepository.getExpenses()
        assertEquals(0, expenses.size)
    }


    @Test
    fun `update expense by id`() = runBlocking {
        // Given
        val expenseId = "expense123"
        val updatedDescription = "Updated description"
        val updatedAmount = BigDecimal("75.0")
        val updatedOwingAmounts = mapOf("user1" to BigDecimal("30.00"), "user2" to BigDecimal("45.00"))
        val updatedExpense = Expense(expenseId, "payerNameUpdated", "payer123",updatedDescription, updatedAmount.toDouble(), updatedOwingAmounts.mapValues { it.value.toDouble() })
        expenseRepository.addExpense(updatedExpense)

        // When
        viewModel.updateExpenseById(expenseId, "payerNameUpdated", "payer123", updatedDescription, updatedAmount, updatedOwingAmounts)

        // Then
        val expenses = expenseRepository.getExpenses()
        assertEquals(1, expenses.size)
        val updatedExpenseFromRepo = expenses.first()
        assertEquals(updatedDescription, updatedExpenseFromRepo.description)
        assertEquals(updatedAmount, updatedExpenseFromRepo.amount.toBigDecimal())
        assertEquals(updatedOwingAmounts, updatedExpenseFromRepo.owingAmounts.mapValues { it.value.toBigDecimal().setScale(2) })
    }

    @Test
    fun `fetch payments`() = runBlocking {
        // Given
        val payment1 = Payment("payment1", "payer1", "payerId1", "payee1", "payeeId1", 20.00)
        val payment2 = Payment("payment2", "payer2", "payerId2", "payee2", "payeeId2",30.00)
        expenseRepository.addPayment(payment1)
        expenseRepository.addPayment(payment2)

        // When
        viewModel.fetchPayments()

        // Then
        val payments = viewModel.paymentItems.first()
        assertEquals(2, payments.size)
        assertEquals(payment1, payments[0])
        assertEquals(payment2, payments[1])
    }

    @Test
    fun `add payment`() = runBlocking {
        // Given
        val paymentId = "payment123"
        val payerId = "payer123"
        val payeeId = "payee123"
        val amount = BigDecimal("50.0")

        // When
        viewModel.addPayment(paymentId, payerId, "payerName", payeeId, "payeeName", amount)

        // Then
        val payments = expenseRepository.getPayments()
        assertEquals(1, payments.size)
        val addedPayment = payments.first()
        assertEquals(paymentId, addedPayment.id)
        assertEquals(payerId, addedPayment.payerId)
        assertEquals(payeeId, addedPayment.payeeId)
        assertEquals(amount, addedPayment.amount.toBigDecimal())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

