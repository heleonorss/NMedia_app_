package ru.netology.nmedia.dto

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 999,
    var shares: Int = 55,
    val viewings: Int = 1000000,
    val likedByMe: Boolean = false,
    val shared: Boolean = false,
    val videoLink: String = ""

)
