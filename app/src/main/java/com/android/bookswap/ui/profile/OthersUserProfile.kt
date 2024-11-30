package com.android.bookswap.ui.profile

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.OthersUserViewModel
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.ui.components.BookListComponent
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID

/**
 * Composable function to display the user profile screen.
 *
 * @param userId The UUID of the user whose profile is to be displayed.
 * @param otherUserVM The ViewModel containing user data. Defaults to a new UserViewModel instance.
 * @param topAppBar A composable function to display the top app bar. Defaults to an empty
 *   composable.
 * @param bottomAppBar A composable function to display the bottom app bar. Defaults to an empty
 *   composable.
 */
@Composable
fun OthersUserProfileScreen(
    userId: UUID,
    otherUserVM: OthersUserViewModel = OthersUserViewModel(userId),
    booksRepository: BooksRepository,
    UserBookViewModel: UserBookViewModel = UserBookViewModel(booksRepository),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  // var user = userVM.getUser()
  var user by remember { mutableStateOf(DataUser()) }
  var isLoading by remember { mutableStateOf(true) }

  // I think it is better (good thing) to use LaunchedEffect
  LaunchedEffect(userId) {
    isLoading = true
    otherUserVM.getUserByUUID(userId, callback = { user = it ?: DataUser() })
    Log.e("FetchedUser", "Name: ${user.firstName}")
    isLoading = false
  }

    val bookListData = remember { mutableStateOf<List<DataBook>>(emptyList()) }
    var isBooksLoading by remember { mutableStateOf(true) }

    LaunchedEffect(user.bookList) {
        val userBookList = user.bookList
        for (book in userBookList) {
            Log.e("OtherUserProfileScreen", "BookListUUID: $userBookList")
        }


        isBooksLoading = true
        try {
            bookListData.value = UserBookViewModel.getBooks(user.bookList)
        } catch (exception: Exception) {
            Log.e("OtherUserProfileScreen", "Error fetching books: $exception")
        } finally {
            isBooksLoading = false
        }
    }

    Log.e("OtherUserProfileScreen", "BookListDataBook: ${bookListData.value}")
    //bookListData.clear()
    //bookListData.addAll(fetchedBooks)


  Scaffold(
      modifier = Modifier.testTag("OtherUserProfileScreen"),
      topBar = topAppBar,
      bottomBar = bottomAppBar) { padding ->
        if (isLoading) {
          Box(
              modifier = Modifier.fillMaxSize().padding(padding),
              contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // To show it is loading
                Log.e("OtherUserProfileScreen", "Loading") // log to see on logcat
          }
        } else {
          Column(
              modifier =
                  Modifier.padding(padding).fillMaxSize().background(ColorVariable.BackGround),
              verticalArrangement = Arrangement.spacedBy(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
              // Profile Picture
              Box(
                  modifier =
                  Modifier.padding(16.dp)
                      .size(90.dp)
                      .border(3.dp, ColorVariable.Accent, CircleShape)
                      .background(ColorVariable.AccentSecondary, CircleShape),
                  contentAlignment = Alignment.Center
              ) {
                  if (user.profilePictureUrl.isNotEmpty()) {
                      // Replace with an image loader like Coil or Glide if required
                      Text("Profile Picture Placeholder")
                  } else {
                      Icon(
                          imageVector = Icons.Default.AccountCircle,
                          contentDescription = null,
                          modifier = Modifier.size(80.dp),
                          tint = ColorVariable.Accent
                      )
                  }
              }/*
              Column(
                  modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
              ) {*/

                  // Full Name

                  LabeledText(
                      label = "Name:",
                      value = "${user.greeting} ${user.firstName} ${user.lastName}"
                  )

                  // Email
                  LabeledText(label = "Email:", value = user.email)

                  // Phone Number
                  LabeledText(label = "Phone:", value = user.phoneNumber)

                  // Address
                  LabeledText(label = "Address:", value = "${user.latitude}, ${user.longitude}")
              //}

          //}

              val testBooks = listOf(
                  DataBook(
                      uuid = UUID.randomUUID(),
                      title = "Test Book 1",
                      author = "Author 1",
                      description = "Description 1",
                      rating = 4,
                      photo = null,
                      language = BookLanguages.OTHER,
                      isbn = null,
                      genres = emptyList(),
                      userId = UUID.randomUUID()
                  ),
                  DataBook(
                      uuid = UUID.randomUUID(),
                      title = "Test Book 2",
                      author = "Author 2",
                      description = "Description 2",
                      rating = 5,
                      photo = null,
                      language = BookLanguages.OTHER,
                      isbn = null,
                      genres = emptyList(),
                      userId = UUID.randomUUID()
                  )
              )

              BookListComponent(
                  modifier = Modifier.fillMaxWidth(),
                  bookList = testBooks
              )

              // Book List
              if (isBooksLoading) {
                  Log.e("OtherUserProfileScreen", "Books are loading")
                  CircularProgressIndicator(modifier = Modifier.padding(16.dp))
              } else if (bookListData.value.isEmpty()) {
                  Log.e("OtherUserProfileScreen", "No books available")
                  Text("No books available", style = MaterialTheme.typography.bodyLarge)
              } else {
                  Log.e("OtherUserProfileScreen", "Displaying book list")
                  BookListComponent(
                      modifier = Modifier.testTag("otherUserBookList").fillMaxWidth().padding(16.dp),//background(Color.LightGray) // Debug background
                          //.border(2.dp, Color.Red),    // Debug border,
                      bookList = bookListData.value
                  )
              }
              /*
              BookListComponent(
                  modifier = Modifier.testTag("otherUserBookList"),
                  bookList = bookListData,
                  // bc it is a list of UUIDs I maybe need to retrieve each book before
                  // or maybe the retrieval of the books should be done in the booklist composable
              )
              */
          }
        }
      }
}
/**
 * A composable function to display a labeled text field.
 *
 * @param label The label for the field (e.g., "Email:").
 * @param value The value of the field.
 */
@Composable
fun LabeledText(label: String, value: String) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .background(ColorVariable.Secondary, shape = MaterialTheme.shapes.small)
              .border(1.dp, ColorVariable.Accent, shape = MaterialTheme.shapes.small)
      // .padding(8.dp)
      ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = label,
                  color = ColorVariable.Accent,
                  style = MaterialTheme.typography.labelLarge,
                  modifier = Modifier.weight(0.5f))
              Text(
                  text = value,
                  color = ColorVariable.Accent,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.weight(2f))
            }
      }
}
