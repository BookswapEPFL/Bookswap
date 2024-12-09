package com.android.bookswap.data.source.network

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PhotoFirebaseStorageSourceTest {
  @MockK private lateinit var mockFirebaseStorage: FirebaseStorage
  @MockK private lateinit var mockStorageReference: StorageReference

  private lateinit var photoStorageSource: PhotoFirebaseStorageSource
  private val photoId = "etranger-test"
  private val photoUrl =
      "https://firebasestorage.googleapis.com/v0/b/app.appspot.com/o/images%2F$photoId.jpg?alt=media&token=someToken"

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    // Initialize Firebase if necessary
    FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())

    // Set up Firebase storage mocks
    every { mockFirebaseStorage.reference } returns mockStorageReference
    photoStorageSource = PhotoFirebaseStorageSource(mockFirebaseStorage)
  }

  @Test
  fun `addPhotoToStorage uploads photo and returns URL on success`() {
    // Load a real bitmap from resources
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.etranger_test)

    val mockDownloadUrl = "https://mockurl.com/test_photo.jpg" // url we expect to receive
    val callback = mockk<(Result<String>) -> Unit>(relaxed = true)

    // Mock storage reference behavior
    val photoStorageReference = mockk<StorageReference>()
    every { mockStorageReference.child("images/$photoId.jpg") } returns photoStorageReference

    // Mock successful upload task with both listeners
    val mockUploadTask = mockk<UploadTask>()
    every { photoStorageReference.putBytes(any()) } returns mockUploadTask
    val successListenerSlot = slot<OnSuccessListener<UploadTask.TaskSnapshot>>()
    val failureListenerSlot = slot<OnFailureListener>()

    every { mockUploadTask.addOnSuccessListener(capture(successListenerSlot)) } answers
        {
          successListenerSlot.captured.onSuccess(mockk())
          mockUploadTask
        }
    every { mockUploadTask.addOnFailureListener(capture(failureListenerSlot)) } answers
        {
          mockUploadTask
        }

    // Mock successful download URL retrieval
    every { photoStorageReference.downloadUrl } returns Tasks.forResult(Uri.parse(mockDownloadUrl))

    // Add photo to storage
    photoStorageSource.addPhotoToStorage(photoId, testBitmap, callback)

    // Force Robolectric to process any pending tasks on the main thread
    shadowOf(Looper.getMainLooper()).idle()

    verify { callback(Result.success(mockDownloadUrl)) }
  }

  @Test
  fun `addPhotoToStorage returns failure on upload error`() {
    // Load a real bitmap from resources
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.etranger_test)

    val callback = mockk<(Result<String>) -> Unit>(relaxed = true)
    val exception = Exception("Upload failed")

    // Mock storage reference behavior
    val photoStorageReference = mockk<StorageReference>()
    every { mockStorageReference.child("images/$photoId.jpg") } returns photoStorageReference

    // Mock upload failure task with both listeners
    val mockUploadTask = mockk<UploadTask>()
    every { photoStorageReference.putBytes(any()) } returns mockUploadTask
    val successListenerSlot = slot<OnSuccessListener<UploadTask.TaskSnapshot>>()
    val failureListenerSlot = slot<OnFailureListener>()

    every { mockUploadTask.addOnSuccessListener(capture(successListenerSlot)) } answers
        {
          mockUploadTask
        }
    every { mockUploadTask.addOnFailureListener(capture(failureListenerSlot)) } answers
        {
          failureListenerSlot.captured.onFailure(exception)
          mockUploadTask
        }

    // Add photo to storage
    photoStorageSource.addPhotoToStorage(photoId, testBitmap, callback)

    verify { callback(Result.failure(exception)) }
  }

  @Test
  fun `init calls callback with success`() {
    val callback = mockk<(Result<Unit>) -> Unit>(relaxed = true)

    photoStorageSource.init(callback)

    verify { callback(Result.success(Unit)) }
  }

  @Test
  fun `deletePhotoFromStorage deletes photo and calls callback with success`() {
    val callback = mockk<(Result<Unit>) -> Unit>(relaxed = true)

    // Mock the storage reference behavior
    val photoStorageReference = mockk<StorageReference>()
    every { mockStorageReference.child("images/$photoId.jpg") } returns photoStorageReference

    // Mock successful delete behavior
    every { photoStorageReference.delete() } returns Tasks.forResult(null)

    // Call the method
    photoStorageSource.deletePhotoFromStorage(photoId, callback)

    // Process pending tasks
    shadowOf(Looper.getMainLooper()).idle()

    // Verify the callback is called with success
    verify { callback(Result.success(Unit)) }
  }

  @Test
  fun `deletePhotoFromStorage calls callback with failure when delete fails`() {
    val callback = mockk<(Result<Unit>) -> Unit>(relaxed = true)
    val exception = Exception("Delete failed")

    // Mock the storage reference behavior
    val photoStorageReference = mockk<StorageReference>()
    every { mockStorageReference.child("images/$photoId.jpg") } returns photoStorageReference

    // Mock delete failure
    every { photoStorageReference.delete() } returns Tasks.forException(exception)

    // Call the method
    photoStorageSource.deletePhotoFromStorage(photoId, callback)

    // Process pending tasks
    shadowOf(Looper.getMainLooper()).idle()

    // Verify the callback is called with failure
    verify { callback(Result.failure(exception)) }
  }

  @Test
  fun `deletePhotoFromStorageWithUrl deletes photo and calls callback with success`() {
    val callback = mockk<(Result<Unit>) -> Unit>(relaxed = true)

    // Mock the storage reference behavior
    val photoStorageReference = mockk<StorageReference>()
    every { mockStorageReference.child("images/$photoId.jpg") } returns photoStorageReference

    // Mock successful delete behavior
    every { photoStorageReference.delete() } returns Tasks.forResult(null)

    // Call the method
    photoStorageSource.deletePhotoFromStorageWithUrl(photoUrl, callback)

    // Process pending tasks
    shadowOf(Looper.getMainLooper()).idle()

    // Verify the callback is called with success
    verify { callback(Result.success(Unit)) }
  }

  @Test
  fun `deletePhotoFromStorageWithUrl calls callback with failure when delete fails`() {
    val callback = mockk<(Result<Unit>) -> Unit>(relaxed = true)
    val exception = Exception("Delete failed")

    // Mock the storage reference behavior
    val photoStorageReference = mockk<StorageReference>()
    every { mockStorageReference.child("images/$photoId.jpg") } returns photoStorageReference

    // Mock delete failure
    every { photoStorageReference.delete() } returns Tasks.forException(exception)

    // Call the method
    photoStorageSource.deletePhotoFromStorageWithUrl(photoUrl, callback)

    // Process pending tasks
    shadowOf(Looper.getMainLooper()).idle()

    // Verify the callback is called with failure
    verify { callback(Result.failure(exception)) }
  }
}
