package com.oudombun.chelfy.feature.detail


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.oudombun.chelfy.R
import com.oudombun.chelfy.databinding.ActivityDetailsBinding
import com.oudombun.chelfy.core.model.AnalyzedInstruction
import com.oudombun.chelfy.core.model.ResultData
import com.oudombun.chelfy.core.model.StepDisplay
import com.oudombun.chelfy.feature.cooking.InstructionBottomSheet
import com.oudombun.chelfy.feature.cooking.toCookingSteps
import com.oudombun.chelfy.core.common.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup
import kotlinx.coroutines.launch
import java.text.BreakIterator
import java.util.Locale

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private val args by navArgs<DetailsActivityArgs>()

    private val viewModel: DetailViewModel by viewModels()

    private var recipeSteps = emptyList<StepDisplay>()
    private var recipeSaved = false
    private var favoriteMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(left = systemBars.left, right = systemBars.right)
            binding.toolbarInset.updatePadding(top = systemBars.top)
            binding.footerInset.updatePadding(bottom = systemBars.bottom)
            insets
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setSupportActionBar(binding.toolbard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupData(args.resultData)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.instructions.collect(::renderInstructions) }
                launch {
                    viewModel.favorites.collect { favorites ->
                        recipeSaved = favorites.any { it.result.id == args.resultData.id }
                        updateFavoriteIcon()
                    }
                }
                launch { viewModel.messages.collect(::showSnackBar) }
            }
        }

        binding.retryInstructions.setOnClickListener { viewModel.loadInstructions(args.resultData.id, refresh = true) }
        binding.startCook.setOnClickListener {
            if (recipeSteps.isNotEmpty() &&
                !supportFragmentManager.isStateSaved &&
                supportFragmentManager.findFragmentByTag(InstructionBottomSheet.TAG) == null
            ) {
                InstructionBottomSheet.newInstance(recipeSteps)
                    .showNow(supportFragmentManager, InstructionBottomSheet.TAG)
            }
        }
        viewModel.loadInstructions(args.resultData.id)
    }

    private fun renderInstructions(response: NetworkResult<List<AnalyzedInstruction>>) {
        binding.instructionProgress.isVisible = response is NetworkResult.Loading
        binding.retryInstructions.isVisible = response is NetworkResult.Error
        recipeSteps = when (response) {
            is NetworkResult.Success -> response.data.orEmpty().toCookingSteps()
            else -> emptyList()
        }
        binding.startCook.isVisible = recipeSteps.isNotEmpty()
        val ingredients = recipeSteps.flatMap { it.ingredients }
            .distinctBy { it.lowercase(Locale.ROOT) }
        binding.ingredientsHeading.isVisible = ingredients.isNotEmpty()
        binding.ingredientsList.isVisible = ingredients.isNotEmpty()
        binding.ingredientsList.text = ingredients.joinToString("\n") { "•  $it" }

        val status = when (response) {
            is NetworkResult.Loading -> R.string.loading_cooking_steps
            is NetworkResult.Error -> R.string.cooking_steps_error
            is NetworkResult.Success -> if (recipeSteps.isEmpty()) {
                R.string.no_cooking_steps
            } else {
                null
            }
        }
        binding.instructionStatus.isVisible = status != null
        status?.let(binding.instructionStatus::setText)
    }

    private fun setupData(recipe: ResultData) {
        binding.mainImageView.load(recipe.image)
        binding.titleText.text = recipe.title
        binding.cookTime.isVisible = recipe.readyInMinutes > 0
        binding.cookTime.text = getString(R.string.recipe_cook_time, recipe.readyInMinutes)

        val doc = Jsoup.parse(recipe.summary.orEmpty())
        val nutrition = doc.select("b, strong").map { it.text() }
        val values = listOf(
            Triple(binding.caloriesTxt, R.string.recipe_calories, caloriesPattern),
            Triple(binding.proteinTxt, R.string.recipe_protein, proteinPattern),
            Triple(binding.fatTxt, R.string.recipe_fat, fatPattern)
        )
        values.forEach { (view, label, pattern) ->
            val value = nutrition.firstNotNullOfOrNull { pattern.find(it)?.groupValues?.get(1) }
            view.isVisible = value != null
            view.text = value?.let { getString(label, it) }
        }
        binding.nutritionLayout.isVisible = values.any { (view, _, _) -> view.isVisible }

        val summary = doc.text()
        val sentences = BreakIterator.getSentenceInstance(Locale.ENGLISH).apply { setText(summary) }
        val end = sentences.next()
        binding.textDes.text = if (end == BreakIterator.DONE) summary else summary.take(end).trim()
        binding.textDes.isVisible = summary.isNotBlank()
    }

    private companion object {
        val caloriesPattern = Regex("""([0-9.,]+)\s+calories""", RegexOption.IGNORE_CASE)
        val proteinPattern = Regex("""([0-9.,]+\s*g)\s+of protein""", RegexOption.IGNORE_CASE)
        val fatPattern = Regex("""([0-9.,]+\s*g)\s+of fat""", RegexOption.IGNORE_CASE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.save_to_favorites_menu -> {
                viewModel.setFavorite(args.resultData, !recipeSaved)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        favoriteMenu = menu.findItem(R.id.save_to_favorites_menu)
        updateFavoriteIcon()
        return true
    }

    private fun updateFavoriteIcon() {
        favoriteMenu?.icon?.setTint(
            ContextCompat.getColor(this, if (recipeSaved) R.color.yellow else R.color.white)
        )
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.detailsLayout, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.footerInset)
            .setAction("Okay") {}
            .show()
    }

}
