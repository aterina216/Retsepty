package com.example.retsepty.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retsepty.data.models.Meal

@Dao
interface MealDao {

    @Query("SELECT * FROM meals")
    fun selectAllMeals() : LiveData<List<Meal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<Meal>)

    @Query("DELETE FROM meals")
    suspend fun clearMeals()
}