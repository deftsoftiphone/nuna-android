package com.demo.muebert.adapter

import android.view.View
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.MusicStreamRecyclerViewItemBinding
import com.demo.muebert.modal.ChannelsItem
import com.demo.muebert.viewmodel.AwesomeAudioContentViewModel

class MusicStreamAdapter(
    override val layoutId: Int,
    private var mClickHandler: AwesomeAudioContentViewModel.ClickHandler
) : BaseRecyclerAdapter<MusicStreamRecyclerViewItemBinding, ChannelsItem>() {

    var newItem: ChannelsItem? = null
    var channelName: String? = null
    var newPosition: Int? = -1
    var isPlaying = false

    override fun bind(holder: ViewHolder, item: ChannelsItem, position: Int) {
        holder.binding.apply {
            mClickHandler = this@MusicStreamAdapter.mClickHandler
            index = position
            item.categoryName = channelName
            data = item
            categoryName = channelName
            isPlaying = this@MusicStreamAdapter.isPlaying
            if (newPosition == position && newItem != null && item.name!! == newItem!!.name) {
                clSelector.visibility = View.VISIBLE
                isPlaying = true
                ivStream.isSelected = true
            } else {
                isPlaying = false
                ivStream.isSelected = false
                clSelector.visibility = View.GONE
            }
        }
    }

    fun updateMusicController(item: ChannelsItem, position: Int) {
        newItem = item
        newPosition = position
        notifyDataSetChanged()
    }

    fun onPlayPauseClick(item: ChannelsItem, position: Int) {
        if (position == newPosition) {
            isPlaying = !isPlaying
            notifyItemChanged(position)
        }
    }

    fun deleteStream(item: ChannelsItem) {
        newItem = null
        notifyDataSetChanged()
    }

    fun updateChannelName(name: String) {
        this.channelName = name
    }

}