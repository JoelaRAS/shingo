package com.crypticsignals.data.repository

import com.crypticsignals.data.model.Signal

interface SignalRepository {
    suspend fun getSignalsForTrader(traderId: String): List<Signal>
    suspend fun getSignalById(id: String): Signal?
}
