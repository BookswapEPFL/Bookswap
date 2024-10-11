package com.android.bookswap.utils

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JSONUtils {
  private lateinit var jsonObj: JSONObject

  @Before
  fun setup() {
    val json =
        """
          {
            "test": {
                "test1": "works"
            },
            "array": [
                "works2"
            ]
          }
        """
            .trimIndent()

    jsonObj = JSONObject(json)
  }

  @Test
  fun getJSONObjectOrNull() {
    assertEquals("works", jsonObj.getJSONObjectOrNull("test")!!.getString("test1"))
    assertNull(jsonObj.getJSONObjectOrNull("not-exist"))
  }

  @Test
  fun getJSONArrayOrNull() {
    assertEquals("works2", jsonObj.getJSONArrayOrNull("array")!!.get(0))
    assertNull(jsonObj.getJSONArrayOrNull("not-exist"))
  }

  @Test
  fun getStringOrNull() {
    // Test for JSONObject
    assertEquals("works", jsonObj.getJSONObject("test").getStringOrNull("test1"))
    assertNull(jsonObj.getJSONObject("test").getStringOrNull("not-exist"))

    // Test for JSONArray
    assertEquals("works2", jsonObj.getJSONArray("array").getString(0))
    assertNull(jsonObj.getJSONArray("array").getStringOrNull(2))
  }
}
