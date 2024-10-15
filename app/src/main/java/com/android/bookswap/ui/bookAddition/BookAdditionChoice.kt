package com.android.bookswap.ui.bookAddition

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.navigation.BackButton
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.profile.ProfileIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAdditionChoiceScreen(navController: NavigationActions) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = { BackButton(navController)},
                actions = { ProfileIcon() }
            )
        },
        bottomBar = {
            // BottomNavigationBar() // Uncomment and implement this later
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonWithIcon(text = "Manually", leftIcon = Icons.Default.Add, rightIcon = Icons.Default.ArrowForward)
            Spacer(modifier = Modifier.height(16.dp))
            ButtonWithIcon(text = "From ISBN", leftIcon = Icons.Default.KeyboardArrowDown, rightIcon = Icons.Default.ArrowForward)
            Spacer(modifier = Modifier.height(16.dp))
            ButtonWithIcon(text = "From Photo", leftIcon = Icons.Default.KeyboardArrowUp, rightIcon = Icons.Default.ArrowForward)
        }
    }
}

@Composable
fun ButtonWithIcon(text: String, leftIcon: ImageVector, rightIcon: ImageVector) {
    Button(onClick = { /* TODO: Handle button click */ }) {
        Icon(imageVector = leftIcon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = rightIcon, contentDescription = null)
    }
}