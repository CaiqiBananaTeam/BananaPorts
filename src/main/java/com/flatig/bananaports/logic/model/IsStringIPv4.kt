package com.flatig.bananaports.logic.model

import java.util.regex.Pattern

class IsStringIPv4 {
    // Only IPv4
    fun isIP(str: String?): Boolean {
        if (str != null) {
            if (str.length < 7 || str.length > 15) return false
            val arr = str.split(".").toTypedArray()
            if (arr.size != 4) return false
            for (i in 0..3) {
                if (!(Companion.isNum(arr[i]) && arr[i].isNotEmpty()
                            && arr[i].toInt() <= 255 && arr[i].toInt() >= 0)) {
                    return false
                }
            }
        }

        return true
    }

    companion object {
        fun isNum(str: String?): Boolean {
            if (str != null) {
                val pattern = Pattern.compile("[0-9]*")
                val matcher = pattern.matcher(str)
                return matcher.matches()
            }
            return false
        }
    }
}