package com.developmentwords.developmentwords.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.course.CourseInnerFragment
import com.developmentwords.developmentwords.databinding.FragmentHomeBinding
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.CourseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            moveMyCourseButton1.setOnClickListener {
                startActivity(Intent(requireContext(), MyCourseActivity::class.java))
                requireActivity().finish()
            }

            moveMyCourseButton2.setOnClickListener {
                startActivity(Intent(requireContext(), MyCourseActivity::class.java))
                requireActivity().finish()
            }

            bannerButton1.setOnClickListener {
                val bundle = Bundle()
                val item = CourseItem(
                    title = "프로그래밍 코스 초급", subject = "이번 코스는 프로그래밍을 처음 배우는 초보자를 위한 과정으로 누구나 쉽게 프로그래밍을 배울 수 있습니다",
                    wordCount = 18, level = "쉬움", levelSubject = "이번 코스는 프로그래밍을 처음 배우는 초보자들과 알고리즘의 숙련자가 복습하기에 적합합니다",
                    updateDate = "2023년 5월 14일"
                )
                bundle.putSerializable("courseItem", item)

                val fragment = CourseInnerFragment()
                fragment.arguments = bundle
                view.findNavController().navigate(R.id.action_home_to_courseInnerFragment, bundle)
            }

            bannerButton2.setOnClickListener {
                val bundle = Bundle()
                val item = CourseItem(
                    title = "알고리즘 코스 초급", subject = "이번 코스는 알고리즘을 처음 배우는 초보자를 위한 과정으로 누구나 쉽게 알고리즘을 배울 수 있습니다",
                    wordCount = 17, level = "쉬움", levelSubject = "이번 코스는 알고리즘을 처음 배우는 초보자들과 알고리즘의 숙련자가 복습하기에 적합합니다",
                    updateDate = "2023년 5월 14일"
                )
                bundle.putSerializable("courseItem", item)

                val fragment = CourseInnerFragment()
                fragment.arguments = bundle
                view.findNavController().navigate(R.id.action_home_to_courseInnerFragment, bundle)
            }

            bannerButton3.setOnClickListener {
                val bundle = Bundle()
                val item = CourseItem(
                    title = "자료구조 코스 일반", subject = "이번 코스는 자료구조를 처음 배우는 일반인들을 위한 과정으로 누구나 쉽게 자료구조 단어들을 배울 수 있습니다",
                    wordCount = 21, level = "보통", levelSubject = "이번 코스는 자료구조를 배우기 희망하는 모든 분이 학습하기에 적합합니다",
                    updateDate = "2023년 5월 14일"
                )
                bundle.putSerializable("courseItem", item)

                val fragment = CourseInnerFragment()
                fragment.arguments = bundle
                view.findNavController().navigate(R.id.action_home_to_courseInnerFragment, bundle)
            }

            bannerButton4.setOnClickListener {
                val bundle = Bundle()
                val item = CourseItem(
                    title = "인공지능 코스 일반", subject = "이번 코스는 인공지능 처음 배우는 일반인들을 위한 과정으로 누구나 쉽게 인공지능 단어들을 배울 수 있습니다",
                    wordCount = 14, level = "쉬움", levelSubject = "이번 코스는 인공지능 배우기 희망하는 모든 분이 학습하기에 적합합니다",
                    updateDate = "2023년 5월 14일"
                )
                bundle.putSerializable("courseItem", item)

                val fragment = CourseInnerFragment()
                fragment.arguments = bundle
                view.findNavController().navigate(R.id.action_home_to_courseInnerFragment, bundle)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireActivity().applicationContext)
                val user = db?.userDao()?.getUser()

                val imageUri = user!!.userImage

                withContext(Dispatchers.Main) {
                    userNameText.text = "반가워요, ${user!!.userName}님"
                    todayLearnWordText.text = "${user.todayWord}ea"
                    todayLearnWordProgress.progress = user.todayWord

                    if (imageUri == "null") {
                        Glide.with(requireContext())
                            .load(R.drawable.default_avatar)
                            .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                            .into(binding.profile)
                    } else {
                        Glide.with(requireContext())
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                            .into(binding.profile)
                    }

                    if (user.nowCourse.title != "") {
                        nullLearningCourseText.visibility = View.INVISIBLE
                        moveMyCourseButton2.visibility = View.VISIBLE
                        learningCourseBox1.visibility = View.VISIBLE
                        learningCourseText1.text = user.nowCourse.title
                        learningCourseProgress1.max = user.nowCourse.maxWord
                        learningCourseProgress1.progress = user.nowCourse.nowWord
                        learningCourseMaxText1.text = user.nowCourse.maxWord.toString()
                        learningCourseNowText1.text = user.nowCourse.nowWord.toString()
                        if (user.myCourse.count() > 2) {
                            for (i in user.myCourse) {
                                if (!i.finishChk && i.title != "" && i != user.nowCourse) {
                                    learningCourseBox2.visibility = View.VISIBLE
                                    learningCourseText2.text = i.title
                                    learningCourseProgress2.max = i.maxWord
                                    learningCourseProgress2.progress = i.nowWord
                                    learningCourseMaxText2.text = i.maxWord.toString()
                                    learningCourseNowText2.text = i.nowWord.toString()
                                }
                            }
                        }
                    } else {
                        nullLearningCourseText.visibility = View.VISIBLE
                        learningCourseBox1.visibility = View.INVISIBLE
                        moveMyCourseButton2.visibility = View.INVISIBLE
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