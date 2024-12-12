package com.android.bookswap.data.source.network

import android.util.Log
import java.util.UUID

object DataConverter {
  /**
   * Converts a string to a UUID.
   *
   * @param string The string to be converted to a UUID.
   * @return The UUID if the string is in the correct format, or null if the string is not in the
   *   correct format.
   */
  private fun parse_string_UUID(string: String): UUID? {
    return try {
      UUID.fromString(string)
    } catch (e: Exception) {
      Log.e(
          "BookSwap_FirestoreDataConverter",
          "DataConverter.parse_string_UUID: Error converting \"$string\" to UUID, the provided string is not in the correct format")
      null
    }
  }
  /**
   * Parses a string representation of a UUID with most and least significant bits.
   *
   * @param string The string to be parsed, expected in the format {mostSignificantBits=<Long>,
   *   leastSignificantBits=<Long>}.
   * @return The UUID if the string is in the correct format, or null if the string cannot be
   *   parsed.
   */
  private fun parse_bits_UUID(string: String): UUID? {
    val uid =
        string.removePrefix("{").removeSuffix("}").let { sid ->
          buildMap<String, Long?> {
            sid.split(", ").let { comps -> // List<String> ["lsb=Long", "msb=Long"]
              comps.map { half -> // String "name=Long"
                half.split("=").let { part -> // List<String> ["name", "Long"]
                  put(part.first(), parse_raw_long(part.last()))
                }
              }
            }
          }
        } // List<Map<String, Long>>
    return try {
      val msb = uid["mostSignificantBits"]!!
      val lsb = uid["leastSignificantBits"]!!
      UUID(msb, lsb)
    } catch (e: Exception) {
      Log.e(
          "BookSwap_FirestoreDataConverter",
          "DataConverter.parse_bits_UUID: Error converting \"$string\" to UUID, the provided string cannot be parsed as a map {mostSignificantBits=<Long>, leastSignificantBits=<Long>}")
      null
    }
  }
  /**
   * Parses a string representation of a UUID.
   *
   * @param string The string to be parsed.
   * @return The UUID if the string is in the correct format, or null if the string cannot be
   *   parsed.
   */
  fun parse_raw_UUID(string: String): UUID? {
    return when (string.contains("=")) {
      true -> parse_bits_UUID(string)
      false -> parse_string_UUID(string)
    }
  }
  /**
   * Parses a string representation of a list of UUIDs.
   *
   * @param string The string to be parsed, expected in the format [UUID1, UUID2, ...] or
   *   [{mostSignificantBits=<Long>, leastSignificantBits=<Long>}, ...].
   * @return A list of UUIDs if the string is in the correct format, or null if the string cannot be
   *   parsed.
   */
  fun parse_raw_UUID_list(string: String): List<UUID?> {
    val temp_string = string.removeSurrounding("[", "]").removeSurrounding("{", "}")
    val delimiter =
        if (temp_string.contains("=")) {
          "}, "
        } else {
          ", "
        }
    return temp_string.split(delimiter).map { parse_raw_UUID(it) }
  }
  /**
   * Parses a string representation of a Long.
   *
   * @param string The string to be parsed.
   * @return The Long value if the string is in the correct format, or 0 if the string cannot be
   *   parsed.
   * @throws Exception if the string cannot be parsed as a Long.
   */
  fun parse_raw_long(string: String): Long {
    return try {
      string.toLong()
    } catch (e: Exception) {
      Log.e(
          "BookSwap_FirestoreDataConverter",
          "DataConverter.parse_raw_long: Error converting \"$string\" to Long, the provided string cannot be parsed as a Long")
      0L.also { throw e }
    }
  }
  /**
   * Parses a string representation of a list of Long values.
   *
   * @param string The string to be parsed, expected in the format [Long1, Long2, ...].
   * @return A list of Long values if the string is in the correct format, or an empty list if the
   *   string cannot be parsed.
   */
  fun parse_raw_long_list(string: String): List<Long> {
    return string.removeSurrounding("[", "]").split(", ").map { parse_raw_long(it) }.filterNotNull()
  }
  /**
   * Converts a UUID to its string representation.
   *
   * @param uuid The UUID to be converted.
   * @return The string representation of the UUID.
   */
  fun convert_UUID(uuid: UUID): String {
    return uuid.toString()
  }
  /**
   * Converts a list of UUIDs to their string representations.
   *
   * @param uuid_list The list of UUIDs to be converted.
   * @return A list of string representations of the UUIDs.
   */
  fun convert_UUID_list(uuid_list: List<UUID?>): List<String> {
    return uuid_list.map { it.toString() }
  }
  /**
   * Converts a Long to its string representation.
   *
   * @param long The Long to be converted.
   * @return The string representation of the Long.
   */
  fun convert_Long(long: Long): String {
    return long.toString()
  }
  /**
   * Converts a list of Long values to their string representations.
   *
   * @param long_list The list of Long values to be converted.
   * @return A list of string representations of the Long values.
   */
  fun convert_Long_list(long_list: List<Long>): List<String> {
    return long_list.map { it.toString() }
  }
}
