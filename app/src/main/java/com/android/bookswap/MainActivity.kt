package com.android.bookswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.resources.C
import com.android.bookswap.ui.chat.ChatScreen
import com.android.bookswap.ui.theme.BookSwapAppTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val db = FirebaseFirestore.getInstance()
      val messageRepository = MessageFirestoreSource(db)
      val currentUserId = "user123" // Replace with the actual logged-in user ID
      val otherUserId = "user124"

      setContent {
        // Call your MessageView composable here
        ChatScreen(
            messageRepository = messageRepository,
            currentUserId = currentUserId,
            otherUserId = otherUserId)
      }

      /**
       * BookSwapAppTheme { // A surface container using the 'background' color from the theme
       * Surface( modifier = Modifier.fillMaxSize().semantics { testTag =
       * C.Tag.main_screen_container }, color = MaterialTheme.colorScheme.background) {
       * Greeting("Android") } }
       */
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  BookSwapAppTheme { Greeting("Android") }
}
