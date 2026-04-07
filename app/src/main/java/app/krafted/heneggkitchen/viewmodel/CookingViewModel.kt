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
    val recipe: Recipe? = null,
    val currentStep: Int = 0,
    val totalSteps: Int = 0
)

class CookingViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CookingUiState())
    val state: StateFlow<CookingUiState> = _state.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(recipeId) ?: return@launch
            _state.update {
                it.copy(
                    recipe = recipe,
                    currentStep = 0,
                    totalSteps = recipe.steps.size
                )
            }
        }
    }

    fun nextStep() {
        _state.update {
            if (it.currentStep < it.totalSteps - 1) {
                it.copy(currentStep = it.currentStep + 1)
            } else {
                it
            }
        }
    }

    fun previousStep() {
        _state.update {
            if (it.currentStep > 0) {
                it.copy(currentStep = it.currentStep - 1)
            } else {
                it
            }
        }
    }
}
