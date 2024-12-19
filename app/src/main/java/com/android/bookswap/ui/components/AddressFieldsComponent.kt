package com.android.bookswap.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.R
import com.android.bookswap.model.InputVerification
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable

private val ERROR_FONT_SIZE = 12.sp
private val NEW_ADDRESS_FONT_SIZE = 18.sp
private val PADDING_COLUMN = 8.dp
private val PADDING_ROW = 4.dp
private const val THREE_QUARTERS = 0.75f
private const val ONE_QUARTER = 0.25f
private const val TWO_THIRDS = 0.66f
private const val ONE_THIRD = 0.33f
private val COLOUR = ColorVariable.AccentSecondary

/**
 * Component for a series of fields that when merged together allow for an accurate geolocation
 *
 * @param firstAttempt: Boolean to indicate if the user has attempted to submit the form before
 * @param editVSNew: Boolean to indicate if the user is editing an existing address or creating a
 *   new one
 * @param address: String representing the street address
 * @param onAddressChange: Function to update the street address
 * @param city: String representing the city
 * @param onCityChange: Function to update the city
 * @param cityError: String representing the error message for the city field
 * @param canton: String representing the canton
 * @param onCantonChange: Function to update the canton
 * @param postal: String representing the postal code
 * @param onPostalChange: Function to update the postal code
 * @param country: String representing the country
 * @param onCountryChange: Function to update the country
 * @param countryError: String representing the error message for the country field
 */
@Composable
fun AddressFieldsComponent(
    firstAttempt: Boolean = false,
    editVSNew: Boolean = true,
    address: String = "",
    onAddressChange: (String) -> Unit,
    city: String = "",
    onCityChange: (String) -> Unit,
    cityError: String = "",
    canton: String = "",
    onCantonChange: (String) -> Unit,
    postal: String = "",
    onPostalChange: (String) -> Unit,
    country: String = "",
    onCountryChange: (String) -> Unit,
    countryError: String = ""
) {
  val verification = InputVerification()
  Column {
    if (!editVSNew) {
      Text(
          stringResource(R.string.new_address),
          fontSize = NEW_ADDRESS_FONT_SIZE,
          modifier = Modifier.padding(PADDING_COLUMN).testTag(C.Tag.AddressFields.newAddress))
    }
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text(stringResource(R.string.street_display)) },
        placeholder = { Text(stringResource(R.string.street_example), color = COLOUR) },
        modifier =
            Modifier.fillMaxWidth().padding(PADDING_COLUMN).testTag(C.Tag.AddressFields.address))
    Row(modifier = Modifier.fillMaxWidth().padding(PADDING_COLUMN)) {
      OutlinedTextField(
          value = city,
          onValueChange = onCityChange,
          label = { Text(stringResource(R.string.city_display)) },
          placeholder = { Text(stringResource(R.string.city_example), color = COLOUR) },
          modifier =
              Modifier.weight(if (!editVSNew) TWO_THIRDS else THREE_QUARTERS)
                  .padding(end = PADDING_ROW)
                  .testTag(C.Tag.AddressFields.city),
          isError = !verification.validateNonEmpty(city) && !firstAttempt && editVSNew)
      OutlinedTextField(
          value = postal,
          onValueChange = onPostalChange,
          label = { Text(stringResource(R.string.postal_display)) },
          placeholder = { Text(stringResource(R.string.postal_example), color = COLOUR) },
          modifier =
              Modifier.weight(if (!editVSNew) ONE_THIRD else ONE_QUARTER)
                  .padding(start = PADDING_ROW)
                  .testTag(C.Tag.AddressFields.postal))
    }
    if (!verification.validateNonEmpty(city) && !firstAttempt && editVSNew) {
      Text(
          cityError,
          color = Color.Red,
          fontSize = ERROR_FONT_SIZE,
          modifier = Modifier.testTag(C.Tag.AddressFields.cityError))
    }
    OutlinedTextField(
        value = canton,
        onValueChange = onCantonChange,
        label = { Text(stringResource(R.string.canton_display)) },
        placeholder = { Text(stringResource(R.string.canton_example), color = COLOUR) },
        modifier =
            Modifier.fillMaxWidth().padding(PADDING_COLUMN).testTag(C.Tag.AddressFields.canton))
    OutlinedTextField(
        value = country,
        onValueChange = onCountryChange,
        label = { Text(stringResource(R.string.country_display)) },
        placeholder = { Text(stringResource(R.string.country_example), color = COLOUR) },
        modifier =
            Modifier.fillMaxWidth().padding(PADDING_COLUMN).testTag(C.Tag.AddressFields.country),
        isError = !verification.validateNonEmpty(country) && !firstAttempt && editVSNew)
    if (!verification.validateNonEmpty(country) && !firstAttempt && editVSNew) {
      Text(
          countryError,
          color = Color.Red,
          fontSize = ERROR_FONT_SIZE,
          modifier = Modifier.testTag(C.Tag.AddressFields.countryError))
    }
  }
}
