package com.android.bookswap.data.source

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

val REQUEST_IMAGE_CAPTURE = 1
/*
fun openCamera(activity: Activity) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }
}

// Override onActivityResult to get the image
fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
        val imageBitmap = data?.extras?.get("data") as Bitmap
        val imageUri = getImageUriFromBitmap(imageBitmap)

        uploadPhotoToFirebaseStorage(imageUri, onSuccess = { downloadUrl ->
            savePhotoUrlToFirestore(downloadUrl,
                onSuccess = { println("Photo saved successfully!") },
                onFailure = { error -> println("Firestore Error: ${error.message}") }
            )
        },
            onFailure = { error -> println("Storage Error: ${error.message}") }
        )
    }
}
fun getImageUriFromBitmap(context: Bitmap, bitmap: Bitmap): Uri {
    val cacheDir = File(context.cacheDir, "images")
    val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
    val outputStream = FileOutputStream(tempFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return Uri.fromFile(tempFile)
}
fun uploadPhotoToFirebaseStorage(photoUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val fileName = "photos/${UUID.randomUUID()}.jpg"
    val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

    storageRef.putFile(photoUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}
fun savePhotoUrlToFirestore(photoUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val photoData = mapOf("photoUrl" to photoUrl)

    firestore.collection("photos")
        .add(photoData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}*/