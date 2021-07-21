package org.coffey.json

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon

data class CoffeyRepoPackage(
    @Json var name: String,
    @Json var version: Double,
    @Json var description: String? = "",
    @Json var WindowsInstaller: String,
    @Json var LinuxInstaller: String
) {
    companion object CoffeyRepoPackage {
        var defaultWindowsInstaller = "installer.exe"
        var defaultLinuxInstaller = "installer.deb"
    }

    override fun toString(): String {
        return Klaxon().toJsonString(this)
    }
}