package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.Banner
import kotlinx.serialization.Serializable

@Serializable
data class BannerDto(
    val id: Int,
    val imagePath: String,
    val title: String,
    val url: String
) {
    fun toDomain(): Banner = Banner(
        id = id,
        imagePath = imagePath,
        title = title,
        url = url
    )
}
