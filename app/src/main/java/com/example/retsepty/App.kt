package com.example.retsepty

import android.app.Application
import com.example.retsepty.data.dagger.AppComponent
import com.example.retsepty.data.dagger.DaggerAppComponent
import com.example.retsepty.data.db.AppDataBase
import com.example.retsepty.data.network.RetrofitInstance
import com.example.retsepty.data.repo.MealRepository

class App: Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}