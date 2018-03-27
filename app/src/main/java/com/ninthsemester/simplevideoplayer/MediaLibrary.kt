package com.ninthsemester.simplevideoplayer

import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat

/**
 * Created by sahil-mac on 27/03/18.
 */

open class MediaLibrary(
        val list : MutableList<MediaDescriptionCompat>) :
        List<MediaDescriptionCompat> by list {

    companion object : MediaLibrary(mutableListOf())


    init {

        //  From assets folder.

        list.add(
                with(MediaDescriptionCompat.Builder()) {
                    setDescription("MP$ loaded from assets")
                    setMediaId("1")
                    setMediaUri(Uri.parse("asset:///ed_hd.mp4"))
                    setTitle("Sample Video")
                    setSubtitle("Local Video")
                    build()
                }
        )

        list.add(
                with(MediaDescriptionCompat.Builder()) {
                    setDescription("MP3 loaded from assets folder")
                    setMediaId("2")
                    setMediaUri(Uri.parse("asset:///sample_audio_file.mp3"))
                    setTitle("Sample audio")
                    setSubtitle("Local audio")
                    build()
                }
        )

        // More creative commons, creative commons videos - https://www.blender.org/about/projects/
        list.add(
                with(MediaDescriptionCompat.Builder()) {
                    setDescription("MP4 loaded over HTTP")
                    setMediaId("3")
                    // License - https://peach.blender.org/download/
                    setMediaUri(Uri.parse("http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4"))
                    setTitle("Short film Big Buck Bunny")
                    setSubtitle("Streaming video")
                    build()
                })
        list.add(
                with(MediaDescriptionCompat.Builder()) {
                    setDescription("MP4 loaded over HTTP")
                    setMediaId("4")
                    // License - https://archive.org/details/ElephantsDream
                    //setMediaUri(Uri.parse("https://archive.org/download/ElephantsDream/ed_hd.mp4"))
                    setMediaUri(Uri.parse("asset:///ed_hd.mp4"))
                    setTitle("Short film Elephants Dream")
                    setSubtitle("Streaming video")
                    build()
                })
        list.add(
                with(MediaDescriptionCompat.Builder()) {
                    setDescription("MOV loaded over HTTP")
                    setMediaId("5")
                    // License - https://mango.blender.org/sharing/
                    setMediaUri(Uri.parse("http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"))
                    setTitle("Short film Tears of Steel")
                    setSubtitle("Streaming audio")
                    build()
                })
        list.add(
                with(MediaDescriptionCompat.Builder()) {
                    setDescription("MP3 loaded over HTTP")
                    setMediaId("6")
                    setMediaUri(Uri.parse("http://storage.googleapis.com/exoplayer-test-media-0/play.mp3"))
                    setTitle("Spoken track")
                    setSubtitle("Streaming audio")
                    build()
                })

    }

}