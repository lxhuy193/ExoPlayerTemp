package com.example.exoplayertemp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    lateinit var video_view: PlayerView
    var player: SimpleExoPlayer? = null
    var playWhenReady = true
    var currentWindow: Int = 0
    var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        video_view = findViewById(R.id.video_view)
        initPlayer()
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24)
            initPlayer()
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initPlayer()
            hideSystemUI()
        }
    }

    override fun onPause() {
        //
        super.onPause()
        if (Util.SDK_INT < 24)
            releasePlayer()

    }

    override fun onStop() {
        //
        super.onStop()
        if (Util.SDK_INT < 24)
            releasePlayer()
    }

    private fun hideSystemUI() {
        video_view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        video_view.player = player

        val videoUrl = "https://www.youtube.com/watch?v=1g-GQRms2SA"
        object : YouTube

    }

}