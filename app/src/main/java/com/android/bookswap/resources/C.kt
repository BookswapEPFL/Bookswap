package com.android.bookswap.resources

// Like R, but C

// Private constants used to construct various test tags
private const val SCREEN = "_screen"
private const val BOOK = "book"
private const val USER = "user"
private const val PROFILE = "_profile"
private const val LIST = "_list"

// Private object containing UI actions
private object A {
  const val NEW = "new"
  const val EDIT = "edit"
  const val DELETE = "delete"
  const val CONFIRM = "confirm"
  const val CANCEL = "cancel"
  const val NEXT = "next"
  const val BACK = "back"
}

// Private object containing UI component types
private object UI_T {
  const val TEXT = "_text"
  const val LABEL = "_label"
  const val DROPDOWN = "_dropdown"
  const val IMAGE = "_image"
  const val ICON = "_icon"
  const val FIELD = "_field"
  const val TEXT_FIELD = TEXT + FIELD
  const val DROPDOWN_FIELD = DROPDOWN + FIELD
  const val IMAGE_FIELD = IMAGE + FIELD
  const val BUTTON = "_button"
  const val ICON_BUTTON = ICON + BUTTON
  const val SCROLLABLE = "_scrollable"
  const val CONTAINER = "_container"
  const val SCROLLABLE_CONTAINER = SCROLLABLE + CONTAINER
  const val SCREEN_CONTAINER = SCREEN + CONTAINER
  const val DIVIDER = "_divider"
}

// Private object containing screen names
private object S {
  const val AUTH = "auth"
  const val NEW_USER = A.NEW + "_" + USER
  const val CHAT = "chat"
  const val CHAT_LIST = CHAT + LIST
  const val NEW_BOOK = A.NEW + "_" + BOOK
  const val BOOK_PROFILE = BOOK + PROFILE
  const val EDIT_BOOK = A.EDIT + "_" + BOOK
  const val MAP = "map"
  const val USER_PROFILE = USER + PROFILE
  const val EDIT_PROFILE = A.EDIT + PROFILE
  const val OTHERS_USER_PROFILE = "others_" + USER_PROFILE
  const val SETTINGS = "settings"
  const val MAP_FILTER = MAP + "_filters"
}

object C {
  object Tag {
    // Screen and components containers
    const val main_screen_container = "main" + UI_T.SCREEN_CONTAINER
    const val sign_in_screen_container = S.AUTH + UI_T.SCREEN_CONTAINER
    const val new_user_screen_container = S.NEW_USER + UI_T.SCREEN_CONTAINER
    const val user_profile_screen_container = S.USER_PROFILE + UI_T.SCREEN_CONTAINER
    const val other_user_profile_screen_container = S.OTHERS_USER_PROFILE + UI_T.SCREEN_CONTAINER
    const val edit_profile_screen_container = S.EDIT_PROFILE + UI_T.SCREEN_CONTAINER
    const val map_screen_container = S.MAP + UI_T.SCREEN_CONTAINER
    const val map_filters_screen_container = S.MAP_FILTER + UI_T.SCREEN_CONTAINER
    const val new_book_choice_screen_container = S.NEW_BOOK + "_choice" + UI_T.SCREEN_CONTAINER
    const val new_book_isbn_screen_container = S.NEW_BOOK + "_isbn" + UI_T.SCREEN_CONTAINER
    const val new_book_manual_screen_container = S.NEW_BOOK + "_manual" + UI_T.SCREEN_CONTAINER
    const val new_book_scan_screen_container = S.NEW_BOOK + "_scan" + UI_T.SCREEN_CONTAINER
    const val edit_book_screen_container = S.EDIT_BOOK + UI_T.SCREEN_CONTAINER
    const val book_profile_screen_container = S.BOOK_PROFILE + UI_T.SCREEN_CONTAINER
    const val chat_list_screen_container = S.CHAT_LIST + UI_T.SCREEN_CONTAINER
    const val chat_screen_container = S.CHAT + UI_T.SCREEN_CONTAINER
    const val bottom_navigation_menu_container = "bottom_navigation_menu" + UI_T.CONTAINER
    const val top_app_bar_container = "top_app_bar" + UI_T.CONTAINER

    // TopAppBar specific tags (TopAppBarComponent.kt)
    object TopAppBar {
      const val back_button = A.BACK + UI_T.ICON_BUTTON
      const val back_icon = A.BACK + UI_T.ICON
      const val screen_title = "screen_title"
      const val profile_button = S.USER_PROFILE + UI_T.ICON_BUTTON
      const val profile_icon = S.USER_PROFILE + UI_T.ICON
    }

    // BottomNavigationMenu specific tags (BottomNavigationMenu.kt)
    object BottomNavMenu {
      const val nav_icon = "_nav" + UI_T.ICON
      const val nav_item = "_nav" + UI_T.ICON_BUTTON
    }

