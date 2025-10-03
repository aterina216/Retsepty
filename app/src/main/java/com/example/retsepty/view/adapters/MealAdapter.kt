package com.example.retsepty.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retsepty.R
import com.example.retsepty.data.models.Meal
import com.example.retsepty.databinding.ItemMealBinding

class MealAdapter(
    private var meals: List<Meal> = emptyList(),
    private val onMealClick: (Meal) -> Unit
) :
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: MealViewHolder,
        position: Int
    ) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    fun updateMeals(newMeals: List<Meal>) {
        this.meals = newMeals
        notifyDataSetChanged()
    }

    inner class MealViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(meal: Meal) {
            binding.tvMealName.text = meal.strMeal
            binding.tvCategory.text = "Категория + ${meal.strCategory}"
            binding.tvArea.text = meal.strArea

            Glide.with(binding.root)
                .load(meal.strMealThumb)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.ivMeal)

            binding.root.setOnClickListener {
                onMealClick.invoke(meal)
            }
        }

    }
}