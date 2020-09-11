package com.demo.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentFollowingBinding
import com.demo.model.response.Document
import com.demo.util.EndlessRecyclerViewScrollListener


class FollowingFragment : BaseFragment() {
    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(FollowingViewModel::class.java)
    }

    lateinit var mViewModel: FollowingViewModel
    lateinit var mBinding: FragmentFollowingBinding
    lateinit var mAdapter: FollowingAdapter
    lateinit var mClickHandler: ClickHandler
    var mEndlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        mViewModel.callGetDocumentListApi(null, mViewModel.responseDocumentList_NEW)
    }

    private fun setupObserver() {
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
            mBinding = FragmentFollowingBinding.inflate(inflater, container, false).apply {
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
        mAdapter = FollowingAdapter(mClickHandler)
        mBinding.recyclerDocuments.adapter = mAdapter
        //setScrollListener()
    }

    private fun resetList() {
        mEndlessRecyclerViewScrollListener?.resetState()
    }

    private fun setScrollListener() {
        mEndlessRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerDocuments.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    // mViewModel.loadNextPage()
                }
            }
        mBinding.recyclerDocuments.addOnScrollListener(mEndlessRecyclerViewScrollListener!!)
    }

    inner class ClickHandler {
        fun onClickDocument(clickedItem: Document) {

        }
    }
}
