package org.housemate.presentation.viewmodel

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
import org.housemate.domain.repositories.AuthRepository
import org.housemate.domain.repositories.UserRepository
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _logoutState = MutableStateFlow<Boolean?>(null)
    val logoutState: StateFlow<Boolean?> = _logoutState

    private val _deleteAccountState = MutableStateFlow<DeleteAccountResult?>(null)
    val deleteAccountState: StateFlow<DeleteAccountResult?> = _deleteAccountState

    private val _username = MutableStateFlow<String?>("")
    val username: StateFlow<String?> = _username

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