    // LabeledText composable specific tags (OtherUserProfile.kt)
    object LabeledText {
      const val label = UI_T.LABEL
      const val text = UI_T.TEXT
    }

    // BookListComponent specific tags (BookListComponent.kt)
    object BookListComp {
      const val book_list_container = BOOK + LIST + UI_T.SCROLLABLE_CONTAINER
      const val empty_list_text = "empty" + LIST + UI_T.TEXT
      const val divider = BOOK + LIST + UI_T.DIVIDER
    }

    object BookEntryComp {
      // Parent container
      const val entries_list_book_container = "entries_list_book" + UI_T.CONTAINER
      const val scrollable = "scrollable" + UI_T.SCROLLABLE_CONTAINER
      // Fields
      const val title_field = "title" + UI_T.TEXT_FIELD
      const val genre_field = "genres" + UI_T.DROPDOWN
      const val author_field = "author" + UI_T.TEXT_FIELD
      const val description_field = "description" + UI_T.TEXT_FIELD
      const val rating_field = "rating" + UI_T.TEXT_FIELD
      const val isbn_field = "isbn" + UI_T.TEXT_FIELD
      const val photo_field = "photo" + UI_T.IMAGE_FIELD
      const val language_field = "language" + UI_T.DROPDOWN

      // Dropdown menus
      const val genre_menu = "genre_menu" + UI_T.SCROLLABLE_CONTAINER
      const val genre_menu_item = "genre_menu_item" + UI_T.CONTAINER
      const val language_menu = "language_menu" + UI_T.SCROLLABLE_CONTAINER
      const val language_menu_item = "language_menu_item" + UI_T.CONTAINER

      // Buttons
      const val action_buttons = "action_buttons" + UI_T.CONTAINER
      const val confirm_button = A.CONFIRM + UI_T.BUTTON
      const val cancel_button = A.CANCEL + UI_T.BUTTON

      const val rating_field_stars = "rating_stars" + UI_T.CONTAINER
      const val rating_star = "rating_star" + UI_T.ICON
      const val rating_star_empty = "rating_star_empty" + UI_T.ICON
    }

    // BookDisplayComponent specific tags (BookDisplayComponent.kt)
    object BookDisplayComp {
      const val book_display_container = BOOK + UI_T.CONTAINER
      const val image = BOOK + UI_T.IMAGE
      const val image_picture = BOOK + "_image_picture" + UI_T.IMAGE
      const val image_gray_box = BOOK + "_image_gray_box" + UI_T.IMAGE
      const val middle_container = BOOK + "_middle" + UI_T.CONTAINER
      const val right_container = BOOK + "_right" + UI_T.CONTAINER
      const val title = BOOK + "_title" + UI_T.TEXT
      const val author = BOOK + "_author" + UI_T.TEXT
      const val rating = BOOK + "_rating" + UI_T.TEXT
      const val genres = BOOK + "_genres" + UI_T.TEXT
      const val filled_star = "filled_star" + UI_T.ICON
      const val hollow_star = "hollow_star" + UI_T.ICON
    }

    // User Profile Screen specific tags (UserProfile.kt)
    object UserProfile {
      const val fullname = "fullname" + UI_T.TEXT
      const val email = "email" + UI_T.TEXT
      const val phone = "phoneNumber" + UI_T.TEXT
      const val address = "address" + UI_T.TEXT
      const val edit = S.EDIT_PROFILE + UI_T.BUTTON
      const val profileImage = "profile" + UI_T.IMAGE
      const val take_photo = A.NEW + UI_T.IMAGE + UI_T.BUTTON
      const val profileImageBox = "profile_image_box" + UI_T.CONTAINER
    }

    object AddressFields {
      const val address = "address" + UI_T.TEXT_FIELD
      const val city = "city" + UI_T.TEXT_FIELD
      const val canton = "canton" + UI_T.TEXT_FIELD
      const val postal = "postal" + UI_T.TEXT_FIELD
      const val country = "country" + UI_T.TEXT_FIELD
    }

    object OtherUserProfile {
      const val fullname = "fullname"
      const val email = "email"
      const val phone = "phoneNumber"
      const val address = "address"
      const val profilePictureContainer = S.OTHERS_USER_PROFILE + "_picture" + UI_T.CONTAINER
      const val profile_image_picture = S.OTHERS_USER_PROFILE + "_picture" + UI_T.IMAGE
      const val profile_image_icon = S.OTHERS_USER_PROFILE + "_picture" + UI_T.ICON
      const val chatButton = "chatButton"
    }

