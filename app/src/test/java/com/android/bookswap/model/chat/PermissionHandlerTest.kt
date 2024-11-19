package com.android.bookswap.model.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], application = TestApplicationChat::class)
class PermissionHandlerTest {

  private val mockActivity: ComponentActivity =
      spyk(Robolectric.buildActivity(ComponentActivity::class.java).get())

  private val permissionHandler: PermissionHandler = PermissionHandler(mockActivity)

  @Before
  fun setUp() {
    FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
  }

  @Test
  fun `test askNotificationPermission when permission is granted`() {
    // Arrange
    mockkStatic(ContextCompat::class)
    every {
      ContextCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
    } returns PackageManager.PERMISSION_GRANTED

    // Act
    permissionHandler.askNotificationPermission()

    // Assert
    verify(exactly = 0) {
      mockActivity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  @Test
  fun `test enableNotifications shows toast`() {
    // Arrange
    val toast: Toast = mockk()
    mockkStatic(Toast::class)
    every { Toast.makeText(any<Context>(), any<String>(), any()) } returns toast
    every { toast.show() } just runs

    // Act
    permissionHandler.enableNotifications()

    // Assert
    verify(exactly = 1) { toast.show() }
  }

  @Test
  fun `test informUserNotificationsDisabled shows toast`() {
    // Arrange
    val toast: Toast = mockk()
    mockkStatic(Toast::class)
    every { Toast.makeText(any<Context>(), any<String>(), any()) } returns toast
    every { toast.show() } just runs
    // Act
    permissionHandler.informUserNotificationsDisabled()

    // Assert
    verify(exactly = 1) { toast.show() }
  }
}
