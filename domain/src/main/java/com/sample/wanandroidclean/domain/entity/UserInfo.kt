package com.sample.wanandroidclean.domain.entity

/**
 * Represents user information in the domain layer.
 */
data class UserInfo(
    val id: Int,
    val username: String,
    val level: Int,
    val rank: String,
    val coinCount: Int
)