    // Edit User Profile Screen specific tags (EditProfile.kt)
    object EditProfile {
      const val greeting = "greeting" + UI_T.TEXT_FIELD
      const val firstname = "firstname" + UI_T.TEXT_FIELD
      const val lastname = "lastname" + UI_T.TEXT_FIELD
      const val email = "email" + UI_T.TEXT_FIELD
      const val phone = "phone" + UI_T.TEXT_FIELD
      const val confirm = A.CONFIRM + UI_T.BUTTON
      const val dismiss = A.CANCEL + UI_T.BUTTON
    }

    // Book Profile Screen specific tags (BookProfile.kt)
    object BookProfile {
      const val scrollable = Screen.BOOK_PROFILE + UI_T.SCROLLABLE_CONTAINER
      const val title = BOOK + "_title" + UI_T.TEXT
      const val author = BOOK + "_author" + UI_T.TEXT
      const val rating = BOOK + "_rating" + UI_T.TEXT
      const val synopsis_label = BOOK + "_synopsis" + UI_T.LABEL
      const val synopsis = BOOK + "_synopsis" + UI_T.TEXT
      const val language = BOOK + "_language" + UI_T.TEXT
      const val genres = BOOK + "_genres" + UI_T.LABEL
      const val genre = "_genre" + UI_T.TEXT
      const val isbn = BOOK + "_isbn" + UI_T.TEXT
      const val date = BOOK + "_date" + UI_T.TEXT
      const val imagePlaceholder = BOOK + "_placeholder" + UI_T.IMAGE
      const val image = BOOK + UI_T.IMAGE
      const val volume = BOOK + "_volume" + UI_T.TEXT
      const val issue = BOOK + "_issue" + UI_T.TEXT
      const val editorial = BOOK + "_editorial" + UI_T.TEXT
      const val location = BOOK + "_location" + UI_T.TEXT
      const val edit = BOOK + A.EDIT + UI_T.BUTTON
      const val scrollable_end = S.BOOK_PROFILE + UI_T.DIVIDER
    }

    // SignIn Screen specific tags (SignIn.kt)
    object SignIn {
      const val app_name = S.AUTH + "_login_title" + UI_T.TEXT
      const val signIn = S.AUTH + UI_T.BUTTON
    }

    // NewUser Screen specific tags (NewUser.kt)
    object NewUser {
      const val personal_info = "personal_info" + UI_T.TEXT
      const val profile_pic = A.NEW + PROFILE + UI_T.IMAGE + UI_T.ICON_BUTTON
      const val greeting = "greeting" + UI_T.TEXT_FIELD
      const val firstname = "firstname" + UI_T.TEXT_FIELD
      const val lastname = "lastname" + UI_T.TEXT_FIELD
      const val email = "email" + UI_T.TEXT_FIELD
      const val phone = "phone" + UI_T.TEXT_FIELD
      const val firstname_error = "firstname_error" + UI_T.TEXT
      const val lastname_error = "lastname_error" + UI_T.TEXT
      const val email_error = "email_error" + UI_T.TEXT
      const val phone_error = "phone_error" + UI_T.TEXT
      const val confirm = A.CONFIRM + UI_T.BUTTON
    }

    // New Book Choice Screen specific tags (BookAdditionChoice.kt)
    object NewBookChoice {
      object btnWIcon {
        val button = UI_T.BUTTON
        val icon = UI_T.ICON
        val png = UI_T.IMAGE
        val arrow = "_arrow" + UI_T.ICON
      }
    }

    // New Book ISBN Screen specific tags (AddISBNScreen.kt)
    object NewBookISBN {
      const val isbn = "isbn" + UI_T.TEXT_FIELD
      const val search = "search" + UI_T.BUTTON
    }

    // New Book Manually Screen specific tags (AddToBook.kt)
    object NewBookManually {
      const val title = BOOK + "_title" + UI_T.TEXT_FIELD
      const val genres = BOOK + "_genres" + UI_T.FIELD
      const val author = "author " + UI_T.TEXT_FIELD
      const val synopsis = "description " + UI_T.TEXT_FIELD
      const val rating = "rating " + UI_T.TEXT_FIELD
      const val isbn = "isbn " + UI_T.TEXT_FIELD
      const val photo = "photo" + UI_T.IMAGE_FIELD
      const val language = BOOK + "_language" + UI_T.FIELD
      const val save = A.CONFIRM + UI_T.BUTTON
    }

