package com.android.bookswap.data.source.network

import android.util.Log
import java.util.UUID

object DataConverter {
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

  fun parse_raw_UUID(string: String): UUID? {
    return when (string.contains("=")) {
      true -> parse_bits_UUID(string)
      false -> parse_string_UUID(string)
    }
  }

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

  fun parse_raw_long_list(string: String): List<Long> {
    return string.removeSurrounding("[", "]").split(", ").map { parse_raw_long(it) }.filterNotNull()
  }

  fun convert_UUID(uuid: UUID): String {
    return uuid.toString()
  }

  fun convert_UUID_list(uuid_list: List<UUID?>): List<String> {
    return uuid_list.map { it.toString() }
  }

  fun convert_Long(long: Long): String {
    return long.toString()
  }

  fun convert_Long_list(long_list: List<Long>): List<String> {
    return long_list.map { it.toString() }
  }
}
