package com.crypticsignals.domain

data class HomeStats(
    val totalTraders: Int,
    val activeSignals: Int,
    val expiringSoon: Int,
    val avgConfidence: Int
)
