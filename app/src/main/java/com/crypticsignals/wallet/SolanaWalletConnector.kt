package com.crypticsignals.wallet

import android.net.Uri
import com.funkatronics.encoders.Base58
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.Solana
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.mobilewalletadapter.clientlib.successPayload

class SolanaWalletConnector(
    private val resultSender: ActivityResultSender
) : WalletConnector {

    private val identity = ConnectionIdentity(
        identityUri = Uri.parse("https://crypticsignals.app"),
        iconUri = Uri.parse("favicon.ico"),
        identityName = "CrypticSignals"
    )

    private val adapter = MobileWalletAdapter(connectionIdentity = identity).apply {
        blockchain = Solana.Testnet
    }
    private var currentAccount: WalletAccount? = null

    override val isConnected: Boolean
        get() = currentAccount != null

    override val address: String?
        get() = currentAccount?.address

    override suspend fun connect(): WalletAccount {
        val result = adapter.connect(resultSender)
        return when (result) {
            is TransactionResult.Success -> {
                val accountBytes = result.authResult.accounts.firstOrNull()?.publicKey
                    ?: throw IllegalStateException("Aucun compte renvoyé par le wallet.")
                val account = WalletAccount(
                    address = Base58.encodeToString(accountBytes),
                    publicKey = accountBytes
                )
                currentAccount = account
                account
            }

            is TransactionResult.NoWalletFound -> {
                throw IllegalStateException("Aucun wallet compatible Solana Mobile trouvé sur l'appareil.")
            }

            is TransactionResult.Failure -> {
                throw IllegalStateException(
                    "Erreur de connexion au wallet : ${result.e.message ?: "inconnue"}"
                )
            }
        }
    }

    override suspend fun signAndSendTransaction(tx: ByteArray): String {
        val result = adapter.transact(resultSender) {
            signAndSendTransactions(arrayOf(tx))
        }
        return when (result) {
            is TransactionResult.Success -> {
                val sig = result.successPayload?.signatures?.firstOrNull()
                    ?: throw IllegalStateException("Aucune signature renvoyée par le wallet.")
                Base58.encodeToString(sig)
            }

            is TransactionResult.NoWalletFound -> {
                throw IllegalStateException("Aucun wallet compatible pour signer la transaction.")
            }

            is TransactionResult.Failure -> {
                throw IllegalStateException(
                    "Erreur signAndSendTransaction : ${result.e.message ?: "inconnue"}"
                )
            }
        }
    }

    override suspend fun signMessage(message: ByteArray): ByteArray {
        val result = adapter.transact(resultSender) { authResult ->
            signMessagesDetached(
                arrayOf(message),
                arrayOf(authResult.accounts.firstOrNull()?.publicKey
                    ?: throw IllegalStateException("Aucun compte autorisé."))
            )
        }
        return when (result) {
            is TransactionResult.Success -> {
                result.successPayload
                    ?.messages
                    ?.firstOrNull()
                    ?.signatures
                    ?.firstOrNull()
                    ?: throw IllegalStateException("Aucune signature de message renvoyée.")
            }

            is TransactionResult.NoWalletFound -> {
                throw IllegalStateException("Aucun wallet compatible pour signer le message.")
            }

            is TransactionResult.Failure -> {
                throw IllegalStateException(
                    "Erreur signMessage : ${result.e.message ?: "inconnue"}"
                )
            }
        }
    }

    override suspend fun disconnect() {
        adapter.disconnect(resultSender)
        currentAccount = null
    }
}
