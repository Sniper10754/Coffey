package org.coffey.commands

import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager

class Help : Command {
    var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        manager.println("Command list\n")


        for (command in Properties.commands) {
            manager.println("Command name: ${command.getName()}")
            manager.println("Command description: ${command.getDescription()}")
            println()
        }

        return 1
    }

    override fun getDescription(): String {
        return "Show help message"
    }
}