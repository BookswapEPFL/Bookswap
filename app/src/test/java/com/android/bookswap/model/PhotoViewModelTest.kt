package com.android.bookswap.model

import android.graphics.Bitmap
import com.android.bookswap.data.DataPhoto
import com.android.bookswap.data.repository.PhotoRepository
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class PhotoViewModelTest {

  @Mock private lateinit var photoRepository: PhotoRepository

  private lateinit var photoViewModel: PhotoViewModel

  private val testDispatcher = TestCoroutineDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    photoViewModel = PhotoViewModel(photoRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun testGetPhoto() = runBlockingTest {
    val uid = UUID.randomUUID()
    val dataPhoto = DataPhoto(uid, "url", System.currentTimeMillis(), "base64String")
    val result = Result.success(dataPhoto)

    Mockito.`when`(photoRepository.getPhoto(uid, any())).thenAnswer {
      val callback = it.getArgument<(Result<DataPhoto>) -> Unit>(1)
      callback(result)
    }

    val onSuccess: (Result<DataPhoto>) -> Unit = mock()
    photoViewModel.getPhoto(uid, onSuccess)

    Mockito.verify(photoRepository).getPhoto(uid, onSuccess)
  }

  @Test
  fun testBitmapToBase64() {
    val bitmap: Bitmap = mock()
    val base64String = "base64String"
    Mockito.`when`(photoRepository.bitmapToBase64(bitmap)).thenReturn(base64String)

    val result = photoViewModel.bitmapToBase64(bitmap)

    Assert.assertEquals(base64String, result)
  }
}
