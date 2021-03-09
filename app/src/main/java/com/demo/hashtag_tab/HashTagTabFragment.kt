package com.demo.hashtag_tab

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentFollwingTabBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.HashTagImagesItem
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.*
import com.demo.util.Util.Companion.hasInternet
import com.demo.viewPost.home.ViewPostActivity
import com.opensooq.pluto.listeners.OnItemClickListener
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
                NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                    val view = nsvScroll.getChildAt(nsvScroll.childCount - 1)
                    val diff = view.bottom - (nsvScroll.height + nsvScroll.scrollY)
                    val offset = mViewModel.hashTagOverview.value?.size!!
                    if (diff == 0 && offset % 10 == 0 && !mViewModel.loadedAllPages.value!!) {
                        mViewModel.getHashTagOverview(offset)
                    }
//                    else mBinding.tvNoHashTag.visibility = VISIBLE
                }

            nsvScroll.setOnScrollChangeListener(scrollViewScrollListener)

            categoryScroller = object :
                EndlessRecyclerViewScrollListener(rvCategories.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    val offset = mViewModel.categories.value!!.size
//                    if (mViewModel.categories.value!!.size % 10 == 0 && offset != 0)
//                        getCategories(offset)
                }
            }
            rvCategories.addOnScrollListener(categoryScroller!!)

            slTags.setOnScrollChangeListener { v, i, i2, i3, i4 ->
                val view = slTags.getChildAt(slTags.childCount - 1)
                val diff = view.bottom - (slTags.height + slTags.scrollY)
                val offset = mViewModel.hashTagBanner.value?.size!!
                if (diff == 0 && offset % 10 == 0 && !mViewModel.allBannerLoaded) {
                    mViewModel.getHashTagBanner(offset)
                }
            }
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

            setupScrollViewScrollListener()
            setupObserver()
            setupHashTags()
            setupCategories()

            mViewModel.getCategories(0)
            mViewModel.getHashTagBanner(0)
            mViewModel.hashTagBanner.postValue(ArrayList())
        }

        return mBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupImageSlider() {
        mBinding.apply {
            hashTagImageSliderAdapter = HashTagTabImageSliderAdapter(mViewModel.hashTagBanner?.value!!)
            slTags.create(hashTagImageSliderAdapter!!, lifecycle = lifecycle)

            hashTagImageSliderAdapter!!.setOnItemClickListener(object :
                OnItemClickListener<HashTagImagesItem> {
                override fun onItemClicked(item: HashTagImagesItem?, position: Int) {
                    item?.hashtag?.id?.let { hashTagClicked(it, PostAssociated(), 0) }
                }
            })

            if (mViewModel.hashTagBanner.value!!.size > 0) {
                rlSlideImages.visibility = VISIBLE
                slTags.visibility = VISIBLE
                slTags.setCustomIndicator(iPlutoIndicator)
                slTags.indicatorVisibility = true
                slTags.isCycling = true
                slTags.startAutoCycle()
                iPlutoIndicator.visibility =
                    if (hashTagImageSliderAdapter!!.itemCount > 1) VISIBLE else GONE
            } else {
                slTags.visibility = GONE
                iPlutoIndicator.visibility = GONE
                rlSlideImages.visibility = GONE
            }
        }
    }

    private fun showNoData(show: Boolean) {
        mBinding.apply {
            if (show) {
                lNoDataFound.visibility = VISIBLE
                rvHashTags.visibility = GONE
            } else {
                lNoDataFound.visibility = GONE
                rvCategories.visibility = VISIBLE
                rvHashTags.visibility = VISIBLE
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun setupObserver() = launch {
        mViewModel.apply {
            showLoading.observe(this@HashTagTabFragment, {
                (requireActivity() as DashboardActivity).apply {
                    if (!Util.checkIfHasNetwork()) {
                        hideProgressDialog()
                    } else {
                        if (it) MyProgress.show(childFragmentManager)
                        else MyProgress.hide(childFragmentManager)
                    }
                }
            })

            showNoData.observe(this@HashTagTabFragment, {
                if (it) {
                    if (mViewModel.hashTagOverViewRecyclerAdapter?.itemCount!! < 1) {
                        showNoData(true)
                    } else showNoData(false)
                } else showNoData(false)
            })

            toastMessage.observe(this@HashTagTabFragment, {
                if (!TextUtils.isEmpty(it) && it != getString(R.string.noHashtagMsg))
                    Validator.showMessage(it)
            })

            selectedCategory.observe(this@HashTagTabFragment, {
                mViewModel.hashTagOverViewRecyclerAdapter?.clearData()
                mBinding.rvHashTags.scrollToPosition(0)
//                hashTagBanner.postValue(ArrayList())
//                getHashTagBanner(0)
            })

            hashTagOverview.observe(this@HashTagTabFragment, {
                println("it.size = ${it.size}")
                if (it.isNotEmpty()) {
//                    mViewModel.hashTagOverViewRecyclerAdapter!!.setNewItems(it)
                }
                isLoading.postValue(false)
            })

            isLoading.observe(this@HashTagTabFragment, {
                mBinding.rvCategories.isEnabled = !it
            })

            loadedAllPages.observe(this@HashTagTabFragment, {
                mBinding.tvNoHashTag.visibility =
                    if (it && mViewModel.hashTagOverViewRecyclerAdapter?.itemCount!! > 0) VISIBLE else GONE
            })

            hashTagBanner.observe(this@HashTagTabFragment, {
                setupImageSlider()
            })

            categories.observe(this@HashTagTabFragment, {
                if (it.isNotEmpty())
                    categoriesRecyclerAdapter?.setNewItems(it)

//                mViewModel.selectedCategoryPosition.value?.let {
//                    mBinding.rvCategories.scrollToPosition(it)
//
//                }
            })
        }
    }

    private fun setupCategories() {
        mBinding.apply {
            rvCategories.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            categoriesRecyclerAdapter =
                CategoriesRecyclerAdapter(
                    R.layout.layout_hashtag_category_recycler_item,
                    mViewModel.selectedCategoryPosition,
                    mViewModel.categoryClickListener()
                )
            rvCategories.adapter = categoriesRecyclerAdapter
        }
    }

    /* @SuppressLint("FragmentLiveDataObserve")
     private fun getCategories(offset: Int) = launch {
          mViewModel.showLoading.postValue(true)
          mViewModel.getCategories(offset).observe(this@HashTagTabFragment, {
              if (it.error == null) {
                  val categories = it.data?.categories
                  if (categories != null && categories.isNotEmpty()) {
                      mViewModel.updateCategories(categories)
                      if (categoriesRecyclerAdapter != null) {
  //                        categoriesRecyclerAdapter!!.updateCategories(
  //                            mViewModel.categories.value!!.removeCategoriesWithNoPost(getString(R.string.all_categories))
  //                                .reversed()
  //                        )

                          categoriesRecyclerAdapter!!.updateCategories(mViewModel.categories.value!!.reversed())
                      }
                  }
              } else {
  //                if (mViewModel.categories.value?.isEmpty()!!) {
  //                handleAPIError(it.error!!.message.toString())
              }
              mViewModel.showLoading.postValue(false)
          })
      }*/

    private fun setupHashTags() {
        mViewModel.clearOverview()
        mViewModel.hashTagOverViewRecyclerAdapter =
            HashTagTabRecyclerAdapter(R.layout.hashtag_posts, this)
        mBinding.apply {
            rvHashTags.layoutManager = LinearLayoutManager(requireContext())
            rvHashTags.itemAnimator?.changeDuration = 0
            rvHashTags.adapter = mViewModel.hashTagOverViewRecyclerAdapter
        }
        mViewModel.getHashTagOverview(0)
    }

    override fun onResume() {
        super.onResume()
        requireContext().updateLanguage()
        (requireActivity() as DashboardActivity).hideKeyboard()
        mBinding.invalidateAll()
        setupImageSlider()

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

    override fun hashTagClicked(hashTagId: String, post: PostAssociated, position: Int) {
        if (requireContext().hasInternet()) {
            if (post.id == null) {
//            mViewModel.position.postValue(position)
                findNavController().navigate(
                    R.id.allHashTagPostsFragment, bundleOf(
                        ParcelKeys.SELECTED_HASHTAG_ID to hashTagId,
                        ParcelKeys.SELECTED_CATEGORY_ID to mViewModel.selectedCategory.value?.id
                    )
                )
            }
        }
    }

    @ExperimentalStdlibApi
    override fun postClicked(hashTagId: String, posts: ArrayList<PostAssociated>, position: Int) {
        if (requireContext().hasInternet()) {
            val bundle = Bundle()
            val list = ArrayList<PostAssociated>()
            list.addAll(posts.filter { postAssociated -> !postAssociated.id.isNullOrEmpty() })
            DataHolder.data = list
            bundle.putInt(getString(R.string.intent_key_post_position), position)
            startActivity(
                Intent(
                    requireContext(),
                    ViewPostActivity::class.java
                ).putExtra(getString(R.string.intent_key_show_post), bundle)
            )
        }
    }
}
