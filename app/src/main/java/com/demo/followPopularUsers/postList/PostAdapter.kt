package com.demo.followPopularUsers.postList

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.demo.base.BaseRecyclerAdapter
import com.demo.base.MainApplication
import com.demo.databinding.LayoutDashboardPostItemBinding
import com.demo.model.response.Post
import com.demo.util.Prefs
import com.demo.util.RoundedCornersTransformation

class PostAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<LayoutDashboardPostItemBinding, Post>() {


    override fun bind(holder: ViewHolder, item: Post, position: Int) {


        holder.binding.tvPostCount.text = item.images.split(",").size.toString()

        if(item.video.isNullOrEmpty()){
            holder.binding.tvPostCount.visibility= View.VISIBLE
            holder.binding.imvPlayButton.visibility= View.GONE

        }else if(item.video.equals("null")){
            holder.binding.tvPostCount.visibility= View.VISIBLE
            holder.binding.imvPlayButton.visibility= View.GONE
        }else{
            holder.binding.tvPostCount.visibility= View.GONE
            holder.binding.imvPlayButton.visibility= View.VISIBLE
        }
        holder.binding.imvEdit.visibility=View.VISIBLE
        holder.binding.imvDelete.visibility=View.VISIBLE
        holder.binding.apply {
            this.item = item
            this.responseLogin = Prefs.init().loginData
            listOf(tvOwner, ivOwner).forEach { it.visibility = View.GONE }
        }

        if (item.video != null)
            if (item.video.isNotEmpty()) {/*

                var url: String
                url = item.video

                if (url.startsWith("htt")) {

                } else {
                    var strurl: String
                    strurl =
                        AmazonUtil.S3_MEDIA_PATH + url + ".mp4";
                    url = strurl
                }
                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

                val transformation = RoundedCornersTransformation(12, 0)

             *//*   Picasso.get().load(url).transform(transformation).resize(200, 200)
                    .centerCrop()
                    .into(holder.binding.ivImage)
                *//*
                var thumb =""
                if(url.contains(AmazonUtil.GIF_Video)){

                    var  gif=url.replace(".mp4",".gif").replace("video/","gif/")
                    thumb=gif
                }else{
                    thumb=url
                }
                Glide.with(MainApplication.get().getContext())
                    .load(thumb)
                    .apply(requestOptions)
                   // .thumbnail(Glide.with(MainApplication.get().getContext()).load(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.ivImage)
*/
            }


        if (item.images!=null)
            if (item.images.isNotEmpty()) {/*

                var urlList = item.images
                var url: String

                val urlArr = urlList?.split(",")


                val transformation = RoundedCornersTransformation(12, 0)


                if (urlList?.isNotEmpty() == true && urlArr?.get(0)?.isNotBlank() == true) {
                    url = urlArr[0]


                    if (url.startsWith("htt")) {

                    } else {
                        var strurl: String
                        strurl = AmazonUtil.S3_MEDIA_PATH + url + ".jpg";
                        url = strurl
                    }

                    var thumb = url.replace(
                        url.split("/")[url.split("/").size - 1],
                        ""
                    ) + "thumb/" + url.split("/")[url.split("/").size - 1]


                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

                    Glide.with(MainApplication.get().getContext())
                        .load(thumb)
                        .apply(requestOptions)
                        // .thumbnail(Glide.with(MainApplication.get().getContext()).load(url))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.binding.ivImage)
                }*/
            }


       /* if (item.video != null)
            if (item.video.isNotEmpty()) {

                var url: String
                url = item.video

                if (url.startsWith("htt")) {

                } else {
                    var strurl: String
                    strurl =
                        AmazonUtil.S3_MEDIA_PATH + url + ".mp4";
                    url = strurl
                }
                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))
                Glide.with(appco)
                    .load(url)
                    .apply(requestOptions)
                    .thumbnail(Glide.with(context).load(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.ivImage)

            }*/
    }
}