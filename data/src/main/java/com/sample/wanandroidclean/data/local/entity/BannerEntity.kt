package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.wanandroidclean.domain.entity.Banner

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: Int,
    val imagePath: String,
    val title: String,
    val url: String,
    val sortOrder: Int // To maintain the order from the API
) {
    fun toDomain(): Banner = Banner(
        id = id,
        imagePath = imagePath,
        title = title,
        url = url
    )

    companion object {
        fun fromDomain(banner: Banner, sortOrder: Int): BannerEntity = BannerEntity(
            id = banner.id,
            imagePath = banner.imagePath,
            title = banner.title,
            url = banner.url,
            sortOrder = sortOrder
        )
    }
}
