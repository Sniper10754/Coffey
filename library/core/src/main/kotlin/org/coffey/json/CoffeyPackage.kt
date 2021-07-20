package org.coffey.json

import com.beust.klaxon.Json

data class CoffeyPackage(
    @Json var name: String,
    @Json var version: java.lang.Double,
    @Json var description: String = "",
    @Json var WindowsInstaller: String,
    @Json var LinuxInstaller: String
)