package com.android.bookswap.model

import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

// A class that implements the Booksrepository interface using Firebase Firestore as the data source
class BooksFirestorerRepository(private val db: FirebaseFirestore) : Booksrepository {

  // Name of the Firestore collection that stores books
  private val collectionBooks = "Books"

  // Initializes the repository by adding an auth state listener to Firebase Authentication
  // If the user is authenticated, it triggers the OnSuccess callback
  override fun init(OnSucess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        OnSucess()
      }
    }
  }
  // Generates and returns a new unique document ID for a book in Firestore
  override fun getNewUid(): String {
    return db.collection(collectionBooks).document().id
  }
  // Fetches the list of books from the Firestore collection
  // If the task is successful, maps the Firestore documents to DataBook objects
  // Calls OnSuccess with the list of books, or onFailure if the task fails
  override fun getbook(OnSucess: (List<DataBook>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionBooks).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataBook objects or returns an empty list
        val books = task.result?.mapNotNull { document -> documenttoBooks(document) } ?: emptyList()
        OnSucess(books)
      } else {
        task.exception?.let { e -> onFailure(e) }
      }
    }
  }
  // Adds a new book to the Firestore collection
  // Calls OnSuccess if the operation is successful, otherwise onFailure with the exception
  override fun addBooks(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.title).set(dataBook), OnSucess, onFailure)
  }
  // Updates an existing book in Firestore by replacing the document with the same title
  // Uses performFirestoreOperation to handle success and failure
  override fun updatebook(
      dataBook: DataBook,
      OnSucess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.title).set(dataBook), OnSucess, onFailure)
  }
  // Deletes a book from Firestore by its title
  // Uses performFirestoreOperation to handle success and failure
  override fun deletebooks(
      id: String,
      dataBook: DataBook,
      OnSucess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.isbn).delete(), OnSucess, onFailure)
  }
  // Maps a Firestore document to a DataBook object
  // If any required field is missing, returns null to avoid incomplete objects
  fun documenttoBooks(document: DocumentSnapshot): DataBook? {
    return try {
      val title = document.getString("Title") ?: return null
      val author = document.getString("Author") ?: return null
      val description = document.getString("Description") ?: return null
      val rating = document.getString("Rating") ?: return null
      val photo = document.getString("Photo") ?: return null
      val isbn = document.getString("ISBN") ?: return null
      val languageBook = BookLanguages.valueOf(document.getString("Language") ?: return null)
      DataBook(title, author, description, rating.toInt(), photo, languageBook, isbn)
    } catch (e: Exception) {
      null // Return null in case of any exception during the conversion
    }
  }
  // Helper function to perform Firestore operations (add, update, delete)
  // Executes the provided Firestore task and triggers success or failure callbacks
  fun performFirestoreOperation(
      task: Task<Void>,
      OnSucess: () -> Unit,
      OnFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        OnSucess()
      } else {
        result.exception?.let { e -> OnFailure(e) }
      }
    }
  }
}
