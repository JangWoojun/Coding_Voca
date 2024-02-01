package com.developmentwords.developmentwords.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.developmentwords.developmentwords.R
import com.developmentwords.developmentwords.intro.AgreementActivity
import com.developmentwords.developmentwords.util.ProgressUtil.createDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FBDB {
    companion object {
        private lateinit var database: DatabaseReference

        private fun readUserInfo(ref: String?, callback: ((Any) -> Unit)? = null) {
            database = Firebase.database.reference

            val myRef = if (ref != null) {
                database.child("user").child(FBAuth.getUid()).child(ref)
            } else {
                database.child("user").child(FBAuth.getUid())
            }

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    value?.let {
                        callback?.let { it1 -> it1(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("오류", error.toString())
                }
            })
        }

        fun setVersion(callback: () -> Unit) {
            PreferenceSingleton.prefs.setString("last update", getTodayDate())

            database = Firebase.database.reference
            val myRef = database.child("version")

            myRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    val version = value.toString().toInt()

                    PreferenceSingleton.prefs.setString("version", version.toString())
                    callback()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        fun downloadCourseItem(context: Context, callback: () -> Unit) {
            database = Firebase.database.reference
            val myRef = database.child("courseInfo")
            val list = arrayListOf<CourseItem>()
            var pinList = arrayListOf<String>()

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
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
                        val db = AppDatabase.getDatabase(context)
                        list.forEach {
                            db?.courseItemDao()!!.insertCourseItem(it)
                        }
                    }
                    callback()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("오류", error.toString())
                }
            })
        }

        fun deleteData(fragment: Fragment, chk: Boolean) {

            val loadingDialog = ProgressUtil.createLoadingDialog(fragment.requireContext())
            loadingDialog.show()

            CoroutineScope(Dispatchers.IO).launch {

                database = Firebase.database.reference

                val db = AppDatabase.getDatabase(fragment.requireContext())
                val myRef = database.child("user").child(FBAuth.getUid())

                db!!.userDao().deleteUser(db.userDao().getUser())
                db.myVocaDao().deleteMyVoca(db.myVocaDao().getMyVoca())
                db.courseItemDao().deleteCourseItem(db.courseItemDao().getCourseItem())

                if (chk) {
                    myRef.setValue(null)
                    val storageRef = FirebaseStorage.getInstance().reference.child("images/${FBAuth.getUid()}.jpg")
                    storageRef.delete()
                        .addOnSuccessListener {
                            FBAuth.getUser().delete().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    FBAuth.logOut()
                                    PreferenceSingleton.prefs.removeString()
                                    loadingDialog.dismiss()

                                    val intent = Intent(fragment.requireContext(), AgreementActivity::class.java)
                                    fragment.requireContext().startActivity(intent)
                                    fragment.requireActivity().finishAffinity()
                                } else {
                                    loadingDialog.dismiss()
                                    createDialog(fragment.requireContext(), false)
                                }
                            }
                        }
                        .addOnFailureListener {
                            FBAuth.logOut()
                            PreferenceSingleton.prefs.removeString()
                            loadingDialog.dismiss()

                            val intent = Intent(fragment.requireContext(), AgreementActivity::class.java)
                            fragment.requireContext().startActivity(intent)
                            fragment.requireActivity().finishAffinity()
                        }

                } else {
                    FBAuth.logOut()
                    PreferenceSingleton.prefs.removeString()
                    loadingDialog.dismiss()

                    val intent = Intent(fragment.requireContext(), AgreementActivity::class.java)
                    fragment.requireContext().startActivity(intent)
                    fragment.requireActivity().finishAffinity()
                }

            }
        }

        fun getTodayDate(): String {
            val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            return format.format(Calendar.getInstance().time)
        }

        fun uploadUserInfo(context: Context) {
            database = Firebase.database.reference
            val myRef = database.child("user").child(FBAuth.getUid())
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(context)
                val user = db?.userDao()!!.getUser()
                myRef.setValue(user)
            }
        }

        fun newUser(context: Context, callback: () -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                db?.userDao()!!.insertUser(
                    User(lastDate = getTodayDate(), todayWord = 0)
                )
                uploadUserInfo(context)
                callback()
            }
        }

        fun checkTodayWord(context: Context, onComplete: (() -> Unit)? = null) {
            readUserInfo(null) { it1 ->

                it1 as HashMap<*, *>
                val lastDate = it1["lastDate"]

                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(context)
                    val user = db?.userDao()?.getUser()

                    if (lastDate != getTodayDate()) {
                        user?.lastDate = getTodayDate()
                        user?.todayWord = 0
                        user?.let { db.userDao().updateUser(it) }
                    } else {
                        user?.todayWord = it1["todayWord"].toString().toInt()
                        user?.let { db.userDao().updateUser(it) }
                    }
                    onComplete?.let { it() }
                }
            }
        }

        fun checkGoogleLogin(context: Context, onComplete: () -> Unit) {
            database = Firebase.database.reference
            val myRef = database.child("user")

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value as HashMap<String, Any>
                    if (value.containsKey(FBAuth.getUid())) {
                        downloadUserInfo(context) {
                            checkTodayWord(context) {
                                onComplete()
                            }
                        }
                    } else {
                        newUser(context) {
                            onComplete()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("오류", error.toString())
                }
            })
        }

        fun downloadUserInfo(context: Context, onComplete: () -> Unit) {
            readUserInfo(null) { userInfo ->
                userInfo as HashMap<String, *>

                val pinCourseItem = userInfo["pinCourseItem"] as ArrayList<String>

                val now = userInfo["nowCourse"] as HashMap<String, *>
                val nowCourse = Course(
                    now["title"].toString(),
                    now["nowWord"].toString().toInt(),
                    now["maxWord"].toString().toInt(),
                    now["finishChk"].toString().toBoolean()
                )

                val my = userInfo["myCourse"] as ArrayList<HashMap<String, *>>
                val myCourse = my.map { course ->
                    Course(
                        course["title"].toString(),
                        course["nowWord"].toString().toInt(),
                        course["maxWord"].toString().toInt(),
                        course["finishChk"].toString().toBoolean()
                    )
                }

                val user = User(
                    id = userInfo["id"].toString().toInt(),
                    lastDate = getTodayDate(),
                    todayWord = userInfo["todayWord"].toString().toInt(),
                    userName = userInfo["userName"].toString(),
                    userImage = userInfo["userImage"].toString(),
                    pinCourseItem = pinCourseItem,
                    nowCourse = nowCourse,
                    myCourse = ArrayList(myCourse)
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(context)
                    db!!.userDao().insertUser(user)

                    myCourse.forEach { course ->
                        if (course.title == "") {
                            return@forEach
                        }
                        database = Firebase.database.reference
                        val myVocaRef = database.child("course").child(course.title)
                        val quizRef = database.child("quiz").child(course.title)

                        val vocaList = suspendCoroutine { continuation ->
                            myVocaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val value = snapshot.value as ArrayList<*>

                                    val vocaList = value.map { item ->
                                        item as ArrayList<*>
                                        Voca(item[0].toString(), item[1].toString(), false)
                                    }
                                    continuation.resume(vocaList)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("오류", error.toString())
                                    continuation.resumeWithException(error.toException())
                                }
                            })
                        }

                        val quizList = suspendCoroutine { continuation ->
                            quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val value = snapshot.value as ArrayList<*>

                                    val quizList = value.map { item ->
                                        item as ArrayList<*>
                                        Quiz(
                                            question = item[0].toString(),
                                            correctAnswer = item[1].toString().toInt(),
                                            oxQuestion = item[2].toString(),
                                            oxCorrectAnswer = item[3].toString().toBoolean(),
                                            answer1 = item[4].toString(),
                                            answer2 = item[5].toString(),
                                            answer3 = item[6].toString(),
                                            answer4 = item[7].toString(),
                                            title = item[8].toString(),
                                            context = item[9].toString()
                                        )
                                    }
                                    continuation.resume(quizList)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("오류", error.toString())
                                    continuation.resumeWithException(error.toException())
                                }
                            })
                        }

                        val myVoca = MyVoca(title = course.title, voca = ArrayList(vocaList), quiz = ArrayList(quizList))
                        db.myVocaDao().insertMyVoca(myVoca)
                    }

                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
            }
        }

        fun newCourse(item: CourseItem, context: Context, view: View) {
            val loadingDialog = ProgressUtil.createLoadingDialog(context)
            loadingDialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val user = db?.userDao()!!.getUser()

                val course: ArrayList<Course> = ArrayList(db.userDao().getUser().myCourse)
                course.add(Course(item.title, 0, item.wordCount, false))

                user.myCourse = ArrayList(course.distinct())
                user.nowCourse = Course(item.title, 0, item.wordCount, false)

                db.userDao().updateUser(user)

                database = Firebase.database.reference
                val myVocaRef = database.child("course").child(item.title)
                val quizRef = database.child("quiz").child(item.title)
                val vocaList = arrayListOf<Voca>()
                val quizList = arrayListOf<Quiz>()

                val myVocaSnapshot = suspendCancellableCoroutine { continuation ->
                    val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            continuation.resume(snapshot) { error ->
                                Log.d("오류", "작업이 취소되었습니다: $error")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resumeWithException(error.toException())
                        }
                    }

                    continuation.invokeOnCancellation {
                        myVocaRef.removeEventListener(listener)
                    }

                    myVocaRef.addListenerForSingleValueEvent(listener)
                }

                val quizSnapshot = suspendCancellableCoroutine { continuation ->
                    val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            continuation.resume(snapshot) { error ->
                                Log.d("오류", "작업이 취소되었습니다: $error")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resumeWithException(error.toException())
                        }
                    }

                    continuation.invokeOnCancellation {
                        quizRef.removeEventListener(listener)
                    }

                    quizRef.addListenerForSingleValueEvent(listener)
                }

                val vocaValue = myVocaSnapshot.value as ArrayList<*>
                vocaValue.forEach { it as ArrayList<String>
                    vocaList.add(Voca(it[0], it[1], false))
                }

                val quizValue = quizSnapshot.value as ArrayList<*>
                quizValue.forEach { it as ArrayList<*>
                    val quiz = Quiz(
                        question = it[0].toString(),
                        correctAnswer = it[1].toString().toInt(),
                        oxQuestion = it[2].toString(),
                        oxCorrectAnswer = it[3].toString().toBoolean(),
                        answer1 = it[4].toString(),
                        answer2 = it[5].toString(),
                        answer3 = it[6].toString(),
                        answer4 = it[7].toString(),
                        title = it[8].toString(),
                        context = it[9].toString()
                    )
                    quizList.add(quiz)
                }

                var vocaChk = true

                db.myVocaDao().getMyVoca().forEach {
                    if (it.title == item.title) {
                        vocaChk = false
                    }
                }

                if (vocaChk) {
                    db.myVocaDao().insertMyVoca(MyVoca(title = item.title, voca = vocaList, quiz = quizList))
                    uploadUserInfo(context)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "수강 신청 완료!", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                    view.findNavController().navigate(R.id.action_courseInnerFragment_to_word)
                }
            }
        }



    }
}
