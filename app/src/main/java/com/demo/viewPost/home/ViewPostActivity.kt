package com.demo.viewPost.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseActivity
import com.demo.base.MainApplication
import com.demo.databinding.LayoutViewPostRecyclerItemBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.UpdatePostEvent
import com.demo.util.*
import com.demo.util.Util.Companion.hasInternet
import com.demo.viewPost.clickhandler.CommentListener
import com.demo.viewPost.clickhandler.EmojiClickHandler
import com.demo.viewPost.clickhandler.PostUpdateListener
import com.demo.viewPost.clickhandler.ShareClickHandler
import com.demo.viewPost.home.adapter.ViewPostRecyclerAdapter
import com.demo.viewPost.home.playermf.PlayerViewAdapter
import com.demo.viewPost.home.playermf.RecyclerViewScrollListener
import com.demo.viewPost.home.viewModal.ViewPostViewModal
import com.demo.viewPost.home.viewModal.ViewPostViewModalFactory
import com.demo.viewPost.utils.ConstantObjects
import com.demo.viewPost.utils.ConstantObjects.toEditable
import com.demo.viewPost.utils.MyRecycler
import com.demo.viewPost.utils.OnSwipeTouchListener
import com.demo.viewPost.utils.ReadMoreOption
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.gson.annotations.SerializedName
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.video_player_exo_controllers.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class ViewPostActivity : BaseActivity(), ReadMoreOption.OnHashTagClickListener,
    ReadMoreOption.onExpandTextView, KodeinAware, PostUpdateListener, CommentListener,
    ShareClickHandler, EmojiClickHandler {

    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ViewPostViewModalFactory by instance()

    private val url by lazy { if (intent.hasExtra(URL)) intent.getStringExtra(URL) else "" }

    private val queryParam by lazy {
        if (intent.hasExtra(QUERY_PARAM)) intent.getParcelableExtra(
            QUERY_PARAM
        ) as QueryParams else null
    }

    private val paginationProgress by lazy { findViewById<ProgressBar>(R.id.paginationProgress) }

    companion object {
        const val URL = "URL"
        const val QUERY_PARAM = "QUERY_PARAM"
        const val ALL_LOADED = "ALL_LOADED"
        const val LAUNCH_COMMENT_ACTIVITY = 1
    }

    private lateinit var mViewModel: ViewPostViewModal
    private var mBinding: LayoutViewPostRecyclerItemBinding? = null
    private var mInitialTextureWidth = 0
    private var recyclerView: MyRecycler? = null
    private var mPostAdapter: ViewPostRecyclerAdapter? = null
    private lateinit var updatePostEvent: UpdatePostEvent
    private var layoutManager: LinearLayoutManager? = null
    private var posts = mutableListOf<PostAssociated>()
    var currentPage = -1
    var postPosition = -1
    private var currentSharePost: SharePost? = null
    private var downLoadBroadcastReceiver: BroadcastReceiver = DownLoadBroadcast()

    // for handle scroll and get first visible item index
    private lateinit var scrollListener: RecyclerViewScrollListener
    private var player: Player? = null
    private var readMoreOption: ReadMoreOption? = null
    private var shareFile: File? = null
    private var filePath = "MediaFile.MP4"
    private lateinit var myApp: MainApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreenFullSize()
        setUpViewModel()
        setUpUI()
        setPaginationUrl()
        setAdapter()
        setRecyclerScrollListener()
        setRecyclerItemClickListener()
        readArguments()
        observeList()
        initMyApp()
    }

    private fun setPaginationUrl() {
        if (!TextUtils.isEmpty(url)) {
            println("url = ${url}")
            queryParam?.let { mViewModel.queryParameter = it }
            mViewModel.url = url
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downLoadBroadcastReceiver)
        PlayerViewAdapter.releaseAllPlayers()
        mViewModel.toastMessage.postValue(null)
    }

    private fun initMyApp() {
        myApp = applicationContext as MainApplication
        myApp.setCurrentActivity(this)
    }

    private fun setRecyclerItemClickListener() {
        mPostAdapter?.let {
            it.setAdapterClickHandler(this@ViewPostActivity, this@ViewPostActivity)
            mViewModel.mPostAdapter = it
            it.setOnItemClickListener(object : ViewPostRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    type: String,
                    model: PostAssociated?
                ) {
                    mBinding?.let { mBinding ->
                        when (type) {
                            "back" -> {
                                finish()
                            }
                            "settings" -> {
                                shareFile(model, 0)
                            }
                            "commentSub" -> {
                                recyclerView!!.enableVerticalScroll(false)
                                mBinding.itemVideoExoplayer.setOnTouchListener(null)
                                mBinding.itemVideoExoplayer.setOnTouchListener(null)
                                if (isNetworkConnected()) {
                                    mBinding.apply {
                                        mBinding.noNetwork.visibility = GONE
                                        recyclerView!!.adapter?.let { adapter ->
                                            if (adapter.itemCount > 0) {
                                                commentsList.visibility = VISIBLE
                                            } else {
                                                commentsList.visibility = GONE
                                            }
                                        }
                                    }
                                } else {
                                    mBinding.apply {
                                        commentsList.visibility = GONE
                                        noNetwork.visibility = VISIBLE
                                    }
                                }
                                ConstantObjects.slideUp(
                                    mBinding.commentsView,
                                    mBinding.mainItemLayout
                                )
                                mBinding.addCommentSub.visibility = GONE
                                mBinding.addCommentMain.text = "".toEditable()
                                openComments(true)
                            }
                            "commentClick" -> {
                                if (hasInternet()) {
                                    recyclerView!!.enableVerticalScroll(false)
                                    mBinding.itemVideoExoplayer.setOnTouchListener(null)
                                    if (isNetworkConnected()) {
                                        mBinding.apply {
                                            mBinding.noNetwork.visibility = GONE
                                            recyclerView!!.adapter?.let { adapter ->
                                                if (adapter.itemCount > 0) {
                                                    commentsList.visibility = VISIBLE
                                                } else {
                                                    commentsList.visibility = GONE
                                                }
                                            }
                                        }
                                    } else {
                                        mBinding.apply {
                                            commentsList.visibility = GONE
                                            noNetwork.visibility = VISIBLE
                                        }
                                    }
                                    ConstantObjects.slideUp(
                                        mBinding.commentsView,
                                        mBinding.mainItemLayout
                                    )
                                    mBinding.addCommentSub.visibility = GONE
                                    mBinding.addCommentMain.text = "".toEditable()
                                    openComments(false)
                                }
                            }
                            "shareClick" -> {
                                shareFile(model, 1)
                            }
                            "hidecommentview" -> {
                                recyclerView!!.enableVerticalScroll(true)
                                mBinding.itemVideoExoplayer.setOnTouchListener(
                                    clickFrameSwipeListener
                                )
                                mBinding.mainItemLayout.visibility = VISIBLE
                                ConstantObjects.slideDown(
                                    mBinding.commentsView,
                                    mBinding.mainItemLayout
                                )
                                mBinding.addCommentSub.visibility = VISIBLE
                            }
                            "hideShareSection" -> {
                                mBinding.addCommentSub.visibility = VISIBLE
                                recyclerView!!.enableVerticalScroll(true)
                                mBinding.itemVideoExoplayer.setOnTouchListener(
                                    clickFrameSwipeListener
                                )
                                ConstantObjects.slideDown(
                                    mBinding.shareView,
                                    mBinding.mainItemLayout
                                )
                            }
                            "cancelShare" -> {
                                mBinding.addCommentSub.visibility = VISIBLE
                                recyclerView!!.enableVerticalScroll(true)
                                mBinding.itemVideoExoplayer.setOnTouchListener(
                                    clickFrameSwipeListener
                                )
                                ConstantObjects.slideDown(
                                    mBinding.shareView,
                                    mBinding.mainItemLayout
                                )
                            }
                            "playBigButton" -> {
                                PlayerViewAdapter.playCurrentPlayingVideo()
                            }
                            "addCommentMain" -> {
                                startActivityForResult(
                                    Intent(
                                        this@ViewPostActivity,
                                        SendCommentActivity::class.java
                                    ).apply {
                                        putExtra(
                                            "COMMENT_TEXT",
                                            mBinding.addCommentMain.text.toString()
                                        )
                                        putExtra("POST_ID", mBinding.dataModel!!.id)
                                    }, LAUNCH_COMMENT_ACTIVITY
                                )
                            }
                            "mfclick" -> {
                                if (player!!.playWhenReady) {
                                    PlayerViewAdapter.pauseCurrentPlayingVideo()
                                    mBinding.playBigButton.visibility = VISIBLE
                                } else {
                                    PlayerViewAdapter.playCurrentPlayingVideo()
                                    mBinding.playBigButton.visibility = GONE
                                }
                            }
                            "sendCommentView" -> {
                                if (mBinding.addCommentMain.text.toString().isNotBlank()) {
                                    mBinding.dataModel!!.id?.let {
                                        mViewModel.addComment(
                                            it,
                                            mBinding.addCommentMain.text.toString()
                                        )
                                    }
                                }
                                mBinding.addCommentMain.text = "".toEditable()
                            }
                            else -> {
                            }
                        }
                    }
                }
            })
        }
    }

    private fun shareFile(model: PostAssociated?, i: Int) {
        runWithPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            mBinding?.let { mBinding ->
                if (i == 0) {
                    recyclerView!!.enableVerticalScroll(false)
                    mBinding.itemVideoExoplayer.setOnTouchListener(null)
                    mBinding.addCommentSub.visibility = VISIBLE
                    if (model?.createdBy?.id == Prefs.init().currentUser?.id) {
                        mBinding.reportLayout.visibility = GONE
                        mBinding.deleteLayout.visibility = VISIBLE
                    } else {
                        mBinding.reportLayout.visibility = VISIBLE
                        mBinding.deleteLayout.visibility = GONE
                    }
                    ConstantObjects.slideUp(
                        mBinding.shareView,
                        mBinding.mainItemLayout
                    )
                    ConstantObjects.slideDown(
                        mBinding.commentsView,
                        mBinding.mainItemLayout
                    )
                } else {
                    mBinding.addCommentSub.visibility = VISIBLE
                    recyclerView!!.enableVerticalScroll(false)
                    mBinding.itemVideoExoplayer.setOnTouchListener(null)
                    mBinding.reportLayout.visibility = GONE
                    mBinding.deleteLayout.visibility = GONE
                    ConstantObjects.slideUp(
                        mBinding.shareView,
                        mBinding.mainItemLayout
                    )
                    ConstantObjects.slideDown(
                        mBinding.commentsView,
                        mBinding.mainItemLayout
                    )
                }
            }
        }
    }

    private fun setRecyclerScrollListener() {
        scrollListener = object : RecyclerViewScrollListener() {
            override fun onItemIsFirstVisibleItem(index: Int, lastItemIndex: Int) {
                updateViewTime(lastItemIndex)
                findLayoutByPosition(index)
            }
        }
        recyclerView!!.addOnScrollListener(scrollListener)
    }

    private fun updateViewTime(index: Int) {
        try {
            mViewModel.getPosts().value?.let {
                if (index > -1 && index < it.size) {
                    mViewModel.postViewDurationRequest.apply {
                        player?.let {
                            viewPortion =
                                (it.currentPosition.toDouble() / it.duration.toDouble()).roundOff()
                        }
                        postId = it[index].id
                    }
                    mViewModel.updatePostViewDuration()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findLayoutByPosition(index: Int) {
        if (index != -1) {
            PlayerViewAdapter.playIndexThenPausePreviousPlayer(index)
        }

        layoutManager?.let { layoutManager ->
            val position = layoutManager.findFirstCompletelyVisibleItemPosition()

            if (currentPage != position) {
                currentPage = index
                val view: View? = recyclerView?.layoutManager?.findViewByPosition(position)
                if (view != null) {
                    val binding1: LayoutViewPostRecyclerItemBinding? =
                        DataBindingUtil.bind(view)
                    setItemBinding(binding1)
                }
                val loadedItem = mViewModel.getPosts().value!!.size
                if (position == loadedItem - 5 || position == loadedItem - 1) {
                    if (isNetworkConnected()) {
                        apiVideosList()
                    } else {
                        showMessage(getString(R.string.connectErr))
                    }
                }

            }
            if (!isNetworkConnected()) {
                showMessage(getString(R.string.connectErr))
            }
        }
    }

    private fun apiVideosList() {
        if (!TextUtils.isEmpty(url))
            mViewModel.getMorePosts()
    }

    private fun setUpViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(ViewPostViewModal::class.java)
        mViewModel.setPostUpdateListener(this)
        mViewModel.setCommentListener(this)
    }

    private fun observeList() {
        mViewModel.apply {

            showPaginationLoading.observe(this@ViewPostActivity, Observer {
                it?.let {
                    if (it) {
                        paginationProgress.visibility = View.VISIBLE
                    } else {
                        paginationProgress.visibility = View.GONE
                    }
                }
            })

            getPosts().observe(this@ViewPostActivity, Observer {
                startPreLoadingService(it)
                mPostAdapter.updateListData(it)
                if (postPosition > -1) {
                    recyclerView?.scrollToPosition(postPosition)
                    postPosition = -1
                }
            })

            comments.observe(this@ViewPostActivity, Observer {
                if (it.isEmpty()) {
                    mBinding?.lNoDataFoundComments?.visibility = VISIBLE
                    mBinding?.commentsList?.visibility = GONE
                } else {
                    mBinding?.lNoDataFoundComments?.visibility = GONE
                    mBinding?.commentsList?.visibility = VISIBLE
                    mCommentAdapter.setNewItems(it)
                }
            })

            toastMessage.observe(this@ViewPostActivity, { message ->
                if (!TextUtils.isEmpty(message))
                    Validator.showMessage(message)
            })

            clickedUserId.observe(this@ViewPostActivity, {
                if (!it.isNullOrEmpty()) {
                    startActivity(
                        Intent(
                            this@ViewPostActivity,
                            DashboardActivity::class.java
                        ).apply {
                            putExtra(
                                getString(R.string.intent_key_from),
                                getString(R.string.intent_key_show_user_from_post)
                            )
                            putExtra(getString(R.string.intent_key_user_id), it)
                        })
                    slideRightToLeft()
                }
            })

            showLoading.observe(this@ViewPostActivity, Observer {
                if (it) showProgressDialog()
                else hideProgressDialog()
            })

            removePost.observe(this@ViewPostActivity, {
                mViewModel.getPosts().value?.let { posts ->
                    it?.let {
                        if (it > -1) {
                            posts.removeAt(it)
                            if (mPostAdapter.itemCount == 1) {
                                mPostAdapter.removeItemOnPosition(it)
                                finish()
                            } else {
                                mPostAdapter.removeItemOnPosition(it)
                                Handler(mainLooper).postDelayed({
                                    PlayerViewAdapter.playIndexThenPausePreviousPlayer(it)
                                }, 1000)
                            }
                        }
                    }
                }
            })

            noPostFound.observe(this@ViewPostActivity, {
                if (it) {
                    noPostFound.postValue(false)
                    finish()
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    private fun setScreenFullSize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setUpUI() {
        registerReceiver(
            downLoadBroadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        updatePostEvent = UpdatePostEvent()
        setStatusBarTransparentFlag()
        setContentView(R.layout.activity_view_post)
        recyclerView = findViewById(R.id.recycler_view)
        mInitialTextureWidth = Resources.getSystem().displayMetrics.widthPixels
        readMoreOption = ReadMoreOption.Builder(this)
            .build()
    }

    private fun readArguments() {
        val bundle = intent.getBundleExtra(getString(R.string.intent_key_show_post))
        val list = DataHolder.data
        val position = bundle?.getInt(getString(R.string.intent_key_post_position), 0)
        postPosition = position ?: -1
        list?.let {
            posts.addAll(it)
            mViewModel.setPosts(posts as ArrayList<PostAssociated>)
        }
    }

    private fun setAdapter() {
        PlayerViewAdapter.addCaching(this)
        mPostAdapter =
            ViewPostRecyclerAdapter(ArrayList(), mViewModel.mClickHandler)
        layoutManager = LinearLayoutManager(applicationContext)
        recyclerView?.apply {
            enableVerticalScroll(true)
            layoutManager = this@ViewPostActivity.layoutManager
            adapter = mPostAdapter
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
        PlayerViewAdapter.playCurrentPlayingVideo()
        mBinding?.let {
            mViewModel.getPostDetail(it.dataModel!!.id!!)
        }
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
        PlayerViewAdapter.pauseCurrentPlayingVideo()
    }

    override fun onStop() {
        super.onStop()
        updateViewTime(currentPage)
        player?.playWhenReady = false
        PlayerViewAdapter.pauseCurrentPlayingVideo()
    }

    private fun hashtagExist(dataModel: PostAssociated?, hashtag: String): String {
        dataModel?.hashTags?.let {
            for (i in it.indices) {
                if (hashtag.equals(it[i].tagName, ignoreCase = true)) {
                    return it[i].id!!
                }
            }
        }
        return ""
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setItemBinding(binding1: LayoutViewPostRecyclerItemBinding?) {
        hideKeyboard()
        if (binding1 != null) {
            mBinding = binding1
        } else {
            return
        }

        mBinding?.let { mBinding ->
            mBinding.dataModel!!.id?.let { mViewModel.getPostDetail(it) }

//            posts[currentPage].medias?.let {
//                if (it.isNotEmpty()) {
//                    val mediaUrl =
//                        if (it[0].isVideo == true) it.get(0).mediaUrl else it.get(1).mediaUrl
//
//                    mBinding.itemVideoExoplayer.resizeMode =
//                        if (mediaUrl?.endsWith("mp4")!!) AspectRatioFrameLayout.RESIZE_MODE_FIT else AspectRatioFrameLayout.RESIZE_MODE_ZOOM
//                }
//            }

            player = mBinding.itemVideoExoplayer.player
            ConstantObjects.slideDown(mBinding.commentsView, mBinding.mainItemLayout)
            ConstantObjects.slideDown(mBinding.shareView, mBinding.mainItemLayout)
            mBinding.descriptionView.visibility = GONE
            mBinding.itemVideoExoplayer.exo_progress.setScrubberColor(
                ContextCompat.getColor(
                    this@ViewPostActivity, R.color.transparent
                )
            )

            mViewModel.getPosts().value?.get(currentPage)?.description?.let {
                //getHashTag
                val hashtagList = getHashtagList(it)
                var description = it
                for (i in 0 until hashtagList.size) {
                    description = description.replace(hashtagList[i], hashtagList[i].toLowerCase())
                }
                readMoreOption!!.addReadMoreTo(
                    mBinding.descriptionText,
                    description, this, this
                )
            }
            mBinding.playBigButton.visibility = GONE
            mBinding.addCommentSub.visibility = VISIBLE
            mBinding.itemVideoExoplayer.setOnTouchListener(clickFrameSwipeListener)
            mBinding.mainItemLayout.visibility = VISIBLE
            mBinding.addCommentSub.visibility = VISIBLE
            mBinding.itemVideoExoplayer.bottom_controller.visibility = GONE
            setCommentAdapter()
            player!!.addListener(object : Player.EventListener {
                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    mBinding.playBigButton.visibility = GONE
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {

                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)

                    if (playbackState == Player.STATE_READY) {
                        if (player!!.playWhenReady) {
                            mBinding.playBigButton.visibility = GONE
                        }
                    }
                }
            })
        }
    }

    private fun getHashtagList(text: CharSequence): ArrayList<String> {
        val hashtagList = mutableListOf<String>()
        var startIndexOfNextHashSign: Int
        var index = 0
        while (index < text.length - 1) {
            val sign = text[index]
            var nextNotLetterDigitCharIndex =
                index + 1 // we assume it is next. if if was not changed by findNextValidHashTagChar then index will be incremented by 1
            if (sign == '#') {
                startIndexOfNextHashSign = index
                nextNotLetterDigitCharIndex =
                    findNextValidHashTagChar(text, startIndexOfNextHashSign)
                val hashtag = text.substring(startIndexOfNextHashSign, nextNotLetterDigitCharIndex)
                hashtagList.add(hashtag)
            }
            index = nextNotLetterDigitCharIndex
        }
        return hashtagList as ArrayList<String>
    }

    private fun findNextValidHashTagChar(text: CharSequence, start: Int): Int {
        var nonLetterDigitCharIndex = -1 // skip first sign '#"
        for (index in start + 1 until text.length) {
            val sign = text[index]
            val isValidSign =
                sign.equals("#") || Character.isSpaceChar(sign) || containsIllegalCharacters(sign.toString())
            if (isValidSign) {
                nonLetterDigitCharIndex = index
                break
            }
        }
        if (nonLetterDigitCharIndex == -1) {
            // we didn't find non-letter. We are at the end of text
            nonLetterDigitCharIndex = text.length
        }
        return nonLetterDigitCharIndex
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCommentAdapter() {
        mBinding?.apply {
            commentsList.apply {
                adapter = mViewModel.mCommentAdapter
                (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }

            commentsList.addOnScrollListener(object : RecyclerViewScrollListener() {
                override fun onItemIsFirstVisibleItem(index: Int, lastItemIndex: Int) {
                    if (commentsList.childCount > 0) {
                        val view = commentsList.getChildAt(commentsList.childCount - 1)
                        val diff = view.bottom - (commentsList.height + commentsList.scrollY)
                        val offset = mViewModel.getCurrentOffset()
                        if (diff == 0 && offset % 15 == 0 && !mViewModel.allCommentsLoaded) {
                            mViewModel.getPostComment(offset)
                        }
                    }
                }
            })
            commentsList.addOnItemTouchListener(object : OnItemTouchListener {
                override fun onInterceptTouchEvent(
                    rv: RecyclerView,
                    e: MotionEvent
                ): Boolean {
                    val action = e.action
                    when (action) {
                        MotionEvent.ACTION_MOVE -> rv.parent
                            .requestDisallowInterceptTouchEvent(true)
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
            addCommentMain.setOnTouchListener(View.OnTouchListener { v, event ->
                if (addCommentMain.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })
        }
    }

    override fun onBackPressed() {
        mBinding?.let {
            when {
                mBinding!!.shareView.visibility == VISIBLE -> {
                    hideKeyboard()
                    recyclerView!!.enableVerticalScroll(true)
                    mBinding!!.itemVideoExoplayer.setOnTouchListener(clickFrameSwipeListener)
                    ConstantObjects.slideDown(
                        mBinding!!.shareView,
                        mBinding!!.mainItemLayout
                    )
                }

                mBinding!!.commentsView.visibility == VISIBLE -> {
                    hideKeyboard()
                    mBinding!!.addCommentSub.visibility = VISIBLE
                    recyclerView!!.enableVerticalScroll(true)
                    mBinding!!.itemVideoExoplayer.setOnTouchListener(clickFrameSwipeListener)
                    ConstantObjects.slideDown(
                        mBinding!!.commentsView,
                        mBinding!!.mainItemLayout
                    )
                }
                else -> {
                    mBinding!!.mainItemLayout.visibility = VISIBLE
                    hideKeyboard()
                    recyclerView!!.enableVerticalScroll(true)
                    mBinding!!.itemVideoExoplayer.setOnTouchListener(clickFrameSwipeListener)
                    super.onBackPressed()
                }
            }
        }
    }

    override fun setLang(strLang: String) {}

    private var clickFrameSwipeListener = object : OnSwipeTouchListener(true) {
        var diffTime = -1f
        var finalTime = -1f
        override fun onMove(dir: Direction, diff: Float) {
            try {
                // If swipe is not enabled, move should not be evaluated.
                if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                    player?.let { player ->
                        recyclerView!!.enableVerticalScroll(false)
                        diffTime = if (player.duration <= 60) {
                            player.duration.toFloat() * diff / mInitialTextureWidth.toFloat()
                        } else {
                            60000.toFloat() * diff / mInitialTextureWidth.toFloat()
                        }
                        if (dir == Direction.LEFT) {
                            diffTime *= -1f
                        }
                        finalTime = player.currentPosition + diffTime
                        if (finalTime < 0) {
                            finalTime = 0f
                        } else if (finalTime > player.duration) {
                            finalTime = player.duration.toFloat()
                        }
                        diffTime = finalTime - player.currentPosition

                        val progressText =
                            ConstantObjects.getDurationString(finalTime.toLong(), false) +
                                    " / " + ConstantObjects.getDurationString(
                                abs(player.duration),
                                false
                            )
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())
                        val maxseconds = TimeUnit.MILLISECONDS.toSeconds(abs(player.duration))
                        val percentage = seconds * 100 / maxseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mBinding!!.seekprogress.setProgress(percentage.toInt(), true)
                        } else {
                            mBinding!!.seekprogress.progress = percentage.toInt()
                        }
                        mBinding!!.positionTextview.text = progressText
                    }
                } else {
                    recyclerView!!.enableVerticalScroll(true)
                    mBinding!!.gestureLayout.visibility = GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onClick() {
            mBinding?.let { mBinding ->
                mBinding.descriptionView.visibility = GONE
                recyclerView!!.enableVerticalScroll(true)
                when {
                    mBinding.shareView.visibility == VISIBLE -> {
                        ConstantObjects.slideDown(
                            mBinding.shareView,
                            mBinding.mainItemLayout
                        )
                    }
                    mBinding.commentsView.visibility == VISIBLE -> {
                        mBinding.addCommentSub.visibility = VISIBLE
                        ConstantObjects.slideDown(
                            mBinding.commentsView,
                            mBinding.mainItemLayout
                        )
                    }
                    else -> {
                        if (mBinding.mainItemLayout.visibility == GONE) {
                            mBinding.mainItemLayout.visibility = VISIBLE
                            mBinding.addCommentSub.visibility = VISIBLE
                            mBinding.itemVideoExoplayer.bottom_controller.visibility = GONE
                            mBinding.itemVideoExoplayer.fake_click.visibility = VISIBLE
                            mBinding.itemVideoExoplayer.exo_progress.setScrubberColor(
                                ContextCompat.getColor(
                                    this@ViewPostActivity,
                                    R.color.transparent
                                )
                            )
                        } else {
                            mBinding.mainItemLayout.visibility = GONE
                            mBinding.addCommentSub.visibility = GONE
                            mBinding.itemVideoExoplayer.bottom_controller.visibility = VISIBLE
                            mBinding.itemVideoExoplayer.fake_click.visibility = GONE
                            mBinding.itemVideoExoplayer.exo_progress.setScrubberColor(
                                ContextCompat.getColor(
                                    this@ViewPostActivity,
                                    R.color.white
                                )
                            )
                        }
                        PlayerViewAdapter.playCurrentPlayingVideo()
                        mBinding.gestureLayout.visibility = GONE
                    }
                }
            }
        }

        override fun onDoubleTap(event: MotionEvent) {
            mBinding?.let {
                it.descriptionView.visibility = GONE
                recyclerView!!.enableVerticalScroll(true)
                it.gestureLayout.visibility = GONE
                if (!it.likebtn.isLiked) {
                    mViewModel.likePost(it.dataModel!!.id!!, 0)
                }
                mPostAdapter!!.doubleTap(
                    it.likebtn, it.heartAnim
                )
            }
        }

        override fun onAfterMove() {
            if (finalTime >= 0) {
                seekTo(finalTime.toInt())
            }
            mBinding!!.gestureLayout.visibility = GONE
            player!!.playWhenReady = true
            recyclerView!!.enableVerticalScroll(true)
        }

        override fun onBeforeMove(dir: Direction) {
            if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                player!!.playWhenReady = !player!!.playWhenReady
                mBinding!!.gestureLayout.visibility = VISIBLE
                recyclerView!!.enableVerticalScroll(false)
            } else {
                player!!.playWhenReady = true
                mBinding!!.gestureLayout.visibility = GONE
                recyclerView!!.enableVerticalScroll(true)
            }
        }
    }

    fun seekTo(pos: Int) {
        val duration = player!!.duration
        if (pos >= duration) {
            player!!.seekTo(duration)
        } else {
            player!!.seekTo(pos.toLong())
        }
    }

    fun openComments(isKeyboard: Boolean) {
        if (isKeyboard) {
            startActivityForResult(Intent(this, SendCommentActivity::class.java).apply {
                putExtra("COMMENT_TEXT", mBinding!!.addCommentMain.text.toString())
                putExtra("POST_ID", mBinding!!.dataModel!!.id)
            }, LAUNCH_COMMENT_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            LAUNCH_COMMENT_ACTIVITY -> {
                val resultString = data!!.getStringExtra("result")
                val postID = data.getStringExtra("POST_ID")
                postID?.let {
                    fetchComment(it)
                }
                resultString?.let {
                    mBinding!!.addCommentMain.text = it.toEditable()
                }
            }
        }
    }

    private fun fetchComment(postId: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            mViewModel.apply {
                queryParams.postId = postId
                comments.value?.clear()
                getPostComment(0)
                mBinding!!.addCommentMain.text?.clear()
            }
        }, 1000)
    }

    override fun onHashTagClicked(hashTag: String?) {
        if (hasInternet()) {
            hashTag?.let { hashtag ->
                mBinding?.let {
                    val hashTagId: String = hashtagExist(it.dataModel, hashtag)
                    if (hashTagId.isNotEmpty()) {
                        startActivity(
                            Intent(this@ViewPostActivity, DashboardActivity::class.java).apply {
                                putExtra(
                                    getString(R.string.intent_key_from),
                                    getString(R.string.intent_key_hashtag)
                                )
                                putExtra(
                                    getString(R.string.intent_key_hashtag),
                                    hashTagId
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onExpand(isExpanded: Boolean?) {
        if (isExpanded!!) {
            mBinding!!.descriptionView.visibility = VISIBLE
        } else {
            mBinding!!.descriptionView.visibility = GONE
        }
    }

    override fun onPostUpdate(data: PostAssociated, position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            data.let {
                mBinding?.apply {
                    it.followedByMe?.let { isFollowing ->
                        if (isFollowing || it.createdBy!!.id.equals(Prefs.init().currentUser!!.id)) {
                            this.followClick.visibility = GONE
                        } else {
                            this.followClick.visibility = VISIBLE
                        }
                        updateFollowList(it.createdBy?.id!!, isFollowing)
                    }
                    it.likedByUser?.let {
                        likebtn.isLiked = it
                    }
                    it.savedByUser?.let {
                        bookmarkbtn.isLiked = it
                    }
                    updatePostEvent.post = it
                    dataModel!!.hashTags = data.hashTags
                    likesCount.text = it.noOfLikes
                    shareCount.text = it.noOfShares
                    commentsCount.text = it.noOfComments
                    totalComments.text = if (it.noOfComments.equals("0")) {
                        getString(R.string.comment)
                    } else {
                        "${it.noOfComments} ${getString(R.string.commentes)}"
                    }
                }
            }
        }
    }

    private fun updateFollowList(userId: String, isFollowing: Boolean) {
        val temp = ArrayList<PostAssociated>()
        mViewModel.getPosts().value?.forEach { post ->
            if (post.createdBy?.id.equals(userId)) {
                post.followedByMe = isFollowing
            }
            temp.add(post)
        }
        mPostAdapter?.updateListData(temp)
    }

    override fun onFollowUpdate(userId: String) {
        mBinding?.apply {
            val temp = ArrayList<PostAssociated>()
            mViewModel.getPosts().value?.let {
                it.forEach { post ->
                    if (post.createdBy?.id.equals(userId)) {
                        post.followedByMe = true
                    }
                    temp.add(post)
                }
                mPostAdapter?.updateListData(it)
            }
        }
    }

    override fun onPostComment() {
        mBinding?.dataModel?.id?.let {
            fetchComment(it)
        }
    }

    inner class DownLoadBroadcast : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            hideProgressDialog()
            shareFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                filePath
            )

            currentSharePost?.let {
                when (it.socialPosition) {
                    0 -> shareOnWhatsApp(it.post!!, it.adapterPosition!!)
                    1 -> shareOnMessages(it.post!!, it.adapterPosition!!)
                    2 -> shareOnWhatsAppStatus(it.post!!, it.adapterPosition!!)
                    3 -> shareOnInstagram(it.post!!, it.adapterPosition!!)
                    4 -> shareOnInstagramStory(it.post!!, it.adapterPosition!!)
                    5 -> shareOnFB(it.post!!, it.adapterPosition!!)
                }
                currentSharePost = null
            }
        }
    }

    override fun onWhatsappShareClick(model: PostAssociated, adapterPosition: Int) {
        if (hasInternet()) {
            model.medias?.get(1)?.mediaUrl?.let {
                downloadPost(it)
                currentSharePost =
                    SharePost(model, adapterPosition, 0)
            }
        }
    }

    override fun onMessageShareClick(model: PostAssociated, adapterPosition: Int) {
        if (hasInternet()) {
            model.medias?.get(1)?.mediaUrl?.let {
                downloadPost(it)
                currentSharePost =
                    SharePost(model, adapterPosition, 1)
            }
        }
    }

    override fun onStatusShareClick(model: PostAssociated, adapterPosition: Int) {
        if (hasInternet()) {
            model.medias?.get(1)?.mediaUrl?.let {
                downloadPost(it)
                currentSharePost =
                    SharePost(model, adapterPosition, 2)
            }

        }
    }

    override fun onInstagramShareClick(model: PostAssociated, adapterPosition: Int) {
        if (hasInternet()) {
            model.medias?.get(1)?.mediaUrl?.let {
                downloadPost(it)
                currentSharePost =
                    SharePost(model, adapterPosition, 3)
            }
        }
    }

    override fun onINSShareClick(model: PostAssociated, adapterPosition: Int) {
        if (hasInternet()) {
            model.medias?.get(1)?.mediaUrl?.let {
                downloadPost(it)
                currentSharePost =
                    SharePost(model, adapterPosition, 4)
            }
        }
    }

    override fun onFacebookShareClick(model: PostAssociated, adapterPosition: Int) {
        if (hasInternet()) {
            model.medias?.get(1)?.mediaUrl?.let {
                downloadPost(it)
                currentSharePost =
                    SharePost(model, adapterPosition, 5)
            }
        }
    }

    override fun deletePost(model: PostAssociated, adapterPosition: Int) {
        DialogUtil.build(this@ViewPostActivity) {
            title = "DELETE"
            message = "DELETE"
            dialogType = DialogUtil.DialogType.DELETE
            yesNoDialogClickListener = object : DialogUtil.YesNoDialogClickListener {
                override fun onClickYes() {
                    if (hasInternet())
                        mViewModel.deletePost(model.id!!, adapterPosition)
                }

                override fun onClickNo() {}
            }
        }
    }

    //Emoji Helper and clickHandler
    private fun setEmoji(emojiValue: Int) {
        mBinding?.apply {
            addCommentMain.append(ConstantObjects.getEmojiByUnicode(emojiValue))
            addCommentMain.text?.length?.let {
                addCommentMain.setSelection(it)
            }
        }
    }

    private fun downloadPost(url: String) {
        if (Util.checkIfHasNetwork()) {
            val mediaUrl = url.toMp4Url()
            showProgressDialog()
            shareFile = File(
                getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath,
                filePath
            )

            shareFile?.let {
                if (it.exists()) {
                    it.delete()
                }
                it.createNewFile()

                AndroidNetworking.download(mediaUrl, it.absolutePath, "")
                    .setTag("Media Download")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .startDownload(object : DownloadListener {
                        override fun onDownloadComplete() {
                            hideProgressDialog()
                            currentSharePost?.let {
                                when (it.socialPosition) {
                                    0 -> shareOnWhatsApp(it.post!!, it.adapterPosition!!)
                                    1 -> shareOnMessages(it.post!!, it.adapterPosition!!)
                                    2 -> shareOnWhatsAppStatus(it.post!!, it.adapterPosition!!)
                                    3 -> shareOnInstagram(it.post!!, it.adapterPosition!!)
                                    4 -> shareOnInstagramStory(it.post!!, it.adapterPosition!!)
                                    5 -> shareOnFB(it.post!!, it.adapterPosition!!)
                                }
                                currentSharePost = null
                            }
                        }

                        override fun onError(error: ANError?) {
                            hideProgressDialog()
                            Validator.showMessage("Unable to download video, Please try again later.")
                        }
                    })
            }
        } else {
            Validator.showMessage(getString(R.string.no_internet))
        }

//        startService(
//            Intent(
//                this@ViewPostActivity,
//                DownloadFileService::class.java
//            ).apply {
//                putExtra("FILE_URL", mediaUrl)
//                putExtra("FILE_PATH", filePath)
//            }
//        )
    }

    private fun shareOnWhatsApp(
        model: PostAssociated,
        adapterPosition: Int,
    ) {
        shareFile?.let {
            if (Util.isPackageExist(this@ViewPostActivity, "com.whatsapp")) {
                mViewModel.sharePost(model.id!!, adapterPosition)
                val uri: Uri = getUriFromFile(it)
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    type = "video/*"
                    setPackage("com.whatsapp")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, model.description)
                })
            } else {
                Validator.showMessage(getString(R.string.please_install_whatsapp))
            }
        }
    }

    fun shareOnMessages(model: PostAssociated, adapterPosition: Int) {
        shareFile?.let {
            mViewModel.sharePost(model.id!!, adapterPosition)
            val uri: Uri = getUriFromFile(it)
            startActivity(Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_TEXT, model.description)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    fun shareOnWhatsAppStatus(model: PostAssociated, adapterPosition: Int) {
        shareFile?.let {
            if (Util.isPackageExist(this@ViewPostActivity, "com.whatsapp")) {
                mViewModel.sharePost(model.id!!, adapterPosition)
                val uri: Uri = getUriFromFile(it)
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    type = "*/*"
                    setPackage("com.whatsapp")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, model.description)
                })
            } else {
                Validator.showMessage(getString(R.string.please_install_whatsapp))
            }
        }
    }

    fun shareOnInstagram(model: PostAssociated, adapterPosition: Int) {
        shareFile?.let {
            if (Util.isPackageExist(this@ViewPostActivity, "com.instagram.android")) {
                mViewModel.sharePost(model.id!!, adapterPosition)
                val uri: Uri = getUriFromFile(it)
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    type = "video/*"
                    setPackage("com.instagram.android")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, model.description)
                })
            } else {
                Validator.showMessage(getString(R.string.please_install_instagram))
            }
        }
    }

    fun shareOnInstagramStory(model: PostAssociated, adapterPosition: Int) {
        shareFile?.let {
            if (Util.isPackageExist(this@ViewPostActivity, "com.instagram.android")) {
                mViewModel.sharePost(model.id!!, adapterPosition)
                val uri: Uri = getUriFromFile(it)
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    type = "video/*"
                    setPackage("com.instagram.android")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, model.description)
                })
            } else {
                Validator.showMessage(getString(R.string.please_install_instagram))
            }
        }
    }

    fun shareOnFB(model: PostAssociated, adapterPosition: Int) {
        shareFile?.let {
            if (Util.isPackageExist(this@ViewPostActivity, "com.facebook.katana")) {
                mViewModel.sharePost(model.id!!, adapterPosition)
                val uri: Uri = getUriFromFile(it)
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    type = "video/*"
                    setPackage("com.facebook.katana")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, model.description)
                })
            } else {
                Validator.showMessage(getString(R.string.please_install_fb))
            }
        }
    }


    override fun onSmileyEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onCoolEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onCwlEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onWinkEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onKissEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onShyEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onHeartEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }

    override fun onHugEmojiClick(emojiValue: Int) {
        setEmoji(emojiValue)
    }
}

data class SharePost(
    @field:SerializedName("post")
    val post: PostAssociated? = null,

    @field:SerializedName("adapterPosition")
    val adapterPosition: Int? = null,

    @field:SerializedName("socialPosition")
    val socialPosition: Int? = -1,
)

