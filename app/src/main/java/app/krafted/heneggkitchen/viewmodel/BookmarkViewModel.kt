package app.krafted.heneggkitchen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.heneggkitchen.data.RecipeRepository
import app.krafted.heneggkitchen.data.db.BookmarkDao
import app.krafted.heneggkitchen.data.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookmarkUiState(
    val bookmarkedRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true
)

class BookmarkViewModel(
    private val repository: RecipeRepository,
    private val bookmarkDao: BookmarkDao
) : ViewModel() {
    private val _state = MutableStateFlow(BookmarkUiState())
    val state: StateFlow<BookmarkUiState> = _state.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            bookmarkDao.getAllBookmarks()
                .catch { emit(emptyList()) }
                .collect { bookmarks ->
                    val recipes = bookmarks.mapNotNull { bookmark ->
                        repository.getRecipeById(bookmark.recipeId)
                    }
                    _state.update {
                        it.copy(
                            bookmarkedRecipes = recipes,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
