package com.sample.wanandroidclean.data.remote

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(private val storage: CookieStorage) : CookieJar {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        storage.saveCookies(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return storage.getCookies(url)
    }
}
