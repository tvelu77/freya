package io.tvelu77.freya.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.tvelu77.freya.presentation.onboarding.disclaimer.DisclaimerPage
import io.tvelu77.freya.presentation.onboarding.preferences.PreferencesPage
import io.tvelu77.freya.presentation.onboarding.profile.ProfilePage
import io.tvelu77.freya.presentation.onboarding.target.TargetPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit,
                     viewModel: OnboardingViewModel = hiltViewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val pagerState = rememberPagerState(pageCount = { 4 })
  val scope = rememberCoroutineScope()

  val goNext: () -> Unit = {
    scope.launch {
      if (pagerState.currentPage < 3) {
        pagerState.animateScrollToPage(pagerState.currentPage + 1)
      } else {
        viewModel.onFinish(onFinished)
      }
    }
  }

  HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    userScrollEnabled = true
  ) { page ->
    when (page) {
      0 -> DisclaimerPage(
        currentStep = page,
        onNext = goNext
      )
      1 -> ProfilePage(
        currentStep = page,
        name = uiState.firstName,
        onNameChanged = viewModel::onFirstNameChanged,
        onNext = goNext
      )
      2 -> TargetPage(
        currentStep = page,
        calories = uiState.baseCalories,
        onCaloriesChanged = viewModel::onCaloriesChanged,
        onNext = goNext
      )
      3 -> PreferencesPage(
        currentStep = page,
        friendlyModeEnabled = uiState.friendlyModeEnabled,
        notificationsEnabled = uiState.notificationsEnabled,
        onFriendlyModeToggled = viewModel::onFriendlyModeToggled,
        onNotificationsToggled = viewModel::onNotificationsToggled,
        onNext = goNext
      )
    }
  }
}