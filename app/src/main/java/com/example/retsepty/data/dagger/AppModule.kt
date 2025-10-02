package com.example.retsepty.data.dagger

import android.app.Application
import android.content.Context
import com.example.retsepty.data.db.AppDataBase
import com.example.retsepty.data.db.MealDao
import com.example.retsepty.data.network.MealApi
import com.example.retsepty.data.network.RetrofitInstance
import com.example.retsepty.data.repo.MealRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): AppDataBase {
        return AppDataBase.getDataBase(app)
    }

    @Provides
    fun provideMealDao(dataBase: AppDataBase): MealDao{
        return dataBase.mealDao()
    }

    @Provides
    fun provideApi(): MealApi{
        return RetrofitInstance.api
    }

    @Singleton
    @Provides
    fun provideRepo(mealDao: MealDao, mealApi: MealApi): MealRepository{
        return MealRepository(mealDao, mealApi)
    }
}