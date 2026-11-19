package com.oudombun.chelfy.feature.cooking

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.GridLayout
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import coil.dispose
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.InstructionBottomSheetBinding
import com.oudombun.chelfy.databinding.ItemCookingIngredientBinding
import com.oudombun.chelfy.core.model.StepDisplay
import com.oudombun.chelfy.core.common.Constants.Companion.BASE_IMAGE_URL
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class InstructionBottomSheet : BottomSheetDialogFragment(R.layout.instruction_bottom_sheet) {
    private var stepIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
        stepIndex = savedInstanceState?.getInt(STEP_INDEX_KEY) ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val screenHeight = resources.configuration.screenHeightDp * resources.displayMetrics.density
        dialog.behavior.maxHeight = (screenHeight * 0.9f).roundToInt()
        return dialog
    }

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val steps = arguments?.let {
            BundleCompat.getParcelableArrayList(it, RECIPE_STEPS_KEY, StepDisplay::class.java)
        }.orEmpty()
        if (steps.isEmpty()) {
            dismiss()
            return
        }

        val binding = InstructionBottomSheetBinding.bind(view)
        stepIndex = stepIndex.coerceIn(steps.indices)
        binding.stepProgress.max = steps.size
        renderStep(binding, steps)
        binding.closeCooking.setOnClickListener { dismiss() }
        binding.previousStep.setOnClickListener {
            if (stepIndex > 0) {
                stepIndex--
                renderStep(binding, steps)
            }
        }
        binding.nextStep.setOnClickListener {
            if (stepIndex == steps.lastIndex) {
                dismiss()
            } else {
                stepIndex++
                renderStep(binding, steps)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STEP_INDEX_KEY, stepIndex)
    }

    private fun renderStep(binding: InstructionBottomSheetBinding, steps: List<StepDisplay>) {
        val step = steps[stepIndex]
        binding.stepCounter.text = getString(R.string.cooking_step_progress, stepIndex + 1, steps.size)
        binding.stepProgress.progress = stepIndex + 1
        binding.stepInstruction.text = step.step
        binding.stepIngredients.isVisible = step.ingredients.isNotEmpty()
        binding.ingredientTiles.isVisible = step.ingredients.isNotEmpty()
        showIngredients(binding.ingredientTiles, step)

        val equipment = step.equipment.distinctBy { it.lowercase(Locale.ROOT) }
        binding.stepEquipment.isVisible = equipment.isNotEmpty()
        binding.stepEquipment.text = getString(
            R.string.cooking_step_equipment,
            equipment.joinToString(", ")
        )
        binding.previousStep.isEnabled = stepIndex > 0
        binding.previousStep.alpha = if (stepIndex > 0) 1f else 0.45f
        binding.nextStep.setText(
            if (stepIndex == steps.lastIndex) R.string.cooking_step_done else R.string.cooking_step_next
        )
        binding.stepScroll.scrollTo(0, 0)
    }

    private fun showIngredients(grid: GridLayout, step: StepDisplay) {
        for (index in 0 until grid.childCount) {
            grid.getChildAt(index).findViewById<ImageView>(R.id.ingredient_thumbnail).dispose()
        }
        grid.removeAllViews()

        val configuration = resources.configuration
        val columns = if (configuration.fontScale >= 1.3f || configuration.screenWidthDp < 360) 1 else 2
        val gap = (8 * resources.displayMetrics.density).roundToInt()
        grid.columnCount = columns

        val ingredients = step.ingredients.mapIndexed { index, name ->
            name to step.ingredientsImageList.getOrNull(index).orEmpty()
        }.distinctBy { (name, _) -> name.lowercase(Locale.ROOT) }

        ingredients.forEachIndexed { index, (name, image) ->
            val tile = ItemCookingIngredientBinding.inflate(layoutInflater, grid, false)
            val column = index % columns
            tile.ingredientName.text = name
            tile.root.layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(index / columns),
                GridLayout.spec(column, GridLayout.FILL, 1f)
            ).apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                if (column < columns - 1) marginEnd = gap
                bottomMargin = gap
            }
            grid.addView(tile.root)

            if (image.isNotEmpty()) {
                val url = if (image.startsWith("https://")) image else "$BASE_IMAGE_URL/${Uri.encode(image)}"
                tile.ingredientThumbnail.isVisible = true
                tile.ingredientThumbnail.load(url) {
                    listener(onError = { _, _ -> tile.ingredientThumbnail.isVisible = false })
                }
            }
        }
    }

    companion object {
        const val TAG = "InstructionBottomSheet"
        private const val RECIPE_STEPS_KEY = "recipe_steps"
        private const val STEP_INDEX_KEY = "step_index"

        fun newInstance(recipeSteps: List<StepDisplay>) = InstructionBottomSheet().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(RECIPE_STEPS_KEY, ArrayList(recipeSteps))
            }
        }
    }
}
