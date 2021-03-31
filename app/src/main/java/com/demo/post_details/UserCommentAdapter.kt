package com.demo.post_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.commentList.CommentsFragment
import com.demo.commentList.CommentsViewModel
import com.demo.databinding.LayoutCommentAudioItemBinding
import com.demo.databinding.LayoutCommentHeaderBinding
import com.demo.databinding.LayoutCommentTextItemBinding
import com.demo.databinding.LayoutPopularUserBinding
import com.demo.model.response.UserComment
import com.demo.util.Prefs
import com.demo.util.Util
import com.google.android.exoplayer2.Player
//import com.potyvideo.library.PlayerCallback
class UserCommentAdapter(
    val context: Context,
    val mAdapterFragmentBridge: CommentsFragment.AdapterFragmentBridge,
    val viewModel: CommentsViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
//    PlayerCallback
{

    // var audioView: LayoutCommentAudioItemBinding
     var audioView: LayoutCommentAudioItemBinding?=null
    private val VIEW_TEXT_COMMENT = 1
    private val VIEW_AUDIO_COMMENT = 2
    private val ITEM_LIMITS_IN_LIMITED_VIEW_MODE = 3

    private var commentList = ArrayList<UserComment>()
     lateinit var player: AudioPlayer
    private var playingItemPosition = -1;
    private var recyclerView: RecyclerView? = null

    init {
        initAudioPlayer(context)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }


    private fun initAudioPlayer(context: Context) {
        player = AudioPlayer(context)
    }

    fun setNewData(newData: List<UserComment>) {
        commentList = ArrayList(newData)
        notifyDataSetChanged()
        initAudioPlayer(context)
    }

    fun refresh() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val i = LayoutInflater.from(parent.context)
        when (viewType) {
            VIEW_TEXT_COMMENT -> {
                val b = LayoutCommentTextItemBinding.inflate(i, parent, false)
                return TextCommentHolder(b)
            }

            VIEW_AUDIO_COMMENT -> {
                val b = LayoutCommentAudioItemBinding.inflate(i, parent, false)
                return AudioCommentHolder(b)
            }

            else -> throw RuntimeException("Unknown layout type in comments section")
        }
    }

    override fun getItemCount(): Int {
        if (viewModel.areLimitedEntries) return commentList.size.coerceAtMost(
            ITEM_LIMITS_IN_LIMITED_VIEW_MODE
        ) else return commentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (commentList[position].url) return VIEW_AUDIO_COMMENT
        else VIEW_TEXT_COMMENT
    }

    fun getReplyItemViewType(url: Boolean): Int {
        return if (url) return VIEW_AUDIO_COMMENT
        else VIEW_TEXT_COMMENT
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextCommentHolder -> {
                holder.bind(position, commentList[position])
            }

            is AudioCommentHolder -> {
                holder.bind(position, commentList[position])
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        (holder as? AudioCommentHolder)?.onRecycle()
        super.onViewRecycled(holder)
    }

    abstract inner class BaseCommentViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        var itemPosition = -1

        abstract fun getHeaderBinding(): LayoutCommentHeaderBinding

        open fun bind(pos: Int, newItem: UserComment) {
            itemPosition = pos
            getHeaderBinding().tvLikeCount.text = newItem.likeCount.toString()

            getHeaderBinding().ivUser.setOnClickListener {
                mAdapterFragmentBridge.onClickOwnerProfile(getHeaderBinding().ivUser.getTag() as Int?)
            }
            getHeaderBinding().tvLikeCount.setOnClickListener {
                viewModel.requestLikeUnLikeComment.get()?.apply {
                    like = !commentList[itemPosition].commentLiked
                    commentId = commentList[itemPosition].commentId!!
                    likerUserId = Prefs.init().loginData!!.userId
                    viewModel.callLikeUnlikeCommentApi(position)
                }
            }
        }

        private fun setupRecycler(binding: LayoutPopularUserBinding, comments: List<UserComment>) {

            val adapter: UserCommentAdapter?
            var firstBind = false
            if (binding.recyclerPosts.adapter == null) {
                adapter = UserCommentAdapter(context, mAdapterFragmentBridge, viewModel)
                firstBind = true
            } else {
                adapter = binding.recyclerPosts.adapter as? UserCommentAdapter
            }

            if (adapter == null) return

            if (comments.isEmpty()) {
                adapter.clearData()
            } else {
                //adapter.addClickEventWithView(R.id.card_parent, mClickHandler::onClickUserPost)
                adapter.setNewData(comments)
                if (firstBind) binding.recyclerPosts.adapter = adapter
            }
        }
    }

    private fun clearData() {
        commentList.clear()
    }

    inner class TextCommentHolder(val binding: LayoutCommentTextItemBinding) :
        BaseCommentViewHolder(binding.root) {

        override fun getHeaderBinding(): LayoutCommentHeaderBinding {
            return binding.headerBinding
        }

        override fun bind(pos: Int, newItem: UserComment) {
            binding.comment = newItem
            var dateTim: String = "";
            dateTim = newItem.createdDate.toString();
            binding.headerBinding.tvDate.text = "  " + Util.covertTimeToText(dateTim,context);
            getHeaderBinding().tvLikeCount.text = newItem.likeCount.toString()
            getHeaderBinding().ivUser.setOnClickListener {
                mAdapterFragmentBridge.onClickOwnerProfile(getHeaderBinding().ivUser.getTag() as Int?)
            }

            if(isReply==true){
                binding.reply.visibility= View.GONE
            }else{
                if (newItem.parentCommentId != 0) binding.reply.visibility = View.INVISIBLE
            }


            binding.reply.text = "${newItem.replyCount} "+context.getString(R.string.replies)
            binding.reply.setOnClickListener {
                mAdapterFragmentBridge.onClickReply(commentList[pos], pos)
            }
            getHeaderBinding().tvLikeCount.setOnClickListener {
                viewModel.requestLikeUnLikeComment.get()?.apply {
                    like = !commentList[pos].commentLiked
                    commentId = commentList[pos].commentId!!
                    likerUserId = Prefs.init().loginData!!.userId
                    viewModel.callLikeUnlikeCommentApi(position)
                }
            }
            binding.rvReplyThread.removeAllViews()

            //inner
            if (newItem.childComment != null) {
                for (reply in newItem.childComment!!) {
                    val inflater =
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    if (getReplyItemViewType(reply.url) == VIEW_AUDIO_COMMENT) {
                        val layoutCommentAudioItemBinding =
                            DataBindingUtil.inflate<LayoutCommentAudioItemBinding>(
                                inflater,
                                R.layout.layout_comment_audio_item,
                                null,
                                false
                            )
                        layoutCommentAudioItemBinding.comment = reply
                        val dateTim: String = newItem.createdDate.toString();
                        layoutCommentAudioItemBinding.headerBinding.tvDate.text =
                            "  " + Util.covertTimeToText(dateTim,context);
                        layoutCommentAudioItemBinding.headerBinding.tvLikeCount.text =
                            newItem.likeCount.toString()
                        layoutCommentAudioItemBinding.headerBinding.ivUser.setOnClickListener {
                            mAdapterFragmentBridge.onClickOwnerProfile(reply.user?.userId)
                        }
                        layoutCommentAudioItemBinding.reply.visibility = View.GONE
                        layoutCommentAudioItemBinding.root.requestLayout()
                        layoutCommentAudioItemBinding.viewSpace.visibility = View.VISIBLE
                        //TODo audio play click and play code
//                        layoutCommentAudioItemBinding.clickHandler = ClickHandler()
                        layoutCommentAudioItemBinding.headerBinding.tvLikeCount.setOnClickListener {
                            viewModel.requestLikeUnLikeReply.get()?.apply {
                                like = !reply.commentLiked
                                commentId = reply.commentId!!
                                likerUserId = Prefs.init().loginData!!.userId
                                isReply = true
                                viewModel.callLikeUnlikeReplyApi(reply, pos)
                            }
                        }
                        binding.rvReplyThread.addView(layoutCommentAudioItemBinding.root)
                    } else {
                        val layoutCommentTextItemBinding =
                            DataBindingUtil.inflate<LayoutCommentTextItemBinding>(
                                inflater,
                                R.layout.layout_comment_text_item,
                                null,
                                false
                            )
                        layoutCommentTextItemBinding.comment = reply
                        val dateTim: String = newItem.createdDate.toString();
                        layoutCommentTextItemBinding.headerBinding.tvDate.text =
                            "  " + Util.covertTimeToText(dateTim,context);
                        layoutCommentTextItemBinding.headerBinding.tvLikeCount.text =
                            newItem.likeCount.toString()
                        layoutCommentTextItemBinding.headerBinding.ivUser.setOnClickListener {
                            mAdapterFragmentBridge.onClickOwnerProfile(reply.user!!.userId)
                        }
                        layoutCommentTextItemBinding.reply.visibility = View.GONE
                        layoutCommentTextItemBinding.viewSpace.visibility = View.VISIBLE
                        layoutCommentTextItemBinding.headerBinding.tvLikeCount.setOnClickListener {
                            viewModel.requestLikeUnLikeReply.get()?.apply {
                                like = !reply.commentLiked!!
                                commentId = reply.commentId!!
                                likerUserId = Prefs.init().loginData!!.userId
                                isReply = true
                                viewModel.callLikeUnlikeReplyApi(reply, pos)
                            }
                        }
                        binding.rvReplyThread.addView(layoutCommentTextItemBinding.root)
                    }
                }
            }
        }
    }

    inner class AudioCommentHolder(val binding: LayoutCommentAudioItemBinding) :
        BaseCommentViewHolder(binding.root) {

        override fun getHeaderBinding(): LayoutCommentHeaderBinding {
            return binding.headerBinding
        }


        override fun bind(pos: Int, newItem: UserComment) {
            super.bind(pos, newItem)
            binding.comment = newItem
            audioView=binding;
            var dateTim: String = "";
            dateTim = newItem.createdDate.toString();
            binding.headerBinding.tvDate.text = "   " + Util.covertTimeToText(dateTim,context);
            binding.clickHandler = ClickHandler()
            if(isReply==true){
                binding.reply.visibility= View.GONE
            }else{
                if (newItem.parentCommentId != 0) binding.reply.visibility = View.INVISIBLE
            }
            binding.reply.setOnClickListener {
                mAdapterFragmentBridge.onClickReply(commentList[pos], pos)
            }
            getHeaderBinding().ivUser.setOnClickListener {
                mAdapterFragmentBridge.onClickOwnerProfile(getHeaderBinding().ivUser.getTag() as Int?)
            }
            binding.reply.text = "${newItem.replyCount} "+context.getString(R.string.replies)
            if (newItem.childComment != null) {
                binding.rvReplyThread.removeAllViews()
                for (reply in newItem.childComment!!) {
                    val inflater =
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    if (getReplyItemViewType(reply.url) == VIEW_AUDIO_COMMENT) {
                        val layoutCommentAudioItemBinding =
                            DataBindingUtil.inflate<LayoutCommentAudioItemBinding>(
                                inflater,
                                R.layout.layout_comment_audio_item,
                                null,
                                false
                            )
                        layoutCommentAudioItemBinding.comment = reply
                        layoutCommentAudioItemBinding.reply.visibility = View.GONE
                        val dateTim: String = newItem.createdDate.toString();
                        layoutCommentAudioItemBinding.headerBinding.tvDate.text =
                            "  " + Util.covertTimeToText(dateTim,context);
                        layoutCommentAudioItemBinding.headerBinding.tvLikeCount.text =
                            newItem.likeCount.toString()
                        layoutCommentAudioItemBinding.headerBinding.ivUser.setOnClickListener {
                            mAdapterFragmentBridge.onClickOwnerProfile(reply.user?.userId)
                        }
                        layoutCommentAudioItemBinding.reply.visibility = View.GONE
                        layoutCommentAudioItemBinding.viewSpace.visibility = View.VISIBLE
                        layoutCommentAudioItemBinding.clickHandler = ClickHandler()


                        layoutCommentAudioItemBinding.headerBinding.tvLikeCount.setOnClickListener {
                            viewModel.requestLikeUnLikeReply.get()?.apply {
                                like = if (reply.commentLiked) false else true
                                commentId = reply.commentId!!
                                likerUserId = Prefs.init().loginData!!.userId
                                isReply = true
                                viewModel.callLikeUnlikeReplyApi(reply, pos)
                            }

                        }
                        binding.rvReplyThread.addView(layoutCommentAudioItemBinding.root)
                    } else {
                        val layoutCommentTextItemBinding =
                            DataBindingUtil.inflate<LayoutCommentTextItemBinding>(
                                inflater,
                                R.layout.layout_comment_text_item,
                                null,
                                false
                            )
                        layoutCommentTextItemBinding.reply.visibility = View.GONE
                        layoutCommentTextItemBinding.comment = reply
                        val dateTim: String = newItem.createdDate.toString()
                        layoutCommentTextItemBinding.headerBinding.tvDate.text =
                            "  " + Util.covertTimeToText(dateTim,context);
                        layoutCommentTextItemBinding.headerBinding.ivUser.setOnClickListener {
                            mAdapterFragmentBridge.onClickOwnerProfile(reply.user?.userId)
                        }


                        layoutCommentTextItemBinding.headerBinding.tvLikeCount.text =
                            newItem.likeCount.toString()
                        layoutCommentTextItemBinding.viewSpace.visibility = View.VISIBLE
                        layoutCommentTextItemBinding.reply.visibility = View.GONE
                        layoutCommentTextItemBinding.headerBinding.tvLikeCount.setOnClickListener {
                            viewModel.requestLikeUnLikeReply.get()?.apply {
                                like = if (reply.commentLiked) false else true
                                commentId = reply.commentId!!
                                likerUserId = Prefs.init().loginData!!.userId
                                viewModel.callLikeUnlikeReplyApi(reply, pos)
                            }
                        }
                        binding.rvReplyThread.addView(layoutCommentTextItemBinding.root)
                    }
                }
            }
        }

        fun onRecycle() {
            player.pause()
            uiOnPause()
        }

        fun onMediaFinished() {
            binding.progressBar.progress = 0
            uiOnPause()
        }

        inner class ClickHandler {


            fun onClickPlayPause() {
                if (itemPosition == playingItemPosition) {
                    //playing
                    //pause item
                    if (player.isPlaying) {
                       // player.pause()

                        uiOnPause()
                    } else {
                        player.resume()
                        uiOnPlay()
                    }

                } else {
                    //not playing

                    if (playingItemPosition > 0) {

                    }

//                    player.playNewItem(
//                        commentList[itemPosition].commentText,
//                        this@UserCommentAdapter
//                    )

                    uiOnPlay()
                }
            }
        }

        fun uiOnPlay() {
            player.isPlaying = true
            playingItemPosition = itemPosition
            binding.btnPlayPause.setImageResource(R.drawable.pause_in_comments)
        }

        fun uiOnPause() {
            player.isPlaying = false
            playingItemPosition = itemPosition

            binding.btnPlayPause.setImageResource(R.drawable.play_button)
        }
    }

   /* override fun onPlayerStateChange(state: Int) {
        when (state) {
            Player.STATE_IDLE -> {
            }
            Player.STATE_BUFFERING -> {
            }
            Player.STATE_READY -> {
            }
            Player.STATE_ENDED -> {

            }
            else -> {
            }
        }
    }

    override fun onProgressUpdate(value: Int) {
        val vh = recyclerView?.findViewHolderForLayoutPosition(playingItemPosition)
        (vh as? AudioCommentHolder)?.binding?.progressBar?.progress = value
    }

    override fun onError() {

    }

    override fun onMediaFinished() {
        val vh = recyclerView?.findViewHolderForLayoutPosition(playingItemPosition)
        (vh as? AudioCommentHolder)?.onMediaFinished()
    }*/

    fun changeData(userComment: UserComment, position: Int) {
        commentList[position] = userComment
        notifyItemChanged(position)
    }

    var isReply: Boolean = false
    fun isReply(isReply: Boolean) {
        this.isReply = isReply
    }


}