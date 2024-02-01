package com.developmentwords.developmentwords.util

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.developmentwords.developmentwords.R
import java.io.Serializable

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return if (uriString.isNullOrEmpty()) null else Uri.parse(uriString)
    }

    @TypeConverter
    fun fromString(value: String): ArrayList<CourseItem> {
        val listType = object : TypeToken<ArrayList<CourseItem>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<CourseItem>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringList(value: ArrayList<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromQuizList(value: ArrayList<Quiz>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toQuizList(value: String): ArrayList<Quiz> {
        val listType = object : TypeToken<ArrayList<Quiz>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromVocaList(value: ArrayList<Voca>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toVocaList(value: String): ArrayList<Voca> {
        val listType = object : TypeToken<ArrayList<Voca>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCourse(course: Course): String {
        return Gson().toJson(course)
    }

    @TypeConverter
    fun toCourse(courseString: String): Course {
        return Gson().fromJson(courseString, Course::class.java)
    }

    @TypeConverter
    fun fromCourseList(courseList: ArrayList<Course>): String {
        return Gson().toJson(courseList)
    }

    @TypeConverter
    fun toCourseList(courseListString: String): ArrayList<Course> {
        val listType = object : TypeToken<ArrayList<Course>>() {}.type
        return Gson().fromJson(courseListString, listType)
    }
}

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var lastDate: String,
    var todayWord: Int,
    var userName: String = "평범한 유저",
    var userImage: String = "null",
    var pinCourseItem: ArrayList<String> = arrayListOf(""),
    var nowCourse: Course = Course("", 0, 0, true),
    var myCourse: ArrayList<Course> = arrayListOf(Course("", 0, 0, true))
)

@Entity
data class MyVoca(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    val title: String,
    val voca: ArrayList<Voca>,
    val quiz: ArrayList<Quiz>
)

@Entity
data class CourseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val wordCount: Int,
    val level: String,
    val levelSubject: String,
    val updateDate: String,
    var pin: Boolean = false
) : Serializable

data class Course(
    val title: String,
    var nowWord: Int,
    val maxWord: Int,
    var finishChk: Boolean
)

data class Voca(
    val title: String,
    val context: String,
    var chk: Boolean
) : Serializable

data class Quiz(
    val question: String,
    val correctAnswer: Int,
    val oxQuestion: String,
    val oxCorrectAnswer: Boolean,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    val title: String,
    val context: String,
)

data class PagerItem(
    val image: Int,
    val title: String,
    val subject: String
)
