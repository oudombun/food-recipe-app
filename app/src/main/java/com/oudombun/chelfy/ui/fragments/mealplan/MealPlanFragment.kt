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
import kotlinx.coroutines.launch



@AndroidEntryPoint
class MealPlanFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()

    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!

    private val MAX_CLICKS_PER_DAY = 3
    private val COOLDOWN_TIME = (30 * 60 * 1000).toLong() // 30 minutes in milliseconds

    private var clickCount = 0
    private val handler: Handler = Handler() // For running code on the UI thread

    private var timer: CountDownTimer? = null
    private var meal = emptyList<Meal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel
        loadDataFromCache()

        mainViewModel.mealPlanResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.layoutMeal.visibility = View.VISIBLE
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

        binding.generateButton.setOnClickListener {
            if (clickCount >= MAX_CLICKS_PER_DAY) {
                timer?.cancel()
                startCooldownTimer()
                return@setOnClickListener
            }

            clickCount++
            generateMealPlan()
        }

        binding.rowBreakfast.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sourceUrl", meal[0].sourceUrl)
            view.findNavController().navigate(
                R.id.action_foodJokeFragment_to_mealPlanDetailFragment,
                bundle
            )
        }

        binding.rowLunch.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sourceUrl", meal[1].sourceUrl)
            view.findNavController().navigate(
                R.id.action_foodJokeFragment_to_mealPlanDetailFragment,
                bundle
            )
        }

        binding.rowDinner.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sourceUrl", meal[2].sourceUrl)
            view.findNavController().navigate(
                R.id.action_foodJokeFragment_to_mealPlanDetailFragment,
                bundle
            )
        }
    }

    private fun generateMealPlan() {
        binding.caloriesEdittext.text.clear()
        val selectedDiet: String = binding.dietSpinner.selectedItem.toString()
        val caloriesInput: String = binding.caloriesEdittext.text.toString()

        if (caloriesInput.isNotEmpty()) {
            mainViewModel.getMealPlan(selectedDiet, caloriesInput.toInt())
        } else {
            mainViewModel.getMealPlan(selectedDiet)
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readMealPlan.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    binding.layoutMeal.visibility = View.VISIBLE
                    binding.mealPlan = database[0].mealPlan
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }

    private fun startCooldownTimer() {
        binding.generateButton.isEnabled = false
        binding.generateButton.text = "Wait: 30m"

        timer = object : CountDownTimer(COOLDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                val timeLeft = String.format("%d:%02d", minutes, seconds)
                binding.generateButton.text = "Wait: $timeLeft"
            }

            override fun onFinish() {
                binding.generateButton.isEnabled = true
                binding.generateButton.text = "Generate"
                clickCount = 0
            }
        }.start()
    }
}