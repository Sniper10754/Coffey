package org.coffey.json

import com.beust.klaxon.Json

data class CoffeyPackage(
    @Json var version: Int,
    @Json var description: String = "",
    @Json var WindowsInstaller: String,
    @Json var LinuxInstaller: String
)