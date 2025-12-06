package com.crypticsignals.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletUiState(
    val isConnected: Boolean = false,
    val address: String? = null,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)

class WalletViewModel(
    private val connector: WalletConnector
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    fun connect() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, errorMessage = null) }
            runCatching { connector.connect() }
                .onSuccess { account ->
                    _uiState.update {
                        it.copy(
                            isConnecting = false,
                            isConnected = true,
                            address = account.address,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isConnecting = false,
                            isConnected = false,
                            errorMessage = err.message ?: "Erreur de connexion au wallet"
                        )
                    }
                }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            runCatching { connector.disconnect() }
            _uiState.update { it.copy(isConnected = false, address = null, isConnecting = false, errorMessage = null) }
        }
    }

    companion object {
        fun provideFactory(connector: WalletConnector): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WalletViewModel(connector) as T
                }
            }
    }
}
