package com.example.retsepty

import android.app.Application
import com.example.retsepty.data.dagger.DaggerAppComponent
import com.example.retsepty.data.db.AppDataBase
import com.example.retsepty.data.network.RetrofitInstance
import com.example.retsepty.data.repo.MealRepository

class App: Application() {

    override fun onCreate() {

        super.onCreate()
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}