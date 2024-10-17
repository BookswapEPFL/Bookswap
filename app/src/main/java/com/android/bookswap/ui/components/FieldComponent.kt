package com.android.bookswap.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.ColorVariable

@Composable
fun FieldComponent(
    labelText: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier.scale(1.0f, 0.8f),
        shape = RoundedCornerShape(25.dp),
        value = value,
        colors =
        TextFieldDefaults.colors(
            unfocusedContainerColor = ColorVariable.Secondary,
            focusedContainerColor = ColorVariable.Secondary,
            cursorColor = ColorVariable.Secondary, // Custom green for the cursor
            focusedLabelColor = ColorVariable.Secondary, // Custom green for focused label
            unfocusedLabelColor = ColorVariable.Secondary, // Lighter color for unfocused label
        ),
        onValueChange = onValueChange,
        label = {
            Text(
                labelText,
                style =
                TextStyle(
                    color = ColorVariable.AccentSecondary,
                ),
            )
        })
}
