package com.crypticsignals.data.mock

import com.crypticsignals.data.model.Direction
import com.crypticsignals.data.model.EntryType
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.TakeProfit
import com.crypticsignals.data.model.Trader
import kotlin.random.Random

object MockData {
    private val now = System.currentTimeMillis()

    val traders = listOf(
        Trader(
            id = "t1",
            name = "Cipher Alpha",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&h=200",
            bio = "BTC Swing Specialist. Low frequency, institutional grade setups.",
            winRate = 87,
            roi = 420,
            totalSignals = 142,
            activeSubscribers = 1250,
            isUserSubscribed = true,
            price = 49,
            rating = 4.9,
            reviewsCount = 312
        ),
        Trader(
            id = "t3",
            name = "Quantum Whale",
            avatarUrl = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=200&h=200",
            bio = "On-chain flow analysis. I see what whales do before you do.",
            winRate = 72,
            roi = 310,
            totalSignals = 310,
            activeSubscribers = 890,
            isUserSubscribed = true,
            price = 99,
            rating = 4.8,
            reviewsCount = 156
        ),
        Trader(
            id = "t5",
            name = "Altcoin Queen",
            avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&h=200",
            bio = "Hunting 100x gems in the micro-cap forest.",
            winRate = 55,
            roi = 1250,
            totalSignals = 420,
            activeSubscribers = 5000,
            isUserSubscribed = true,
            price = 35,
            rating = 4.6,
            reviewsCount = 890
        ),
        Trader(
            id = "t2",
            name = "Velocity FX",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&h=200",
            bio = "Scalping ETH & Solana. High volatility hunter.",
            winRate = 64,
            roi = 185,
            totalSignals = 850,
            activeSubscribers = 3400,
            isUserSubscribed = false,
            price = 29,
            rating = 4.5,
            reviewsCount = 1042
        ),
        Trader(
            id = "t4",
            name = "Satoshi's Ghost",
            avatarUrl = "https://images.unsplash.com/photo-1527980965255-d3b416303d12?auto=format&fit=crop&w=200&h=200",
            bio = "Macro trends and cycle analysis. Playing the long game.",
            winRate = 92,
            roi = 850,
            totalSignals = 45,
            activeSubscribers = 5600,
            isUserSubscribed = false,
            price = 149,
            rating = 5.0,
            reviewsCount = 2400
        ),
        Trader(
            id = "t6",
            name = "DeFi Degen",
            avatarUrl = "https://images.unsplash.com/photo-1633332755192-727a05c4013d?auto=format&fit=crop&w=200&h=200",
            bio = "High risk, high reward. Yield farming and loops.",
            winRate = 45,
            roi = -12,
            totalSignals = 90,
            activeSubscribers = 200,
            isUserSubscribed = false,
            price = 15,
            rating = 3.2,
            reviewsCount = 45
        ),
        Trader(
            id = "t7",
            name = "Forex Converter",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&h=200",
            bio = "Applying traditional forex strategies to crypto markets.",
            winRate = 68,
            roi = 140,
            totalSignals = 1200,
            activeSubscribers = 850,
            isUserSubscribed = false,
            price = 59,
            rating = 4.1,
            reviewsCount = 120
        ),
        Trader(
            id = "t8",
            name = "AI Signals Bot",
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=200&h=200",
            bio = "Algorithmic trading based on sentiment analysis.",
            winRate = 75,
            roi = 300,
            totalSignals = 5000,
            activeSubscribers = 10000,
            isUserSubscribed = false,
            price = 19,
            rating = 4.3,
            reviewsCount = 3000
        )
    )

    private fun generateHistory(traderId: String, count: Int, volatility: Double, winBias: Double): List<Signal> {
        val signals = mutableListOf<Signal>()
        var currentTime = now
        val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "AVAX/USDT", "BNB/USDT")

