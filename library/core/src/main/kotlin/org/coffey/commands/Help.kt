package org.coffey.commands

import org.coffey.Command
import org.coffey.cli.CLIManager

class Help : Command {
    var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        manager.println("Help")

        return 1
    }

    override fun getDescription(): String {
        return "Show help message"
    }
}