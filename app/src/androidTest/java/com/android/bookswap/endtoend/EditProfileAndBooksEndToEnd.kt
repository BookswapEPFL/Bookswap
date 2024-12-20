package com.android.bookswap.endtoend

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.android.bookswap.MainActivity
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.resources.C
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditProfileAndBooksEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

  private lateinit var mockBookRepository: BooksRepository
  private lateinit var mockUserRepository: UsersRepository
  private lateinit var mockPhotoStorage: PhotoFirebaseStorageSource
  private lateinit var mockMessageStorage: OfflineMessageStorage
  private lateinit var mockContext: Context
  private lateinit var userVM: UserViewModel

  private var currentUserUUID = UUID.randomUUID()
  private var booksUUIDs =
      listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())

  private val testBook =
      DataBook(
          booksUUIDs[0],
          "Historia de España",
          "Jose Ignacio Pastor Iglesias",
          """
    Recuento de la historia de España desde los primeros pobladores hasta la actualidad. 
    Este libro abarca desde las antiguas civilizaciones ibéricas y celtas hasta la llegada 
    de los romanos y la posterior invasión de los visigodos. Se examinan en detalle los 
    ocho siglos de ocupación musulmana, con especial atención a figuras como Abderramán III 
    y la esplendorosa época de Al-Ándalus. 
    
    También se analizan las cruzadas cristianas para recuperar el territorio, conocidas como 
    la Reconquista, que culminaron en 1492 con la toma de Granada por los Reyes Católicos. 
    Este evento marcó el inicio de una nueva era para España, incluyendo el descubrimiento 
    de América por Cristóbal Colón y el auge del Imperio Español durante los siglos XVI y XVII.
    
    El libro continúa con las guerras napoleónicas, el colapso del Antiguo Régimen y la 
    instauración de la Primera República. Se detallan los turbulentos años de la Guerra Civil 
    Española (1936-1939) y el posterior régimen franquista. Finalmente, se explora la 
    Transición Democrática y el ascenso de España como una nación moderna dentro de la 
    Unión Europea.
    
    Cada capítulo incluye ilustraciones, mapas y cronologías detalladas para facilitar 
    el estudio de los eventos históricos más importantes. Es una lectura obligatoria 
    para cualquier persona interesada en comprender la rica y compleja historia de España, 
    llena de conquistas, descubrimientos, conflictos y reformas.
    """
              .trimIndent(),
          9,
          null,
          BookLanguages.SPANISH,
          "978-84-09025-23-5",
          listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
          currentUserUUID,
          archived = false,
          exchange = false)

  private val testBooks =
      listOf(
          testBook,
          DataBook(
              booksUUIDs[1],
              "Generic Book 1",
              "Author 1",
              "Description of Generic Book 1",
              5,
              null,
              BookLanguages.ENGLISH,
              "111-11-1111111-1",
              listOf(BookGenres.FICTION),
              currentUserUUID,
              archived = false,
              exchange = false),
          DataBook(
              booksUUIDs[2],
              "Generic Book 2",
              "Author 2",
              "Description of Generic Book 2",
              7,
              null,
              BookLanguages.GERMAN,
              "222-22-2222222-2",
              listOf(BookGenres.THRILLER),
              currentUserUUID,
              archived = false,
              exchange = false),
          DataBook(
              booksUUIDs[3],
              "Generic Book 3",
              "Author 3",
              "Description of Generic Book 3",
              8,
              null,
              BookLanguages.FRENCH,
              "333-33-3333333-3",
              listOf(BookGenres.ROMANCE),
              currentUserUUID,
              archived = false,
              exchange = false))

  @Before
  fun setUp() {
    mockPhotoStorage = mockk()
    mockMessageStorage = mockk()
    mockContext = mockk()

    mockBookRepository = mockk()
    every { mockBookRepository.getBook(any(), any(), any()) } answers
        {
          val bookId = it.invocation.args[0] as UUID
          val successCallback = it.invocation.args[1] as (DataBook) -> Unit
          val failureCallback = it.invocation.args[2] as (Exception) -> Unit

          // Find the matching book in `testBooks`
          val book = testBooks.find { it.uuid == bookId }
          if (book != null) {
            successCallback(book)
          } else {
            failureCallback(Exception("Book not found for ID: $bookId"))
          }
        }
    every { mockBookRepository.getBook(any()) } answers
        {
          val callback = it.invocation.args[0] as (Result<List<DataBook>>) -> Unit
          callback(Result.success(testBooks))
        }

    mockUserRepository = mockk()
    every { mockUserRepository.getUsers(any()) } just runs
    every { mockUserRepository.getUser(any<UUID>(), any()) } just runs

    mockkConstructor(ContactViewModel::class)
    every { anyConstructed<ContactViewModel>().updateMessageBoxMap() } just runs

    userVM = MockUserViewModel(currentUserUUID, booksUUIDs)

    composeTestRule.setContent {
      val db: FirebaseFirestore = mockk(relaxed = true)
      val messageRepository = MessageFirestoreSource(db)

      CompositionLocalProvider(LocalAppConfig provides AppConfig(userViewModel = userVM)) {
        val userData = userVM.getUser()
        Log.d("setContent", "userData: $userData")

        MainActivity()
            .BookSwapApp(
                messageRepository,
                mockBookRepository,
                mockUserRepository,
                C.Route.MAP,
                mockPhotoStorage,
                mockMessageStorage,
                context = mockContext,
                userVM = userVM)
      }
    }
  }

  @Test
  fun testReachProfileAndEditProfile() {
    // Click on the profile icon in the top app bar and check if the user profile screen is
    // displayed
    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.profile_icon, useUnmergedTree = true)
        .assertExists()
        .performClick()

    composeTestRule.onNodeWithTag(C.Tag.user_profile_screen_container).assertExists()

    // Check that everything in the user profile screen is displayed correctly

    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.profile_button, useUnmergedTree = true)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.back_button, useUnmergedTree = true)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.screen_title, useUnmergedTree = true)
        .assertExists()
        .assertTextEquals("Your Profile")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.fullname)
        .assertExists()
        .assertTextEquals("Mr. Jaime Oliver Pastor")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.email)
        .assertExists()
        .assertTextEquals("J.OP@epfl.ch")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.phone)
        .assertExists()
        .assertTextEquals("+41223456789")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.address)
        .assertExists()
        .assertTextEquals("Rue de la Paix 20, 1202 Genève, Switzerland")

    // Check for the edit button and click on it

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.edit)
        .assertExists()
        .assertHasClickAction()
        .performClick()

    composeTestRule.onNodeWithTag(C.Tag.edit_profile_screen_container).assertExists()

    // Check if the edit profile screen is displayed and replace a few fields

    composeTestRule
        .onNodeWithTag(C.Tag.EditProfile.greeting)
        .assertExists()
        .assertTextContains("Mr.")

    composeTestRule
        .onNodeWithTag(C.Tag.EditProfile.firstname)
        .assertExists()
        .assertTextContains("Jaime")

    composeTestRule
        .onNodeWithTag(C.Tag.EditProfile.lastname)
        .assertExists()
        .assertTextContains("Oliver Pastor")

    val emailNode1 = composeTestRule.onNodeWithTag(C.Tag.EditProfile.email)
    emailNode1.assertExists().assertTextContains("J.OP@epfl.ch").performTextClearance()
    emailNode1.performTextInput("jaime.oliverpastor@epfl.ch")
    emailNode1.assertTextContains("jaime.oliverpastor@epfl.ch")

    composeTestRule
        .onNodeWithTag(C.Tag.EditProfile.phone)
        .assertExists()
        .assertTextContains("+41223456789")

    // Scroll to cancel button and click on it

    composeTestRule
        .onNodeWithTag(C.Tag.edit_profile_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.EditProfile.dismiss))

    composeTestRule
        .onNodeWithTag(C.Tag.EditProfile.dismiss)
        .assertExists()
        .assertHasClickAction()
        .performClick()

    // Check that the user profile is displayed again and it has NOT updated

    composeTestRule.onNodeWithTag(C.Tag.user_profile_screen_container).assertExists()

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.fullname)
        .assertExists()
        .assertTextEquals("Mr. Jaime Oliver Pastor")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.email)
        .assertExists()
        .assertTextEquals("J.OP@epfl.ch")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.phone)
        .assertExists()
        .assertTextEquals("+41223456789")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.address)
        .assertExists()
        .assertTextEquals("Rue de la Paix 20, 1202 Genève, Switzerland")

    // Click on the edit button again and change the email and phone number

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.edit)
        .assertExists()
        .assertHasClickAction()
        .performClick()

    composeTestRule.onNodeWithTag(C.Tag.edit_profile_screen_container).assertExists()

    val emailNode2 = composeTestRule.onNodeWithTag(C.Tag.EditProfile.email)

    emailNode2.assertExists().assertTextContains("J.OP@epfl.ch").performTextClearance()
    emailNode2.performTextInput("jaime.oliverpastor@epfl.ch")
    emailNode2.assertTextContains("jaime.oliverpastor@epfl.ch")

    val phoneNode = composeTestRule.onNodeWithTag(C.Tag.EditProfile.phone)

    phoneNode.assertExists().assertTextContains("+41223456789").performTextClearance()
    phoneNode.performTextInput("+41791234567")
    phoneNode.assertTextContains("+41791234567")

    // Scroll to save button and click on it

    composeTestRule
        .onNodeWithTag(C.Tag.edit_profile_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

    composeTestRule
        .onNodeWithTag(C.Tag.EditProfile.confirm)
        .assertExists()
        .assertHasClickAction()
        .performClick()

    composeTestRule.onNodeWithTag(C.Tag.user_profile_screen_container).assertExists()

    // Check that values have indeed been updated

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.email)
        .assertExists()
        .assertTextEquals("jaime.oliverpastor@epfl.ch")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.phone)
        .assertExists()
        .assertTextEquals("+41791234567")
  }

  @Test
  fun checkUserProfileBooksDisplay() {
    // Click on the profile icon in the top app bar and check if the user profile screen is
    // displayed
    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.profile_icon, useUnmergedTree = true)
        .assertExists()
        .performClick()

    composeTestRule.onNodeWithTag(C.Tag.user_profile_screen_container).assertExists()

    // Check that everything in the user profile screen is displayed correctly

    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.profile_button, useUnmergedTree = true)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.back_button, useUnmergedTree = true)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.screen_title, useUnmergedTree = true)
        .assertExists()
        .assertTextEquals("Your Profile")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.fullname)
        .assertExists()
        .assertTextEquals("Mr. Jaime Oliver Pastor")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.email)
        .assertExists()
        .assertTextEquals("J.OP@epfl.ch")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.phone)
        .assertExists()
        .assertTextEquals("+41223456789")

    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.address)
        .assertExists()
        .assertTextEquals("Rue de la Paix 20, 1202 Genève, Switzerland")

    // Here there are changes from the other test
    // Check that the user has 4 books in the book list and that they are correctly displayed

    composeTestRule.onNodeWithTag(C.Tag.BookListComp.book_list_container).assertExists()

    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.BookListComp.book_list_container)
        .performScrollToNode(hasTestTag("1_" + C.Tag.BookDisplayComp.book_display_container))

    composeTestRule
        .onNodeWithTag("1_" + C.Tag.BookDisplayComp.book_display_container)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.BookListComp.book_list_container)
        .performScrollToNode(hasTestTag("2_" + C.Tag.BookDisplayComp.book_display_container))

    composeTestRule
        .onNodeWithTag("2_" + C.Tag.BookDisplayComp.book_display_container)
        .assertExists()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.BookListComp.book_list_container)
        .performScrollToNode(hasTestTag("3_" + C.Tag.BookDisplayComp.book_display_container))

    composeTestRule
        .onNodeWithTag("3_" + C.Tag.BookDisplayComp.book_display_container)
        .assertExists()
        .assertHasClickAction()

    // Click on the first book and check if the book details screen is displayed

    composeTestRule
        .onNodeWithTag(C.Tag.BookListComp.book_list_container)
        .performScrollToNode(hasTestTag("0_" + C.Tag.BookDisplayComp.book_display_container))

    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .performClick()
  }

  class MockUserViewModel(userUUID: UUID, booksUUIDs: List<UUID>) : UserViewModel(userUUID) {
    private var currentUser =
        DataUser(
            userUUID,
            greeting = "Mr.",
            firstName = "Jaime",
            lastName = "Oliver Pastor",
            email = "J.OP@epfl.ch",
            phoneNumber = "+41223456789",
            latitude = 47.5596,
            longitude = 7.5886,
            profilePictureUrl = "bestPic.png",
            bookList = booksUUIDs)

    override var addressStr = MutableStateFlow("Rue de la Paix 20, 1202 Genève, Switzerland")

    override fun getUser(force: Boolean): DataUser {
      return currentUser
    }

    override fun updateUser(newDataUser: DataUser) {
      currentUser = newDataUser
    }

    override fun getUserByGoogleUid(googleUid: String) {
      // No-op
    }

    override fun updateGoogleUid(googleUid: String) {
      // No-op
    }

    override fun updateAddress(latitude: Double, longitude: Double, context: Context) {
      // No-op
    }

    override fun updateCoordinates(
        addressComponents: List<String>,
        context: Context,
        userUUID: UUID
    ) {
      // No-op
    }
  }
}
