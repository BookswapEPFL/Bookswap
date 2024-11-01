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
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.model.chat.MessageBox
import com.android.bookswap.model.chat.PermissionHandler
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.resources.C
import com.android.bookswap.ui.authentication.SignInScreen
import com.android.bookswap.ui.books.add.AddISBNScreen
import com.android.bookswap.ui.books.add.AddToBookScreen
import com.android.bookswap.ui.books.add.BookAdditionChoiceScreen
import com.android.bookswap.ui.chat.ChatScreen
import com.android.bookswap.ui.chat.ListChatScreen
import com.android.bookswap.ui.map.FilterMapScreen
import com.android.bookswap.ui.map.MapScreen
import com.android.bookswap.ui.map.TempUser
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.theme.BookSwapAppTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class MainActivity : ComponentActivity() {

  private lateinit var permissionHandler: PermissionHandler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // permissionHandler = PermissionHandler(this)
    // permissionHandler.askNotificationPermission()

    // Initialize Firebase Firestore
    val db = FirebaseFirestore.getInstance()

    // Create the MessageFirestoreSource object
    val messageRepository = MessageFirestoreSource(db)
    val bookRepository = BooksFirestoreRepository(db)

    setContent {
      BookSwapAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              BookSwapApp(messageRepository, bookRepository)
            }
      }
    }
  }

  @Composable
  fun BookSwapApp(
      messageRepository: MessageFirestoreSource,
      bookRepository: BooksFirestoreRepository,
      startDestination: String = Route.AUTH
  ) {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)
    val bookFilter = BookFilter()

    val placeHolder =
        listOf(
            MessageBox(
                contactName = "user124",
                message = "Welcome message for user124",
                date = "01.01.24")) +
            List(6) {
              MessageBox(
                  contactName = "Contact ${it + 1}",
                  message = "Test message $it test for the feature of ellipsis in the message",
                  date = "01.01.24")
            }

    NavHost(navController = navController, startDestination = startDestination) {
      navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
        composable(Screen.AUTH) { SignInScreen(navigationActions) }
      }
      navigation(startDestination = Screen.CHATLIST, route = Route.CHAT) {
        composable(Screen.CHATLIST) { ListChatScreen(placeHolder, navigationActions) }
        composable("${Screen.CHAT}/{user1}/{user2}") { backStackEntry ->
          val user1 = backStackEntry.arguments?.getString("user1") ?: ""
          val user2 = backStackEntry.arguments?.getString("user2") ?: ""
          ChatScreen(messageRepository, user1, user2, navigationActions)
        }
        navigation(startDestination = Screen.MAP, route = Route.MAP) {
          composable(Screen.MAP) {
            MapScreen(user, navigationActions = navigationActions, bookFilter = bookFilter)
          }
          composable(Screen.FILTER) { FilterMapScreen(navigationActions, bookFilter) }
        }
        navigation(startDestination = Screen.NEWBOOK, route = Route.NEWBOOK) {
          composable(Screen.NEWBOOK) { BookAdditionChoiceScreen(navigationActions) }
          composable(Screen.ADD_BOOK_MANUALLY) {
            AddToBookScreen(bookRepository, navigationActions)
          }
          composable(Screen.ADD_BOOK_SCAN) { /*Todo*/}
          composable(Screen.ADD_BOOK_ISBN) { AddISBNScreen(navigationActions, bookRepository) }
        }
      }
    }
  }

  // Temporary user list for the map as it is not yet linked to the database.
  // Better to see how the map screen should look like at the end.
  // Need to be removed in the future.
  val user =
      listOf(
          TempUser(
              latitude = 0.0,
              longitude = 0.0,
              listBook =
                  listOf(
                      DataBook(
                          uuid = UUID.randomUUID(),
                          title = "Book 1",
                          author = "Author 1",
                          description = "Description of Book 1",
                          rating = 5,
                          photo = null,
                          language = BookLanguages.ENGLISH,
                          isbn = null,
                          genres = listOf(BookGenres.FANTASY)),
                      DataBook(
                          uuid = UUID.randomUUID(),
                          title = "Book 2",
                          author = "Author 2",
                          description = "Description of Book 2",
                          rating = 4,
                          photo = null,
                          language = BookLanguages.FRENCH,
                          isbn = null,
                          genres = listOf(BookGenres.FICTION)),
                      DataBook(
                          uuid = UUID.randomUUID(),
                          title = "Book 3",
                          author = "Author 3",
                          description = "Description of Book 3",
                          rating = null,
                          photo = null,
                          language = BookLanguages.GERMAN,
                          isbn = null,
                          genres = listOf(BookGenres.SCIENCEFICTION, BookGenres.AUTOBIOGRAPHY)),
                  )))
}
