package ru.vladamamutova.remotestick.utils

import android.text.InputFilter

class InputUtils {

    companion object {
        /**
         * Возвращает фильтр ввода ip-адреса.
         * @return Array<InputFilter?> фильтр ввода ip-адреса
         */
        fun getIpAddressFilters(): Array<InputFilter?> {
            val filters = arrayOfNulls<InputFilter>(1)
            filters[0] = InputFilter { source, start, end, dest, dstart, dend ->
                if (end > start) {
                    val destTxt = dest.toString()
                    val resultingTxt = (destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend))
                    if (!resultingTxt
                            .matches(Regex("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}" +
                                    "(\\.(\\d{1,3})?)?)?)?)?)?"))
                    ) {
                        return@InputFilter ""
                    } else {
                        val splits = resultingTxt.split("\\.".toRegex())
                            .filter { !it.isBlank() }
                        for (i in splits.indices) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return@InputFilter ""
                            }
                        }
                    }
                }
                null
            }

            return filters
        }
    }
}
