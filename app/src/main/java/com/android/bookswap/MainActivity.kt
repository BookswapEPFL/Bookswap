package com.android.bookswap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.theme.BookSwapAppTheme

class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
          // FCM SDK (and your app) can post notifications.
        } else {
          // TODO: Inform user that your app will not show notifications.
        }
      }

  private fun askNotificationPermission() {
    // This is only necessary for API level >= 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED) {
        // FCM SDK (and your app) can post notifications.
      } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
        // TODO: display an educational UI explaining to the user the features that will be enabled
        //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
        //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the
        // permission.
        //       If the user selects "No thanks," allow the user to continue without notifications.
      } else {
        // Directly ask for the permission
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      BookSwapAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              BookSwapApp()
            }
      }
    }

    // Ask for notification permission
    askNotificationPermission()
  }

  @Composable
  fun BookSwapApp() {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    NavHost(navController = navController, startDestination = Route.AUTH) {
      navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
        composable(Screen.AUTH) { /*Todo*/}
      }
      navigation(startDestination = Screen.CHATLIST, route = Route.CHAT) {
        composable(Screen.CHATLIST) { /*Todo*/}
        composable(Screen.CHAT) { /*Todo*/}
      }
      navigation(startDestination = Screen.MAP, route = Route.MAP) {
        composable(Screen.MAP) { /*Todo*/}
      }
      navigation(startDestination = Screen.NEWBOOK, route = Route.NEWBOOK) {
        composable(Screen.NEWBOOK) { /*Todo*/}
        composable(Screen.ADD_BOOK_MANUALLY) { /*Todo*/}
        composable(Screen.ADD_BOOK_SCAN) { /*Todo*/}
        composable(Screen.ADD_BOOK_ISBN) { /*Todo*/}
      }
    }
  }
}
