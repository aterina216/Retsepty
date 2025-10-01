package com.example.retsepty.data.repo

import com.example.retsepty.data.models.Meal
import com.example.retsepty.data.models.MealResponse
import com.example.retsepty.data.network.RetrofitInstance

class MealRepository {

    suspend fun getMeals(): List<Meal>{
      return  try {
          RetrofitInstance.api.searchMeals().meals ?: emptyList()
        }
      catch (e: Exception){
         emptyList()
      }
    }
}