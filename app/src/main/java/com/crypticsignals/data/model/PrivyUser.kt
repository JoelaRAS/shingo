package com.crypticsignals.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PrivyUser(
    val id: String,
    val walletAddress: String,
    val chain: String,
    val label: String = "Privy Wallet"
)
