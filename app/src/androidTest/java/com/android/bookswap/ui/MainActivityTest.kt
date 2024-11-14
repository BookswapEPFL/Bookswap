package com.android.bookswap.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.bookswap.MainActivity
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
  @get:Rule val androidComposeRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testMainActivity() {
    androidComposeRule.onNodeWithTag("main_screen_container").assertExists()
    androidComposeRule.onNodeWithTag("SignInScreen").assertExists()
  }
}
