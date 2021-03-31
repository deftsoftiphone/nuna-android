package com.demo.hashtag_tab

import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.demo.R
import com.demo.model.response.baseResponse.HashTagImagesItem
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder


class HashTagTabImageSliderAdapter(private val mSliderItems: ArrayList<HashTagImagesItem>) :
    PlutoAdapter<HashTagImagesItem, HashTagTabImageSliderAdapter.SliderViewHolder>(mSliderItems) {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(parent, R.layout.layout_slide_view_item)
    }

    inner class SliderViewHolder(val parent: ViewGroup, itemLayoutId: Int) :
        PlutoViewHolder<HashTagImagesItem>(parent, itemLayoutId) {


        private var ivBanner: ImageView = getView(R.id.ivBanner)
        private var hashtagName: TextView = getView(R.id.hashtagName)
        override fun set(item: HashTagImagesItem, position: Int) {
            Log.e("SLIDERIMAGHEE", item.toString())

            Glide.with(parent).load(item.media?.mediaUrl).thumbnail(0.05f)
                .placeholder(R.drawable.dashboard_item_bg)
                .error(R.drawable.dashboard_item_bg).into(ivBanner)
            if(!item.hashtag?.tagName.isNullOrEmpty()){
//                hashtagName.visibility= VISIBLE
                hashtagName.setText("#${item.hashtag?.tagName}")
            }else{
                hashtagName.visibility=GONE
            }

        }

    }
}
