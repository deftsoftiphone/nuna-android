package com.demo.create_post

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentEditPostBinding
import com.demo.model.request.RequestSavePost
import com.demo.model.response.GetCategory
import com.demo.model.response.Post
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList


class EditPostFragment : BaseFragment() {
    lateinit var post: Post
    lateinit var mBinding: FragmentEditPostBinding
    lateinit var mViewModel: CreatePostViewModel
    var isVideo: Boolean = false
    var chipsData = arrayListOf<GetCategory>()
    lateinit var progressDialog:ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        progressDialog = ProgressDialog(activity)
    }
    @SuppressLint("ClickableViewAccessibility", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!::mBinding.isInitialized) {
            mBinding = FragmentEditPostBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@EditPostFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }
        }
        post = requireArguments().getSerializable(Post::class.java.name) as Post


//        mViewModel.callGetPostDetailsApi(post.postId)


        mBinding.etDescription.setOnTouchListener(View.OnTouchListener { view, event ->
            if (view.id == R.id.et_description) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(
                        false
                    )
                }
            }
            false
        })

//        mViewModel.callGetCategoryApi()


        commonCallbacks?.setupToolBar(
            mBinding.toolbarLayout,
            true,
            getString(R.string.edit_post)
        )
        mBinding.chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            val chip = chipGroup.findViewById<Chip>(i)
           // chip.setCheckableResource(R.color.bg_chip_color_category_new)
            if (chip != null) {
                category = ""+chip.getTag()
            }
        }

        return mBinding.root

    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(CreatePostViewModel::class.java)
        // mViewModel.parseBundle(arguments)
    }

    private fun setupObserver() {
        mViewModel.responseSavePost.observe(this, Observer {

            onFragBack()
            findNavController().navigate(R.id.ProfileFragment)
            /* Handler(Looper.getMainLooper()).postDelayed({
                 run {
                     findNavController().navigate(R.id.ProfileFragment)
                 }
             },200)*/
        })
        mViewModel.responseGetPostDetails.observe(this, Observer {
            if (it.data != null) {
                post= it?.data!!.post!!

              /*  var req: RequestSavePost
                req= RequestSavePost()

                req.title=post.title
                req.description=post.description
                req.hashTags=post.hashTags
                req.postId=post.postId
                req.images=post.images
                req.video=post.video
                req.user?.userId = Prefs.get().loginData!!.userId
                req.categoryIds = post.categoryIds
                category== post.categoryIds
                mViewModel.requestSavePost.set(req)

                mBinding.invalidateAll()
*/
            }
        })


        mViewModel.responseGetCategory.observe(this, Observer {
            if (it.data != null) {
                initChips(it.data!!)
            }
        })


    }





    inner class ClickHandler {
        fun onClickSave() {

            val req: RequestSavePost = mViewModel.requestSavePost.get()!!
//            req.categoryId=category
            mViewModel.requestSavePost.set(req)
            if (mViewModel.isSavePostValid()) {
//                mViewModel.callSavePostApi()
            }
        }


        fun onClickAddInterest() {
            val b = bundleOf(ParcelKeys.PK_GO_BACK to true)
            findNavController().navigate(R.id.CategoryFragment, b)
        }

    }

    fun getFileName(path: String): String {
        return System.currentTimeMillis().toString() + path.substring(path.lastIndexOf("."))
    }

    var category: String = ""
    fun initChips(list: List<GetCategory>) {
      chipsData = arrayListOf<GetCategory>()
      chipsData = ArrayList(list)
        mBinding.chipGroup.removeAllViews()
        for (c in chipsData) {
            buildChip(mBinding.chipGroup, c)
        }

    }

    private fun buildChip(entryChipGroup: ChipGroup, value: GetCategory): Chip? {
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f,
            resources.displayMetrics
        ).toInt()

        val chip = Chip(requireContext()).apply {
            setPadding(paddingDp, 0, paddingDp, paddingDp)


            if (Prefs.init().selectedLangId == 2) {
                text = value.categoryNameHindi
            } else if (Prefs.init().selectedLangId == 5) {
                text = value.categoryNameBengali
            } else if (Prefs.init().selectedLangId == 4) {
                text = value.categoryNameMarathi

            } else if (Prefs.init().selectedLangId == 8) {
                text = value.categoryNameTelugu

            } else if (Prefs.init().selectedLangId == 6) {
                text = value.categoryNameTamil

            } else if (Prefs.init().selectedLangId == 3) {
                text = value.categoryNameGujarati

            } else if (Prefs.init().selectedLangId == 7) {
                text = value.categoryNameKannada

            } else if (Prefs.init().selectedLangId == 1) {
                text = value.categoryName


            }



        }

        val chipDrawable = ChipDrawable.createFromAttributes(
            activity as Context,
            null,
            0,
            R.style.chipFiter
        )

      //  val colorInt = resources.getColor(R.color.bg_chip_color_category_new)
      //  val csl: ColorStateList = ColorStateList.valueOf(colorInt)
       // chipDrawable.chipBackgroundColor=csl
        chip.setTag(value.categoryId)
        chip.setChipDrawable(chipDrawable)

        chip.setTextAppearanceResource(R.style.chipTextappearance);


        val states = arrayOf(
            intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(-android.R.attr.state_selected), // disabled
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(-android.R.attr.state_pressed)  // unpressed
        )

        val colors = intArrayOf(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.colorPrimaryDark), resources.getColor(R.color.colorPrimaryDark),  resources.getColor(R.color.colorPrimary))

        val colorsStateList = ColorStateList(states, colors)
        chip.apply {
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.colorAccent)
            )

            closeIconTint = colorsStateList
            chipBackgroundColor=colorsStateList

        }


        entryChipGroup.addView(chip)
        return chip
    }

    override fun onReceiveLocation(newLocation: Location?): Boolean {
        return super.onReceiveLocation(newLocation)
        mViewModel.latestLocation = newLocation

    }
    private fun bitmapToFile(bitmap:Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(activity?.applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream:OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }

    override fun onFragBack(): Boolean {
        return super.onFragBack()
    }
    // Add these extension functions to an empty kotlin file
/*
    fun Activity.getRootView(): View {
        return findViewById<View>(android.R.id.content)
    }
    fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
    }
    fun Activity.isKeyboardOpen(): Boolean {
        val visibleBounds = Rect()
        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        val heightDiff = getRootView().height - visibleBounds.height()
        val marginOfError = Math.round(this.convertDpToPx(50F))
        return heightDiff > marginOfError
    }

    fun Activity.isKeyboardClosed(): Boolean {
        return !this.isKeyboardOpen()
    }

    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        // Keep a reference to the last state of the keyboard
        private var lastState: Boolean =  (activity as Activity).isKeyboardOpen()
        */
/**
         * Something in the layout has changed
         * so check if the keyboard is open or closed
         * and if the keyboard state has changed
         * save the new state and invoke the callback*//*


        override fun onGlobalLayout() {
            val isOpen = (activity as Activity).isKeyboardOpen()
            if (isOpen == lastState) {
                return
            } else {
                dispatchKeyboardEvent(isOpen)
                lastState = isOpen
            }
        }
    }

    private fun dispatchKeyboardEvent(open: Any) {

        Log.e("LKSS",open.toString())
       // Toast.makeText("Lks is keyborad "+open)
    }
*/

}