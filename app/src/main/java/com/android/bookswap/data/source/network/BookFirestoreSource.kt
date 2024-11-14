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
 * @param db The Firestore instance to use for database operations.
 */
class BooksFirestoreRepository(private val db: FirebaseFirestore) : BooksRepository {

  /** The name of the Firestore collection for books. */
  private val collectionBooks = "Books"
  /**
   * A StateFlow that holds the list of books. This is a read-only view of the internal
   * MutableStateFlow.
   */
  private val books_ = MutableStateFlow<List<DataBook>>(emptyList())
  val books: StateFlow<List<DataBook>> = books_.asStateFlow()

  // Selected todo, i.e the todo for the detail view
  private val selectedBook_ = MutableStateFlow<DataBook?>(null)
  open val selectedBook: StateFlow<DataBook?> = selectedBook_.asStateFlow()
  // Use this code in editBookScreen and modify the editBookScreen structure if needed when
  // incorporating in the app navigation
  /**
   * Initializes the Firestore repository by adding an authentication state listener. Calls the
   * provided OnSuccess callback if the user is authenticated.
   *
   * @param OnSucess The callback to invoke when the user is authenticated.
   */
  override fun init(OnSucess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        OnSucess()
      }
    }
  }
  /**
   * Generates a new unique id for a book.
   *
   * @return A new UUID.
   */
  override fun getNewUUID(): UUID {
    return UUID.randomUUID()
  }
  /**
   * Retrieves all books from the Firestore collection. Calls OnSuccess with the list of books if
   * the operation is successful, otherwise onFailure with the exception.
   *
   * @param OnSucess The callback to invoke when the operation is successful.
   * @param onFailure The callback to invoke when the operation fails.
   */
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
  /**
   * Adds a new book to Firestore. Calls OnSuccess if the operation is successful, otherwise
   * onFailure with the exception.
   *
   * @param dataBook The book to add.
   * @param OnSucess The callback to invoke when the operation is successful.
   * @param onFailure The callback to invoke when the operation fails.
   */
  override fun addBook(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit) {
    // Check if essential fields are non-null before attempting to save
    if (dataBook.title.isBlank() ||
        dataBook.author.isNullOrBlank() ||
        dataBook.isbn.isNullOrBlank()) {
      val exception = IllegalArgumentException("Missing required book fields.")
      Log.e("BooksFirestoreRepository", "Failed to add book: ${exception.message}")
      onFailure(exception)
      return
    }

    Log.d("BooksFirestoreRepository", "Attempting to add book: ${dataBook.title}")

    // Attempt to add book to Firestore
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(dataBook),
        {
          Log.d("BooksFirestoreRepository", "Book added successfully: ${dataBook.title}")
          OnSucess()
        },
        { e ->
          Log.e("BooksFirestoreRepository", "Failed to add book: ${e.message}", e)
          onFailure(e)
        })
  }
  /**
   * Updates a book in Firestore. Calls OnSuccess if the operation is successful, otherwise
   * onFailure with the exception.
   *
   * @param dataBook The book to update.
   * @param OnSucess The callback to invoke when the operation is successful.
   * @param onFailure The callback to invoke when the operation fails.
   */
  override fun updateBook(
      dataBook: DataBook,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(dataBook),
        onSuccess,
        onFailure)
  }
  /**
   * Deletes a book from Firestore. Calls OnSuccess if the operation is successful, otherwise
   * onFailure with the exception.
   *
   * @param uuid The UUID of the book to delete.
   * @param dataBook The book to delete.
   * @param OnSucess The callback to invoke when the operation is successful.
   * @param onFailure The callback to invoke when the operation fails.
   */
  override fun deleteBooks(
      uuid: UUID,
      dataBook: DataBook,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).delete(),
        onSuccess,
        onFailure)
  }
  /**
   * Retrieves a DataBook object from a Firestore document.
   *
   * @param document The Firestore document to convert.
   * @return The DataBook object, or null if the document is missing required fields.
   */
  fun documentToBooks(document: DocumentSnapshot): DataBook? {
    return try {
      val mostSignificantBits = document.getLong("uuid.mostSignificantBits") ?: return null
      val leastSignificantBits = document.getLong("uuid.leastSignificantBits") ?: return null
      val title = document.getString("title") ?: return null
      val author = document.getString("author")
      val description = document.getString("description")
      val rating = document.getLong("rating")
      val photo = document.get("photo") as? UUID
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
   * Performs a Firestore operation and calls the appropriate callback based on the result.
   *
   * @param task The Firestore task to perform.
   * @param OnSucess The callback to invoke when the operation is successful.
   * @param onFailure The callback to invoke when the operation fails.
   */
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
