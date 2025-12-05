package com.crypticsignals.data.privy

import com.crypticsignals.data.model.PrivyUser
import kotlinx.coroutines.delay
import kotlin.random.Random

data class PrivySession(
    val token: String,
    val user: PrivyUser,
    val expiresAt: Long
) {
    companion object {
        const val REFRESH_MS = 30 * 60 * 1000L

        fun fake(appId: String): PrivySession {
            val now = System.currentTimeMillis()
            return PrivySession(
                token = "privy_${appId}_${Random.nextLong().toString(16)}",
                user = PrivyUser(
                    id = "user-$now",
                    walletAddress = "0xA1C8...9F3D",
                    chain = "Solana",
                    label = "Privy Wallet"
                ),
                expiresAt = now + REFRESH_MS
            )
        }
    }
}

interface PrivyClient {
    suspend fun connect(): PrivySession
    suspend fun restoreSession(): PrivySession?
    suspend fun refreshSession(current: PrivySession?): PrivySession?
    suspend fun disconnect()
}

/**
 * Persistent fake client that mimics Privy session lifecycle with local storage.
 * Replace this with the official Privy Android client when available.
 */
class PersistentPrivyClient(
    private val storage: PrivySessionStorage,
    private val appId: String,
    private val remote: PrivyRemoteClient
) : PrivyClient {
    override suspend fun connect(): PrivySession {
        delay(300)
        val session = remote.fetchSession(appId)
        storage.save(session)
        return session
    }

    override suspend fun restoreSession(): PrivySession? {
        delay(150)
        val stored = storage.read()
        return stored?.takeIf { it.expiresAt > System.currentTimeMillis() }
    }

    override suspend fun refreshSession(current: PrivySession?): PrivySession? {
        delay(120)
        val base = current ?: storage.read() ?: return null
        val refreshed = remote.refreshSession(base)
        storage.save(refreshed)
        return refreshed
    }

    override suspend fun disconnect() {
        storage.clear()
    }
}
