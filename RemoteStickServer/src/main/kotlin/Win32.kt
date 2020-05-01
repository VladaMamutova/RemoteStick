package main.kotlin

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Paths


class Win32 {
    companion object {
        init {
            val dllPath = Paths.get(
                System.getProperty("user.dir"),
                "lib", "win32.dll"
            ).toString()
            if (!File(dllPath).exists()) {
                throw Exception("Файл \"$dllPath\" не найден.")
            } else {
                System.load(dllPath)
            }
        }
    }

    external fun doubleClick()
    external fun leftClick()
    external fun rightClick()
    external fun moveMouse(x: Int, y: Int)
}