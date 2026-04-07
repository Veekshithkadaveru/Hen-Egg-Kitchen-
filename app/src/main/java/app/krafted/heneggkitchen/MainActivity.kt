package app.krafted.heneggkitchen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import app.krafted.heneggkitchen.ui.navigation.NavGraph
import app.krafted.heneggkitchen.ui.navigation.Screen
import app.krafted.heneggkitchen.ui.theme.HenEggKitchenTheme

class MainActivity : ComponentActivity() {
    private val app: HenEggKitchenApp by lazy { application as HenEggKitchenApp }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HenEggKitchenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val categories = app.recipeRepository.getCategories()
                        val totalRecipes = app.recipeRepository.getAllRecipes().size
                        Log.d(
                            "MainActivity",
                            "Loaded ${categories.size} categories, $totalRecipes recipes"
                        )

                        startDestination = Screen.Home.route
                    }

                    startDestination?.let { dest ->
                        NavGraph(
                            navController = navController,
                            startDestination = dest
                        )
                    }
                }
            }
        }
    }
}