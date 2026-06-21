package com.sample.wanandroidclean.domain.entity

/**
 * 用户信息领域模型。
 * [nickname] 始终代表展示给用户的名字（优先取昵称，无昵称则取账号名）。
 */
data class UserInfo(
    val id: Int,
    val nickname: String, 
    val level: Int,
    val rank: String,
    val coinCount: Int
)
