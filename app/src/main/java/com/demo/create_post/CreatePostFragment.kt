package com.demo.create_post

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apradanas.simplelinkabletext.Link
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentCreatePostBinding
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.HashTag
import com.demo.util.*
import com.google.android.exoplayer2.Player
import com.google.firebase.crashlytics.internal.common.CommonUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.regex.Pattern


class CreatePostFragment : BaseFragment(), KodeinAware,
    CreatePostRecyclerItemClickListener {
    private val clickHandle = ClickHandler()
    private val videoURL = "asset:///sample_vid.mp4"
     private var hashtagsScrollListener: EndlessRecyclerViewScrollListener? = null
    override val kodein: Kodein by closestKodein()
    private val viewModalFactory: CreatePostViewModalFactory by instance()
    lateinit var mBinding: FragmentCreatePostBinding
    lateinit var mViewModel: CreatePostViewModel
    var isVideo: Boolean = false
    lateinit var progressDialog: ProgressDialog
    var category: String = ""
    private var search = true
    private var recyclerAdaptor: CreatePostRecyclerAdaptor? = null
    private var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
    private var workRunnable: Runnable? = null
    private lateinit var linkHashTag: Link
    private lateinit var linkAllWords: Link
    private var wordValidationToast: Toast? = null
    private var filePath: String? = null
    private var thumbnailFilePath: String? = null
    private var fileName: String? = null
    private var thumbImageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    @SuppressLint("ClickableViewAccessibility", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!::mBinding.isInitialized) {

            mBinding = FragmentCreatePostBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@CreatePostFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }
        }

        setupScrollListener()
        getCategories(true)
        getHashTags(true)
        showMarkedTags()
        readArguments()

        mBinding.apply {
            epVideo.setSource(if (isVideo) filePath else videoURL)
            epVideo.setRepeatMode(Player.REPEAT_MODE_ONE)
            etDescription.addTextChangedListener(textWatcher)
        }

        return mBinding.root
    }


    private fun createThumbnail() {
        val thumb = ThumbnailUtils.createVideoThumbnail(
            filePath!!,
            MediaStore.Images.Thumbnails.MINI_KIND
        )
        thumbnailFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/${getString(R.string.app_name)}";
        val dir = File(thumbnailFilePath!!);
        if (!dir.exists())
            dir.mkdirs();
        thumbImageName = fileName?.substring(0, fileName?.indexOf(".")!!) + "thumbnail.jpeg"
        val thumbnailFile = File(dir, thumbImageName!!);
        thumbnailFilePath = "${thumbnailFilePath}/${thumbImageName}"
        val fOut = FileOutputStream(thumbnailFile);

        thumb?.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
    }

    private fun readArguments() {
        filePath = requireArguments().getString("strfile")
        fileName =
            filePath?.let { getFileName(it) }
        isVideo = requireArguments().getBoolean("IsVideo")
        if (isVideo) {
            createThumbnail()
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.apply {
            epVideo.play()
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding.apply {
            epVideo.pausePlayer()
        }

    }

    private fun setupScrollListener() {
        hashtagsScrollListener = object :
            EndlessRecyclerViewScrollListener(mBinding.rvData.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

            }
        }
        mBinding.rvData.addOnScrollListener(hashtagsScrollListener!!)
    }

    private fun getVideo(): File {
        val f = File(requireActivity().cacheDir.toString() + "/sample_vid.mp4")
        if (!f.exists()) try {
            val `is`: InputStream = requireActivity().assets.open("sample_vid.mp4")
            val buffer = ByteArray(1024)
            `is`.read(buffer)
            `is`.close()
            val fos = FileOutputStream(f)
            fos.write(buffer)
            fos.close()
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(e)
        }
        return f
    }

    private fun createMultipartBody(
        filePath: String,
        fileName: String,
        name: String
    ): MultipartBody.Part? {
        var file: File? = null
        if (filePath.contains(getString(R.string.sample_video))) {
            file = getVideo()
        } else {
            file = File(filePath)
        }

        val fileSizeInBytes = file.length();
        val fileSizeInKB = fileSizeInBytes / 1024;
        val fileSizeInMB = fileSizeInKB / 1024;
        val requestBody =
            RequestBody.create(
                requireContext().getMIMEType(Uri.fromFile(file))!!.toMediaTypeOrNull(), file
            )
        return MultipartBody.Part.createFormData(name, fileName, requestBody)
    }

    private fun getFileName(path: String): String {
        return System.currentTimeMillis().toString() + path.substring(path.lastIndexOf("."))
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(this, viewModalFactory).get(CreatePostViewModel::class.java)
    }

    private fun initRecyclerView(data: MutableList<Any>) {
        recyclerAdaptor = CreatePostRecyclerAdaptor(data, this)
        mBinding.rvData.adapter = recyclerAdaptor
        recyclerAdaptor?.notifyDataSetChanged()
    }

    override fun onFragBack(): Boolean {
        if (!isVideo) {
            var bundle = bundleOf("strfile" to arguments?.getParcelableArrayList<Uri>("files"))
            // navigate(R.id.SubFragment, bundle
        }
//        backConfirmationDialog()
        return false
    }

    inner class ClickHandler {
        fun backPress() {

        }

        fun crossPress() {
            backConfirmationDialog()
        }


        fun onClickSave() {
            mViewModel.description.set(mBinding.etDescription.text.toString())
            if (mViewModel.isSavePostValid()) {
                mViewModel.requestSavePost.get()!!.apply {

                    Log.e("ISVIdeo", isVideo.toString())
                    if (isVideo && filePath != null) {

                        video = createMultipartBody(
                            filePath!!,
                            fileName!!,
                            getString(R.string.post_file_video)
                        )
                        image = createMultipartBody(
                            thumbnailFilePath!!,
                            thumbImageName!!,
                            getString(R.string.post_file_image)
                        )
                    } else {
                        video = createMultipartBody(
                            videoURL,
                            getString(R.string.sample_video),
                            getString(R.string.post_file_video)
                        )
                        image = createMultipartBody(
                            filePath!!,
                            fileName!!,
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
                mBinding.invalidateAll()

                createPost()
            }
        }

        private fun createPost() = launch {
            commonCallbacks?.showProgressDialog()
            mViewModel.createPost().observe(viewLifecycleOwner, Observer {
                commonCallbacks?.hideProgressDialog()
                if (it.error == null && it.success!!) {
                    Validator.showMessage(getString(R.string.postCreatedSuccess))
                    findNavController().navigateUp()
                } else handleAPIError(it.error!!.message.toString())
            })
        }

        fun onClickAddInterest() {
            val b = bundleOf(ParcelKeys.PK_GO_BACK to true)
            findNavController().navigate(R.id.CategoryFragment, b)
        }


        fun showCategories() {
            CommonUtils.hideKeyboard(context, mBinding.root)
            if (mViewModel.categories.isNotEmpty()) {
                initRecyclerView(mViewModel.categories.toMutableList())
                showData(true)
            } else Validator.showMessage(getString(R.string.no_categories_available))
        }

        fun showHashTags() {
            CommonUtils.hideKeyboard(context, mBinding.root)
            if (mViewModel.hashTags.isNotEmpty()) {
//                initRecyclerView(mViewModel.hashTags.toMutableList())
                getHashTags(true)
                println("mBinding.rvData.visibility = ${mBinding.rvData.visibility}")
                if (mBinding.rvData.visibility == GONE) {
                    showData(true)
                    addHashToDescription()
                    mBinding.etDescription.requestFocus()
                    (requireActivity() as DashboardActivity).showKeyboard()
                    mBinding.etDescription.cursorAtEnd()
                } else if (mBinding.rvData.visibility == VISIBLE) showData(false)
            }
        }

        fun showSearchedHashTags() {
            if (mViewModel.searchTags.isNotEmpty()) {
                initRecyclerView(mViewModel.searchTags.toMutableList())
                showData(true)
            }
        }
    }

    private fun showData(show: Boolean) {
        mBinding.let {
            if (show) {
                it.clCategory.visibility = GONE
                it.rvData.visibility = VISIBLE
//                it.ivFooterGradient.visibility = VISIBLE
                it.llButton.visibility = GONE

            } else {
                hashtagsScrollListener?.resetState()
                it.clCategory.visibility = VISIBLE
                it.rvData.visibility = GONE
                it.ivFooterGradient.visibility = GONE
                it.llButton.visibility = VISIBLE
            }
        }
    }

    private fun getCategories(showProgress: Boolean) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getCategories().observe(viewLifecycleOwner, Observer {
            if (showProgress) commonCallbacks?.hideProgressDialog()
            if (it.error == null) {
                val categories = it.data?.categories
                if (categories != null && categories.isNotEmpty())
                    mViewModel.categories.addAll(it.data?.categories!!)
                mViewModel.sortCategories()
//                clickHandle.showSearchedHashTags()
            } else {
                handleAPIError(it.error!!.message.toString())
            }
        })
    }

    private fun getHashTags(showProgress: Boolean) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getHashTags().observe(viewLifecycleOwner, Observer {
            if (showProgress) commonCallbacks?.hideProgressDialog()
            if (it.error == null) {
                val hashTags = it.data?.hashTags
                if (hashTags != null && hashTags.isNotEmpty()) {
                    mViewModel.hashTags.addAll(it.data?.hashTags!!)
//                    recyclerAdaptor!!.changeData(mViewModel.hashTags)
                    initRecyclerView(mViewModel.hashTags.toMutableList())

//                    clickHandle.showSearchedHashTags()
                }
            } else if (it.error!!.message != getString(R.string.no_hashtags_available))
                handleAPIError(
                    it.error!!.message.toString()
                )
        })
    }

    private fun searchHashTags() = launch {
        commonCallbacks?.showProgressDialog()
        mViewModel.params.offset = 0
        mViewModel.searchHashTag().observe(viewLifecycleOwner, Observer {
            commonCallbacks?.hideProgressDialog()

            if (it.success == true) {
                showSearchedTag(it.data?.hashTags!!)
            } else if (it.success == false && it.message!!.contains(getString(R.string.no_hashtag_found))) {
                val regex =
                    Pattern.compile("[^\u0A80-\u0AFF\u0980-\u09FF\u0C00-\u0C7F\u0B80-\u0BFF\u0C80-\u0CFF\u0900-\u097Fa-zA-Z0-9#]");
                if (!regex.matcher(mViewModel.searchQuery).find()) {
                    showSearchedTag(ArrayList<HashTag>().apply { add(HashTag(tagName = mViewModel.searchQuery)) })
                }

            }
        })
        mBinding.etDescription.cursorAtEnd()
    }

    private fun showSearchedTag(tags: ArrayList<HashTag>) {
        mViewModel.searchTags.clear()
        mViewModel.searchTags.addAll(tags)
        clickHandle.showSearchedHashTags()
    }

    override fun selectedCategory(value: Category) {
        mViewModel.selectedCategory.set(value)
        mBinding.tvCategoryValue.text = value.fetchCurrentLanguageCategoryName()
        mViewModel.selectedCategory.set(value)
        showData(false)
    }

    override fun selectedHashTag(value: String) {
        CommonUtils.hideKeyboard(context, mBinding.root)
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
        } else {
            mBinding.etDescription.setText(
                if (currentDesc.endsWith("#")) currentDesc.plus(value) else currentDesc.plus("#$value")
            )
        }
        mViewModel.clearSearch()
//            mViewModel.hashTags.clear()
//        }
//        else Validator.showMessage(getString(R.string.hashtag_already_exist))
        mBinding.apply {
            this.etDescription.cursorAtEnd()
            this.etDescription.requestFocus()
            (requireActivity() as DashboardActivity).showKeyboard()
            showData(false)
            this.etDescription.addTextChangedListener(textWatcher)
        }
    }

    override fun fetchData(value: Any) {
        if (value is Category) {
            mViewModel.params.offset = mViewModel.categories.size
            getCategories(false)
        } else if (!TextUtils.isEmpty(mViewModel.searchQuery)) {
            mViewModel.params.offset = mViewModel.searchTags.size
            searchHashTags()
        } else {
            mViewModel.params.offset = mViewModel.hashTags.size
            getHashTags(false)
        }
    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            handler.removeCallbacks(workRunnable)
            workRunnable = Runnable {
                mViewModel.apply {
                    var query = s!!.toString()
                    if (query.contains("\n")) query = query.replace("\n", " \n")
                    searchQuery = mViewModel.getSearchQuery(query)
                    if (searchQuery.startsWith("#")) {
                        hashtagsScrollListener?.resetState()
                        searchQuery = searchQuery.replace("#", "")
                        searchHashTags()
//                        hashTagAlreadyExistWhileTyping()
                    } else showData(false)
                }
            }
            handler.postDelayed(workRunnable, 500 /*delay*/)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                maxLimitReachedAlert()
                val regex =
                    Pattern.compile("[^\u0A80-\u0AFF\u0980-\u09FF\u0C00-\u0C7F\u0B80-\u0BFF\u0C80-\u0CFF\u0900-\u097Fa-zA-Z0-9#]");
                if (it.endsWith(" ") || it.endsWith("\n")
                    || (it.isNotEmpty() && regex.matcher(it[it.lastIndex].toString()).find())
                ) {
                    mViewModel.searchTags.clear()
                    showData(false)
                }

            }
        }
    }

    private fun showMarkedTags() {
        linkHashTag = Link(Pattern.compile("(#\\w+)"))
            .setUnderlined(false)
            .setTextStyle(Link.TextStyle.BOLD)
            .setTextColor(resources.getColor(R.color.colorPrimary))

        mBinding.etDescription.addLink(linkHashTag)
    }

    private fun addHashToDescription() {
        val value = mBinding.etDescription.text.toString()
        mBinding.etDescription.setText(if (!value.endsWith("#")) value.plus("#") else value)
    }

    private fun maxLimitReachedAlert() {
        if (mBinding.etDescription.text.toString().length == 200) {
            wordValidationToast = Toast.makeText(
                requireContext(),
                getString(R.string.max_character_reached),
                Toast.LENGTH_SHORT
            )

            wordValidationToast?.show()
        }
    }

    private fun backConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.cancel_post))
        builder.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            dialog.dismiss()
            findNavController().navigateUp()
        }

        builder.setNegativeButton(
            "No"
        ) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()

    }

//    fun hashTagAlreadyExistWhileTyping() {
//        if (mViewModel.hashTagAlreadyExistWhenType(mViewModel.searchQuery)) {
////            Validator.showMessage(getString(R.string.hashtag_already_exist))
//            var currentDesc = mBinding.etDescription.text.toString()
//            currentDesc = currentDesc.removeRange(
//                currentDesc.lastIndexOf('#') + 1,
//                currentDesc.length
//            )
//            mBinding.etDescription.setText(currentDesc)
//        }
//    }

}
