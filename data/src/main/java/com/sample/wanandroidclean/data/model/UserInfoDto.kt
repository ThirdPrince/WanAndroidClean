package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    val userId: Int,
    val username: String,
    val nickname: String,
    val level: Int,
    val rank: String,
    val coinCount: Int
) {
    fun toDomain(): UserInfo = UserInfo(
        id = userId,
        username = nickname.ifEmpty { username },
        level = level,
        rank = rank,
        coinCount = coinCount
    )
}
