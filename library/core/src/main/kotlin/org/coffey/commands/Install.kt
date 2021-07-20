package org.coffey.commands

import org.coffey.CoffeyShell
import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager
import org.coffey.utils.Utils
import java.io.File
import java.io.IOException
import java.net.URL

class Install : Command {
    var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        for (pack in args) {
            val packInitial: Char = pack.toCharArray()[0]
            var installerName: String

            if ((System.getProperty("os.name").lowercase()).contains("windows")) {
                installerName = "installer.cmd"
            } else {
                installerName = "installer.sh"
            }

            val installerDir = File("${System.getenv(Properties.installationEnvVar)}/${pack}")
            val installerFile = File("${installerDir.absolutePath}/$installerName")
            val downloadURL = "${Properties.repositoryUrl}/${packInitial}/${pack}/$installerName"

            manager.println("Downloading $installerFile from $downloadURL")
            try {
                Utils().downloadFromURL(
                    URL(downloadURL),
                    installerFile
                )

                manager.println("Download complete. \n")

                manager.println("Executing $installerFile...")

                try {

                    val builder = ProcessBuilder()

                    if (installerName.lowercase().contains(".sh")) {
                        builder.command("bash ", "-c ", installerName)
                    } else {
                        builder.command("cmd ", "/c ", installerName)
                    }

                    builder
                        .directory(installerDir)
                        .inheritIO()

                    builder.start()
                } catch (e: IOException) {
                    manager.println("Failed to execute installer")
                    manager.println("Error: $e")
                    manager.println("path: ${installerFile.absolutePath}")
                }

            } catch (e: IOException) {
                manager.println("During the download was raised an exception")
                manager.println(e)
            }
        }

        return CoffeyShell.Companion.ERROR_CODES.NO_ERROR.stat
    }

    override fun getDescription(): String {
        return "Install a package, Usage: install <List of packages>"
    }
}