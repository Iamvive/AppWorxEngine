package com.appworx.appworxengine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.appworx.appworxengine.presentation.products.ProductListScreen
import com.appworx.appworxengine.ui.theme.AppWorxEngineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppEngineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppWorxEngineTheme {
                ProductListScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppWorxEngineTheme {
    }
}