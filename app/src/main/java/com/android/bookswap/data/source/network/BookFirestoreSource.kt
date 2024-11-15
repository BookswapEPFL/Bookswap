package com.android.bookswap.data.source.network

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Firestore implementation of the BooksRepository.
 *
 * @param db the Firestore database instance
 */
class BooksFirestoreSource(private val db: FirebaseFirestore) : BooksRepository {

  private val collectionBooks = "Books"

  private val books_ = MutableStateFlow<List<DataBook>>(emptyList())
  val books: StateFlow<List<DataBook>> = books_.asStateFlow()

  // Selected todo, i.e the todo for the detail view
  private val selectedBook_ = MutableStateFlow<DataBook?>(null)
  val selectedBook: StateFlow<DataBook?> = selectedBook_.asStateFlow()

  /**
   * Initializes the Firestore source by adding an authentication state listener. Calls the
   * onSuccess callback if the user is authenticated.
   *
   * @param onSuccess Callback to be invoked when the user is authenticated.
   */
  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }
  /**
   * Generates a new UUID.
   *
   * @return a randomly generated UUID
   */
  override fun getNewUUID(): UUID {
    return UUID.randomUUID()
  }
  /**
   * Fetches the list of books from the Firestore database.
   *
   * @param callback Callback to be invoked with the result of the operation. The result is a list
   *   of DataBook objects on success, or an exception on failure.
   */
  override fun getBook(callback: (Result<List<DataBook>>) -> Unit) {
    db.collection(collectionBooks).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataBook objects or returns an empty list
        val books = task.result?.mapNotNull { document -> documentToBooks(document) } ?: emptyList()
        callback(Result.success(books))
      } else {
        task.exception?.let { e -> callback(Result.failure(e)) }
      }
    }
  }
  /**
   * Adds a new book to the Firestore database.
   *
   * @param dataBook The DataBook object containing the book details.
   * @param callback Callback to be invoked with the result of the operation. The result is Unit on
   *   success, or an exception on failure.
   */
  override fun addBook(dataBook: DataBook, callback: (Result<Unit>) -> Unit) {
    // Check if essential fields are non-null before attempting to save
    if (dataBook.title.isBlank() ||
        dataBook.author.isNullOrBlank() ||
        dataBook.isbn.isNullOrBlank()) {
      val exception = IllegalArgumentException("Missing required book fields.")
      Log.e("BooksFirestoreRepository", "Failed to add book: ${exception.message}")
      callback(Result.failure(exception))
      return
    }

    Log.d("BooksFirestoreRepository", "Attempting to add book: ${dataBook.title}")

    // Attempt to add book to Firestore
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(dataBook)) { result ->
          if (result.isSuccess)
              Log.d("BooksFirestoreRepository", "Book added successfully: ${dataBook.title}")
          else {
            val error = result.exceptionOrNull()!!
            Log.e("BooksFirestoreRepository", "Failed to add book: ${error.message}", error)
          }
          callback(result)
        }
  }
  /**
   * Updates an existing book in the Firestore database.
   *
   * @param dataBook The DataBook object containing the updated book details.
   * @param callback Callback to be invoked with the result of the operation. The result is Unit on
   *   success, or an exception on failure.
   */
  override fun updateBook(dataBook: DataBook, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(dataBook), callback)
  }
  /**
   * Deletes a book from the Firestore database.
   *
   * @param uuid The UUID of the book to be deleted.
   * @param dataBook The DataBook object containing the book details.
   * @param callback Callback to be invoked with the result of the operation. The result is Unit on
   *   success, or an exception on failure.
   */
  override fun deleteBooks(uuid: UUID, dataBook: DataBook, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).delete(), callback)
  }
  /**
   * Maps a Firestore document to a DataBook object. If any required field is missing, returns null
   * to avoid incomplete objects.
   *
   * @param document The Firestore document to be converted.
   * @return A DataBook object if all required fields are present, null otherwise.
   */
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
  /**
   * Helper function to perform Firestore operations (add, update, delete) Executes the provided
   * Firestore task and triggers success or failure callbacks
   */
  private fun performFirestoreOperation(task: Task<Void>, callback: (Result<Unit>) -> Unit) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        callback(Result.success(Unit))
      } else {
        result.exception?.let { e -> callback(Result.failure(e)) }
      }
    }
  }
}
