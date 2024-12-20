package com.android.bookswap.ui.map

import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.android.bookswap.R
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.isNetworkAvailable
import com.android.bookswap.model.map.BookManagerViewModel
import com.android.bookswap.model.map.DefaultGeolocation
import com.android.bookswap.model.map.IGeolocation
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.BookListComponent
import com.android.bookswap.ui.navigation.BOTTOM_NAV_HEIGHT
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.GoogleMap
import java.util.UUID
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** Constants * */
const val INIT_ZOOM = 10F
const val NO_USER_SELECTED = -1
const val SHOW_MARKER_DISTANCE = 50

val CameraPositionKey = SemanticsPropertyKey<CameraPositionState>("CameraPosition")
var SemanticsPropertyReceiver.cameraPosition by CameraPositionKey

/**
 * Composable function to display a map with user locations and associated book information.
 *
 * This screen renders a GoogleMap that shows book locations as markers. Upon clicking a marker, it
 * displays a custom info window with the list of books at this location.
 *
 * @param bookManagerViewModel The view model that provides the map screen with the list of books to
 *   display.
 * @param navigationActions The navigation actions to handle navigation events.
 * @param selectedUser An optional user, it will display the info window related to this user. This
 *   user’s info window will be shown if it is greater than or equal to 0.
 * @param geolocation An instance of [IGeolocation] to get the user's current location.
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 */
@Composable
fun MapScreen(
    bookManagerViewModel: BookManagerViewModel,
    navigationActions: NavigationActions,
    selectedUser: Int = NO_USER_SELECTED,
    geolocation: IGeolocation = DefaultGeolocation(),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {},
) {
  val appConfig = LocalAppConfig.current
  val userVM = appConfig.userViewModel
  val cameraPositionState = rememberCameraPositionState {
    this.position =
        CameraPosition.fromLatLngZoom(
            userVM.getUser().let { LatLng(it.latitude, it.longitude) }, INIT_ZOOM)
  }
  var mapProperties by remember { mutableStateOf(MapProperties()) }
  var mapUISettings by remember {
    mutableStateOf(MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false))
  }
  fun enableLocation() {
    geolocation.startLocationUpdates()
    mapProperties = MapProperties(isMyLocationEnabled = true)
    mapUISettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = false)
  }
  val permissions =
      arrayOf(
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.ACCESS_FINE_LOCATION)
  val permLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (permissions.map { p -> it.getOrDefault(p, false) }.contains(true)) {
          enableLocation()
        }
      }
  // Get the user's current location
  val latitude = geolocation.latitude.collectAsState()
  val longitude = geolocation.longitude.collectAsState()
  val context = LocalContext.current
  var isOnline = remember { isNetworkAvailable(context) }
  // Start location and books updates
  LaunchedEffect(Unit) {
    appConfig.userViewModel.getContacts()
    if (isOnline) {
      bookManagerViewModel.startUpdatingBooks()
    } else {
      Toast.makeText(
              context,
              context.getString(R.string.map_screen_please_connect_to_internet),
              Toast.LENGTH_SHORT)
          .show()
    }
    val hasPermissions =
        permissions
            .map {
              ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
            .contains(true)
    if (hasPermissions) {
      enableLocation()
    } else {
      permLauncher.launch(permissions)
    }
    isOnline = isNetworkAvailable(context)
  }
  // Stop location and books updates when the screen is disposed
  DisposableEffect(Unit) {
    onDispose {
      if (!latitude.value.isNaN() && !longitude.value.isNaN()) {
        userVM.updateAddress(latitude.value, longitude.value, context)
      }
      geolocation.stopLocationUpdates()
      bookManagerViewModel.stopUpdatingBooks()
    }
  }

  var mutableStateSelectedUser by remember { mutableIntStateOf(selectedUser) }
  var markerScreenPosition by remember { mutableStateOf<Offset?>(null) }

  val filteredBooks = bookManagerViewModel.filteredBooks.collectAsState()

  val filteredUsers = bookManagerViewModel.filteredUsers.collectAsState()

  // compute the position of the marker on the screen given the camera position and the marker's
  // position on the map
  fun computePositionOfMarker(cameraPositionState: CameraPositionState, markerLatLng: LatLng) {
    val projection = cameraPositionState.projection
    projection?.let {
      val screenPosition = it.toScreenLocation(markerLatLng)
      markerScreenPosition = Offset(screenPosition.x.toFloat(), screenPosition.y.toFloat())
    }
  }

  if (mutableStateSelectedUser >= 0 && mutableStateSelectedUser < filteredUsers.value.size) {
    computePositionOfMarker(
        cameraPositionState,
        LatLng(
            filteredUsers.value[mutableStateSelectedUser].latitude,
            filteredUsers.value[mutableStateSelectedUser].longitude))
  }

  val coroutineScope = rememberCoroutineScope()

  // Recalculate marker screen position during camera movement
  LaunchedEffect(cameraPositionState.position) {
    if (mutableStateSelectedUser >= 0 && mutableStateSelectedUser < filteredUsers.value.size) {
      computePositionOfMarker(
          cameraPositionState,
          LatLng(
              filteredUsers.value[mutableStateSelectedUser].latitude,
              filteredUsers.value[mutableStateSelectedUser].longitude))
    }
  }

  // State to control the visibility of the marker
  var showMarker by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag(C.Tag.map_screen_container),
      topBar = topAppBar,
      bottomBar = bottomAppBar,
      content = { pd ->
        Box(
            Modifier.padding(
                top = pd.calculateTopPadding(), bottom = pd.calculateBottomPadding())) {

              // Marker state for user's location
              val markerState = remember {
                MarkerState(position = LatLng(latitude.value, longitude.value))
              }

              // Update the marker's position when latitude or longitude changes
              LaunchedEffect(latitude.value, longitude.value) {
                markerState.position = LatLng(latitude.value, longitude.value)
              }

              // Show the InfoWindow when the marker becomes visible
              LaunchedEffect(showMarker) {
                if (showMarker) {
                  markerState.showInfoWindow()
                } else {
                  markerState.hideInfoWindow()
                }
              }

              GoogleMap(
                  onMapClick = { clickedLatLng ->
                    Log.i("MapScreen", "Map clicked")

                    // Check if the clicked location is near the user's location
                    val userLocation = LatLng(latitude.value, longitude.value)
                    val distance = FloatArray(1)

                    // Calculate the distance between the clicked point and the user's location
                    Location.distanceBetween(
                        clickedLatLng.latitude,
                        clickedLatLng.longitude,
                        userLocation.latitude,
                        userLocation.longitude,
                        distance)

                    // Show marker if the distance is within 50 meters
                    showMarker = distance[0] <= SHOW_MARKER_DISTANCE

                    mutableStateSelectedUser = NO_USER_SELECTED
                  },
                  onMapLoaded = {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(
                            LatLng(latitude.value, longitude.value), INIT_ZOOM)
                  },
                  modifier =
                      Modifier.fillMaxSize().testTag(C.Tag.Map.google_map).semantics {
                        cameraPosition = cameraPositionState
                      },
                  cameraPositionState = cameraPositionState,
                  uiSettings = mapUISettings,
                  properties = mapProperties,
              ) {
                // Marker for user's current location
                if (!latitude.value.isNaN() && !longitude.value.isNaN()) {
                  Log.i("MapScreen", "Marker displayed at: ${latitude.value}, ${longitude.value}")
                  Marker(
                      state = markerState,
                      title = stringResource(R.string.map_screen_your_location),
                      visible = showMarker,
                      icon =
                          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                }
                filteredUsers.value
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
                            // Navigate to the user profile
                            /*navigationActions.navigateTo(
                            screen = C.Screen.OTHERS_USER_PROFILE,
                            UUID =
                                item.userUUID
                                    .toString() // Assuming `item` has a unique UUID field
                            // called `id`
                            )*/
                            false
                          })
                    }
              }
              FilterButton { navigationActions.navigateTo(C.Screen.MAP_FILTER) }

              // Custom info window linked to the marker
              markerScreenPosition?.let { screenPos ->
                if (mutableStateSelectedUser >= 0 &&
                    mutableStateSelectedUser < filteredUsers.value.size &&
                    filteredUsers.value[mutableStateSelectedUser].books.isNotEmpty()) {
                  CustomInfoWindow(
                      modifier =
                          Modifier.offset {
                            IntOffset(screenPos.x.roundToInt(), screenPos.y.roundToInt())
                          },
                      userBooks = filteredUsers.value[mutableStateSelectedUser].books,
                      filteredUsers.value[mutableStateSelectedUser].userUUID,
                      navigationActions)
                }
              }
              // Draggable Bottom List
              DraggableMenu(filteredBooks.value, navigationActions)
            }
      })
}
/** Constants used for padding, dimensions, and font sizes in the UI components. */
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
 * @param userBooks The list of [DataBook] objects containing the books to be displayed inside the
 *   info window.
 */
