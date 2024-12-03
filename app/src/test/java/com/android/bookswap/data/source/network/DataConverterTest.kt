package com.android.bookswap.data.source.network

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class DataConverterTest {
  val big_long = 1234567890123456780
  val raw_long = "1234567890123456780"
  val str_uuid = "00000000-0000-4000-c000-00000000000"

  @Test
  fun parseRawUUID() {
    val bit_uuid = "{mostSignificantBits=16384, leastSignificantBits=-4611686018427387904}"
    val buuid = DataConverter.parse_raw_UUID(bit_uuid)
    val suuid = DataConverter.parse_raw_UUID(str_uuid + "0")
    assertEquals("Converted UUIDs are not equal!", suuid, buuid)
  }

  @Test
  fun parseRawUUIDList() {
    val bit_uuid_list =
        List(3, { "{mostSignificantBits=16384, leastSignificantBits=${-4611686018427387904+it}}" })
    val str_uuid_list = List(3, { str_uuid + "$it" })
    val buuids = DataConverter.parse_raw_UUID_list(bit_uuid_list.toString())
    val suuids = DataConverter.parse_raw_UUID_list(str_uuid_list.toString())
    assertEquals("Converted UUIDs are not equal!", suuids, buuids)
  }

  @Test
  fun parseRawLong() {
    val parsed_l = DataConverter.parse_raw_long(raw_long)
    assertEquals("Value $raw_long was not parsed correctly!!", big_long, parsed_l)
  }

  @Test
  fun parseRawLongList() {
    val raw_long_list = List(3, { big_long + it })
    val parsed_l = DataConverter.parse_raw_long_list(raw_long_list.toString())
    assertEquals("Value $raw_long_list was not parsed correctly!!", raw_long_list, parsed_l)
  }

  @Test
  fun convertUUIDToString() {
    val uuid = UUID.fromString(str_uuid + "0")
    val convrt_uuid = DataConverter.convert_UUID(uuid)
    assertEquals("UUID $uuid not converted to ${str_uuid}0!", str_uuid+"0", convrt_uuid)
  }

  @Test
  fun convertUUIDListToStringList() {
    val str_uuid_list = List(3, { str_uuid + "$it" })
    val uuid_list = List(3, { UUID.fromString(str_uuid_list[it]) })
    val convrt_uuid_list = DataConverter.convert_UUID_list(uuid_list)
    assertEquals(
        "UUID list $uuid_list not converted to $str_uuid_list!", str_uuid_list, convrt_uuid_list)
  }

  @Test
  fun convertLongToString() {
    val convrt_l = DataConverter.convert_Long(big_long)
    assertEquals("Value $big_long was not converted correctly!", raw_long, convrt_l)
  }

  @Test
  fun convertLongListToStringList() {
    val raw_long_list = List(3, { big_long + it })
    val str_long_list = List(3, { raw_long_list[it].toString() })
    val convrt_long_list = DataConverter.convert_Long_list(raw_long_list)
    assertEquals(
        "Value list $raw_long_list not converted to $str_long_list",
        str_long_list,
        convrt_long_list)
  }
}
