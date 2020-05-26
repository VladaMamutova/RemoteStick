package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import ru.vladamamutova.remotestick.plugins.SpecialKey
import ru.vladamamutova.remotestick.utils.KeyboardListener
import ru.vladamamutova.remotestick.utils.OnBackPressedListener


class KeyboardTextView(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    private var length: Int = 0
    private var keyboardListener: KeyboardListener? = null
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
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD  // без автокоррекции
        setHorizontallyScrolling(false) // текст переносится на новую строку
        maxLines = 10
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                keyboardListener?.sendSpecialKey(SpecialKey.ENTER)
                text?.clear()
                length = 0
                return true // событие уже обработано

            } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                keyboardListener?.sendSpecialKey(SpecialKey.BACKSPACE)
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
            onBackPressedListener?.doBack()
            return true // обработка уже выполнена, клавиатура скрыта не будет
        }

        return super.onKeyPreIme(keyCode, event)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        val currentLength = text.toString().length
        if (currentLength != 0 && currentLength > length) {
            text?.get(start)?.let { keyboardListener?.sendSymbol(it) }
        }

        length = currentLength
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Любое касание или удерживание текста не обрабатываем,
        // таким образом, отключения выделение текста и перемещение курсора.
        // Возвращаем false, чтобы показать, что событие не было обработано,
        // и было передано в другие view (в нашем случае - в TouchpadView).
        return false
    }

    fun setKeyboardListener(keyboardListener: KeyboardListener){
        this.keyboardListener = keyboardListener
    }

    fun setOnBackPressedListener(onBackPressedListener: OnBackPressedListener){
        this.onBackPressedListener = onBackPressedListener
    }
}