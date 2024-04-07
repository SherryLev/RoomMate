package org.housemate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Chore
import org.housemate.domain.model.User
import org.housemate.domain.repositories.ChoreRepository
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

    private val _choreTypes = MutableStateFlow<List<String>>(emptyList())
    val choreTypes: StateFlow<List<String>> = _choreTypes

    private val _choreCategories = MutableStateFlow<List<String>>(emptyList())
    val choreCategories: StateFlow<List<String>> = _choreCategories
    init {
        getAllChores()
        fetchAllHousemates()
        fetchCurrentUserId()
        createDefaultChoreCategories()
        fetchChoreTypes()
        fetchChoreCategories()
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
                val allChores = choreRepository.getGroupChores().await()
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
                println("Failed to add chore: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addChoreType(newChore: String, userId: String) {
        viewModelScope.launch {
            try {
                choreRepository.addChoreType(newChore, userId).await()
                fetchChoreTypes()
            } catch (e: Exception) {
                println("Failed to add chore type: ${e.message}")
            }
        }
    }

    fun fetchChoreTypes() {
        viewModelScope.launch {
            try {
                // Call the getChoreTypes function from the repository to fetch chore types
                val fetchedChoreTypesTask = choreRepository.getChoreTypes()
                fetchedChoreTypesTask.addOnSuccessListener { fetchedChoreTypes ->
                    // Update the _choreTypes StateFlow object with the fetched chore types
                    _choreTypes.value = fetchedChoreTypes
                }.addOnFailureListener { e ->
                    // Handle failure
                    println("Failed to fetch chore types: ${e.message}")
                }
            } catch (e: Exception) {
                // Handle other exceptions
                println("Failed to fetch chore types: ${e.message}")
            }
        }
    }

    fun fetchChoreCategories() {
        viewModelScope.launch {
            try {
                // Call the getChoreCategories function from the repository to fetch chore categories
                val fetchedChoreCategoriesTask = choreRepository.getChoreCategories()
                fetchedChoreCategoriesTask.addOnSuccessListener { fetchedChoreCategories ->
                    // Update the _choreCategories StateFlow object with the fetched chore categories
                    _choreCategories.value = fetchedChoreCategories
                }.addOnFailureListener { e ->
                    println("Failed to fetch chore categories: ${e.message}")
                }
            } catch (e: Exception) {
                println("Failed to fetch chore categories: ${e.message}")
            }
        }
    }

    fun addChoreCategory(newCategory: String, userId: String) {
        viewModelScope.launch {
            try {
                // Call the addChoreCategories function from the repository to add a chore category
               choreRepository.addCategory(newCategory, userId).await()
                // Since the chore category is added successfully, we can fetch the updated list of chore categories
                fetchChoreCategories()

            } catch (e: Exception) {
                // Handle other exceptions
                println("Failed to add chore category: ${e.message}")
            }
        }
    }

    fun createDefaultChoreCategories() {
        viewModelScope.launch {
            try {
                choreRepository.createDefaultChoresAndCategories()
            } catch (e: Exception) {
                // Handle the exception
                println("Failed to create default chore categories: ${e.message}")
            }
        }
    }

    fun deleteMultipleChores(chorePrefix: String, userId: String) {
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
                choreRepository.updateChoreRating(chore, newRating, userId).await()
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}