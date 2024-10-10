package com.android.bookswap.model

import android.location.Address
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.User

open class UserViewModel(var email: String) : ViewModel() {
  var uid = "ERROR_UUID"
  private var user = User(email = email)
  private var isLoaded = false
  // private val firebaseConnection: FirebaseConnection = FirebaseConnection.getInstance()

  open fun getUser(force: Boolean = false): User {
    if (!isLoaded || force) {
      fetchUser()
    }
    return user
  }

  private fun fetchUser() {
    /*
    firebaseConnection.getUserUidByEmail(user.email).addOnSuccessListener {
        if (it.documents.isNotEmpty()) {
            this.uid = it.documents[0].id
            val docRef = firebaseConnection.getUserData(uid)

            docRef.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        this.user.name = document.getString("name") ?: ""
                        this.user.email = document.getString("email") ?: ""
                        val street = document.getString("address.street") ?: ""
                        val city = document.getString("address.city") ?: ""
                        val state = document.getString("address.state") ?: ""
                        val postalCode = document.getString("address.postalCode") ?: ""
                        val loc = document.get("address.location")
                        val location =
                            if (loc != null) {
                                val l = loc as HashMap<*, *>
                                LocationMap(
                                    l["latitude"] as Double, l["longitude"] as Double, l["name"] as String)
                            } else {
                                LocationMap()
                            }
                        this.user.address = Address(street, city, state, postalCode, location)
                        this.user.phoneNumber = document.getString("phoneNumber") ?: ""
                        this.user.pawPoints = document.getLong("pawPoints")?.toInt() ?: 0
                        this.user.profilePictureUrl = document.getString("profilePictureUrl") ?: ""
                    }
                }
            }
        } else {
            Log.w(
                "firebase query",
                "Unable to find UUID from email ${user.email} ! falling back to default user")
            uid = "ERROR_UUID"
        }
    }
    */
    /*
    if (uid != "ERROR_UUID") {
        val docRef = firebaseConnection.getUserData(uid)
        docRef.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    this.user.name = document.getString("name") ?: ""
                    this.user.email = document.getString("email") ?: ""
                    val street = document.getString("address.street") ?: ""
                    val city = document.getString("address.city") ?: ""
                    val state = document.getString("address.state") ?: ""
                    val postalCode = document.getString("address.postalCode") ?: ""
                    val loc = document.get("address.location")
                    val location =
                        if (loc != null) {
                            val l = loc as HashMap<*, *>
                            LocationMap(
                                l["latitude"] as Double, l["longitude"] as Double, l["name"] as String)
                        } else {
                            LocationMap()
                        }
                    this.user.address = Address(street, city, state, postalCode, location)
                    this.user.phoneNumber = document.getString("phoneNumber") ?: ""
                    this.user.pawPoints = document.getLong("pawPoints")?.toInt() ?: 0
                    this.user.profilePictureUrl = document.getString("profilePictureUrl") ?: ""
                }
            }
        }
        isLoaded = true
    }
    */
  }

  fun updateUser(
      greeting: String = user.greeting,
      firstName: String = user.firstName,
      lastName: String = user.lastName,
      email: String = user.email,
      phone: String = user.phoneNumber,
      address: Address = user.address,
      picURL: String = user.profilePictureUrl
  ) {
    this.user = User(greeting, firstName, lastName, email, phone, address, picURL)
    // firebaseConnection.storeData("users", uid, this.user)
  }

  fun updateUser(newUser: User = user) {
    this.user = newUser
    // firebaseConnection.storeData("users", uid, this.user)
  }

  fun getNameFromFirebase(onComplete: (String) -> Unit) {
    /*
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result
            if (document != null) {
                var name = document.getString("name") ?: ""
                onComplete(name)
            }
        }
    }
    */
  }

  fun getPhoneNumberFromFirebase(onComplete: (String) -> Unit) {
    /*
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result
            if (document != null) {
                var phoneNumber = document.getString("phoneNumber") ?: ""
                onComplete(phoneNumber)
            }
        }
    }
    */
  }

  fun getAddressFromFirebase(onComplete: (Address) -> Unit) {
    /*
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result
            if (document != null) {
                // Fetch each part of the address from the document
                val street = document.getString("address.street") ?: ""
                val city = document.getString("address.city") ?: ""
                val state = document.getString("address.state") ?: ""
                val postalCode = document.getString("address.postalCode") ?: ""
                //val location = document.get("address.coord") as? HashMap<Double, Double> ?: emptyMap<Double, Double>()

                // Construct an Address object
                val address = Address(Locale.getDefault())
                address.setAddressLine(0,street)
                address.locality = city
                address.adminArea = state
                address.postalCode = postalCode
                //address.latitude = location[0]

                onComplete(address)
            }
        } else {
            // Handle the error or complete with a default Address
            onComplete(Address(Locale.getDefault()))
        }
    }
    */
  }

  fun updateAddress(newAddress: Address, onComplete: () -> Unit) {
    var address = newAddress
  }
}
