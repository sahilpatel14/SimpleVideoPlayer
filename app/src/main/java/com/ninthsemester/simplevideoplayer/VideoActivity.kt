package com.ninthsemester.simplevideoplayer

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private lateinit var playerHolder: PlayerHolder
    private val state = PlayerState()

    lateinit var mediaSession: MediaSessionCompat
    lateinit var connector: MediaSessionConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        playerHolder = PlayerHolder(this, exoplayerview_activity_video,state)
        mediaSession = MediaSessionCompat(this, packageName)
        connector = MediaSessionConnector(mediaSession)

        // If QueueNavigator isn't set, then mediaSessionConnector won’t handle
        // MediaSession actions (they won't show up in the minimized PIP activity):
        // [ACTION_SKIP_PREVIOUS], [ACTION_SKIP_NEXT], [ACTION_SKIP_TO_QUEUE_ITEM]
        connector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {

            override fun getMediaDescription(idx: Int): MediaDescriptionCompat {
                return MediaCatalog.list[idx]
            }

        })
    }

    override fun onStart() {
        super.onStart()
        playerHolder.start()
        connector.setPlayer(playerHolder.player, null)
        mediaSession.isActive = true
    }

    override fun onStop() {
        super.onStop()
        playerHolder.stop()
        connector.setPlayer(null, null)
        mediaSession.isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        playerHolder.release()
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
}
