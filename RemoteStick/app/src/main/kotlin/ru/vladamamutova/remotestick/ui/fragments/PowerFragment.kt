package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.fragment_power.view.*
import kotlinx.android.synthetic.main.fragment_presentation.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient

class PowerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_power, container, false)

        with(view) {
            sleepButton.setOnClickListener {
                RemoteStickClient.myInstance.powerPlugin.sleep()
            }
            hibernateButton.setOnClickListener {
                RemoteStickClient.myInstance.powerPlugin.hibernate()
            }
            lockButton.setOnClickListener {
                RemoteStickClient.myInstance.powerPlugin.lock()
            }
            restartButton.setOnClickListener {
                RemoteStickClient.myInstance.powerPlugin.restart()
            }
            powerOffButton.setOnClickListener {
                RemoteStickClient.myInstance.powerPlugin.powerOff()
            }
        }
        return view
    }
}