    // Edit Book Screen specific tags (EditBookScreen.kt)
    object EditBook {
      const val scrollable = Screen.EDIT_BOOK + UI_T.SCROLLABLE_CONTAINER
      const val title = BOOK + "_title" + UI_T.TEXT_FIELD
      const val genres = BOOK + "_genres" + UI_T.DROPDOWN
      const val genre = "_genres" + UI_T.DROPDOWN_FIELD
      const val author = BOOK + "_author" + UI_T.TEXT_FIELD
      const val synopsis = BOOK + "_description" + UI_T.TEXT_FIELD
      const val rating = BOOK + "_rating" + UI_T.TEXT_FIELD
      const val isbn = BOOK + "_isbn" + UI_T.TEXT_FIELD
      const val image = BOOK + UI_T.IMAGE_FIELD
      const val language = BOOK + "_language" + UI_T.TEXT_FIELD
      const val save = A.CONFIRM + UI_T.BUTTON
      const val delete = A.DELETE + UI_T.BUTTON
    }

    // Map Screen specific tags (Map.kt)
    object Map {
      const val google_map = "google_map" + UI_T.CONTAINER
      const val bottom_drawer_container = "bottom_drawer" + UI_T.CONTAINER
      const val bottom_drawer_layout = "bottom_drawer_layout" + UI_T.CONTAINER
      const val bottom_drawer_handle = "bottom_drawer_handle" + UI_T.CONTAINER
      const val bottom_drawer_handle_divider = "bottom_drawer_handle" + UI_T.DIVIDER
      const val filter_button = "filter" + UI_T.BUTTON
      // Tags specific to components related to Google Maps Markers
      object Marker {
        const val info_window_container = "marker_info" + UI_T.CONTAINER
        const val info_window_scrollable = "marker_info" + UI_T.SCROLLABLE_CONTAINER
        const val info_window_divider = "marker_info" + UI_T.DIVIDER
        const val info_window_book_container = "marker_info_" + BOOK + UI_T.CONTAINER
        const val book_title = "marker_info_" + BOOK + "_title" + UI_T.TEXT
        const val book_author = "marker_info_" + BOOK + "_author" + UI_T.TEXT
      }
    }

    // Map Filter Screen specific tags (FilterMap.kt)
    object MapFilter {
      const val apply = A.CONFIRM + UI_T.BUTTON
      const val filter = "_filter" + UI_T.BUTTON
    }

    // Chat List Screen specific tags (ChatList.kt)
    object ChatList {
      const val scrollable = S.CHAT_LIST + UI_T.SCROLLABLE_CONTAINER
      const val item = S.CHAT + "_item" + UI_T.CONTAINER
      const val contact = S.CHAT + "_contact" + UI_T.TEXT
      const val message = S.CHAT + "_message" + UI_T.TEXT
      const val timestamp = S.CHAT + "_timestamp" + UI_T.TEXT
    }

    // Chat Screen specific tags (ChatScreen.kt)
    object ChatScreen {
      const val scrollable = Screen.CHAT + UI_T.SCROLLABLE_CONTAINER
      const val add_image = A.NEW + UI_T.ICON + UI_T.BUTTON
      const val message = "message" + UI_T.TEXT_FIELD
      const val confirm_button = A.CONFIRM + UI_T.BUTTON
      const val edit = A.EDIT + UI_T.BUTTON
      const val delete = A.DELETE + UI_T.BUTTON
      const val messages = "message_item"
      const val container = messages + UI_T.CONTAINER
      const val content = messages + "_content"
      const val timestamp = messages + "_timestamp" + UI_T.TEXT
      const val pop_out = "pop_out" + UI_T.CONTAINER
    }
  }

  // Top-level navigation routes
  object Route {
    const val AUTH = S.AUTH
    const val CHAT_LIST = S.CHAT_LIST
    const val NEW_BOOK = S.NEW_BOOK
    const val MAP = S.MAP
    const val USER_PROFILE = S.USER_PROFILE
    const val OTHERS_USER_PROFILE = S.OTHERS_USER_PROFILE
  }

  // Every Screen route in the app
  object Screen {
    const val AUTH = S.AUTH + SCREEN
    const val NEW_USER = S.NEW_USER + SCREEN
    const val CHAT_LIST = S.CHAT_LIST + SCREEN
    const val CHAT = S.CHAT + SCREEN
    const val MAP = S.MAP + SCREEN
    const val MAP_FILTER = S.MAP_FILTER + SCREEN
    const val NEW_BOOK = S.NEW_BOOK + "_choice" + SCREEN
    const val ADD_BOOK_MANUALLY = S.NEW_BOOK + "_manually" + SCREEN
    const val ADD_BOOK_SCAN = S.NEW_BOOK + "_scan" + SCREEN
    const val ADD_BOOK_ISBN = S.NEW_BOOK + "_isbn" + SCREEN
    const val USER_PROFILE = S.USER_PROFILE + SCREEN
    const val OTHERS_USER_PROFILE = S.OTHERS_USER_PROFILE + SCREEN
    const val BOOK_PROFILE = S.BOOK_PROFILE + SCREEN
    const val EDIT_BOOK = S.EDIT_BOOK + SCREEN
    const val SETTINGS = S.SETTINGS + SCREEN
  }
}
