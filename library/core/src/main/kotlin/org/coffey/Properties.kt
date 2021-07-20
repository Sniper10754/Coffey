package org.coffey

import org.coffey.commands.*

object Properties {
    const val repositoryUrl = "https://raw.githubusercontent.com/Sniper10754/CoffeyRepository/main"
    const val coffeyHome = "COFFEY_HOME"
    const val installationEnvVar = "COFFEY_PACKAGES"

    var commands = listOf(
        Install(), Uninstall(),
        Help()
    )
}