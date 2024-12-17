package com.android.bookswap.model

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import java.io.File

class PhotoRequesterTest {
    @get:Rule val composeTestRule = createComposeRule()
    private val mockContext: Context = mockk()


    @Before
    fun setup() {

        val mockFile: File = mockk()
        val mockURI: Uri = mockk()
        val mockContentResolver: ContentResolver = mockk()

        every { mockContext.cacheDir } returns mockFile
        every { mockContext.contentResolver } returns mockContentResolver

        mockkStatic(File::class)
        every { File.createTempFile(any(), any(), any()) } returns mockFile
        mockkStatic(FileProvider::class)

        every { FileProvider.getUriForFile(any(), any(), any()) } returns mockURI
    }

    @Test
    fun photoRequesterSuccessWhenFine() {
        val mockCallback: (Result<ImageBitmap>) -> Unit = mockk()
        every { mockCallback(any()) } just Runs


        val mockSource: ImageDecoder.Source = mockk()
        val mockBitmap: Bitmap = mockk()


        mockkStatic(ImageDecoder::class)
        every { ImageDecoder.createSource(any<ContentResolver>(), any()) } returns mockSource
        every { ImageDecoder.decodeBitmap(mockSource) } returns mockBitmap


        val testRegistry = object : ActivityResultRegistry() {

            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                when( contract::class) {
                    ActivityResultContracts.RequestPermission::class -> {
                        val requestPermissionResponse = true
                        dispatchResult(requestCode, requestPermissionResponse)
                    }
                    ActivityResultContracts.TakePicture::class -> {
                        val takePictureResponse = true
                        dispatchResult(requestCode, takePictureResponse)
                    }
                    else -> {
                        assertTrue("Contract of type ${contract::class} is not implemented", false)
                    }
                }
            }
        }

        val photoRequester = PhotoRequester(mockContext, mockCallback)

        composeTestRule.setContent {
            WithActivityResultRegistry(testRegistry) {
                photoRequester.Init()
            }

        }
        photoRequester.requestPhoto()

        verify { mockCallback(Result.success(any<ImageBitmap>())) }
    }

    @Test
    fun photoRequesterInitError() {
        val mockCallback: (Result<ImageBitmap>) -> Unit = mockk()
        every { mockCallback(any()) } just Runs

        val photoRequester = PhotoRequester(mockContext, mockCallback)

        assertThrows(PhotoRequester.Companion.ExceptionType.INIT_NOT_CALLED.toException()::class.java) {
            photoRequester.requestPhoto()
        }
    }

    @Test
    fun photoRequesterErrorPermissionDenied() {
        val mockCallback: (Result<ImageBitmap>) -> Unit = mockk()

        val testRegistry = object : ActivityResultRegistry() {

            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                when( contract::class) {
                    ActivityResultContracts.RequestPermission::class -> {
                        val requestPermissionResponse = true
                        dispatchResult(requestCode, requestPermissionResponse)
                    }
                    //Make TakePicture fail
                    ActivityResultContracts.TakePicture::class -> {
                        val takePictureResponse = false
                        dispatchResult(requestCode, takePictureResponse)
                    }
                    else -> {
                        assertTrue("Contract of type ${contract::class} is not implemented", false)
                    }
                }
            }
        }

        val photoRequester = PhotoRequester(mockContext, mockCallback)

        composeTestRule.setContent {
            WithActivityResultRegistry(testRegistry) {
                photoRequester.Init()
            }

        }

        every { mockCallback(any()) } answers {
            val exception = firstArg<Result<ImageBitmap>>().exceptionOrNull()
            assertEquals(PhotoRequester.Companion.ExceptionType.IMAGE_NOT_SAVED.toException().message, exception!!.message)
        }

        photoRequester.requestPhoto()

        verify { mockCallback(any()) }
    }

    @Test
    fun photoRequesterErrorImageSaved() {
        val mockCallback: (Result<ImageBitmap>) -> Unit = mockk()

        val testRegistry = object : ActivityResultRegistry() {

            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                when( contract::class) {
                    //Make request permission fail
                    ActivityResultContracts.RequestPermission::class -> {
                        val requestPermissionResponse = false
                        dispatchResult(requestCode, requestPermissionResponse)
                    }
                    else -> {
                        assertTrue("Contract of type ${contract::class} is not implemented", false)
                    }
                }
            }
        }

        val photoRequester = PhotoRequester(mockContext, mockCallback)

        composeTestRule.setContent {
            WithActivityResultRegistry(testRegistry) {
                photoRequester.Init()
            }

        }

        every { mockCallback(any()) } answers {
            val exception = firstArg<Result<ImageBitmap>>().exceptionOrNull()
            assertEquals(PhotoRequester.Companion.ExceptionType.PERMISSION_DENIED.toException().message, exception!!.message)
        }

        photoRequester.requestPhoto()

        verify { mockCallback(any()) }
    }



    /**
     * Defines a custom [ActivityResultRegistry] to be used when calls to ActivityResultContracts API via rememberLauncherForActivityResult.
     *
     * This allow us to mock external Activity calls in tests or in mock mode.
     */
    @Composable
    fun WithActivityResultRegistry(activityResultRegistry: ActivityResultRegistry, content: @Composable () -> Unit) {
        val activityResultRegistryOwner = object : ActivityResultRegistryOwner {
            override val activityResultRegistry = activityResultRegistry
        }
        CompositionLocalProvider(LocalActivityResultRegistryOwner provides activityResultRegistryOwner) { content() }
    }

}