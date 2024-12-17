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
import androidx.compose.ui.unit.dp
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable

@Composable
fun AddressFieldsComponent(
    address: String = "",
    onAddressChange: (String) -> Unit,
    city: String = "",
    onCityChange: (String) -> Unit,
    canton: String = "",
    onCantonChange: (String) -> Unit,
    postal: String = "",
    onPostalChange: (String) -> Unit,
    country: String = "",
    onCountryChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Address") },
            placeholder = { Text("Avenue de la Gare 20", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag(C.Tag.AddressFields.address)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City") },
                placeholder = { Text("Lausanne", color = ColorVariable.AccentSecondary) },
                modifier = Modifier
                    .weight(0.75f)
                    .padding(end = 4.dp)
                    .testTag(C.Tag.AddressFields.city)
            )
            OutlinedTextField(
                value = postal,
                onValueChange = onPostalChange,
                label = { Text("PLZ") },
                placeholder = { Text("1003",  color = ColorVariable.AccentSecondary) },
                modifier = Modifier
                    .weight(0.25f)
                    .padding(start = 4.dp)
                    .testTag(C.Tag.AddressFields.postal)
            )
        }
        OutlinedTextField(
            value = canton,
            onValueChange = onCantonChange,
            label = { Text("Region") },
            placeholder = { Text("Vaud",  color = ColorVariable.AccentSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag(C.Tag.AddressFields.canton)
        )
        OutlinedTextField(
            value = country,
            onValueChange = onCountryChange,
            label = { Text("Country") },
            placeholder = { Text("Switzerland",  color = ColorVariable.AccentSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag(C.Tag.AddressFields.country)
        )
    }
}