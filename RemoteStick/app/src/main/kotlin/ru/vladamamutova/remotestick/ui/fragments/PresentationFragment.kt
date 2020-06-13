package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.fragment_presentation.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient

class PresentationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_presentation, container, false)

        with(view) {
            startButton.setOnClickListener {
                RemoteStickClient.myInstance.presentationPlugin.start()
            }
            continueButton.setOnClickListener {
                RemoteStickClient.myInstance.presentationPlugin.continuePresentation()
            }
            stopButton.setOnClickListener {
                RemoteStickClient.myInstance.presentationPlugin.stop()
            }
            nextSlideButton.setOnClickListener {
                RemoteStickClient.myInstance.presentationPlugin.next()
            }
            previousSlideButton.setOnClickListener {
                RemoteStickClient.myInstance.presentationPlugin.previous()
            }
            pointerButton.isSelected =
                RemoteStickClient.myInstance.presentationPlugin.pointerChecked
            pointerButton.setOnClickListener {
                RemoteStickClient.myInstance.presentationPlugin.togglePointer()
                (it as ImageButton).isSelected =
                    RemoteStickClient.myInstance.presentationPlugin.pointerChecked
            }
        }
        return view
    }
}
