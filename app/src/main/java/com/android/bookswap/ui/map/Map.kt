package com.android.bookswap.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.bookswap.model.DataBook
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.GoogleMap

@Composable
fun MapScreen(listUser : List<TempUser>) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f) // San Francisco
    }

    var selectedUser by remember { mutableStateOf<TempUser?>(null) }

    Scaffold(
        bottomBar = {
            //To add a bottom navigation bar when it will be created
        },
        content = { pd ->
            GoogleMap(
                onMapClick = {
                    selectedUser = null
                },
                modifier = Modifier.fillMaxSize().padding(pd).testTag("mapScreen"),
                cameraPositionState = cameraPositionState) {
                listUser
                    .filter { !it.longitude.isNaN() && !it.latitude.isNaN() && it.listBook.isNotEmpty()}
                    .forEach { item ->
                        Marker(
                            state =
                            MarkerState(
                                position =
                                LatLng(item.latitude, item.longitude)),
                            title = "temp",
                            onClick = {
                                selectedUser = item
                                true // Return true to consume the event
                            },
                        )
                    }
            }
            if (selectedUser != null) {
                BookListCard(
                    books = selectedUser?.listBook ?: emptyList(),
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        })
}

@Composable
fun BookListCard(books: List<DataBook>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .wrapContentSize()
            .background(Color.White)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = "Books", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            books.forEach { book ->
                Text(text = "Title: ${book.Title}", fontWeight = FontWeight.Bold)
                Text(text = "Author: ${book.Author}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}




data class TempUser(
    val latitude: Double,
    val longitude: Double,
    val listBook: List<DataBook>
)