package com.demo.dashboard_search

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.dashboard_search.categoryFilter.CategoryFilterFragment
import com.demo.dashboard_search.categoryFilter.CategoryFilterStatus
import com.demo.databinding.FragmentDashboardBinding
import com.demo.model.response.GetCategory
import com.demo.model.response.Post
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.ParcelKeys
import com.demo.util.Util
import com.demo.webservice.ApiRegister

class DashboardSearchFragment : BaseFragment() {

    lateinit var mBinding: FragmentDashboardBinding
    lateinit var mViewModel: DashboardSearchViewModel
    lateinit var mAdapter: DashboardAdapter
    lateinit var llm: StaggeredGridLayoutManager
    private var mEndlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    val mClickHandler: ClickHandler by lazy { ClickHandler() }
    public val searchHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentDashboardBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@DashboardSearchFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }
            setupRecycler()
          /*  mBinding.edtSearch?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        // Your piece of code on keyboard search click
                        // mViewModel.se()
                        mViewModel.lastRequestGetPost.value!!.pageNumber = 0
                        mViewModel.searchHandler.postDelayed(mViewModel.searchRunnable, 400)
                        resetList()
                        return true
                    }
                    return false
                }
            })*/
        }
        Util.updateStatusBarColor("#FFFFFF", activity as FragmentActivity)
        return mBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        childFragmentManager.findFragmentById(R.id.category_frag)?.apply {
            (this as CategoryFilterFragment).parentClick =
                    this@DashboardSearchFragment.mClickHandler::onCategoryToggle
        }

        getParcelAndConsume(ParcelKeys.PARCEL_ID_POST_DETAIL) {
            if (it.size() == 0) return@getParcelAndConsume
            it.getSerializable(ParcelKeys.PK_POST)?.apply {
                mAdapter.updateItem(this as Post) {
                    it.postId == postId
                }
            }
        }
    }

    private fun setupViewModel() {
        mViewModel =
                ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                        .get(DashboardSearchViewModel::class.java)
    }

    private fun setupObserver() {
        mViewModel.responseGetPostList_NEW.observe(this, Observer {
            if (it.data != null) {



              /*  mBinding.recyclerPosts.visibility=View.GONE
                mBinding.recyclerPosts.visibility=View.VISIBLE
                mBinding.recyclerPosts.invalidate()*/
                mAdapter.clearData()
                mAdapter.setNewItems(it.data as List<Post>)
              /*  for (list in it.data as List<Post>){

                    if (list.images.isNotEmpty()) {

                        var urlList = list.images
                        var url: String

                        val urlArr = urlList?.split(",")


                        val transformation = RoundedCornersTransformation(12, 0)


                        if (urlList?.isNotEmpty() == true && urlArr?.get(0)?.isNotBlank() == true) {
                            url = urlArr[0]


                            if (url.startsWith("htt")) {

                            } else {
                                var strurl: String
                                strurl = AmazonUtil.S3_MEDIA_PATH + url + ".jpg";
                                url = strurl
                            }

                            var thumb = url.replace(
                                url.split("/")[url.split("/").size - 1],
                                ""
                            ) + "thumb/" + url.split("/")[url.split("/").size - 1]


                            var requestOptions = RequestOptions()
                            requestOptions =
                                requestOptions.transforms(CenterCrop(), RoundedCorners(12))
                            this!!.context?.let { it1 ->
                                Glide.with(it1)
                                    .load(thumb)
                                    .apply(requestOptions)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                            }
                        }

                    }}*/

              /*  Glide.with(context)
                    .load(url)
                    .apply(requestOptions)
                    .thumbnail(Glide.with(context).load(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)*/

            } else if (mViewModel.lastRequestGetPost.value!!.pageNumber == 0 && it == null) {
                mAdapter.clearData()
            }

        })

        mViewModel.responseGetPostList_PAGING.observe(this, Observer {
            mViewModel.pagerProgress.value = false
            if (it.data?.size != 0) {
                mAdapter.addNewItems(it.data as List<Post>)
                mViewModel.isPageLoading = false
                mViewModel.loadedAllPages = false
            }
        })

        mViewModel.responseLikeUnLikePost.observe(this, Observer {
            if (it.data != null) {
                val newValue = it.data?.like!!
                var post = mAdapter.getItem(mViewModel.position)
                post.postLiked = newValue
                post.likeCount = if (newValue) post.likeCount + 1 else post.likeCount - 1
                mAdapter.updatePost(post, mViewModel.position)
            }
        })
    }


    override fun onApiRequestFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        if (apiUrl == ApiRegister.GETPOSTLIST) {
            if (mViewModel.lastSearchKeyword.get()?.isBlank() != true) {
                if( mViewModel.lastRequestGetPost.value!!.pageNumber ==0){
                    mAdapter.clearData()
                }

            } else {
                mViewModel.pagerProgress.value = false
                mViewModel.loadedAllPages = true
                mViewModel.isPageLoading = false
            }
            return true

        } else return super.onApiRequestFailed(apiUrl, errCode, errorMessage)
    }

    private fun setupRecycler() {
        DashboardAdapter(R.layout.layout_dashboard_post_item).apply {
            mAdapter = this
            context = activity as Activity
            mAdapter.addClickEventWithView(R.id.card_parent, mClickHandler::onClickPost)
            mAdapter.addClickEventWithView(R.id.iv_owner, mClickHandler::onClickPostOwnerProfile)
            mAdapter.addClickEventWithView(R.id.iv_fav, mClickHandler::onFavClick)

        }
        mBinding.recyclerPosts.itemAnimator = null
        mBinding.recyclerPosts.apply {
            llm = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
            )
            layoutManager = llm
            adapter = mAdapter
        }


        setScrollListener()
    }

    private fun resetList() {
        mEndlessRecyclerViewScrollListener?.resetState()
    }

    private fun setScrollListener() {
        mEndlessRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerPosts.layoutManager as StaggeredGridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    mViewModel.loadNextPage()
                }
            }

        mBinding.recyclerPosts.addOnScrollListener(mEndlessRecyclerViewScrollListener!!)


