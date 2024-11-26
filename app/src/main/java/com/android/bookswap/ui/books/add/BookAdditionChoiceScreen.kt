package com.android.bookswap.ui.books.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.android.bookswap.R
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/**
 * Composable function to display the screen for choosing how to add a book.
 *
 * @param navController The navigation actions to handle navigation events.
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAdditionChoiceScreen(
    navController: NavigationActions,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {},
) {
  val columnPadding = 16.dp
  val buttonWidth = (LocalConfiguration.current.screenWidthDp.dp * (0.75f))
  Scaffold(
      modifier = Modifier.testTag(C.Tag.new_book_choice_screen_container),
      topBar = topAppBar,
      bottomBar = bottomAppBar) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .fillMaxSize()
                    .background(ColorVariable.BackGround)
                    .padding(bottom = 10f * columnPadding, top = columnPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              ButtonWithIcon(
                  text = "Manually",
                  leftIcon = Icons.Default.Add,
                  leftIconPainter = null,
                  navController = navController,
                  navDestination = C.Screen.ADD_BOOK_MANUALLY,
                  buttonWidth = buttonWidth)
              Spacer(modifier = Modifier.height(2f * columnPadding))
              ButtonWithIcon(
                  text = "From ISBN",
                  leftIcon = null,
                  leftIconPainter = painterResource(id = R.drawable.download),
                  navController = navController,
                  navDestination = C.Screen.ADD_BOOK_ISBN,
                  buttonWidth = buttonWidth)
              Spacer(modifier = Modifier.height(2f * columnPadding))
              ButtonWithIcon(
                  text = "From Photo",
                  leftIcon = null,
                  leftIconPainter = painterResource(id = R.drawable.photoicon),
                  navController = navController,
                  navDestination = C.Screen.ADD_BOOK_SCAN,
                  buttonWidth = buttonWidth)
            }
      }
}
/**
 * Composable function to display a button with an icon.
 *
 * @param text The text to display on the button.
 * @param leftIcon The optional left icon to display on the button.
 * @param leftIconPainter The optional left icon painter to display on the button.
 * @param navController The navigation actions to handle navigation events.
 * @param navDestination The destination to navigate to when the button is clicked.
 * @param buttonWidth The width of the button.
 */
@Composable
fun ButtonWithIcon(
    text: String,
    leftIcon: ImageVector? = null,
    leftIconPainter: Painter? = null,
    navController: NavigationActions,
    navDestination: String,
    buttonWidth: Dp
) {
  val borderPadding = 1.dp
  val buttonPadding = 8.dp
  val iconSize = 32.dp
  val pngSize = 24.dp
  val textSize = 18.sp
  Button(
      onClick = { navController.navigateTo(navDestination) },
      colors =
          ButtonDefaults.buttonColors(
              containerColor = ColorVariable.AccentSecondary,
              contentColor = ColorVariable.BackGround),
      border = BorderStroke(borderPadding, ColorVariable.Accent),
      shape = RoundedCornerShape(buttonPadding),
      modifier =
          Modifier.padding(buttonPadding)
              .width(buttonWidth)
              .testTag(navDestination + C.Tag.NewBookChoice.btnWIcon.button)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              if (leftIcon != null) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = null,
                    modifier =
                        Modifier.size(iconSize)
                            .testTag(navDestination + C.Tag.NewBookChoice.btnWIcon.icon))
              } else if (leftIconPainter != null) {
                Image(
                    painter = leftIconPainter,
                    contentDescription = null,
                    modifier =
                        Modifier.size(pngSize)
                            .testTag(navDestination + C.Tag.NewBookChoice.btnWIcon.png))
              }
              Text(text, fontSize = textSize)
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  modifier =
                      Modifier.size(iconSize)
                          .testTag(navDestination + C.Tag.NewBookChoice.btnWIcon.arrow))
            }
      }
}
