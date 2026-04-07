package app.krafted.heneggkitchen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.heneggkitchen.data.RecipeRepository
import app.krafted.heneggkitchen.data.db.BookmarkDao
import app.krafted.heneggkitchen.data.db.BookmarkEntity
import app.krafted.heneggkitchen.data.models.Ingredient
import app.krafted.heneggkitchen.data.models.Recipe
import app.krafted.heneggkitchen.data.scaleAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val currentServings: Int = 2,
    val isBookmarked: Boolean = false,
    val scaledIngredients: List<Ingredient> = emptyList()
)

class RecipeViewModel(
    private val repository: RecipeRepository,
    private val bookmarkDao: BookmarkDao
) : ViewModel() {
    private val _state = MutableStateFlow(RecipeDetailUiState())
    val state: StateFlow<RecipeDetailUiState> = _state.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        val recipe = repository.getRecipeById(recipeId) ?: return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    recipe = recipe,
                    currentServings = recipe.baseServings,
                    scaledIngredients = recipe.ingredients
                )
            }
        }
        viewModelScope.launch {
            bookmarkDao.isBookmarked(recipeId)
                .catch { emit(false) }
                .collect { bookmarked ->
                    _state.update { it.copy(isBookmarked = bookmarked) }
                }
        }
    }

    fun updateServings(newServings: Int) {
        if (newServings < 1 || newServings > 12) return
        _state.update { current ->
            val scaled = current.recipe?.ingredients?.map { ingredient ->
                ingredient.copy(
                    amount = scaleAmount(
                        baseAmount = ingredient.amount,
                        baseServings = current.recipe.baseServings,
                        targetServings = newServings
                    )
                )
            } ?: emptyList()
            current.copy(currentServings = newServings, scaledIngredients = scaled)
        }
    }

    fun toggleBookmark() {
        val recipe = _state.value.recipe ?: return
        viewModelScope.launch {
            if (_state.value.isBookmarked) {
                bookmarkDao.removeBookmark(recipe.id)
            } else {
                bookmarkDao.addBookmark(BookmarkEntity(recipe.id))
            }
        }
    }
}
