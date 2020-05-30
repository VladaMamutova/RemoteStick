package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.button_double_key.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.plugins.SpecialKey
import ru.vladamamutova.remotestick.service.RemoteStickClient


class DoubleKeyButton(context: Context, attrs: AttributeSet?) :
    RelativeLayout(context, attrs), View.OnClickListener {

    private var mainKey: String?
    private var additionalKey: SpecialKey?

    init {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.button_double_key, this, true)

        val attrsArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.DoubleKeyButton, 0, 0
        )
        try {
            mainKey = attrsArray.getString(R.styleable.DoubleKeyButton_mainKeyText)
            val key = attrsArray.getString(R.styleable
                .DoubleKeyButton_additionalKeyText)
            additionalKey = SpecialKey.fromString(key!!)

            mainKeyText.text = mainKey
            additionalKeyText.text = additionalKey?.name
        } finally {
            attrsArray.recycle()
        }

        setOnClickListener(this)
    }

    fun switchActions(isInitial: Boolean) {
        if (isInitial) {
            mainKeyText.text = mainKey
            additionalKeyText.text = additionalKey?.name
        } else {
            mainKeyText.text = additionalKey?.name
            additionalKeyText.text = mainKey
        }
    }

    override fun onClick(view: View?) {
        if (mainKeyText.text == mainKey) {
            if (mainKeyText.text == "←") {
                RemoteStickClient.myInstance.keyboardPlugin
                    .onSpecialKeyPress(SpecialKey.BACKSPACE)
            } else {
                mainKey?.let {
                    RemoteStickClient.myInstance.keyboardPlugin.onKeyPress(it[0])
                }
            }
        } else {
            additionalKey?.let {
                RemoteStickClient.myInstance.keyboardPlugin.onSpecialKeyPress(it)
            }
        }
    }
}
