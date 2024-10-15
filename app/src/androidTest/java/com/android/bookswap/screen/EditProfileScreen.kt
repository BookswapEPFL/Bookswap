package com.android.bookswap.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EditProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("editProfileAlert") }) {

  val titleTxt: KNode = child { hasTestTag("editProfileTitleTxt") }
  val greetingTbx: KNode = child { hasTestTag("greetingTbx") }
  val firstnameTbx: KNode = child { hasTestTag("firstnameTbx") }
  val lastnameTbx: KNode = child { hasTestTag("lastnameTbx") }
  val emailTbx: KNode = child { hasTestTag("emailTbx") }
  val phoneNumberTbx: KNode = child { hasTestTag("phoneTbx") }
  val confirmBtn: KNode = child { hasTestTag("confirmBtn") }
  val dismissBtn: KNode = child { hasTestTag("dismissBtn") }
}
