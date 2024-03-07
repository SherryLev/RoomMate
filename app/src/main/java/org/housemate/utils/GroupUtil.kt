package org.housemate.utils

import java.util.UUID
object GroupUtil {
    fun generateUniqueCode(): String {
        return UUID.randomUUID().toString().take(8)
    }
}