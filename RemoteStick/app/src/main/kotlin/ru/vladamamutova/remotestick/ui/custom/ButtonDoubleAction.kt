package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.button_double_action.view.*
import ru.vladamamutova.remotestick.R


class ButtonDoubleAction(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    init {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.button_double_action, this, true)

        val attrsArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonDoubleAction, 0, 0
        )
        try {
            mainActionText.text = attrsArray.
            getString(R.styleable.ButtonDoubleAction_mainActionText)
            additionActionText.text = attrsArray.
            getString(R.styleable.ButtonDoubleAction_additionActionText)
        } finally {
            attrsArray.recycle()
        }
    }
}
