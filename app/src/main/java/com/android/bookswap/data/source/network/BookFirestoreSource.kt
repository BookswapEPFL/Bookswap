package com.android.bookswap.data.source.network

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

// A class that implements the BooksRepository interface using Firebase Firestore as the data source
class BooksFirestoreRepository(private val db: FirebaseFirestore) : BooksRepository {

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
  override fun getNewUid(): UUID {
    return UUID.randomUUID()
  }
  // Fetches the list of books from the Firestore collection
  // If the task is successful, maps the Firestore documents to DataBook objects
  // Calls OnSuccess with the list of books, or onFailure if the task fails
  override fun getBook(OnSucess: (List<DataBook>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionBooks).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataBook objects or returns an empty list
        val books = task.result?.mapNotNull { document -> documentToBooks(document) } ?: emptyList()
        OnSucess(books)
      } else {
        task.exception?.let { e -> onFailure(e) }
      }
    }
  }
  // Adds a new book to the Firestore collection
  // Calls OnSuccess if the operation is successful, otherwise onFailure with the exception
  override fun addBook(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(dataBook),
        OnSucess,
        onFailure)
  }
  // Updates an existing book in Firestore by replacing the document with the same title
  // Uses performFirestoreOperation to handle success and failure
  override fun updateBook(
      dataBook: DataBook,
      OnSucess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(dataBook),
        OnSucess,
        onFailure)
  }
  // Deletes a book from Firestore by its title
  // Uses performFirestoreOperation to handle success and failure
  override fun deleteBooks(
      id: String,
      dataBook: DataBook,
      OnSucess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).delete(),
        OnSucess,
        onFailure)
  }
  // Maps a Firestore document to a DataBook object
  // If any required field is missing, returns null to avoid incomplete objects
  fun documentToBooks(document: DocumentSnapshot): DataBook? {
    return try {
      val mostSignificantBits = document.getLong("uuid.mostSignificantBits") ?: return null
      val leastSignificantBits = document.getLong("uuid.leastSignificantBits") ?: return null
      val title = document.getString("title") ?: return null
      val author = document.getString("author")
      val description = document.getString("description")
      val rating = document.getLong("rating")
      val photo = document.getString("photo")
      val isbn = document.getString("isbn")
      val languageBook = BookLanguages.valueOf(document.getString("language") ?: return null)
      val genres = document.get("genres") as? List<String> ?: emptyList()
      val bookGenres =
          genres.mapNotNull { genre ->
            try {
              BookGenres.valueOf(genre)
            } catch (e: IllegalArgumentException) {
              null
            }
          }
      DataBook(
          UUID(mostSignificantBits, leastSignificantBits),
          title,
          author,
          description,
          rating?.toInt(),
          photo,
          languageBook,
          isbn,
          bookGenres)
    } catch (e: Exception) {
      null // Return null in case of any exception during the conversion
    }
  }
  // Helper function to perform Firestore operations (add, update, delete)
  // Executes the provided Firestore task and triggers success or failure callbacks
  private fun performFirestoreOperation(
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
