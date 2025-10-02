package com.example.retsepty

import android.app.Application
import com.example.retsepty.data.db.AppDataBase
import com.example.retsepty.data.network.RetrofitInstance
import com.example.retsepty.data.repo.MealRepository

class App: Application() {

    val database by lazy { AppDataBase.getDataBase(this) }
    val api by lazy { RetrofitInstance.api }
    val repo by lazy { MealRepository(database.mealDao(), api) }
}