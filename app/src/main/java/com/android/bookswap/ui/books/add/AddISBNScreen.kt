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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.bookswap.R
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.model.InputVerification
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHISBN
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/** This is the main screen for the chat feature. It displays the list of messages */
@Composable
fun AddISBNScreen(
    navigationActions: NavigationActions,
    booksRepository: BooksRepository,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val context = LocalContext.current
  val inputVerification = InputVerification()
  val appConfig = LocalAppConfig.current
  Scaffold(
      modifier = Modifier.testTag(C.Tag.new_book_isbn_screen_container),
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
                        modifier = Modifier.testTag(C.Tag.NewBookISBN.isbn),
                        labelText = stringResource(R.string.add_isbn_field_label),
                        value = isbn,
                        maxLength = MAXLENGTHISBN) {
                          isbn = it
                          Log.d("ISBN Input", "Updated ISBN: $isbn")
                        }
                    ButtonComponent(
                        modifier = Modifier.testTag(C.Tag.NewBookISBN.search),
                        onClick = {
                          if (inputVerification.testIsbn(isbn)) {
                            GoogleBookDataSource(context).getBookFromISBN(
                                isbn, appConfig.userViewModel.getUser().userUUID) { result ->
                                  if (result.isFailure) {
                                    Toast.makeText(
                                            context, context.resources.getString(R.string.add_isbn_toast_search_unsuccessful), Toast.LENGTH_LONG)
                                        .show()
                                    Log.e("AddBook", result.exceptionOrNull().toString())
                                  } else {
                                    booksRepository.addBook(
                                        result.getOrThrow(),
                                        callback = { res ->
                                          if (res.isSuccess) {
                                            val newBookList =
                                                appConfig.userViewModel.getUser().bookList +
                                                    result.getOrThrow().uuid
                                            appConfig.userViewModel.updateUser(
                                                bookList = newBookList)
                                            navigationActions.navigateTo(
                                                C.Screen.EDIT_BOOK,
                                                result.getOrThrow().uuid.toString())
                                          } else {
                                            val error = res.exceptionOrNull()!!
                                            Log.e("AddBook", res.toString())
                                            Toast.makeText(
                                                    context, error.message, Toast.LENGTH_LONG)
                                                .show()
                                          }
                                        })
                                  }
                                }
                          } else {
                            Toast.makeText(context, context.resources.getString(R.string.add_isbn_toast_invalid), Toast.LENGTH_LONG).show()
                          }
                        }) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.add_isbn_button_search))
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
