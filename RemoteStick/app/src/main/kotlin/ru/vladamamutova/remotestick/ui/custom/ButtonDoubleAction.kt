package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.button_double_action.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.plugins.SpecialKey
import ru.vladamamutova.remotestick.service.RemoteStickClient


class ButtonDoubleAction(context: Context, attrs: AttributeSet?) :
    RelativeLayout(context, attrs), View.OnClickListener {

    private var mainAction: String?
    private var additionalAction: SpecialKey?

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
            val key = attrsArray.
            getString(R.styleable.ButtonDoubleAction_additionalActionText)
            additionalAction = SpecialKey.fromString(key!!)

            mainActionText.text = mainAction
            additionalActionText.text = additionalAction?.name
        } finally {
            attrsArray.recycle()
        }

        setOnClickListener(this)
    }

    fun switchActions(isInitial: Boolean) {
        if (isInitial) {
            mainActionText.text = mainAction
            additionalActionText.text = additionalAction?.name
        } else {
            mainActionText.text = additionalAction?.name
            additionalActionText.text = mainAction
        }
    }

    override fun onClick(view: View?) {
        if (mainActionText.text == mainAction) {
            if (mainActionText.text == "←") {
                RemoteStickClient.myInstance.keyboardPlugin.onSpecialKeyPress(SpecialKey.BACKSPACE)
            } else {
                mainAction?.let {
                    RemoteStickClient.myInstance.keyboardPlugin.onKeyPress(it[0])
                }
            }
        } else {
            additionalAction?.let {
                RemoteStickClient.myInstance.keyboardPlugin.onSpecialKeyPress(it)
            }
        }
    }
}
