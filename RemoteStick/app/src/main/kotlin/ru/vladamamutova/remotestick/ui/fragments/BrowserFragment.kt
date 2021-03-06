package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.fragment_browser.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient
import kotlin.math.min

class BrowserFragment(private val buttonHeight: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_browser, container, false)

        with(view) {
            for (index in 0 until (this as ViewGroup).childCount) {
                (this.getChildAt(index) as ImageButton).minimumHeight = buttonHeight
            }

            backButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.back()
            }
            forwardButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.forward()
            }
            homeButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.home()
            }
            refreshButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.refresh()
            }
            previousTabButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.previousTab()
            }
            nextTabButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.nextTab()
            }
            zoomInButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.zoomIn()
            }
            zoomOutButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.zoomOut()
            }
            fullScreenButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.fullScreen()
            }
            newTabButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.newTab()
            }
            closeTabButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.closeTab()
            }
            lastTabButton.setOnClickListener {
                RemoteStickClient.myInstance.browserPlugin.lastTab()
            }
        }
        return view
    }
}
