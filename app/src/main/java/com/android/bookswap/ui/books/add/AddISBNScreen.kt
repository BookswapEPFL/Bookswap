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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.ui.components.BackButtonComponent
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.TopLevelDestinations
import com.android.bookswap.ui.profile.ProfileIcon
import com.android.bookswap.ui.theme.ColorVariable

/** This is the main screen for the chat feature. It displays the list of messages */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddISBNScreen(navigationActions: NavigationActions, booksRepository: BooksRepository) {
  val context = LocalContext.current
  Scaffold(
	topBar = {
	  MediumTopAppBar(
		colors =
		TopAppBarDefaults.topAppBarColors(
		  containerColor = ColorVariable.BackGround,
		),
		title = {
		  Box(modifier = Modifier) {
			Row(
			  horizontalArrangement = Arrangement.Center,
			  modifier = Modifier.fillMaxWidth(0.85f)) {
			  BackButtonComponent(navigationActions)
			  Text(
				text = "Search ISBN",
				style =
				TextStyle(
				  fontSize = 30.sp,
				  lineHeight = 20.sp,
				  fontWeight = FontWeight(700),
				  color = ColorVariable.Accent,
				  letterSpacing = 0.3.sp,
				),
				modifier = Modifier.padding(top = 4.dp))
			}
		  }
		},
		actions = { Box(modifier = Modifier.padding(top = 30.dp)) { ProfileIcon() } })
	},
	content = { pv ->
	  Box(
		modifier =
		Modifier.fillMaxSize()
		  .padding(pv)
		  .padding()
		  .background(color = ColorVariable.BackGround)) {
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
			}
		  }
		  ButtonComponent(
			modifier = Modifier.testTag("isbn_searchButton"),
			onClick = {
			  GoogleBookDataSource(context).getBookFromISBN(isbn) { result ->
				if (result.isFailure) {
				  Toast.makeText(context, "Search unsuccessful", Toast.LENGTH_LONG)
					.show()
				  Log.e("AddBook", result.exceptionOrNull().toString())
				} else {
				  booksRepository.addBook(
					result.getOrThrow(),
					{ navigationActions.navigateTo(TopLevelDestinations.NEW_BOOK) },
					{ error ->
					  Log.e("AddBook", error.toString())
					  Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
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
