package com.android.bookswap.ui.authentication

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.R
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Screen
import com.android.bookswap.ui.navigation.TopLevelDestinations
import com.android.bookswap.ui.theme.ColorVariable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Composable function for the SignIn screen.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun SignInScreen(
    navigationActions: NavigationActions,
    userVM: UserViewModel
) { // Add this when navigation is
  // implemented
  val context = LocalContext.current
  var googleUid = ""
  // Check if user is already signed in
  LaunchedEffect(Unit) {
    if (Firebase.auth.currentUser != null) {
      navigationActions.navigateTo(TopLevelDestinations.MAP)
    }
  }

  val launcher =
      rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            val googleUserName = result.user?.displayName ?: ""
            googleUid = result.user?.uid ?: ""
            userVM.getUserByGoogleUid(googleUid)
            Log.d("SignInScreen", "isStored: ${userVM.isStored}")
            Log.d("SignInScreen", "User signed in: $googleUserName")
            Toast.makeText(context, "Welcome $googleUserName!", Toast.LENGTH_LONG).show()
          },
          onAuthError = {
            Log.e("SignInScreen", "Failed to sign in: ${it.statusCode}")
            Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show()
          })
  val token = stringResource(R.string.default_web_client_id)

  val isStored by userVM.isStored.collectAsState()

  LaunchedEffect(isStored) {
    when (isStored) {
      true -> navigationActions.navigateTo(TopLevelDestinations.MAP)
      false -> {
        navigationActions.navigateTo(Screen.NEW_USER)
      }
      null -> {} // Attendre que `isStored` soit défini
    }
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("SignInScreen"),
      containerColor = ColorVariable.BackGround, // Set the background color
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          // App Logo Image
          Image(
              painter = painterResource(id = R.drawable.logo5), // Bookswap logo
              contentDescription = "App Logo",
              modifier = Modifier.size(250.dp))

          Spacer(modifier = Modifier.height(50.dp))

          // First part of the title:
          Text(
              modifier = Modifier.testTag("login_loginTitle1"),
              text = "Welcome to",
              style =
                  TextStyle(
                      fontSize = 40.sp,
                      lineHeight = 40.sp,
                      // fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(600),
                      color = Color(108, 88, 76),
                      letterSpacing = 0.4.sp,
                      textAlign = TextAlign.Center))

          Spacer(modifier = Modifier.height(5.dp))

          // Second part of the logo:
          Text(
              modifier = Modifier.testTag("login_loginTitle2"),
              text = "BookSwap",
              style =
                  TextStyle(
                      fontSize = 60.sp,
                      lineHeight = 40.sp,
                      // fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(800),
                      color = Color(108, 88, 76),
                      letterSpacing = 0.6.sp,
                      textAlign = TextAlign.Center))

          Spacer(modifier = Modifier.height(50.dp))

          // Authenticate With Google Button
          GoogleSignInButton(
              onSignInClick = {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
              })
        }
      })
}
/**
 * Composable function for the Google Sign-In button.
 *
 * @param onSignInClick Lambda function to be executed when the button is clicked.
 */
@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  Button(
      onClick = onSignInClick,
      colors =
          ButtonDefaults.buttonColors(containerColor = ColorVariable.Secondary), // Button color
      shape = RoundedCornerShape(50), // Circular edges for the button
      border = BorderStroke(1.dp, ColorVariable.Primary), // Button's border color
      modifier =
          Modifier.padding(8.dp)
              .height(48.dp) // Adjust height as needed
              .testTag("loginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
              // Google logo
              Image(
                  painter = painterResource(id = R.drawable.google_logo), // Google logo
                  contentDescription = "Google Logo",
                  modifier =
                      Modifier.size(30.dp) // Size of the Google logo
                          .padding(end = 8.dp))

              // Text for the button
              Text(
                  text = "Sign in with Google",
                  color = ColorVariable.Accent, // Text color
                  fontSize = 16.sp, // Font size
                  fontWeight = FontWeight.Medium)
            }
      }
}
/**
 * Remembers a Firebase authentication launcher for Google Sign-In.
 *
 * @param onAuthComplete Lambda function to be executed when authentication is complete.
 * @param onAuthError Lambda function to be executed when authentication fails.
 * @return A `ManagedActivityResultLauncher` for handling the Google Sign-In intent.
 */
@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()
  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
      scope.launch {
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        onAuthComplete(authResult)
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}
