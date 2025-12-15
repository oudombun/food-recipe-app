package com.oudombun.chelfy.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.navArgs
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.oudombun.chelfy.R
import com.oudombun.chelfy.adapters.RecipeStepAdapter
import com.oudombun.chelfy.data.database.entities.FavoritesEntity
import com.oudombun.chelfy.databinding.ActivityDetailsBinding
import com.oudombun.chelfy.models.AnalyzedInstruction
import com.oudombun.chelfy.models.ResultData
import com.oudombun.chelfy.models.StepDisplay
import com.oudombun.chelfy.ui.fragments.instructions.InstructionBottomSheet
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup
import kotlin.random.Random

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private val args by navArgs<DetailsActivityArgs>()

    // ✅ In Activity use viewModels(), not activityViewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private val recipeSteps = mutableListOf<StepDisplay>()
    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar icons: white (because toolbar is dark)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false //false is light

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbard) { view, insets ->
            val statusBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            view.setPadding(
                view.paddingLeft,
                statusBarHeight,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setSupportActionBar(binding.toolbard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupData(args.resultData)

        mainViewModel.getDetailRecipe(args.resultData.id.toString())
        mainViewModel.recipesDetailResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.startCook.visibility = View.VISIBLE
                    binding.ingredientText.visibility = View.VISIBLE
                    binding.progressBar3.visibility = View.INVISIBLE
                    ingredient(response.data!!)
                }

                is NetworkResult.Error -> {
                    binding.startCook.visibility = View.INVISIBLE
                    binding.ingredientText.visibility = View.INVISIBLE
                    binding.progressBar3.visibility = View.INVISIBLE
                    Toast.makeText(this, response.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    binding.startCook.visibility = View.INVISIBLE
                    binding.ingredientText.visibility = View.INVISIBLE
                    binding.progressBar3.visibility = View.VISIBLE
                }
            }
        }

        handleListener()
    }

    private fun handleListener() {
        binding.startCook.setOnClickListener {
            val modalBottomSheet = InstructionBottomSheet.newInstance(recipeSteps)
            modalBottomSheet.show(supportFragmentManager, InstructionBottomSheet.TAG)
        }
    }

    private fun ingredient(extendedIngredients: List<AnalyzedInstruction>) {
        extendedIngredients.forEach { instruction ->
            instruction.steps.forEach { step ->
                val ingredientsList = step.ingredients.map { it.name }
                val ingredientsImageList = step.ingredients.map { it.image }
                val equipmentList = step.equipment.map { it.name }

                recipeSteps.add(
                    StepDisplay(
                        step.number,
                        step.step,
                        ingredientsList,
                        ingredientsImageList,
                        equipmentList,
                        step.length
                    )
                )
            }
        }

        var ingredients = ""
        for (step in recipeSteps) {
            if (step.ingredients.isNotEmpty()) ingredients += step.ingredients
        }

        val ingredientsList = ingredients
            .replace("[", "")
            .replace("]", "")
            .split(", ")
            .map { it.trim() }

        displayIngredients(this, ingredientsList)
    }

    private fun displayIngredients(context: Context, ingredientsList: List<String>) {
        val recipeStepsListView: ListView = binding.recipeStepsLayout
        val adapter = RecipeStepAdapter(context, ingredientsList)
        recipeStepsListView.adapter = adapter

        val listItem: View = recipeStepsListView.adapter.getView(0, null, recipeStepsListView)
        listItem.measure(0, 0)

        var totalHeight: Int = listItem.measuredHeight * recipeStepsListView.adapter.count
        totalHeight += recipeStepsListView.dividerHeight * (recipeStepsListView.adapter.count - 1)

        val params: ViewGroup.LayoutParams = recipeStepsListView.layoutParams
        params.height = totalHeight
        recipeStepsListView.layoutParams = params
        recipeStepsListView.requestLayout()
    }

    private fun setupData(myBundle: ResultData) {
        binding.mainImageView.load(myBundle.image)
        binding.titleText.text = myBundle.title

        val doc = Jsoup.parse(myBundle.summary)
        val nutrients = mutableMapOf<String, String>()

        for (element in doc.select("b")) {
            val text = element.text()
            if (text.contains("calories")) {
                nutrients["calories"] = text.split(" ")[0]
            } else if (text.contains("protein")) {
                nutrients["protein"] = text.substringBefore("of")
            } else if (text.contains("fat")) {
                nutrients["fat"] = text.substringBefore("of")
            }
        }

        binding.caloriesTxt.text = "${nutrients["calories"]}"
        binding.proteinTxt.text = "${nutrients["protein"]}"
        binding.fatTxt.text = "${nutrients["fat"]}"

        val staticPhrases = listOf(
            "This recipe is perfect for a satisfying meal! Get ready for a",
            "Enjoy a delicious and nourishing meal with this recipe! It offers a",
            "Looking for a budget-friendly and convenient meal? Try this recipe! It's a",
            "Discover a healthy and flavorful dish with this recipe! It boasts a"
        )

        val selectedPhrase = staticPhrases[Random.nextInt(staticPhrases.size)]
        val costKeyword = "per serving"
        val timeKeyword = "approximately"

        val sentences = myBundle.summary.split(".")
        val costSentence = cleanHtml(sentences.firstOrNull { it.contains(costKeyword, true) } ?: "")
        val timeSentence = cleanHtml(sentences.firstOrNull { it.contains(timeKeyword, true) } ?: "")

        binding.textDes.text = "$selectedPhrase $costSentence, $timeSentence"
    }

    private fun cleanHtml(sentence: String): String = Jsoup.parse(sentence).text()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.save_to_favorites_menu -> {
                if (!recipeSaved) saveToFavorites(item) else removeFromFavorites(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(0, args.resultData)
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(savedRecipeId, args.resultData)
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        val menuItem = menu?.findItem(R.id.save_to_favorites_menu)
        if (menuItem != null) checkSavedRecipes(menuItem)
        return true
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.detailsLayout, message, Snackbar.LENGTH_SHORT)
            .setAction("Okay") {}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this) { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity) {
                    if (savedRecipe.result.id == args.resultData.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                        return@observe
                    }
                }
                changeMenuItemColor(menuItem, R.color.white)
                recipeSaved = false
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }
}
