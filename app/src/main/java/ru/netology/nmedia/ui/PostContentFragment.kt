package ru.netology.nmedia.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.databinding.PostContentFragmentBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.ui.PostContentFragment.Companion.REQUEST_KEY


class PostContentFragment : Fragment() {

    private val args by navArgs<PostContentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PostContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        // обращаемся к тексту для ред-я, с которым фрагмент был запущен, и достаем данные, переданные через аргумент
        binding.edit.setText(args.textToEdit)
        //val textToEdit = intent?.extras?.getString(Intent.EXTRA_TEXT) ?: ""
        //binding.edit.setText(textToEdit)
        binding.edit.requestFocus() // как только откроется экран, то курсор поставится на поле edit
        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: PostContentFragmentBinding) {
        if (!binding.edit.text.isNullOrBlank()) {
            val resultBundle = Bundle(1)
            // конвертируем текст в строку, укладываем в заготовленный бандл
            resultBundle.putString(RESULT_KEY, binding.edit.text.toString())
            setFragmentResult(REQUEST_KEY, resultBundle)
        }
        findNavController().popBackStack() // навигируемся назад, фрагмент выкидывается из backstackа фрагментов
    }


    // ключи для передачи данных между фрагментами
    companion object {
        const val REQUEST_KEY = "requestKey"
        const val RESULT_KEY = "postNewContent"

    }


}