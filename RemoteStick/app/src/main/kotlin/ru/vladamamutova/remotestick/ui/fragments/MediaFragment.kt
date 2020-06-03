package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.fragment_media.view.*
import kotlinx.android.synthetic.main.fragment_media.view.playPauseButton

import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient
import ru.vladamamutova.remotestick.utils.VolumeListener

class MediaFragment : Fragment() {
    private var mute: Boolean = false

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
                mute = RemoteStickClient.myInstance.mediaPlugin.getMute()
                muteButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity!!,
                        if (mute) R.drawable.ic_sound_off
                        else R.drawable.ic_sound_on
                    )
                )
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
            muteButton.setOnClickListener {
                mute = RemoteStickClient.myInstance.mediaPlugin.volumeMute()
                (it as ImageButton).setImageDrawable(
                    ContextCompat.getDrawable(
                        activity!!,
                        if (mute) R.drawable.ic_sound_off
                        else R.drawable.ic_sound_on
                    )
                )
            }
        }
        return view
    }
}
