package com.developmentwords.developmentwords.util

import androidx.room.*

@Dao
interface UserDAO {
    @Insert
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM User")
    fun getUser(): User

    @Delete
    fun deleteUser(user: User)
}

@Dao
interface MyVocaDao {
    @Insert
    fun insertMyVoca(myVoca: MyVoca)

    @Update
    fun updateMyVoca(myVoca: MyVoca)

    @Query("SELECT * FROM MyVoca")
    fun getMyVoca(): List<MyVoca>

    @Query("SELECT * FROM MyVoca WHERE title = :title")
    suspend fun getMyVocaByTitle(title: String): List<MyVoca>

    @Delete
    fun deleteMyVoca(myVoca: List<MyVoca>)
}

@Dao
interface CourseItemDao {
    @Insert
    fun insertCourseItem(courseItem: CourseItem)

    @Query("SELECT * FROM CourseItem")
    fun getCourseItem(): List<CourseItem>

    @Query("SELECT * FROM CourseItem WHERE pin = 1")
    fun getFavoriteCourseItems(): List<CourseItem>

    @Update
    fun updateCourseItem(courseItems: List<CourseItem>)

    @Delete
    fun deleteCourseItem(courseItem: List<CourseItem>)
}