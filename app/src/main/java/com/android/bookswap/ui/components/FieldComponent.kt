package com.android.bookswap.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val FIELD_MIN_WIDTH = 280.dp
private val FIELD_MIN_HEIGHT = 56.dp

private val FIELD_PADDING = 8.dp
private val FIELD_CONTENT_PADDING = 16.dp

private val ANIMATION_DURATION = 150

/**
 * A composable function that displays a custom OutlinedTextField with a label.
 *
 * @param labelText The text to be displayed as the label of the text field.
 * @param value The current value of the text field.
 * @param modifier The modifier to be applied to the text field.
 * @param onValueChange A callback function to be invoked when the value of the text field changes.
 */
@Composable
fun FieldComponent(
    labelText: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
  FieldComponent(
      value = value,
      onValueChange = onValueChange,
      modifier = modifier,
      label = { Text(labelText) })
}
/**
 * A composable function that displays a custom OutlinedTextField with various customization
 * options.
 *
 * @param value The current value of the text field.
 * @param onValueChange A callback function to be invoked when the value of the text field changes.
 * @param modifier The modifier to be applied to the text field.
 * @param enabled Whether the text field is enabled or not.
 * @param readOnly Whether the text field is read-only or not.
 * @param textStyle The style to be applied to the text field's text.
 * @param label The composable function to be used as the label of the text field.
 * @param placeholder The composable function to be used as the placeholder of the text field.
 * @param isError Whether the text field is in an error state or not.
 * @param keyboardOptions The keyboard options to be applied to the text field.
 * @param keyboardActions The keyboard actions to be applied to the text field.
 * @param singleLine Whether the text field is single-line or not.
 * @param maxLines The maximum number of lines the text field can have.
 * @param minLines The minimum number of lines the text field can have.
 * @param visualTransformation The visual transformation to be applied to the text field.
 * @param onTextLayout A callback function to be invoked when the text layout changes.
 * @param interactionSource The interaction source to be used for the text field.
 * @param cursorBrush The brush to be used for the cursor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable() (() -> Unit)? = null,
    placeholder: @Composable() (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Color.Black)
) {
  val colors = OutlinedTextFieldDefaults.colors()
  val m_interactionSource = interactionSource ?: remember { MutableInteractionSource() }
  val textColor =
      textStyle.color.takeOrElse { textColor(enabled, isError, m_interactionSource, colors).value }
  val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
  Box() {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier =
            if (label != null) {
              modifier
                  .defaultMinSize(FIELD_MIN_WIDTH, FIELD_MIN_HEIGHT)
                  // Merge semantics at the beginning of the modifier chain to ensure padding is
                  // considered part of the text field.
                  .semantics(mergeDescendants = true) {}
                  .padding(top = FIELD_PADDING)
            } else {
              modifier
            },
        enabled,
        readOnly,
        mergedTextStyle,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        minLines,
        visualTransformation,
        onTextLayout,
        m_interactionSource,
        cursorBrush,
        decorationBox =
            @Composable {
              OutlinedTextFieldDefaults.DecorationBox(
                  value = value,
                  innerTextField = it,
                  enabled = enabled,
                  singleLine = singleLine,
                  visualTransformation = visualTransformation,
                  interactionSource = m_interactionSource,
                  isError = isError,
                  label = label,
                  placeholder = placeholder,
                  container = {
                    Box(
                        Modifier.border(
                                animateBorderStrokeAsState(
                                        enabled = enabled,
                                        isError = isError,
                                        interactionSource = m_interactionSource,
                                        colors = colors,
                                        focusedBorderThickness = 2.dp,
                                        unfocusedBorderThickness = 1.dp)
                                    .value,
                                RoundedCornerShape(100))
                            .background(
                                backgroundColor(
                                        enabled = enabled,
                                        isError = isError,
                                        isEmpty =
                                            if (m_interactionSource
                                                .collectIsFocusedAsState()
                                                .value) {
                                              false
                                            } else {
                                              value.isEmpty()
                                            },
                                        colors = colors)
                                    .value,
                                CircleShape)
                            .padding(FIELD_PADDING)
                            .background(
                                backgroundColor(
                                        enabled = enabled,
                                        isError = isError,
                                        isEmpty =
                                            if (m_interactionSource
                                                .collectIsFocusedAsState()
                                                .value) {
                                              true
                                            } else {
                                              value.isEmpty()
                                            },
                                        colors = colors)
                                    .value,
                                CircleShape),
                    )
                  },
                  contentPadding = PaddingValues(FIELD_CONTENT_PADDING))
            })
  }
}
/**
 * Animates the border stroke of a text field based on its focus state.
 *
 * @param enabled Whether the text field is enabled.
 * @param isError Whether the text field is in an error state.
 * @param interactionSource The interaction source for the text field.
 * @param colors The colors to be used for the text field.
 * @param focusedBorderThickness The thickness of the border when the text field is focused.
 * @param unfocusedBorderThickness The thickness of the border when the text field is unfocused.
 * @return A state containing the animated border stroke.
 */
