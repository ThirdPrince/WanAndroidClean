package com.sample.wanandroidclean.domain.entity

/**
 * Represents a system category in the domain layer.
 */
data class SystemCategory(
    val id: Int,
    val name: String,
    val children: List<SystemCategory>
)
