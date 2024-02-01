package com.developmentwords.developmentwords.learn.ox

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.developmentwords.developmentwords.databinding.ActivityOxquizBinding
import com.developmentwords.developmentwords.learn.ResultActivity
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.Quiz
import com.developmentwords.developmentwords.util.Voca
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OXQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOxquizBinding
    private lateinit var list: List<Quiz>
    private val correctAnswerList = ArrayList<Voca>()
    private val wrongAnswerList = ArrayList<Voca>()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOxquizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(baseContext)
            val user = db?.userDao()!!.getUser()
            val myVocaDao = db.myVocaDao()

            val nowCourse = user.nowCourse.title

            val myVoca = myVocaDao.getMyVocaByTitle(nowCourse)

            list = myVoca[0].quiz

            withContext(Dispatchers.Main) {
                binding.apply {

                    courseTitleText.text = nowCourse

                    progressBar.max = list.size
                    progressBar.progress = 0

                    oText.setTextColor(Color.parseColor("#858597"))
                    xText.setTextColor(Color.parseColor("#858597"))

                    oButton.isEnabled = true
                    xButton.isEnabled = true

                    titleText.text = list[index].oxQuestion

                    oButton.setOnClickListener {
                        changeQuiz(true)
                    }
                    xButton.setOnClickListener {
                        changeQuiz(false)
                    }

                    closeButton.setOnClickListener {
                        finish()
                    }
                }
            }
        }
    }
    private fun changeQuiz(chk: Boolean) {
        binding.apply {
            oButton.isEnabled = false
            xButton.isEnabled = false
            if (chk == list[index].oxCorrectAnswer) {
                correctAnswerList.add(Voca(list[index].title, list[index].context, false))
                if (chk) {
                    oText.setTextColor(Color.parseColor("#3D5CFF"))
                } else {
                    xText.setTextColor(Color.parseColor("#3D5CFF"))
                }
            } else {
                wrongAnswerList.add(Voca(list[index].title, list[index].context, true))
                if (chk) {
                    oText.setTextColor(Color.parseColor("#DB4455"))
                } else {
                    xText.setTextColor(Color.parseColor("#DB4455"))
                }
            }
            index++
            Handler().postDelayed({
                if (index == (list.size)) {
                    val intent = Intent(baseContext, ResultActivity::class.java)
                    intent.putExtra("정답", correctAnswerList)
                    intent.putExtra("오답", wrongAnswerList)
                    intent.putExtra("이름", "OXQuiz")
                    startActivity(intent)
                    finish()
                } else {
                    titleText.text = list[index].oxQuestion
                    progressBar.progress = index
                    oText.setTextColor(Color.parseColor("#858597"))
                    xText.setTextColor(Color.parseColor("#858597"))
                    oButton.isEnabled = true
                    xButton.isEnabled = true
                }
            }, 500)
        }
    }
}