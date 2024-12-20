package com.android.bookswap.ui

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.android.bookswap.MainActivity
import com.android.bookswap.resources.C
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule
  val androidComposeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

  init {

    val listenerRegistration: ListenerRegistration = mockk()
    every { listenerRegistration.remove() } just runs

    val documentReference: CollectionReference = mockk()
    every { documentReference.addSnapshotListener(any()) } returns listenerRegistration

    val firebaseFirestore: FirebaseFirestore = mockk()
    every { firebaseFirestore.collection(any()) } returns documentReference

    mockkStatic(FirebaseFirestore::class)
    every { FirebaseFirestore.getInstance() } returns firebaseFirestore

    androidComposeRule = createAndroidComposeRule<MainActivity>()
  }

  @Test
  fun testMainActivity() {
    androidComposeRule.onNodeWithTag(C.Tag.main_screen_container).assertExists()
    androidComposeRule.onNodeWithTag(C.Tag.sign_in_screen_container).assertExists()
  }
}
