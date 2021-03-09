package com.demo.create_post

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.MainApplication
import com.demo.databinding.ActivityCreatePostBinding
import com.demo.databinding.LayoutUploadProgressDialogBinding
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostCategory
import com.demo.photoeditor.base.BaseActivity
import com.demo.providers.aws.AWSProvider
import com.demo.util.*
import com.demo.util.link.Link
import com.google.firebase.crashlytics.internal.common.CommonUtils
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File
import java.util.regex.Pattern

class CreatePostActivity : BaseActivity(), KodeinAware,
    CreatePostRecyclerItemClickListener {
    var compressedImageFile: File? = null
    private val videoURL = "asset:///sample_vid.mp4"
    private var hashtagsScrollListener: EndlessRecyclerViewScrollListener? = null
    override val kodein: Kodein by closestKodein()
    private val viewModalFactory: CreatePostViewModalFactory by instance()
    private val awsProvider: AWSProvider by instance()
    lateinit var mBinding: ActivityCreatePostBinding
    lateinit var mViewModel: CreatePostViewModel
    var category: String = ""
    private var categoriesAdaptor: CreatePostCategoryRecyclerAdapter? = null
    private var hashTagsAdaptor: CreatePostHashTagRecyclerAdapter? = null
    private var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
    private var workRunnable: Runnable? = null
    private var wordValidationToast: Toast? = null
    private var videoFilePath: String? = null
    private var thumbnailFilePath: String? = null
    private var videoFileName: String? = null
    private var thumbnailFileName: String? = null
    private var keyboardListener: Unregistrar? = null
    private var isKeyboardVisible = false
    private var maxCharacterReached = false
    private var uploadedUrl = ""
    private var uploadProgressDialog: AlertDialog? = null
    private var dialogBinding: LayoutUploadProgressDialogBinding? = null
    private lateinit var myApp: MainApplication
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            workRunnable?.let { handler.removeCallbacks(it) }
            workRunnable = Runnable {
                mViewModel.apply {
                    var query = s!!.toString()
                    if (query.contains("\n")) query = query.replace("\n", " \n")
                    searchQuery = mViewModel.getSearchQuery(query)
                    println("searchQuery = ${searchQuery}")
                    if (searchQuery.startsWith("#")) {
                        hashtagsScrollListener?.resetState()
                        searchQuery = searchQuery.replace("#", "")
                        searchHashTags()
//                        hashTagAlreadyExistWhileTyping()
                    } else showData(false, mBinding.rvHashtags)
                }
            }
            handler.postDelayed(workRunnable!!, 500 /*delay*/)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {


                maxLimitReachedAlert()
                val regex =
                    Pattern.compile("[^\u0A80-\u0AFF\u0980-\u09FF\u0C00-\u0C7F\u0B80-\u0BFF\u0C80-\u0CFF\u0900-\u097Fa-zA-Z0-9#]")
                if (it.endsWith(" ") || it.endsWith("\n")
                    || (it.isNotEmpty() && regex.matcher(it[it.lastIndex].toString()).find())
                ) {
                    mViewModel.searchTags.clear()
                    showData(false, mBinding.rvHashtags)
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        initMyApp()
        mViewModel = ViewModelProvider(this, viewModalFactory).get(CreatePostViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_post)
        mBinding.apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }

        setupScrollListener()
        initRecyclerView()
        showMarkedTags()
        readArguments()
        setupObservers()
        mViewModel.getCategories(0)
        getHashTags()
        mBinding.etDescription.addTextChangedListener(textWatcher)
        mBinding.llHashTags.setOnClickListener { mBinding.clickHandler?.showHashTags() }
        mBinding.tvHashTags.setOnClickListener { mBinding.clickHandler?.showHashTags() }

        ClickGuard.guard(mBinding.llHashTags, mBinding.tvHashTags)
    }

    private fun setupObservers() {
        mViewModel.toastMessage.observe(this, {
            if (!TextUtils.isEmpty(it))
                Validator.showMessage(it)
        })

        mViewModel.showLoading.observe(this,
            {
                if (it) {
                    showProgressDialog()
//                    mBinding.llBlockCLick.visibility = VISIBLE
                } else {
                    hideProgressDialog()
//                    mBinding.llBlockCLick.visibility = GONE
                }
            })

        mViewModel.categories.observe(this, {
            if (it.isNotEmpty())
                categoriesAdaptor?.setNewItems(it)
        })
    }

    private fun initMyApp() {
        myApp = applicationContext as MainApplication
        myApp.setCurrentActivity(this)
    }

    private fun clearReferences() {
        val currActivity: Activity? = myApp.getCurrentActivity()
//        if (this == currActivity) myApp.setCurrentActivity(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearReferences()
    }

    override fun onResume() {
        super.onResume()
        keyboardShowListener()
//        mBinding.epVideo.play()

    }

    override fun onPause() {
        super.onPause()
        keyboardListener?.unregister()
//        mBinding.epVideo.pausePlayer()
    }

    private fun maxLimitReachedAlert() {
        if (!maxCharacterReached && mBinding.etDescription.text.toString().length == 200) {
            maxCharacterReached = true
            wordValidationToast = Toast.makeText(
                this@CreatePostActivity,
                getString(R.string.max_character_reached),
                Toast.LENGTH_SHORT
            )

            wordValidationToast?.show()
        } else maxCharacterReached = false
    }


    private fun showData(show: Boolean, view: RecyclerView?) {
        mBinding.let {
            if (show) {
                it.clCategory.visibility = GONE
//                it.ivFooterGradient.visibility = VISIBLE
                it.llButton.visibility = GONE

                if (view == it.rvData) {
                    it.rvData.visibility = VISIBLE
                } else it.rvHashtags.visibility = VISIBLE

            } else {
                hashtagsScrollListener?.resetState()
                if (view == it.rvData) {
                    it.rvData.visibility = GONE
                } else it.rvHashtags.visibility = GONE
                it.ivFooterGradient.visibility = GONE

                if (it.rvData.visibility == GONE && it.rvHashtags.visibility == GONE)
                    it.clCategory.visibility = VISIBLE

                if (!isKeyboardVisible && it.rvData.visibility == GONE && it.rvHashtags.visibility == GONE) {
                    it.llButton.visibility = VISIBLE
                }
            }
        }
    }

    private fun readArguments() {
        val bundle = intent.getBundleExtra(getString(R.string.create_post_arguments))
        videoFilePath = bundle?.getString(getString(R.string.result_video_file_path))
        thumbnailFilePath =
            bundle?.getString((getString(R.string.result_video_thumbnail_file_path)))
        lifecycleScope.launch {
            compressedImageFile =
                Compressor.compress(this@CreatePostActivity, File(thumbnailFilePath))
        }

        videoFileName = videoFilePath?.getFileName()
        thumbnailFileName = thumbnailFilePath?.getFileName()



        Glide.with(this).load(thumbnailFilePath)
            .placeholder(R.drawable.dashboard_item_bg)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail(0.05f)
            .apply(RequestOptions().override(400, 600))
            .skipMemoryCache(true)
            .into(mBinding.ivThumbNail)
    }

    private fun uploadToAWS(isImage: Boolean, onComplete: () -> Unit) = launch(Dispatchers.IO) {
        thumbnailFilePath = compressedImageFile?.path

//        dialogBinding?.title =
//            if (isImage) getString(R.string.uploading_image) else getString(R.string.uploading_video)
        uploadedUrl = awsProvider.uploadFile(
            if (isImage) thumbnailFilePath!! else videoFilePath!!,
            object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            onComplete()
//                            dismissDialog()
                        }

                        TransferState.CANCELED, TransferState.FAILED -> {
                            dismissDialog()
                            showMsg(getString(R.string.something_went_wrong))
                        }
                        else -> {
                        }
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val valueProgess = (bytesCurrent * 100 / bytesTotal)
                    println("Upload Progress = ${valueProgess.toInt()}")
                    if (!isImage)
                        updateUploadProgress(valueProgess.toInt())
                }

                override fun onError(id: Int, ex: Exception?) {
                    ex?.printStackTrace()
                    dismissDialog()
                    showMsg(getString(R.string.something_went_wrong))
                }
            })

    }

    private fun dismissDialog() = launch(Dispatchers.Main) {
        uploadProgressDialog?.dismiss()
    }

    private fun showMsg(msg: String) = launch(Dispatchers.Main) {
        Validator.showMessage(msg)
    }

    private fun updateUploadProgress(progress: Int) = launch(Dispatchers.Main) {
        dialogBinding?.apply {
            val animation =
                ObjectAnimator.ofInt(
                    pbUploadProgress,
                    "progress",
                    pbUploadProgress.progress,
                    progress
                )
            animation.duration = 500
            animation.interpolator = DecelerateInterpolator()
            animation.start()
        }
    }

    private fun showMarkedTags() {
        val linkHashTag = Link(Pattern.compile("(#\\w+)"))
            .setUnderlined(false)
            .setTextStyle(Link.TextStyle.BOLD)
            .setTextColor(getColor(R.color.colorPrimary))

        linkHashTag.typeFace =
            if (Prefs.init().selectedLang.languageIntId == 2) ResourcesCompat.getFont(
                this,
                R.font.gothampro_medium
            ) else ResourcesCompat.getFont(this, R.font.nirmala_b)

        mBinding.etDescription.addLink(linkHashTag)
    }

