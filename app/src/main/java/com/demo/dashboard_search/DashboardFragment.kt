package com.demo.dashboard_search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.create_post.CategoryClickedListener
import com.demo.databinding.DashboardFragmentBinding
import com.demo.hashtag_tab.CategoriesRecyclerAdapter
import com.demo.model.response.ProfilePosts
import com.demo.model.response.baseResponse.Category
import com.demo.profile.CommonPostsAdapter
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.ParcelKeys
import com.demo.util.removeCategoriesWithNoPost
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class DashboardFragment : BaseFragment(), KodeinAware, CommonPostsAdapter.OnItemClickPosts,
    CategoryClickedListener {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: DashboardFragmentBinding
    lateinit var mViewModel: DashboardSearchViewModel
    private val viewModelFactory: DashboardViewModelFactory by instance()
    var mEndlessFollowingRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    var mEndlessPopularRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    private var categoriesRecyclerAdapter: CategoriesRecyclerAdapter? = null
    private var categoryScrollListener: EndlessRecyclerViewScrollListener? = null
    val mClickHandler: ClickHandler by lazy { ClickHandler() }
    lateinit var followingPostsAdapter: CommonPostsAdapter
    lateinit var popularPostsAdapter: CommonPostsAdapter
    lateinit var postsList: ArrayList<ProfilePosts>
    var categoryId: String? = "all"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()

    }


