package com.android.bookswap

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.BooksFirestoreSource
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.model.chat.PermissionHandler
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.model.map.BookManagerViewModel
import com.android.bookswap.model.map.DefaultGeolocation
import com.android.bookswap.model.map.Geolocation
import com.android.bookswap.model.map.IGeolocation
import com.android.bookswap.resources.C
import com.android.bookswap.ui.authentication.SignInScreen
import com.android.bookswap.ui.books.BookProfileScreen
import com.android.bookswap.ui.books.add.AddISBNScreen
import com.android.bookswap.ui.books.add.AddToBookScreen
import com.android.bookswap.ui.books.add.BookAdditionChoiceScreen
import com.android.bookswap.ui.books.edit.EditBookScreen
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
import com.android.bookswap.ui.profile.NewUserScreen
import com.android.bookswap.ui.profile.UserProfile
import com.android.bookswap.ui.theme.BookSwapAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    val storage = FirebaseStorage.getInstance()

    val context = LocalContext.current

    // Create the data source objects
    val messageRepository = MessageFirestoreSource(db)
    val bookRepository = BooksFirestoreSource(db)
    val userDataSource = UserFirestoreSource(db)
    val photoStorage = PhotoFirebaseStorageSource(storage)
    val messageStorage = OfflineMessageStorage(context)

    // Initialize the geolocation
    val geolocation = Geolocation(this)
    BookSwapAppTheme {
      // A surface container using the 'background' color from the theme
      Surface(
          modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
          color = MaterialTheme.colorScheme.background) {
            BookSwapApp(
                messageRepository = messageRepository,
                bookRepository = bookRepository,
                userRepository = userDataSource,
                photoStorage = photoStorage,
                messageStorage = messageStorage,
                geolocation = geolocation,
                context = context)
          }
    }
  }

  @Composable
  fun BookSwapApp(
      messageRepository: MessageRepository,
      bookRepository: BooksRepository,
      userRepository: UsersRepository,
      startDestination: String = Route.AUTH,
      photoStorage: PhotoFirebaseStorageRepository,
      messageStorage: OfflineMessageStorage,
      geolocation: IGeolocation = DefaultGeolocation(),
      context: Context
  ) {
    // navigation part
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    // user part
    Firebase.auth.signOut() // Uncomment this line to test the sign in screen
    val currentUser = Firebase.auth.currentUser
    val userVM = UserViewModel(UUID.randomUUID(), userRepository)

    if (currentUser != null) {
      userVM.getUserByGoogleUid(currentUser.uid) // This will scrap the user from the database
    }
    // Book part
    val bookFilter = BookFilter()
    val bookManagerViewModel =
        BookManagerViewModel(geolocation, bookRepository, userRepository, bookFilter)

    val currentUserUUID = UUID.fromString("77942cd7-8b99-41ba-a0a5-147214703434")
    val otherUserUUID = UUID.fromString("7284fd9d-3edc-458b-93cd-2b0c4a8c0fc0")
    val testUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002")
    val currentUserPlaceholder =
        DataUser(
            currentUserUUID,
            "Mr.",
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
            "Mr.",
            "Théo",
            "Schlaeppi",
            "",
            "",
            46.3,
            6.43,
            "https://www.shutterstock.com/image-photo/wonderful-epesses-fairtytale-village-middle-600nw-2174791585.jpg",
            emptyList(),
            "googleUid")
    val testUser =
        DataUser(
            testUserUUID,
            "Mr.",
            "John",
            "Doe",
            "john.doe@hotmail.com",
            "+41999999999",
            0.0,
            0.0,
            "john_doe.jpg",
            emptyList(),
            "googleUid")

    val placeHolder =
        listOf(
            MessageBox(otherUser, message = "Welcome message for user124", date = "01.01.24"),
            MessageBox(
                currentUserPlaceholder,
                message = "Welcome message for user123",
                date = "01.01.24")) +
            List(5) {
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
            } +
            listOf(MessageBox(testUser, message = "Welcome message for test", date = "01.01.24"))
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
        composable(Screen.AUTH) { SignInScreen(navigationActions, userVM) }
        composable(Screen.NEW_USER) { NewUserScreen(navigationActions, userVM) }
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
            ChatScreen(
                messageRepository,
                userVM.getUser(),
                user2,
                navigationActions,
                photoStorage,
                messageStorage,
                context)
          } else {
            BookAdditionChoiceScreen(
                navigationActions,
                topAppBar = { topAppBar("Add a Book") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
                photoFirebaseStorageRepository = photoStorage,
                booksRepository = bookRepository,
                userUUID = currentUserUUID)
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
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
              photoFirebaseStorageRepository = photoStorage,
              booksRepository = bookRepository,
              userUUID = currentUserUUID)
        }
        composable(Screen.ADD_BOOK_MANUALLY) {
          AddToBookScreen(
              bookRepository,
              topAppBar = { topAppBar("Add your Book") },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
              userId = currentUserUUID)
        }
        composable(Screen.ADD_BOOK_SCAN) { /*Todo*/}
        composable(Screen.ADD_BOOK_ISBN) {
          AddISBNScreen(
              navigationActions,
              bookRepository,
              topAppBar = { topAppBar(null) },
              bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
              userId = currentUserUUID)
        }
      }
      navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
        composable(Screen.PROFILE) { UserProfile(userVM) }
        composable(Screen.BOOK_PROFILE) { backStackEntry ->
          val bookId = backStackEntry.arguments?.getString("bookId")?.let { UUID.fromString(it) }

          if (bookId != null) {
            BookProfileScreen(
                bookId = bookId ?: UUID.randomUUID(), // Default for testing
                booksRepository = BooksFirestoreSource(FirebaseFirestore.getInstance()),
                navController = NavigationActions(navController),
                currentUserId = UUID.randomUUID() // Pass the actual logged-in user ID
                )
          } else {
            Log.e("Navigation", "Invalid bookId passed to BookProfileScreen")
          }
        }
        composable("${Screen.EDIT_BOOK}/{bookId}") { backStackEntry ->
          val bookId = backStackEntry.arguments?.getString("bookId")?.let { UUID.fromString(it) }
          var book: DataBook? = null // How to create a book that will be assigned after ?
          // Fetch book data
          if (bookId != null) {

            bookRepository.getBook(
                uuid = bookId,
                OnSucess = { fetchedbook -> book = fetchedbook },
                onFailure = { Log.d("EditScreen", "Error while loading the book") })
            EditBookScreen(
                booksRepository = bookRepository,
                navigationActions = NavigationActions(navController),
                book = book!!)
          } else {
            Log.e("Navigation", "Invalid bookId passed to EditBookScreen")
          }
        }
      }
    }
  }
}
