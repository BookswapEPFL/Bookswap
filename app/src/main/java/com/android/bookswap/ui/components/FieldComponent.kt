package com.android.bookswap.ui.components

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.BookSwapAppTheme

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FieldComponent(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
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
                  .defaultMinSize(280.dp, 56.dp)
                  // Merge semantics at the beginning of the modifier chain to ensure padding is
                  // considered part of the text field.
                  .semantics(mergeDescendants = true) {}
                  .padding(top = 8.dp)
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
                  value = value.text,
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
                                              value.text.isEmpty()
                                            },
                                        interactionSource = m_interactionSource,
                                        colors = colors)
                                    .value,
                                RoundedCornerShape(100))
                            .padding(8.dp)
                            .padding(top = 0.dp)
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
                                              value.text.isEmpty()
                                            },
                                        interactionSource = m_interactionSource,
                                        colors = colors)
                                    .value,
                                RoundedCornerShape(100)),
                    )
                  },
                  contentPadding =
                      PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp))
            })
  }
}

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
                  .defaultMinSize(280.dp, 56.dp)
                  // Merge semantics at the beginning of the modifier chain to ensure padding is
                  // considered part of the text field.
                  .semantics(mergeDescendants = true) {}
                  .padding(top = 8.dp)
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
        interactionSource = m_interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = {
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
                                        if (m_interactionSource.collectIsFocusedAsState().value) {
                                          false
                                        } else {
                                          value.isEmpty()
                                        },
                                    interactionSource = m_interactionSource,
                                    colors = colors)
                                .value,
                            RoundedCornerShape(100))
                        .padding(8.dp)
                        .padding(top = 0.dp)
                        .background(
                            backgroundColor(
                                    enabled = enabled,
                                    isError = isError,
                                    isEmpty =
                                        if (m_interactionSource.collectIsFocusedAsState().value) {
                                          true
                                        } else {
                                          value.isEmpty()
                                        },
                                    interactionSource = m_interactionSource,
                                    colors = colors)
                                .value,
                            RoundedCornerShape(100)),
                )
              },
              contentPadding =
                  PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp))
        })
  }
}

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
        animateDpAsState(targetThickness, tween(durationMillis = 150))
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
    interactionSource: InteractionSource,
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
    animateColorAsState(targetValue, tween(durationMillis = 150), "BackgroundColorAnim")
  } else {
    rememberUpdatedState(targetValue)
  }
}

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
    animateColorAsState(targetValue, tween(durationMillis = 150), "IndicatorColorAnim")
  } else {
    rememberUpdatedState(targetValue)
  }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "LightMode",
    widthDp = 672,
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "DarkMode",
    widthDp = 672,
)
@Composable
fun FieldComponentPreview() {
  val txtValue1 = remember { mutableStateOf("FExample TextStr") }
  val txtValue2 = remember { mutableStateOf("OExample TextStr") }
  val txtValue3 = remember { mutableStateOf("FExample TextStr") }
  val txtValue4 = remember { mutableStateOf(TextFieldValue("OExample TextTFV")) }
  val txtValue5 = remember { mutableStateOf(TextFieldValue("FExample TextTFV")) }
  val txtValue6 = remember { mutableStateOf(TextFieldValue("")) }
  val labelText = "Example Label"
  val label: @Composable() (String) -> Unit = { Text(labelText + it) }

  BookSwapAppTheme(false) {
    Box(modifier = Modifier.padding(4.dp)) {
      Row(Modifier.padding(4.dp)) {
        Column(Modifier.padding(16.dp)) {
          FieldComponent(
              value = txtValue1.value,
              { txtValue1.value = it },
              modifier = Modifier.padding(8.dp, 4.dp),
              false,
              true,
              label = { label("Str1") },
          )
          OutlinedTextField(
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
        Column(Modifier.padding(16.dp)) {
          OutlinedTextField(
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
