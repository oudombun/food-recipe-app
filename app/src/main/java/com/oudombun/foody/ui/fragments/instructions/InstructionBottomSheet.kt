package com.oudombun.foody.ui.fragments.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oudombun.foody.R
import com.oudombun.foody.models.StepDisplay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstructionBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.instruction_bottom_sheet, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipeSteps = arguments?.getParcelableArrayList<StepDisplay>(RECIPE_STEPS_KEY)
    }
    companion object {
        const val TAG = "InstructionBottomSheet"
        private const val RECIPE_STEPS_KEY = "recipe_steps"

        fun newInstance(recipeSteps: MutableList<StepDisplay>): InstructionBottomSheet {
            val args = Bundle()
            args.putParcelableArrayList(RECIPE_STEPS_KEY, ArrayList(recipeSteps))
            val fragment = InstructionBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }
}