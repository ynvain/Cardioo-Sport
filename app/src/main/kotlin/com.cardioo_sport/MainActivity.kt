package com.cardioo_sport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cardioo_sport.presentation.app.CardiooSportRoot
import com.cardioo_sport.presentation.theme.CardiooSportTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            CardiooSportTheme {
                CardiooSportRoot()
            }
        }
    }
}

