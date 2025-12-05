package com.crypticsignals.data.repository

import com.crypticsignals.data.model.Trader

interface TraderRepository {
    suspend fun getTraders(): List<Trader>
    suspend fun getTraderById(id: String): Trader?
}
