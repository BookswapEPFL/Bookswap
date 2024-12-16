package com.android.bookswap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable

val borderPadding = 1.dp
val buttonPadding = 8.dp
val iconSize = 32.dp
val pngSize = 24.dp
val textSize = 18.sp
private const val BUTTON_HEIGHT = 48

/**
 * Composable function to display a button with icons to the left and right. If there is an argument
 * for both icon and iconPainter, only the icon will be displayed.
 *
 * @param text The text to display on the button.
 * @param leftIcon The optional left icon to display on the button.
 * @param leftIconPainter The optional left icon painter to display on the button.
 * @param rightIcon The optional right icon to display on the button.
 * @param rightIconPainter The optional right icon painter to display on the button.
 * @param onClick action on click
 * @param buttonWidth The width of the button.
 * @param buttonHeight The width of the button.
 */
@Composable
fun ButtonWithIcons(
    text: String,
    leftIcon: ImageVector? = null,
    leftIconPainter: Painter? = null,
    rightIcon: ImageVector? = null,
    rightIconPainter: Painter? = null,
    onClick: () -> Unit,
    buttonWidth: Dp,
    buttonHeight: Dp = BUTTON_HEIGHT.dp,
    buttonShape: RoundedCornerShape = RoundedCornerShape(buttonPadding)
) {
  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = ColorVariable.AccentSecondary,
              contentColor = ColorVariable.BackGround),
      border = BorderStroke(borderPadding, ColorVariable.Accent),
      shape = buttonShape,
      modifier =
          Modifier.padding(buttonPadding)
              .width(buttonWidth)
              .height(buttonHeight)
              .testTag(text + C.Tag.NewBookChoice.btnWIcon.button)) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              IconOrPainter(
                  leftIcon,
                  leftIconPainter,
                  text + C.Tag.NewBookChoice.btnWIcon.leftIcon,
                  text + C.Tag.NewBookChoice.btnWIcon.leftPng)
              Text(text, fontSize = textSize)
              IconOrPainter(
                  rightIcon,
                  rightIconPainter,
                  text + C.Tag.NewBookChoice.btnWIcon.rightIcon,
                  text + C.Tag.NewBookChoice.btnWIcon.rightPng)
            }
      }
}

@Composable
private fun IconOrPainter(
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    iconTag: String,
    iconPainterTag: String
) {
  if (icon != null) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(iconSize).testTag(iconTag))
  } else if (iconPainter != null) {
    Image(
        painter = iconPainter,
        contentDescription = null,
        modifier = Modifier.size(pngSize).testTag(iconPainterTag))
  } else {
    Spacer(modifier = Modifier.width(iconSize)) // Reserve space when no icon or iconPainter
  }
}
