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
import org.coffey.json.CoffeyRepoPackage
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
            var coffeyRepoPack: CoffeyRepoPackage? = null

            // Get the coffeyPackage manifest.
            val request = HttpGet("$packageUrl/${Properties.jsonPackage}")

            manager.println("Reading ${Properties.jsonPackage}...")

            try {
                val response = client?.execute(request)

                if (response?.statusLine?.statusCode == 404) {
                    throw IOException()
                }

                coffeyRepoPack = Klaxon().parse<CoffeyRepoPackage>(EntityUtils.toString(response?.entity))!!

            } catch (e: IOException) {
                manager.println("Failed to read ${Properties.jsonPackage}, ignoring it.")

            } catch (e: KlaxonException) {

                manager.println("Failed to read/parse ${Properties.jsonPackage}, cannot install package")
                manager.println(e.localizedMessage)

                return CoffeyShell.ERROR_CODES.COMMAND_ERROR.code
            }

            // If the coffey ropository pack isn't here try to
            // manually download executables.

            if (coffeyRepoPack == null) {
                coffeyRepoPack = CoffeyRepoPackage(
                    pack,
                    0.0,
                    null,
                    CoffeyRepoPackage.defaultWindowsInstaller,
                    CoffeyRepoPackage.defaultLinuxInstaller
                )
            }

            manager.println("Installing ${coffeyRepoPack.name} ${coffeyRepoPack.version}")

            if ((System.getProperty("os.name").lowercase()).contains("windows")) {
                installerName = File(coffeyRepoPack.WindowsInstaller)
            } else {
                installerName = File(coffeyRepoPack.LinuxInstaller)
            }

            val installerFile = File("${installerDir.absolutePath}\\$installerName")

            val downloadURL = "${packageUrl}/$installerName"

            manager.println("Downloading ${installerFile.name} from $downloadURL")
            try {
                try {
                    Utils().downloadFromURL(
                        URL(downloadURL),
                        installerFile
                    )

                    manager.println("Download complete.")
                } catch (e: IOException) {
                    throw IOException("Failed to download installer.", e)
                }



                manager.println("Creating ${Properties.jsonPackage} manifest...")

                try {
                    val manifest = File("${installerDir.absolutePath}\\${Properties.jsonPackage}")
                    val coffeyPackage = CoffeyPackage(
                        coffeyRepoPack.name,
                        coffeyRepoPack.version,
                        coffeyRepoPack.description.toString()
                    )

                    manifest.createNewFile()

                    val outputStream = FileOutputStream(manifest)

                    outputStream.write(coffeyPackage.toString().toByteArray())

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

                    if (installerName.name.lowercase().contains(".deb")) {
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

        manager.println("Package installed.")

        return CoffeyShell.ERROR_CODES.NO_ERROR.code
    }

    override fun getDescription(): String {
        return "Install a package, Usage: install <List of packages>"
    }
}