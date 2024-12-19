package com.android.bookswap.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.android.bookswap.resources.C
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EditProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(C.Tag.edit_profile_screen_container) }) {
  val titleTxt: KNode = child { hasTestTag(C.Tag.TopAppBar.screen_title) }
  val greetingTbx: KNode = child { hasTestTag(C.Tag.EditProfile.greeting) }
  val firstnameTbx: KNode = child { hasTestTag(C.Tag.EditProfile.firstname) }
  val lastnameTbx: KNode = child { hasTestTag(C.Tag.EditProfile.lastname) }
  val emailTbx: KNode = child { hasTestTag(C.Tag.EditProfile.email) }
  val phoneNumberTbx: KNode = child { hasTestTag(C.Tag.EditProfile.phone) }
  val confirmBtn: KNode = child { hasTestTag(C.Tag.EditProfile.confirm) }
  val dismissBtn: KNode = child { hasTestTag(C.Tag.EditProfile.dismiss) }
  val streetBox: KNode = child { hasTestTag(C.Tag.AddressFields.address) }
  val cityBox: KNode = child { hasTestTag(C.Tag.AddressFields.city) }
  val cantonBox: KNode = child { hasTestTag(C.Tag.AddressFields.canton) }
  val postalBox: KNode = child { hasTestTag(C.Tag.AddressFields.postal) }
  val countryBox: KNode = child { hasTestTag(C.Tag.AddressFields.country) }
}
