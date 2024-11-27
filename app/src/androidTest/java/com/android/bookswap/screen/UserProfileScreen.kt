package com.android.bookswap.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import com.android.bookswap.resources.C
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class UserProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<UserProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(C.Tag.user_profile_screen_container) }) {

  val titleTxt: KNode = child { hasTestTag(C.Tag.TopAppBar.screen_title) }
  val fullNameTxt: KNode = child { hasTestTag(C.Tag.UserProfile.fullname) }
  val emailTxt: KNode = child { hasTestTag(C.Tag.UserProfile.email) }
  val phoneNumberTxt: KNode = child { hasTestTag(C.Tag.UserProfile.phone) }
  val addressTxt: KNode = child { hasTestTag(C.Tag.UserProfile.address) }
  val editProfileBtn: KNode = child { hasTestTag(C.Tag.UserProfile.edit) }
}
