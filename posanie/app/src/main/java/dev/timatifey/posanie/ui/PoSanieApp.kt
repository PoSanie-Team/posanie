package dev.timatifey.posanie.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.timatifey.posanie.ui.theme.PoSanieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoSanieApp() {
    PoSanieTheme {
        val navController = rememberNavController()
        val bottomNavItems = listOf(
            BottomNavItems.Scheduler,
            BottomNavItems.Groups,
            BottomNavItems.Settings,
        )
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController, items = bottomNavItems)
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                PoSanieNavGraph(navController = navController)
            }
        }
    }
}