        repeat(count) { index ->
            val isWin = Random.nextDouble() < winBias
            val pnl = if (isWin) {
                Random.nextDouble(volatility) + (volatility * 0.2)
            } else {
                -Random.nextDouble(volatility * 0.6)
            }
            currentTime -= (1000 * 60 * 60 * (Random.nextDouble() * 48 + 4)).toLong()
            signals += Signal(
                id = "${traderId}_hist_$index",
                traderId = traderId,
                pair = pairs.random(),
                direction = if (Random.nextBoolean()) Direction.LONG else Direction.SHORT,
                entryType = EntryType.LIMIT,
                entryPrice = 100 + Random.nextDouble() * 60000,
                stopLoss = 90.0,
                takeProfits = emptyList(),
                timeframe = if (Random.nextBoolean()) "H4" else "H1",
                confidence = (Random.nextInt(5) + 5),
                createdAt = currentTime,
                expiresAt = currentTime + 1000 * 60 * 60 * 4,
                status = SignalStatus.EXPIRED,
                resultPnl = String.format("%.2f", pnl).toDouble()
            )
        }
        return signals.sortedByDescending { it.createdAt }
    }

    private val t1Hist = generateHistory("t1", 30, 8.0, 0.85)
    private val t2Hist = generateHistory("t2", 40, 5.0, 0.60)
    private val t3Hist = generateHistory("t3", 25, 12.0, 0.70)
    private val t4Hist = generateHistory("t4", 15, 25.0, 0.90)
    private val t5Hist = generateHistory("t5", 45, 30.0, 0.55)
    private val t6Hist = generateHistory("t6", 20, 10.0, 0.40)
    private val t7Hist = generateHistory("t7", 35, 3.0, 0.65)
    private val t8Hist = generateHistory("t8", 60, 2.5, 0.75)

    val signals = buildList {
        add(
            Signal(
                id = "s1",
                traderId = "t1",
                pair = "BTC/USDT",
                direction = Direction.LONG,
                entryType = EntryType.LIMIT,
                entryPrice = 64200.0,
                stopLoss = 63000.0,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 65500.0, percentage = 2.02),
                    TakeProfit(level = 2, price = 67000.0, percentage = 4.36),
                    TakeProfit(level = 3, price = 70000.0, percentage = 9.03)
                ),
                timeframe = "H4",
                confidence = 9,
                createdAt = now - 2 * 60 * 60 * 1000,
                expiresAt = now + 24 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s2",
                traderId = "t1",
                pair = "SOL/USDT",
                direction = Direction.SHORT,
                entryType = EntryType.MARKET,
                entryPrice = 145.50,
                stopLoss = 148.00,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 140.00, percentage = 3.78),
                    TakeProfit(level = 2, price = 135.00, percentage = 7.21)
                ),
                timeframe = "H1",
                confidence = 7,
                createdAt = now - 30 * 60 * 1000,
                expiresAt = now + 8 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s7",
                traderId = "t3",
                pair = "LINK/USDT",
                direction = Direction.LONG,
                entryType = EntryType.LIMIT,
                entryPrice = 18.20,
                stopLoss = 17.50,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 19.50, percentage = 7.1),
                    TakeProfit(level = 2, price = 21.00, percentage = 15.3)
                ),
                timeframe = "D1",
                confidence = 9,
                createdAt = now - 5 * 60 * 60 * 1000,
                expiresAt = now + 48 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s8",
                traderId = "t5",
                pair = "PEPE/USDT",
                direction = Direction.LONG,
                entryType = EntryType.MARKET,
                entryPrice = 0.00000850,
                stopLoss = 0.00000780,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 0.00001000, percentage = 17.6),
                    TakeProfit(level = 2, price = 0.00001500, percentage = 76.4)
                ),
                timeframe = "M15",
                confidence = 6,
                createdAt = now - 10 * 60 * 1000,
                expiresAt = now + 4 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s9",
                traderId = "t5",
                pair = "WIF/USDT",
                direction = Direction.SHORT,
                entryType = EntryType.MARKET,
                entryPrice = 3.20,
                stopLoss = 3.50,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 2.80, percentage = 12.5),
                ),
                timeframe = "H1",
                confidence = 5,
                createdAt = now - 45 * 60 * 1000,
                expiresAt = now + 6 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s3",
                traderId = "t2",
                pair = "ETH/USDT",
                direction = Direction.LONG,
                entryType = EntryType.MARKET,
                entryPrice = 3400.0,
                stopLoss = 3350.0,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 3450.0, percentage = 1.47),
                    TakeProfit(level = 2, price = 3500.0, percentage = 2.94),
                ),
                timeframe = "M15",
                confidence = 6,
                createdAt = now - 15 * 60 * 1000,
                expiresAt = now + 1 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s10",
                traderId = "t2",
                pair = "XRP/USDT",
                direction = Direction.SHORT,
                entryType = EntryType.LIMIT,
                entryPrice = 0.62,
                stopLoss = 0.63,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 0.60, percentage = 3.2),
                ),
                timeframe = "H1",
                confidence = 7,
                createdAt = now - 20 * 60 * 1000,
                expiresAt = now + 12 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s11",
                traderId = "t4",
                pair = "BTC/USDT",
                direction = Direction.LONG,
                entryType = EntryType.LIMIT,
                entryPrice = 58000.0,
                stopLoss = 55000.0,
                takeProfits = listOf(
                    TakeProfit(level = 1, price = 75000.0, percentage = 29.3),
                    TakeProfit(level = 2, price = 82000.0, percentage = 41.3)
                ),
                timeframe = "W1",
                confidence = 10,
                createdAt = now - 100 * 60 * 60 * 1000,
                expiresAt = now + 7 * 24 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )

        addAll(t1Hist)
        addAll(t2Hist)
        addAll(t3Hist)
        addAll(t4Hist)
        addAll(t5Hist)
        addAll(t6Hist)
        addAll(t7Hist)
        addAll(t8Hist)
    }
}
