package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import ru.vladamamutova.remotestick.utils.OnBackPressedListener


class KeyboardTextView(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    private var length: Int = 0
    private var onBackPressedListener: OnBackPressedListener? = null

    init {
        isCursorVisible = false
        setTextIsSelectable(false)
        setBackgroundColor(Color.TRANSPARENT)
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        imeOptions = EditorInfo.IME_ACTION_NONE // иконка перевода строки на новую
        // Устанавливаем тип ввода текста. Первые два флага гарантируют,
        // что текст при вводе не будет подчёркиваться.
        inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or // без подсказок при вводе
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or // без автокоррекции
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.d("TAG", "ENTER PRESSED")
                text?.clear()
                length = 0
                return true // событие уже обработано

            } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                Log.d("TAG", "DEL PRESSED")
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        // Обрабатываем нажатие клавиши BACK до того, как оно будет
        // обработано редактором ввода (Input Method Editor), и до того,
        // как будет скрыта клавиатура.
        if (event?.action == KeyEvent.ACTION_DOWN &&
            keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("TAG pre ime", "BACK PRESSED")
            onBackPressedListener?.doBack()
            return true // обработка уже выполнена, клавиатура скрыта не будет
        }

        return super.onKeyPreIme(keyCode, event)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        val currentLength = text.toString().length
        if(currentLength != 0 && currentLength > length) {
            Log.d("TAG", "new char = " + text?.get(start).toString())
            //keyboardPlugin.sendKey(charSequence[before])
        }

        length = text.toString().length
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun selectAll() {
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
    }

    override fun setSelection(index: Int) {
    }

    fun setOnBackPressedListener(onBackPressedListener: OnBackPressedListener){
        this.onBackPressedListener = onBackPressedListener
    }
}