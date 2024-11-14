package com.android.bookswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.chat.PermissionHandler
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.model.map.BookManagerViewModel
import com.android.bookswap.model.map.DefaultGeolocation
import com.android.bookswap.model.map.Geolocation
import com.android.bookswap.model.map.IGeolocation
import com.android.bookswap.resources.C
import com.android.bookswap.ui.authentication.SignInScreen
import com.android.bookswap.ui.books.add.AddISBNScreen
import com.android.bookswap.ui.books.add.AddToBookScreen
import com.android.bookswap.ui.books.add.BookAdditionChoiceScreen
import com.android.bookswap.ui.chat.ChatScreen
import com.android.bookswap.ui.chat.ListChatScreen
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.map.FilterMapScreen
import com.android.bookswap.ui.map.MapScreen
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.profile.UserProfile
import com.android.bookswap.ui.theme.BookSwapAppTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class MainActivity : ComponentActivity() {

  private lateinit var permissionHandler: PermissionHandler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // permissionHandler = PermissionHandler(this)
    // permissionHandler.askNotificationPermission()
    setContent { BookSwapApp() }
  }

  @Composable
  fun BookSwapApp() {

    // Initialize a Firebase Firestore database instance
    val db = FirebaseFirestore.getInstance()

    // Create the data source objects
    val messageRepository = MessageFirestoreSource(db)
    val bookRepository = BooksFirestoreRepository(db)
    val userDataSource = UserFirestoreSource(db)

    // Initialize the geolocation
    val geolocation = Geolocation(this)
    BookSwapAppTheme {
      // A surface container using the 'background' color from the theme
      Surface(
          modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
          color = MaterialTheme.colorScheme.background) {
            BookSwapApp(
                messageRepository, bookRepository, userDataSource, geolocation = geolocation)
          }
    }
  }

  @Composable
  fun BookSwapApp(
      messageRepository: MessageRepository,
      bookRepository: BooksRepository,
      userRepository: UsersRepository,
      startDestination: String = Route.AUTH,
      geolocation: IGeolocation = DefaultGeolocation()
  ) {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)
    val bookFilter = BookFilter()
    val userVM = UserViewModel(UUID.randomUUID(), userRepository)
    val bookManagerViewModel = BookManagerViewModel(geolocation, bookRepository, user, bookFilter)

    val currentUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    val otherUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    val currentUser =
        DataUser(
            currentUserUUID,
            "Hello",
            "Jaime",
            "Oliver Pastor",
            "",
            "",
            42.5717,
            0.5471,
            "https://media.istockphoto.com/id/693813718/photo/the-fortress-of-jaca-soain.jpg?s=612x612&w=0&k=20&c=MdnKl1VJIKQRwGdrGwBFx_L00vS8UVphR9J-nS6J90c=",
            emptyList(),
            "googleUid")

    val otherUser =
        DataUser(
            otherUserUUID,
            "Hey",
            "Théo",
            "Schlaeppi",
            "",
            "",
            46.3,
            6.43,
            "https://www.shutterstock.com/image-photo/wonderful-epesses-fairtytale-village-middle-600nw-2174791585.jpg",
            emptyList(),
            "googleUid")
    val placeHolder =
        listOf(MessageBox(otherUser, message = "Welcome message for user124", date = "01.01.24")) +
            List(6) {
              MessageBox(
                  DataUser(
                      UUID.randomUUID(),
                      "Hello",
                      "First ${it + 1}",
                      "Last ${it + 1}",
                      "",
                      "",
                      0.0,
                      0.0,
                      "",
                      emptyList(),
                      "googleUid"),
                  message = "Test message $it test for the feature of ellipsis in the message",
                  date = "01.01.24")
            }
    val topAppBar =
        @Composable { s: String? ->
          TopAppBarComponent(
              modifier = Modifier,
              navigationActions = navigationActions,
              title = s ?: navigationActions.currentRoute())
        }
    val bottomAppBar =
        @Composable { s: String? ->
          BottomNavigationMenu(
              onTabSelect = { destination -> navigationActions.navigateTo(destination) },
              tabList = List_Navigation_Bar_Destinations,
              selectedItem = s ?: "")
        }

    NavHost(navController = navController, startDestination = startDestination) {
      navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
        composable(Screen.AUTH) { SignInScreen(navigationActions) }
      }
      navigation(startDestination = Screen.CHATLIST, route = Route.CHAT) {
        composable(Screen.CHATLIST) {
          ListChatScreen(
              placeHolder,
              navigationActions,
              topAppBar = { topAppBar("Messages") },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
        }
        composable("${Screen.CHAT}/{user2}") { backStackEntry ->
          val user2UUID = UUID.fromString(backStackEntry.arguments?.getString("user2"))
          val user2 = placeHolder.firstOrNull { it.contact.userUUID == user2UUID }?.contact

          if (user2 != null) {
            ChatScreen(messageRepository, currentUser, user2, navigationActions)
          } else {
            BookAdditionChoiceScreen(
                navigationActions,
                topAppBar = { topAppBar("Add a Book") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
          }
        }
      }
      navigation(startDestination = Screen.MAP, route = Route.MAP) {
        composable(Screen.MAP) {
          MapScreen(
              bookManagerViewModel,
              navigationActions = navigationActions,
              geolocation = geolocation,
              topAppBar = { topAppBar("Map") },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
        }
        composable(Screen.FILTER) { FilterMapScreen(navigationActions, bookFilter) }
      }
      navigation(startDestination = Screen.NEWBOOK, route = Route.NEWBOOK) {
        composable(Screen.NEWBOOK) {
          BookAdditionChoiceScreen(
              navigationActions,
              topAppBar = { topAppBar("Add a Book") },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
        }
        composable(Screen.ADD_BOOK_MANUALLY) {
          AddToBookScreen(
              bookRepository,
              topAppBar = { topAppBar("Add your Book") },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
        }
        composable(Screen.ADD_BOOK_SCAN) { /*Todo*/}
        composable(Screen.ADD_BOOK_ISBN) {
          AddISBNScreen(
              navigationActions,
              bookRepository,
              topAppBar = { topAppBar(null) },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
        }
      }
      navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
        composable(Screen.PROFILE) { UserProfile(userVM) }
      }
    }
  }
}

// Temporary user list for the map as it is not yet linked to the database.
// Better to see how the map screen should look like at the end.
// Need to be removed in the future.
val user =
    listOf(
        DataUser(longitude = 0.04, latitude = 0.04, bookList = listOf(UUID(12345678L, 87654321L))),
        DataUser(longitude = -0.08, latitude = -0.08, bookList = listOf(UUID(-848484, 848484))),
        DataUser(
            longitude = 0.0,
            latitude = 0.0,
            bookList = listOf(UUID(763879565731911, 5074118859109511))))
