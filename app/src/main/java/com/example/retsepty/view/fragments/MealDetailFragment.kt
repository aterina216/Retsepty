package com.example.retsepty.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.buildIntSet
import com.bumptech.glide.Glide
import com.example.retsepty.R
import com.example.retsepty.data.models.Meal
import com.example.retsepty.databinding.FragmentMealDetailBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MealDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MealDetailFragment : Fragment() {

    private lateinit var binding: FragmentMealDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMealDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val meal = arguments?.getParcelable<Meal>(ARGS)
        if(meal!=null){
            setupDetails(meal)
        }
    }


    private fun setupDetails(meal: Meal){
        Glide.with(this)
            .load(meal.strMealThumb)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.ivMealDetail)

        binding.tvMealNameDetail.text = meal.strMeal
        binding.tvCategoryDetail.text = meal.strCategory
        binding.tvAreaDetail.text = meal.strArea
        binding.tvInstructions.text = meal.strInstructions
    }

    companion object {
        private const val ARGS = "meal"

        fun newInstance(meal: Meal): MealDetailFragment {
            return MealDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGS, meal)
                }
            }
        }
    }
}