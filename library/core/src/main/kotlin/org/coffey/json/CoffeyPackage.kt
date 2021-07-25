package org.coffey.json

data class CoffeyPackage(
    var name: String,
    var version: Double,
    var description: String
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