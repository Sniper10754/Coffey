package org.coffey.commands

import org.coffey.CoffeyShell
import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager
import java.io.File

class PackageList : Command {
    var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        for (dir in File(Properties.installationEnvVar).listFiles()) {
            if (dir.isDirectory) {
                manager.println("Module")
            }
        }

        return CoffeyShell.ERROR_CODES.NO_ERROR.code
    }

    override fun getName(): String {
        return "list"
    }

    override fun getDescription(): String {
        TODO("Not yet implemented")
    }
}