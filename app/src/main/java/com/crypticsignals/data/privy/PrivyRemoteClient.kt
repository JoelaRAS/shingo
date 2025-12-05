package com.crypticsignals.data.privy

/**
 * Abstraction of the backend call that would hold the Privy app secret.
 * The mobile client should only receive a session/token, never the secret itself.
 */
interface PrivyRemoteClient {
    suspend fun fetchSession(appId: String): PrivySession
    suspend fun refreshSession(session: PrivySession): PrivySession
}

class MockPrivyRemoteClient : PrivyRemoteClient {
    override suspend fun fetchSession(appId: String): PrivySession {
        return PrivySession.fake(appId)
    }

    override suspend fun refreshSession(session: PrivySession): PrivySession {
        return session.copy(expiresAt = System.currentTimeMillis() + PrivySession.REFRESH_MS)
    }
}
