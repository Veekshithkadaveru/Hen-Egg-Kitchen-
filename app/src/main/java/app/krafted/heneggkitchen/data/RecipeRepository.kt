package app.krafted.heneggkitchen.data

import android.content.Context
import app.krafted.heneggkitchen.data.models.Recipe
import app.krafted.heneggkitchen.data.models.RecipeCategory
import com.google.gson.Gson

private data class RecipesWrapper(val categories: List<RecipeCategory>)

class RecipeRepository(private val context: Context) {
    private var _categories: List<RecipeCategory>? = null

    fun getCategories(): List<RecipeCategory> {
        if (_categories == null) {
            val json = context.assets.open("recipes.json")
                .bufferedReader().use { it.readText() }
            val wrapper = Gson().fromJson(json, RecipesWrapper::class.java)
            _categories = wrapper.categories
        }
        return _categories!!
    }

    fun getAllRecipes(): List<Recipe> = getCategories().flatMap { it.recipes }

    fun getRecipeById(id: Int): Recipe? = getAllRecipes().find { it.id == id }

    fun getCategoryById(id: Int): RecipeCategory? = getCategories().find { it.id == id }

    fun getRecipesByCategoryId(categoryId: Int): List<Recipe> {
        return getCategoryById(categoryId)?.recipes ?: emptyList()
    }
}
