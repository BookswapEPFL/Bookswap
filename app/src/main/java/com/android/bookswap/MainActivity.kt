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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.BooksFirestoreSource
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
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
import com.android.bookswap.ui.profile.NewUserScreen
import com.android.bookswap.ui.profile.OthersUserProfileScreen
import com.android.bookswap.ui.profile.UserProfile
import com.android.bookswap.ui.theme.BookSwapAppTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class MainActivity : ComponentActivity() {

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
    val apiKey = BuildConfig.MAPS_API_KEY
    if (!Places.isInitialized()) Places.initialize(applicationContext, apiKey)
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
      startDestination: String = C.Route.AUTH,
      photoStorage: PhotoFirebaseStorageRepository,
      messageStorage: OfflineMessageStorage,
      geolocation: IGeolocation = DefaultGeolocation(),
      context: Context
  ) {
    // navigation part
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    // user part
    // Firebase.auth.signOut() // Uncomment this line to test the sign in screen
    val currentUser = Firebase.auth.currentUser
    val userVM = UserViewModel(UUID.randomUUID(), userRepository)

    if (currentUser != null) {
      userVM.getUserByGoogleUid(currentUser.uid) // This will scrap the user from the database
    }
    // Message part
    val contactViewModel = ContactViewModel(userVM, userRepository, messageRepository)
    // Book part
    val bookFilter = BookFilter()
    val bookManagerViewModel =
        BookManagerViewModel(geolocation, bookRepository, userRepository, bookFilter)

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

    // CompositionLocalProvider provides LocalAppConfig to every child
    CompositionLocalProvider(LocalAppConfig provides AppConfig(userViewModel = userVM)) {
      NavHost(navController = navController, startDestination = startDestination) {
        navigation(startDestination = C.Screen.AUTH, route = C.Route.AUTH) {
          composable(C.Screen.AUTH) { SignInScreen(navigationActions) }
          composable(C.Screen.NEW_USER) { NewUserScreen(navigationActions, photoStorage) }
        }
        navigation(startDestination = C.Screen.CHAT_LIST, route = C.Route.CHAT_LIST) {
          composable(C.Screen.CHAT_LIST) {
            ListChatScreen(
                navigationActions,
                topAppBar = { topAppBar("Messages") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
                contactViewModel = contactViewModel)
          }
          composable("${C.Screen.CHAT}/{user2}") { backStackEntry ->
            val user2UUID = UUID.fromString(backStackEntry.arguments?.getString("user2"))
            val user2: DataUser? = contactViewModel.getUserInMessageBoxMap(user2UUID)
            if (user2 != null) {
              ChatScreen(
                  messageRepository,
                  userRepository,
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
                  booksRepository = bookRepository)
            }
          }
        }
        navigation(startDestination = C.Screen.MAP, route = C.Route.MAP) {
          composable(C.Screen.MAP) {
            MapScreen(
                bookManagerViewModel,
                navigationActions = navigationActions,
                geolocation = geolocation,
                topAppBar = { topAppBar("Map") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
          }
          composable(C.Screen.MAP_FILTER) { FilterMapScreen(navigationActions, bookFilter) }
        }
        navigation(startDestination = C.Screen.NEW_BOOK, route = C.Route.NEW_BOOK) {
          composable(C.Screen.NEW_BOOK) {
            BookAdditionChoiceScreen(
                navigationActions,
                topAppBar = { topAppBar("Add a Book") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
                photoFirebaseStorageRepository = photoStorage,
                booksRepository = bookRepository)
          }
          composable(C.Screen.ADD_BOOK_MANUALLY) {
            AddToBookScreen(
                bookRepository,
                topAppBar = { topAppBar("Add your Book") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
          }
          composable(C.Screen.ADD_BOOK_ISBN) {
            AddISBNScreen(
                navigationActions,
                bookRepository,
                topAppBar = { topAppBar(null) },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
            )
          }
        }
        navigation(startDestination = C.Screen.USER_PROFILE, route = C.Route.USER_PROFILE) {
          composable(C.Screen.USER_PROFILE) {
            UserProfile(
                photoStorage = photoStorage,
                booksRepository = bookRepository,
                navigationActions = navigationActions,
                topAppBar = { topAppBar("Your Profile") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
          }
          composable("${C.Screen.BOOK_PROFILE}/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.let { UUID.fromString(it) }

            if (bookId != null) {
              BookProfileScreen(
                  bookId = bookId, // Default for testing
                  booksRepository = BooksFirestoreSource(FirebaseFirestore.getInstance()),
                  navController = NavigationActions(navController),
              )
            } else {
              Log.e("Navigation", "Invalid bookId passed to BookProfileScreen")
            }
          }
          composable("${C.Screen.EDIT_BOOK}/{bookUUID}") { backStackEntry ->
            val bookUUID =
                backStackEntry.arguments?.getString("bookUUID")?.let { UUID.fromString(it) }
            EditBookScreen(
                booksRepository = bookRepository,
                navigationActions = NavigationActions(navController),
                bookUUID = bookUUID!!)
          }
        }
        navigation(
            startDestination = C.Screen.OTHERS_USER_PROFILE, route = C.Route.OTHERS_USER_PROFILE) {
              // OthersUserProfileScreen :
              composable("${C.Screen.OTHERS_USER_PROFILE}/{userId}") { backStackEntry ->
                val userId =
                    backStackEntry.arguments?.getString("userId")?.let { UUID.fromString(it) }
                Log.e("Main Launch OthersUserProfile", "userId: $userId")
                if (userId != null) {
                  OthersUserProfileScreen(
                      userId = userId,
                      booksRepository = bookRepository,
                      navigationActions = navigationActions,
                      topAppBar = { topAppBar("User Profile") },
                      bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
                } else {
                  Log.e("Navigation", "Invalid userId passed to OthersUserProfileScreen")
                }
              }
            }
      }
    }
  }
}
