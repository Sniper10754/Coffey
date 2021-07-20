package org.coffey.commands

import org.coffey.CoffeyShell
import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager
import org.coffey.utils.Utils
import java.io.File
import java.net.URL

class Install : Command {
    var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        for (pack in args) {
            val packInitial: Char = pack.toCharArray()[0]
            val downloadURL = "${Properties.repositoryUrl}/${packInitial}/${pack}/install.sh"
            val installerName = "installer.sh"

            manager.println("Downloading $installerName from $downloadURL")
            Utils().downloadFromURL(
                URL(downloadURL),
                File("${System.getenv(Properties.installationEnvVar)}/${pack}/$installerName")
            )
        }

        return CoffeyShell.Companion.ERROR_CODES.NO_ERROR.stat
    }

    override fun getDescription(): String {
        return "Install a package"
    }
}