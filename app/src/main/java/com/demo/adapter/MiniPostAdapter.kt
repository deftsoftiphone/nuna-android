/*
package com.demo.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.demo.base.BaseRecyclerAdapter
import com.demo.base.MainApplication
import com.demo.databinding.LayoutPostMiniItemBinding
import com.demo.model.response.Post
import com.demo.util.RoundedCornersTransformation

class MiniPostAdapter (override val layoutId: Int) : BaseRecyclerAdapter<LayoutPostMiniItemBinding, Post>(){

    var showDelete = false

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


        if (item.video!=null)
            if (item.video.isNotEmpty()) {

                var url:String
                url=item.video

                if (url.startsWith("htt")){

                }else {
                    var strurl: String
                    strurl= AmazonUtil.S3_MEDIA_PATH+url+".mp4";
                    url=strurl
                }

                var thumb =""
                if(url.contains(AmazonUtil.GIF_Video)){

                    var  gif=url.replace(".mp4",".gif").replace("video/","gif/")
                    thumb=gif
                }else{
                    thumb=url
                }

                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))
                Glide.with(MainApplication.get().getContext())
                    .load(thumb)
                    .apply(requestOptions)
                   // .thumbnail(Glide.with(MainApplication.get().getContext()).load(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.ivImage)

                */
/*  Glide
                      .with( context )
                      .load( Uri.fromFile( File(url) ) )
                      .into( holder.binding.ivImage );*//*



                */
/*          Glide
                              .with(context)
                              .asBitmap()
                              .load(item.video).listener(object : RequestListener<Bitmap> {
                                  override fun onLoadFailed(
                                      e: GlideException?,
                                      model: Any?,
                                      target: Target<Bitmap>?,
                                      isFirstResource: Boolean
                                  ): Boolean {
                                      return false
                                  }

                                  override fun onResourceReady(
                                      resource: Bitmap?,
                                      model: Any?,
                                      target: Target<Bitmap>?,
                                      dataSource: DataSource?,
                                      isFirstResource: Boolean
                                  ): Boolean {
                                      val transformation = RoundedCornersTransformation(10, 0)
                                     // if (resource != null)
                                         // Picasso.get().load(getImageUri(context, resource)).transform(
                                             // transformation
                                         // ).fit().into(holder.binding.ivImage)
                                      return true
                                  }

                              })
                              .into(holder.binding.ivImage)*//*

            }

        if (item.images!=null)
            if (item.images.isNotEmpty()) {

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
                }
            }

        holder.binding.apply {
            this.item = item
            this.showDelete = this@MiniPostAdapter.showDelete
            listOf(tvOwner,ivOwner).forEach { it.visibility = View.GONE }
        }
    }

    fun getItem(position: Int): Post {
        return list[position]
    }

    fun updatePost(post: Post, position: Int) {
        list[position] = post
        notifyItemChanged(position)
    }
}*/
