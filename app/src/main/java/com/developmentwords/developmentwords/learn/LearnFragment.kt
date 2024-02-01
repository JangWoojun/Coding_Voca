package com.developmentwords.developmentwords.learn

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.FragmentLearnBinding
import com.developmentwords.developmentwords.learn.finish.FinishQuizActivity
import com.developmentwords.developmentwords.learn.flash.FlashActivity
import com.developmentwords.developmentwords.learn.initial.InitialQuizActivity
import com.developmentwords.developmentwords.learn.next.NextActivity
import com.developmentwords.developmentwords.learn.ox.OXQuizActivity
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.Voca
import com.developmentwords.developmentwords.word.WordAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

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
                        nullMoveCourseButton.visibility = View.GONE
                        learnView.visibility = View.VISIBLE
                        learnFinishButton.visibility = View.VISIBLE
                    } else {
                        nullMoveCourseButton.visibility = View.VISIBLE
                        learnView.visibility = View.GONE
                        learnFinishButton.visibility = View.GONE
                    }
                }
            }

            moveButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_learn_to_course)
            }

            flashButton.setOnClickListener {
                startActivity(Intent(context, FlashActivity::class.java))
            }
            nextButton.setOnClickListener {
                startActivity(Intent(context, NextActivity::class.java))
            }
            oxButton.setOnClickListener {
                startActivity(Intent(context, OXQuizActivity::class.java))
            }
            initialQuizButton.setOnClickListener {
                startActivity(Intent(context, InitialQuizActivity::class.java))
            }
            learnFinishButton.setOnClickListener {
                startActivity(Intent(context, FinishQuizActivity::class.java))
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}