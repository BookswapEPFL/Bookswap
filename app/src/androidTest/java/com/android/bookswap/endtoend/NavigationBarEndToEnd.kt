package com.android.bookswap.endtoend

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.bookswap.MainActivity
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationBarEndToEnd {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val db = FirebaseFirestore.getInstance()

            val messageRepository = MessageFirestoreSource(db)
            val bookRepository = BooksFirestoreRepository(db)
            MainActivity().BookSwapApp(messageRepository, bookRepository)
        }
    }

    @Test
    fun testNavigationBar() {
        // Check if the navigation bar is displayed
        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists()

        // Click on the Map tab and check if the MapScreen is displayed
        composeTestRule.onNodeWithTag("Map").assertExists().performClick()
        composeTestRule.onNodeWithTag("mapScreen").assertExists()

        // Click on the Add Book tab and check if the AddToBookScreen is displayed
        composeTestRule.onNodeWithTag("New Book").assertExists().performClick()
        composeTestRule.onNodeWithTag("addBookChoiceScreen").assertExists()

        // Click on the Chat tab and check if the ListChatScreen is displayed
        composeTestRule.onNodeWithTag("Chat").assertExists().performClick()
        composeTestRule.onNodeWithTag("chat_listScreen").assertExists()
    }
}