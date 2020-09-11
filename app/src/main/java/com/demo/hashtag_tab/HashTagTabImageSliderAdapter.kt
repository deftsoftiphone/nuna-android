package com.demo.hashtag_tab

import android.view.ViewGroup
import android.widget.TextView
import com.demo.R
import com.demo.model.response.baseResponse.HashTag
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder


class HashTagTabImageSliderAdapter(private val mSliderItems: ArrayList<HashTag>) :
    PlutoAdapter<HashTag, HashTagTabImageSliderAdapter.SliderViewHolder>(mSliderItems) {


    override fun getViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(parent, R.layout.layout_slide_view_item)
    }

    inner class SliderViewHolder(parent: ViewGroup, itemLayoutId: Int) :
        PlutoViewHolder<HashTag>(parent, itemLayoutId) {

        private var hashtagName: TextView = getView(R.id.hashtagName)
        override fun set(item: HashTag, position: Int) {
            hashtagName.text ="#${mSliderItems[position].tagName}"
        }

    }
}
