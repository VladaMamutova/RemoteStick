package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.fragment_media.view.*
import kotlinx.android.synthetic.main.fragment_media.view.playPauseButton

import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient
import ru.vladamamutova.remotestick.utils.VolumeListener

class MediaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_media, container, false)
        val volumeListener = object : VolumeListener {
            override fun beforeVolumeChanged(value: Int) {
                playPauseButton.visibility = View.GONE
            }

            override fun volumeChanged(value: Int, step: Int) {
                RemoteStickClient.myInstance.mediaPlugin.changeVolume(value, step)
                muteButton.isSelected =
                    RemoteStickClient.myInstance.mediaPlugin.muteChecked
            }

            override fun afterVolumeChanged(value: Int) {
                playPauseButton.visibility = View.VISIBLE
            }

        }

        with(view) {
            volumeControl.setVolumeListener(volumeListener)

            playPauseButton.setOnClickListener {
                RemoteStickClient.myInstance.mediaPlugin.playPause()
            }
            nextTrackButton.setOnClickListener {
                RemoteStickClient.myInstance.mediaPlugin.nextTrack()
            }
            previousTrackButton.setOnClickListener {
                RemoteStickClient.myInstance.mediaPlugin.previousTrack()
            }
            stopButton.setOnClickListener {
                RemoteStickClient.myInstance.mediaPlugin.stop()
            }
            muteButton.isSelected =
                RemoteStickClient.myInstance.mediaPlugin.muteChecked
            muteButton.setOnClickListener {
                RemoteStickClient.myInstance.mediaPlugin.toggleMute()
                (it as ImageButton).isSelected =
                    RemoteStickClient.myInstance.mediaPlugin.muteChecked
            }
        }
        return view
    }
}
