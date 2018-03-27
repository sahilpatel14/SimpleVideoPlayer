package com.ninthsemester.simplevideoplayer

import android.annotation.TargetApi
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.media.AudioAttributesCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

/**
 * Created by sahil-mac on 27/03/18.
 */


class AudioFocusWrapper(private val audioAttributes: AudioAttributesCompat,
                        private val audioManager: AudioManager,
                        private val player: SimpleExoPlayer) : ExoPlayer by player, AnkoLogger {

    private var shouldPlayWhenReady = false

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->

        when (focusChange) {

            AudioManager.AUDIOFOCUS_GAIN -> {

                if (shouldPlayWhenReady && player.playWhenReady) {
                    player.playWhenReady = true
                    player.volume = MEDIA_VOLUME_DEFAULT
                }
                shouldPlayWhenReady = false
            }

            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {

                if (player.playWhenReady) {
                    player.volume = MEDIA_VOLUME_DUCK
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {

                shouldPlayWhenReady = player.playWhenReady
                player.playWhenReady = false
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                abandonAudioFocus()
            }
        }
    }

    @get:RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusRequest by lazy { buildFocusRequest() }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        if (playWhenReady) requestAudioFocus() else abandonAudioFocus()
    }

    private fun requestAudioFocus() {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestAudioFocusOreo()
        } else {
            @Suppress("deprecation")
            audioManager.requestAudioFocus(audioFocusListener,
                    audioAttributes.legacyStreamType,
                    AudioManager.AUDIOFOCUS_GAIN)
        }

        // Call the listener whenever focus is granted - even the first time!
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            shouldPlayWhenReady = true
            audioFocusListener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN)
        } else {
            warn { "Playback not started: Audio focus request denied" }
        }
    }

    private fun abandonAudioFocus() {
        player.playWhenReady = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            abandonAudioFocusOreo()
        } else {
            @Suppress("deprecation")
            audioManager.abandonAudioFocus (audioFocusListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocusOreo(): Int = audioManager.requestAudioFocus(audioFocusRequest)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun abandonAudioFocusOreo(): Int = audioManager.abandonAudioFocusRequest(audioFocusRequest)

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildFocusRequest() : AudioFocusRequest =
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes.unwrap() as? android.media.AudioAttributes)
                    .setOnAudioFocusChangeListener  (audioFocusListener)
                    .build()
}

private const val MEDIA_VOLUME_DEFAULT = 1.0f
private const val MEDIA_VOLUME_DUCK = 0.2f