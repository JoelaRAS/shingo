package com.crypticsignals.data.privy

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crypticsignals.data.model.PrivyUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.privyDataStore by preferencesDataStore(name = "privy_session")

class PrivySessionStorage(private val context: Context) {
    private val keyToken = stringPreferencesKey("token")
    private val keyUser = stringPreferencesKey("user_json")
    private val keyExpires = longPreferencesKey("expires")

    suspend fun save(session: PrivySession) {
        context.privyDataStore.edit { prefs ->
            prefs[keyToken] = session.token
            prefs[keyExpires] = session.expiresAt
            prefs[keyUser] = Json.encodeToString(session.user)
        }
    }

    suspend fun read(): PrivySession? {
        val prefs = context.privyDataStore.data.map { it }.first()
        val token = prefs[keyToken] ?: return null
        val expires = prefs[keyExpires] ?: return null
        val userJson = prefs[keyUser] ?: return null
        val user = runCatching { Json.decodeFromString<PrivyUser>(userJson) }.getOrNull() ?: return null
        return PrivySession(token = token, user = user, expiresAt = expires)
    }

    suspend fun clear() {
        context.privyDataStore.edit { it.clear() }
    }
}
