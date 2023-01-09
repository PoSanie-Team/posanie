package dev.timatifey.posanie.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.timatifey.posanie.usecases.getLanguageFromPreferences
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PoSanieApp(activity = this)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) {
            super.attachBaseContext(null)
            return
        }

        val language = getLanguageFromPreferences(context = newBase)
        val config = newBase.resources.configuration
        config.setLocale(Locale(language.localeName))

        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}