/*

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, )
    }
*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            setListeners()
            loadViews()
        }
    }*/

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = DashboardFragmentBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@DashboardFragment
                viewModel = mViewModel
            }
            setupRecyclerView()
            setupCategories()
            getFollowing(true)
            onTabSelectionListener()


        }
        return mBinding.root

    }

    private fun onTabSelectionListener() {

        mBinding.tabs.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    rvCategories.visibility = View.GONE

                    if (mViewModel.followingPosts.size == 0) {
                        getFollowing(true)
                    } else {
                        lNoDataFound.visibility = View.GONE
                    }
                    recycler_view_following.visibility = View.VISIBLE
                    recycler_view_popular.visibility = View.GONE
                } else {
                    rvCategories.visibility = View.VISIBLE

                    Log.e("Ctaegoriessize...", "${mViewModel.popularPosts.size}")
                    if (mViewModel.popularPosts.size == 0) {
                        setupCategories()
                        getPopular(true, categoryId)
                    } else {
                        lNoDataFound.visibility = View.GONE

                    }
                    recycler_view_following.visibility = View.GONE
                    recycler_view_popular.visibility = View.VISIBLE
                }
            }

        })
    }


    private fun setupCategories() {

        getCategories(true)
        mBinding.apply {
            rvCategories.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            categoriesRecyclerAdapter =
                CategoriesRecyclerAdapter(
                    viewModel?.selectedCategoryPosition!!,
                    this@DashboardFragment
                )
            rvCategories.adapter = categoriesRecyclerAdapter
        }
        setupScrollListener()
    }

    private fun getCategories(showProgress: Boolean) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getCategories().observe(this@DashboardFragment, Observer {
            if (showProgress) commonCallbacks?.hideProgressDialog()
            if (it.error == null) {
                mViewModel.categories.clear()
                val categories = it.data?.categories
                if (categories != null && categories.isNotEmpty()) {
                    mViewModel.updateCategories(categories)

                    if (categoriesRecyclerAdapter != null)

                        categoriesRecyclerAdapter!!.addCategories(categories.removeCategoriesWithNoPost(getString(R.string.all_categories)).reversed())
                }
            } else {
                handleAPIError(it.error!!.message.toString())
            }
        })
    }

    private fun setupScrollListener() {
        categoryScrollListener = object :
            EndlessRecyclerViewScrollListener(mBinding.rvCategories.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (mViewModel.categories.size % 10 == 0)
                    getCategories(false)
            }
        }
        mBinding.rvCategories.addOnScrollListener(categoryScrollListener!!)
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(DashboardSearchViewModel::class.java)

    }

    private fun setupRecyclerView() {
        followingPostsAdapter = CommonPostsAdapter(R.layout.dashboard_screen_item)
        popularPostsAdapter = CommonPostsAdapter(R.layout.dashboard_screen_item)

        followingPostsAdapter.click = this
        popularPostsAdapter.click = this

        //Following Recycler View
        mBinding.recyclerViewFollowing.layoutManager = GridLayoutManager(context, 2)
        mBinding.recyclerViewFollowing.adapter = followingPostsAdapter

        //Popular Recycler View
        mBinding.recyclerViewPopular.layoutManager = GridLayoutManager(context, 2)
        mBinding.recyclerViewPopular.adapter = popularPostsAdapter

        setScrollListeners()
    }


    private fun getFollowing(showProgress: Boolean) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getFolllowing(mViewModel.followingPosts.size)
            .observe(this@DashboardFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {
                    val posts = it.data?.followingData
                    if (posts != null && posts.isNotEmpty()) {
                        lNoDataFound.visibility = View.GONE
                        recycler_view_following.visibility = View.VISIBLE
                        recycler_view_popular.visibility = View.GONE
                        mViewModel.followingPosts.addAll(it.data?.followingData!!)
                        followingPostsAdapter.addNewItems(it.data?.followingData!!)
                    } else {
                        if (mViewModel.followingPosts.size == 0) {
                            recycler_view_following.visibility = View.GONE
                            lNoDataFound.visibility = View.VISIBLE
                        }
                    }
                } else {
                    if (mViewModel.followingPosts.size == 0) {
                        recycler_view_following.visibility = View.GONE
                        lNoDataFound.visibility = View.VISIBLE
                    }
//                    handleAPIError(it.error!!.message.toString())
                }
            })
    }

    private fun getPopular(showProgress: Boolean, id: String?) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getPopular(mViewModel.popularPosts.size, id)
            .observe(this@DashboardFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {
                    val posts = it.data?.popularPosts
                    if (posts != null && posts.isNotEmpty()) {
                        recycler_view_popular.visibility = View.VISIBLE
                        recycler_view_following.visibility = View.GONE
                        lNoDataFound.visibility = View.GONE
                        mViewModel.popularPosts.addAll(it.data?.popularPosts!!)
                        Log.e("PopularListSIze", "S ${mViewModel.popularPosts.size}")
                        popularPostsAdapter.addNewItems(it.data?.popularPosts!!)
                    } else {
                        if (mViewModel.popularPosts.size == 0) {
                            recycler_view_popular.visibility = View.GONE
                            lNoDataFound.visibility = View.VISIBLE
                        }

                    }

                } else {
                    Log.e("popularSizeFailure", mViewModel.popularPosts.size.toString())
                    if (mViewModel.popularPosts.size == 0) {
                        recycler_view_popular.visibility = View.GONE
                        lNoDataFound.visibility = View.VISIBLE
                    }
//                    handleAPIError(it.error!!.message.toString())
                }
            })
    }

    private fun resetFollowersList() {
        mEndlessFollowingRecyclerViewScrollListener?.resetState()

    }

    private fun resetPopular() {
        mEndlessPopularRecyclerViewScrollListener?.resetState()

    }

    private fun setScrollListeners() {
        mEndlessFollowingRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerViewFollowing.layoutManager as GridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    Log.e("ONLOADMORE", "Onloadmore" + mViewModel.followingPosts.size)

                    if (mViewModel.followingPosts.size % 10 == 0 && mViewModel.followingPosts.size != 0 ) {
                        getFollowing(true)
                    }

                }
            }


        mEndlessPopularRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerViewPopular.layoutManager as GridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    Log.e("ONLOADMORE", "Onloadmore" + mViewModel.popularPosts.size)

                    if (mViewModel.popularPosts.size % 10 == 0 && mViewModel.followingPosts.size != 0 ) {
                        getPopular(true, categoryId)

                    }


                }
            }

        mBinding.recyclerViewPopular.addOnScrollListener(mEndlessPopularRecyclerViewScrollListener!!)
        mBinding.recyclerViewFollowing.addOnScrollListener(
            mEndlessFollowingRecyclerViewScrollListener!!
        )


    }

    inner class ClickHandler {

        fun onClickFollowingPost() {

        }

        fun onClickPopularPost() {

        }


    }

    override fun onPostClick(position: Int, id: String) {
        findNavController().navigate(
            R.id.OthersProfileFragment,
            bundleOf(ParcelKeys.OTHER_USER_ID to id)
        )
    }

    override fun selectedCategory(value: Category) {
        categoryId = value.id
        popularPostsAdapter.clearData()
        mViewModel.popularPosts.clear()
        mBinding.recyclerViewPopular.scrollToPosition(0)
        popularPostsAdapter.notifyDataSetChanged()
        resetPopular()
        getPopular(true, value.id)

    }


}