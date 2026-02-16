package com.sample.wanandroidclean.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BannerResponse(
    val data: List<BannerDto>,
    val errorCode: Int,
    val errorMsg: String
)
