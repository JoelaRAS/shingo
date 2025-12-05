package com.crypticsignals.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crypticsignals.data.model.PrivyUser
import com.crypticsignals.data.privy.PrivyClient
import com.crypticsignals.data.privy.PrivySession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isConnecting: Boolean = false,
    val isRestoring: Boolean = false,
    val isConnected: Boolean = false,
    val user: PrivyUser? = null,
    val sessionToken: String? = null,
    val sessionExpiresAt: Long? = null,
    val error: String? = null
)

class AuthViewModel(
    private val client: PrivyClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun restore() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRestoring = true, error = null)
            val restored = runCatching { client.restoreSession() }.getOrNull()
            if (restored != null) {
                applySession(restored, isConnecting = false, isRestoring = false)
            } else {
                _uiState.value = AuthUiState(isRestoring = false)
            }
        }
    }

    fun connect() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConnecting = true, isRestoring = false, error = null)
            runCatching { client.connect() }
                .onSuccess { session ->
                    applySession(session, isConnecting = false, isRestoring = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        error = it.message ?: "Unable to connect"
                    )
                }
        }
    }

    fun refreshSession() {
        viewModelScope.launch {
            val current = _uiState.value
            val refreshed = runCatching {
                client.refreshSession(
                    current.sessionToken?.let {
                        PrivySession(
                            token = it,
                            user = current.user ?: return@runCatching null,
                            expiresAt = current.sessionExpiresAt ?: 0L
                        )
                    }
                )
            }.getOrNull()
            if (refreshed != null) applySession(refreshed, isConnecting = false, isRestoring = false)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConnecting = true)
            runCatching { client.disconnect() }
            _uiState.value = AuthUiState(isConnecting = false, isConnected = false, user = null)
        }
    }

    private fun applySession(session: PrivySession, isConnecting: Boolean, isRestoring: Boolean) {
        _uiState.value = AuthUiState(
            isConnecting = isConnecting,
            isRestoring = isRestoring,
            isConnected = true,
            user = session.user,
            sessionToken = session.token,
            sessionExpiresAt = session.expiresAt,
            error = null
        )
    }

    companion object {
        fun provideFactory(client: PrivyClient): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(client) as T
            }
        }
    }
}
