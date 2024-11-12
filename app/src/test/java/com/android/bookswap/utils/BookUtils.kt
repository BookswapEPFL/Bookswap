package com.android.bookswap.utils

import com.android.bookswap.data.DataBook
import org.junit.Assert.assertEquals

/**
 * Assert that two books are identical (uuid can be checked or not)
 *
 * @param expected the expected result
 * @param result the result
 * @param strict true if the two books should have the same UUID
 */
fun assertBookEquals(expected: DataBook, result: DataBook?, strict: Boolean = false) {
  assertEquals(expected, if (strict) result else result?.copy(uuid = expected.uuid))
}
