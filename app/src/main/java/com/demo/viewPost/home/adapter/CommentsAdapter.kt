package com.demo.viewPost.home.adapter

import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.ItemCommentsBinding
import com.demo.model.response.baseResponse.Comment
import com.demo.util.ClickGuard
import com.demo.viewPost.home.viewModal.ViewPostViewModal
import com.like.LikeButton
import com.like.OnAnimationListener
import com.like.OnLikeListener

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
class CommentsAdapter(
    override val layoutId: Int,
    private var mClickHandler: ViewPostViewModal.ClickHandler
) : BaseRecyclerAdapter<ItemCommentsBinding, Comment>() {

    lateinit var mBinding: ItemCommentsBinding

    override fun bind(holder: ViewHolder, item: Comment, position: Int) {
        mBinding = holder.binding
        holder.binding.data = item
        holder.binding.clickHandler = mClickHandler
        holder.binding.likebtncomments.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
//                mClickHandler.onCommentLikeClick(item, position)
            }

            override fun unLiked(likeButton: LikeButton) {
                mClickHandler.onCommentLikeClick(item, position)
            }
        })
        try {
            ClickGuard.guard(holder.binding.likebtncomments)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.binding.likebtncomments.setOnAnimationEndListener(object :OnAnimationListener{
            override fun onAnimationEnd(likeButton: LikeButton?) {
                mClickHandler.onCommentLikeClick(item, position)

            }

            override fun onAnimationStart(likeButton: LikeButton?) {
            }

        })

//        holder.binding.likebtncomments.setOnAnimationEndListener {
//            mClickHandler.onCommentLikeClick(item, position)
//        }

        item.likedByUser?.let { holder.binding.likebtncomments.isLiked = it }


    }

    fun likeUnlikeComment(comment: Comment, position: Int) {
        list[position].likedByUser = comment.likedByUser
        list[position].noOfLikes = comment.noOfLikes
        notifyItemChanged(position)
    }
}


/* : RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {
    var commentsModelList: List<CommentsModel> =
        ArrayList()
    var mContext: Activity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comments, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val commentsModel = commentsModelList[position]

        holder.comments_description.text = commentsModel.comment

        holder.profile_pic.setOnClickListener {
//            val intent = Intent(mContext, ProfileActivity::class.java)
//            mContext.startActivity(intent)
//            mContext.overridePendingTransition(R.anim.enter_right, R.anim.exit_left)
        }

        holder.usernane.setOnClickListener {
//            val intent = Intent(mContext, ProfileActivity::class.java)
//            mContext.startActivity(intent)
//            mContext.overridePendingTransition(R.anim.enter_right, R.anim.exit_left)
        }
    }

    override fun getItemCount(): Int {
        return commentsModelList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var likebtncomments: LikeButton = itemView.findViewById(R.id.likebtncomments)
        var comments_description: AppCompatTextView =
            itemView.findViewById(R.id.comments_description)
        var profile_pic: CircleImageView = itemView.findViewById(R.id.profile_pic)
        var usernane: AppCompatTextView = itemView.findViewById(R.id.usernane)
    }

    init {
        this.commentsModelList = commentsModelList
        this.mContext = mContext
    }
}*/