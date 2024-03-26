package com.oudombun.chelfy.ui.fragments.mealplan

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.FragmentMealPlanBinding
import com.oudombun.chelfy.models.Meal
import com.oudombun.chelfy.models.MealPlanResponse
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_meal_plan.calories_edittext
import kotlinx.android.synthetic.main.fragment_meal_plan.diet_spinner
import kotlinx.android.synthetic.main.fragment_meal_plan.generate_button
import kotlinx.android.synthetic.main.fragment_meal_plan.layoutMeal
import kotlinx.android.synthetic.main.fragment_meal_plan.view.generate_button
import kotlinx.android.synthetic.main.fragment_meal_plan.view.row_breakfast
import kotlinx.android.synthetic.main.fragment_meal_plan.view.row_dinner
import kotlinx.android.synthetic.main.fragment_meal_plan.view.row_lunch
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MealPlanFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()

    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!

    private val MAX_CLICKS_PER_DAY = 3
    private val COOLDOWN_TIME = (30 * 60 * 1000 // 30 minutes in milliseconds
            ).toLong()

    private var clickCount = 0
    private val handler: Handler = Handler() // For running code on the UI thread

    private var timer: CountDownTimer? = null
    private var meal = emptyList<Meal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel
        loadDataFromCache()
        mainViewModel.mealPlanResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    layoutMeal.visibility =View.VISIBLE
                    binding.mealPlan = response.data!!
                    meal = response.data.meals
                }

                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.generate_button.setOnClickListener {
            if (clickCount >= MAX_CLICKS_PER_DAY) {
                timer?.cancel()
                startCooldownTimer();
                return@setOnClickListener;
            }

            clickCount++;
            generateMealPlan()
        }
        view.row_breakfast.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sourceUrl", meal[0].sourceUrl)
            view.findNavController().navigate(R.id.action_foodJokeFragment_to_mealPlanDetailFragment,bundle)
        }
        view.row_lunch.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sourceUrl", meal[1].sourceUrl)
            view.findNavController().navigate(R.id.action_foodJokeFragment_to_mealPlanDetailFragment,bundle)
        }
        view.row_dinner.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sourceUrl", meal[2].sourceUrl)
            view.findNavController().navigate(R.id.action_foodJokeFragment_to_mealPlanDetailFragment,bundle)
        }

    }

    private fun generateMealPlan() {
        calories_edittext.text.clear()
        val selectedDiet: String = diet_spinner.selectedItem.toString()
        val caloriesInput: String = calories_edittext.text.toString()
        if (caloriesInput.isNotEmpty()) {
            mainViewModel.getMealPlan(selectedDiet,caloriesInput.toInt())
        }else{
            mainViewModel.getMealPlan(selectedDiet)
        }
    }


    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readMealPlan.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    layoutMeal.visibility =View.VISIBLE
                    binding.mealPlan = database[0].mealPlan
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startCooldownTimer() {
        generate_button.isEnabled = false // Disable button
        generate_button.text = "Wait: 30m" // Set initial button text
        timer = object : CountDownTimer(COOLDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                val timeLeft = String.format("%d:%02d", minutes, seconds)
                generate_button.text = "Wait: $timeLeft"
            }

            override fun onFinish() {
                generate_button.isEnabled = true
                generate_button.text = "Generate"
                clickCount = 0 // Reset click count after cooldown
            }
        }.start()
    }
}