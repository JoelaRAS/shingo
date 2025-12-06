package com.crypticsignals.data.mock

import com.crypticsignals.data.model.Direction
import com.crypticsignals.data.model.EntryType
import com.crypticsignals.data.model.Signal
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.data.model.TakeProfit
import com.crypticsignals.data.model.Trader
import kotlin.random.Random
import java.util.Locale

object MockData {
    private val now = System.currentTimeMillis()

    val traders = listOf(
        Trader(
            id = "t1",
            name = "Cipher Alpha",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&h=200",
            bio = "BTC Swing Specialist. Low frequency, institutional grade setups.",
            winRate = 87,
            totalSignals = 142,
            activeSubscribers = 1250,
            isUserSubscribed = true,
            roi = 420,
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
            totalSignals = 310,
            activeSubscribers = 890,
            isUserSubscribed = true,
            roi = 310,
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
            totalSignals = 420,
            activeSubscribers = 5000,
            isUserSubscribed = true,
            roi = 1250,
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
            totalSignals = 850,
            activeSubscribers = 3400,
            isUserSubscribed = false,
            roi = 185,
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
            totalSignals = 45,
            activeSubscribers = 5600,
            isUserSubscribed = false,
            roi = 850,
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
            totalSignals = 90,
            activeSubscribers = 200,
            isUserSubscribed = false,
            roi = -12,
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
            totalSignals = 1200,
            activeSubscribers = 850,
            isUserSubscribed = false,
            roi = 140,
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
            totalSignals = 5000,
            activeSubscribers = 10000,
            isUserSubscribed = false,
            roi = 300,
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
                resultPnl = String.format(Locale.US, "%.2f", pnl).toDouble()
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
        addAll(t1Hist)
        addAll(t2Hist)
        addAll(t3Hist)
        addAll(t4Hist)
        addAll(t5Hist)
        addAll(t6Hist)
        addAll(t7Hist)
        addAll(t8Hist)
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
                stopLoss = 152.0,
                takeProfits = listOf(
                    TakeProfit(1, 140.0, 25),
                    TakeProfit(2, 130.0, 35),
                    TakeProfit(3, 120.0, 40)
                ),
                timeframe = "H1",
                confidence = 7,
                createdAt = now - 5 * 60 * 60 * 1000,
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
                entryPrice = 3300.0,
                stopLoss = 3200.0,
                takeProfits = listOf(
                    TakeProfit(1, 3400.0, 30),
                    TakeProfit(2, 3550.0, 30),
                    TakeProfit(3, 3700.0, 40)
                ),
                timeframe = "H4",
                confidence = 8,
                createdAt = now - 6 * 60 * 60 * 1000,
                expiresAt = now + 2 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
        add(
            Signal(
                id = "s4",
                traderId = "t3",
                pair = "AVAX/USDT",
                direction = Direction.SHORT,
                entryType = EntryType.LIMIT,
                entryPrice = 44.2,
                stopLoss = 47.0,
                takeProfits = listOf(
                    TakeProfit(1, 41.0, 33),
                    TakeProfit(2, 38.0, 33),
                    TakeProfit(3, 35.0, 34)
                ),
                timeframe = "H1",
                confidence = 6,
                createdAt = now - 12 * 60 * 60 * 1000,
                expiresAt = now + 12 * 60 * 60 * 1000,
                status = SignalStatus.ACTIVE
            )
        )
    }

    val traderPerformance = mapOf(
        "t1" to t1Hist,
        "t2" to t2Hist,
        "t3" to t3Hist,
        "t4" to t4Hist,
        "t5" to t5Hist,
        "t6" to t6Hist,
        "t7" to t7Hist,
        "t8" to t8Hist
    )
}
