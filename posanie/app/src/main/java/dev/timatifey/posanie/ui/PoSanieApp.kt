package dev.timatifey.posanie.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.navigation.compose.rememberNavController
import dev.timatifey.posanie.ui.theme.PoSanieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoSanieApp() {
    PoSanieTheme {
        val navController = rememberNavController()
        val bottomNavItems = listOf(
            BottomNavItems.Scheduler,
            BottomNavItems.Picker,
            BottomNavItems.Settings,
        )
        val popupDataList = remember { mutableStateListOf<PopupData>() }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController, items = bottomNavItems)
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                PoSanieNavGraph(
                    navController = navController,
                    createPopup = { visibilityState, content ->
                        popupDataList.add(PopupData(visibilityState, content))
                    }
                )
            }
        }

        for (popupData in popupDataList) {
            AppPopup(
                isVisible = popupData.visibilityState.value,
                onDismiss = { popupData.visibilityState.value = false },
                content = popupData.content
            )
        }

    }
}

class PopupData(
    val visibilityState: MutableState<Boolean>,
    val content: @Composable () -> Unit
)

@Composable
fun AppPopup(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (!isVisible) return

    val shadowColor = Color.Black.copy(alpha = 0.2f)
    Box(modifier = Modifier
        .fillMaxSize()
        .background(shadowColor)
        .clickable {  }
    )
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss
    ) {
        content()
    }
}