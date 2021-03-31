package com.demo.muebert.listener

import android.widget.ImageView
import com.demo.muebert.modal.ChannelsItem

interface MusicClickHandler {
    fun onTrackClick(item: ChannelsItem, position: Int)
    fun onClickDeleteTrack(item: ChannelsItem)
    fun onClickPlayPauseTrack(item: ChannelsItem, position: Int)
    fun onClickRefreshTrack(item: ChannelsItem, position: Int, view: ImageView)
    fun onClickSelectTrack(item: ChannelsItem, position: Int)
}