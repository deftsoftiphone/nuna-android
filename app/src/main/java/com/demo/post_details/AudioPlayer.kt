package com.demo.post_details

import android.content.Context
import android.util.Patterns
//import com.potyvideo.library.AndExoPlayerView
//import com.potyvideo.library.PlayerCallback


class AudioPlayer(context : Context){

//    var mPlayerView : AndExoPlayerView? = null
    var isPlaying = false


    init {
//        mPlayerView = AndExoPlayerView(context)
//        mPlayerView?.setPlayWhenReady(true)

    }

//    fun playNewItem(uri : String?, extCallback : PlayerCallback){
//        uri?.apply {
//            if ( Patterns.WEB_URL.matcher(uri).matches()){
//                mPlayerView?.setExternalCallback(extCallback)
//                mPlayerView?.setSource(uri)
//            }
//        }
//    }

    fun pause(){
//        mPlayerView?.pausePlayer()
    }

    fun resume(){
//        mPlayerView?.play()
    }

    fun onPauseFragment(){
        pause()
    }

    fun onResumeFragment(){

    }

}