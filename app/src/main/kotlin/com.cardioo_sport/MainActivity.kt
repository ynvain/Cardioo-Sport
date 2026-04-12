package com.cardioo_sport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cardioo_sport.presentation.theme.CardiooSportTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
      //  installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            CardiooSportTheme {
           //     CardiooRoot()
            }
        }
    }
}

