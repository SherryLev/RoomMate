package org.housemate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Chore
import org.housemate.domain.model.User
import org.housemate.domain.repositories.ChoreRepository
import org.housemate.domain.repositories.ExpenseRepository
import org.housemate.domain.repositories.GroupRepository
import org.housemate.domain.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class ChoresViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _chores = MutableStateFlow<List<Chore>>(emptyList())
    val chores: StateFlow<List<Chore>> = _chores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _housemates = MutableStateFlow<List<User>>(emptyList())
    val housemates: StateFlow<List<User>> = _housemates

    init {
        getAllChores()
        fetchAllHousemates()
        fetchCurrentUserId()
    }

    private val _dialogDismissed = MutableStateFlow(false)
    val dialogDismissed: StateFlow<Boolean> = _dialogDismissed

    fun setDialogDismissed(dismissed: Boolean) {
        getAllChores()
        _dialogDismissed.value = dismissed
    }

    fun fetchCurrentUserId() {
        viewModelScope.launch {
            _userId.value = userRepository.getCurrentUserId()
        }
    }
    fun fetchCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = _userId.value?.let { userRepository.getUserById(it) }
        }
    }
    fun fetchAllHousemates() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = _userId.value ?: return@launch
                val housemates = groupRepository.fetchAllGroupMembers(userId)
                if (housemates != null) {
                    _housemates.value = housemates
                } else {
                    _error.value = "Failed to fetch housemates"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllChores() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val allChores = choreRepository.getAllChores().await()
                _chores.value = allChores
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addChore(chore: Chore) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                choreRepository.createChore(chore).await()
                println("Chore added successfully")
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                println("Failed to add expense: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getChoresByUserId(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val chores = choreRepository.getChoresByUserId(userId).await()
                _chores.value = chores
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateChore(chore: Chore) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                choreRepository.updateChore(chore).await()
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteChore(choreId: String, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                choreRepository.deleteChore(choreId, userId).await()
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMultipleChores(chorePrefix: String, userId: String){
        viewModelScope.launch {
            try {
                choreRepository.deleteMultipleChores(chorePrefix, userId).await()
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
                getAllChores()
            }
        }
    }
    fun updateChoreRating(chore: Chore, newRating: Float, userId: String) {
        viewModelScope.launch {
            try {
                choreRepository.updateChoreRating(chore, newRating,userId).await()
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }

    }
}
