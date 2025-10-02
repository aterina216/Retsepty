package com.example.retsepty.data.dagger

import android.app.Application
import com.example.retsepty.App
import com.example.retsepty.data.repo.MealRepository
import com.example.retsepty.view.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: App)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application): Builder
        fun build() : AppComponent

    }

}