@Composable
private fun animateBorderStrokeAsState(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors,
    focusedBorderThickness: Dp,
    unfocusedBorderThickness: Dp
): State<BorderStroke> {
  val focused by interactionSource.collectIsFocusedAsState()
  val indicatorColor = indicatorColor(enabled, isError, interactionSource, colors)
  val targetThickness = if (focused) focusedBorderThickness else unfocusedBorderThickness
  val animatedThickness =
      if (enabled) {
        animateDpAsState(targetThickness, tween(durationMillis = ANIMATION_DURATION))
      } else {
        rememberUpdatedState(unfocusedBorderThickness)
      }
  return rememberUpdatedState(
      BorderStroke(animatedThickness.value, SolidColor(indicatorColor.value)))
}

@Composable
internal fun backgroundColor(
    enabled: Boolean,
    isError: Boolean,
    isEmpty: Boolean,
    colors: TextFieldColors
): State<Color> {
  val targetValue =
      when {
        !enabled -> colors.disabledContainerColor
        isError -> colors.errorContainerColor
        isEmpty -> MaterialTheme.colorScheme.secondaryContainer
        else -> colors.unfocusedContainerColor
      }
  return if (enabled) {
    animateColorAsState(
        targetValue, tween(durationMillis = ANIMATION_DURATION), "BackgroundColorAnim")
  } else {
    rememberUpdatedState(targetValue)
  }
}
/**
 * Determines the text color of a text field based on its state.
 *
 * @param enabled Whether the text field is enabled.
 * @param isError Whether the text field is in an error state.
 * @param interactionSource The interaction source for the text field.
 * @param colors The colors to be used for the text field.
 * @return A state containing the color of the text.
 */
@Composable
internal fun textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors
): State<Color> {
  val focused by interactionSource.collectIsFocusedAsState()

  return rememberUpdatedState(
      when {
        !enabled -> colors.disabledTextColor
        isError -> colors.errorTextColor
        focused -> colors.focusedTextColor
        else -> colors.unfocusedTextColor
      })
}
/**
 * Determines the indicator color of a text field based on its state.
 *
 * @param enabled Whether the text field is enabled.
 * @param isError Whether the text field is in an error state.
 * @param interactionSource The interaction source for the text field.
 * @param colors The colors to be used for the text field.
 * @return A state containing the color of the indicator.
 */
@Composable
internal fun indicatorColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors
): State<Color> {
  val focused by interactionSource.collectIsFocusedAsState()

  val targetValue =
      when {
        !enabled -> colors.disabledIndicatorColor
        isError -> colors.errorIndicatorColor
        focused -> colors.focusedIndicatorColor
        else -> colors.unfocusedIndicatorColor
      }
  return if (enabled) {
    animateColorAsState(
        targetValue, tween(durationMillis = ANIMATION_DURATION), "IndicatorColorAnim")
  } else {
    rememberUpdatedState(targetValue)
  }
}

/*
@androidx.compose.ui.tooling.preview.Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "LightMode",
    widthDp = 672,
)
@androidx.compose.ui.tooling.preview.Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "DarkMode",
    widthDp = 672,
)
@Composable
fun FieldComponentPreview() {
  val txtValue1 = remember { androidx.compose.runtime.mutableStateOf("FExample TextStr") }
  val txtValue2 = remember { androidx.compose.runtime.mutableStateOf("OExample TextStr") }
  val txtValue3 = remember { androidx.compose.runtime.mutableStateOf("FExample TextStr") }
  val txtValue4 = remember { androidx.compose.runtime.mutableStateOf("OExample TextTFV") }
  val txtValue5 = remember { androidx.compose.runtime.mutableStateOf("FExample TextTFV") }
  val txtValue6 = remember { androidx.compose.runtime.mutableStateOf("") }
  val labelText = "Example Label"
  val label: @Composable() (String) -> Unit = { Text(labelText + it) }

  com.android.bookswap.ui.theme.BookSwapAppTheme(false) {
    Box(modifier = Modifier.padding(4.dp)) {
	  androidx.compose.foundation.layout.Row(Modifier.padding(4.dp)) {
		androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
		  FieldComponent("1", "2", Modifier)
          FieldComponent(
              value = txtValue1.value,
              { txtValue1.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              false,
              true,
              label = { label("Str1") },
          )
		  androidx.compose.material3.OutlinedTextField(
              value = txtValue2.value,
              { txtValue2.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              label = { label("Str2") },
          )
          FieldComponent(
              value = txtValue3.value,
              { txtValue3.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              false,
              label = { label("Str3") },
              isError = true)
        }
		androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
		  androidx.compose.material3.OutlinedTextField(
              value = txtValue4.value,
              { txtValue4.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              label = { label("TFV4") })
          FieldComponent(
              value = txtValue5.value,
              { txtValue5.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              label = { label("TFV5") },
          )
          FieldComponent(
              value = txtValue6.value,
              { txtValue6.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              label = { label("TFV6") },
          )
        }
      }
    }
  }
}
// */
