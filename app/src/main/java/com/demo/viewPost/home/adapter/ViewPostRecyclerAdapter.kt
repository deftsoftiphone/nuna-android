package com.demo.viewPost.home.adapter

import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.androidnetworking.AndroidNetworking
import com.demo.R
import com.demo.databinding.LayoutViewPostRecyclerItemBinding
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.ClickGuard
import com.demo.util.Prefs
import com.demo.viewPost.callback.PostDiffUtil
import com.demo.viewPost.clickhandler.EmojiClickHandler
import com.demo.viewPost.clickhandler.ShareClickHandler
import com.demo.viewPost.home.playermf.PlayerStateCallback
import com.demo.viewPost.home.playermf.PlayerViewAdapter
import com.demo.viewPost.home.viewModal.ViewPostViewModal
import com.google.android.exoplayer2.Player
import com.like.LikeButton
import com.like.OnAnimationListener
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.video_player_exo_controllers.view.*
import java.util.*


/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
class ViewPostRecyclerAdapter(
    private var postList: ArrayList<PostAssociated>,
    private var mClickHandler: ViewPostViewModal.ClickHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PlayerStateCallback {

    private var mItemClickListener: OnItemClickListener? = null
    private var shareClickHandler: ShareClickHandler? = null
    private var emojiClickHandler: EmojiClickHandler? = null
    private var allow = false
    private var currentTime = 0L
    fun setAdapterClickHandler(
        shareClickHandler: ShareClickHandler,
        emojiClickHandler: EmojiClickHandler
    ) {
        this.shareClickHandler = shareClickHandler
        this.emojiClickHandler = emojiClickHandler
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {
        val binding: LayoutViewPostRecyclerItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.layout_view_post_recycler_item,
            viewGroup,
            false
        )
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        //Here you can fill your row view
        if (holder is VideoPlayerViewHolder) {
            val model = getItem(position)
            // send data to view holder
            holder.onBind(model)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private fun getItem(position: Int): PostAssociated {
        return postList[position]
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        PlayerViewAdapter.releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    fun updateListData(postList: ArrayList<PostAssociated>) {
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(PostDiffUtil(this.postList, postList))
        diffResult.dispatchUpdatesTo(this)
        if (this.postList.isNotEmpty())
            this.postList.clear()
        this.postList.addAll(postList)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (holder is VideoPlayerViewHolder && payloads.get(0) is PostAssociated) {
                (payloads.get(0) as PostAssociated).let {
                    val model = it
                    // send data to view holder
                    holder.onBind(model)
                }
            }
        }
    }

    fun addNewItems(postList: ArrayList<PostAssociated>) {
        this.postList.addAll(postList)
        notifyItemInserted(this.postList.size - 1)
    }

    fun removeItemOnPosition(position: Int) {
        this.postList.removeAt(position)
        PlayerViewAdapter.releaseAllPlayers()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            position: Int,
            type: String,
            model: PostAssociated?
        )
    }

    inner class VideoPlayerViewHolder(private val binding: LayoutViewPostRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: PostAssociated) {
            binding.apply {
                dataModel = model
                clickHandler = mClickHandler
                callback = this@ViewPostRecyclerAdapter
                index = adapterPosition
                userId = Prefs.init().currentUser?.id
                likebtn.isLiked = model.likedByUser!!
                bookmarkbtn.isLiked = model.savedByUser!!
                usernane.setShadowLayer(1F, 0F, 0F, Color.BLACK)
                descriptionText.setShadowLayer(1F, 0F, 0F, Color.BLACK)
                addCommentSub.setShadowLayer(1F, 0F, 0F, Color.BLACK)
                likesCount.setShadowLayer(1F, 0F, 0F, Color.BLACK)
                commentsCount.setShadowLayer(1F, 0F, 0F, Color.BLACK)
                shareCount.setShadowLayer(1F, 0F, 0F, Color.BLACK)
                executePendingBindings()
            }
            setClickHandler(binding, model)
            binding.apply {
                try {
                    ClickGuard.guard(
                        llFacebookContainer,
                        llINSContainer,
                        llInstagramContainer,
                        llMessageContainer,
                        llStatusContainer,
                        llWhatsappContainer,
                        bookmarkbtn,
                        likebtn,
                        commentClick,
                        shareClick,
                        deleteLayout,
                        reportLayout,
                        profilePic,
                        usernane
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun setClickHandler(
            binding: LayoutViewPostRecyclerItemBinding,
            model: PostAssociated
        ) {
            // handel on item click
            binding.root.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "main",
                    model
                )
            }

            binding.likebtn.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {

                }

                override fun unLiked(likeButton: LikeButton) {
                    mClickHandler.onUnLikeClick(model, adapterPosition)
                }
            })

            binding.likebtn.setOnAnimationEndListener(object : OnAnimationListener {
                override fun onAnimationEnd(likeButton: LikeButton?) {
                    mClickHandler.onLikeClick(model, adapterPosition)
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.likebtn.isEnabled = true
                    }, 500)
                }

                override fun onAnimationStart(likeButton: LikeButton?) {
                    AndroidNetworking.forceCancel("GetPostDetail")
                    binding.likebtn.isEnabled = false
                }

            })

            binding.bookmarkbtn.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {

                }

                override fun unLiked(likeButton: LikeButton) {
                    mClickHandler.onRemoveSaveClick(model, adapterPosition)
                }
            })

            binding.bookmarkbtn.setOnAnimationEndListener(object : OnAnimationListener {
                override fun onAnimationEnd(likeButton: LikeButton?) {
                    mClickHandler.onSaveClick(model, adapterPosition)
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.bookmarkbtn.isEnabled = true
                    }, 500)
                }

                override fun onAnimationStart(likeButton: LikeButton?) {
                    AndroidNetworking.forceCancel("GetPostDetail")
                    binding.bookmarkbtn.isEnabled = false
                }
            })

            binding.reportLayout.setOnClickListener {
                mClickHandler.onReportClick(model, adapterPosition)
                binding.cancelShare.performClick()
            }

            binding.deleteLayout.setOnClickListener {
                shareClickHandler?.deletePost(model, adapterPosition)
                binding.cancelShare.performClick()
            }

            binding.settings.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "settings",
                    model
                )
            }

            binding.backscreen.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "back",
                    model
                )
            }

            binding.addCommentSub.setOnClickListener {
                mClickHandler.onCommentClick(model, adapterPosition)
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "commentSub",
                    model
                )
            }

            binding.commentClick.setOnClickListener {
                mClickHandler.onCommentClick(model, adapterPosition)
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "commentClick",
                    model
                )
            }

            binding.hideCommentSection.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "hidecommentview",
                    model
                )
            }

            binding.hideShareSection.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "hideShareSection",
                    model
                )
            }

            binding.cancelShare.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "cancelShare",
                    model
                )
            }

            binding.commentsView.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "commentsView",
                    model
                )
            }

            binding.shareView.setOnClickListener {
                println("VideoPlayerViewHolder.onBind")
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "shareView",
                    model
                )
            }

            binding.shareClick.setOnClickListener {
                mClickHandler.onShareClick(model, adapterPosition)
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "shareClick",
                    model
                )
            }

            binding.followClick.setOnClickListener {
                it.visibility = View.GONE
                mClickHandler.onUserFollowClick(model, adapterPosition)
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "followClick",
                    model
                )
            }

            binding.itemVideoExoplayer.fake_click.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "fakeclick",
                    model
                )
            }

            binding.itemVideoExoplayer.mfclick.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "mfclick",
                    model
                )
            }

            binding.profileLayout.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "profilelayout",
                    model
                )
            }

            binding.socialLayout.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "sociallayout",
                    model
                )
            }

            binding.playBigButton.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "playBigButton",
                    model
                )
            }

            binding.blankclick.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "blankclick",
                    model
                )
            }

            binding.addCommentMain.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "addCommentMain",
                    model
                )
            }

            binding.sendCommentView.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    "sendCommentView",
                    model
                )
            }

            binding.apply {
                emojiCool.setOnClickListener {
                    emojiClickHandler?.onCoolEmojiClick(0x1F60D)
                }
                emojiHug.setOnClickListener {
                    emojiClickHandler?.onCoolEmojiClick(0x1F600)
                }
                emojiHeart.setOnClickListener {
                    emojiClickHandler?.onCoolEmojiClick(0x2764)
                }
                emojiShy.setOnClickListener {
                    emojiClickHandler?.onShyEmojiClick(0x1F62D)
                }
                emojiKiss.setOnClickListener {
                    emojiClickHandler?.onKissEmojiClick(0x1F618)
                }
                emojiWink.setOnClickListener {
                    emojiClickHandler?.onWinkEmojiClick(0x1F923)
                }
                emojiCwl.setOnClickListener {
                    emojiClickHandler?.onCwlEmojiClick(0x1F602)
                }
                emojiSmiley.setOnClickListener {
                    emojiClickHandler?.onSmileyEmojiClick(0x1F644)
                }
                llFacebookContainer.setOnClickListener {
                    shareClickHandler?.onFacebookShareClick(model, adapterPosition)
                }
                llINSContainer.setOnClickListener {
                    shareClickHandler?.onINSShareClick(model, adapterPosition)
                }
                llInstagramContainer.setOnClickListener {
                    shareClickHandler?.onInstagramShareClick(model, adapterPosition)
                }
                llMessageContainer.setOnClickListener {
                    shareClickHandler?.onMessageShareClick(model, adapterPosition)
                }
                llStatusContainer.setOnClickListener {
                    shareClickHandler?.onStatusShareClick(model, adapterPosition)
                }
                llWhatsappContainer.setOnClickListener {
                    shareClickHandler?.onWhatsappShareClick(model, adapterPosition)
                }

            }
        }
    }

    fun doubleTap(likeButton: LikeButton, likeImage: AppCompatImageView) {
        likeButton.isLiked = true
        val animatedVectorDrawableCompat: AnimatedVectorDrawableCompat
        val animatedVectorDrawable: AnimatedVectorDrawable
        val drawable: Drawable = likeImage.drawable
        likeImage.alpha = 0.80f
        if (drawable is AnimatedVectorDrawableCompat) {
            animatedVectorDrawableCompat = drawable
            animatedVectorDrawableCompat.start()
        } else if (drawable is AnimatedVectorDrawable) {
            animatedVectorDrawable = drawable
            animatedVectorDrawable.start()
        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {

    }

    override fun onVideoBuffering(player: Player) {

    }

    override fun onStartedPlaying(player: Player) {

    }

    override fun onFinishedPlaying(player: Player) {
    }

}