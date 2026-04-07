package app.krafted.heneggkitchen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.heneggkitchen.data.RecipeRepository
import app.krafted.heneggkitchen.data.models.Recipe
import app.krafted.heneggkitchen.data.models.RecipeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<RecipeCategory> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Recipe> = emptyList()
)

class HomeViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val categories = repository.getCategories()
            _state.update { it.copy(categories = categories) }
        }
    }

    fun onSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList()) }
            return
        }
        val q = query.lowercase()
        val results = repository.getAllRecipes().filter { recipe ->
            recipe.title.lowercase().contains(q) ||
                recipe.ingredients.any { it.name.lowercase().contains(q) } ||
                recipe.tags.any { it.lowercase().contains(q) }
        }
        _state.update { it.copy(searchResults = results) }
    }
}
