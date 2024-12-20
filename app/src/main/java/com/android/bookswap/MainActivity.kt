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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.bookswap.data.DataMessage
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
import com.android.bookswap.model.NotificationService
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.model.edit.EditBookViewModel
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class MainActivity : ComponentActivity() {

  private var chatListener: ListenerRegistration? = null
  private lateinit var notificationService: NotificationService

  private fun listenForChatUpdates(userViewModel: UserViewModel) {
    val db = FirebaseFirestore.getInstance()

    chatListener =
        db.collection("chats").addSnapshotListener { snapshot, e ->
          if (e != null) {
            Log.e("Firestore", "Listen failed: $e")
            return@addSnapshotListener
          }

          if (snapshot != null && !isAppInForeground) {
            for (change in snapshot.documentChanges) {
              if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                val newMessage = change.document.toObject(DataMessage::class.java)

                // Check if the current user is the receiver
                if (newMessage.receiverUUID == userViewModel.uuid) {
                  val senderName = newMessage.senderUUID.toString()
                  val messageContent = newMessage.text

                  // Send a notification
                  notificationService.sendNotification(
                      "New Message", "From $senderName: $messageContent")
                }
              }
            }
          }
        }
  }

  override fun onDestroy() {
    super.onDestroy()
    // Remove the listener to avoid memory leaks
    chatListener?.remove()
  }

  companion object {
    var isAppInForeground = false
  }

  override fun onStart() {
    super.onStart()
    isAppInForeground = true
  }

  override fun onStop() {
    super.onStop()
    isAppInForeground = false
  }

  /**
   * Called when the activity is starting. This is where most initialization should go.
   *
   * @param savedInstanceState If the activity is being re-initialized after previously being shut
   *   down then this Bundle contains the data it most recently supplied in
   *   onSaveInstanceState(Bundle).
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { BookSwapApp() }
  }
  /**
   * Composable function for the main BookSwap application. Initializes Firebase Firestore, Firebase
   * Storage, and other data sources. Sets up the geolocation and applies the app theme.
   */
  @Composable
  fun BookSwapApp() {
    // Initialize a Firebase Firestore database instance
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    val context = LocalContext.current

    notificationService = NotificationService(context)

    // Create the data source objects
    val messageRepository = MessageFirestoreSource(db)
    val bookRepository = BooksFirestoreSource(db)
    val userRepository = UserFirestoreSource(db)
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
                userRepository = userRepository,
                photoStorage = photoStorage,
                messageStorage = messageStorage,
                geolocation = geolocation,
                context = context)
          }
    }
  }
  /**
   * Composable function for the main BookSwap application.
   *
   * @param messageRepository The repository for handling messages.
   * @param bookRepository The repository for handling books.
   * @param userRepository The repository for handling users.
   * @param startDestination The initial destination for navigation.
   * @param photoStorage The repository for handling photo storage.
   * @param messageStorage The storage for offline messages.
   * @param geolocation The geolocation service.
   * @param context The context of the application.
   */
  @Composable
  fun BookSwapApp(
      messageRepository: MessageRepository,
      bookRepository: BooksRepository,
      startDestination: String = C.Route.AUTH,
      photoStorage: PhotoFirebaseStorageRepository,
      messageStorage: OfflineMessageStorage,
      geolocation: IGeolocation = DefaultGeolocation(),
      userRepository: UsersRepository,
      userVM: UserViewModel = UserViewModel(UUID.randomUUID(), userRepository),
      context: Context
  ) {
    // navigation part
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    // user part
    // Firebase.auth.signOut() // Uncomment this line to test the sign in screen
    val currentUser = Firebase.auth.currentUser

    if (currentUser != null) {
      userVM.getUserByGoogleUid(currentUser.uid) // This will scrap the user from the database
      Thread.sleep(500)
    }

    // Book part
    val bookFilter = BookFilter()

    val bookManagerVM =
        BookManagerViewModel(geolocation, bookRepository, userRepository, bookFilter)

    val contactViewModel = ContactViewModel(userVM, userRepository, messageRepository)

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
      listenForChatUpdates(userVM)

      NavHost(navController = navController, startDestination = startDestination) {
        navigation(startDestination = C.Screen.AUTH, route = C.Route.AUTH) {
          composable(C.Screen.AUTH) { SignInScreen(navigationActions) }
          composable(C.Screen.NEW_USER) { NewUserScreen(navigationActions, photoStorage) }
        }
        navigation(startDestination = C.Screen.CHAT_LIST, route = C.Route.CHAT_LIST) {
          // Message part
          composable(C.Screen.CHAT_LIST) {
            ListChatScreen(
                navigationActions,
                topAppBar = { topAppBar(stringResource(R.string.chat_list_screen_title)) },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
                contactViewModel = contactViewModel)
          }
          composable("${C.Screen.CHAT}/{user2}") { backStackEntry ->
            val user2UUID = UUID.fromString(backStackEntry.arguments?.getString("user2"))
            userVM.addContact(user2UUID)
            val user2 = userVM.listOfContacts.find { it.userUUID == user2UUID }
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
            }
          }
        }
        navigation(startDestination = C.Screen.MAP, route = C.Route.MAP) {
          composable(C.Screen.MAP) {
            MapScreen(
                bookManagerViewModel = bookManagerVM,
                navigationActions = navigationActions,
                geolocation = geolocation,
                topAppBar = { topAppBar(stringResource(R.string.map_screen_title)) },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
          }
          composable(C.Screen.MAP_FILTER) { FilterMapScreen(navigationActions, bookFilter) }
        }
        navigation(startDestination = C.Screen.NEW_BOOK, route = C.Route.NEW_BOOK) {
          composable(C.Screen.NEW_BOOK) {
            BookAdditionChoiceScreen(
                navigationActions,
                topAppBar = { topAppBar(stringResource(R.string.book_addition_choice_title)) },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
                photoFirebaseStorageRepository = photoStorage,
                booksRepository = bookRepository)
          }
          composable(C.Screen.ADD_BOOK_MANUALLY) {
            AddToBookScreen(
                AddToBookViewModel(bookRepository, userVM),
                photoStorage,
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
                topAppBar = { topAppBar(stringResource(R.string.user_profile_screen_title)) },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") })
          }
          composable("${C.Screen.BOOK_PROFILE}/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.let { UUID.fromString(it) }

            if (bookId != null) {
              BookProfileScreen(
                  bookId = bookId, // Default for testing
                  topAppBar = { topAppBar(stringResource(R.string.book_profile_screen_title)) },
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
                viewModel = EditBookViewModel(bookRepository, navigationActions, userVM),
                photoStorage,
                topAppBar = { topAppBar("Edit your Book") },
                bottomAppBar = { bottomAppBar(this@navigation.route ?: "") },
                bookUUID = bookUUID!!)
          }
          composable(C.Screen.AUTH) { SignInScreen(navigationActions) }
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
                      context = context,
                      userId = userId,
                      booksRepository = bookRepository,
                      navigationActions = navigationActions,
                      topAppBar = {
                        topAppBar(stringResource(R.string.other_user_profile_screen_title))
                      },
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
