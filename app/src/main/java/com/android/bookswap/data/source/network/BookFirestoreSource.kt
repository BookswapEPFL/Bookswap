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

  // Name of the Firestore collection that stores books
  private val collectionBooks = "Books"

  private val books_ = MutableStateFlow<List<DataBook>>(emptyList())
  val books: StateFlow<List<DataBook>> = books_.asStateFlow()

  // Selected todo, i.e the todo for the detail view
  private val selectedBook_ = MutableStateFlow<DataBook?>(null)
  open val selectedBook: StateFlow<DataBook?> = selectedBook_.asStateFlow()
  // Use this code in editBookScreen and modify the editBookScreen structure if needed when
  // incorporating in the app navigation

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

  override fun getBook(uuid: UUID, OnSucess: (DataBook) -> Unit, onFailure: (Exception) -> Unit) {
    // Log the UUID bits for debugging
    // val (mostSigBits, leastSigBits) = Pair(uuid.mostSignificantBits, uuid.leastSignificantBits)
    Log.d(
        "BooksFirestoreRepository",
        "UUID: $uuid") // Most Significant Bits: $mostSigBits, Least Significant Bits:
    // $leastSigBits")

    db.collection(collectionBooks).document(uuid.toString()).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null && document.exists()) {
          try {
            // Parse the fields and handle genres mapping separately
            val genresList = document.get("genres") as? List<String> ?: emptyList()
            val bookGenres =
                genresList.mapNotNull { genre ->
                  try {
                    BookGenres.valueOf(genre) // Attempt to map each string to BookGenres
                  } catch (e: IllegalArgumentException) {
                    Log.e("BooksFirestoreRepository", "Unknown genre: $genre")
                    null // Skip if genre is not valid in the BookGenres enum
                  }
                }
            var archived = document.getBoolean("archived") ?: false
            var exchange = document.getBoolean("exchange") ?: false

            val userId = UUID.fromString(document.getString("userId"))
            // Create the DataBook object
            val dataBook =
                DataBook(
                    uuid = UUID.fromString(document.getString("uuid")),
                    title = document.getString("title") ?: "",
                    author = document.getString("author"),
                    description = document.getString("description"),
                    rating = document.getLong("rating")?.toInt(),
                    photo = document.getString("photo"),
                    language =
                        document.getString("language")?.let { BookLanguages.valueOf(it) }
                            ?: BookLanguages.ENGLISH, // Default or adjust based on requirements
                    isbn = document.getString("isbn"),
                    genres = bookGenres,
                    userId = userId,
                    archived = archived,
                    exchange = exchange)
            OnSucess(dataBook)
          } catch (e: Exception) {
            Log.e("BooksFirestoreRepository", "Error parsing book document: ${e.message}", e)
            onFailure(e)
          }
        } else {
          Log.e("BooksFirestoreRepository", "Book with UUID $uuid not found in Firestore.")
          onFailure(IllegalArgumentException("Book not found"))
        }
      } else {
        task.exception?.let { e ->
          Log.e("BooksFirestoreRepository", "Error retrieving book: ${e.message}", e)
          onFailure(e)
        }
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
    val bookMap =
        mapOf(
            "uuid" to dataBook.uuid.toString(),
            "title" to dataBook.title,
            "author" to dataBook.author,
            "description" to dataBook.description,
            "rating" to dataBook.rating,
            "photo" to dataBook.photo,
            "language" to dataBook.language.toString(),
            "isbn" to dataBook.isbn,
            "genres" to dataBook.genres.map { it.toString() },
            "userId" to dataBook.userId.toString())

    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(bookMap)) { result ->
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
    val bookDocument = bookToDocument(dataBook) // Transform to Map<String, Any?>
    performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(bookDocument),
        callback)
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
      val uuid = UUID.fromString(document.getString("uuid") ?: return null)
      val title = document.getString("title") ?: return null
      val author = document.getString("author")
      val description = document.getString("description")
      val rating = document.getLong("rating")?.toInt()
      val photo = document.getString("photo")
      val language = BookLanguages.valueOf(document.getString("language") ?: return null)
      val isbn = document.getString("isbn")
      val genres =
          (document.get("genres", List::class.java) as? List<String>)?.mapNotNull {
            try {
              BookGenres.valueOf(it)
            } catch (e: IllegalArgumentException) {
              null
            }
          } ?: emptyList()
      val userId = UUID.fromString(document.getString("userId") ?: return null)
      val archived = document.getBoolean("archived") ?: false
      val exchange = document.getBoolean("exchange") ?: false

      DataBook(
          uuid = uuid,
          title = title,
          author = author,
          description = description,
          rating = rating,
          photo = photo,
          language = language,
          isbn = isbn,
          genres = genres,
          userId = userId,
          archived = archived,
          exchange = exchange)
    } catch (e: Exception) {
      null
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
  /**
   * Maps a DataBook object to a Firebase document-like Map
   *
   * @param dataBook The object to convert into a Map
   * @return Map<String,Any?> A Mapping of each of the DataBook object fields to it's value,
   *   properly formatted for storing
   */
  fun bookToDocument(dataBook: DataBook): Map<String, Any?> {
    return mapOf(
        "uuid" to dataBook.uuid.toString(),
        "title" to dataBook.title,
        "author" to dataBook.author,
        "description" to dataBook.description,
        "rating" to dataBook.rating,
        "photo" to dataBook.photo,
        "language" to dataBook.language.toString(),
        "isbn" to dataBook.isbn,
        "genres" to dataBook.genres.map { it.toString() },
        "userId" to dataBook.userId.toString(),
        "archived" to dataBook.archived,
        "exchange" to dataBook.exchange)
  }
}
