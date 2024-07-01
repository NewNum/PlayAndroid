package com.linx.playAndroid

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import coil.annotation.ExperimentalCoilApi
import com.linx.common.baseData.themeTypeState
import com.linx.playAndroid.ui.theme.CustomThemeManager
import com.linx.common.widget.TwoBackFinish
import com.linx.playAndroid.composable.MainCompose

class MainActivity : ComponentActivity() {

    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val themeState = themeTypeState.value

            //设置为沉浸式状态栏
            WindowCompat.setDecorFitsSystemWindows(window, false)

            //主题包裹
            CustomThemeManager.WanAndroidTheme(themeState) {
                //主界面
                MainCompose(onFinish = { finish() })
            }
        }

    }

}