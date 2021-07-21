package org.coffey

import org.coffey.commands.*

object Properties {
    const val repositoryUrl = "https://raw.githubusercontent.com/Sniper10754/CoffeyRepository/main"
    const val coffeyHome = "COFFEY_HOME"
    const val installationEnvVar = "COFFEY_PACKAGES"
    const val jsonPackage = "coffeypackage.json"

    val packageList = PackageList()
    val install = Install()
    val uninstall = Uninstall()
    val help = Help()

    var commands = listOf(
        packageList,
        install,
        help,
        uninstall
    )
}