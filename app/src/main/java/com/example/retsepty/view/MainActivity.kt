package com.example.retsepty.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retsepty.App
import com.example.retsepty.R
import com.example.retsepty.databinding.ActivityMainBinding
import com.example.retsepty.view.adapters.MealAdapter
import com.example.retsepty.view.viewmodels.MealViewModel
import com.example.retsepty.view.viewmodels.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MealViewModel
    private lateinit var adapter: MealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = (application as App).repo

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