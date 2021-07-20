package org.coffey.commands

import com.beust.klaxon.Klaxon
import org.coffey.CoffeyShell
import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager
import org.coffey.json.CoffeyPackage
import java.io.File
import java.lang.NullPointerException


class PackageList : Command {
    private var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        try {
            for (dir in File(System.getenv(Properties.installationEnvVar)).listFiles()) {
                if (dir.isDirectory) {
                    val manifest = File("${dir.absolutePath}\\${Properties.jsonPackage}")

                    if (manifest.exists()) {
                        val pack = Klaxon().parse<CoffeyPackage>(manifest)

                        manager.println("name: ${pack?.name} version: ${pack?.version}")
                        manager.println("Description: ${pack?.description}")
                    }
                }
            }
        } catch (e: NullPointerException) {
            manager.println("Failed to list packages.")
        }
        return CoffeyShell.ERROR_CODES.NO_ERROR.code
    }

    override fun getName(): String {
        return "list"
    }

    override fun getDescription(): String {
        return "Show all packages installed."
    }
}