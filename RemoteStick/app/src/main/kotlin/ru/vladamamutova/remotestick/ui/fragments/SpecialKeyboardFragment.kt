package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_special_keyboard.*
import kotlinx.android.synthetic.main.fragment_special_keyboard.view.*
import ru.vladamamutova.remotestick.R

private const val ARG_IS_NUM = "isNum"

class SpecialKeyboardFragment : Fragment() {
    private var isNum: Boolean = true

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(ARG_IS_NUM, isNum)
        super.onSaveInstanceState(outState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_special_keyboard,
            container, false)

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

    companion object {
        @JvmStatic
        fun newInstance() = SpecialKeyboardFragment()
    }
}
