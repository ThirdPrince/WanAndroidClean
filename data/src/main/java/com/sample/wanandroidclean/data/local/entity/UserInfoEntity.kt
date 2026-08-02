package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.wanandroidclean.domain.entity.UserInfo

@Entity(tableName = "user_info")
data class UserInfoEntity(
    @PrimaryKey val id: Int,
    val nickname: String,
    val level: Int,
    val rank: String,
    val coinCount: Int
) {
    fun toDomain(): UserInfo = UserInfo(
        id = id,
        nickname = nickname,
        level = level,
        rank = rank,
        coinCount = coinCount
    )

    companion object {
        fun fromDomain(domain: UserInfo): UserInfoEntity = UserInfoEntity(
            id = domain.id,
            nickname = domain.nickname,
            level = domain.level,
            rank = domain.rank,
            coinCount = domain.coinCount
        )
    }
}
