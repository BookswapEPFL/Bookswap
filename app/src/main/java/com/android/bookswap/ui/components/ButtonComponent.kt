package com.android.bookswap.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.ColorVariable

@Composable
fun ButtonComponent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable() (RowScope.() -> Unit)
) {
  OutlinedButton(
      modifier =
          modifier
              .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(size = 28.dp))
              .padding(0.5.dp)
              .width(120.dp)
              .height(38.dp)
              .focusable(),
      colors = ButtonDefaults.buttonColors(containerColor = ColorVariable.Primary),
      onClick = onClick,
      enabled = enabled,
      content = content)
}
