package com.demo.followPopularUsers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentFollowPopularUsersBinding
import com.demo.model.UserPostWrapper
import com.demo.model.response.Post
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.ParcelKeys
import com.demo.util.Util


class FollowPopularUsersFragment : BaseFragment() {

    lateinit var mViewModel: FollowPopularUsersViewModel
    lateinit var mBinding: FragmentFollowPopularUsersBinding
    lateinit var mAdapter: FollowPopularUsersAdapter
    lateinit var mClickHandler: ClickHandler
    private var mEndlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        Util.updateStatusBarColor("#FAFAFA",activity as FragmentActivity)
        mViewModel.callGetPopularUsersApi(null, mViewModel.responseGetPopularUsers_NEW)
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(FollowPopularUsersViewModel::class.java)
    }

    private fun setupObserver() {
        mViewModel.responseGetPopularUsers_NEW.observe(this, Observer {
            if (it.data?.size != 0) {
                mAdapter.setNewItems(it.data!!)
            }
        })

        mViewModel.responseFollowUnfollowUser.observe(this, Observer {
            if (it.data?.followerUserId != 0) {
                mAdapter.updateRow(it.data!!)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!::mBinding.isInitialized) {
            mClickHandler = ClickHandler()
            mViewModel.selectedDate.set(null)
            mViewModel.lastSearchKeyword.set("")
            mBinding = FragmentFollowPopularUsersBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@FollowPopularUsersFragment
                clickHandler = mClickHandler
                viewModel = mViewModel
            }
            commonCallbacks?.setupToolBar(
                mBinding.toolbarLayout,
                false,
                getString(R.string.follow_popular_users)
            )
            commonCallbacks?.setupActionBarWithNavController(mBinding.toolbarLayout.toolbar)
            setupRecycler()

        }
        return mBinding.root
    }

    private fun setupRecycler() {
        mAdapter = FollowPopularUsersAdapter(R.layout.layout_popular_user, mClickHandler)
        mAdapter.addClickEventWithView(
            R.id.tv_follow_unfollow,
            mClickHandler::onClickFollowUnFollow
        )
        mAdapter.addClickEventWithView(R.id.iv_user, mClickHandler::onClickPostOwnerProfile)
        mBinding.recyclerUsers.adapter = mAdapter
    }

    private fun resetList() {
        mEndlessRecyclerViewScrollListener?.resetState()
    }

    private fun setScrollListener() {
        mEndlessRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerUsers.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    //mViewModel.loadNextPage()
                }
            }
        mBinding.recyclerUsers.addOnScrollListener(mEndlessRecyclerViewScrollListener!!)
    }

    inner class ClickHandler {

        fun onClickFollowUnFollow(pos: Int, clickedItem: UserPostWrapper) {
            when (clickedItem.user.followStatus) {

                FollowStatus.UNFOLLOWED -> {
                    mViewModel.callFollowUnFollowUserApi(clickedItem.user.userId, true)
                }

                FollowStatus.FOLLOWED -> {
                    mViewModel.callFollowUnFollowUserApi(clickedItem.user.userId, false)
                }

                else -> {
                }
            }
        }

        fun onClickPostOwnerProfile(position: Int, clickedItem: UserPostWrapper) {
            navigate(R.id.OthersProfileFragment, ParcelKeys.PK_PROFILE_ID to clickedItem.user.userId)
        }

        fun onClickContinue() {
            startActivity(Intent(activity, DashboardActivity::class.java))
            activity?.finish()
        }

        fun onClickUserPost(position: Int, clickedPost: Post) {
            Log.d("", "")
            navigate(R.id.PostDetailsFragment, ParcelKeys.PK_POST_ID to clickedPost.postId)
        }

        fun onClickUser(position: Int, clickedUser: UserPostWrapper) {

        }
    }
}
