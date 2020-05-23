package main.kotlin

import com.sun.xml.internal.fastinfoset.util.StringArray
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

    external fun leftClick()
    external fun middleClick()
    external fun rightClick()
    external fun leftDown()
    external fun leftUp()
    external fun move(dx: Int, dy: Int)
    external fun sendSymbol(symbol: Char)
    external fun sendSpecialKeys(specialKeys: IntArray)
    external fun sendKeys(specialKeys: IntArray, symbol: Char)
}
