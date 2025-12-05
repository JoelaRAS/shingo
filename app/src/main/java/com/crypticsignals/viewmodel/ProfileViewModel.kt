package com.crypticsignals.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.crypticsignals.data.model.Direction
import com.crypticsignals.data.model.EntryType
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.TakeProfit
import com.crypticsignals.data.repository.SignalRepository
import com.crypticsignals.data.repository.TraderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val activeSignals: List<Signal> = emptyList(),
    val statusMessage: String? = null,
    val error: String? = null
)

data class SignalDraft(
    val pair: String,
    val direction: Direction,
    val entryPrice: Double,
    val stopLoss: Double,
    val tpPrices: List<Double>,
    val timeframe: String,
    val confidence: Int,
    val traderId: String? = null
)

class ProfileViewModel(
    private val signalRepository: SignalRepository,
    private val traderRepository: TraderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        refreshActiveSignals()
    }

    fun refreshActiveSignals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val traders = traderRepository.getTraders()
                traders.flatMap { trader -> signalRepository.getSignalsForTrader(trader.id) }
                    .filter { it.status == SignalStatus.ACTIVE }
            }.onSuccess { active ->
                _uiState.update { it.copy(isLoading = false, activeSignals = active) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Erreur inattendue") }
            }
        }
    }

    fun createSignal(draft: SignalDraft) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, statusMessage = null, error = null) }
            delay(300)
            val now = System.currentTimeMillis()
            val takeProfits = draft.tpPrices.mapIndexed { index, price ->
                TakeProfit(level = index + 1, price = price, sizePct = listOf(30, 30, 40).getOrElse(index) { 25 })
            }
            val signal = Signal(
                id = "draft_${UUID.randomUUID()}",
                traderId = draft.traderId ?: "samurai",
                pair = draft.pair,
                direction = draft.direction,
                entryType = EntryType.MARKET,
                entryPrice = draft.entryPrice,
                stopLoss = draft.stopLoss,
                takeProfits = takeProfits,
                timeframe = draft.timeframe,
                confidence = draft.confidence.coerceIn(0, 100),
                createdAt = now,
                expiresAt = now + 24 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    activeSignals = listOf(signal) + it.activeSignals,
                    statusMessage = "Signal créé (offline)"
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            signalRepository: SignalRepository,
            traderRepository: TraderRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProfileViewModel(signalRepository, traderRepository) }
        }
    }
}
