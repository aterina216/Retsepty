package com.example.retsepty.data.network

import com.example.retsepty.data.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MealApi {

    @GET("api/json/v1/1/search.php")
    suspend fun searchMeals(@Query("s") query: String = "a"): MealResponse
}