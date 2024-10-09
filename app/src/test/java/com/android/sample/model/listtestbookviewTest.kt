import com.android.bookswap.model.Booksrepository
import com.android.bookswap.model.DataBook
import com.android.bookswap.model.Languages
import com.android.bookswap.ui.Addbook.listToBooksView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ListToBooksViewTest {

  // MutableStateFlow to hold the current list of books. It starts with an empty list.
  private val books_ = MutableStateFlow<List<DataBook>>(emptyList())

  // Publicly accessible read-only StateFlow of books, so that the UI can observe changes.
  val books: StateFlow<List<DataBook>> = books_.asStateFlow()

  // MutableStateFlow to hold the currently selected book. Initially, no book is selected (null).
  private val selectbook_ = MutableStateFlow<DataBook?>(null)

  private lateinit var booksRepository: Booksrepository
  private lateinit var listToBooksView: listToBooksView

  // Updated test book to match the DataBook properties
  private val testBook =
      DataBook(
          Title = "Test Book",
          Author = "Test Author",
          Description = "This is a test description",
          Rating = 5,
          photo = "test_photo_url",
          Language = Languages.ENGLISH,
          ISBN = "123-4567891234")

  @Before
  fun setUp() {
    booksRepository = mock(Booksrepository::class.java)
    listToBooksView = listToBooksView(booksRepository)
  }

  @Test
  fun getNewUid() {
    // Mock the response of getNewUid()
    `when`(booksRepository.getNewUid()).thenReturn("new_uid")

    // Verify that the getNewUid method works as expected
    assertThat(listToBooksView.getNewUid(), `is`("new_uid"))
  }
}
