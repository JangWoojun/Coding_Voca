package com.developmentwords.developmentwords.word

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.FragmentWordBinding
import com.developmentwords.developmentwords.util.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordFragment : Fragment() {

    private var _binding: FragmentWordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            moveButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_word_to_course)
            }

            moveLearnButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_word_to_learn)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext())
                val userDao = db?.userDao()
                val myVocaDao = db?.myVocaDao()

                val user = userDao!!.getUser()
                val nowCourse = user.nowCourse.title

                val myVoca = myVocaDao!!.getMyVocaByTitle(nowCourse)

                withContext(Dispatchers.Main) {
                    if (myVoca.isNotEmpty()) {
                        val it = myVoca[0]

                        val lastIndex = it.title.lastIndexOf(' ')
                        val title = it.title.substring(0, lastIndex)
                        val level = it.title.substring(lastIndex + 1)

                        titleText.text = title
                        levelText.text = "$level 단계"
                        wordText.text = "${it.voca.size} word"
                        wordList.layoutManager = LinearLayoutManager(context)
                        wordList.adapter = WordAdapter(it.voca)
                        nullMoveCourseButton.visibility = View.GONE
                        moveLearnButton.visibility = View.VISIBLE
                    } else {
                        nullMoveCourseButton.visibility = View.VISIBLE
                        moveLearnButton.visibility = View.GONE
                    }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}