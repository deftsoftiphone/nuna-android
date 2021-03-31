package com.demo.muebert.listener

import android.view.View
import com.demo.muebert.modal.ChannelsItem

interface OnTrackClickListener {
    fun onTrackClick(item: ChannelsItem, position: Int)
    fun onClickPlayPauseTrack(item: ChannelsItem, position: Int)
    fun onClickRefreshTrack(item: ChannelsItem, position: Int, view: View)
    fun onClickSelectTrack(item: ChannelsItem, position: Int)
}