package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.UserInfo
import kotlinx.serialization.Serializable

/**
 * User data transfer object that is compatible with both login and coin info responses.
 */
@Serializable
data class UserInfoDto(
    val id: Int = 0,          // Used in login response
    val userId: Int = 0,      // Used in coin info response
    val username: String = "", // This is the login account (e.g., phone number)
    val nickname: String = "", // This is the preferred display name
    val level: Int = 0,
    val rank: String = "0",
    val coinCount: Int = 0
) {
    fun toDomain(): UserInfo {
        // Use either userId or id, whichever is provided
        val finalId = if (userId != 0) userId else id
        return UserInfo(
            id = finalId,
            // Logic: Prefer nickname if it's not empty, otherwise fallback to username
            nickname = nickname.ifEmpty { username },
            level = level,
            rank = rank,
            coinCount = coinCount
        )
    }
}
