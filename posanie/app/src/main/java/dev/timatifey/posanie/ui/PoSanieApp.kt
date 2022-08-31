package dev.timatifey.posanie.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import dev.timatifey.posanie.ui.theme.PoSanieTheme

@Composable
fun PoSanieApp() {
    PoSanieTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            PoSanieNavigationActions(navController)
        }
        PoSanieNavGraph(
            navController = navController
        )
    }
}