package com.mfarzan80.zerooneswitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.mfarzan80.zerooneswitch.ui.theme.ZeroOneSwitchTheme
import com.mfarzan80.zoneswitch.ZeroOneSwitch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeroOneSwitchTheme(darkTheme = false) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val value = remember { mutableStateOf(false) }
                    ZeroOneSwitch(
                        modifier = Modifier.scale(5f),
                        checked = value.value,
                        animationDuration = 1000,
                        thumbBounce = false,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF47e789),
                            uncheckedTrackColor = Color(0xFFFB4550),
                            checkedTrackAlpha = 1f,
                            uncheckedTrackAlpha = 1f,
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.White,
                        ),
                        onCheckedChange = { value.value = it })
                }
            }
        }
    }
}

