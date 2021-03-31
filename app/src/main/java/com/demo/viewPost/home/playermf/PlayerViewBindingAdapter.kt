package com.demo.viewPost.home.playermf

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.demo.R
import com.demo.base.MainApplication
import com.demo.util.hideView
import com.demo.util.mediaUrlAccordingToInternetSpeed
import com.demo.util.showView
import com.demo.viewPost.home.ViewPostActivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
// extension function for show toast
fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {

    companion object {

        private var simpleCache: SimpleCache? = null
        private var cacheDataSourceFactory: CacheDataSourceFactory? = null
        private var lastIndex = 0

        // for hold all players generated
        private var playersMap: MutableMap<Int, SimpleExoPlayer?> = mutableMapOf()

        // for hold current player
        private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null

        fun releaseAllPlayers() {
            playersMap.map {
                try {
                    it.value?.release()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
            playersMap.clear()
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int) {
            playersMap[index]?.release()
            playersMap[index] = null
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo() {
            if (currentPlayingVideo != null) {
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }

        // call when scroll to pause any playing player
        fun playCurrentPlayingVideo() {
            if (currentPlayingVideo != null) {
                currentPlayingVideo?.second?.playWhenReady = true
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int) {
            if (playersMap[index]?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                if (lastIndex != index) {
                    playersMap[index]?.seekTo(0)
                }
                lastIndex = index

                playersMap[index]?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap[index]!!)
            }
        }

        fun addCaching(activity: ViewPostActivity) {
            simpleCache = MainApplication.simpleCache
            cacheDataSourceFactory = CacheDataSourceFactory(
                simpleCache!!,
                DefaultHttpDataSourceFactory(
                    Util.getUserAgent(
                        activity,
                        activity.getString(R.string.app_name)
                    )
                ),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            )
        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        @JvmStatic
        @BindingAdapter(
            value = ["video_url", "on_state_change", "progressbar", "thumbnail", "item_index"],
            requireAll = false
        )
        fun PlayerView.loadVideo(
            url: String,
            callback: PlayerStateCallback,
            progressbar: ProgressBar,
            thumbnail: ImageView,
            item_index: Int? = null
        ) {
            if (url == null) return
            var player = SimpleExoPlayer.Builder(context).build()

            player.playWhenReady = false
            player.repeatMode = Player.REPEAT_MODE_ALL

            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = true
            // Provide url to load the video from here
            // val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(Uri.parse(url))
            if (url.endsWith("mp4")) {
                this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            } else {
                this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }

            val speed = MainApplication.get().getNetworkSpeed()
            println("simpleCache = ${url.mediaUrlAccordingToInternetSpeed(speed)}")
            val mediaSource = if (url.endsWith("m3u8")) {
                HlsMediaSource.Factory(cacheDataSourceFactory!!)
                    .createMediaSource(Uri.parse(url.mediaUrlAccordingToInternetSpeed(speed)))
            } else ProgressiveMediaSource.Factory(cacheDataSourceFactory!!)
                .createMediaSource(Uri.parse(url))

            player.prepare(mediaSource)

            // add player with its index to map
            if (playersMap.containsKey(item_index)) {
                if (playersMap[item_index] == null) {
                    if (item_index != null)
                        playersMap[item_index] = player
                } else {
                    player = playersMap[item_index]!!
                }
            } else {
                if (item_index != null)
                    playersMap[item_index] = player
            }
            this.player = player

            this.player!!.addListener(object : Player.EventListener {

                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    progressbar.showView()
                    // this@loadVideo.context.toast("Oops! Error occurred while playing media. Seems like no internet connection")
                    //  error.message?.let { this@loadVideo.context.toast(it) }
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)

                    if (playbackState == Player.STATE_BUFFERING) {
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
                        thumbnail.showView()
//                        thumbnail.visibility = View.VISIBLE
                        progressbar.showView()
//                        progressbar.visibility = View.VISIBLE
                        callback.onVideoBuffering(player)
                    }
                    if (playbackState == Player.STATE_READY) {
                        // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                        progressbar.hideView()
//                        progressbar.visibility = View.GONE
                        // set thumbnail gone
                        thumbnail.hideView()
//                        thumbnail.visibility = View.GONE
                        callback.onVideoDurationRetrieved(this@loadVideo.player!!.duration, player)
                    }

                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        // [PlayerView] has started playing/resumed the video
                        callback.onStartedPlaying(player)
                    }
                }
            })
        }
    }
}