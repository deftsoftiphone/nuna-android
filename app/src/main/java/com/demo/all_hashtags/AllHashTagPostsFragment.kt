package com.demo.all_hashtags

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.banuba.sdk.cameraui.domain.MODE_RECORD_VIDEO
import com.banuba.sdk.ve.flow.VideoCreationActivity
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.activity.EDITVIDEO
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.databinding.AllHashtagPostsFragmentBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.HashTag
import com.demo.profile.CommonPostsAdapter
import com.demo.util.*
import com.demo.viewPost.home.ViewPostActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AllHashTagPostsFragment : BaseFragment(), KodeinAware, CommonPostsAdapter.OnItemClickPosts,
    BaseActivity.SetOnBackActionListener {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: AllHashTagPostsViewModelFactory by instance()
    private lateinit var viewModel: AllHashTagPostsViewModel
    private lateinit var binding: AllHashtagPostsFragmentBinding
    private lateinit var postAdapter: CommonPostsAdapter
    val mClickHandler by lazy { ClickHandler() }
    private var postScrollListener: EndlessRecyclerViewScrollListener? = null
    var fromPost = false

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
        fromPost =
            arguments?.getString("FROM") == "show_hashtag"
        (requireActivity() as BaseActivity).setOnBackActionListener = this@AllHashTagPostsFragment
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

    /*   override fun onStop() {
           super.onStop()
           requireContext().stopPreLoadingService()
       }*/

    private fun setupObserver() {
        viewModel.apply {
            hashTagPosts.observe(viewLifecycleOwner, Observer { posts ->
                if (posts.isNotEmpty()) {
                    postAdapter.setNewItems(posts!!)
                    hashTagPosts.value?.let { requireContext().startPreLoadingService(it) }
//                    requireActivity().stopPreloadingService()
                } else {

                }


            })

            showLoading.observe(viewLifecycleOwner, Observer {
                (requireActivity() as DashboardActivity).apply {
                    if (!Util.checkIfHasNetwork()) {
                        hideProgressDialog()
                    } else {
                        if (it) commonCallbacks?.showProgressDialog() else commonCallbacks?.hideProgressDialog()
                    }
                }
            })

            toastMessage.observe(viewLifecycleOwner, Observer {
                (requireActivity() as DashboardActivity).apply {
                    if (!Util.checkIfHasNetwork()) {
                        hideProgressDialog()
                    }
                    if (!TextUtils.isEmpty(it))
                        Validator?.showMessage(it)


                }
                if (it.equals("No hashtag found")) {
                    findNavController().navigateUp()
                }
            })

            showNoData.observe(viewLifecycleOwner, Observer {
                binding.apply {
                    lNoDataFound.visibility = if (it) VISIBLE else GONE
                }
            })

            hashTag.observe(viewLifecycleOwner, Observer {
                it?.let { tag ->
                    binding?.let { binding ->
                        binding.hashTag = tag
                        binding.invalidateAll()
                        updateFollowStatusUI(hashTag.value!!)
                    }
                    if (!TextUtils.isEmpty(tag.tagName)) {
                        binding.tvHashTag.text = "#${tag.tagName.toString().toLowerCase()}"
                        runWithDelay(100) {
                            binding.tvHashTag.visibility = VISIBLE
                        }
                    }
                }
            })
        }
    }


    private fun updateFollowStatusUI(hashTag: HashTag) {
        binding.apply {
            if (Prefs.init().selectedLangId == 2) {
                viewModel.isFollowed =
                    if (hashTag.followedByMe == null) false else hashTag.followedByMe!!
                if (viewModel.isFollowed) {
                    bFollow.isSelected = false
                    bFollow.text = getString(R.string.following)
                    bFollow.typeface = ResourcesCompat.getFont(requireContext(), R.font.gotham_pro)
                } else {
                    bFollow.isSelected = true
                    bFollow.text = getString(R.string.follow)
                    bFollow.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.gotham_pro_medium)
                }
                bFollow.visibility = VISIBLE
            } else {
                viewModel.isFollowed =
                    if (hashTag.followedByMe == null) false else hashTag.followedByMe!!
                if (viewModel.isFollowed) {
                    bFollow.isSelected = false
                    bFollow.text = getString(R.string.following)
                    bFollow.typeface = ResourcesCompat.getFont(requireContext(), R.font.nirmala)
                } else {
                    bFollow.isSelected = true
                    bFollow.text = getString(R.string.follow)
                    bFollow.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.nirmala_b)
                }
                bFollow.visibility = VISIBLE
            }
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
        hashTagId?.let { viewModel.updateIds(it, categoryId) }

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
            if (fromPost) {
                requireActivity().finish()
            } else {
                onFragBack()
            }
        }

        fun followHashTag() {
            viewModel.followHashTag()
        }

        fun onClickAdd() {
            (requireActivity() as DashboardActivity).startActivityForResult(
                VideoCreationActivity.buildIntent(
                    requireActivity(),
                    MODE_RECORD_VIDEO
                ), EDITVIDEO
            )
        }
    }

    override fun onPostClick(position: Int, id: String) {
        viewModel.hashTagPosts?.let {
            val bundle = Bundle()
//            bundle.putSerializable(getString(R.string.intent_key_post), it.value)
            DataHolder.data = it.value

            bundle.putInt(getString(R.string.intent_key_post_position), position)
            startActivity(
                Intent(
                    requireContext(),
                    ViewPostActivity::class.java
                ).putExtra(getString(R.string.intent_key_show_post), bundle)
            )
        }
    }

    override fun onUserClick(position: Int, id: String) {
        val userId = Prefs.init().currentUser?.id
        if (id == userId)
            findNavController().navigate(R.id.ProfileFragment)
        else
            findNavController().navigate(
                R.id.OthersProfileFragment,
                bundleOf(ParcelKeys.OTHER_USER_ID to id)
            )
    }

    override fun onActionBack(): Boolean {
        if (fromPost) {
            requireActivity().finish()
            return true
        } else {
            return false
        }
    }
}