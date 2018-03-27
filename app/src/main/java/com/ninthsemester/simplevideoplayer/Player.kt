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
import javax.xml.transform.Source


/**
 * Created by sahil-mac on 26/03/18.
 */

val mediaMap = mapOf<SourceType, Uri>(
        SourceType.local_audio to Uri.parse("asset:///sample_audio_file.mp3"),
        SourceType.local_video to Uri.parse("asset:///ed_hd.mp4")
)

enum class SourceType {
    local_audio, local_video, http_audio, http_video, playlist
}

data class PlayerState(var window: Int = 0,
                       var position : Long = 0,
                       var whenReady : Boolean = true,
                       var source : SourceType = SourceType.local_audio)


class PlayerHolder (val context: Context,
                    val playerView: PlayerView,
                    val playerState: PlayerState) : AnkoLogger {

    val player: ExoPlayer

    init {

        player = ExoPlayerFactory.newSimpleInstance(
                //  Renders audio, video, text (subtitles) content,
                DefaultRenderersFactory(context),

                // Choose best audio, video, text track from available sources,
                // based on bandwidth, device capabilities, language, etc
                DefaultTrackSelector(),

                //  Manage buffering and loading data over the network
                DefaultLoadControl()
        ).also {
            playerView.player = it
            info { "SimpleExoPlayer created" }
        }

//        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
//                .also {
//                    playerView.player = it
//                    info { "SimpleExoPlayer created" }
//                }
    }

    fun start() {
        //  Load media
        player.prepare(buildMediaSource(playerState.source))

        //  Restore state(after onResume()/onStart()
        with(playerState) {
            player.playWhenReady = whenReady
            player.seekTo(position)
        }

        info { "SimpleExoPlayer is started" }

    }


    private fun buildMediaSource(source: SourceType): MediaSource {
        return when(source) {
            SourceType.playlist -> {
                ConcatenatingMediaSource(
                        createExtractorMediaSource(SourceType.local_audio),
                        createExtractorMediaSource(SourceType.local_video),
                        createExtractorMediaSource(SourceType.http_audio),
                        createExtractorMediaSource(SourceType.http_video)
                )
            }
            else -> {
                createExtractorMediaSource(source)
            }
        }
    }

    private fun createExtractorMediaSource(sourceType: SourceType): MediaSource {
        return ExtractorMediaSource.Factory(
                DefaultDataSourceFactory(context, "exoplayer-learning"))
                .createMediaSource(mediaMap[sourceType])
    }

    fun stop(){

        //  Save state
        with(playerState){
            position = player.currentPosition
            window = player.currentWindowIndex
            whenReady = player.playWhenReady
        }

        player.stop(true)

        info { "SimpleExoPlayer is stopped" }
    }

    fun release(){
        info { "SimpleExoPlayer is released" }
    }
}