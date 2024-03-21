package org.housemate.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.housemate.domain.repositories.AuthRepository
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _logoutState = MutableStateFlow<Boolean?>(null)
    val logoutState: StateFlow<Boolean?> = _logoutState

    fun logout() {
        viewModelScope.launch {
            val success = authRepository.logout()
            _logoutState.value = success
        }
    }

    // Function to reset logout state
    fun resetLogoutState() {
        _logoutState.value = null
    }
}