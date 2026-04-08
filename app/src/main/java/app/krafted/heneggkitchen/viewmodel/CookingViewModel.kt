package app.krafted.heneggkitchen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.heneggkitchen.data.RecipeRepository
import app.krafted.heneggkitchen.data.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CookingUiState(
    val recipe: Recipe? = null
)

class CookingViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CookingUiState())
    val state: StateFlow<CookingUiState> = _state.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(recipeId) ?: return@launch
            _state.update { it.copy(recipe = recipe) }
        }
    }
}
