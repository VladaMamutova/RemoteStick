package ru.vladamamutova.remotestick.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_media.view.*

import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient

class MediaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_media, container, false)

        with(view) {
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
                val mute = RemoteStickClient.myInstance.mediaPlugin.volumeMute()
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
