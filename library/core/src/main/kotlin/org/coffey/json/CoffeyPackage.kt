package org.coffey.json

import com.beust.klaxon.Json

data class CoffeyPackage(
    @Json var name: String,
    @Json var version: Double,
    @Json var description: String
) {
    override fun toString(): String {
        return """
            {
                "name": "$name",
                "version": $version,
                "description": "$version"
            }
        """.trimIndent()
    }
}