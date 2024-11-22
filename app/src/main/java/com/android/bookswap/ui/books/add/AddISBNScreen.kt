package com.android.bookswap.ui.books.add

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.TopLevelDestinations
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID

/** This is the main screen for the chat feature. It displays the list of messages */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddISBNScreen(
    navigationActions: NavigationActions,
    booksRepository: BooksRepository,
    userVM: UserViewModel = UserViewModel(UUID.randomUUID()),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val context = LocalContext.current
  val user = userVM.getUser()
  Scaffold(
      topBar = topAppBar,
      bottomBar = bottomAppBar,
      content = { pv ->
        Box(
            modifier =
                Modifier.fillMaxSize().padding(pv).background(color = ColorVariable.BackGround)) {
              var isbn by remember { mutableStateOf("") }

              Column(
                  modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(45.dp)) {
                    FieldComponent(
                        modifier = Modifier.testTag("isbn_field"),
                        labelText = "ISBN*",
                        value = isbn) {
                          if (it.all { c -> c.isDigit() } && it.length <= 13) {
                            isbn = it
                            Log.d("ISBN Input", "Updated ISBN: $isbn")
                          }
                        }
                    ButtonComponent(
                        modifier = Modifier.testTag("isbn_searchButton"),
                        onClick = {
                          GoogleBookDataSource(context).getBookFromISBN(isbn, user.userUUID) {
                              result ->
                            if (result.isFailure) {
                              Toast.makeText(context, "Search unsuccessful", Toast.LENGTH_LONG)
                                  .show()
                              Log.e("AddBook", result.exceptionOrNull().toString())
                            } else {
                              booksRepository.addBook(
                                  result.getOrThrow(),
                                  callback = { res ->
                                    if (res.isSuccess) {
                                      val newBookList = user.bookList + result.getOrNull()?.uuid!!
                                      userVM.updateUser(bookList = newBookList)
                                      Toast.makeText(
                                              context,
                                              "${result.getOrNull()?.title} added",
                                              Toast.LENGTH_LONG)
                                          .show()
                                      navigationActions.navigateTo(TopLevelDestinations.NEW_BOOK)
                                    } else {
                                      val error = res.exceptionOrNull()!!
                                      Log.e("AddBook", res.toString())
                                      Toast.makeText(context, error.message, Toast.LENGTH_LONG)
                                          .show()
                                    }
                                  })
                            }
                          }
                        }) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Search")
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search icon",
                            )
                          }
                        }
                  }
            }
      })
}
