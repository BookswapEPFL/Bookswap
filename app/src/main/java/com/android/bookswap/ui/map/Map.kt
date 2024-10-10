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
import com.android.bookswap.model.DataBook
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.GoogleMap
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MapScreen(listUser: List<TempUser>) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f) // Initial camera position
    }

    var selectedUser by remember { mutableStateOf<TempUser?>(null) }
    var markerScreenPosition by remember { mutableStateOf<Offset?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Recalculate marker screen position during camera movement
    LaunchedEffect(cameraPositionState.position) {
        if (selectedUser != null) {
            val projection = cameraPositionState.projection
            projection?.let {
                val markerLatLng = LatLng(selectedUser!!.latitude, selectedUser!!.longitude)
                val screenPosition = it.toScreenLocation(markerLatLng)
                markerScreenPosition = Offset(screenPosition.x.toFloat(), screenPosition.y.toFloat())
            }
        }
    }

    Scaffold(
        modifier = Modifier.testTag("mapScreen"),
        bottomBar = {
            //To add a bottom navigation bar when it will be created
        },
        content = { pd ->
            GoogleMap(
                onMapClick = {
                    selectedUser = null
                },
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
                                selectedUser = item
                                coroutineScope.launch {
                                    // Calculate the screen position when marker is clicked
                                    val projection = cameraPositionState.projection
                                    projection?.let {
                                        val screenPosition = it.toScreenLocation(markerState.position)
                                        markerScreenPosition = Offset(screenPosition.x.toFloat(), screenPosition.y.toFloat())
                                    }
                                }
                                false
                            }
                        )
                    }
            }

            // Custom info window linked to the marker
            markerScreenPosition?.let { screenPos ->
                selectedUser?.let { user ->
                    CustomInfoWindow(
                        modifier = Modifier
                            .offset { IntOffset(screenPos.x.roundToInt(), screenPos.y.roundToInt()) },
                        user = user
                    )
                }
            }

        }
    )
}

@Composable
fun CustomInfoWindow(modifier: Modifier = Modifier, user: TempUser) {
    Card(
        modifier = modifier
            .wrapContentSize().width(200.dp)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = Color.hsl(23f, 0.17f, 0.36f, 1f)),
                shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp)
            )
            .heightIn(max = 400.dp)
            .testTag("mapBoxMarker")
            .background(Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.hsl(70f, 0.47f, 0.81f, 1f)),
        shape = RoundedCornerShape(0.dp,10.dp,10.dp,10.dp)
    ) {
        Spacer(modifier.height(10.dp))
        LazyColumn (
            modifier = Modifier.fillMaxWidth().testTag("mapBoxMarkerList")
        ) {
            itemsIndexed(user.listBook) { index, book ->
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp).testTag("mapBoxMarkerListBox")
                ) {
                    Text(
                        text = book.Title,
                        color = Color.hsl(23f, 0.17f, 0.36f, 1f),
                        fontSize = 20.sp,
                        modifier = Modifier.testTag("mapBoxMarkerListBoxTitle")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.Author,
                        color = Color.hsl(26f, 0.28f, 0.53f, 1f),
                        fontSize = 16.sp,
                        modifier = Modifier.testTag("mapBoxMarkerListBoxTitle")
                    )
                }
                if(index < user.listBook.size - 1)
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                        .height(5.dp)
                        .testTag("mapBoxMarkerListSpacer"),
                    thickness = 1.dp,
                    color = Color.hsl(23f, 0.17f, 0.36f, 1f)
                )
            }
        }
        Spacer(modifier.height(4.dp))
    }
}





data class TempUser(
    val latitude: Double,
    val longitude: Double,
    val listBook: List<DataBook>
)