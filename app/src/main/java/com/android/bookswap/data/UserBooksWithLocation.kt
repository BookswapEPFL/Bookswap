package com.android.bookswap.data

import java.util.UUID

/**
 * Represents a user's book collection along with their geographical location.
 *
 * @property userUUID Unique identifier for the user.
 * @property longitude The user's longitude coordinate.
 * @property latitude The user's latitude coordinate.
 * @property books List of books associated with the user.
 */
data class UserBooksWithLocation(
    val userUUID: UUID,
    val longitude: Double,
    val latitude: Double,
    val books: List<DataBook>
)
