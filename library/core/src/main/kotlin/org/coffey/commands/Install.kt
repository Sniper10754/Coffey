package org.coffey.commands

import com.beust.klaxon.JsonParsingException
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.coffey.CoffeyShell
import org.coffey.Command
import org.coffey.Properties
import org.coffey.cli.CLIManager
import org.coffey.json.CoffeyPackage
import org.coffey.utils.Utils
import java.io.File
import java.io.IOException
import java.net.URL


class Install : Command {
    var manager = CLIManager(javaClass)
    var useRepository = true

    override fun run(args: Array<String>): Int {
        for (pack in args) {
            val packInitial: Char = pack.toCharArray()[0]
            var packageUrl: String
            val installerDir = File("${System.getenv(Properties.installationEnvVar)}/${pack}")
            var installerName: String
            val client: CloseableHttpClient? = HttpClientBuilder.create().build()
            var coffeyPack: CoffeyPackage

            if (useRepository) {
                packageUrl = "${Properties.repositoryUrl}/$packInitial/$pack"
            } else {
                packageUrl = "${Properties.repositoryUrl}/$pack"
            }

            val request = HttpGet("$packageUrl/${Properties.jsonPackage}")

            manager.println("Reading ${Properties.jsonPackage}...")
            try {
                var response = client?.execute(request)

                if (response?.statusLine?.statusCode == 404) {
                    throw IOException()
                }

                coffeyPack = Klaxon().parse<CoffeyPackage>(EntityUtils.toString(response?.entity))!!

            } catch (e: IOException) {
                manager.println("Failed to read ${Properties.jsonPackage}, cannot install package")

                return CoffeyShell.Companion.ERROR_CODES.NO_ERROR.stat
            } catch (e: KlaxonException) {
                manager.println("Failed to parse ${Properties.jsonPackage}, cannot install package")
                manager.println(e.localizedMessage)
                return CoffeyShell.Companion.ERROR_CODES.NO_ERROR.stat
            }

            if ((System.getProperty("os.name").lowercase()).contains("windows")) {
                installerName = coffeyPack.WindowsInstaller
            } else {
                installerName = coffeyPack.LinuxInstaller
            }

            val installerFile = File("${installerDir.absolutePath}/$installerName")

            val downloadURL = "${packageUrl}/$installerName"

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