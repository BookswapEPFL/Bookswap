package com.android.bookswap.model

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.UUID
import com.android.bookswap.MainActivity

/**
 * Data stored across the whole app through Static CompositionLocal
 * Used for rarely updated data.
 * @param userViewModel the user view model storing the user information.
 */
data class AppConfig(
    val userViewModel: UserViewModel
)


/**
 * Static provider of the app Local config.
 * This is used by [MainActivity] in CompositionLocalProvider
 */
val LocalAppConfig = staticCompositionLocalOf {
    AppConfig(userViewModel = UserViewModel(UUID.randomUUID())) //By default the user doesn't really exist thus the random UUID
}