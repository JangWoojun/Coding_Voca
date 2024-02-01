package com.developmentwords.developmentwords.learn.flash

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import com.developmentwords.developmentwords.databinding.ActivityFlashBinding
import com.developmentwords.developmentwords.learn.ResultActivity
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.PreferenceSingleton
import com.developmentwords.developmentwords.util.Voca
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class FlashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashBinding
    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var manager: CardStackLayoutManager
    private lateinit var list: List<Voca>
    private val correctAnswerList = ArrayList<Voca>()
    private val wrongAnswerList = ArrayList<Voca>()
    private var o = 0
    private var x = 0
    private var tts: TextToSpeech? = null
    private var ttsChk = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashBinding.inflate(layoutInflater)
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

                        ttsChk = if (PreferenceSingleton.prefs.getString("flash tts") == "0") {
                            true
                        } else {
                            PreferenceSingleton.prefs.getString("flash tts").toBoolean()
                        }

                        if (ttsChk) {
                            binding.ttsButton.setTextColor(Color.parseColor("#3D5CFF"))
                        } else {
                            binding.ttsButton.setTextColor(Color.parseColor("#c8c8c8"))
                        }

                        if (ttsChk) {
                            tts!!.speak(list[0].title, TextToSpeech.QUEUE_FLUSH, null)
                        }
                    }
                }

                manager = CardStackLayoutManager(baseContext,object : CardStackListener {
                    override fun onCardDragging(direction: Direction?, ratio: Float) {

                    }

                    override fun onCardSwiped(direction: Direction?) {
                        if (direction == Direction.Left) {
                            x++
                            wrongAnswerList.add(Voca(list[o+x-1].title, list[o+x-1].context, true))
                            isEndLearnCheck()
                        } else {
                            o++
                            correctAnswerList.add(list[o+x-1])
                            isEndLearnCheck()
                        }
                        if (ttsChk) {
                            if (o+x < list.size) {
                                tts!!.speak(list[o+x].title, TextToSpeech.QUEUE_FLUSH, null)
                            }
                        }
                    }

                    override fun onCardRewound() {

                    }

                    override fun onCardCanceled() {

                    }

                    override fun onCardAppeared(view: View?, position: Int) {

                    }

                    override fun onCardDisappeared(view: View?, position: Int) {

                    }

                })

                manager.setStackFrom(StackFrom.Bottom)
                manager.setSwipeThreshold(0.2f)

                cardStackAdapter = CardStackAdapter(list)

                binding.apply {

                    courseTitleText.text = nowCourse

                    cardStackView.layoutManager = manager
                    cardStackView.adapter = cardStackAdapter

                    progressBar.max = list.size
                    progressBar.progress = 0

                    ttsButton.setOnClickListener {
                        ttsChk = if (ttsChk) {
                            ttsButton.setTextColor(Color.parseColor("#c8c8c8"))
                            false
                        } else {
                            ttsButton.setTextColor(Color.parseColor("#3D5CFF"))
                            true
                        }
                    }

                    closeButton.setOnClickListener {
                        finish()
                    }

                    yesButton.setOnClickListener {
                        val setting1 = SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Right)
                            .build()
                        manager.setSwipeAnimationSetting(setting1)
                        binding.cardStackView.swipe()
                    }

                    noButton.setOnClickListener {
                        val setting1 = SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Left)
                            .build()
                        manager.setSwipeAnimationSetting(setting1)
                        binding.cardStackView.swipe()
                    }
                }

            }
        }

    }

    private fun isEndLearnCheck() {
        if (o+x == (list.size)) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("정답", correctAnswerList)
            intent.putExtra("오답", wrongAnswerList)
            intent.putExtra("이름", "Flash")
            startActivity(intent)
            finish()
        } else {
            binding.progressBar.progress = o+x
        }
    }

    override fun onStop() {
        super.onStop()
        PreferenceSingleton.prefs.setString("flash tts", ttsChk.toString())
    }

    override fun onPause() {
        super.onPause()
        PreferenceSingleton.prefs.setString("flash tts", ttsChk.toString())
    }
}