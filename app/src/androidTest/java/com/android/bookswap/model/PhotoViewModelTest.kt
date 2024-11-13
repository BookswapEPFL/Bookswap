package com.android.bookswap.model

import PhotoRepository
import com.android.bookswap.data.DataPhoto
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
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
  fun testGetNewUid() {
    val uuid = UUID.randomUUID()
    Mockito.`when`(photoRepository.getNewUUID()).thenReturn(uuid)

    val result = photoViewModel.getNewUid()

    Assert.assertEquals(uuid, result)
  }

  @Test
  fun testInit() = runBlockingTest {
    val callback: (Result<Unit>) -> Unit = mock()

    photoViewModel.init(callback)

    Mockito.verify(photoRepository).init(callback)
  }

  @Test
  fun testGetPhoto() = runBlockingTest {
    val uid = UUID.randomUUID()
    val callback: (Result<DataPhoto>) -> Unit = mock()

    photoViewModel.getPhoto(uid, callback)

    Mockito.verify(photoRepository).getPhoto(uid, callback)
  }
}
