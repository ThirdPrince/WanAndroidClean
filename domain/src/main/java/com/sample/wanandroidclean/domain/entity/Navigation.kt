package com.sample.wanandroidclean.domain.entity

/**
 * Represents a navigation category in the domain layer.
 */
data class Navigation(
    val id: Int,
    val name: String,
    val articles: List<Article>
)
