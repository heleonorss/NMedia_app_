package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FeedFragmentBinding
import ru.netology.nmedia.viewModel.PostViewModel


class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    // делегирование для того, чтобы при перевороте экрана не сбрасывался текст
    // в кач-ве аргумента объединяем все вьюмодели, чтобы все изменения передавались из одного фрагмента в другой

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.sharePostContent.observe(this) { postContent ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND  // создаем пустой интент и заполняем его через apply
                putExtra(Intent.EXTRA_TEXT, postContent) // кладем некоторые данные, кот будем share
                type = "text/plain"
            }
            val shareIntent =
                Intent.createChooser(
                    intent,
                    getString(R.string.description_post_share) // создаем меню выбора
                )
            startActivity(shareIntent)
        }

        viewModel.videoPlayEvent.observe(this) { videoLink ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
            // если у-во пользователя может выполнить данную задачу, то запускаем этот интент
            // if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }

        //организация перехода к фрагменту postContentFragment
        viewModel.navigateToPostContentScreenEvent.observe(this) { textToEdit ->
            val direction = FeedFragmentDirections.actionFeedFragmentToPostContentFragment(textToEdit)
            findNavController().navigate(direction)
        }

        //организация перехода к фрагменту separatePostFragment
        viewModel.separatePostViewEvent.observe(this) { postCardId ->
            val direction = FeedFragmentDirections.actionFeedFragmentToSeparatePostFragment(postCardId)
            findNavController().navigate(direction)
        }
    }

    // Можно использовать одинаковый ключ в обоих фрагментах,
    // но при появлении FeedFragment устанавливать обработчик заново,
    // чтобы он переписал тот обработчик, что был установлен SinglePostFragment
    override fun onResume() {
        super.onResume()
        // показываем новый экран в нашем приложении
        // данная ф-ция будет вызвана при завершении PostContentActivity
        setFragmentResultListener(
            requestKey = PostContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != PostContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newPostContent = bundle.getString(
                PostContentFragment.RESULT_KEY
            ) ?: return@setFragmentResultListener
            viewModel.onSaveButtonClicked(newPostContent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FeedFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        val adapter = PostsAdapter(viewModel)
        binding.postsRecyclerView.adapter = adapter
        // повесили наблюдателя за отрисовкой поста, как только в посте изменятся данные,
        // то вызовется лямбда с ф-цией рендера, кот создана ниже
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        binding.fab.setOnClickListener {
            viewModel.onAddButtonClicked()
        }
    }.root

    companion object {
        const val TAG = "FeedFragment"
    }
}



