package com.android.bookswap.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.android.bookswap.ui.theme.ColorVariable

/** Constants for the scrollbar */
const val STANDARD_THICKNESS = 4f
const val MINIMUM_COERCION = 1f
const val END_PADDING = 8f

/**
 * Draw a vertical scrollbar on the right side of the composable that uses this modifier.
 *
 * @param scrollState The scroll state of the composable
 * @param color The color of the scrollbar
 * @param thickness The thickness of the scrollbar
 * @param paddingEnd The padding from the right edge of the composable
 */
fun Modifier.drawVerticalScrollbar(
    scrollState: ScrollState,
    color: Color = ColorVariable.Accent,
    thickness: Float = STANDARD_THICKNESS,
    paddingEnd: Float = END_PADDING
): Modifier =
    this.then(
        Modifier.drawWithContent {
          drawContent()

          // Only draw the scroll bar if the content is scrollable
          if (scrollState.maxValue > 0) {
            // Height of the scrollbar (fixed proportion of the total height)
            val scrollBarHeight = size.height * 0.1f
            // Scrollbar position based on scroll progress
            val scrollBarY =
                (size.height - scrollBarHeight) *
                    (scrollState.value /
                        scrollState.maxValue.toFloat().coerceAtLeast(MINIMUM_COERCION))

            drawLine(
                color = color,
                start = Offset(size.width - paddingEnd, scrollBarY),
                end = Offset(size.width - paddingEnd, scrollBarY + scrollBarHeight),
                strokeWidth = thickness,
                cap = StrokeCap.Round)
          }
        })
