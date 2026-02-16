package com.sample.wanandroidclean.domain.entity

/**
 * Represents a banner in the domain layer.
 */
data class Banner(
    val id: Int,
    val imagePath: String,
    val title: String,
    val url: String
)
