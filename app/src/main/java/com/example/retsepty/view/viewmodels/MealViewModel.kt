package com.example.retsepty.view.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retsepty.data.models.Meal
import com.example.retsepty.data.repo.MealRepository
import kotlinx.coroutines.launch

class MealViewModel: ViewModel() {

    private val repository = MealRepository()

    private val _meals = MutableLiveData<List<Meal>>()
    val meals: LiveData<List<Meal>> = _meals

    init{
        loadMeals()
    }

    fun loadMeals(){
        viewModelScope.launch {

            try {
                val mealList = repository.getMeals()
                _meals.value =mealList
            }
            catch (e: Exception){
                print(e.message)
            }
        }
    }
}