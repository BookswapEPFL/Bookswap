package com.android.bookswap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Padding for the button content. */
private val BUTTON_CONTENT_PADDING = 8.dp

/** Height of the button. */
private val BUTTON_HEIGHT = 56.dp

/** Minimum width of the button, calculated as three times the button height. */
private val BUTTON_MIN_WIDTH = BUTTON_HEIGHT * 3
/**
 * A composable function that creates a custom button component.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button is enabled or not.
 * @param border The border to be applied to the button.
 * @param interactionSource The interaction source representing the stream of interaction events.
 * @param content The content to be displayed inside the button.
 */
@Composable
fun ButtonComponent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable() (RowScope.() -> Unit)
) {
  val colors = ButtonDefaults.outlinedButtonColors()
  val contentPadding = PaddingValues(BUTTON_CONTENT_PADDING)
  val shape = CircleShape
  val containerColor =
      if (enabled) {
        MaterialTheme.colorScheme.primary
      } else {
        colors.disabledContainerColor
      }
  val contentColor =
      if (enabled) {
        MaterialTheme.colorScheme.onPrimary
      } else {
        colors.disabledContentColor
      }

  Surface(
      onClick = onClick,
      modifier =
          modifier
              .minimumInteractiveComponentSize()
              .clip(shape)
              .semantics { role = Role.Button }
              .height(BUTTON_HEIGHT)
              .defaultMinSize(minWidth = BUTTON_MIN_WIDTH),
      enabled = enabled,
      shape = shape,
      color = containerColor,
      contentColor = contentColor,
      border = border,
      interactionSource = interactionSource) {
        val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge)
        CompositionLocalProvider(
            LocalContentColor provides contentColor, LocalTextStyle provides mergedStyle) {
              Row(
                  Modifier.defaultMinSize(
                          minWidth = ButtonDefaults.MinWidth, minHeight = ButtonDefaults.MinHeight)
                      .padding(contentPadding),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically,
                  content = content)
            }
      }
}

/*
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, name = "LightMode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "DarkMode")
@Composable
fun ButtonComponentPreview() {
  val text = "Button"
  BookSwapAppTheme {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
      ButtonComponent({}) { Text(text) }
      TextButton(
          onClick = { /*TODO*/},
          modifier = Modifier.padding(0.5.dp),
          border = ButtonDefaults.outlinedButtonBorder) {
            Text(text)
          }
      ButtonComponent(onClick = { /*TODO*/}, border = null) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            // tint = MaterialTheme.colorScheme.secondary,
            contentDescription = "Back",
            modifier = Modifier.testTag("backIcon").size(32.dp))
      }
      IconButtonComponent(onClick = { /*TODO*/}, tint = MaterialTheme.colorScheme.outline) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            // tint = MaterialTheme.colorScheme.secondary,
            contentDescription = "Back",
            modifier = Modifier.testTag("backIcon").size(32.dp))
      }
      IconButton(onClick = { /*TODO*/}) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            // tint = MaterialTheme.colorScheme.secondary,
            contentDescription = "Back",
            modifier = Modifier.testTag("backIcon").size(32.dp))
      }
      TextButton(onClick = { /*TODO*/}) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            // tint = MaterialTheme.colorScheme.secondary,
            contentDescription = "Back",
            modifier = Modifier.testTag("backIcon").size(32.dp))
      }
    }
  }
}
*/
