package ru.netology.nmedia.data.impl

import androidx.lifecycle.map
import ru.netology.nmedia.data.PostRepository
import ru.netology.nmedia.db.PostDao
import ru.netology.nmedia.db.toEntity
import ru.netology.nmedia.db.toModel
import ru.netology.nmedia.dto.Post

/*
Репозиторий "кеширует" в памяти данные для ускорения доступа.
Поскольку у нас с базой работает только он (через DAO), то это допустимо.
*/

class RoomPostRepository(
    private val dao: PostDao
) : PostRepository {

    override val data = dao.getAll().map { entities ->
        entities.map { it.toModel() }
    }

    override fun save(post: Post) {
        dao.save(post.toEntity())
    }

    override fun share(postId: Long) {
        dao.share(postId)
    }

    override fun like(postId: Long) {
        dao.like(postId)
    }

    override fun delete(postId: Long) {
        dao.delete(postId)
    }
}