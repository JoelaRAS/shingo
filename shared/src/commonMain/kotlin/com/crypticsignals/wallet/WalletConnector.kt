package com.crypticsignals.wallet

interface WalletConnector {
    suspend fun connect(): WalletAccount
    suspend fun signAndSendTransaction(tx: ByteArray): String
    suspend fun signMessage(message: ByteArray): ByteArray
    suspend fun disconnect()

    val isConnected: Boolean
    val address: String?
}

data class WalletAccount(
    val address: String,
    val publicKey: ByteArray
)
