package com.android.bookswap.data

import android.location.Address
import java.util.Locale

data class User(
    var greeting: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var address: Address = Address(Locale.getDefault()),
    var profilePictureUrl: String = "",
    var userId: String = ""
) {}