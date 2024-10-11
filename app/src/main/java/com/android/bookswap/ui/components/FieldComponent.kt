package com.android.bookswap.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.AccentSecondary
import com.android.bookswap.ui.theme.Secondary

@Composable
fun FieldComponent(labelText: String, value: String, onValueChange: (String) -> Unit = {}) {
  OutlinedTextField(
      modifier = Modifier.width(290.dp).height(42.dp),
      shape = RoundedCornerShape(25.dp),
      value = value,
      colors =
          TextFieldDefaults.colors(
              unfocusedContainerColor = Secondary,
              focusedContainerColor = Secondary,
              cursorColor = Secondary, // Custom green for the cursor
              focusedLabelColor = Secondary, // Custom green for focused label
              unfocusedLabelColor = Secondary, // Lighter color for unfocused label
          ),
      onValueChange = onValueChange,
      label = {
        Text(
            labelText,
            style =
                TextStyle(
                    color = AccentSecondary,
                ),
        )
      })
}
