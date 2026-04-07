package app.krafted.heneggkitchen.data.models

data class Recipe(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val prepMins: Int,
    val cookMins: Int,
    val baseServings: Int,
    val difficulty: String,
    val tags: List<String>,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val tip: String
)
