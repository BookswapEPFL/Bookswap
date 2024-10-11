package com.android.bookswap.data.source.network

import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

/** Name of the Firestore collection that stores books */
const val COLLECTION_NAME = "Books"

/** Implement [BooksRepository] interface using Firebase's Firestore as the data source */
class BookFirestoreSource(private val db: FirebaseFirestore) : BooksRepository {

  /**
   * Initializes the repository by adding an auth state listener to Firebase Authentication If the
   * user is authenticated, it triggers the OnSuccess callback
   */
  override fun init(callback: (Result<Unit>) -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        callback(Result.success(Unit))
      }
      callback(Result.failure(Exception("User is not logged in firestore")))
    }
  }
  /** Generates and returns a new unique document ID for a book in Firestore */
  override fun getNewUid(): String {
    return db.collection(COLLECTION_NAME).document().id
  }
  /**
   * Fetches the list of books from the Firestore collection If the task is successful, maps the
   * Firestore documents to DataBook objects Calls OnSuccess with the list of books, or onFailure if
   * the task fails
   */
  override fun getBook(callback: (Result<List<DataBook>>) -> Unit) {
    db.collection(COLLECTION_NAME).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataBook objects or returns an empty list
        val books =
            task.result?.mapNotNull { document ->
              val result = documentToBooks(document)
              result.exceptionOrNull()?.printStackTrace() // Print exception if there is one
              result.getOrNull()
            } ?: emptyList()
        callback(Result.success(books))
      } else {
        callback(Result.failure(task.exception!!))
      }
    }
  }

  /** Adds a new book to the Firestore collection */
  override fun addBooks(dataBook: DataBook, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(dataBook.title).set(dataBook),
        callback,
    )
  }
  /**
   * Updates an existing book in Firestore by replacing the document with the same title Uses
   * performFirestoreOperation to handle success and failure
   */
  override fun updateBook(dataBook: DataBook, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(dataBook.title).set(dataBook), callback)
  }

  /**
   * Deletes a book from Firestore by its title Uses performFirestoreOperation to handle success and
   * failure
   */
  override fun deleteBook(uuid: UUID, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(uuid.toString()).delete(), callback)
  }
  /**
   * Maps a Firestore document to a DataBook object If any required field is missing, returns null
   * to avoid incomplete objects
   *
   * @return DataBook on success, otherwise error
   */
  fun documentToBooks(document: DocumentSnapshot): Result<DataBook> {

    return try {
      val title = document.getString("Title")!!
      val author = document.getString("Author")!!
      val description = document.getString("Description")!!
      val rating = document.getString("Rating")!!
      val photo = document.getString("Photo")!!
      val isbn = document.getString("ISBN")!!
      val languageBook = BookLanguages.valueOf(document.getString("Language")!!)
      Result.success(
          DataBook(
              UUID.randomUUID(),
              title,
              author,
              description,
              rating.toInt(),
              photo,
              languageBook,
              isbn))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
  /**
   * Helper function to perform Firestore operations (add, update, delete) Executes the provided
   * Firestore task and triggers success or failure callbacks
   *
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
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
