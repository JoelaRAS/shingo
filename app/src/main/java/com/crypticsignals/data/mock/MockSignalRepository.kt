package com.crypticsignals.data.mock

import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.repository.SignalRepository
import kotlinx.coroutines.delay

class MockSignalRepository : SignalRepository {
    override suspend fun getSignalsForTrader(traderId: String): List<Signal> {
        delay(500)
        return MockData.signals.filter { it.traderId == traderId }
    }

    override suspend fun getSignalById(id: String): Signal? {
        delay(500)
        return MockData.signals.firstOrNull { it.id == id }
    }
}
