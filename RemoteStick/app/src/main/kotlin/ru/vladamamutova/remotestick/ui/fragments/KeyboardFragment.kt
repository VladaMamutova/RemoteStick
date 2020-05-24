package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.vladamamutova.remotestick.R

class KeyboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keyboard, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = KeyboardFragment()
    }
}