/*
        mBinding.nestedScroll.viewTreeObserver
                .addOnScrollChangedListener {
                    val nS = mBinding.nestedScroll
                    val view = nS.getChildAt(nS.childCount - 1) as View
                    val diff: Int = view.bottom - (nS.height + nS.scrollY)
                    if (diff == 0*//* && mAdapter.itemCount % 10 == 0*//*) {
                        mViewModel.loadNextPage()
                    }
                }*/
    }

    inner class ClickHandler {
        fun onClickClearText() {
            mBinding.edtSearch.setText("")
        }

        fun onCategoryToggle(position: Int, item: GetCategory) {

            if (position == 0) {
                if (mViewModel.latestLocation == null) {
                    commonCallbacks?.requestLocation()
                } else {
                    if (item.filterStatus == CategoryFilterStatus.UNSELECTED) {
                        mViewModel.lastRequestGetPost.value?.toggleLocation(
                                mViewModel.latestLocation,
                                false
                        )
                    } else {
                        mViewModel.lastRequestGetPost.value?.toggleLocation(
                                mViewModel.latestLocation,
                                true
                        )
                    }
                }
            } else {
                if (item.filterStatus == CategoryFilterStatus.SELECTED_NORMAL) {
                    //add category in filter
                    item.categoryId?.let { mViewModel.lastRequestGetPost.value?.addCategory(it) }

                } else {
                    //remove category from filter
                    item.categoryId?.let { mViewModel.lastRequestGetPost.value?.removeCategory(it) }

                }
            }

            var stringIds:String=mViewModel.lastRequestGetPost.value!!.categoryIds

            mViewModel.lastRequestGetPost.value!!.pageNumber=0
//            mViewModel.callGetPostList(mViewModel.responseGetPostList_NEW, true)
            resetList()
        }


        fun onClickPost(position: Int, clickedItem: Post) {
            navigate(R.id.PostDetailsFragment, ParcelKeys.PK_POST_ID to clickedItem.postId)
        }

        fun onClickPostOwnerProfile(position: Int, clickedItem: Post) {
            navigate(
                    R.id.OthersProfileFragment,
                    ParcelKeys.PK_PROFILE_ID to clickedItem.user.userId
            )
        }

        fun onFavClick(position: Int, clickedItem: Post) {
            mViewModel.position = position
//            mViewModel.callLikeUnlikePostApi(
//                mAdapter.getItem(position).postId,
//                !mAdapter.getItem(position).postLiked
//            )

            Handler().postDelayed({


            }, 5000)


        }
    }

    override fun onReceiveLocation(newLocation: Location?): Boolean {
        if (newLocation == null) {
            if (mViewModel.latestLocation == null) {
                (childFragmentManager.findFragmentById(R.id.CategoryFilterFragment) as? CategoryFilterFragment)?.onNoLocationAvailable()
            } else {

            }
        } else {
            mViewModel.latestLocation = newLocation
            mViewModel.lastRequestGetPost.value?.toggleLocation(
                    mViewModel.latestLocation,
                    false
            )
//            mViewModel.callGetPostList(mViewModel.responseGetPostList_NEW, true)
        }
        return true
    }
}
