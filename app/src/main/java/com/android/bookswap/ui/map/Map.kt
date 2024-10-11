package com.android.bookswap.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.DataBook
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.GoogleMap
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Composable function to display a map with user locations and associated book information.
 *
 * This screen renders a GoogleMap that shows books locations as markers. Upon clicking a marker, it
 * displays a custom info window with the list of books at this location.
 *
 * @param listUser List of users (`TempUser`) to display on the map, each containing their location
 *   (latitude, longitude) and a list of books they own (`listBook`). This argument will later be
 *   deleted as the code should in the future use a class to get the user from the database.
 * @param selectedUser An optional user (`TempUser`) to be initially selected and focused on the
 *   map. This user’s info window will be shown if not null.
 */
@Composable
fun MapScreen(listUser: List<TempUser>, selectedUser: TempUser? = null) {

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f) // Initial camera position
  }

  var mutableStateSelectedUser by remember { mutableStateOf(selectedUser) }
  var markerScreenPosition by remember { mutableStateOf<Offset?>(null) }

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
        // To add a bottom navigation bar when it will be created
      },
      content = { pd ->
        GoogleMap(
            onMapClick = { mutableStateSelectedUser = null },
            modifier = Modifier.fillMaxSize().padding(pd).testTag("mapGoogleMap"),
            cameraPositionState = cameraPositionState,
        ) {
          listUser
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

        // Custom info window linked to the marker
        markerScreenPosition?.let { screenPos ->
          mutableStateSelectedUser?.let { user ->
            CustomInfoWindow(
                modifier =
                    Modifier.offset {
                      IntOffset(screenPos.x.roundToInt(), screenPos.y.roundToInt())
                    },
                user = user)
          }
        }
      })
}

/**
 * Composable function to display a custom info window for a selected book's list marker on the map.
 *
 * This function creates a card that shows detailed information about a user's list of books when
 * the corresponding map marker is clicked.
 *
 * @param modifier A `Modifier` to apply to the card containing the custom info window. It can be
 *   used to modify the position, size, and appearance of the info window, but is mainly intended to
 *   give the position of the CustomInfoWindow. Default is `Modifier`.
 * @param user The `TempUser` object containing the list of books (`listBook`) to be displayed
 *   inside the info window.
 */
@Composable
fun CustomInfoWindow(modifier: Modifier = Modifier, user: TempUser) {
  Card(
      modifier =
          modifier
              .wrapContentSize()
              .width(200.dp)
              .border(
                  BorderStroke(width = 1.dp, color = Color.hsl(23f, 0.17f, 0.36f, 1f)),
                  shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp))
              .heightIn(max = 400.dp)
              .testTag("mapBoxMarker")
              .background(Color.Transparent),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      colors = CardDefaults.cardColors(containerColor = Color.hsl(70f, 0.47f, 0.81f, 1f)),
      shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp)) {
        Spacer(modifier.height(10.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth().testTag("mapBoxMarkerList")) {
          itemsIndexed(user.listBook) { index, book ->
            Column(modifier = Modifier.padding(horizontal = 8.dp).testTag("mapBoxMarkerListBox")) {
              Text(
                  text = book.title,
                  color = Color.hsl(23f, 0.17f, 0.36f, 1f),
                  fontSize = 20.sp,
                  modifier = Modifier.testTag("mapBoxMarkerListBoxTitle"))
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                  text = book.author ?: "",
                  color = Color.hsl(26f, 0.28f, 0.53f, 1f),
                  fontSize = 16.sp,
                  modifier = Modifier.testTag("mapBoxMarkerListBoxAuthor"))
            }
            if (index < user.listBook.size - 1)
                HorizontalDivider(
                    modifier =
                        Modifier.fillMaxWidth().height(5.dp).testTag("mapBoxMarkerListDivider"),
                    thickness = 1.dp,
                    color = Color.hsl(23f, 0.17f, 0.36f, 1f))
          }
        }
        Spacer(modifier.height(4.dp))
      }
}

// need to be removed when user dataclass will be created
data class TempUser(val latitude: Double, val longitude: Double, val listBook: List<DataBook>)
