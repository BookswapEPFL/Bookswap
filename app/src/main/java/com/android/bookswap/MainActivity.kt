package com.android.bookswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.theme.BookSwapAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookSwapAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .semantics { testTag = C.Tag.main_screen_container },
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookSwapApp()
                }
            }
        }
    }


    @Composable
    fun BookSwapApp() {
        val navController = rememberNavController()
        val navigationActions = NavigationActions(navController)

        NavHost(navController = navController, startDestination = Route.AUTH) {
            navigation(
                startDestination = Screen.AUTH,
                route = Route.AUTH
            ) {
                composable(Screen.AUTH) {/*Todo*/ }
            }
            navigation(
                startDestination = Screen.CHATLSIT,
                route = Route.CHAT
            ) {
                composable(Screen.CHATLSIT) {/*Todo*/ }
                composable(Screen.CHAT) {/*Todo*/ }
            }
            navigation(
                startDestination = Screen.MAP,
                route = Route.MAP
            ) {
                composable(Screen.MAP) {/*Todo*/ }
            }
            navigation(
                startDestination = Screen.NEWBOOK,
                route = Route.NEWBOOK
            ) {
                composable(Screen.NEWBOOK) {/*Todo*/ }
                composable(Screen.ADD_BOOK_MANUALLY) {/*Todo*/ }
                composable(Screen.ADD_BOOK_SCAN) {/*Todo*/ }
                composable(Screen.ADD_BOOK_ISBN) {/*Todo*/ }
            }
        }
    }
}