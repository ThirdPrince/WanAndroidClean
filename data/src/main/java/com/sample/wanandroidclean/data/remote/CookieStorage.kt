package com.sample.wanandroidclean.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.ConcurrentHashMap

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cookie_prefs")

/**
 * Optimized Cookie storage with an immediate in-memory StateFlow.
 * Ensures UI can react to login status changes without any disk I/O delay.
 */
class CookieStorage(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val cookieCache = ConcurrentHashMap<String, List<Cookie>>()
    
    // 内存真值流：实现状态的毫秒级同步
    private val _cookiesState = MutableStateFlow<List<Cookie>>(emptyList())
    val cookies: Flow<List<Cookie>> = _cookiesState.asStateFlow()

    init {
        scope.launch {
            try {
                val preferences = context.dataStore.data.first()
                val allLoadedCookies = mutableListOf<Cookie>()
                preferences.asMap().forEach { (key, value) ->
                    if (value is String) {
                        val host = key.name
                        val url = "https://$host/".toHttpUrlOrNull() ?: return@forEach
                        val hostCookies = value.split("|").mapNotNull {
                            Cookie.parse(url, it)
                        }
                        cookieCache[host] = hostCookies
                        allLoadedCookies.addAll(hostCookies)
                    }
                }
                _cookiesState.value = allLoadedCookies
            } catch (e: Exception) {}
        }
    }

    /**
     * 同步返回内存中的 Cookie，用于 UserRepository 快速判断
     */
    fun hasLoginCookie(): Boolean {
        return _cookiesState.value.any { it.name == "loginUserName" && it.value.isNotEmpty() }
    }

    fun saveCookies(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val currentHostCookies = cookieCache[host]?.toMutableList() ?: mutableListOf()
        
        cookies.forEach { newCookie ->
            currentHostCookies.removeAll { it.name == newCookie.name }
            currentHostCookies.add(newCookie)
        }
        
        cookieCache[host] = currentHostCookies
        
        // 关键：立即更新内存流，触发 UI 刷新
        _cookiesState.value = cookieCache.values.flatten()

        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(host)] = currentHostCookies.joinToString("|") { it.toString() }
            }
        }
    }

    fun getCookies(url: HttpUrl): List<Cookie> {
        return cookieCache[url.host] ?: emptyList()
    }

    fun clearAll() {
        cookieCache.clear()
        _cookiesState.value = emptyList()
        scope.launch {
            context.dataStore.edit { it.clear() }
        }
    }
}
