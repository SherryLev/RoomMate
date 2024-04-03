package org.housemate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Chore
import org.housemate.domain.repositories.ChoreRepository
import org.housemate.domain.repositories.ExpenseRepository
import org.housemate.domain.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class ChoresViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _chores = MutableStateFlow<List<Chore>>(emptyList())
    val chores: StateFlow<List<Chore>> = _chores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        getAllChores()
    }

    private val _dialogDismissed = MutableStateFlow(false)
    val dialogDismissed: StateFlow<Boolean> = _dialogDismissed

    fun setDialogDismissed(dismissed: Boolean) {
        _dialogDismissed.value = dismissed
    }

    fun fetchCurrentUserId() {
        viewModelScope.launch {
            _userId.value = userRepository.getCurrentUserId()
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
}
