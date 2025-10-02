package com.example.retsepty.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.retsepty.data.models.Meal
import kotlin.reflect.KClass

@Database(entities = [Meal::class], version = 1, exportSchema = false )
abstract class AppDataBase: RoomDatabase() {

    abstract fun mealDao(): MealDao

    companion object{

        fun getDataBase(context: Context): AppDataBase{
            return Room.databaseBuilder(context,
                AppDataBase::class.java,
                "meals_db")
                .build()
        }
    }
}