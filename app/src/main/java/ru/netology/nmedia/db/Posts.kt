package ru.netology.nmedia.db

import android.database.Cursor
import ru.netology.nmedia.dto.Post

// делаем ф-цию расширения на курсоре

fun PostEntity.toModel() = Post(
    // мы не можем вытащить данные по названию колонки, а только по ее ид(или индексу)
    id = id,
    author = author,
    content = content,
    published = published,
    likes = likes,
    likedByMe = likedByMe,
    shares = shares,
    shared = shared,
    viewings = viewings,
    videoLink = videoLink
)

fun Post.toEntity() = PostEntity(
    id = id,
    author = author,
    content = content,
    published = published,
    likes = likes,
    likedByMe = likedByMe,
    shares = shares,
    shared = shared,
    viewings = viewings,
    videoLink = videoLink
)