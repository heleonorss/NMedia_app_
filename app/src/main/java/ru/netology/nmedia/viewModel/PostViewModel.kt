package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.adapter.PostInteractionListener
import ru.netology.nmedia.data.PostRepository
import ru.netology.nmedia.data.impl.RoomPostRepository
import ru.netology.nmedia.data.impl.FilePostRepository
import ru.netology.nmedia.data.impl.InMemoryPostRepository

//import ru.netology.nmedia.data.impl.SQLitePostRepository
import ru.netology.nmedia.data.impl.SharedPrefsPostRepository
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.SingleLiveEvent

//class PostViewModel : ViewModel(), PostInteractionListener {

// ViewModel не должна знать о Binding и View,
// ее задача - подготовить данные для отображения во фрагменте,
// само отображение она не осуществляет.
class PostViewModel(
    application: Application
) : AndroidViewModel(application), PostInteractionListener {

    private val repository: PostRepository = RoomPostRepository(
   // private val repository: PostRepository = SQLitePostRepository(

        dao = AppDb.getInstance(
            context = application
        ).postDao
    )
    //private val repository: PostRepository = InMemoryPostRepository

    //private val repository: PostRepository = SharedPrefsPostRepository(application)
    //private val repository: PostRepository = FilePostRepository(application)

    val data get() = repository.data

    val sharePostContent = SingleLiveEvent<String>()

    val videoPlayEvent = SingleLiveEvent<String>()

    val separatePostViewEvent = SingleLiveEvent<Long>()

    // Эта LiveData хранит текст поста, который редактируется, или null, если новый текст добавляется пользователем
    val navigateToPostContentScreenEvent = SingleLiveEvent<String?>()

    // liveData - это живой поток, в кот только одни какие-то данные, самая актуальная последняя инфа.
    // Вызывая метод value - мы закидываем данные в поток, на кот кто-то где-то подписывается, напр наша viewModel
    // на поле data подписалась наша activity, поэтому данные обновились в liveData и вызвался перерендеринг
    val currentPost = MutableLiveData<Post?>(null)

    fun onSaveButtonClicked(content: String) { // нужно научить, когда пришел новый пост, а когда неновый для редактирования контента
        if (content.isBlank()) return
        val newPost = currentPost.value?.copy( // создание копии поста с новым контентом
            content = content
        ) ?: Post(
            id = PostRepository.NEW_POST_ID,
            author = "Me",
            content = content,
            published = "now"
        )
        repository.save(newPost)
        currentPost.value = null // сброс контента сохраненного поста в строке, где мы его печатали
    }

    /*fun onCancelButtonClicked() {
        currentPost.value = null
    }
*/
    fun onAddButtonClicked() {
        navigateToPostContentScreenEvent.call()
    }

    override fun onLikeButtonClicked(post: Post) = repository.like(post.id)
    override fun onShareButtonClicked(post: Post) {
        sharePostContent.value = post.content
        repository.share(post.id)
    }

    override fun onRemoveButtonClicked(post: Post) = repository.delete(post.id)
    override fun onEditButtonClicked(post: Post) {
        currentPost.value = post // закидываем пост в поток редактирования
        navigateToPostContentScreenEvent.value =
            post.content // отобразится контент текущего поста на экране
    }

    override fun onVideoPlayButtonClicked(post: Post) {
        videoPlayEvent.value = post.videoLink // закидываем videoLink в поток
    }

    override fun onPostCardClicked(post: Post) {
        separatePostViewEvent.value = post.id
    }
}
