package io.tvelu77.freya.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.tvelu77.freya.presentation.home.HomeScreen

sealed class Screen(val route: String) {
  object Home  : Screen("home")
  object Cycle : Screen("cycle")
  object Food  : Screen("food")
}

@Composable
fun FreyaNavGraph(
  navController: NavHostController = rememberNavController()
) {
  NavHost(navController = navController, startDestination = Screen.Home.route) {
    composable(Screen.Home.route) {
      HomeScreen(
        onNavigateToCycle = { navController.navigate(Screen.Cycle.route) },
        onNavigateToFood  = { navController.navigate(Screen.Food.route) }
      )
    }
    composable(Screen.Cycle.route) {
      // TODO
    }
    composable(Screen.Food.route) {
      // TODO
    }
  }
}