package ru.netology.nmedia.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.netology.nmedia.dto.Post

// Всю непосредственную работу по запросам мы вынесем в DAO (Data Access Object)

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: PostEntity)

    @Query("UPDATE posts SET content = :content WHERE id = :id")
    fun updateContentById(id: Long, content: String)

    fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
        UPDATE posts SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun like(id: Long)

    @Query("DELETE FROM posts WHERE id = :id")
    fun delete(id: Long)

    @Query(
        """
        UPDATE posts SET
        shares = shares + CASE WHEN shared THEN 0 ELSE 1 END,
        shared = CASE WHEN shared THEN 1 ELSE 0 END
        WHERE id = :id
        """
    )
    fun share(id: Long)

}


/*
fun getAll(): List<Post>
fun save(post: Post): Post
fun like(id: Long)
fun delete(id: Long)
fun share(id: Long)*/
