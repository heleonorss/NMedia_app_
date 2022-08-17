package ru.netology.nmedia.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Это объектное представлление таблицы (кот состоит из объектов) вместо PostsTable

@Entity(tableName = "posts")
class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") // чтобы не слетели имена при релизной сборке
    val id: Long,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "published")
    val published: String,
    @ColumnInfo(name = "likedByMe")
    val likedByMe: Boolean,
    @ColumnInfo(name = "likes")
    val likes: Int = 0,
    @ColumnInfo(name = "shares")
    var shares: Int = 0,
    @ColumnInfo(name = "shared")
    val shared: Boolean,
    @ColumnInfo(name = "viewings")
    val viewings: Int = 1,
    @ColumnInfo(name = "videoLink")
    val videoLink: String = ""
)