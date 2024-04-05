package org.housemate.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.housemate.domain.model.User
import org.housemate.domain.repositories.AuthRepository
import org.housemate.domain.repositories.GroupRepository
import org.housemate.domain.repositories.UserRepository
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _logoutState = MutableStateFlow<Boolean?>(null)
    val logoutState: StateFlow<Boolean?> = _logoutState

    private val _deleteAccountState = MutableStateFlow<DeleteAccountResult?>(null)
    val deleteAccountState: StateFlow<DeleteAccountResult?> = _deleteAccountState

    private val _username = MutableStateFlow<String?>("")
    val username: StateFlow<String?> = _username

    private val _members = MutableStateFlow<List<User?>>(emptyList())
    val members: StateFlow<List<User?>> = _members

    fun logout() {
        viewModelScope.launch {
            val success = authRepository.logout()
            _logoutState.value = success
        }
    }

    fun getUsername(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _username.value = user?.username
        }
    }

    private val _groupName = MutableStateFlow<String?>(null)
    val groupName: StateFlow<String?> = _groupName

    fun fetchUserGroupName(userId: String) {
        viewModelScope.launch {
            try {
                val groupCode = userRepository.getGroupCodeForUser(userId)
                if (groupCode != null) {
                    val groupName = groupRepository.getGroupName(groupCode)
                    _groupName.value = groupName
                } else {
                    Log.e(TAG, "Group code not found for user with ID: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user's group name", e)
            }
        }
    }

    fun fetchGroupMembers(userId: String) {
        viewModelScope.launch {
            try {
                val members = groupRepository.fetchAllGroupMembers(userId)
                if (members != null) {
                    _members.value = members
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching members in group", e)
            }
        }
    }

    // Function to reset logout state
    fun resetLogoutState() {
        _logoutState.value = null
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            val result = authRepository.deleteAccount(password)
            _deleteAccountState.value = result

            if (result == DeleteAccountResult.Success) {
                _logoutState.value = true
            }
        }
    }
    // Function to reset delete account state
    fun resetDeleteAccountState() {
        _deleteAccountState.value = null
    }
}

sealed class DeleteAccountResult {
    object Success : DeleteAccountResult()
    object IncorrectPassword : DeleteAccountResult()
    object Error : DeleteAccountResult()
}