package main.kotlin

import java.io.File
import java.nio.file.Paths


class Win32 {
    companion object {
        var isVolumeValid = false

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

    /**
     * Инициализирует системное устройство звука и возращает true, если оно
     * успешно создано, и false - если возникла ошибка (версия Windows < Vista).
     */
    private external fun init(): Boolean

    /**
     * Освобождает системное устройство звука, если оно было успешно создано.
     */
    external fun release()

    /**
     * Инициализирует системное устройство звука, которое будет использоваться
     * для получения текущей громкости и состояния беззвучного режима.
     * Устанавливает флаг isVolumeValid, показывающий, можно ли успешно
     * использовать функцию getVolumeAndMute().
     */
    fun initialize() {
        isVolumeValid = init()
    }

    external fun leftClick()
    external fun middleClick()
    external fun rightClick()
    external fun leftDown()
    external fun leftUp()
    external fun move(dx: Int, dy: Int)
    external fun scroll(dy: Int)

    external fun sendSymbol(symbol: Char)
    external fun sendSpecialKeys(specialKeys: IntArray)
    external fun sendKeys(specialKeys: IntArray, symbol: Char)

    external fun changeVolume(volumeDifference: Int)
    external fun volumeMute()

    /**
     * Получает массив с настройками звука. Первый элемент содержит значение
     * громкости, а второй элемент показывает, отключён ли звук (0 - включён,
     * 1 - отключён). Если isVolumeValid = false, то оба значения равны -1.
     */
    external fun getVolumeAndMute(): IntArray

    external fun playPause()
    external fun nextTrack()
    external fun prevTrack()
    external fun stop()
}
