package com.sample.wanandroidclean.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cookie_prefs")

/**
 * Optimized Cookie storage using DataStore with an in-memory cache to avoid blocking.
 * The CoroutineScope is now injected for better testability and lifecycle management.
 */
class CookieStorage(
    private val context: Context,
    private val scope: CoroutineScope
) {

    private val cookieCache = ConcurrentHashMap<String, List<Cookie>>()

    init {
        // Pre-load cookies into memory from DataStore
        scope.launch {
            try {
                val preferences = context.dataStore.data.first()
                preferences.asMap().forEach { (key, value) ->
                    if (value is String) {
                        val host = key.name
                        val baseUrl = HttpUrl.Builder()
                            .scheme("https")
                            .host(host)
                            .build()
                        val cookies = value.split("|").mapNotNull {
                            Cookie.parse(baseUrl, it)
                        }
                        cookieCache[host] = cookies
                    }
                }
            } catch (e: Exception) {
                // Handle potential errors during initialization
            }
        }
    }

    /**
     * Non-blocking save: updates memory immediately and persists in background.
     */
    fun saveCookies(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val currentCookies = cookieCache[host]?.toMutableList() ?: mutableListOf()
        
        // Merge new cookies with existing ones
        cookies.forEach { newCookie ->
            currentCookies.removeAll { it.name == newCookie.name }
            currentCookies.add(newCookie)
        }
        
        // Update memory cache
        cookieCache[host] = currentCookies

        // Persist to DataStore asynchronously
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(host)] = currentCookies.joinToString("|") { it.toString() }
            }
        }
    }

    /**
     * Fast return from memory cache, never blocks the caller.
     */
    fun getCookies(url: HttpUrl): List<Cookie> {
        return cookieCache[url.host] ?: emptyList()
    }
}