@Composable
private fun CustomInfoWindow(
    modifier: Modifier = Modifier,
    userBooks: List<DataBook>,
    userUUID: UUID,
    navigationActions: NavigationActions
) {
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
              .testTag(C.Tag.Map.Marker.info_window_container)
              .background(Color.Transparent),
      colors = CardDefaults.cardColors(containerColor = ColorVariable.Secondary),
      shape =
          RoundedCornerShape(
              0.dp, CARD_CORNER_RADIUS.dp, CARD_CORNER_RADIUS.dp, CARD_CORNER_RADIUS.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().testTag(C.Tag.Map.Marker.info_window_scrollable)) {
              item {
                Column(
                    modifier =
                        Modifier.clickable {
                              navigationActions.navigateTo(
                                  screen = C.Screen.OTHERS_USER_PROFILE, UUID = userUUID.toString())
                            }
                            .background(ColorVariable.Green)
                            .testTag(C.Tag.Map.Marker.info_window_user_profile)) {
                      Text(
                          text = stringResource(R.string.map_screen_window_user),
                          color = ColorVariable.Accent,
                          fontSize = PRIMARY_TEXT_FONT_SP.sp,
                          modifier =
                              Modifier.padding(
                                  horizontal = PADDING_HORIZONTAL_DP.dp,
                                  vertical = CARD_CORNER_RADIUS.dp))
                      HorizontalDivider(
                          modifier = Modifier.fillMaxWidth(),
                          thickness = DIVIDER_THICKNESS_DP.dp,
                          color = ColorVariable.Accent)
                    }
              }
              itemsIndexed(userBooks) { index, book ->
                Column(
                    modifier =
                        Modifier.padding(horizontal = PADDING_HORIZONTAL_DP.dp)
                            .testTag(C.Tag.Map.Marker.info_window_book_container)) {
                      Text(
                          text = book.title,
                          color = ColorVariable.Accent,
                          fontSize = PRIMARY_TEXT_FONT_SP.sp,
                          modifier = Modifier.testTag(C.Tag.Map.Marker.book_title))
                      Spacer(modifier = Modifier.height(PADDING_VERTICAL_DP.dp))
                      Text(
                          text = book.author ?: "",
                          color = ColorVariable.AccentSecondary,
                          fontSize = SECONDARY_TEXT_FONT_SP.sp,
                          modifier = Modifier.testTag(C.Tag.Map.Marker.book_author))
                    }
                if (index < userBooks.size - 1)
                    HorizontalDivider(
                        modifier =
                            Modifier.fillMaxWidth()
                                .height(PADDING_VERTICAL_DP.dp)
                                .testTag(C.Tag.Map.Marker.info_window_divider),
                        thickness = DIVIDER_THICKNESS_DP.dp,
                        color = ColorVariable.Accent)
              }
            }
        Spacer(modifier.height(PADDING_VERTICAL_DP.dp))
      }
}
/** Constants used for dimensions and sizes in the draggable menu UI components. */
const val HEIGHT_RETRACTED_DRAGGABLE_MENU_DP = 50
// 1.5:1 ratio + the padding
const val HANDLE_WIDTH_DP = 120
const val HANDLE_HEIGHT_DP = 15
const val HANDLE_CORNER_RADIUS_DP = 10
const val SPACER_HEIGHT_DP = 20

