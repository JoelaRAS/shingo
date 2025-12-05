package com.crypticsignals.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Trader(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val bio: String,
    val winRate: Int,
    val totalSignals: Int,
    val activeSubscribers: Int,
    val isUserSubscribed: Boolean,
    val roi: Int = 0,
    val price: Int = 0,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0
)
