package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.button_double_action.view.*
import ru.vladamamutova.remotestick.R


class ButtonDoubleAction(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    private var mainAction: String?
    private var additionalAction: String?

    init {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.button_double_action, this, true)

        val attrsArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonDoubleAction, 0, 0
        )
        try {
            mainAction = attrsArray.
            getString(R.styleable.ButtonDoubleAction_mainActionText)
            additionalAction = attrsArray.
            getString(R.styleable.ButtonDoubleAction_additionalActionText)

            mainActionText.text = mainAction
            additionalActionText.text = additionalAction
        } finally {
            attrsArray.recycle()
        }
    }

    fun switchActions(isInitial: Boolean) {
        if (isInitial) {
            mainActionText.text = mainAction
            additionalActionText.text = additionalAction
        } else {
            mainActionText.text = additionalAction
            additionalActionText.text = mainAction
        }
    }
}
