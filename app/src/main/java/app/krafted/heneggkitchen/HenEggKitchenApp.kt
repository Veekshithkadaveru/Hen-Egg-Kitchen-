package app.krafted.heneggkitchen

import android.app.Application
import app.krafted.heneggkitchen.data.RecipeRepository
import app.krafted.heneggkitchen.data.db.AppDatabase
import app.krafted.heneggkitchen.data.db.BookmarkDao

class HenEggKitchenApp : Application() {
    val recipeRepository: RecipeRepository by lazy { RecipeRepository(this) }
    val bookmarkDao: BookmarkDao by lazy { AppDatabase.getInstance(this).bookmarkDao() }
}
