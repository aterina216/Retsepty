package com.example.retsepty.view.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.buildIntSet
import com.bumptech.glide.Glide
import com.example.retsepty.R
import com.example.retsepty.data.models.Meal
import com.example.retsepty.databinding.FragmentMealDetailBinding
import com.example.retsepty.util.ImageDownloader
import com.example.retsepty.util.PermissionManager

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

    private lateinit var imageDownloader: ImageDownloader

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

        println("🟢 Фрагмент создан, аргументы: ${arguments?.keySet()}")
        println("🟢 image_url: ${arguments?.getString("image_url")}")
        println("🟢 meal_name: ${arguments?.getString("meal_name")}")

        imageDownloader = ImageDownloader(requireContext())

        val meal = arguments?.getParcelable<Meal>(ARGS)
        if(meal!=null){
            setupDetails(meal)
        }

        binding.fabDownload.setOnClickListener {

            println("🟢 КНОПКА РАБОТАЕТ!")
            Toast.makeText(requireContext(), "Кнопка нажата!", Toast.LENGTH_SHORT).show()
            downloadImage()
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

    private fun downloadImage(){
        val meal = arguments?.getParcelable<Meal>(ARGS) ?: return.also {
            println("❌ Meal не найден в аргументах")
            Toast.makeText(requireContext(), "Ошибка: данные не найдены", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrl = meal.strMealThumb
        val mealName = meal.strMeal ?: "Рецепт"

        imageDownloader.downloadImage(imageUrl, mealName, this)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionManager.REQUEST_CODE_PERMISSION -> {
                val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    // Повторно запускаем скачивание при получении разрешения
                    downloadImage()
                } else {
                    Toast.makeText(requireContext(), "Разрешение отклонено", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}