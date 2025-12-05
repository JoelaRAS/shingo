package com.crypticsignals.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.crypticsignals.data.model.Trader
import com.crypticsignals.data.repository.TraderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TradersUiState(
    val isLoading: Boolean = false,
    val traders: List<Trader> = emptyList(),
    val error: String? = null
)

class TradersViewModel(
    private val traderRepository: TraderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TradersUiState(isLoading = true))
    val uiState: StateFlow<TradersUiState> = _uiState

    init {
        loadTraders()
    }

    fun refresh() = loadTraders()

    private fun loadTraders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { traderRepository.getTraders() }
                .onSuccess { traders -> _uiState.update { it.copy(isLoading = false, traders = traders) } }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Erreur inconnue") }
                }
        }
    }

    companion object {
        fun provideFactory(repository: TraderRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { TradersViewModel(repository) }
        }
    }
}
