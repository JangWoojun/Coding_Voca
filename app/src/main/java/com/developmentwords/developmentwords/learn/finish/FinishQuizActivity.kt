package com.developmentwords.developmentwords.learn.finish

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.developmentwords.developmentwords.databinding.ActivityFinishQuizBinding
import com.developmentwords.developmentwords.learn.ResultActivity
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.Quiz
import com.developmentwords.developmentwords.util.Voca
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FinishQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinishQuizBinding
    private lateinit var list: List<Quiz>
    private val correctAnswerList = ArrayList<Voca>()
    private val wrongAnswerList = ArrayList<Voca>()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishQuizBinding.inflate(layoutInflater)
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

                    button1.isEnabled = true
                    button2.isEnabled = true
                    button3.isEnabled = true
                    button4.isEnabled = true

                    button1.text = list[index].answer1
                    button2.text = list[index].answer2
                    button3.text = list[index].answer3
                    button4.text = list[index].answer4

                    val color = Color.parseColor("#EEEEEE")
                    val colorStateList = ColorStateList.valueOf(color)

                    button1.backgroundTintList = colorStateList
                    button2.backgroundTintList = colorStateList
                    button3.backgroundTintList = colorStateList
                    button4.backgroundTintList = colorStateList

                    button1.setTextColor(Color.parseColor("#000000"))
                    button2.setTextColor(Color.parseColor("#000000"))
                    button3.setTextColor(Color.parseColor("#000000"))
                    button4.setTextColor(Color.parseColor("#000000"))

                    titleText.text = list[index].question

                    button1.setOnClickListener {
                        changeQuiz(1)
                    }

                    button2.setOnClickListener {
                        changeQuiz(2)
                    }

                    button3.setOnClickListener {
                        changeQuiz(3)
                    }

                    button4.setOnClickListener {
                        changeQuiz(4)
                    }

                    closeButton.setOnClickListener {
                        finish()
                    }
                }
            }
        }
    }

    private fun changeQuiz(chk: Int) {
        binding.apply {
            button1.isEnabled = false
            button2.isEnabled = false
            button3.isEnabled = false
            button4.isEnabled = false

            if (chk == list[index].correctAnswer) {
                correctAnswerList.add(Voca(list[index].title, list[index].context, false))
                val selectedColor = Color.parseColor("#3D5CFF")
                val colorStateListSelected = ColorStateList.valueOf(selectedColor)
                when (chk) {
                    1 -> {
                        button1.backgroundTintList = colorStateListSelected
                        button1.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    2 -> {
                        button2.backgroundTintList = colorStateListSelected
                        button2.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    3 -> {
                        button3.backgroundTintList = colorStateListSelected
                        button3.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    else -> {
                        button4.backgroundTintList = colorStateListSelected
                        button4.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            } else {
                wrongAnswerList.add(Voca(list[index].title, list[index].context, true))
                val selectedColor = Color.parseColor("#DB4455")
                val colorStateListSelected = ColorStateList.valueOf(selectedColor)
                when (chk) {
                    1 -> {
                        button1.backgroundTintList = colorStateListSelected
                        button1.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    2 -> {
                        button2.backgroundTintList = colorStateListSelected
                        button2.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    3 -> {
                        button3.backgroundTintList = colorStateListSelected
                        button3.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    else -> {
                        button4.backgroundTintList = colorStateListSelected
                        button4.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }
            index++
            Handler().postDelayed({
                if (index == (list.size)) {
                    val intent = Intent(baseContext, ResultActivity::class.java)
                    intent.putExtra("정답", correctAnswerList)
                    intent.putExtra("오답", wrongAnswerList)
                    intent.putExtra("이름", "finishQuiz")
                    startActivity(intent)
                    finish()
                } else {
                    titleText.text = list[index].question
                    progressBar.progress = index

                    button1.text = list[index].answer1
                    button2.text = list[index].answer2
                    button3.text = list[index].answer3
                    button4.text = list[index].answer4

                    button1.isEnabled = true
                    button2.isEnabled = true
                    button3.isEnabled = true
                    button4.isEnabled = true

                    val color = Color.parseColor("#EEEEEE")
                    val colorStateList = ColorStateList.valueOf(color)

                    button1.backgroundTintList = colorStateList
                    button2.backgroundTintList = colorStateList
                    button3.backgroundTintList = colorStateList
                    button4.backgroundTintList = colorStateList

                    button1.setTextColor(Color.parseColor("#000000"))
                    button2.setTextColor(Color.parseColor("#000000"))
                    button3.setTextColor(Color.parseColor("#000000"))
                    button4.setTextColor(Color.parseColor("#000000"))

                }
            }, 300)
        }
    }
}