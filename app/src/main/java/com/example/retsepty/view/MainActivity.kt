package com.example.retsepty.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retsepty.App
import com.example.retsepty.R
import com.example.retsepty.data.dagger.AppComponent
import com.example.retsepty.data.db.AppDataBase
import com.example.retsepty.data.models.Meal
import com.example.retsepty.data.network.RetrofitInstance
import com.example.retsepty.data.repo.MealRepository
import com.example.retsepty.databinding.ActivityMainBinding
import com.example.retsepty.view.adapters.MealAdapter
import com.example.retsepty.view.fragments.MealDetailFragment
import com.example.retsepty.view.viewmodels.MealViewModel
import com.example.retsepty.view.viewmodels.ViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MealViewModel
    private lateinit var adapter: MealAdapter

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {

        (application as App).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                // Если стек пуст (все фрагменты закрыты), показываем CardView
                binding.cardView.visibility = View.VISIBLE
            } else {
                // Если есть фрагменты в стеке, убеждаемся, что CardView скрыт
                binding.cardView.visibility = View.GONE
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Вкусные рецепты"

        setupBottomNavigation()

        adapter = MealAdapter{
            meal -> openMealDetail(meal)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[MealViewModel::class.java]


        viewModel.meals.observe(this){
            meals -> adapter.updateMeals(meals)
        }

    }

    private fun openMealDetail(meal: Meal){

        val fragment = MealDetailFragment.newInstance(meal)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                // Реализуй поиск
                Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_favorites -> {
                // Реализуй избранное
                Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigation(){
        binding.bottomNavigation.setOnNavigationItemSelectedListener{
            item ->
            when(item.itemId){
                R.id.nav_home -> {
                    true
                }
                R.id.nav_favorites -> {
                    true
                }
                else -> false
            }
        }
    }
}