package app.krafted.heneggkitchen.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Category : Screen("category/{categoryId}") {
        fun createRoute(categoryId: Int) = "category/$categoryId"
    }
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipe/$recipeId"
    }
    object CookingMode : Screen("cooking/{recipeId}") {
        fun createRoute(recipeId: Int) = "cooking/$recipeId"
    }
    object Bookmarks : Screen("bookmarks")
    object Search : Screen("search")
}
