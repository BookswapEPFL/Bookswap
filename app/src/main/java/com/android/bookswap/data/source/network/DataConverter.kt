package com.android.bookswap.data.source.network

import android.util.Log
import java.util.UUID

object DataConverter {
  private fun parse_string_UUID(string: String): UUID? {
	return try {
	  UUID.fromString(string)
	} catch (e:Exception){
	  Log.e("BookSwap_FirestoreDataConverter","DataConverter.parse_string_UUID: Error converting \"$string\" to UUID, the provided string is not in the correct format")
	  null
	}
  }
  
  private fun parse_bits_UUID(string: String): UUID? {
	val uid = string.removeSurrounding("{","}").let{sid ->
	  buildMap<String, Long>{
		sid.split(", ").let{comps ->// List<String> ["lsb=Long", "msb=Long"]
		  comps.map{half ->// String "name=Long"
			half.split("=").let{part ->//List<String> ["name", "Long"]
			  put(part.first(), part.last().toLong())
			}
		  }
		}
	  }
	}// List<Map<String, Long>>
	return try {
	  val msb = uid["mostSignificantBits"]!!
	  val lsb = uid["leastSignificantBits"]!!
	  UUID(msb,lsb)
	} catch (e:Exception){
	  Log.e("BookSwap_FirestoreDataConverter","DataConverter.parse_bits_UUID: Error converting \"$string\" to UUID, the provided string cannot be parsed as a map {mostSignificantBits=<Long>, leastSignificantBits=<Long>}")
	  null
	}
  }
  
  
  fun parse_raw_UUID(string: String): UUID? {
	return when(string.contains("=")){
	  true -> parse_bits_UUID(string)
	  false -> parse_string_UUID(string)
	}
  }
  
  fun parse_raw_UUID_list(string: String){
	var temp_string = string.removeSurrounding("[","]")
  }
}