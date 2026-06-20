package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.CookieStorage
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.Cookie

class UserRepositoryImpl(
    private val wanAndroidApi: WanAndroidApi,
    private val cookieStorage: CookieStorage
) : UserRepository {

    /**
     * 观察登录状态。
     * 通过检查 Cookie 存储中是否存在有效的 WanAndroid 登录标识来判断。
     */
    override val isUserLoggedIn: Flow<Boolean> = cookieStorage.cookies.map { cookies ->
        // 玩Android 登录成功的关键标识是存在名为 "loginUserName" 且值不为空的 Cookie
        cookies.any { it.name == "loginUserName" && it.value.isNotEmpty() }
    }

    override suspend fun login(username: String, password: String): Result<UserInfo> {
        val result = safeApiCall { wanAndroidApi.login(username, password) }
        return result.map { it.toDomain() }
    }
}
