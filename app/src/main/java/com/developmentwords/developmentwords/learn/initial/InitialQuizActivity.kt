package com.developmentwords.developmentwords.learn.initial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.developmentwords.developmentwords.databinding.ActivityInitialQuizBinding
import com.developmentwords.developmentwords.learn.ResultActivity
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.Quiz
import com.developmentwords.developmentwords.util.Voca
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InitialQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialQuizBinding
    private lateinit var list: List<Quiz>
    private val correctAnswerList = ArrayList<Voca>()
    private val wrongAnswerList = ArrayList<Voca>()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialQuizBinding.inflate(layoutInflater)
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
                    titleText.text = getChosung(list[index].title)
                    contextText.text = list[index].context

                    answerArea.setOnEditorActionListener { _, _, _ ->
                        val answer = answerArea.text.toString()
                        if (answer.isNotEmpty()) {
                            changeQuiz(answer)
                            answerArea.setText("")
                            true
                        } else {
                            false
                        }
                    }

                    closeButton.setOnClickListener {
                        finish()
                    }
                }
            }
        }
    }
    private fun changeQuiz(chk: String) {
        binding.apply {
            var answer = list[index].title

            val i = answer.indexOf("(")
            answer = if (i != -1) {
                answer.substring(0, i)
            } else {
                answer
            }

            if (chk == answer) {
                correctAnswerList.add(Voca(list[index].title, list[index].context, false))
            } else {
                wrongAnswerList.add(Voca(list[index].title, list[index].context, true))
            }
            index++
            Handler().postDelayed({
                if (index == (list.size)) {
                    val intent = Intent(baseContext, ResultActivity::class.java)
                    intent.putExtra("정답", correctAnswerList)
                    intent.putExtra("오답", wrongAnswerList)
                    intent.putExtra("이름", "initialQuiz")
                    startActivity(intent)
                    finish()
                } else {
                    titleText.text = getChosung(list[index].title)
                    contextText.text = list[index].context
                    progressBar.progress = index
                }
            }, 300)
        }
    }

    private fun getChosung(text: String): String {
        val unicodeBase = '가'.toInt() // 한글 시작 코드 포인트
        val chosungList = listOf(
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ",
            "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
        )

        var result = ""
        for (char in text) {
            if (char in '가'..'힣') {
                val unicode = char.toInt() - unicodeBase
                val chosungIndex = unicode / 21 / 28 // 21: 중성 개수, 28: 종성 개수
                result += chosungList[chosungIndex]
            } else {
                result += char
            }
        }

        return result
    }

}