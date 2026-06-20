package com.sample.wanandroidclean.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cookie_prefs")

/**
 * Optimized Cookie storage using DataStore with an in-memory cache.
 */
class CookieStorage(
    private val context: Context,
    private val scope: CoroutineScope
) {

    private val cookieCache = ConcurrentHashMap<String, List<Cookie>>()

    /**
     * Exposes a Flow of all stored cookies across all hosts.
     * Useful for observing login status.
     */
    val cookies: Flow<List<Cookie>> = context.dataStore.data.map { preferences ->
        val allCookies = mutableListOf<Cookie>()
        preferences.asMap().forEach { (key, value) ->
            if (value is String) {
                val host = key.name
                val baseUrl = HttpUrl.Builder()
                    .scheme("https")
                    .host(host)
                    .build()
                value.split("|").forEach { cookieString ->
                    Cookie.parse(baseUrl, cookieString)?.let { cookie -> 
                        allCookies.add(cookie) // 修正：添加解析后的 cookie 对象而非原始字符串
                    }
                }
            }
        }
        allCookies
    }

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

    fun saveCookies(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val currentCookies = cookieCache[host]?.toMutableList() ?: mutableListOf()
        
        cookies.forEach { newCookie ->
            currentCookies.removeAll { it.name == newCookie.name }
            currentCookies.add(newCookie)
        }
        
        cookieCache[host] = currentCookies

        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(host)] = currentCookies.joinToString("|") { it.toString() }
            }
        }
    }

    fun getCookies(url: HttpUrl): List<Cookie> {
        return cookieCache[url.host] ?: emptyList()
    }
}
