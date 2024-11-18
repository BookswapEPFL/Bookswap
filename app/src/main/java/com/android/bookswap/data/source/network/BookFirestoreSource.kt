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

// A class that implements the BooksRepository interface using Firebase Firestore as the data source
class BooksFirestoreRepository(private val db: FirebaseFirestore) : BooksRepository {

  // Name of the Firestore collection that stores books
  private val collectionBooks = "Books"

  private val books_ = MutableStateFlow<List<DataBook>>(emptyList())
  val books: StateFlow<List<DataBook>> = books_.asStateFlow()

  // Selected todo, i.e the todo for the detail view
  private val selectedBook_ = MutableStateFlow<DataBook?>(null)
  open val selectedBook: StateFlow<DataBook?> = selectedBook_.asStateFlow()
  // Use this code in editBookScreen and modify the editBookScreen structure if needed when
  // incorporating in the app navigation

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
  override fun getNewUUID(): UUID {
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

  override fun getBook(uuid: UUID, OnSucess: (DataBook) -> Unit, onFailure: (Exception) -> Unit) {
      // Log the UUID bits for debugging
      //val (mostSigBits, leastSigBits) = Pair(uuid.mostSignificantBits, uuid.leastSignificantBits)
      Log.d("BooksFirestoreRepository", "UUID: $uuid") //Most Significant Bits: $mostSigBits, Least Significant Bits: $leastSigBits")

      db.collection(collectionBooks).document(uuid.toString()).get().addOnCompleteListener { task ->
          if (task.isSuccessful) {
              val document = task.result
              if (document != null && document.exists()) {
                  try {
                      // Parse the fields and handle genres mapping separately
                      val genresList = document.get("genres") as? List<String> ?: emptyList()
                      val bookGenres = genresList.mapNotNull { genre ->
                          try {
                              BookGenres.valueOf(genre) // Attempt to map each string to BookGenres
                          } catch (e: IllegalArgumentException) {
                              Log.w("BooksFirestoreRepository", "Unknown genre: $genre")
                              null // Skip if genre is not valid in the BookGenres enum
                          }
                      }

                      // Create the DataBook object
                      val dataBook = DataBook(
                          uuid = UUID.fromString(document.getString("uuid")),
                          title = document.getString("title") ?: "",
                          author = document.getString("author"),
                          description = document.getString("description"),
                          rating = document.getLong("rating")?.toInt(),
                          photo = document.getString("photo"),
                          language = document.getString("language")?.let { BookLanguages.valueOf(it) }
                              ?: BookLanguages.ENGLISH, // Default or adjust based on requirements
                          isbn = document.getString("isbn"),
                          genres = bookGenres,
                          userId = UUID.fromString(document.getString("userId"))
                      )
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

  // Adds a new book to the Firestore collection
  // New verification and Log have been added to help debuging
  // Calls OnSuccess if the operation is successful, otherwise onFailure with the exception
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
    val bookMap = mapOf(
        "uuid" to dataBook.uuid.toString(),
        "title" to dataBook.title,
        "author" to dataBook.author,
        "description" to dataBook.description,
        "rating" to dataBook.rating,
        "photo" to dataBook.photo,
        "language" to dataBook.language.toString(),
        "isbn" to dataBook.isbn,
        "genres" to dataBook.genres.map { it.toString() },
        "userId" to dataBook.userId.toString()
    )


      performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(bookMap),
        {
          Log.d("BooksFirestoreRepository", "Book added successfully: ${dataBook.title}")
          OnSucess()
        },
        { e ->
          Log.e("BooksFirestoreRepository", "Failed to add book: ${e.message}", e)
          onFailure(e)
        })
  }
  // Updates an existing book in Firestore by replacing the document with the same uuid
  // Uses performFirestoreOperation to handle success and failure
  override fun updateBook(
      dataBook: DataBook,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
      val bookMap = mapOf(
          "uuid" to dataBook.uuid.toString(),
          "title" to dataBook.title,
          "author" to dataBook.author,
          "description" to dataBook.description,
          "rating" to dataBook.rating,
          "photo" to dataBook.photo,
          "language" to dataBook.language.toString(),
          "isbn" to dataBook.isbn,
          "genres" to dataBook.genres.map { it.toString() },
          "userId" to dataBook.userId.toString()
      )

      performFirestoreOperation(
        db.collection(collectionBooks).document(dataBook.uuid.toString()).set(bookMap),
        onSuccess,
        onFailure)
  }
  // Deletes a book from Firestore by its title
  // Uses performFirestoreOperation to handle success and failure
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
  // Maps a Firestore document to a DataBook object
  // If any required field is missing, returns null to avoid incomplete objects
  fun documentToBooks(document: DocumentSnapshot): DataBook? {
    return try {
      //val mostSignificantBits = document.getString("uuid.mostSignificantBits") ?: return null
      //val leastSignificantBits = document.getString("uuid.leastSignificantBits") ?: return null
      val bookuuid = document.getString("uuid") ?: return null
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
      val userid = UUID.fromString(document.getString("userid")) ?: return null
      DataBook(
          UUID.fromString(bookuuid),
          title,
          author,
          description,
          rating?.toInt(),
          photo,
          languageBook,
          isbn,
          bookGenres,
          userid)
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
