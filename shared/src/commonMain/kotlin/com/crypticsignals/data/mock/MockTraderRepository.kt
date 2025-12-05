package com.crypticsignals.data.mock

import com.crypticsignals.data.model.Trader
import com.crypticsignals.data.repository.TraderRepository
import kotlinx.coroutines.delay

class MockTraderRepository : TraderRepository {
    override suspend fun getTraders(): List<Trader> {
        delay(500)
        return MockData.traders
    }

    override suspend fun getTraderById(id: String): Trader? {
        delay(300)
        return MockData.traders.find { it.id == id }
    }
}
