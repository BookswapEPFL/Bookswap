package com.android.bookswap.ui.map

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.R
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.BackButtonComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/** Constants */
private const val TOP_BAR_TITLE_WIDTH_RATIO = 3 / 4f
private val BUTTON_WIDTH = 173.dp
private val BUTTON_HEIGHT = 43.24.dp
private val TOP_BAR_TITLE_FONT_SIZE = 30.sp
private val TOP_BAR_TITLE_LINE_HEIGHT = 20.sp
private val TOP_BAR_TITLE_LETTER_SPACING = 0.3.sp
private val TOP_BAR_TITLE_FONT_WEIGHT = FontWeight(700)

/**
 * This is the main screen for the filter feature. It displays the filters for the user to select
 *
 * @param navigationActions The navigation actions to navigate between screens
 * @param bookFilter The filter object that contains the selected filters
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterMapScreen(navigationActions: NavigationActions, bookFilter: BookFilter) {

  val selectedFiltersGenres by bookFilter.genresFilter.collectAsState() // genre filter
  val selectedFiltersLanguages by bookFilter.languagesFilter.collectAsState() // language filter
  val context = LocalContext.current

  Scaffold(
      containerColor = ColorVariable.BackGround,
      topBar = {
        TopAppBar(
            colors =
                TopAppBarDefaults.smallTopAppBarColors(containerColor = ColorVariable.BackGround),
            title = {
              Text(
                  text = "Filters", // Hard coded string that should be extracted to a strings.xml
                  modifier =
                      Modifier.fillMaxWidth(TOP_BAR_TITLE_WIDTH_RATIO)
                          .testTag(C.Tag.TopAppBar.screen_title),
                  textAlign = TextAlign.Center,
                  style =
                      TextStyle(
                          fontSize =
                              TOP_BAR_TITLE_FONT_SIZE, // Hard coded style that should be extracted
                          // to a theme
                          lineHeight = TOP_BAR_TITLE_LINE_HEIGHT,
                          fontWeight = TOP_BAR_TITLE_FONT_WEIGHT,
                          color = ColorVariable.Accent,
                          letterSpacing = TOP_BAR_TITLE_LETTER_SPACING,
                      ))
            },
            navigationIcon = { BackButtonComponent(navigationActions) })
      },
      content = { paddingValues ->
        LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxSize()) {
          item {
            ButtonBlock(
                BookGenres.values().map { it.Genre }, selectedFiltersGenres.map { it.Genre }) {
                    newSelection ->
                  bookFilter.setGenres(newSelection) // Actualize the selected genres as OnClick
            }
          }
          item {
            ButtonBlock(
                buttonTexts = BookLanguages.values().map { it.languageName },
                selectedFiltersLanguages.map { it.languageName }) { newSelection ->
                  bookFilter.setLanguages(
                      newSelection) // Actualize the selected languages as OnClick
            }
          }
        }
      },
      bottomBar = {
        BottomAppBar(containerColor = ColorVariable.BackGround) {
          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                  bookFilter.setGenres(selectedFiltersGenres.map { it.Genre })
                  bookFilter.setLanguages(selectedFiltersLanguages.map { it.languageName })
                  Toast.makeText(
                          context,
                          context.getString(R.string.filters_applied_message),
                          Toast.LENGTH_SHORT)
                      .show()
                  navigationActions.goBack()
                },
                colors = ButtonDefaults.buttonColors(ColorVariable.Primary),
                modifier =
                    Modifier.width(BUTTON_WIDTH)
                        .height(BUTTON_HEIGHT)
                        .testTag(C.Tag.MapFilter.apply)) {
                  Text(
                      text =
                          stringResource(
                              R.string.filter_apply_button_text), // Hard coded string that should
                      textAlign = TextAlign.Center,
                      style =
                          TextStyle(
                              color = ColorVariable.BackGround,
                          ))
                }
          }
        }
      })
}

/** Constants */
private val BUTTON_HEIGHT_BB = 35.dp
private val BORDER_WIDTH_BB = 3.dp
private val BUTTON_PADDING_BB = 1.dp
private val BUTTON_SHAPE_BB = RoundedCornerShape(25.dp)
private val HORIZONTAL_PADDING_BB = 37.dp
private val VERTICAL_PADDING_BB = 20.dp
private const val MAX_ITEMS_PER_ROW_BB = 3

/**
 * This is a composable that displays a row of buttons
 *
 * @param buttonTexts The list of strings to display on the buttons
 * @param selectedFilters The list of strings that are currently selected
 * @param onSelectionChange The function to call when the selection changes
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ButtonBlock(
    buttonTexts: List<String>,
    selectedFilters: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
  FlowRow(
      modifier =
          Modifier.fillMaxWidth()
              .padding(
                  top = VERTICAL_PADDING_BB,
                  start = HORIZONTAL_PADDING_BB,
                  end = HORIZONTAL_PADDING_BB,
                  bottom = VERTICAL_PADDING_BB),
      maxItemsInEachRow = MAX_ITEMS_PER_ROW_BB,
      horizontalArrangement = Arrangement.Center) {
        buttonTexts.forEach { text ->
          val isSelected = selectedFilters.contains(text)

          Button(
              onClick = {
                val newSelection =
                    if (isSelected) {
                      selectedFilters - text
                    } else {
                      selectedFilters + text
                    }
                onSelectionChange(newSelection)
              },
              modifier =
                  Modifier.wrapContentSize()
                      .padding(BUTTON_PADDING_BB)
                      .border(
                          BORDER_WIDTH_BB, color = ColorVariable.Accent, shape = BUTTON_SHAPE_BB)
                      .height(BUTTON_HEIGHT_BB)
                      .testTag(text + C.Tag.MapFilter.filter),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor =
                          if (isSelected) ColorVariable.Accent else ColorVariable.BackGround),
              shape = BUTTON_SHAPE_BB) {
                Text(
                    text = text,
                    style =
                        TextStyle(
                            color =
                                if (isSelected) ColorVariable.BackGround else ColorVariable.Accent))
              }
        }
      }
}
