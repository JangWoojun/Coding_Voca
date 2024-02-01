package com.developmentwords.developmentwords.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.developmentwords.developmentwords.MainActivity
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.util.AppDatabase
import com.developmentwords.developmentwords.util.CourseItem
import com.developmentwords.developmentwords.util.FBDB
import com.developmentwords.developmentwords.util.FBDB.Companion.getTodayDate
import com.developmentwords.developmentwords.util.PreferenceSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth

        FBDB.checkTodayWord(applicationContext)

        val lastUpdate = if(PreferenceSingleton.prefs.getString("last update") == "0") {
            getTodayDate()
        } else {
            PreferenceSingleton.prefs.getString("last update")
        }

        val isGreaterThanSevenDays = isDateDifferenceGreaterThanSevenDays(lastUpdate)

        if (isGreaterThanSevenDays) {
            database = Firebase.database.reference
            val myRef = database.child("version")

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    val version = value.toString().toInt()
                    val myVersion = PreferenceSingleton.prefs.getString("version").toInt()

                    if (version != myVersion) {
                        PreferenceSingleton.prefs.setString("version", version.toString())
                        PreferenceSingleton.prefs.setString("last update", getTodayDate())
                        updateCourseItem()
                    } else {
                        PreferenceSingleton.prefs.setString("last update", getTodayDate())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("오류", error.toString())
                }
            })

        }

        Handler().postDelayed({
            if (auth.currentUser?.uid != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, AgreementActivity::class.java))
                finish()
            }
        },2500)
    }

    private fun isDateDifferenceGreaterThanSevenDays(dateString: String): Boolean {
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayDate = getTodayDate()
        val today = format.parse(todayDate)
        val targetDate = format.parse(dateString)

        val differenceInMillis = targetDate.time - today.time
        val differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24)

        return differenceInDays >= 7
    }

    private fun updateCourseItem() {
        database = Firebase.database.reference
        val myRef = database.child("courseInfo")
        val list = arrayListOf<CourseItem>()
        var pinList = arrayListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(baseContext)
            pinList = db!!.userDao().getUser().pinCourseItem
        }

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                value as ArrayList<ArrayList<*>>
                value.forEach {
                    var pinChk = false
                    pinList.forEach { pin ->
                        if (pin == it[0].toString()) {
                            pinChk = true
                        }
                    }

                    list.add(
                        CourseItem(
                            title = it[0].toString(), subject = it[1].toString(),
                            wordCount = it[2].toString().toInt(), level = it[3].toString(), levelSubject = it[4].toString(),
                            updateDate = it[5].toString(), pin = pinChk
                        )
                    )
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(baseContext)
                    db!!.courseItemDao().deleteCourseItem(db.courseItemDao().getCourseItem())
                    list.forEach {
                        db.courseItemDao().insertCourseItem(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("오류", error.toString())
            }
        })
    }
}