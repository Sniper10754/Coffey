package org.coffey.commands

import com.beust.klaxon.Klaxon
import org.coffey.Command
import org.coffey.Properties
import org.coffey.json.CoffeyPackage
import java.io.File

class Info : Command {
    var packagesDirectory = File(System.getenv(Properties.installationEnvVar))

    override fun run(args: Array<String>): Int {
        args.forEach {
            packagesDirectory.listFiles().forEach {

                if (it.isDirectory) {
                    val coffeyPackageFile = File("${it.absolutePath}\\${Properties.jsonPackage}")
                    if (coffeyPackageFile.exists()) {
                        var pack = Klaxon().parse<CoffeyPackage>(coffeyPackageFile)

                        println("""
Package: ${pack?.name}
  Version: ${pack?.version}
  Description: ${pack?.description}""")
                    }
                }
            }
        }

        return 0;
    }

    override fun getDescription() = "Get info of a specific package."
}