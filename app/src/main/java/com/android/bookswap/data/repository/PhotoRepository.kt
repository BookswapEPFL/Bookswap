import android.graphics.Bitmap
import com.android.bookswap.data.DataPhoto
import java.util.UUID

interface PhotoRepository {
  /** Generates a new unique id for a message */
  fun getNewUUID(): UUID

  /**
   * Initialize the repository
   *
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun init(callback: (Result<Unit>) -> Unit)

  /**
   * Fetches a specific photo from Firestore by UUID
   *
   * @param uuid the UUID of the photo to fetch
   * @param callback callback function that receives Result.success(DataPhoto) when operation
   *   succeed of Result.failure(exception) if error
   */
  fun getPhoto(uuid: UUID, callback: (Result<DataPhoto>) -> Unit)

  /**
   * Converts a Bitmap to a Base64 encoded string.
   *
   * @param bitmap the Bitmap to convert.
   * @return the Base64 encoded string.
   */
  fun bitmapToBase64(bitmap: Bitmap): String

  /**
   * Converts a Base64 encoded string to a Bitmap.
   *
   * @param base64 the Base64 encoded string to convert.
   * @return the Bitmap.
   */
  fun base64ToBitmap(base64: String): Bitmap

  /**
   * Uploads a photo to Firestore.
   *
   * @param dataPhoto the photo data to upload.
   * @param callback callback function that receives Unit if success or an an exception if error.
   */
  fun addPhoto(dataPhoto: DataPhoto, callback: (Result<Unit>) -> Unit)
  /**
   * Converts a URL to a Bitmap.
   *
   * @param urlString The URL of the image to convert.
   * @return The Bitmap representation of the image, or null if conversion fails.
   */
  fun urlToBitmap(urlString: String): Bitmap?
}
