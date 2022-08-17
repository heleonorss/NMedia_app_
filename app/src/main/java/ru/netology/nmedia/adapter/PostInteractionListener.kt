package ru.netology.nmedia.adapter

import ru.netology.nmedia.dto.Post

interface PostInteractionListener {

    fun onLikeButtonClicked(post: Post)
    fun onShareButtonClicked(post: Post)
    fun onRemoveButtonClicked(post: Post)
    fun onEditButtonClicked(post: Post)
    fun onVideoPlayButtonClicked(post: Post)
    fun onPostCardClicked(post: Post)
}
