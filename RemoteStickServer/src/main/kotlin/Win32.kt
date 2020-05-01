package main.kotlin

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class Win32 {
    companion object {
        var dllPath: String
        init {
            var file: File? = null
            val resource = "/win32.dll"
            val res = Main::class.java.getResource(resource)
            if (res.protocol == "jar") {
                //try {
                    val input = Main::class.java.getResourceAsStream(resource)
                    file = File.createTempFile("win32", ".dll")
                    val out: OutputStream = FileOutputStream(file!!)
                    var read: Int
                    val bytes = ByteArray(1024)

                    while (input.read(bytes).also { read = it } != -1) {
                        out.write(bytes, 0, read)
                    }
                    out.close()
                    file.deleteOnExit()
               /* } catch (ex: Exception) {
                    throw Exception(ex.message)
                }*/
            } else {
                //this will probably work in your IDE, but not from a JAR
                file = File(res.file)
            }

            if (!file.exists()) {
                throw Exception("File $file not found!")
            } else {
                dllPath = file.absolutePath
                System.load(file.absolutePath)
            }
            //System.load(System.getProperty("user.dir") + ("/lib/win32.dll").toString())
            //System.loadLibrary("win32")
        }
    }

    external fun leftDoubleClick(x: Int, y: Int)
}