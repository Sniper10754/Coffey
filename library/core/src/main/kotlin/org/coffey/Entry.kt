package org.coffey

import org.coffey.cli.CLIManager
import java.io.PrintStream
import kotlin.system.exitProcess

class Entry {
    val manager = CLIManager(javaClass)

    fun main(args: Array<String>): Int {
        val env = System.getenv()

        val coffeyHome: String? = env[Properties.coffeyHome]
        val installPath: String? = env[Properties.installationEnvVar]

        if ((installPath == null) or (coffeyHome == null)) {
            if (installPath == null) {
                manager.println("Environment variable \"${Properties.installationEnvVar}\" is missing.")
            }
            if (coffeyHome == null) {
                manager.println("Environment variable \"${Properties.coffeyHome}\" is missing.")
            }

            exitProcess(1)
        }

        if (args.size >= 1) {
            val status = CoffeyShell.eval(args)

        } else {
            CoffeyShell.eval(arrayOf("help"))
        }

        return 0
    }
}