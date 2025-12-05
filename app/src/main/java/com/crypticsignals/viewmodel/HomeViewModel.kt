package com.crypticsignals.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.crypticsignals.data.mock.MockData
import com.crypticsignals.data.mock.MockSignalRepository
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.Trader
import com.crypticsignals.data.repository.SignalRepository
import com.crypticsignals.data.repository.TraderRepository
import com.crypticsignals.domain.HomeStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class HomeUiState(
    val isLoading: Boolean = false,
    val stats: HomeStats? = null,
    val featuredTrader: Trader? = null,
    val highlight: String? = null
)

class HomeViewModel(
    private val traderRepository: TraderRepository,
    private val signalRepository: SignalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(300)
            runCatching {
                val traders = traderRepository.getTraders()
                val signals = when (signalRepository) {
                    is MockSignalRepository -> MockData.signals
                    else -> traders.flatMap { trader -> signalRepository.getSignalsForTrader(trader.id) }
                }
                val activeSignals = signals.filter { it.status == SignalStatus.ACTIVE }
                val expiringSoon = activeSignals.count { it.expiresAt - System.currentTimeMillis() < 6 * 60 * 60 * 1000 }
                val avgConfidence = activeSignals.map { it.confidence }.average().roundToInt()
                val topTrader = traders.maxByOrNull { it.winRate }
                HomeUiState(
                    isLoading = false,
                    stats = HomeStats(
                        totalTraders = traders.size,
                        activeSignals = activeSignals.size,
                        expiringSoon = expiringSoon,
                        avgConfidence = avgConfidence
                    ),
                    featuredTrader = topTrader,
                    highlight = "Mode offline sécurisé — signaux encryptés localement."
                )
            }.onSuccess { computedState ->
                _uiState.value = computedState
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, highlight = throwable.message ?: "Erreur inattendue") }
            }
        }
    }

    companion object {
        fun provideFactory(
            traderRepository: TraderRepository,
            signalRepository: SignalRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(traderRepository, signalRepository) }
        }
    }
}
