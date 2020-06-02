package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_special_keyboard.*
import kotlinx.android.synthetic.main.fragment_special_keyboard.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.plugins.SpecialKey
import ru.vladamamutova.remotestick.service.RemoteStickClient

private const val ARG_IS_NUM = "isNum"

class SpecialKeyboardFragment : Fragment() {
    private var isNum: Boolean = true

    override fun onSaveInstanceState(outState: Bundle) {
        // Сохраняем состояние кнопки-переключателя Num-Fn.
        outState.putBoolean(ARG_IS_NUM, isNum)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_special_keyboard,
            container, false
        )

        // Получаем состояние кнопки-переключателя Num-Fn
        // и изменяем состояние кнопок двойного действия:
        // устанавливаем кнопки типа Fn или кнопки с цифрами.
        if (savedInstanceState != null) {
            isNum = savedInstanceState.getBoolean(ARG_IS_NUM)
            view.numFnButton.setIsNumState(isNum)
            view.backspaceF1Button.switchActions(isNum)
            view.divideF2Button.switchActions(isNum)
            view.multiplyF3Button.switchActions(isNum)
            view.sevenF4Button.switchActions(isNum)
            view.eightF5Button.switchActions(isNum)
            view.nineF6Button.switchActions(isNum)
            view.fourF7Button.switchActions(isNum)
            view.fiveF8Button.switchActions(isNum)
            view.sixF9Button.switchActions(isNum)
            view.oneF10Button.switchActions(isNum)
            view.twoF11Button.switchActions(isNum)
            view.threeF12Button.switchActions(isNum)
        }

        // Слушатель для обычных кнопок со специальными клавишами.
        val specialKeyClickListener = View.OnClickListener {
            val specialKey: SpecialKey? = when (it.id) {
                R.id.escButton -> SpecialKey.ESC
                R.id.tabButton -> SpecialKey.TAB
                R.id.insertButton -> SpecialKey.INSERT
                R.id.deleteButton -> SpecialKey.DELETE
                R.id.homeButton -> SpecialKey.HOME
                R.id.endButton -> SpecialKey.END
                R.id.pageUpButton -> SpecialKey.PAGE_UP
                R.id.pageDownButton -> SpecialKey.PAGE_DOWN
                R.id.enterButton -> SpecialKey.ENTER
                R.id.arrowUpButton -> SpecialKey.UP
                R.id.arrowLeftButton -> SpecialKey.LEFT
                R.id.arrowRightButton -> SpecialKey.RIGHT
                R.id.arrowDownButton -> SpecialKey.DOWN
                R.id.printScreenButton -> SpecialKey.PRINT_SCREEN
                else -> null
            }

            specialKey?.let { key ->
                RemoteStickClient.myInstance.keyboardPlugin.onSpecialKeyPress(key)
            }
        }

        // Устанавливаем слушатели для обычных кнопок со специальными клавишами.
        with(view) {
            escButton.setOnClickListener(specialKeyClickListener)
            tabButton.setOnClickListener(specialKeyClickListener)
            insertButton.setOnClickListener(specialKeyClickListener)
            deleteButton.setOnClickListener(specialKeyClickListener)
            homeButton.setOnClickListener(specialKeyClickListener)
            endButton.setOnClickListener(specialKeyClickListener)
            pageUpButton.setOnClickListener(specialKeyClickListener)
            pageDownButton.setOnClickListener(specialKeyClickListener)
            enterButton.setOnClickListener(specialKeyClickListener)
            arrowUpButton.setOnClickListener(specialKeyClickListener)
            arrowLeftButton.setOnClickListener(specialKeyClickListener)
            arrowRightButton.setOnClickListener(specialKeyClickListener)
            arrowDownButton.setOnClickListener(specialKeyClickListener)
            printScreenButton.setOnClickListener(specialKeyClickListener)
        }

        // Устанавливаем слушатели для кнопок, которые отправляют сочетания клавиш.
        view.desktopButton.setOnClickListener {
            RemoteStickClient.myInstance.keyboardPlugin.sendDesktopKeys()
        }
        view.copyButton.setOnClickListener {
            RemoteStickClient.myInstance.keyboardPlugin.sendCopyKeys()
        }
        view.pasteButton.setOnClickListener {
            RemoteStickClient.myInstance.keyboardPlugin.sendPasteKeys()
        }

        // Слушатель для кнопок с символами.
        val symbolClickListener = View.OnClickListener {
            RemoteStickClient.myInstance.keyboardPlugin
                .onKeyPress((it as Button).text[0])
        }

        // Устанавливаем слушатели для кнопок, которые отправляют символы.
        view.minusButton.setOnClickListener(symbolClickListener)
        view.plusButton.setOnClickListener(symbolClickListener)
        view.equalButton.setOnClickListener(symbolClickListener)
        view.zeroButton.setOnClickListener(symbolClickListener)
        view.dotButton.setOnClickListener(symbolClickListener)

        // Устанавливаем слушатель для кнопки Num-Fn.
        view.numFnButton.setOnClickListener {
            switchIsNumState()
        }

        return view
    }

    private fun switchIsNumState() {
        isNum = !isNum
        numFnButton.setIsNumState(isNum)
        backspaceF1Button.switchActions(isNum)
        divideF2Button.switchActions(isNum)
        multiplyF3Button.switchActions(isNum)
        sevenF4Button.switchActions(isNum)
        eightF5Button.switchActions(isNum)
        nineF6Button.switchActions(isNum)
        fourF7Button.switchActions(isNum)
        fiveF8Button.switchActions(isNum)
        sixF9Button.switchActions(isNum)
        oneF10Button.switchActions(isNum)
        twoF11Button.switchActions(isNum)
        threeF12Button.switchActions(isNum)
    }

    private fun ImageButton.setIsNumState(isNum: Boolean) {
        if (isNum) {
            this.setImageDrawable(
                ContextCompat.getDrawable(activity!!, R.drawable.ic_num_fn)
            )
        } else {
            this.setImageDrawable(
                ContextCompat.getDrawable(activity!!, R.drawable.ic_fn_num)
            )
        }
    }
}
