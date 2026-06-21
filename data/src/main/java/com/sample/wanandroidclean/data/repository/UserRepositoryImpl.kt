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
     * 增强版登录状态检查。
     * 同时检查多个关键 Cookie 标识 (loginUserName 或 token_pass)，确保状态识别的即时性。
     */
    override val isUserLoggedIn: Flow<Boolean> = cookieStorage.cookies.map { cookies ->
        cookies.any { 
            (it.name == "loginUserName" || it.name == "token_pass") && it.value.isNotEmpty() 
        }
    }

    override suspend fun login(username: String, password: String): Result<UserInfo> {
        val result = safeApiCall { wanAndroidApi.login(username, password) }
        return result.map { it.toDomain() }
    }

    override suspend fun logout(): Result<Unit> {
        // 对于玩Android，登出通常只需要清理本地保存的 Cookie 即可。
        // 如果服务端有登出接口，也可以在此调用。
        return try {
            cookieStorage.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
