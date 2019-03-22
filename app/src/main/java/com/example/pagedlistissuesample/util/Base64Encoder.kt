package com.example.pagedlistissuesample.util

import android.util.Base64

/**
 * @author chetansachdeva
 */

object Base64Encoder {
    /**
     * encodes a [DataSourceKey]
     * returns a cursor
     */
    fun encode(epochMilliseconds: Long, itemId: String): String{
        val stringToEncode = (epochMilliseconds.toString() +"#" + itemId).toByteArray()
        return Base64.encodeToString(stringToEncode, 0, stringToEncode.size, Base64.NO_WRAP)
    }


    /**
     * decodes a cursor
     * returns a [DataSourceKey.itemId]
     */
    fun decode(cursor: String): String {
        val split = String(Base64.decode(cursor, Base64.NO_WRAP)).split("#")
        return split.last()
    }
}