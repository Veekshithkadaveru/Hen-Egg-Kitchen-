package app.krafted.heneggkitchen.data.models

data class RecipeCategory(
    val id: Int,
    val name: String,
    val icon: String,
    val background: String,
    val accentColor: String,
    val recipes: List<Recipe>
)
