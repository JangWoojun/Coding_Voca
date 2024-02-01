package com.developmentwords.developmentwords.course

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.developmentwords.developmentwords.MainActivity
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.FragmentCourseInnerBinding
import com.developmentwords.developmentwords.util.*
import com.developmentwords.developmentwords.util.FBDB.Companion.newCourse
import com.developmentwords.developmentwords.util.FBDB.Companion.uploadUserInfo
import com.developmentwords.developmentwords.util.ProgressUtil.createLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CourseInnerFragment : Fragment() {

    private var _binding: FragmentCourseInnerBinding? = null
    private val binding get() = _binding!!
    private var buttonChk = true
    private var pinChk = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseInnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavi(true)

        binding.apply {
            val bundle = arguments
            val item = bundle?.getSerializable("courseItem") as CourseItem
            mainTitleText.text = item.title
            subTitleText.text = item.title
            wordText.text = "${item.wordCount}개 단어"
            mainContextText.text = item.subject
            subContextText.text = item.levelSubject
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext())
                val myCourse = db?.userDao()!!.getUser().myCourse

                if (myCourse.any {it.title == item.title && it.finishChk}) {
                    withContext(Dispatchers.Main) {
                        buttonChk = false
                        learnButton.text = "수강 완료"
                    }
                } else if (myCourse.any { it.title == item.title }) {
                    withContext(Dispatchers.Main) {
                        buttonChk = false
                        learnButton.text = "수강중"
                    }
                }

                withContext(Dispatchers.Main) {
                    if (item.pin) {
                        pinChk = true
                        pinIcon.setImageResource(R.drawable.on_star_icon)
                    } else {
                        pinChk = false
                        pinIcon.setImageResource(R.drawable.star_icon)
                    }
                }

            }
            learnButton.setOnClickListener {
                if (buttonChk) {
                    val isConnected = isInternetConnected(requireContext())
                    if (isConnected) {
                        newCourse(item, requireContext(), view)
                    } else {
                        Toast.makeText(requireContext(), "새로운 코스는 인터넷 연결이 필요해요", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val loadingDialog = createLoadingDialog(binding.root.context)
                    loadingDialog.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(binding.root.context)
                        val user = db?.userDao()!!.getUser()

                        user.nowCourse = user.myCourse.find { it.title == item.title }!!
                        db.userDao().updateUser(user)

                        withContext(Dispatchers.Main) {
                            loadingDialog.dismiss()
                            Toast.makeText(binding.root.context, "변경 완료!", Toast.LENGTH_SHORT).show()
                            view.findNavController().navigate(R.id.action_courseInnerFragment_to_word)
                        }
                    }
                }
            }

            pinButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(requireContext())
                    val user = db?.userDao()!!.getUser()
                    val courseItem = db.courseItemDao().getCourseItem()

                    val list = courseItem.map {
                        if (it.title == item.title) {
                            it.copy(pin = !it.pin)
                        } else {
                            it
                        }
                    }

                    db.courseItemDao().updateCourseItem(list)

                    if (pinChk) {
                        user.pinCourseItem.remove(item.title)
                    } else {
                        user.pinCourseItem.add(item.title)
                    }
                    user.pinCourseItem.distinct()

                    db.userDao().updateUser(user)
                    uploadUserInfo(requireContext())

                    withContext(Dispatchers.Main) {
                        pinChk = if (pinChk) {
                            pinIcon.setImageResource(R.drawable.star_icon)
                            !pinChk
                        } else {
                            pinIcon.setImageResource(R.drawable.on_star_icon)
                            !pinChk
                        }
                    }
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavi(false)
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting == true
    }
}