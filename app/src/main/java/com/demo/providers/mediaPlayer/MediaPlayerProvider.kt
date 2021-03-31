package com.demo.providers.mediaPlayer

interface MediaPlayerProvider {
    fun play(track: String)
    fun resume()
    fun pause()
    fun isPlaying(): Boolean
    fun releasePlayer()
    fun stop()
    fun eventListener(listener: AudioStreamListener)
}