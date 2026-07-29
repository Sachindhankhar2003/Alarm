package com.example.wakeupmath

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wakeupmath.ui.home.AlarmViewModel
import com.example.wakeupmath.ui.home.HomeScreen
import com.example.wakeupmath.ui.setalarm.SetAlarmScreen

import com.example.wakeupmath.ui.practice.PracticeScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val alarmViewModel: AlarmViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = alarmViewModel,
                onAddAlarmClick = { navController.navigate(Screen.SetAlarm.route) },
                onPracticeClick = { navController.navigate(Screen.Practice.route) }
            )
        }
        composable(Screen.SetAlarm.route) {
            SetAlarmScreen(
                onSaveClick = { hour, minute, label, difficulty, repeatDays, sound ->
                    alarmViewModel.addAlarm(hour, minute, label, difficulty, repeatDays, sound) {
                        navController.popBackStack()
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Practice.route) {
            PracticeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