/*    private fun getCategories(showProgress: Boolean) = launch {
//        if (showProgress)
        showProgressDialog()
        mBinding.clCategory.isEnabled = false
        mViewModel.getCategories().observe(this@CreatePostActivity, Observer {
//            if (showProgress)
            mBinding.clCategory.isEnabled = true
            hideProgressDialog()
            if (it.error == null) {
                val categories = it.data?.categories
                if (categories != null && categories.isNotEmpty())
                    mViewModel.categories.addAll(it.data?.categories!!)
//                mViewModel.sortCategories()
                categoriesAdaptor?.setNewItems(mViewModel.categories.filter { cat -> cat.isOther == false })
//                clickHandle.showSearchedHashTags()
            } else {
                handleAPIError(it.error!!.message.toString())
            }
        })
    }*/

    private fun getHashTags() = launch {
        mViewModel.getHashTags().observe(this@CreatePostActivity, Observer {
            if (it.error == null) {
                val hashTags = it.data?.hashTags
                if (hashTags != null && hashTags.isNotEmpty()) {
                    mViewModel.hashTags.addAll(it.data?.hashTags!!)
                    hashTagsAdaptor?.setNewItems(mViewModel.hashTags)
                }
            } else if (!Util.checkIfHasNetwork())
                it.error?.message?.let { it1 -> handleAPIError(it1) }
        })
    }

    private fun searchHashTags() = launch {
        mViewModel.params.offset = 0
        mViewModel.searchHashTag().observe(this@CreatePostActivity, {
            if (it.success == true) {
                showSearchedTag(it.data?.hashTags!!)
            } else if (it.success == false && it.data == null) {
//            } else if (it.success == false && it.message!!.contains(getString(R.string.key_no_hashtag_found))) {
                val regex =
                    Pattern.compile("[^\u0A80-\u0AFF\u0980-\u09FF\u0C00-\u0C7F\u0B80-\u0BFF\u0C80-\u0CFF\u0900-\u097Fa-zA-Z0-9#]");

                if (!regex.matcher(mViewModel.searchQuery).find()) {

                    showSearchedTag(ArrayList<HashTag>().apply {
                        if (!TextUtils.isEmpty(mViewModel.searchQuery))
                            add(
                                HashTag(
                                    tagName = mViewModel.searchQuery
                                )
                            )
                    })
                }

            }
        })
        mBinding.etDescription.cursorAtEnd()
    }

    private fun showSearchedTag(tags: ArrayList<HashTag>) {
        mViewModel.searchTags.clear()
        mViewModel.searchTags.addAll(tags)
        hashTagsAdaptor?.setNewItems(mViewModel.searchTags)
        if (mViewModel.searchQuery.isNotEmpty()) {
            showData(false, mBinding.rvData)
            showData(true, mBinding.rvHashtags)
        }
    }


    private fun setupScrollListener() {
        mBinding.apply {

//            hashtagsScrollListener = object :
//                EndlessRecyclerViewScrollListener(mBinding.rvData.layoutManager as LinearLayoutManager) {
//                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
//
//                }
//            }
//            rvData.addOnScrollListener(hashtagsScrollListener!!)

            rvData.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvData.getChildAt(rvData.childCount - 1)
                val diff = view.bottom - (rvData.height + rvData.scrollY)
                val offset = mViewModel.categories.value?.size!!
                if (diff == 0 && offset % 20 == 0 && !mViewModel.allCategoriesLoaded) {
                    mViewModel.getCategories(offset)
                }
            }
        }
    }

    private fun initRecyclerView() {
        categoriesAdaptor =
            CreatePostCategoryRecyclerAdapter(R.layout.layout_create_post_category, this)
        mBinding.rvData.adapter = categoriesAdaptor

        hashTagsAdaptor =
            CreatePostHashTagRecyclerAdapter(R.layout.layout_create_post_hashtag, this)
        mBinding.rvHashtags.adapter = hashTagsAdaptor
    }

    override fun selectedCategory(value: PostCategory) {
        mViewModel.selectedCategory.set(value)
        mBinding.tvCategoryValue.text = value.fetchCurrentLanguageCategoryName()
        mViewModel.selectedCategory.set(value)
        showData(false, mBinding.rvData)
        mBinding.apply {
            tvCategoryValue.typeface =
                if (Prefs.init().selectedLangId == 2)
                    ResourcesCompat.getFont(this@CreatePostActivity, R.font.gotham_pro_medium)
                else ResourcesCompat.getFont(this@CreatePostActivity, R.font.nirmala_b)

            tvCategoryValue.setTextColor(resources?.getColor(R.color.themePink1)!!)
        }
    }

    override fun selectedHashTag(value: String) {
        CommonUtils.hideKeyboard(this, mBinding.root)
        mBinding.etDescription.removeTextChangedListener(textWatcher)
        mViewModel.description.set(mBinding.etDescription.text.toString())

        var currentDesc = mBinding.etDescription.text.toString()
//        if (!mViewModel.hashTagAlreadyExistWhenSelect(value)) {
        if (mViewModel.searchQuery.isNotEmpty()) {
            currentDesc = currentDesc.removeRange(
                currentDesc.lastIndexOf('#'),
                currentDesc.length
            )
            mBinding.etDescription.setText(
                currentDesc.plus("#$value ")
            )
            showData(false, mBinding.rvHashtags)
        } else {
            mBinding.etDescription.setText(
                if (currentDesc.endsWith("#")) currentDesc.plus("$value ") else currentDesc.plus("#$value ")
            )

            showData(false, mBinding.rvHashtags)
        }
        mViewModel.clearSearch()
        hashTagsAdaptor?.setNewItems(mViewModel.hashTags)
//            mViewModel.hashTags.clear()
//        }
//        else Validator.showMessage(getString(R.string.hashtag_already_exist))
        mBinding.apply {
            this.etDescription.cursorAtEnd()
            this.etDescription.requestFocus()
            showKeyboard()
            showData(false, mBinding.rvHashtags)
            this.etDescription.addTextChangedListener(textWatcher)
        }
    }

    override fun fetchData(value: Any) {
//        if (value is Category) {
//            mViewModel.params.offset = mViewModel.categories.size
//            getCategories(false)
//        } else
        if (!TextUtils.isEmpty(mViewModel.searchQuery)) {
            mViewModel.params.offset = mViewModel.searchTags.size
            searchHashTags()
        } else {
            mViewModel.params.offset = mViewModel.hashTags.size
            getHashTags()
        }
    }

    inner class ClickHandler {
        fun crossPress() {
            backPress()
        }


        fun showCategories() {
            CommonUtils.hideKeyboard(this@CreatePostActivity, mBinding.root)
            if (mViewModel.categories.value?.isNotEmpty()!!) {
                showData(false, mBinding.rvHashtags)
                showData(true, mBinding.rvData)
            } else {
                mViewModel.getCategories(0)
            }
//                Validator.showMessage(getString(R.string.no_categories_available))
        }

        fun showHashTags() {
            CommonUtils.hideKeyboard(this@CreatePostActivity, mBinding.root)
            if (mViewModel.hashTags.isNotEmpty()) {
//                initRecyclerView(mViewModel.hashTags.toMutableList())

//                getHashTags()
                if (mBinding.rvHashtags.visibility == GONE) {
                    showData(false, mBinding.rvData)
                    showData(true, mBinding.rvHashtags)
                    addHashToDescription()
                    mBinding.etDescription.requestFocus()
                    showKeyboard()
                    mBinding.etDescription.cursorAtEnd()
                } else if (mBinding.rvHashtags.visibility == VISIBLE)
                    showData(false, mBinding.rvHashtags)
            }
        }


        fun onClickSave() {
            mViewModel.description.set(mBinding.etDescription.text.toString())
            if (!TextUtils.isEmpty(mViewModel.selectedCategory.get()?.toString())) {
                /*
    mViewModel.requestSavePost.get()!!.apply {
    if (videoFilePath != null) {
        video = createMultipartBody(
            videoFilePath!!,
            videoFileName!!,
            getString(R.string.post_file_video)
        )
        image = createMultipartBody(
            thumbnailFilePath!!,
            thumbnailFileName!!,
            getString(R.string.post_file_image)
        )
    } else {
        video = createMultipartBody(
            videoURL,
            getString(R.string.sample_video),
            getString(R.string.post_file_video)
        )
        image = createMultipartBody(
            videoFilePath!!,
            videoFileName!!,
            getString(R.string.post_file_image)
        )

    }

    description = MultipartBody.Part.createFormData(
        "description",
        mBinding.etDescription.text.toString()
    )
    languageId =
        MultipartBody.Part.createFormData(
            "languageId",
            Prefs.init().selectedLang._id!!
        );

    categoryId =
        MultipartBody.Part.createFormData(
            "categoryId",
            mViewModel.selectedCategory.get()!!.id.toString()
        )
    }
    mBinding.invalidateAll()*/

                mViewModel.addPostRequest.get()!!.apply {
                    languageId = Prefs.init().selectedLang._id
                    categoryId = mViewModel.selectedCategory.get()?.id

                    uploadProgressDialog(getString(R.string.uploading_video))
                    uploadToAWS(true) {
                        imageUrl = uploadedUrl
                        uploadToAWS(false) {
                            videoUrl = uploadedUrl
                            Handler(Looper.getMainLooper()).postDelayed({
                                dismissDialog()
                                createPostV2()
                            }, 250)
                        }
                    }
                }
            } else Validator.showMessage(getString(R.string.select_category))
        }

        private fun addHashToDescription() {
            val value = mBinding.etDescription.text.toString()
            mBinding.etDescription.setText(if (!value.endsWith("#")) value.plus("#") else value)
        }

        private fun createMultipartBody(
            filePath: String,
            fileName: String,
            name: String
        ): MultipartBody.Part? {
            val file = File(filePath)

            val fileSizeInBytes = file.length();
            val fileSizeInKB = fileSizeInBytes / 1024;
            val fileSizeInMB = fileSizeInKB / 1024;
            val requestBody =
                RequestBody.create(
                    getMIMEType(Uri.fromFile(file))!!.toMediaTypeOrNull(), file
                )
            return MultipartBody.Part.createFormData(name, fileName, requestBody)
        }

        private fun createPost() = launch {
            showProgressDialog()
            mViewModel.createPost().observe(this@CreatePostActivity, Observer {
                hideProgressDialog()
                if (it.error == null && it.success!!) {
                    setResult(
                        RESULT_OK,
                        Intent(
                            this@CreatePostActivity,
                            DashboardActivity::class.java
                        ).putExtra(
                            getString(R.string.intent_key_from),
                            getString(R.string.intent_key_show_user)
                        ).putExtra(
                            getString(R.string.intent_key_user_id),
                            Prefs.init().currentUser?.id
                        )
                    )

                    /*startActivity(
                        Intent(
                            this@CreatePostActivity,
                            DashboardActivity::class.java
                        ).putExtra(
                            getString(R.string.intent_key_from),
                            getString(R.string.intent_key_show_user)
                        ).putExtra(
                            getString(R.string.intent_key_user_id),
                            Prefs.init().currentUser?.id
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )*/
                    finish()
                } else {
                    if (!TextUtils.isEmpty(it.error!!.message))
                        it.error!!.message?.let { it1 -> handleAPIError(it1) }
                    else handleAPIError(getString(R.string.something_went_wrong))
                }
            })
        }
    }

    private fun createPostV2() = launch {
        showProgressDialog()
        mViewModel.createPostV2().observe(this@CreatePostActivity, Observer {
            hideProgressDialog()
            if (it.error == null && it.success!!) {
                setResult(
                    RESULT_OK,
                    Intent(
                        this@CreatePostActivity,
                        DashboardActivity::class.java
                    ).putExtra(
                        getString(R.string.intent_key_from),
                        getString(R.string.intent_key_show_user)
                    ).putExtra(
                        getString(R.string.intent_key_user_id),
                        Prefs.init().currentUser?.id
                    )
                )

                /*startActivity(
                    Intent(
                        this@CreatePostActivity,
                        DashboardActivity::class.java
                    ).putExtra(
                        getString(R.string.intent_key_from),
                        getString(R.string.intent_key_show_user)
                    ).putExtra(
                        getString(R.string.intent_key_user_id),
                        Prefs.init().currentUser?.id
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )*/
                finish()
            } else {
                if (!TextUtils.isEmpty(it.error!!.message))
                    it.error!!.message?.let { it1 -> handleAPIError(it1) }
                else handleAPIError(getString(R.string.something_went_wrong))
            }
        })
    }

    private fun backConfirmationDialog() {
        val builder = AlertDialog.Builder(this@CreatePostActivity)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.cancel_post))
        builder.setPositiveButton(
            getString(R.string.yes)
        ) { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        builder.setNegativeButton(
            getString(R.string.no)
        ) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun backPress() {
        Handler().postDelayed({
            mBinding.apply {
                when {
                    rvData.visibility == VISIBLE -> showData(false, rvData)
                    rvHashtags.visibility == VISIBLE -> showData(false, rvHashtags)
                    isKeyboardVisible -> hideKeyboard()
                    else -> backConfirmationDialog()
                }
            }
        }, 200)

    }


    override fun onBackPressed() {
        backPress()
    }

    private fun keyboardShowListener() {
        keyboardListener = KeyboardVisibilityEvent.registerEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    isKeyboardVisible = isOpen
                    mBinding.llButton.visibility =
                        if (!isKeyboardVisible && mBinding.rvData.visibility == GONE && mBinding.rvHashtags.visibility == GONE) VISIBLE else GONE
                }
            })
    }

    private fun uploadProgressDialog(title: String) = launch(Dispatchers.Main) {
        val builder = AlertDialog.Builder(this@CreatePostActivity)
        dialogBinding = LayoutUploadProgressDialogBinding.bind(
            layoutInflater.inflate(R.layout.layout_upload_progress_dialog, null, false)
        ).apply {
            this.title = title
            this.progress = 1
            builder.setView(root)
        }
        uploadProgressDialog = builder.create()
        uploadProgressDialog?.let {
            it.setCanceledOnTouchOutside(false)
            it.setCancelable(false)
            it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val displayMetrics = DisplayMetrics()
            it.window!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.window!!.attributes)
            val dialogWindowWidth = (displayMetrics.widthPixels * 0.9f).toInt()
            val dialogWindowHeight = (displayMetrics.heightPixels * 0.5f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            it.window!!.attributes = layoutParams
            it.show()
        }
    }
}