/**
 * Composable function to display a draggable menu containing all the nearest books available.
 *
 * This function creates a draggable menu that can be pulled up or down to show or hide the list of
 * books. The menu's position is adjusted based on the user's drag gestures.
 *
 * @param listAllBooks A [List] of all [DataBook] sorted with the nearest book at the first position
 *   and the furthest one at the last position.
 * @param navigationActions The navigation actions to handle navigation events.
 */
@Composable
private fun DraggableMenu(listAllBooks: List<DataBook>, navigationActions: NavigationActions) {

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
              .testTag(C.Tag.Map.bottom_drawer_container)) {
        Column(modifier = Modifier.fillMaxWidth().testTag(C.Tag.Map.bottom_drawer_layout)) {
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
                      .testTag(C.Tag.Map.bottom_drawer_handle))
          Spacer(modifier = Modifier.height(SPACER_HEIGHT_DP.dp))
          HorizontalDivider(
              modifier = Modifier.fillMaxWidth().testTag(C.Tag.Map.bottom_drawer_handle_divider),
              thickness = DIVIDER_THICKNESS_DP.dp,
              color = ColorVariable.Accent)
          BookListComponent(
              Modifier,
              listAllBooks,
              onBookClick = { bookId ->
                navigationActions.navigateTo("${C.Screen.BOOK_PROFILE}/$bookId")
              })
        }
      }
}
