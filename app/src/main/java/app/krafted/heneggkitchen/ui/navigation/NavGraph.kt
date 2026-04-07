package app.krafted.heneggkitchen.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.krafted.heneggkitchen.HenEggKitchenApp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import app.krafted.heneggkitchen.ui.CategoryScreen
import app.krafted.heneggkitchen.ui.HomeScreen
import app.krafted.heneggkitchen.ui.RecipeDetailScreen
import app.krafted.heneggkitchen.viewmodel.HomeViewModel
import app.krafted.heneggkitchen.viewmodel.RecipeViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
        }
        composable(Screen.Home.route) {
            val app = LocalContext.current.applicationContext as HenEggKitchenApp
            val viewModel = remember { HomeViewModel(app.recipeRepository) }
            HomeScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(
            Screen.Category.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(280)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(280)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(280)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(280)
                )
            }
        ) {
            val categoryId = it.arguments?.getInt("categoryId") ?: return@composable
            val app = LocalContext.current.applicationContext as HenEggKitchenApp
            CategoryScreen(
                categoryId = categoryId,
                repository = app.recipeRepository,
                onRecipeClick = { recipeId -> navController.navigate(Screen.RecipeDetail.createRoute(recipeId)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(280)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(280)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(280)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(280)
                )
            }
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable
            val app = LocalContext.current.applicationContext as HenEggKitchenApp
            val viewModel = remember { RecipeViewModel(app.recipeRepository, app.bookmarkDao) }
            RecipeDetailScreen(
                viewModel = viewModel,
                recipeId = recipeId,
                onBackClick = { navController.popBackStack() },
                onStartCooking = { navController.navigate(Screen.CookingMode.createRoute(recipeId)) }
            )
        }
        composable(
            Screen.CookingMode.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(280)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(280)
                )
            }
        ) {
        }
        composable(
            Screen.Bookmarks.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(280)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(280)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(280)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(280)
                )
            }
        ) {
        }
        composable(
            Screen.Search.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(280)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(280)
                )
            }
        ) {
        }
    }
}
