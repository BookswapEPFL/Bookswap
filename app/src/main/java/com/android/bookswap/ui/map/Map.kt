package com.android.bookswap.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.theme.ColorVariable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.GoogleMap
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

const val INIT_ZOOM = 10F

/**
 * Composable function to display a map with user locations and associated book information.
 *
 * This screen renders a GoogleMap that shows books locations as markers. Upon clicking a marker, it
 * displays a custom info window with the list of books at this location.
 *
 * @param listUser List of users [TempUser] to display on the map, each containing their location
 *   (latitude, longitude) and a list of books they own (listBook). This argument will later be
 *   deleted as the code should in the future use a class to get the user from the database.
 * @param selectedUser An optional user [TempUser] to be initially selected and focused on the map.
 *   This user’s info window will be shown if not null.
 * @param navigationActions An instance of [NavigationActions] to handle navigation actions.
 * @param bookFilter An instance of [BookFilter] to filter the books displayed on the map.
 */
@Composable
fun MapScreen(
    listUser: List<TempUser>,
    selectedUser: TempUser? = null,
    navigationActions: NavigationActions,
    bookFilter: BookFilter
) {

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), INIT_ZOOM) // Initial camera position
  }

  var mutableStateSelectedUser by remember { mutableStateOf(selectedUser) }
  var markerScreenPosition by remember { mutableStateOf<Offset?>(null) }
  val listAllBooks = listUser.flatMap { it.listBook }

  // Filter the books based on the selected filters
  val genresFilter by bookFilter.genresFilter.collectAsState()
  val languagesFilter by bookFilter.languagesFilter.collectAsState()

  val filteredBooks =
      remember(genresFilter, languagesFilter) { bookFilter.filterBooks(listAllBooks) }

  val filteredUsers =
      listUser.filter { user -> user.listBook.any { book -> filteredBooks.contains(book) } }

  // compute the position of the marker on the screen given the camera position and the marker's
  // position on the map
  fun computePositionOfMarker(cameraPositionState: CameraPositionState, markerLatLng: LatLng) {
    val projection = cameraPositionState.projection
    projection?.let {
      val screenPosition = it.toScreenLocation(markerLatLng)
      markerScreenPosition = Offset(screenPosition.x.toFloat(), screenPosition.y.toFloat())
    }
  }

  if (mutableStateSelectedUser != null) {
    computePositionOfMarker(
        cameraPositionState,
        LatLng(mutableStateSelectedUser!!.latitude, mutableStateSelectedUser!!.longitude))
  }

  val coroutineScope = rememberCoroutineScope()

  // Recalculate marker screen position during camera movement
  LaunchedEffect(cameraPositionState.position) {
    if (mutableStateSelectedUser != null) {
      computePositionOfMarker(
          cameraPositionState,
          LatLng(mutableStateSelectedUser!!.latitude, mutableStateSelectedUser!!.longitude))
    }
  }

  Scaffold(
      modifier = Modifier.testTag("mapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = List_Navigation_Bar_Destinations,
            selectedItem = navigationActions.currentRoute())
      },
      content = { pd ->
        GoogleMap(
            onMapClick = { mutableStateSelectedUser = null },
            modifier = Modifier.fillMaxSize().padding(pd).testTag("mapGoogleMap"),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
        ) {
          filteredUsers
              .filter { !it.longitude.isNaN() && !it.latitude.isNaN() && it.listBook.isNotEmpty() }
              .forEach { item ->
                val markerState = MarkerState(position = LatLng(item.latitude, item.longitude))

                Marker(
                    state = markerState,
                    onClick = {
                      mutableStateSelectedUser = item
                      coroutineScope.launch {
                        computePositionOfMarker(cameraPositionState, markerState.position)
                      }
                      false
                    })
              }
        }
        FilterButton { navigationActions.navigateTo(Screen.FILTER) }

        // Custom info window linked to the marker
        markerScreenPosition?.let { screenPos ->
          mutableStateSelectedUser?.let { user ->
            CustomInfoWindow(
                modifier =
                    Modifier.offset {
                      IntOffset(screenPos.x.roundToInt(), screenPos.y.roundToInt())
                    },
                userBooks = bookFilter.filterBooks(user.listBook))
          }
        }
        // Draggable Bottom List
        DraggableMenu(filteredBooks)
      })
}

