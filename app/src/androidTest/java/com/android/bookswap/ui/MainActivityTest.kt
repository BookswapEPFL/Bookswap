package com.android.bookswap.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.bookswap.MainActivity
import com.android.bookswap.resources.C
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
  @get:Rule val androidComposeRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testMainActivity() {
    androidComposeRule.onNodeWithTag(C.Tag.main_screen_container).assertExists()
    androidComposeRule.onNodeWithTag(C.Tag.sign_in_screen_container).assertExists()
  }
}
