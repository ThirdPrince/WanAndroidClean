package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.UserInfo
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi /**
 * User data transfer object that is compatible with both login and coin info responses.
 */
@Serializable
data class UserInfoDto(
    val id: Int = 0,          // Used in login response
    val userId: Int = 0,      // Used in coin info response
    val username: String = "",
    val nickname: String = "",
    val level: Int = 0,
    val rank: String = "0",
    val coinCount: Int = 0
) {
    fun toDomain(): UserInfo {
        // Use either userId or id, whichever is provided
        val finalId = if (userId != 0) userId else id
        return UserInfo(
            id = finalId,
            username = nickname.ifEmpty { username },
            level = level,
            rank = rank,
            coinCount = coinCount
        )
    }
}
