package com.android.bookswap.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.model.map.DefaultGeolocation
import com.android.bookswap.model.map.IGeolocation
import com.android.bookswap.ui.components.BookDisplayComponent
import com.android.bookswap.ui.navigation.BOTTOM_NAV_HEIGHT
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.theme.ColorVariable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.GoogleMap
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

const val INIT_ZOOM = 10F
const val NO_USER_SELECTED = -1

val CameraPositionKey = SemanticsPropertyKey<CameraPositionState>("CameraPosition")
var SemanticsPropertyReceiver.cameraPosition by CameraPositionKey

/**
 * Composable function to display a map with user locations and associated book information.
 *
 * This screen renders a GoogleMap that shows books locations as markers. Upon clicking a marker, it
 * displays a custom info window with the list of books at this location.
 *
 * @param listUser List of users [DataUser] to display on the map, will be replaced by a model that
 *   retrieves the users from the database.
 * @param navigationActions An instance of [NavigationActions] to handle navigation actions.
 * @param bookFilter An instance of [BookFilter] to filter the books displayed on the map.
 * @param booksRepository An instance of [BooksRepository] to retrieve the books from the database.
 * @param selectedUser An optional user, it will display the infoWindow related to this user. This
 *   user’s info window will be shown if it is bigger or equal to 0.
 * @param geolocation An instance of [IGeolocation] to get the user's current location.
 */
