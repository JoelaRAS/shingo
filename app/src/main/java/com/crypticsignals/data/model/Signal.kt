package com.crypticsignals.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Signal(
    val id: String,
    val traderId: String,
    val pair: String,
    val direction: Direction,
    val entryType: EntryType,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfits: List<TakeProfit>,
    val timeframe: String,
    val confidence: Int,
    val createdAt: Long,
    val expiresAt: Long,
    val status: SignalStatus,
    val resultPnl: Double? = null
)

@Serializable
data class TakeProfit(
    val level: Int,
    val price: Double,
    val sizePct: Int = 0,
    val percentage: Double? = null
)

@Serializable
enum class Direction { LONG, SHORT }

@Serializable
enum class EntryType { LIMIT, MARKET }

@Serializable
enum class SignalStatus { ACTIVE, EXPIRED }
