package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostBinding
import ru.netology.nmedia.dto.Post

// у адаптера есть целиком весь список, он знает про все элементы в списке,
// но он оптимально создает вьюхи только в нужный момент
// Это связующее звено между данными и вьюхами!!!

class PostsAdapter(
    private val interactionListener: PostInteractionListener
) : ListAdapter<Post, PostsAdapter.ViewHolder>(DiffCallback) {

    // на экране телефона только штуки 4 ВьюХолдера создасться и следовательно только 4 вьюхи заинфлейтится
    // тк экран больше не вмещает, а одновлеменно вьюхи вообще для всех постов раздувать смысла нет
    // тк их пользователь все равно не увидит, а прога будет сильно тормозить
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    // для перерисовки/перезаполения вьюхи
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: PostBinding,
        listener: PostInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var post: Post // lateinit var отменяет обязательную инициализацию

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.options).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> { // если нажали на кнопку remove
                            listener.onRemoveButtonClicked(post)
                            true
                        }
                        R.id.edit -> { // если нажали на кнопку Edit
                            listener.onEditButtonClicked(post)
                            true
                        }
                        else -> false
                    }
                }
            }
        }

        // повесили (проинициализировали 1 раз) слушателя за нажатием лайка, те нажали лайк, во вьюмодели вызовется метод
        // onLikeClicked, кот в свое время вызовет из репозитория метод like, like получит
        // текущий пост, лайкнет/дизлайкнет его и обновит данные в liveData

        init {
            binding.likeButton.setOnClickListener { listener.onLikeButtonClicked(post) }
        }

        init {
            binding.shareButton.setOnClickListener { listener.onShareButtonClicked(post) }
        }

        init {
            binding.options.setOnClickListener { popupMenu.show() }
        }

        init {
            binding.videoContent.setOnClickListener { listener.onVideoPlayButtonClicked(post)}
            binding.videoPlay.setOnClickListener { listener.onVideoPlayButtonClicked(post)}
        }

        init {
            binding.content.setOnClickListener { listener.onPostCardClicked(post) }
            binding.authorName.setOnClickListener { listener.onPostCardClicked(post) }
            binding.published.setOnClickListener { listener.onPostCardClicked(post) }
            binding.avatar.setOnClickListener { listener.onPostCardClicked(post) }
        }

        fun bind(post: Post) {
            this.post = post // пост, кот из lateinit

            with(binding) {
                authorName.text = post.author
                content.text = post.content
                published.text = post.published
                likeButton.isChecked = post.likedByMe
                likeButton.text = showNumberView(post.likes)
                shareButton.setIconResource(R.drawable.ic_baseline_share_24)
                shareButton.text = showNumberView(post.shares)
                seenNumbers.text = showNumberView(post.viewings)
                videoVisibility.visibility =
                    if (post.videoLink.isBlank()) View.GONE else View.VISIBLE
            }
        }

        private companion object {
            const val THOUSAND = 1000
            const val MILLION = THOUSAND * THOUSAND
        }

        private fun showNumberView(currentNumber: Int): String {
            return when (currentNumber) {
                in 0..999 -> currentNumber.toString()
                in THOUSAND..9999 -> {
                    var numberQuantity =
                        String.format("%.1f", (currentNumber).toDouble() / THOUSAND)
                    if (numberQuantity.endsWith(",0")) {
                        numberQuantity = numberQuantity.substring(
                            0,
                            numberQuantity.length - 2
                        )
                    }
                    numberQuantity + "K"
                }
                in 10 * THOUSAND..999999 -> {
                    val numberQuantity = currentNumber / THOUSAND
                    "$numberQuantity" + "K"
                }
                else -> {
                    var numberQuantity =
                        String.format("%.1f", (currentNumber).toDouble() / MILLION)
                    if (numberQuantity.endsWith(",0")) {
                        numberQuantity = numberQuantity.substring(
                            0,
                            numberQuantity.length - 2
                        )
                    }
                    numberQuantity + "M"
                }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post) =
            oldItem == newItem
    }
}