@Composable
fun MapScreen(
    listUser: List<DataUser>,
    navigationActions: NavigationActions,
    bookFilter: BookFilter,
    booksRepository: BooksRepository,
    selectedUser: Int = NO_USER_SELECTED,
    geolocation: IGeolocation = DefaultGeolocation(),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {},
) {
  val cameraPositionState = rememberCameraPositionState()
  // Get the user's current location
  val latitude by remember { geolocation.latitude }
  val longitude by remember { geolocation.longitude }
  // Start location updates
  LaunchedEffect(Unit) {
    geolocation.startLocationUpdates()
    cameraPositionState.position =
        CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), INIT_ZOOM)
  }
  // Stop location updates when the screen is disposed
  DisposableEffect(Unit) { onDispose { geolocation.stopLocationUpdates() } }

  var mutableStateSelectedUser by remember { mutableStateOf(selectedUser) }
  var markerScreenPosition by remember { mutableStateOf<Offset?>(null) }
  val userBooksList = remember { mutableStateListOf<UserBooksWithLocation>() }

  // Fetch books
  LaunchedEffect(Unit) {
    booksRepository.getBook(
        OnSucess = { books ->
          listUser.forEach { user ->
            userBooksList.add(
                UserBooksWithLocation(
                    user.longitude,
                    user.latitude,
                    books.filter { book -> book.uuid in user.bookList }))
          }
        },
        onFailure = {})
  }

  val listAllBooks = userBooksList.flatMap { it.books }

  // Filter the books based on the selected filters
  val genresFilter by bookFilter.genresFilter.collectAsState()
  val languagesFilter by bookFilter.languagesFilter.collectAsState()

  val filteredBooks =
      remember(genresFilter, languagesFilter, listAllBooks) { bookFilter.filterBooks(listAllBooks) }

  val filteredUsers =
      userBooksList.map {
        it.copy(books = it.books.filter { book -> filteredBooks.contains(book) })
      }

  // compute the position of the marker on the screen given the camera position and the marker's
  // position on the map
  fun computePositionOfMarker(cameraPositionState: CameraPositionState, markerLatLng: LatLng) {
    val projection = cameraPositionState.projection
    projection?.let {
      val screenPosition = it.toScreenLocation(markerLatLng)
      markerScreenPosition = Offset(screenPosition.x.toFloat(), screenPosition.y.toFloat())
    }
  }

  if (mutableStateSelectedUser >= 0 && mutableStateSelectedUser < filteredUsers.size) {
    computePositionOfMarker(
        cameraPositionState,
        LatLng(
            filteredUsers[mutableStateSelectedUser].latitude,
            filteredUsers[mutableStateSelectedUser].longitude))
  }

  val coroutineScope = rememberCoroutineScope()

  // Recalculate marker screen position during camera movement
  LaunchedEffect(cameraPositionState.position) {
    if (mutableStateSelectedUser >= 0 && mutableStateSelectedUser < filteredUsers.size) {
      computePositionOfMarker(
          cameraPositionState,
          LatLng(
              filteredUsers[mutableStateSelectedUser].latitude,
              filteredUsers[mutableStateSelectedUser].longitude))
    }
  }

  Scaffold(
      modifier = Modifier.testTag("mapScreen"),
      topBar = topAppBar,
      bottomBar = bottomAppBar,
      content = { pd ->
        Box(
            Modifier.padding(
                top = pd.calculateTopPadding(), bottom = pd.calculateBottomPadding())) {
              GoogleMap(
                  onMapClick = { mutableStateSelectedUser = NO_USER_SELECTED },
                  modifier =
                      Modifier.fillMaxSize().testTag("mapGoogleMap").semantics {
                        cameraPosition = cameraPositionState
                      },
                  cameraPositionState = cameraPositionState,
                  uiSettings = MapUiSettings(zoomControlsEnabled = false),
              ) {
                // Marker for user's current location
                if (!latitude.isNaN() && !longitude.isNaN()) {
                  Marker(
                      state = MarkerState(position = LatLng(latitude, longitude)),
                      title = "Your Location",
                      icon =
                          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                }
                filteredUsers
                    .filter {
                      !it.longitude.isNaN() && !it.latitude.isNaN() && it.books.isNotEmpty()
                    }
                    .forEachIndexed { index, item ->
                      val markerState =
                          MarkerState(position = LatLng(item.latitude, item.longitude))

                      Marker(
                          state = markerState,
                          onClick = {
                            mutableStateSelectedUser = index
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
                if (mutableStateSelectedUser >= 0 &&
                    mutableStateSelectedUser < filteredUsers.size &&
                    filteredUsers[mutableStateSelectedUser].books.isNotEmpty()) {
                  CustomInfoWindow(
                      modifier =
                          Modifier.offset {
                            IntOffset(screenPos.x.roundToInt(), screenPos.y.roundToInt())
                          },
                      userBooks = filteredUsers[mutableStateSelectedUser].books)
                }
              }
              // Draggable Bottom List
              DraggableMenu(filteredBooks)
            }
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

const val HEIGHT_RETRACTED_DRAGGABLE_MENU_DP = 50
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
  val maxSheetOffsetY = configuration.screenHeightDp.dp - BOTTOM_NAV_HEIGHT * 2
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
              .height(
                  (maxSheetOffsetY - sheetOffsetY).coerceIn(maxSheetOffsetY / 10, maxSheetOffsetY))
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
                          topStart = HEIGHT_RETRACTED_DRAGGABLE_MENU_DP.dp,
                          topEnd = HEIGHT_RETRACTED_DRAGGABLE_MENU_DP.dp))
              .testTag("mapDraggableMenu")) {
        Column(modifier = Modifier.fillMaxWidth().testTag("mapDraggableMenuStructure")) {
          // draggable handle
          Spacer(modifier = Modifier.height(HANDLE_HEIGHT_DP.dp))
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
          LazyColumn(userScrollEnabled = true, modifier = Modifier.fillMaxHeight()) {
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
              itemsIndexed(listAllBooks) { index, book ->
                Spacer(modifier = Modifier.height(PADDING_VERTICAL_DP.dp))
                BookDisplayComponent(
                    modifier =
                        Modifier.heightIn(min = MIN_BOX_BOOK_HEIGHT_DP.dp)
                            .testTag("mapDraggableMenuBookBox${index}"),
                    book = book)

                // Divider below each item
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().testTag("mapDraggableMenuBookBoxDivider"),
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

data class UserBooksWithLocation(
    val longitude: Double,
    val latitude: Double,
    val books: List<DataBook>
)
