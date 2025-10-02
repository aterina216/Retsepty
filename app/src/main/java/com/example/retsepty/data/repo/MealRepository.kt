package com.example.retsepty.data.repo

import androidx.lifecycle.LiveData
import com.example.retsepty.data.db.MealDao
import com.example.retsepty.data.models.Meal
import com.example.retsepty.data.models.MealResponse
import com.example.retsepty.data.network.MealApi
import com.example.retsepty.data.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MealRepository(private val mealDao: MealDao,
    private val api: MealApi) {

    suspend fun getMealsFromCashe(): LiveData<List<Meal>>{

        return mealDao.selectAllMeals()

    }

    suspend fun loadAndSaveMeals(){

        try {
            val mealsNetwork = api.searchMeals().meals ?: emptyList()
            mealDao.clearMeals()
            mealDao.insertMeals(mealsNetwork)
        }
        catch (e: Exception){
            print(e.message)
        }
    }

   fun getMealsAndRefresh(): LiveData<List<Meal>>{
        CoroutineScope(Dispatchers.IO).launch{
            loadAndSaveMeals()
        }

        return mealDao.selectAllMeals()
    }
}