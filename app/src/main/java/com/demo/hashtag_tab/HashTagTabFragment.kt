package com.demo.hashtag_tab

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentFollwingTabBinding
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.ParcelKeys
import com.demo.util.Validator
import com.demo.util.removeCategoriesWithNoPost
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class HashTagTabFragment : BaseFragment(), KodeinAware, HashTagPostClickListener {

    override val kodein: Kodein by closestKodein()
    private val viewModalFactory: HashTagsTabViewModalFactory by instance()
    lateinit var mBinding: FragmentFollwingTabBinding
    lateinit var mViewModel: HashTagsTabViewModel
    private var categoriesRecyclerAdapter: CategoriesRecyclerAdapter? = null
    private var hashTagOverViewRecyclerAdapter: HashTagTabRecyclerAdapter? = null
    private var hashTagImageSliderAdapter: HashTagTabImageSliderAdapter? = null
    private var categoryScroller: EndlessRecyclerViewScrollListener? = null
    private var scrollViewScrollListener: NestedScrollView.OnScrollChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupScrollViewScrollListener() {
        mBinding.apply {
            scrollViewScrollListener =
                NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    val view = nsvScroll.getChildAt(nsvScroll.childCount - 1)
                    val diff = view.bottom - (nsvScroll.height + nsvScroll.scrollY)
                    val offset = mViewModel.hashTagOverview.value?.size!!
                    if (diff == 0 && offset % 10 == 0 && !mViewModel.loadedAllPages.get()) {
                        mViewModel.getHashTagOverview(offset)
                    }
//                    else mBinding.tvNoHashTag.visibility = VISIBLE
                }
            nsvScroll.setOnScrollChangeListener(scrollViewScrollListener)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mViewModel =
                ViewModelProvider(this, viewModalFactory).get(HashTagsTabViewModel::class.java)
            mBinding = FragmentFollwingTabBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@HashTagTabFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
                edtSearch.setOnEditorActionListener(object :
                    TextView.OnEditorActionListener {
                    override fun onEditorAction(
                        v: TextView,
                        actionId: Int,
                        event: KeyEvent?
                    ): Boolean {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            return true
                        }
                        return false
                    }
                })
            }

        }
        setupScrollViewScrollListener()
        showShimmer(false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupHashTags()
        setupCategories()
        setupScrollListener()
//        showShimmer(true)
    }

    private fun setupImageSlider() {
        mBinding.apply {
            if (mViewModel.hashTagOverview.value?.isNotEmpty()!!) {
                val temp = ArrayList<HashTag>()
                var size = 0
                size =
                    if (mViewModel.hashTagOverview.value!!.size < 5) mViewModel.hashTagOverview.value!!.size else 5
                for (i in 0 until size) {
                    temp.add(mViewModel.hashTagOverview.value!![i])
                }
                hashTagImageSliderAdapter = HashTagTabImageSliderAdapter(temp)
                slTags.create(hashTagImageSliderAdapter!!, lifecycle = lifecycle)

                if (size == 0) {
                    slTags.visibility = GONE
                    iPlutoIndicator.visibility = GONE
                } else {
                    slTags.visibility = VISIBLE
                    iPlutoIndicator.visibility = VISIBLE
                    slTags.setCustomIndicator(iPlutoIndicator)
                    slTags.indicatorVisibility = true
                    slTags.isCycling = true
                    slTags.startAutoCycle()
                }

            } else {
                slTags.visibility = GONE
                iPlutoIndicator.visibility = GONE
            }
        }
    }

    private fun showNoData(show: Boolean) {
        mBinding.apply {
            if (show) {
                lNoDataFound.visibility = VISIBLE
                rvHashTags.visibility = GONE
//                slTags.visibility = GONE
//                iPlutoIndicator.visibility = GONE
//                rvCategories.visibility = GONE
            } else {
                lNoDataFound.visibility = GONE
                rvCategories.visibility = VISIBLE
                rvHashTags.visibility = VISIBLE
                slTags.visibility = VISIBLE
                iPlutoIndicator.visibility = VISIBLE
            }
        }
    }

    private fun setupObserver() = launch {
        mViewModel.apply {
            position.observe(viewLifecycleOwner, Observer {
                mBinding.rvHashTags.scrollToPosition(it)
            })

            showLoading.observe(viewLifecycleOwner, Observer {
                if (it) {
                    commonCallbacks?.showProgressDialog()
//                    tvNoHashTag.visibility = GONE
                } else {
                    commonCallbacks?.hideProgressDialog()
                }
            })

            showNoData.observe(viewLifecycleOwner, Observer {
                if (it) {
                    if (mViewModel.hashTagOverview.value?.size!! < 1) {
                        showNoData(true)
                    } else showNoData(false)
                } else showNoData(false)
            })

            toastMessage.observe(viewLifecycleOwner, Observer {
                if (!TextUtils.isEmpty(it) && it != getString(R.string.noHashtagMsg))
                    Validator.showMessage(it)
            })

            selectedCategory.observe(viewLifecycleOwner, Observer {
                hashTagOverViewRecyclerAdapter?.clearData()
                mBinding.rvHashTags.scrollToPosition(0)
            })

            hashTagOverview.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    hashTagOverViewRecyclerAdapter!!.updateHashTags(it)
                    showShimmer(false)
                }
                setupImageSlider()
            })
        }
    }

    private fun setupCategories() {
        mBinding.apply {
            rvCategories.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

            categoriesRecyclerAdapter =
                CategoriesRecyclerAdapter(
                    mViewModel.selectedCategoryPosition,
                    mViewModel.categoryClickListener()
                )
            rvCategories.adapter = categoriesRecyclerAdapter
            if (mViewModel.categories.value?.isNotEmpty()!!) categoriesRecyclerAdapter?.addCategories(
                mViewModel.categories.value!!.removeCategoriesWithNoPost(getString(R.string.all_categories))
                    .reversed()
            )
        }
        getCategories()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getCategories() = launch {
        mViewModel.showLoading.postValue(true)
        mViewModel.getCategories().observe(this@HashTagTabFragment, Observer {
            if (it.error == null) {
                val categories = it.data?.categories
                if (categories != null && categories.isNotEmpty()) {
                    mViewModel.updateCategories(categories)
                    if (categoriesRecyclerAdapter != null) {
                        categoriesRecyclerAdapter!!.addCategories(
                            categories.removeCategoriesWithNoPost(
                                getString(R.string.all_categories)
                            ).reversed()
                        )
                    }
                }
                mViewModel.showLoading.postValue(false)
            } else if (mViewModel.categories.value?.isEmpty()!!) {
                handleAPIError(it.error!!.message.toString())
                mViewModel.showLoading.postValue(false)
            }
        })
    }

    private fun setupScrollListener() {
        categoryScroller = object :
            EndlessRecyclerViewScrollListener(mBinding.rvCategories.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (mViewModel.categories.value!!.size % 10 == 0 && mViewModel.categories.value!!.size != 0)
                    getCategories()
            }
        }
        mBinding.rvCategories.addOnScrollListener(categoryScroller!!)
    }


    private fun setupHashTags() {
        mViewModel.clearOverview()
        hashTagOverViewRecyclerAdapter =
            HashTagTabRecyclerAdapter(R.layout.hashtag_posts, this)
        mBinding.apply {
            rvHashTags.layoutManager = LinearLayoutManager(requireContext())
            rvHashTags.adapter = hashTagOverViewRecyclerAdapter
        }
        mViewModel.getHashTagOverview(0)
    }

    private fun showShimmer(show: Boolean) {
        mBinding.apply {
            if (show) {
                sflShimmerProgress.visibility = VISIBLE
                clBody.visibility = GONE
            } else {
                sflShimmerProgress.stopShimmerAnimation()
                sflShimmerProgress.visibility = GONE
                clBody.visibility = VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as DashboardActivity).hideKeyboard()
//        mBinding.sflShimmerProgress.startShimmerAnimation()
//        mBinding.slTags.startAutoCycle()
    }

    override fun onPause() {
//        mBinding.sflShimmerProgress.stopShimmerAnimation()
        mBinding.slTags.stopAutoCycle()
        super.onPause()
    }

    private fun setupViewModel() {

    }

    inner class ClickHandler {
        fun search() {
            findNavController().navigate(R.id.SearchFragment)
        }
    }

    override fun postClicked(hashTagId: String, post: PostAssociated, position: Int) {
        if (post.id == null) {
            mViewModel.position.postValue(position)
            findNavController().navigate(
                R.id.allHashTagPostsFragment
                , bundleOf(
                    ParcelKeys.SELECTED_HASHTAG_ID to hashTagId
                    , ParcelKeys.SELECTED_CATEGORY_ID to mViewModel.selectedCategory.value?.id
                )
            )
        }
    }
}
