package com.example.remindme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remindme.ui.dialog.addreminder.AddReminder
import com.example.remindme.ui.dialog.addreminder.AddReminderDialog
import com.example.remindme.ui.screen.home.Home
import com.example.remindme.ui.screen.home.HomeScreen

/**
 * The main app composable.
 */
@Composable
fun RemindMeApp(modifier: Modifier = Modifier) {
    // Create a NavController persisting across recompositions
    val navController = rememberNavController()

    // NavHost
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        // Home Screen
        composable<Home> {
            HomeScreen(onAddReminderClick = {
                navController.navigate(AddReminder)
            })
        }

        // AddReminder Dialog
        composable<AddReminder> {
            AddReminderDialog(onDismiss = {
                // On dismiss pop the backstack
                if (!navController.popBackStack()) {
                    // If the backstack is empty, navigate to Home
                    navController.navigate(Home)
                }
            })
        }
    }
}