const val PADDING_VERTICAL_DP = 4
const val PADDING_HORIZONTAL_DP = 8
const val DIVIDER_THICKNESS_DP = 1
const val CARD_CORNER_RADIUS = 10
const val CARD_WIDTH_DP = 200
const val CARD_HEIGHT_DP = CARD_WIDTH_DP * 2

const val PRIMARY_TEXT_FONT_SP = 20
const val SECONDARY_TEXT_FONT_SP = 16

/**
 * Composable function to display a custom info window for a selected book's list marker on the map.
 *
 * This function creates a card that shows detailed information about a user's list of books when
 * the corresponding map marker is clicked.
 *
 * @param modifier A [Modifier] to apply to the card containing the custom info window. It can be
 *   used to modify the position, size, and appearance of the info window, but is mainly intended to
 *   give the position of the CustomInfoWindow. Default is `Modifier`.
 * @param user The `TempUser` object containing the list of books to be displayed inside the info
 *   window.
 */
@Composable
private fun CustomInfoWindow(modifier: Modifier = Modifier, userBooks: List<DataBook>) {
  Card(
      modifier =
          modifier
              .wrapContentSize()
              .width(CARD_WIDTH_DP.dp)
              .border(
                  BorderStroke(width = DIVIDER_THICKNESS_DP.dp, color = ColorVariable.Accent),
                  shape =
                      RoundedCornerShape(
                          0.dp,
                          CARD_CORNER_RADIUS.dp,
                          CARD_CORNER_RADIUS.dp,
                          CARD_CORNER_RADIUS.dp))
              .heightIn(max = CARD_HEIGHT_DP.dp)
              .testTag("mapBoxMarker")
              .background(Color.Transparent),
      colors = CardDefaults.cardColors(containerColor = ColorVariable.Secondary),
      shape =
          RoundedCornerShape(
              0.dp, CARD_CORNER_RADIUS.dp, CARD_CORNER_RADIUS.dp, CARD_CORNER_RADIUS.dp)) {
        Spacer(modifier.height(CARD_CORNER_RADIUS.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth().testTag("mapBoxMarkerList")) {
          itemsIndexed(userBooks) { index, book ->
            Column(
                modifier =
                    Modifier.padding(horizontal = PADDING_HORIZONTAL_DP.dp)
                        .testTag("mapBoxMarkerListBox")) {
                  Text(
                      text = book.title,
                      color = ColorVariable.Accent,
                      fontSize = PRIMARY_TEXT_FONT_SP.sp,
                      modifier = Modifier.testTag("mapBoxMarkerListBoxTitle"))
                  Spacer(modifier = Modifier.height(PADDING_VERTICAL_DP.dp))
                  Text(
                      text = book.author ?: "",
                      color = ColorVariable.AccentSecondary,
                      fontSize = SECONDARY_TEXT_FONT_SP.sp,
                      modifier = Modifier.testTag("mapBoxMarkerListBoxAuthor"))
                }
            if (index < userBooks.size - 1)
                HorizontalDivider(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(PADDING_VERTICAL_DP.dp)
                            .testTag("mapBoxMarkerListDivider"),
                    thickness = DIVIDER_THICKNESS_DP.dp,
                    color = ColorVariable.Accent)
          }
        }
        Spacer(modifier.height(PADDING_VERTICAL_DP.dp))
      }
}

const val HEIGHT_RETRACTED_DRAGGABLE_MENU_DP = 110
const val DRAGGABLE_MENU_CORNER_RADIUS_DP = 50
const val MIN_BOX_BOOK_HEIGHT_DP = 90
const val IMAGE_HEIGHT_DP = MIN_BOX_BOOK_HEIGHT_DP - PADDING_VERTICAL_DP * 2
// 1.5:1 ratio + the padding
const val IMAGE_WIDTH_DP = MIN_BOX_BOOK_HEIGHT_DP * 2 / 3 + PADDING_HORIZONTAL_DP * 2
const val HANDLE_WIDTH_DP = 120
const val HANDLE_HEIGHT_DP = 15
const val HANDLE_CORNER_RADIUS_DP = 10
const val SPACER_HEIGHT_DP = 20
const val STAR_HEIGHT_DP = 30
const val STAR_SIZE_DP = 26
const val STAR_INNER_SIZE_DP = STAR_SIZE_DP / 2
const val WIDTH_TITLE_BOX_DP = 150
const val MAX_RATING = 5

/**
 * Composable function to display a draggable menu containing all the nearest books available.
 *
 * @param listAllBooks A [List] of all [DataBook] sorted with the nearest book at the first position
 *   and the furthest one at the last position.
 */
@Composable
private fun DraggableMenu(listAllBooks: List<DataBook>) {

  // State for menu drag offset
  val configuration = LocalConfiguration.current
  val maxSheetOffsetY = configuration.screenHeightDp.dp
  var sheetOffsetY by remember {
    mutableStateOf((maxSheetOffsetY - HEIGHT_RETRACTED_DRAGGABLE_MENU_DP.dp) / 3 * 2)
  }

  Box(
      modifier =
          Modifier.offset {
                IntOffset(
                    0,
                    sheetOffsetY
                        .toPx()
                        .roundToInt()
                        .coerceIn(
                            0,
                            (maxSheetOffsetY - HEIGHT_RETRACTED_DRAGGABLE_MENU_DP.dp)
                                .toPx()
                                .toInt()))
              }
              .fillMaxWidth()
              .fillMaxHeight()
              .height(
                  (maxSheetOffsetY - sheetOffsetY).coerceIn(
                      maxSheetOffsetY / 10, maxSheetOffsetY)) // Define the height of the menu
              .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                  change.consume()
                  val dragAmountInDp = dragAmount / density
                  sheetOffsetY = (sheetOffsetY + dragAmountInDp.dp)
                }
              }
              .background(
                  color = ColorVariable.BackGround,
                  shape =
                      RoundedCornerShape(
                          topStart = DRAGGABLE_MENU_CORNER_RADIUS_DP.dp,
                          topEnd = DRAGGABLE_MENU_CORNER_RADIUS_DP.dp))
              .testTag("mapDraggableMenu")) {
        Column(
            modifier =
                Modifier.padding(vertical = HANDLE_HEIGHT_DP.dp)
                    .fillMaxWidth()
                    .testTag("mapDraggableMenuStructure")) {
              // draggable handle
              Box(
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally)
                          .width(HANDLE_WIDTH_DP.dp)
                          .height(HANDLE_HEIGHT_DP.dp)
                          .background(
                              color = ColorVariable.AccentSecondary,
                              shape = RoundedCornerShape(HANDLE_CORNER_RADIUS_DP.dp))
                          .testTag("mapDraggableMenuHandle"))
              Spacer(modifier = Modifier.height(SPACER_HEIGHT_DP.dp))
              HorizontalDivider(
                  modifier = Modifier.fillMaxWidth().testTag("mapDraggableMenuHandleDivider"),
                  thickness = DIVIDER_THICKNESS_DP.dp,
                  color = ColorVariable.Accent)
              LazyColumn() {
                if (listAllBooks.isEmpty()) {
                  item {
                    Text(
                        text = "No books found",
                        color = ColorVariable.Accent,
                        fontSize = PRIMARY_TEXT_FONT_SP.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier =
                            Modifier.padding(PADDING_HORIZONTAL_DP.dp)
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .testTag("mapDraggableMenuNoBook"))
                  }
                } else {
                  items(listAllBooks) { book ->
                    Spacer(modifier = Modifier.height(PADDING_VERTICAL_DP.dp))
                    Row(
                        modifier =
                            Modifier.heightIn(min = MIN_BOX_BOOK_HEIGHT_DP.dp)
                                .testTag("mapDraggableMenuBookBox")) {
                          // Image Box
                          Box(
                              modifier =
                                  Modifier.height(IMAGE_HEIGHT_DP.dp)
                                      .width(IMAGE_WIDTH_DP.dp)
                                      .padding(
                                          start = PADDING_HORIZONTAL_DP.dp,
                                          end = PADDING_HORIZONTAL_DP.dp)
                                      .testTag("mapDraggableMenuBookBoxImage")) {
                                // Image of the books, will be added at a later date
                                // We didn't discussed about how we will store the image or how we
                                // will
                                // encode them
                                Box(
                                    modifier =
                                        Modifier.fillMaxSize()
                                            .background(Color.Gray) // Placeholder for the image
                                    )
                              }

                          // Column for text content
                          Column(
                              modifier =
                                  Modifier.padding(vertical = PADDING_VERTICAL_DP.dp)
                                      .width(WIDTH_TITLE_BOX_DP.dp)
                                      .testTag("mapDraggableMenuBookBoxMiddle")) {
                                Text(
                                    text = book.title,
                                    color = ColorVariable.Accent,
                                    fontSize = PRIMARY_TEXT_FONT_SP.sp,
                                    modifier =
                                        Modifier.padding(bottom = PADDING_VERTICAL_DP.dp)
                                            .width(WIDTH_TITLE_BOX_DP.dp)
                                            .testTag("mapDraggableMenuBookBoxTitle"))
                                Text(
                                    text = book.author ?: "",
                                    color = ColorVariable.AccentSecondary,
                                    fontSize = SECONDARY_TEXT_FONT_SP.sp,
                                    modifier =
                                        Modifier.width(WIDTH_TITLE_BOX_DP.dp)
                                            .testTag("mapDraggableMenuBookBoxAuthor"))
                              }
                          Column(
                              modifier =
                                  Modifier.fillMaxWidth().testTag("mapDraggableMenuBookRight")) {
                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .height(STAR_HEIGHT_DP.dp)
                                            .testTag("mapDraggableMenuBookBoxRating")) {
                                      // leave all stars empty if no rating
                                      DisplayStarReview(book.rating ?: 0)
                                    }
                                // text for the tags of the book, will be added at a later date
                                // It isn't decided how we will handle the tag for the books
                                Text(
                                    text = book.genres.joinToString(separator = ", ") { it.Genre },
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .testTag("mapDraggableMenuBookBoxTag"),
                                    fontSize = SECONDARY_TEXT_FONT_SP.sp,
                                    color = ColorVariable.AccentSecondary)
                              }
                        }

                    // Divider below each item
                    HorizontalDivider(
                        modifier =
                            Modifier.fillMaxWidth().testTag("mapDraggableMenuBookBoxDivider"),
                        thickness = DIVIDER_THICKNESS_DP.dp,
                        color = ColorVariable.Accent)
                  }
                }
              }
            }
      }
}

/**
 * Composable function that displays a row of 5 stars, the n first are filled then the rest are
 * empty stars.
 *
 * @param rating A [Int] from 1 to 5, used to know how many filled star should be displayed
 */
@Composable
private fun DisplayStarReview(rating: Int) {
  for (i in 1..rating) {
    Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = "Star Icon",
        tint = Color.Black,
        modifier = Modifier.size(STAR_SIZE_DP.dp).testTag("mapDraggableMenuBookBoxStar"))
  }
  for (i in rating + 1..MAX_RATING) {
    // Hollow star
    // Icons.Outlined.Star doesn't work, it displays the
    // Icons.Filled.Star
    Box(modifier = Modifier.width(STAR_SIZE_DP.dp).testTag("mapDraggableMenuBookBoxEmptyStar")) {
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Star Icon",
          tint = Color.Black,
          modifier = Modifier.size(STAR_SIZE_DP.dp))
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Star Icon",
          tint = ColorVariable.BackGround,
          modifier = Modifier.size(STAR_INNER_SIZE_DP.dp).align(Alignment.Center))
    }
  }
}

// need to be removed when user dataclass will be created
data class TempUser(val latitude: Double, val longitude: Double, val listBook: List<DataBook>)
