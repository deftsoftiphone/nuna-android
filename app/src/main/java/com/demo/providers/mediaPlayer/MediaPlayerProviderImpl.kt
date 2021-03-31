package com.demo.providers.mediaPlayer

import android.content.Context
import android.net.Uri
import com.demo.R
import com.demo.base.MainApplication
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util


class MediaPlayerProviderImpl(
    context: Context
) : MediaPlayerProvider {
    private var player: SimpleExoPlayer? = null
    private var simpleCache: SimpleCache? = null
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private var listener: AudioStreamListener? = null

    init {
        player = SimpleExoPlayer.Builder(context).build()
        addCaching(context)
    }

    private fun loadAudio(audioUrl: String) {
        buildMediaSource(audioUrl)?.let { source ->
            player?.let {
                it.prepare(source)
                it.repeatMode = Player.REPEAT_MODE_ALL
                it.playWhenReady = true
                it.addListener(object : Player.EventListener {
                    override fun onPlayerError(error: ExoPlaybackException) {
                        loadAudio(audioUrl)
                        super.onPlayerError(error)
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        super.onPlayerStateChanged(playWhenReady, playbackState)
                        listener?.onLoadComplete(playbackState == Player.STATE_READY)
                    }
                })
            }
        }
    }

    override fun play(track: String) {
        loadAudio(track)
    }

    private fun buildMediaSource(uri: String): MediaSource? {
        return ProgressiveMediaSource.Factory(cacheDataSourceFactory!!)
            .createMediaSource(Uri.parse(uri))
    }

    override fun resume() {
        player?.let {
            it.playWhenReady = true
        }
    }

    private fun addCaching(context: Context) {
        simpleCache = MainApplication.simpleCache
        cacheDataSourceFactory = CacheDataSourceFactory(
            simpleCache!!,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(
                    context,
                    context.getString(R.string.app_name)
                )
            ),
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )
    }

    override fun pause() {
        player?.let {
            it.playWhenReady = false
        }
    }

    override fun isPlaying(): Boolean {
        return player!!.isPlaying
    }

    override fun releasePlayer() {
        player?.release()
    }

    override fun stop() {
        player?.stop()
    }

    override fun eventListener(listener: AudioStreamListener) {
        this.listener = listener
    }
}


