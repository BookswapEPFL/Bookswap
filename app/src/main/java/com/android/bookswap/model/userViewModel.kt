package com.android.bookswap.model

import android.location.Address
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.User
import java.util.Locale

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
    updateUser(User(greeting, firstName, lastName, email, phone, address, picURL, uid))
      updateAddress(address,{})
    // firebaseConnection.storeData("users", uid, this.user)
  }

  fun updateUser(newUser: User) {
    this.user = newUser
    updateAddress(newUser.address, {})
    this.uid = newUser.userId
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
      fun createAddress(
          locale: Locale = Locale.getDefault(),
          featureName: String? = "",
          addressLines: HashMap<Int, String> = HashMap<Int, String>(),
          adminArea: String? = "",
          subAdminArea: String? = "",
          locality: String? = "",
          subLocality: String? = "",
          thoroughfare: String? = "",
          subThoroughfare: String? = "",
          premises: String? = "",
          postalCode: String? = "",
          countryCode: String? = "",
          countryName: String? = "",
          latitude: Double = 0.0,
          longitude: Double = 0.0,
          hasLat: Boolean = false,
          hasLon: Boolean = false,
          phone: String? = "",
          url: String? = ""
      ): Address {
          var addr = Address(locale)
          addr.setFeatureName(featureName)
          print("f|${featureName.orEmpty()}| ")
          addr.setAdminArea(adminArea)
          print("aa|${adminArea.orEmpty()}| ")
          addr.setSubAdminArea(subAdminArea)
          print("sa|${subAdminArea.orEmpty()}| ")
          addr.setLocality(locality)
          print("lo|${locality.orEmpty()}| ")
          addr.setSubLocality(subLocality)
          print("sl|${subLocality.orEmpty()}| ")
          addr.setThoroughfare(thoroughfare)
          print("tf|${thoroughfare.orEmpty()}| ")
          addr.setSubThoroughfare(subThoroughfare)
          print("st|${subThoroughfare.orEmpty()}| ")
          addr.setPremises(premises)
          print("pr|${premises.orEmpty()}| ")
          addr.setPostalCode(postalCode)
          print("pc|${postalCode.orEmpty()}| ")
          addr.setCountryCode(countryCode)
          print("cc|${countryCode.orEmpty()}| ")
          addr.setCountryName(countryName)
          print("cn|${countryName.orEmpty()}| ")
          if(hasLat) addr.latitude  = latitude
          if(hasLon) addr.longitude = longitude
          addr.setPhone(phone)
          print("ph|${phone.orEmpty()}| ")
          addr.setUrl(url)
          print("url|${url.orEmpty()}|")
          if(addressLines.isNotEmpty())for (line in addressLines)
              addr.setAddressLine(line.key, line.value)
          println(" - ${addr.toString()} - ")
          return addr
      }
      var loc = newAddress.locale
      if(loc == null) loc = Locale.getDefault()

      var addrLines = HashMap<Int, String>()
      if(newAddress.maxAddressLineIndex>-1)for(i in 0..newAddress.maxAddressLineIndex)
          addrLines.put(i,newAddress.getAddressLine(i))

      this.user.address = createAddress(
        loc,
        if(newAddress.featureName.isNullOrEmpty()){""}else{newAddress.featureName},
        addrLines,
        if(newAddress.adminArea.isNullOrEmpty()){""}else{newAddress.adminArea},
        if(newAddress.subAdminArea.isNullOrEmpty()){""}else{newAddress.subAdminArea},
        if(newAddress.locality.isNullOrEmpty()){""}else{newAddress.locality},
        if(newAddress.subLocality.isNullOrEmpty()){""}else{newAddress.subLocality},
        if(newAddress.thoroughfare.isNullOrEmpty()){""}else{newAddress.thoroughfare},
        if(newAddress.subThoroughfare.isNullOrEmpty()){""}else{newAddress.subThoroughfare},
        if(newAddress.premises.isNullOrEmpty()){""}else{newAddress.premises},
        if(newAddress.postalCode.isNullOrEmpty()){""}else{newAddress.postalCode},
        if(newAddress.countryCode.isNullOrEmpty()){""}else{newAddress.countryCode},
        if(newAddress.countryName.isNullOrEmpty()){""}else{newAddress.countryName},
        newAddress.latitude,
        newAddress.longitude,
        newAddress.hasLatitude(),
        newAddress.hasLongitude(),
        if(newAddress.phone.isNullOrEmpty()){""}else{newAddress.phone},
        if(newAddress.url.isNullOrEmpty()){""}else{newAddress.url}
      )
      println(this.user.address.toString())
      Log.d("userVM", "updateAddress: ${this.user.address}")
  }
}
