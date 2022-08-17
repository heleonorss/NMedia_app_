package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.SeparatePostViewBinding
import ru.netology.nmedia.viewModel.PostViewModel


class SeparatePostFragment : Fragment() {

    private val args by navArgs<SeparatePostFragmentArgs>()

    private val separatePostViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SeparatePostViewBinding.inflate(layoutInflater, container, false).also { binding ->
        val postViewHolder =
            PostsAdapter.ViewHolder(binding.separatePostView, separatePostViewModel)
        separatePostViewModel.data.observe(viewLifecycleOwner) { posts ->
            val separatedPost = posts.find { it.id == args.postCardId } ?: run {
                findNavController().navigateUp() // пост был удален
                return@observe
            }
            postViewHolder.bind(separatedPost)
            /*в обработчике observe подписка идет на все посты, а внутри ищется нужный по id,
            который затем передается ViewHolder на отображение. При этом, если поста не нашлось,
            значит он был удален и текущий фрагмент нужно закрыть. Это делается через оператора Элвиса ?:: find возвращает Post?,
            если поста не нашлось, то выходим, а если нашелся, то записываем в переменную и отображаем в bind.*/
        }

        //организация перехода к фрагменту postContentFragment
        separatePostViewModel.navigateToPostContentScreenEvent.observe(viewLifecycleOwner) { textToEdit ->
            val direction =
                SeparatePostFragmentDirections.actionSeparatePostFragmentToPostContentFragment(
                    textToEdit
                )
            findNavController().navigate(direction)
        }
        separatePostViewModel.videoPlayEvent.observe(viewLifecycleOwner) { videoLink ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
            startActivity(intent)
        }
        separatePostViewModel.sharePostContent.observe(viewLifecycleOwner) { postContent ->
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

        // показываем новый экран в нашем приложении
        // данная ф-ция будет вызвана при завершении PostContentActivity
        setFragmentResultListener(
            requestKey = PostContentFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != PostContentFragment.REQUEST_KEY) return@setFragmentResultListener
            val newPostContent = bundle.getString(
                PostContentFragment.RESULT_KEY
            ) ?: return@setFragmentResultListener
            separatePostViewModel.onSaveButtonClicked(newPostContent)
        }
    }.root

}
