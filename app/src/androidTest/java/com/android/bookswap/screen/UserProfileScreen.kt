package com.android.bookswap.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
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
  val editProfileAld: KNode = child { hasTestTag("editProfileAlert") }
  val editProfileConfirm: KNode = child { hasTestTag("confirmBtn") }
  val editProfileDismiss: KNode = child { hasTestTag("dismissBtn") }
}
