package com.ninthsemester.simplevideoplayer

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Rational
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.toast

class VideoActivity : AppCompatActivity() {

    lateinit var mediaSession: MediaSessionCompat
    private lateinit var connector: MediaSessionConnector
    private lateinit var playerHolder: PlayerHolder
    private val state = PlayerState()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        mediaSession = MediaSessionCompat(this, packageName)
        connector = MediaSessionConnector(mediaSession)

        // If QueueNavigator isn't set, then mediaSessionConnector won’t handle
        // MediaSession actions (they won't show up in the minimized PIP activity):
        // [ACTION_SKIP_PREVIOUS], [ACTION_SKIP_NEXT], [ACTION_SKIP_TO_QUEUE_ITEM]
        connector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {

            override fun getMediaDescription(idx: Int): MediaDescriptionCompat {
                return MediaLibrary[idx]
            }

        })
    }


    override fun onStart() {
        super.onStart()

        playerHolder = PlayerHolder(this, exoplayerview_activity_video, state)

        playerHolder.player.addListener(object : Player.DefaultEventListener() {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                when(playbackState) {
                    Player.STATE_ENDED -> toast("playback ended")
                    Player.STATE_READY -> when (playWhenReady) {
                        true -> {
                            toast("playback started")
                        }
                        false -> {
                            toast("playback paused")
                        }
                    }
                }
            }

        })

//        playerHolder.start()
        connector.setPlayer(playerHolder.player, null)
        mediaSession.isActive = true
    }

    override fun onStop() {
        super.onStop()
        playerHolder.release()
        connector.setPlayer(null, null)
        mediaSession.isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    object MediaCatalog{
        val list = mutableListOf<MediaDescriptionCompat>()
        init {
            list.add(
                    with(MediaDescriptionCompat.Builder()) {
                        setDescription("MP4 loaded from assets folder")
                        setMediaId("1")
                        setMediaUri(Uri.parse("asset:///sample_audio_file.mp3"))
                        setTitle("Stock footage")
                        setSubtitle("Local video")
                        build()
                    }
            )
            list.add(
                    with(MediaDescriptionCompat.Builder()) {
                        setDescription("MP3 loaded over HTTP")
                        setMediaId("2")
                        setMediaUri(Uri.parse("asset:///ed_hd.mp4"))
                        setTitle("Spoken track")
                        setSubtitle("Streaming audio")
                        build()
                    }
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {

        enterPictureInPictureMode(
                with(PictureInPictureParams.Builder()) {
                    val width = 16
                    val height = 9
                    setAspectRatio(Rational(width, height))
                    build()
                }
        )
    }


    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        exoplayerview_activity_video.useController = !isInPictureInPictureMode
    }
}
