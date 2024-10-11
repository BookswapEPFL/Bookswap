package com.android.bookswap.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class UserProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<UserProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("profileScreenContainer") }) {

  val titleTxt: KNode = child { hasTestTag("profileTitleTxt") }
  val fullNameTxt: KNode = child { hasTestTag("fullNameTxt") }
  val emailTxt: KNode = child { hasTestTag("emailTxt") }
  val phoneNumberTxt: KNode = child { hasTestTag("phoneNumberTxt") }
  val addressTxt: KNode = child { hasTestTag("addressTxt") }
  val editProfileBtn: KNode = child { hasTestTag("editProfileBtn") }
}
