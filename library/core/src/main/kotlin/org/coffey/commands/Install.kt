package org.coffey.commands

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
import org.coffey.commands.Install.PathProvider
import org.coffey.json.CoffeyPackage
import org.coffey.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


class Install : Command {
    fun interface PathProvider {
        fun providePath(pack: String): String
    }

    var provider = PathProvider {
        "${Properties.repositoryUrl}/${it.toCharArray()[0]}/$it"
    }
        get() = field
        set(value) {
            field = value
        }

    private var manager = CLIManager(javaClass)

    override fun run(args: Array<String>): Int {
        for (pack in args) {
            val packageUrl = provider.providePath(pack)
            val installerDir = File("${System.getenv(Properties.installationEnvVar)}\\${pack}")
            var installerName: File
            val client: CloseableHttpClient? = HttpClientBuilder.create().build()
            var coffeyPack: CoffeyPackage

            // Get the coffeyPackage manifest.
            val request = HttpGet("$packageUrl/${Properties.jsonPackage}")

            manager.println("Reading ${Properties.jsonPackage}...")

            try {
                val response = client?.execute(request)

                if (response?.statusLine?.statusCode == 404) {
                    throw IOException()
                }

                coffeyPack = Klaxon().parse<CoffeyPackage>(EntityUtils.toString(response?.entity))!!

            } catch (e: IOException) {
                manager.println("Failed to read ${Properties.jsonPackage}, cannot install package")

                return CoffeyShell.ERROR_CODES.COMMAND_ERROR.code
            } catch (e: KlaxonException) {

                manager.println("Failed to parse ${Properties.jsonPackage}, cannot install package")
                manager.println(e.localizedMessage)

                return CoffeyShell.ERROR_CODES.COMMAND_ERROR.code
            }

            // Get installer

            manager.println("Installing ${coffeyPack.name} ${coffeyPack.version}")

            if ((System.getProperty("os.name").lowercase()).contains("windows")) {
                installerName = File(coffeyPack.WindowsInstaller)
            } else {
                installerName = File(coffeyPack.LinuxInstaller)
            }

            val installerFile = File("${installerDir.absolutePath}\\$installerName")

            val downloadURL = "${packageUrl}/$installerName"

            manager.println("Downloading ${installerFile.name} from $downloadURL")
            try {
                Utils().downloadFromURL(
                    URL(downloadURL),
                    installerFile
                )

                manager.println("Download complete. \n")

                manager.println("Creating ${Properties.jsonPackage} manifest...")

                try {
                    val manifest = File("${installerDir.absolutePath}\\${Properties.jsonPackage}")

                    manifest.createNewFile()

                    val manifestJson = """
                    {
                        "name": "${coffeyPack.name}",
                        "version": ${coffeyPack.version},
                        "description": "${coffeyPack.description}",
                        
                        "WindowsInstaller": "${coffeyPack.WindowsInstaller}",
                        "LinuxInstaller": "${coffeyPack.LinuxInstaller}"
                    }
                    """.trimIndent()

                    var outputStream = FileOutputStream(manifest)

                    outputStream.write(manifestJson.toByteArray())

                    manager.println("Creation of manifest completed.")

                } catch (e: IOException) {
                    manager.println("Exception was thrown during creation of manifest, ignoring it.")
                    manager.println(e)
                    return CoffeyShell.ERROR_CODES.COMMAND_ERROR.code
                }



                manager.println("Executing $installerFile...")

                try {

                    // Run installer
                    val builder = ProcessBuilder()

                    if (installerName.name.lowercase().contains(".sh")) {
                        builder.command("bash ", "-c ", installerName.name)
                    } else {
                        builder.command("cmd ", "/c ", installerName.name)
                    }

                    builder
                        .directory(installerDir)
                        .inheritIO()

                    builder.start()


                } catch (e: IOException) {
                    manager.println("Failed to execute installer")
                    manager.println("Error: $e")
                    manager.println("path: ${installerFile.absolutePath}")
                    return CoffeyShell.ERROR_CODES.COMMAND_ERROR.code
                }

            } catch (e: IOException) {
                manager.println("During the download was raised an exception")
                manager.println(e)

                return CoffeyShell.ERROR_CODES.COMMAND_ERROR.code
            }
        }

        return CoffeyShell.ERROR_CODES.NO_ERROR.code
    }

    override fun getDescription(): String {
        return "Install a package, Usage: install <List of packages>"
    }
}