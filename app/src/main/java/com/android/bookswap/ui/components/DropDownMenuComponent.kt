package com.android.bookswap.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.android.bookswap.data.BookGenres
import com.android.bookswap.ui.theme.ColorVariable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuComponent(selectedGenre: MutableState<List<BookGenres>>, allOptions: String){
    /*var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.background(ColorVariable.BackGround) // Set background color here
    ) {
        OutlinedTextField(

            value = selectedGenre.value.joinToString { it.Genre } ?: "Select Genre",
            onValueChange = {},
            label = { Text("Genre") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BookGenres.values().forEach { genre ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = genre.Genre,
                            // color = ColorVariable.Secondary // Green text in dropdownmenu
                        )
                    },
                    onClick = {
                        selectedGenre.value += genre
                        expanded = false
                    })
            }
        }
    }*/
}