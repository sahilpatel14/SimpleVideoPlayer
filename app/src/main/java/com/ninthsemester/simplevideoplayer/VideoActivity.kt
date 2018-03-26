package com.ninthsemester.simplevideoplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    lateinit var playerHolder: PlayerHolder
    val state = PlayerState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        playerHolder = PlayerHolder(this, exoplayerview_activity_video,state)
    }

    override fun onStart() {
        super.onStart()
        playerHolder.start()
    }

    override fun onStop() {
        super.onStop()
        playerHolder.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHolder.release()
    }
}
