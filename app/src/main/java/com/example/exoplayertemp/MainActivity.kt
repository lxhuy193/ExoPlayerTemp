package com.example.exoplayertemp

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.upstream.DefaultAllocator

import com.google.android.exoplayer2.DefaultLoadControl

import com.google.android.exoplayer2.LoadControl




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
        if (Util.SDK_INT >= 24 || player == null)
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
        if (Util.SDK_INT < 24)
            releasePlayer()

        super.onPause()
    }

    override fun onStop() {
        if (Util.SDK_INT < 24)
            releasePlayer()

        super.onStop()
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

        val videoUrl = "https://www.youtube.com/watch?v=8JnfIa84TnU&list=RD8JnfIa84TnU&start_radio=1"
        object : YouTubeExtractor(this) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles != null) {
                    val itag = 135 // Tag of video 720p
                    val audioTag = 140 // Tag of m4a audio
                    val videoUrl = ytFiles[itag].url
                    val audioUrl = ytFiles[audioTag].url

                    val videoSource: MediaSource =
                        ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                            .createMediaSource(
                                MediaItem.fromUri(videoUrl)
                            )

                    val audioSource: MediaSource =
                        ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                            .createMediaSource(
                                MediaItem.fromUri(audioUrl)
                            )

                    player!!.setMediaSource(
                        MergingMediaSource(true, videoSource, audioSource),
                        true
                    )
                    player!!.prepare()
                    player!!.playWhenReady = playWhenReady
                    player!!.seekTo(currentWindow, playbackPosition)
                }
            }
        }.extract(videoUrl, false, true)

        println("HELO " + videoUrl)

    }

}

object VideoPlayerConfig {
    //Minimum Video you want to buffer while Playing
    const val MIN_BUFFER_DURATION = 2000

    //Max Video you want to buffer during PlayBack
    const val MAX_BUFFER_DURATION = 5000

    //Min Video you want to buffer before start Playing it
    const val MIN_PLAYBACK_START_BUFFER = 1500

    //Min video You want to buffer when user resumes video
    const val MIN_PLAYBACK_RESUME_BUFFER = 2000
}

var loadControl: LoadControl = DefaultLoadControl.Builder()
    .setAllocator(DefaultAllocator(true, 16))
    .setBufferDurationsMs(
        VideoPlayerConfig.MIN_BUFFER_DURATION,
        VideoPlayerConfig.MAX_BUFFER_DURATION,
        VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
        VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
    )
    .setTargetBufferBytes(-1)
    .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()