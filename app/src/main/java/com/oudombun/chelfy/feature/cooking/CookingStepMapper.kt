package com.oudombun.chelfy.feature.cooking

import com.oudombun.chelfy.core.model.AnalyzedInstruction
import com.oudombun.chelfy.core.model.StepDisplay

internal fun List<AnalyzedInstruction>.toCookingSteps(): List<StepDisplay> =
        flatMap { it.steps.orEmpty() }.mapNotNull { step ->
            val instruction = step.step.orEmpty().trim()
            if (instruction.isEmpty()) return@mapNotNull null

            val ingredients = step.ingredients.orEmpty()
                .map { it.name.orEmpty().trim() to it.image.orEmpty().trim() }
                .filter { (name, _) -> name.isNotEmpty() }
            StepDisplay(
                step.number,
                instruction,
                ingredients.map { (name, _) -> name },
                ingredients.map { (_, image) -> image },
                step.equipment.orEmpty().map { it.name.orEmpty().trim() }.filter(String::isNotEmpty),
                step.length
            )
        }
