package com.demo.all_hashtags

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.activity.EDITVIDEO
import com.demo.base.BaseFragment
import com.demo.databinding.AllHashtagPostsFragmentBinding
import com.demo.databinding.LayoutMediaBinding
import com.demo.marveleditor.VIdeoEditerActivity
import com.demo.model.response.baseResponse.HashTag
import com.demo.profile.CommonPostsAdapter
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.Media
import com.demo.util.ParcelKeys
import com.demo.util.Validator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AllHashTagPostsFragment : BaseFragment(), KodeinAware, CommonPostsAdapter.OnItemClickPosts {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: AllHashTagPostsViewModelFactory by instance()
    private lateinit var viewModel: AllHashTagPostsViewModel
    private lateinit var binding: AllHashtagPostsFragmentBinding
    private lateinit var postAdapter: CommonPostsAdapter
    val mClickHandler by lazy { ClickHandler() }
    private var postScrollListener: EndlessRecyclerViewScrollListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AllHashtagPostsFragmentBinding.inflate(inflater)
        binding.apply {
            clickHandler = mClickHandler
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(AllHashTagPostsViewModel::class.java)
    }

    private fun setupScrollListener() {
        postScrollListener =
            object :
                EndlessRecyclerViewScrollListener(binding.rvPosts.layoutManager as GridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    if (viewModel.hashTagPosts.value?.size!! % 10 == 0) {
                        viewModel.getPosts()
                    }
                }
            }
        binding.rvPosts.addOnScrollListener(postScrollListener!!)
    }

    private fun setupObserver() {
        viewModel.apply {
            hashTagPosts.observe(viewLifecycleOwner, Observer { posts ->
                binding?.let { postAdapter.setNewItems(posts) }
            })

            showLoading.observe(viewLifecycleOwner, Observer {
                if (it) commonCallbacks?.showProgressDialog() else commonCallbacks?.hideProgressDialog()
            })

            toastMessage.observe(viewLifecycleOwner, Observer {
                if (!TextUtils.isEmpty(it)) Validator?.showMessage(it)
            })

            hashTag.observe(viewLifecycleOwner, Observer {
                it?.let { tag ->
                    binding?.let {
                        it.hashTag = tag
                        it.invalidateAll()
                        updateFollowStatusUI(hashTag.value!!)
                    }

                }
            })
        }
    }

    private fun updateFollowStatusUI(hashTag: HashTag) {
        val face: Typeface = Typeface.createFromAsset(
            requireContext().assets,
            "fonts/gotham_pro.ttf"
        )

        val face1: Typeface = Typeface.createFromAsset(
            requireContext().assets,
            "fonts/gothampro_medium.ttf"
        )

        binding.apply {
            viewModel.isFollowed =
                if (hashTag.followedByMe == null) false else hashTag.followedByMe!!
            if (viewModel.isFollowed) {
                bFollow.isSelected = false
                bFollow.text = getString(R.string.following)
                bFollow.typeface = face


            } else {
                bFollow.isSelected = true
                bFollow.text = getString(R.string.follow)
                bFollow.typeface = face1
            }
            bFollow.visibility = VISIBLE
        }
    }

    private fun setupPostRecycler() {
        postAdapter = CommonPostsAdapter(R.layout.dashboard_screen_item)
        postAdapter.click = this
        binding.rvPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPosts.adapter = postAdapter
        if (viewModel.hashTagPosts.value?.size!! < 1)
            viewModel.getPosts()
        binding.invalidateAll()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hashTagId = arguments?.getString(ParcelKeys.SELECTED_HASHTAG_ID)
        val categoryId: String? = arguments?.getString(ParcelKeys.SELECTED_CATEGORY_ID)
        viewModel.updateIds(hashTagId!!, categoryId)
        setupObserver()
        setupPostRecycler()
        setupScrollListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AllHashTagPostsViewModel::class.java)
    }


    inner class ClickHandler {
        fun backPress() {
            findNavController().navigateUp()
        }

        fun followHashTag() {
            viewModel.followHashTag()
        }

        fun onClickAdd() {
//            val dialog = BottomSheetDialog(requireContext())
//
//            val binding =
//                LayoutMediaBinding.inflate(LayoutInflater.from(requireContext())).apply {
//                    clickHandler = null
//                    clickHandler2 = mClickHandler
//                    this.dialog = dialog
//                }
//
//            dialog.setContentView(binding.root)
//            dialog.show()
        }

        fun onSelectMedia(media: Media, dialog: BottomSheetDialog) {
            dialog.dismiss()

            if (media == Media.IMAGE) {
                FishBun.with(this@AllHashTagPostsFragment)
                    .setImageAdapter(GlideAdapter())
                    .setIsUseDetailView(false)
                    .setMaxCount(1) //Deprecated
                    .setMaxCount(5)
                    .setMinCount(1)
                    .setPickerSpanCount(2)
                    .setActionBarColor(
                        Color.parseColor("#ef8f90"),
                        Color.parseColor("#ef8f90"),
                        false
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    // .setArrayPaths(path)
                    // .setAlbumSpanCount(2, 4)
                    .setAlbumSpanCountOnlPortrait(2)
                    .setButtonInAlbumActivity(false)
                    .setCamera(true)
                    .setReachLimitAutomaticClose(true)
                    // .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(this, R.drawable.ic_custom_back_white))
                    // .setOkButtonDrawable(ContextCompat.getDrawable(this, R.drawable.ic_custom_ok))
                    .setAllViewTitle("All")
                    .setActionBarTitle("Picture select")
                    .textOnImagesSelectionLimitReached("Limit Reached!")
                    .textOnNothingSelected("Nothing Selected")
                    .setSelectCircleStrokeColor(Color.BLACK)
                    .isStartInAllView(false)
                    .startAlbum()

            } else {
                val intent: Intent

                intent = Intent(requireContext(), VIdeoEditerActivity::class.java)

                startActivityForResult(intent, EDITVIDEO)
/*
                val intent: Intent = Intent(baseContext, FilePickerActivity::class.java)

                intent.putExtra(FilePickerActivity.CONFIGS,  Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(true)
                    .enableVideoCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build())
                startActivityForResult(intent, FILE_REQUEST_CODE)*/


            }

            // mViewModel.requestSaveUserProfile.get()!!.gender = gender.toString()
            binding.invalidateAll()
        }
    }

    override fun onPostClick(position: Int, id: String) {
        findNavController().navigate(
            R.id.OthersProfileFragment,
            bundleOf(ParcelKeys.OTHER_USER_ID to id)
        )
    }


}