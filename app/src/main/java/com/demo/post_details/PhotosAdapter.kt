package com.demo.post_details

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.demo.databinding.LayoutItemCarouselBinding
import com.demo.databinding.LayoutItemCarouselZoomableBinding
import com.demo.util.aws.AmazonUtil
import com.google.android.exoplayer2.ExoPlayer
import com.potyvideo.library.AndExoPlayerView
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class PhotosAdapter() : PagerAdapter() {

    interface PhotoClickListener {
        fun onClickPhoto(pos: Int, imgUri: String?)
    }

     lateinit var videoView: AndExoPlayerView
    var list: ArrayList<String> = ArrayList()
    var allowDragNZoom: Boolean = false
    var isVideo: Boolean = false
    var removedItems = ArrayList<String>()
    private var clickListener: PhotoClickListener? = null
    var video: String = ""
    lateinit var context: Context

    constructor (
        list: ArrayList<String>,
        allowDragNZoom: Boolean,
        isVideo: Boolean,
        video: String,
        context: Context
    ) : this() {
        if (list.isEmpty()) {
            val temp = ArrayList<String>()
            temp.add("")
            this.list = temp
        } else {
            this.list = list
        }
        this.allowDragNZoom = allowDragNZoom
        this.isVideo = isVideo
        this.video = video
        this.context = context
    }

    fun setClickListener(clickListener: PhotoClickListener?) {
        this.clickListener = clickListener
    }

    fun removeItem(position: Int) {
        removedItems.add(list[position])
        list.removeAt(position)
        notifyDataSetChanged()
    }

    fun removeItems(removedItems: ArrayList<String>) {
        this.removedItems.addAll(removedItems)
        list.removeAll(removedItems)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemPosition(item: Any): Int { // refresh all fragments when data set changed
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val root: View
        val photoView: ImageView
        val inf = LayoutInflater.from(container.context)
        if (allowDragNZoom) {
            val b = LayoutItemCarouselZoomableBinding.inflate(inf, container, true)
            root = b.root
            photoView = b.iv
        }
        else {
           val  b = LayoutItemCarouselBinding.inflate(inf, container, true)
            photoView = b.iv
            root = b.root
            b.imvPlayButton.visibility = if (isVideo) View.VISIBLE else View.GONE
            b.isVideo = isVideo

            if (isVideo) {
                (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                videoView=b.andExoPlayerView
                b.andExoPlayerView.visibility = View.VISIBLE
                b.imvPlayButton.visibility = View.GONE

                if (video.startsWith("htt")){
                    video=video.replace(AmazonUtil.S3_MEDIA_PATH,AmazonUtil.cloud_MEDIA_PATH)

                }else {
                    var strurl: String
                    strurl=AmazonUtil.cloud_MEDIA_PATH+video+".mp4";
                    video=strurl
                }

                b.andExoPlayerView.setSource(video)


            }
            b.imvPlayButton.setOnClickListener {
                if (isVideo) {
                    (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    b.andExoPlayerView.visibility = View.VISIBLE
                    b.imvPlayButton.visibility = View.GONE

                    if (video.startsWith("htt")){
                    video=video.replace(AmazonUtil.S3_MEDIA_PATH,AmazonUtil.cloud_MEDIA_PATH)
                    }else {
                        var strurl: String
                        strurl=AmazonUtil.cloud_MEDIA_PATH+video+".mp4";
                        video=strurl
                    }

                    b.andExoPlayerView.setSource(video)


                }
            }
            b.andExoPlayerView.setPlayerStateListener {
                if (it == ExoPlayer.STATE_ENDED) {
                    (context as Activity).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    b.imvPlayButton.visibility = View.VISIBLE
                    b.andExoPlayerView.visibility = View.GONE
                }
            }
        }
        var uri: String = list[position]

        if (uri.startsWith("htt")){

        }else {
            var strurl: String


            strurl=AmazonUtil.S3_MEDIA_PATH+uri.trim()+".jpg";

            uri=strurl
        }



        if (!uri.isBlank() && !isVideo) Picasso.get().load(uri).into(photoView)
        else if (!uri.isBlank()) {

            if (uri!=null)
                if (uri.isNotEmpty()) {

                    var url: String
                    url = uri

                    if (url.startsWith("htt")) {

                    } else {
                        var strurl: String
                        strurl = AmazonUtil.cloud_MEDIA_PATH + url + ".mp4";
                        url = strurl
                    }


                   /* var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))
                    Glide.with(context)
                        .load(url)
                        .apply(requestOptions)
                        .thumbnail(Glide.with(context).load(url))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(photoView)*/
                }
//            val bitmap = ImageUtils.retriveVideoFrameFromVideo(uri)
//            if (bitmap != null)
//                photoView.setImageBitmap(bitmap)
        }
        return root
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}