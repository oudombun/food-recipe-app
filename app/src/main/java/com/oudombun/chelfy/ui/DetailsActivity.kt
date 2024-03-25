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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.oudombun.chelfy.R
import com.oudombun.chelfy.adapters.RecipeStepAdapter
import com.oudombun.chelfy.data.database.entities.FavoritesEntity
import com.oudombun.chelfy.models.AnalyzedInstruction
import com.oudombun.chelfy.models.ResultData
import com.oudombun.chelfy.models.StepDisplay
import com.oudombun.chelfy.ui.fragments.instructions.InstructionBottomSheet
import com.oudombun.chelfy.util.NetworkResult
import com.oudombun.chelfy.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.RawValue
import kotlinx.android.synthetic.main.activity_details.*
import org.jsoup.Jsoup
import kotlin.random.Random


@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()
    var recipeSteps = mutableListOf<StepDisplay>()
    private var recipeSaved = false
    private var savedRecipeId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setSupportActionBar(toolbard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setupData(args.resultData)

        mainViewModel.getDetailRecipe(args.resultData.id.toString())
        mainViewModel.recipesDetailResponse.observe(this){response->
            when (response) {
                is NetworkResult.Success -> {
                    start_cook.visibility =View.VISIBLE
                    ingredient_text.visibility =View.VISIBLE
                    progressBar3.visibility = View.INVISIBLE
                    ingredient(response.data!!)
                }

                is NetworkResult.Error -> {
                    start_cook.visibility =View.INVISIBLE
                    ingredient_text.visibility =View.INVISIBLE
                    progressBar3.visibility = View.INVISIBLE
                    Toast.makeText(
                        this,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    start_cook.visibility =View.INVISIBLE
                    ingredient_text.visibility =View.INVISIBLE
                    progressBar3.visibility = View.VISIBLE
                }
            }

        }
        handleListener()
    }

    private fun handleListener() {
        start_cook.setOnClickListener {
            val modalBottomSheet = InstructionBottomSheet.newInstance(recipeSteps)
            modalBottomSheet.show(supportFragmentManager, InstructionBottomSheet.TAG)
        }
    }

    private fun ingredient(extendedIngredients: @RawValue List<AnalyzedInstruction>) {
        extendedIngredients.let {
            for (instruction in it) {
                for (step in instruction.steps) {
                    val ingredientsList = step.ingredients.map { it.name }
                    val ingredientsImageList = step.ingredients.map { it.image }
                    val equipmentList = step.equipment.map { it.name }

                    val formattedStep = StepDisplay(
                        step.number,
                        step.step,
                        ingredientsList,
                        ingredientsImageList,
                        equipmentList,
                        step.length
                    )

                    recipeSteps.add(formattedStep)
                }
            }

            var ingredients =""

            for (step in recipeSteps) {
                if(step.ingredients.isNotEmpty()){
                    ingredients += step.ingredients
                }
            }

            val ingredientsList = ingredients
                .replace("[", "")
                .replace("]", "")
                .split(", ")
                .map { it.trim() }


            displayIngredients(this, ingredientsList)
        }
    }

    private fun displayIngredients(context: Context, ingredientsList: List<String>) {
        val recipeStepsListView = findViewById<ListView>(R.id.recipe_steps_layout)
        val adapter = RecipeStepAdapter(context, ingredientsList)
        recipeStepsListView.adapter = adapter

        val listItem: View = recipeStepsListView.adapter.getView(0, null, recipeStepsListView)
        listItem.measure(0, 0)
        var totalHeight: Int = listItem.measuredHeight * recipeStepsListView.getAdapter().getCount()

        totalHeight += recipeStepsListView.getDividerHeight() * (recipeStepsListView.getAdapter()
            .getCount() - 1)
        val params: ViewGroup.LayoutParams = recipeStepsListView.getLayoutParams()
        params.height = totalHeight
        recipeStepsListView.setLayoutParams(params)
        recipeStepsListView.requestLayout()
    }

    private fun setupData(myBundle: ResultData) {

        main_imageView.load(myBundle.image)
        title_text.text = myBundle.title


        val doc = Jsoup.parse(myBundle.summary)
        val nutrients = mutableMapOf<String, String>() // Store nutrient and value

        for (element in doc.select("b")) {
            val text = element.text()
            if (text.contains("calories")) {
                nutrients["calories"] = text.split(" ")[0]
            } else if (text.contains("protein")) {
                nutrients["protein"] = text.substringBefore("of") // Remove "of" and anything after
            } else if (text.contains("fat")) {
                nutrients["fat"] = text.substringBefore("of") // Remove "of" and anything after
            }
        }
        calories_txt.text = "${nutrients["calories"]}"
        protein_txt.text = "${nutrients["protein"]}"
        fat_txt.text ="${nutrients["fat"]}"

        val staticPhrases = listOf(
            "This recipe is perfect for a satisfying meal! Get ready for a",
            "Enjoy a delicious and nourishing meal with this recipe! It offers a",
            "Looking for a budget-friendly and convenient meal? Try this recipe! It's a",
            "Discover a healthy and flavorful dish with this recipe! It boasts a"
        )

        // Randomly select a phrase
        val selectedPhrase = staticPhrases[Random.nextInt(staticPhrases.size)]
        // Keywords
        val costKeyword = "per serving"
        val timeKeyword = "approximately"
//        val dietKeywords = listOf("gluten", "dairy")

        // Find sentences (assumes sentences are separated by '.')
        val sentences = myBundle.summary.split(".")

        val costSentence = cleanHtml(sentences.firstOrNull { it.contains(costKeyword, ignoreCase = true) } ?: "")
        val timeSentence = cleanHtml(sentences.firstOrNull { it.contains(timeKeyword, ignoreCase = true) } ?: "")

        val description = "$selectedPhrase $costSentence, $timeSentence"
        text_des.text = description
    }
    fun cleanHtml(sentence: String): String {
        return Jsoup.parse(sentence).text()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
            saveToFavorites(item)
        }
        else if (item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                0,
                args.resultData
            )
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }
    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                savedRecipeId,
                args.resultData
            )
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu,menu)
        val menuItem = menu?.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem!!)
        return true
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(
            detailsLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this, { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity) {
                    if (savedRecipe.result.id == args.resultData.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    } else {
                        changeMenuItemColor(menuItem, R.color.white)
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        })
    }
}
