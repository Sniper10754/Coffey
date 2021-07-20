package org.coffey.commands

import org.coffey.CoffeyShell
import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager
import java.io.File
import java.io.IOException

class Uninstall : Command {
    val manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        val installingDir = File(System.getenv()[Properties.installationEnvVar])

        for (pack in args) {
            try {
                for (dir in installingDir.listFiles()) {
                    if (dir.name == pack) {
                        if (dir.isDirectory) {
                            manager.println("Deleting: ${dir.absolutePath}")

                            manager.println("Deleted: ${dir.absolutePath}")
                        }
                    }
                }
            } catch (e: Throwable) {
                manager.println("During the uninstall was thrown an exception")
                manager.println(e.localizedMessage)
            }
        }

        return CoffeyShell.ERROR_CODES.NO_ERROR.code
    }

    override fun getDescription(): String {
        return "Uninstall a series of packages, Usage: uninstall <List of packages>"
    }
}