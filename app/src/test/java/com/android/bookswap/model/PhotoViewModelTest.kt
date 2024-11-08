package com.android.bookswap.model

import PhotoRepository
import android.graphics.Bitmap
import com.android.bookswap.data.DataPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import java.util.*
import java.util.concurrent.Executor
@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class PhotoViewModelTest {

    @Mock
    private lateinit var photoRepository: PhotoRepository

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
    fun testGetNewUid() {
        val uuid = UUID.randomUUID()
        Mockito.`when`(photoRepository.getNewUid()).thenReturn(uuid)

        val result = photoViewModel.getNewUid()

        Assert.assertEquals(uuid, result)
    }


    @Test
    fun testGetPhoto() = runBlockingTest {
        val uid = UUID.randomUUID()
        val dataPhoto = DataPhoto(uid, "url", System.currentTimeMillis(), "base64String")
        val onSuccess: (DataPhoto) -> Unit = mock()
        val onFailure: (Exception) -> Unit = mock()

        photoViewModel.getPhoto(uid, onSuccess, onFailure)

        Mockito.verify(photoRepository).getPhoto(uid, onSuccess, onFailure)
    }

    @Test
    fun testBitmapToBase64() {
        val bitmap: Bitmap = mock()
        val base64String = "base64String"
        Mockito.`when`(photoRepository.bitmapToBase64(bitmap)).thenReturn(base64String)

        val result = photoViewModel.bitmapToBase64(bitmap)

        Assert.assertEquals(base64String, result)
    }

    @Test
    fun testBase64ToBitmap() {
        val base64String = "base64String"
        val bitmap: Bitmap = mock()
        Mockito.`when`(photoRepository.base64ToBitmap(base64String)).thenReturn(bitmap)

        val result = photoViewModel.base64ToBitmap(base64String)

        Assert.assertEquals(bitmap, result)
    }

    @Test
    fun testFetchPhoto() = runBlockingTest {
        val uid = UUID.randomUUID()
        val dataPhoto = DataPhoto(uid, "url", System.currentTimeMillis(), "base64String")
        val onSuccess: (DataPhoto) -> Unit = mock()
        val onFailure: (Exception) -> Unit = mock()

        photoViewModel.fetchPhoto(uid, onSuccess, onFailure)

        Mockito.verify(photoRepository).getPhoto(uid, onSuccess, onFailure)
    }
}