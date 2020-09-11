package com.demo.post_details

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demo.R
import com.demo.activity.PhotoViewActivity
import com.demo.base.AsyncViewController
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.chips.ChipsFragment
import com.demo.commentList.CommentsFragment
import com.demo.databinding.FragmentPostDetailsBinding
import com.demo.model.request.RequestGetCommentsReplyList
import com.demo.model.request.RequestSaveComment
import com.demo.model.request.User
import com.demo.model.response.Post
import com.demo.model.response.ResponseNotificationComment
import com.demo.model.response.UserComment
import com.demo.util.*
import com.demo.util.aws.AmazonUtil
import com.demo.util.aws.AwsUploadImageListener
import com.google.android.exoplayer2.ExoPlayer
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class PostDetailsFragment : BaseFragment(), PhotosAdapter.PhotoClickListener,
    AudioRecordView.RecordingListener,
    AwsUploadImageListener {
    override fun onUploadProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

    }

    lateinit var wake: PowerManager.WakeLock
    val VOICE_PERMISSION_REQUEST_CODE = 100
    val STORAGE_PERMISSION_REQUEST_CODE = 101
    val TAG = "MAIN"
    var audioPlayerName: String? = null
    private var isRecording = false
    private var millis: Long? = null
    private var root: String? = null
    private var time: Long = 0
    var valuepermisstion: Boolean = false

    lateinit var recorder: MediaRecorder
    lateinit var mBinding: FragmentPostDetailsBinding
    lateinit var mViewModel: PostDetailViewModel
    private lateinit var carouselAdapter: PhotosAdapter
    private lateinit var relatedPostsAdapter: RelatedPostsAdapter
    private lateinit var commentsAdapter: RelatedPostsAdapter
    val mClickHandler: ClickHandler by lazy { ClickHandler() }

    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        // activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        /*val powerManager =requireContext(). getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock: PowerManager.WakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyApp::MyWakelockTag"
        )
        wake=wakeLock;
        wakeLock.acquire()*/
        progressDialog = ProgressDialog(activity)
        createDirectoriesIfNeeded()
        millis = Calendar.getInstance().timeInMillis
        audioPlayerName = root + "/" + millis + "audio.mp4"

        mViewModel.parseBundle(arguments)
        mViewModel.conte = this.activity!!
        mViewModel.callGetPostDetailsApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentPostDetailsBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@PostDetailsFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }

            setupToolbar()
            iniUi()
            mViewModel.callGetBoardList()
        }
        val external = File(Environment.getExternalStorageDirectory(), "Recorder")
        if (!external.exists()) external.mkdir()
        childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
            (this as? CommentsFragment)?.dataRefreshCallback = ::onCommentsRefresh
        }



        if (!hasVoicePermission() || !hasStoragePermission()) {

            //  askForVoicePermission()
        }
        /*  var displayMetricsD: DisplayMetrics
              displayMetricsD=  activity?.resources!!.displayMetrics

              var dpWidth=displayMetricsD.widthPixels/displayMetricsD.density

              var onesize=displayMetricsD.widthPixels/displayMetricsD.density/3

              var orgnalsize=onesize*4
      */

        //  mBinding.viewPager.layoutParams.height=orgnalsize.toInt()

        /*    DisplayMetrics displayMetrics = acti.getResources().getDisplayMetrics();
    float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
           */

        return mBinding.root
    }


    fun onCommentsRefresh() {
        upadteMessage()
    }

    private fun iniUi() {
        Util.updateStatusBarColor("#FFFFFF", activity as FragmentActivity)


        mBinding.recordingView.recordingListener = this
        mBinding.recordingView.sendView.setOnClickListener {
            /**
             * Comment send
             */
            val messageText = getTextInput()
            if (messageText.trim { it <= ' ' }.length == 0) {
                return@setOnClickListener
            }

            mViewModel.requestSaveComment.get()!!.apply {
                user?.userId = Prefs.init().loginData!!.userId
                isUrl = false
                commentId = 0
                postId = mViewModel.postId
                commentText = mBinding.recordingView.messageView.text.toString().trim()

            }
            mViewModel.callSaveCommentApi()
            clearTextInput()
        }
        /*  mBinding.recordingView.getimageViewMicView().setOnClickListener{

          }*/
        mBinding.recordingView.messageView.setOnEditorActionListener { v, actionId, event -> false }
        mBinding.recordingView.attachmentView.setOnClickListener {
            /**
             * image save
             */
        }
    }

    private fun setupToolbar() {
        (activity as? BaseActivity)?.setSupportActionBar(mBinding.toolbar)
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(PostDetailViewModel::class.java)
    }

    fun upadteMessage() {
        mViewModel.callGetCommentsListApi()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        getParcelAndConsume(ParcelKeys.PARCEL_ID_POST_DETAIL) {
            if (it.size() == 0) return@getParcelAndConsume
            it.getSerializable(ParcelKeys.PK_POST)?.apply {
                relatedPostsAdapter.updateItem(this as Post) {
                    it.postId == postId
                }
            }
        }
    }

    private fun setupObserver() {
        mViewModel.responseGetPostDetails.observe(this, Observer lit@{
            if (it.data != null) {
                mViewModel.isMyPost.value =
                    it.data!!.post!!.user.userId == Prefs.init().loginData!!.userId

                if (it.data!!.post!!.user.userId == Prefs.init().loginData!!.userId) {
                    //  btn_follow

                }




                mBinding.tvCommentCount.visibility = View.VISIBLE
                mBinding.tvCommentCount.text = getString(R.string.no_comment)
                if (it.data?.post?.images!!.length > 0)
                    it.data?.post?.images?.apply {
                        val urls = Util.getUrlListFromString(this)
                        if (urls.isNotEmpty()) {
                            setupCarousel(urls, false, "")
                        }
                    }
                it.data?.post?.video.apply {
                    if (!this.isNullOrEmpty()) {
                        setupCarousel(this.split(","), true, it.data?.post?.video)
                    }
                }
                if (it.data?.commentList != null)
                    if (it.data?.commentList?.size ?: 0 > 0) {
                        childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
                            mBinding.tvCommentCount.visibility = View.VISIBLE
                            mBinding.tvMoreComments.visibility = View.VISIBLE

                            mBinding.tvCommentCount.text = "" + it!!.data!!.totalComments + " " + resources.getString(
                                R.string.comments
                            )
                            mBinding.commentText.text = "" + it.data!!.totalComments
                            mBinding.invalidateAll()
                            (this as CommentsFragment).feedCommentsData(it.data!!.commentList)
                            if (it.data != null) {
                                getReply(it.data!!.commentList, replyCountPosition)
                            }
                        }
                    }

                val hashTags = it.data?.post?.hashTags
                if (hashTags?.isNotBlank() == true) {
                    childFragmentManager.findFragmentById(R.id.chip_group)?.apply {
                        (this as ChipsFragment).init(hashTags.split(",").toList())
                    }
                }

                it.data!!.post!!.apply {
                    mViewModel.isUserBeingFollowed.set(user.iFollow)
                    mViewModel.isPostLiked.set(postLiked)
                    mViewModel.likeCount.set(likeCount)
                }

                mBinding.tvCommentCount.visibility = View.VISIBLE
                //fetch related posts
                mViewModel.callRelatedPostsApi()

            }
        })

        mViewModel.responseSaveComment.observe(this, Observer {
            if (it.data != null) {
                mViewModel.callGetCommentsListApi()
                //mViewModel.callGetPostDetailsApi()

            }
        })

        /*  mViewModel.responseLikeUnLikeComment.observe(this, Observer {
              if (it.data != null) {

              }
          })*/

        mViewModel.responseGetCommentsList.observe(this, Observer {
            if (it.data != null)
                if (it.data?.size ?: 0 > 0) {
                    childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
                        mBinding.tvCommentCount.visibility = View.VISIBLE
                        mBinding.tvMoreComments.visibility = View.VISIBLE
                        mBinding.tvCommentCount.text = "" + it!!.data!!.size + " " + resources.getString(
                            R.string.comments
                        )
                        mBinding.commentText.text = "" + it.data!!.size
                        mBinding.invalidateAll()
                        (this as CommentsFragment).feedCommentsData(it.data!!)
                        if (it.data != null)
                            getReply(it.data!!, ++replyCountPosition)
                    }
                }
        })
        mViewModel.responseGetCommentsReplyList.observe(this, Observer {
            if (it.data != null)
                if (it.data?.size ?: 0 > 0) {
                    childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
                        mBinding.tvCommentCount.visibility = View.VISIBLE
                        mBinding.tvMoreComments.visibility = View.VISIBLE
                        mBinding.tvCommentCount.text = "" + it!!.data!!.size + " " + resources.getString(
                            R.string.comments
                        )
                        mBinding.invalidateAll()
                        (this as CommentsFragment).feedCommentsReplyData(
                            it.data!!,
                            replyCountPosition
                        )

                        if (mViewModel.position < mViewModel.commentsData.size - 1) {
                            replyCountPosition += 1
                            getReply(mViewModel.commentsData, replyCountPosition)
                        }
                    }
                }
        })


        mViewModel.responseLikeUnLikePostTwo.observe(this, Observer {
            if (it.data != null) {
                val newValue = it.data?.like!!
                var post = relatedPostsAdapter.getItem(mViewModel.position)
                post.postLiked = newValue
                post.likeCount = if (newValue) post.likeCount + 1 else post.likeCount - 1
                relatedPostsAdapter.updatePost(post, mViewModel.position)
            }
        })
        mViewModel.responseLikeUnLikePost.observe(this, Observer {
            if (it.data != null) {
                val newValue = it.data?.like!!
                mViewModel.responseGetPostDetails.value?.data?.post?.apply {
                    postLiked = newValue
                    mViewModel.isPostLiked.set(newValue)
                    if (newValue) {
                        likeCount++
                    } else {
                        likeCount--
                    }
                    mViewModel.likeCount.set(likeCount)

                    putParcel(
                        bundleOf(ParcelKeys.PK_POST to mViewModel.responseGetPostDetails.value!!.data!!.post!!),
                        ParcelKeys.PARCEL_ID_POST_DETAIL
                    )
                }
            }
        })

        mViewModel.responseRelatedPosts.observe(this, Observer {
            if (!it.data.isNullOrEmpty()) {
                setupRelatedPostsRecycler(it.data!! as ArrayList<Post>)
            }
        })

        mViewModel.responseBoardList.observe(this, Observer {
            if (!it.data.isNullOrEmpty()) {
                for (e in it.data!!) {
                    e.postList = null
                }
            }
        })

        mViewModel.responseAddPostToPinboard.observe(this, Observer {
            Validator.showCustomToast(getString(R.string.added_to_board))
        })

        mViewModel.responseFollowUnfollowUser.observe(this, Observer {
            if (it.data != null) {
                mViewModel.responseGetPostDetails.value!!.data!!.post!!.user.iFollow =
                    it.data!!.follow
                mViewModel.isUserBeingFollowed.set(it.data!!.follow)
            }
        })

        mViewModel.responseActionOnPost.observe(this, Observer {
            if (it.data != null) {
                if (it.data!!.actionStatus == "SUCCESS") {
                    Validator.showCustomToast(getString(R.string.reported_successfully))
                }
            }
        })
    }


    var replyCountPosition: Int = 0
    private fun getReply(data: List<UserComment>, position: Int) {
        this.replyCountPosition = position
        if (replyCountPosition <= data.size - 1 && data[replyCountPosition].replyCount != 0) {
            val request = RequestGetCommentsReplyList(
                mViewModel.responseGetPostDetails.value!!.data!!.post!!.user.userId/*40*/,
                /*1*/
                data[replyCountPosition].commentId,
                0,
                data[replyCountPosition].replyCount/*20*/
            )
            mViewModel.requestGetCommentsReplyList.set(request)
            mViewModel.callGetCommentsReplylistApi(replyCountPosition)
        } else if (replyCountPosition < data.size - 1) {
            replyCountPosition += 1
            getReply(data, replyCountPosition)
        }
    }

    private fun setupCarousel(
        docUrls: List<String>?,
        isVideo: Boolean,
        video: String?
    ) {
        carouselAdapter = PhotosAdapter(
            ArrayList(
                docUrls ?: emptyList()
            ), false, isVideo, video!!, activity as Activity
        )
        carouselAdapter.setClickListener(this)
        carouselAdapter.isVideo
        mBinding.viewPager.adapter = carouselAdapter


        mBinding.tabLayout.attachViewPager(mBinding.viewPager)
        mBinding.tabLayout.setDotTint(R.color.grey)
        mBinding.tabLayout.setDotTintRes(R.color.colorPrimary)

        // mBinding.tabLayout.setupWithViewPager(mBinding.viewPager, true)
    }

    private fun setupRelatedPostsRecycler(list: ArrayList<Post>) {
        /*var list2 = ArrayList<Post>()
        var mutableList = mutableListOf<Post>()
       // list.addAll(list2)
        list2.addAll(list)
        for(item in list){
            println("postId "+item.postId)

            println("list[$item]:")
            var itr = list.iterator()

            if ( mViewModel.responseGetPostDetails.value!!.data!!.post!!.postId.toString().equals(item.postId.toString()))
            { Log.e("responseGetPostDetails ", mViewModel.responseGetPostDetails.value!!.data!!.post!!.postId.toString())
                Log.e("postId ",item.postId.toString())
              //  list2.remove(item)

            }


        }*/

        RelatedPostsAdapter(R.layout.layout_dashboard_post_item).let {
            relatedPostsAdapter = it

            it.addClickEventWithView(R.id.card_parent, mClickHandler::onClickRelatedPostItem)
            it.addClickEventWithView(R.id.iv_owner, mClickHandler::onClickPostOwnerProfile)

            it.addClickEventWithView(R.id.iv_fav, mClickHandler::onFavClick)

            it.setNewItems(list)
            relatedPostsAdapter.removeItem { mViewModel.responseGetPostDetails.value!!.data!!.post!!.postId == it.postId }
            mBinding.recyclerRelatedPosts
        }.let {
            it.adapter = relatedPostsAdapter
            it.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        }

        mBinding.recyclerRelatedPosts.itemAnimator = null

    }


    fun onShareClickTest() {
        val resources: Resources = resources

        val emailIntent = Intent()
        emailIntent.action = Intent.ACTION_SEND
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Html.fromHtml(resources.getString(R.string.share_email_native))"
        )
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            "resources.getString(R.string.share_email_subject)"
        )
        emailIntent.type = "message/rfc822"
        val pm: PackageManager = activity!!.packageManager
        val sendIntent =
            Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"


        val openInChooser = Intent.createChooser(
            emailIntent,
            "resources.getString(R.string.share_chooser_text)"
        )

        val resInfo: List<ResolveInfo> = pm.queryIntentActivities(sendIntent, 0)
        val intentList: ArrayList<LabeledIntent> = ArrayList<LabeledIntent>()

        for (i in resInfo.indices) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            val ri = resInfo[i]
            val packageName = ri.activityInfo.packageName
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName)
            } else if (packageName.contains("") || packageName.contains("ftwitteracebook") || packageName.contains(
                    "mms"
                ) || packageName.contains("android.gm")
            ) {
                val intent = Intent()
                intent.component = ComponentName(packageName, ri.activityInfo.name)
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                if (packageName.contains("twitter")) {
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        " resources.getString(R.string.share_twitter)"
                    )
                } else if (packageName.contains("facebook")) {
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "resources.getString(R.string.share_facebook)"
                    )
                } else if (packageName.contains("mms")) {
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        " resources.getString(R.string.share_sms)"
                    )
                } else if (packageName.contains("android.gm")) {
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Html.fromHtml(resources.getString(R.string.share_email_gmail))"
                    )
                    intent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        "resources.getString(R.string.share_email_subject)"
                    )
                }
                intentList.add(
                    LabeledIntent(
                        intent,
                        packageName,
                        ri.loadLabel(pm),
                        ri.icon
                    )
                )
            }
        }
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList)
        requireContext().startActivity(openInChooser)
    }

    fun shareIntentSpecificApps() {
        val intentShareList: ArrayList<Intent> = ArrayList()
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        val resolveInfoList: List<ResolveInfo> =
            requireContext().packageManager.queryIntentActivities(shareIntent, 0)
        for (resInfo in resolveInfoList) {
            val packageName = resInfo.activityInfo.packageName
            val name = resInfo.activityInfo.name
            Log.d(TAG, "Package Name : $packageName")
            Log.d(TAG, "Name : $name")
            if (packageName.contains("com.facebook") ||
                packageName.contains("com.whatsapp") ||
                packageName.contains("com.google.android.apps.plus") ||
                packageName.contains("com.linkedin.android")
            ) {
                /* if (name.contains("com.twitter.android.DMActivity")) {
                     continue
                 }*/
                val intent = Intent()
                intent.component = ComponentName(packageName, name)
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject")
                intent.putExtra(Intent.EXTRA_TEXT, "Your Content")
                intentShareList.add(intent)
            }
        }
        if (intentShareList.isEmpty()) {
            /*Toast.makeText(
                this@MainActivity,
                "No apps to share !",
                Toast.LENGTH_SHORT
            ).show()*/
        } else {
            val chooserIntent =
                Intent.createChooser(intentShareList.removeAt(0), "Share via")
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentShareList.toArray(arrayOf<Parcelable>())
            )
            startActivity(chooserIntent)
        }
    }

    inner class ClickHandler {

        fun onFavClick(position: Int, clickedItem: Post) {

            mViewModel.position = position
            mViewModel.callLikeUnlikePostApi(
                relatedPostsAdapter.getItem(position).postId,
                !relatedPostsAdapter.getItem(position).postLiked
            )

            Handler().postDelayed({

            }, 5000)


        }


        fun onClickPost(pos: Int, clickedPost: ResponseNotificationComment) {
            findNavController().navigate(
                R.id.PostDetailsFragment,
                bundleOf(ParcelKeys.PK_POST_ID to clickedPost.postId)
            )
        }


        fun onClickFollowUnfollow(v: View) {
            val following = mViewModel.isUserBeingFollowed.get()
            if (following) {
                //Unfollow User
                mViewModel.callFollowUnfollowUserApi(
                    mViewModel.responseGetPostDetails.value!!.data!!.post!!.user.userId,
                    false
                )

            } else {
                //Follow user
                mViewModel.callFollowUnfollowUserApi(
                    mViewModel.responseGetPostDetails.value!!.data!!.post!!.user.userId,
                    true
                )
            }
        }

        fun onClickShare2() {
            shareIntentSpecificApps()
        }

        fun onClickShare() {
            //

            //


            var url: String = ""
            if (mViewModel.responseGetPostDetails.value!!.data!!.post!!.video != null) {

                //
                val intentShareList: ArrayList<Intent> = ArrayList()
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "video/mp4"// "video/mp4 image/jpeg
                shareIntent.type = "text/plain"
                val resolveInfoList: List<ResolveInfo> =
                    requireContext().packageManager.queryIntentActivities(shareIntent, 0)
                //
                if (mViewModel.responseGetPostDetails.value!!.data!!.post!!.video.isNotEmpty()) {
                    if (!hasStoragePermission()) {

                        askForStoragePermission()
                    } else {

                        url = mViewModel.responseGetPostDetails.value!!.data!!.post!!.video

                        if (url.startsWith("htt")) {
                            url = url.replace(AmazonUtil.S3_MEDIA_PATH, AmazonUtil.S3_MEDIA_PATH)
                        } else {
                            var strurl: String
                            strurl = AmazonUtil.S3_MEDIA_PATH + url + ".mp4"
                            url = strurl
                        }
                        var uri: Uri?
                        var bitmapThumb: Bitmap?
                        //(activity as DashboardActivity).showProgressDialog()

                        progressDialog.setMessage("Ready to Sharing...")
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                        progressDialog.isIndeterminate = true
                        //  progressDialog.setProgress(0);
                        progressDialog.setCancelable(false)
                        progressDialog.show()

                        doAsync {
                            // do things in the background  // (1)
                            bitmapThumb = retriveVideoFrameFromVideo(url)
                            uiThread {
                                uri = bitmapThumb?.let { getImageUri(activity!!, it) }!!


                                ///////////
                                for (resInfo in resolveInfoList) {
                                    val packageName = resInfo.activityInfo.packageName
                                    val name = resInfo.activityInfo.name
                                    Log.d(TAG, "Package Name : $packageName")
                                    Log.d(TAG, "Name : $name")
                                    if (packageName.contains("com.facebook.katana") ||
                                        packageName.contains("com.facebook.orca") ||
                                        packageName.contains("com.whatsapp") ||
                                        packageName.contains("com.instagram.android") ||
                                        packageName.contains("com.linkedin.android") ||
                                        packageName.contains("com.skype.raider") ||
                                        packageName.contains(" com.zhiliaoapp.musically")//com.instagram.android , com.skype.raider com.facebook.orca
                                    ) {
                                        /* if (name.contains("com.twitter.android.DMActivity")) {
                                             continue
                                         }*/
                                        /* val intent = Intent()
                                         intent.component = ComponentName(packageName, name)
                                         intent.action = Intent.ACTION_SEND
                                         intent.type = "text/plain"
                                         intent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject")
                                         intent.putExtra(Intent.EXTRA_TEXT, "Your Content")*/

                                        val share = Intent(Intent.ACTION_SEND)
                                        share.component = ComponentName(packageName, name)
                                        share.apply {
                                            var strDes: String
                                            strDes = ""
                                            if (mViewModel.responseGetPostDetails.value?.data?.post!!.description.length > 100) {
                                                strDes =
                                                    mViewModel.responseGetPostDetails.value?.data?.post!!.description.subSequence(
                                                        0,
                                                        99
                                                    ).toString() + "..."

                                            } else strDes =
                                                mViewModel.responseGetPostDetails.value?.data?.post!!.description
                                            var strtitle: String
                                            strtitle =
                                                "*" + mViewModel.responseGetPostDetails.value?.data?.post!!.title + "*"

                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT,
                                                mViewModel.responseGetPostDetails.value?.data?.post?.user?.let { "_Post by @" + it.userName + "_" + "\n\n" + strtitle + "\n" + strDes.toString() + "\n\n" + "Continue reading on Nuna… \nhttps://nuna.page.link/nYJz" })


                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            //  if()
                                            type = "image/jpeg"
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        intentShareList.add(share)
                                    }
                                }

                                //////////////

                                try {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss()
                                    }

                                    if (intentShareList.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "No apps to share !",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val chooserIntent =
                                            Intent.createChooser(
                                                intentShareList.removeAt(0),
                                                "Share via"
                                            )
                                        chooserIntent.putExtra(
                                            Intent.EXTRA_INITIAL_INTENTS,
                                            intentShareList.toArray(arrayOf<Parcelable>())
                                        )
                                        //   startActivity(chooserIntent)
                                        requireActivity().startActivityForResult(
                                            chooserIntent,
                                            1
                                        ) //1 is request code
                                    }

                                    //  requireActivity().startActivity(share)
                                    ///  requireActivity().startActivityForResult(share,1 ); //1 is request code
                                } catch (ex: ActivityNotFoundException) {
                                }


                            }

                        }


                    }

                } else {

                    //
                    val intentShareList: ArrayList<Intent> = ArrayList()
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND

                    shareIntent.type = "image/jpeg"
                    shareIntent.type = "text/plain"
                    val resolveInfoList: List<ResolveInfo> =
                        requireContext().packageManager.queryIntentActivities(shareIntent, 0)
                    //
                    url = carouselAdapter.list[mBinding.viewPager.currentItem]

                    if (url.startsWith("htt")) {

                    } else {
                        var strurl: String
                        strurl = AmazonUtil.S3_MEDIA_PATH + url + ".jpg"
                        url = strurl
                    }

                    Picasso.get().load(url)
                        .into(object : Target {

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                            override fun onBitmapLoaded(
                                bitmap: Bitmap?,
                                from: Picasso.LoadedFrom?
                            ) {

                                val bos = ByteArrayOutputStream()
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                                val f =
                                    File(requireContext().getExternalFilesDir(null)?.absolutePath + File.separator + "temporary_file.jpg")
                                try {
                                    f.createNewFile()
                                    val fo = FileOutputStream(f)
                                    fo.write(bos.toByteArray())
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                val uriF = FileProvider.getUriForFile(
                                    requireContext(),
                                    "${requireContext().packageName}.provider",
                                    f
                                )


                                ///////////
                                for (resInfo in resolveInfoList) {
                                    val packageName = resInfo.activityInfo.packageName
                                    val name = resInfo.activityInfo.name
                                    Log.d(TAG, "Package Name : $packageName")
                                    Log.d(TAG, "Name : $name")
                                    if (packageName.contains("com.facebook.katana") ||
                                        packageName.contains("com.facebook.orca") ||
                                        packageName.contains("com.whatsapp") ||
                                        packageName.contains("com.instagram.android") ||
                                        packageName.contains("com.linkedin.android") ||
                                        packageName.contains("com.skype.raider") ||
                                        packageName.contains(" com.zhiliaoapp.musically")//com.instagram.android , com.skype.raider com.facebook.orca

                                    ) {
                                        /* if (name.contains("com.twitter.android.DMActivity")) {
                                             continue
                                         }*/
                                        /* val intent = Intent()
                                         intent.component = ComponentName(packageName, name)
                                         intent.action = Intent.ACTION_SEND
                                         intent.type = "text/plain"
                                         intent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject")
                                         intent.putExtra(Intent.EXTRA_TEXT, "Your Content")*/
                                        val share = Intent(Intent.ACTION_SEND)
                                        share.component = ComponentName(packageName, name)
                                        share.apply {
                                            var strDes: String
                                            strDes = ""
                                            if (mViewModel.responseGetPostDetails.value?.data?.post!!.description.length > 100) {
                                                strDes =
                                                    mViewModel.responseGetPostDetails.value?.data?.post!!.description.subSequence(
                                                        0,
                                                        99
                                                    ).toString() + "..."

                                            } else strDes =
                                                mViewModel.responseGetPostDetails.value?.data?.post!!.description
                                            var strtitle: String
                                            strtitle =
                                                "*" + mViewModel.responseGetPostDetails.value?.data?.post!!.title + "*"

                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT,
                                                mViewModel.responseGetPostDetails.value?.data?.post?.user?.let { "_Post by @" + it.userName + "_" + "\n\n" + strtitle + "\n" + strDes.toString() + "\n\n" + "Continue reading on Nuna… \nhttps://nuna.page.link/nYJz" })


                                            putExtra(Intent.EXTRA_STREAM, uriF)
                                            //  if()
                                            type = "image/jpeg"
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }

                                        intentShareList.add(share)
                                    }
                                }

                                //////////////


                                try {
                                    if (intentShareList.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "No apps to share !",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val chooserIntent =
                                            Intent.createChooser(
                                                intentShareList.removeAt(0),
                                                "Share via"
                                            )
                                        chooserIntent.putExtra(
                                            Intent.EXTRA_INITIAL_INTENTS,
                                            intentShareList.toArray(arrayOf<Parcelable>())
                                        )
                                        //   startActivity(chooserIntent)
                                        requireActivity().startActivityForResult(
                                            chooserIntent,
                                            1
                                        ) //1 is request code
                                    }

                                    // requireActivity().startActivity(share)
                                    //  requireActivity().startActivityForResult(  share,1  ); //1 is request code
                                } catch (ex: ActivityNotFoundException) {
                                }
                            }
                        })
                }


            }


        }

        fun onClickRelatedPostItem(pos: Int, clickedPost: Post) {
            navigate(R.id.PostDetailsFragment, ParcelKeys.PK_POST_ID to clickedPost.postId)
        }

        fun onClickPostOwnerProfile(position: Int, clickedItem: Post) {


            navigate(
                R.id.OthersProfileFragment,
                ParcelKeys.PK_PROFILE_ID to clickedItem.user.userId
            )
        }

        fun onClickBack() {
            findNavController().navigateUp()

        }

        fun onClickReport() {
            if (!mViewModel.isMyPost.value!!) {
                mBinding.tvReport
                mViewModel.callReportApi()
            }
        }

        fun onClickViewPost() {


        }

        fun onClickLikePost() {
            if (!mViewModel.isMyPost.value!!)
                mViewModel.callLikeUnlikePostApi()
        }

        fun onClickCommentRequest() {
            // if (!mViewModel.isMyPost.value!!)
            // commonCallbacks?.showKeyboard()


            var scroolto: Int
            scroolto = mBinding.tvCommentCount.top - mBinding.scrollView.top



            mBinding.scrollView.smoothScrollTo(0, scroolto)
            // mBinding.recordingView.messageView.requestFocus()
        }

        fun toggleCommentsSection(v: View) {
            if (mViewModel.showAllComments.value == false) {


                //see more
                childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
                    (this as CommentsFragment).toggleVisibility(true)
                    this@PostDetailsFragment.mViewModel.showAllComments.value = true
                }

            } else {

                //see less
                childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
                    (this as CommentsFragment).toggleVisibility(false)
                    this@PostDetailsFragment.mViewModel.showAllComments.value = false
                }
            }
        }

        fun onClickAddToBoard() {
            showAddToBoardDialog()
        }

        fun onClickProfilePic() {
            navigate(
                R.id.OthersProfileFragment,
                ParcelKeys.PK_PROFILE_ID to mViewModel.responseGetPostDetails.value!!.data!!.post!!.user.userId
            )
        }
    }

    fun showAddToBoardDialog() {

        if (mViewModel.responseBoardList.value == null) {
            Util.run { toast(getString(R.string.msg_pinboards_not_available)) }
            return
        }

        val builderSingle: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builderSingle.setTitle(getString(R.string.select_board))

        val arrayAdapter = AddToBoardAdapter(
            requireContext(),
            0,
            mViewModel.responseBoardList.value!!.data!!
        )

        builderSingle.setAdapter(
            arrayAdapter
        ) { dialog, which ->
            val clickedItem = arrayAdapter.getItem(which)
            mViewModel.addPostToAPinboard(mViewModel.postId, clickedItem.pinboardId)
        }
        builderSingle.show()
    }

    override fun onClickPhoto(pos: Int, imgUri: String?) {
        if (imgUri?.isBlank() == true) {
            return
        }

        val data = mViewModel.responseGetPostDetails.value?.data?.post?.images?.split(",")
        startActivity(
            Intent(requireContext(), PhotoViewActivity::class.java).putExtras(
                bundleOf(
                    ParcelKeys.PK_PICKED_IMAGES_PATH_ARRAY to data,
                    ParcelKeys.PK_TARGET_IMAGE_POSITION to pos,
                    ParcelKeys.PK_IS_DATA_TYPE_URL to true
                )
            )
        )
    }


    private fun startRecording() {
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        // ACC_ELD is supported only from SDK 16+.
        // You can use other encoders for lower vesions.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
            recorder.setAudioEncodingBitRate(48000)
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(64000)
        }
        recorder.setAudioSamplingRate(16000)

        audioPlayerName = root + "/" + millis + "audio.mp4"


        recorder.setOutputFile(audioPlayerName)
        try {
            recorder.prepare()
            recorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun stopRecording() {

        if (recorder == null) {

        } else {
            recorder.release()
            isRecording = false
        }
    }

    private fun createDirectoriesIfNeeded() {
        root = Environment.getExternalStorageDirectory().absolutePath
        val folder = File(
            root,
            "AudioRecord"
        )
        if (!folder.exists()) {
            folder.mkdir()
        }
        val audioFolder = File(folder.absolutePath, "Audio")
        if (!audioFolder.exists()) {
            audioFolder.mkdir()
        }
        root = audioFolder.absolutePath
    }

    private fun hasExternalReadWritePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || activity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || activity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasAudioPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || activity?.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                || activity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val external = File(Environment.getExternalStorageDirectory(), "Recorder")
                if (!external.exists()) external.mkdir()

                // record_view.setAudioDirectory(external)
            } else {
                showToast("Please grant external storage permission to save recorded file")
            }
        } else if (requestCode == VOICE_PERMISSION_REQUEST_CODE) {


            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // showToast("Please grant permission of mic")
            } else {
                Log.d(TAG, "Voice permission granted")
            }
        }
    }

    /* override fun askForVoicePermission() {
         ActivityCompat.requestPermissions(
             requireActivity(),
             arrayOf(android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE),
             VOICE_PERMISSION_REQUEST_CODE
         )
     }*/
    private fun askForAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            VOICE_PERMISSION_REQUEST_CODE
        )
        /* if (!hasVoicePermission() || !hasStoragePermission()) //{/ if(!hasAudioPermission())
          {

          }
  */
    }

    private fun askForStoragePermission() {
        if (!hasExternalReadWritePermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun hasVoicePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || context?.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasMyPermission(): Boolean {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return (context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && context?.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        } else {
            return true
        }

        return false

        /*  return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context?.checkSelfPermission(
              Manifest.permission.RECORD_AUDIO
          ) == PackageManager.PERMISSION_GRANTED*/
    }


    private fun hasStoragePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun getTextInput(): String {
        return mBinding.recordingView.messageView.text.toString()
    }

    private fun clearTextInput() {
        mBinding.recordingView.messageView.setText("")
    }

    override fun onImageUploadSuccess() {
        Log.d("onImageUpload  ", "Success")
    }

    override fun onImageUploadFail(error: String) {
        Log.d("onImageUploadFail ", error)
    }

    override fun onRecordingStarted() {


        // if (!hasVoicePermission() || !hasStoragePermission()) {
        if (!hasMyPermission()) {
            valuepermisstion = false
            askForAudioPermission() // askForVoicePermission()
            onRecordingCanceled()

        } else {
            valuepermisstion = true
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mBinding.recordingView.messageView.windowToken, 0)

            // showToast("started");
            debug("started")

            time = System.currentTimeMillis() / 1000
            startRecording()
        }


    }

    override fun onRecordingLocked() {
    }

    override fun onRecordingCompleted() {

        if (valuepermisstion) {

            mBinding.recordingView.messageView.requestFocus()
            debug("completed")
            stopRecording()

            if (!mBinding.recordingView.timeTextView.text.toString().equals("0:00")) {
                val fileName =
                    AmazonUtil.S3_MEDIA_AUDIO + audioPlayerName?.let { getFileName(it) }

                audioPlayerName?.let { AmazonUtil.uploadFile(it, fileName, this) }

                mViewModel.uploadingAudioUrl = Constant.AMZ_BASE_URL + fileName


                mViewModel.requestSaveComment.set(
                    RequestSaveComment(
                        User(Prefs.init().loginData!!.userId),
                        0,
                        0,
                        mViewModel.postId,
                        mViewModel.uploadingAudioUrl,
                        true
                    )
                )
                mViewModel.callSaveCommentApi()
            }
        }

    }

    fun getFileName(path: String): String {
        return System.currentTimeMillis().toString() + path.substring(path.lastIndexOf("."))
    }


    override fun onRecordingCanceled() {
        if (valuepermisstion) {
            mBinding.recordingView.messageView.requestFocus()

            if (recorder != null) {
                recorder.release()
                isRecording = false
            }
        }
        if (hasMyPermission())
            Validator.showCustomToast(getString(R.string.cancelled))
        debug("canceled")
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun debug(log: String) {
        Log.d("DS", log)
    }

    /*override fun onClick(v: View?) {
    }
*/


    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
                videoPath,
                HashMap()
            ) else mediaMetadataRetriever.setDataSource(videoPath)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.frameAtTime

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
            Log.e("mediaMetadataRetriever", "success ")
        }
        return bitmap
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun onPause() {
        super.onPause()
        Log.e("", "onPause")
        //  if (wake!=null)
        //  wake.release();
        //  activity!!. clearFlags(): getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // activity!!.window.addFlags(WindowManager.LayoutParams.FLAGS_CHANGED)
        if (carouselAdapter.isVideo) {
            activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (carouselAdapter.videoView != null) {

                carouselAdapter.videoView.setPlayWhenReady(false)
                carouselAdapter.videoView.setPlayerStateListener {

                    when (it) {
                        ExoPlayer.STATE_IDLE -> {
                            //   carouselAdapter.videoView.pausePlayer()
                        }
                        ExoPlayer.STATE_BUFFERING -> {
                            //  carouselAdapter.videoView.pausePlayer()
                        }
                        ExoPlayer.STATE_READY -> {
                            // carouselAdapter.videoView.pausePlayer()
                        }
                        ExoPlayer.STATE_ENDED -> {
                            // carouselAdapter.videoView.pausePlayer()
                        }
                    }
                }
            }
        }


        //   (this as CommentsFragment).onMyPause()


    }

    override fun onStop() {
        super.onStop()
        Log.e("", "onStop")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("", "onDestroyView")

    }

    override fun onDetach() {
        super.onDetach()
        Log.e("", "onDetach")
    }

    override fun onResume() {
        super.onResume()
        Log.e("", "onResume")
//        getReply(mViewModel.responseGetPostDetails.value!!.data!!.commentList, 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Toast.makeText(context,"onActivityResult..:",Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK) {
            // Toast.makeText(context,"Yes onActivityResult..:",Toast.LENGTH_SHORT).show();
            if (requestCode == 1) {
                //Toast.makeText(context, "Got Callback yeppeee...:", Toast.LENGTH_SHORT).show();
                mViewModel.callShareApi()
            } else {
                //  Toast.makeText(context, "NNNNot Callback yeppeee...:", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
