package com.developmentwords.developmentwords.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.developmentwords.developmentwords.MainActivity
import com.developmentwords.developmentwords.databinding.ActivityMyCourseBinding
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.Course
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyCourseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backButton.setOnClickListener {
                startActivity(Intent(baseContext, MainActivity::class.java))
                finish()
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(applicationContext)
                val user = db?.userDao()!!.getUser()

                withContext(Dispatchers.Main) {
                    todayLearnWordText.text = "${user.todayWord}ea"
                    todayLearnWordProgress.progress = user.todayWord

                    myCourseList.layoutManager = GridLayoutManager(baseContext, 2)
                    if (user.myCourse.size > 1) {
                        val myCourse = user.myCourse
                        myCourse.remove(Course("", 0, 0, true))
                        myCourseList.adapter = MyCourseAdapter(myCourse)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(baseContext, MainActivity::class.java))
        finish()

    }
}