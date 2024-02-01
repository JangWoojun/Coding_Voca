package com.developmentwords.developmentwords.learn.next

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import com.developmentwords.developmentwords.databinding.ActivityNextBinding
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.PreferenceSingleton
import com.developmentwords.developmentwords.util.Voca
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class NextActivity : AppCompatActivity() {

    private var timerTask: Timer? = null
    private lateinit var binding: ActivityNextBinding
    private lateinit var list: List<Voca>
    private var time = 0
    private var index = 0
    private var stopChk = true
    private var tts: TextToSpeech? = null
    private var ttsChk = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(baseContext)
            val user = db?.userDao()!!.getUser()
            val myVocaDao = db.myVocaDao()

            val nowCourse = user.nowCourse.title

            val myVoca = myVocaDao.getMyVocaByTitle(nowCourse)
            list = myVoca[0].voca
            withContext(Dispatchers.Main) {
                tts = TextToSpeech(
                    applicationContext
                ) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tts!!.language = Locale.KOREA

                        ttsChk = if (PreferenceSingleton.prefs.getString("next tts") == "0") {
                            true
                        } else {
                            PreferenceSingleton.prefs.getString("next tts").toBoolean()
                        }

                        if (ttsChk) {
                            binding.ttsButton.setTextColor(Color.parseColor("#3D5CFF"))
                        } else {
                            binding.ttsButton.setTextColor(Color.parseColor("#c8c8c8"))
                        }
                        changeWord(index, true, ttsChk)
                        start()
                    }
                }

                binding.apply {

                    courseTitleText.text = nowCourse

                    progressBar.max = list.size

                    ttsButton.setOnClickListener {
                        ttsChk = if (ttsChk) {
                            ttsButton.setTextColor(Color.parseColor("#c8c8c8"))
                            false
                        } else {
                            ttsButton.setTextColor(Color.parseColor("#3D5CFF"))
                            true
                        }
                    }

                    stopButton.setOnClickListener {
                        if (stopChk) {
                            stop()
                            stopButton.imageTintList = ColorStateList.valueOf(Color.parseColor("#3D5CFF"))
                            stopChk = !stopChk
                        } else {
                            start()
                            stopButton.imageTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
                            stopChk = !stopChk
                        }
                    }
                    skipButton.setOnClickListener {
                        if (list.size-1 != index) {
                            index++
                            time = 0
                        } else {
                            index = 0
                            time = 0
                        }
                        changeWord(index, true, ttsChk)
                    }
                    closeButton.setOnClickListener {
                        finish()
                    }
                }
            }
        }
    }

    private fun start() {
        timerTask = kotlin.concurrent.timer(period = 10) {
            time++

            if (time == 250 || time == 0) {
                if (list.size-1 == index) {
                    index = 0
                    time = 0
                } else {
                    index++
                    time = 0
                }
                runOnUiThread {
                    changeWord(index, true, ttsChk)
                }
            } else if (time == 150) {
                runOnUiThread {
                    changeWord(index, false, ttsChk)
                }
            }

        }
    }

    private fun stop() {
        timerTask?.cancel()
    }

    private fun changeWord(index: Int, type: Boolean, ttsChk: Boolean) {
        binding.apply {
            if (type) {
                titleText.text = list[index].title
                contextText.visibility = View.INVISIBLE
                contextText.text = list[index].context
                progressBar.progress = index
                if (ttsChk) {
                    tts!!.speak(list[index].title, TextToSpeech.QUEUE_FLUSH, null)
                }
            } else {
                contextText.visibility = View.VISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stop()
        PreferenceSingleton.prefs.setString("next tts", ttsChk.toString())
    }

    override fun onPause() {
        super.onPause()
        stop()
        PreferenceSingleton.prefs.setString("next tts", ttsChk.toString())
    }
}