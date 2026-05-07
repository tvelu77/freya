package io.tvelu77.freya.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.tvelu77.freya.presentation.cycle.CycleScreen
import io.tvelu77.freya.presentation.food.FoodScreen
import io.tvelu77.freya.presentation.home.HomeScreen
import io.tvelu77.freya.presentation.profile.ProfileScreen

sealed class Screen(val route: String) {
  object Home  : Screen("home")
  object Cycle : Screen("cycle")
  object Food  : Screen("food")
  object Profile : Screen("profile")
}

@Composable
fun FreyaNavGraph(
  navController: NavHostController = rememberNavController()
) {
  NavHost(navController = navController, startDestination = Screen.Home.route) {
    composable(Screen.Home.route) {
      HomeScreen(
        onNavigateToCycle = { navController.navigate(Screen.Cycle.route) },
        onNavigateToFood  = { navController.navigate(Screen.Food.route) },
        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
      )
    }
    composable(Screen.Cycle.route) {
      CycleScreen(
        onNavigateBack = { navController.popBackStack() }
      )
    }
    composable(Screen.Food.route) {
      FoodScreen(
        onNavigateBack = { navController.popBackStack() }
      )
    }
    composable(Screen.Profile.route) {
      ProfileScreen(
        onNavigateBack = { navController.popBackStack() }
      )
    }
  }
}