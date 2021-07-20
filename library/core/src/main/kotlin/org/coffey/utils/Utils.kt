package org.coffey.utils

import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

class Utils {
    fun downloadFromURL(url: URL, file: File): File {
        FileUtils.copyURLToFile(url, file)
        return file
    }
}