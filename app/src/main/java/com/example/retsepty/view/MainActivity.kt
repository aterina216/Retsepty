package com.example.retsepty.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retsepty.App
import com.example.retsepty.R
import com.example.retsepty.data.dagger.AppComponent
import com.example.retsepty.data.db.AppDataBase
import com.example.retsepty.data.network.RetrofitInstance
import com.example.retsepty.data.repo.MealRepository
import com.example.retsepty.databinding.ActivityMainBinding
import com.example.retsepty.view.adapters.MealAdapter
import com.example.retsepty.view.viewmodels.MealViewModel
import com.example.retsepty.view.viewmodels.ViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MealViewModel
    private lateinit var adapter: MealAdapter

    private val repository: MealRepository by lazy {
        MealRepository(
            AppDataBase.getDataBase(this).mealDao(),
            RetrofitInstance.api
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter = MealAdapter()

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository)
        )[MealViewModel::class.java]


        viewModel.meals.observe(this){
            meals -> adapter.updateMeals(meals)
        }
    }
}