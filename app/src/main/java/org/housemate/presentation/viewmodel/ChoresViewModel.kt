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
import javax.inject.Inject

@HiltViewModel
class ChoresViewModel @Inject constructor(
    private val choreRepository: ChoreRepository
): ViewModel() {

    private val _chores = MutableStateFlow<List<Chore>>(emptyList())
    val chores: StateFlow<List<Chore>> = _chores

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Initialize the ViewModel and fetch all chores for all users
        getAllChores()
    }

    private fun getAllChores() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val allChores = choreRepository.getAllChores().await()
                _chores.value = allChores
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred"
            }
        }
    }
    fun createChore(chore: Chore) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                choreRepository.createChore(chore).await()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred"
            }
        }
    }

    fun getChoresByUserId(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val chores = choreRepository.getChoresByUserId(userId).await()
                _chores.value = chores
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred"
            }
        }
    }

    fun updateChore(chore: Chore) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                choreRepository.updateChore(chore).await()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred"
            }
        }
    }

    fun deleteChore(choreId: String, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                choreRepository.deleteChore(choreId, userId).await()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "An error occurred"
            }
        }
    }
}