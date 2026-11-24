package io.tvelu77.freya.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.SetMeal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.tvelu77.freya.presentation.activity.ActivityScreen
import io.tvelu77.freya.presentation.cycle.CycleScreen
import io.tvelu77.freya.presentation.food.FoodScreen
import io.tvelu77.freya.presentation.home.HomeScreen
import io.tvelu77.freya.R
import io.tvelu77.freya.presentation.profile.ProfileScreen

sealed class Screen(val route: String, val labelRes: Int, val icon: ImageVector) {
  object Home  : Screen("home", R.string.nav_home, Icons.Rounded.Home)
  object Cycle : Screen("cycle", R.string.nav_cycle, Icons.Rounded.CalendarMonth)
  object Food  : Screen("food", R.string.nav_food, Icons.Rounded.SetMeal)
  object Activity : Screen("activity", R.string.nav_activity, Icons.Rounded.DirectionsRun)
  object Profile : Screen("profile", R.string.nav_profile, Icons.Rounded.AccountCircle)
}

@Composable
fun NavGraph(
  navController: NavHostController = rememberNavController()
) {
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      listOf(Screen.Home, Screen.Cycle, Screen.Food, Screen.Activity, Screen.Profile).forEach {
        item(
          icon = {
            Icon(
              imageVector = it.icon,
              contentDescription = stringResource(it.labelRes)
            )
          },
          label = { Text(stringResource(it.labelRes)) },
          selected = it.route == currentRoute,
          onClick = { navController.navigate(it.route, navOptions {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
            })
          }
        )
      }
    }
  ) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
      composable(Screen.Home.route) {
        HomeScreen(
          onNavigateToCycle = { navController.navigate(Screen.Cycle.route, navOptions {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
          }) },
          onNavigateToFood  = { navController.navigate(Screen.Food.route, navOptions {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
          }) }
        )
      }
      composable(Screen.Cycle.route) {
        CycleScreen()
      }
      composable(Screen.Food.route) {
        FoodScreen()
      }
      composable(Screen.Activity.route) {
        ActivityScreen()
      }
      composable(Screen.Profile.route) {
        ProfileScreen()
      }
    }
  }
}