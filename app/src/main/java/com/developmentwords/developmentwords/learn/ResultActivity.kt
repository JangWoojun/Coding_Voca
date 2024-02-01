package com.developmentwords.developmentwords.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.databinding.ActivityResultBinding
import com.developmentwords.developmentwords.learn.finish.FinishQuizActivity
import com.developmentwords.developmentwords.learn.flash.FlashActivity
import com.developmentwords.developmentwords.learn.initial.InitialQuizActivity
import com.developmentwords.developmentwords.learn.ox.OXQuizActivity
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.FBDB
import com.developmentwords.developmentwords.util.Voca
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val correctList = intent.getSerializableExtra("정답") as ArrayList<Voca>
        val wrongList = intent.getSerializableExtra("오답") as ArrayList<Voca>

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(baseContext)
            val user = db?.userDao()!!.getUser()

            if (intent.getSerializableExtra("이름") == "finishQuiz") {
                withContext(Dispatchers.Main) {
                    binding.titleText.text = "테스트 결과"
                }
                if (!user.nowCourse.finishChk) {
                    user.myCourse.remove(user.nowCourse)
                    user.nowCourse.nowWord = correctList.size

                    if (user.nowCourse.maxWord == correctList.size) {
                        user.nowCourse.finishChk = true
                    }
                    user.myCourse.add(user.nowCourse)

                    db.userDao().updateUser(user)
                }
            }
            if (user.todayWord < correctList.size) {
                user.todayWord = correctList.size
                db.userDao().updateUser(user)
            }
            FBDB.uploadUserInfo(baseContext)
        }

        binding.apply {

            closeButton.setOnClickListener {
                finish()
            }

            replayButton.setOnClickListener {

                when (intent.getStringExtra("이름")) {
                    "Flash" -> {
                        val intent = Intent(baseContext, FlashActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "OXQuiz" -> {
                        val intent = Intent(baseContext, OXQuizActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "initialQuiz" -> {
                        val intent = Intent(baseContext, InitialQuizActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        val intent = Intent(baseContext, FinishQuizActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            resultList.layoutManager = LinearLayoutManager(baseContext)
            resultList.adapter = ResultAdapter(wrongList.plus(correctList))

            allWordButton.setOnClickListener {
                resultList.adapter = ResultAdapter(wrongList.plus(correctList))
                clearBackground()
                allWordTitle.setTextColor(ContextCompat.getColor(baseContext, R.color.point_color))
                allWordBackground.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.point_color))
                allWordClickBackground.visibility = View.VISIBLE
            }

            correctWordButton.setOnClickListener {
                resultList.adapter = ResultAdapter(correctList)
                clearBackground()
                correctWordTitle.setTextColor(ContextCompat.getColor(baseContext, R.color.point_color))
                correctWordBackground.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.point_color))
                correctWordClickBackground.visibility = View.VISIBLE
            }

            wrongWordButton.setOnClickListener {
                resultList.adapter = ResultAdapter(wrongList)
                clearBackground()
                wrongWordTitle.setTextColor(ContextCompat.getColor(baseContext, R.color.point_color))
                wrongWordBackground.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.point_color))
                wrongWordClickBackground.visibility = View.VISIBLE
            }
        }
    }

    private fun clearBackground() {
        binding.apply {
            wrongWordTitle.setTextColor(ContextCompat.getColor(baseContext, R.color.text_color))
            wrongWordBackground.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.text_color))
            wrongWordClickBackground.visibility = View.INVISIBLE

            allWordTitle.setTextColor(ContextCompat.getColor(baseContext, R.color.text_color))
            allWordBackground.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.text_color))
            allWordClickBackground.visibility = View.INVISIBLE

            correctWordTitle.setTextColor(ContextCompat.getColor(baseContext, R.color.text_color))
            correctWordBackground.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.text_color))
            correctWordClickBackground.visibility = View.INVISIBLE
        }
    }
}