package com.example.retsepty.data.dagger

import android.app.Application
import com.example.retsepty.App
import com.example.retsepty.data.repo.MealRepository
import com.example.retsepty.view.MainActivity
import com.example.retsepty.view.viewmodels.ViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun repository(): MealRepository
    fun viewModelFactory(): ViewModelFactory

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application): Builder
        fun build() : AppComponent

    }

}