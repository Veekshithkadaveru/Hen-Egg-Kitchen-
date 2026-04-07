package app.krafted.heneggkitchen.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val recipeId: Int,
    val savedAt: Long = System.currentTimeMillis()
)
