package com.android.bookswap.ui.books

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.android.bookswap.R
import com.android.bookswap.data.DataBook
import com.android.bookswap.ui.components.BackButtonComponent
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.profile.ProfileIcon
import com.android.bookswap.ui.theme.ColorVariable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookProfileScreen(DataBook: DataBook, navController: NavigationActions) {
  val columnPadding = 8.dp
  val pictureWidth = (LocalConfiguration.current.screenWidthDp.dp * (0.60f))
  val pictureHeight = pictureWidth * 1.41f
  val buttonsHeight = pictureHeight / 12.0f
  val images = listOf(R.drawable.isabellacatolica, R.drawable.felipeii)
  val imagesDescription = listOf("Isabel La Catolica", "Felipe II")
  var currentImageIndex by remember { mutableIntStateOf(0) }
  Scaffold(
      modifier = Modifier.testTag("bookProfileScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Book Profile", color = ColorVariable.BackGround) },
            navigationIcon = { BackButtonComponent(navController) },
            actions = { ProfileIcon() },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorVariable.BackGround))
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navController.navigateTo(destination) },
            tabList = List_Navigation_Bar_Destinations,
            selectedItem = navController.currentRoute())
      }) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(ColorVariable.BackGround)
                    .testTag("bookProfileScroll"),
            verticalArrangement = Arrangement.spacedBy(columnPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
              item {
                Text(
                    text = DataBook.title,
                    modifier = Modifier.testTag("bookTitle").padding(columnPadding),
                    color = ColorVariable.Accent,
                    style = MaterialTheme.typography.titleLarge)
              }
              item {
                Text(
                    text = DataBook.author ?: "Author Unknown",
                    modifier = Modifier.testTag("bookAuthor"),
                    color = ColorVariable.AccentSecondary,
                    style = MaterialTheme.typography.titleMedium)
              }
              item { Spacer(modifier = Modifier.height(columnPadding)) }
              item {
                Box(
                    modifier =
                        Modifier.size(pictureWidth, pictureHeight)
                            .background(ColorVariable.BackGround)) {
                      Image(
                          painter = painterResource(id = images[currentImageIndex]),
                          contentDescription = imagesDescription[currentImageIndex],
                          modifier =
                              Modifier.height(pictureHeight)
                                  .fillMaxWidth()
                                  .testTag(
                                      "bookProfileImage ${imagesDescription[currentImageIndex]}"))
                    }
              }
              item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      IconButton(
                          onClick = {
                            currentImageIndex = (currentImageIndex - 1 + images.size) % images.size
                          },
                          modifier =
                              Modifier.height(buttonsHeight).testTag("bookProfileImageLeft")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Image",
                                tint = ColorVariable.Accent)
                          }
                      Text(
                          text = imagesDescription[currentImageIndex],
                          color = ColorVariable.AccentSecondary,
                          modifier = Modifier.padding(horizontal = 8.dp))
                      IconButton(
                          onClick = { currentImageIndex = (currentImageIndex + 1) % images.size },
                          modifier =
                              Modifier.height(buttonsHeight).testTag("bookProfileImageRight")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Image",
                                tint = ColorVariable.Accent)
                          }
                    }
              }
              item { Spacer(modifier = Modifier.height(columnPadding)) }
              item {
                DataBook.rating?.let {
                  Text(
                      text = "Rating: $it/10",
                      color = ColorVariable.Accent,
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileRating"))
                }
              }
              item { Spacer(modifier = Modifier.height(columnPadding)) }
              item {
                Text(
                    text = "Synopsis",
                    color = ColorVariable.Accent,
                    style = MaterialTheme.typography.titleSmall,
                    modifier =
                        Modifier.padding(vertical = 8.dp).testTag("bookProfileSynopsisTitle"))
              }
              item {
                Text(
                    text = DataBook.description ?: "No description available",
                    color = ColorVariable.Accent,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileSynopsis"),
                    textAlign = TextAlign.Center)
              }
              item { Spacer(modifier = Modifier.height(columnPadding)) }
              item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Language: ${DataBook.language.languageCode}",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileLanguage"))
                    Text(
                        text = "Genres:",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.padding(vertical = 8.dp).testTag("bookProfileGenresTitle"))
                    DataBook.genres.forEach { genre ->
                      Text(
                          text = "- ${genre.Genre}",
                          color = ColorVariable.AccentSecondary,
                          style = MaterialTheme.typography.bodyMedium,
                          modifier =
                              Modifier.padding(top = 2.dp, start = 16.dp)
                                  .testTag("bookProfileGenre${genre.Genre}"))
                    }
                    Text(
                        text = "ISBN: ${DataBook.isbn ?: "ISBN doesn't exits or is not available"}",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileISBN"))
                  }

                  VerticalDivider(color = ColorVariable.Accent, thickness = 1.dp)

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date of Publication: [Temporary Date]",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileDate"))
                    Text(
                        text = "Volume: [Temporary Volume]",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileVolume"))
                    Text(
                        text = "Issue: [Temporary Issue]",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp).testTag("bookProfileIssue"))
                    Text(
                        text = "Editorial: [Temporary Editorial]",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.padding(vertical = 8.dp).testTag("bookProfileEditorial"))
                    Text(
                        text = "Place of Edition: [Temporary Place]",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.padding(vertical = 8.dp).testTag("bookProfileEditionPlace"))
                  }
                }
              }
            }
      }
}
