package com.ninthsemester.simplevideoplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Looper.prepare
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import org.jetbrains.anko.AnkoLogger

import android.support.v4.media.AudioAttributesCompat
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import javax.xml.transform.Source


/**
 * Created by sahil-mac on 26/03/18.
 */

enum class SourceType {
    local_audio, local_video, http_audio, http_video, playlist
}

data class PlayerState(var window: Int = 0,
                       var position: Long = 0,
                       var whenReady: Boolean = true,
                       var source: SourceType = SourceType.local_audio)


class PlayerHolder(val context: Context,
                   val playerView: PlayerView,
                   val playerState: PlayerState) : AnkoLogger {

    val player: ExoPlayer

    init {

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributesCompat.Builder()
                .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                .build()

        player = AudioFocusWrapper(
                audioAttributes,
                audioManager,
                ExoPlayerFactory.newSimpleInstance(this.context, DefaultTrackSelector())
                        .apply {

                            //  Bind to the view
                            playerView.player = this

                            //  Load media
                            prepare(buildMediaSource())

                            //  restore state after onResume()/onStart()
                            with(playerState){
                                // Start playback when media has buffered enough
                                // (whenReady is true by default)

                                playWhenReady = whenReady
                                seekTo(window, position)
                            }

                            warn { "SimpleExoPlayer created" }

                        })
    }

    private fun buildMediaSource(): MediaSource {

        val uriList = mutableListOf<MediaSource>()
        MediaLibrary.list.forEach {
            uriList.add(createExtractorMediaSource(it.mediaUri!!))
        }

        return ConcatenatingMediaSource(*uriList.toTypedArray())
    }

    private fun createExtractorMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
                DefaultDataSourceFactory(context, "exoplayer-learning"))
                .createMediaSource(uri)
    }

    fun release() {

        with(player) {
            //  Save State
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenReady = playWhenReady
            }

            //  Release the player
            release()
        }
        warn { "SimpleExoPlayer is released" }
    }
}