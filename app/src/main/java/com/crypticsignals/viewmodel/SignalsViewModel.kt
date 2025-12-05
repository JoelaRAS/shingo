package com.crypticsignals.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.Trader
import com.crypticsignals.data.repository.SignalRepository
import com.crypticsignals.data.repository.TraderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignalsUiState(
    val isLoading: Boolean = false,
    val signals: List<Signal> = emptyList(),
    val error: String? = null
)

class SignalsViewModel(
    private val signalRepository: SignalRepository,
    private val traderRepository: TraderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignalsUiState(isLoading = true))
    val uiState: StateFlow<SignalsUiState> = _uiState

    private val _selectedSignal = MutableStateFlow<Signal?>(null)
    val selectedSignal: StateFlow<Signal?> = _selectedSignal

    private val _selectedTrader = MutableStateFlow<Trader?>(null)
    val selectedTrader: StateFlow<Trader?> = _selectedTrader

    fun loadSignals(traderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val trader = traderRepository.getTraderById(traderId)
                val signals = signalRepository.getSignalsForTrader(traderId)
                    .sortedByDescending { it.createdAt }
                trader to signals
            }.onSuccess { (trader, signals) ->
                _selectedTrader.value = trader
                _uiState.update { it.copy(isLoading = false, signals = signals) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Erreur inconnue") }
            }
        }
    }

    fun getSignalById(id: String) {
        viewModelScope.launch {
            runCatching { signalRepository.getSignalById(id) }
                .onSuccess { signal ->
                    _selectedSignal.value = signal
                    signal?.let {
                        runCatching { traderRepository.getTraderById(it.traderId) }
                            .onSuccess { trader -> _selectedTrader.value = trader }
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message ?: "Impossible de récupérer le signal") }
                }
        }
    }

    companion object {
        fun provideFactory(
            signalRepository: SignalRepository,
            traderRepository: TraderRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { SignalsViewModel(signalRepository, traderRepository) }
        }
    }
}
