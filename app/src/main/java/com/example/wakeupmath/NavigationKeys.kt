package com.example.wakeupmath

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SetAlarm : Screen("set_alarm")
    object Practice : Screen("practice")
}
