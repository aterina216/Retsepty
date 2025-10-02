package com.example.retsepty.view.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retsepty.data.models.Meal
import com.example.retsepty.data.repo.MealRepository
import kotlinx.coroutines.launch

class MealViewModel(private val repo: MealRepository): ViewModel() {


    val meals: LiveData<List<Meal>> = repo.getMealsAndRefresh()

    fun refreshData(){
        viewModelScope.launch {
            repo.loadAndSaveMeals()
        }
    }

}