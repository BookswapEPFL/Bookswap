package com.android.bookswap.ui.chat

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: ContactViewModel
  private lateinit var messageBoxMapStateFlow: MutableStateFlow<Map<UUID, MessageBox>>
  private lateinit var placeHolderDataEmpty: ContactViewModel

  @Before
  fun setUp() {
    // Not empty mocking data
    placeHolderData = mockk()
    val generatedMap =
        List(4) { index ->
              UUID.randomUUID() to
                  MessageBox(
                      DataUser(
                          userUUID = UUID.randomUUID(),
                          greeting = "Hello",
                          firstName = "First ${index + 1}",
                          lastName = "Last ${index + 1}",
                          email = "",
                          phoneNumber = "",
                          latitude = 0.0,
                          longitude = 0.0,
                          profilePictureUrl = if (index % 2 == 0) "" else "https://i.pinimg.com/236x/54/72/d1/5472d1b09d3d724228109d381d617326.jpg",
                          bookList = emptyList(),
                          googleUid = "googleUid"),
                      message =
                          "Test message $index test for the feature of ellipsis in the message",
                      date = "01.01.24")
            }
            .toMap()
    messageBoxMapStateFlow = MutableStateFlow(generatedMap)
    every { placeHolderData.messageBoxMap } returns messageBoxMapStateFlow
    every { placeHolderData.updateMessageBoxMap() } just runs
    // Empty mocking data
    placeHolderDataEmpty = mockk()
    val generatedMapEmpty = emptyMap<UUID, MessageBox>()
    messageBoxMapStateFlow = MutableStateFlow(generatedMapEmpty)
    every { placeHolderDataEmpty.messageBoxMap } returns messageBoxMapStateFlow
    every { placeHolderDataEmpty.updateMessageBoxMap() } just runs
  }

  @Test
  fun hasRequiredComponentsWithMessage() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                tabList = List_Navigation_Bar_Destinations,
                selectedItem = navigationActions.currentRoute())
          },
          contactViewModel = placeHolderData)
    }
    composeTestRule.onNodeWithTag(C.Tag.top_app_bar_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.profile_button).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.ChatList.scrollable).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.bottom_navigation_menu_container).assertIsDisplayed()

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Messages")
  }

  @Test
  fun hasRequiredComponentsWithMessageEmpty() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                tabList = List_Navigation_Bar_Destinations,
                selectedItem = navigationActions.currentRoute())
          },
          contactViewModel = placeHolderDataEmpty)
    }
    composeTestRule.onNodeWithTag(C.Tag.top_app_bar_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.profile_button).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.ChatList.scrollable).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.bottom_navigation_menu_container).assertIsDisplayed()

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Messages")
    composeTestRule
        .onNodeWithTag(C.Tag.ChatList.scrollable)
        .onChild()
        .assertTextEquals("No messages yet")
  }

  @Test
  fun profilPicsNotEmptyAreDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                tabList = List_Navigation_Bar_Destinations,
                selectedItem = navigationActions.currentRoute())
          },
          contactViewModel = placeHolderData)
    }
    composeTestRule.onAllNodesWithContentDescription("Contact Icon").assertCountEquals(2)
    composeTestRule.onAllNodesWithContentDescription("Contact Icon empty").assertCountEquals(2)

  }
  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                tabList = List_Navigation_Bar_Destinations,
                selectedItem = navigationActions.currentRoute())
          },
          contactViewModel = placeHolderData)
    }
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.profile_button).assertHasClickAction()
  }

  @Test
  fun allMessageBoxesAreClickable() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                tabList = List_Navigation_Bar_Destinations,
                selectedItem = navigationActions.currentRoute())
          },
          contactViewModel = placeHolderData)
    }

    val messageNodes = composeTestRule.onAllNodesWithTag(C.Tag.ChatList.item)
    assert(messageNodes.fetchSemanticsNodes().isNotEmpty())
    messageNodes.assertAll(hasClickAction())
  }
}
