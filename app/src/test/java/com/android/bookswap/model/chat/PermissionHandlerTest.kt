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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], application = TestApplicationChat::class)
class PermissionHandlerTest {

  @Mock private lateinit var mockActivity: ComponentActivity

  private lateinit var permissionHandler: PermissionHandler

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockActivity = spy(Robolectric.buildActivity(ComponentActivity::class.java).get())
    permissionHandler = PermissionHandler(mockActivity)
    FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext<Context>())
  }

  @Test
  fun `test askNotificationPermission when permission is granted`() {
    // Arrange
    mockStatic(ContextCompat::class.java).use { mockedContextCompat ->
      mockedContextCompat
          .`when`<Int> {
            ContextCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
          }
          .thenReturn(PackageManager.PERMISSION_GRANTED)

      // Act
      permissionHandler.askNotificationPermission()

      // Assert
      verify(mockActivity, never())
          .shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  @Test
  fun `test askNotificationPermission when permission is not granted and rationale should not be shown`() {
    // Arrange
    reset(mockActivity)
    mockStatic(ContextCompat::class.java).use { mockedContextCompat ->
      `when`(
              ContextCompat.checkSelfPermission(
                  any(Context::class.java), eq(Manifest.permission.POST_NOTIFICATIONS)))
          .thenReturn(PackageManager.PERMISSION_DENIED)

      `when`(
              mockActivity.shouldShowRequestPermissionRationale(
                  Manifest.permission.POST_NOTIFICATIONS))
          .thenReturn(false)

      // Act
      permissionHandler.askNotificationPermission()

      // Assert
      verify(mockActivity, times(1))
          .shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

      // Ensure no other interactions are made with this method
      verifyNoMoreInteractions(mockActivity)
    }
  }

  @Test
  fun `test enableNotifications shows toast`() {
    // Arrange
    val toast = mock(Toast::class.java)
    mockStatic(Toast::class.java).use { mockedToast ->
      `when`(Toast.makeText(any(Context::class.java), anyString(), anyInt())).thenReturn(toast)

      // Act
      permissionHandler.enableNotifications()

      // Assert
      verify(toast, times(1)).show()
    }
  }

  @Test
  fun `test informUserNotificationsDisabled shows toast`() {
    // Arrange
    val toast = mock(Toast::class.java)
    mockStatic(Toast::class.java).use { mockedToast ->
      `when`(Toast.makeText(any(Context::class.java), anyString(), anyInt())).thenReturn(toast)
      // Act
      permissionHandler.informUserNotificationsDisabled()

      // Assert
      verify(toast, times(1)).show()
    }
  }
}
