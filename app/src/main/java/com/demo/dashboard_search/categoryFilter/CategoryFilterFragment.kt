package com.demo.dashboard_search.categoryFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentCategoryFilterBinding
import com.demo.model.response.GetCategory

class CategoryFilterFragment : BaseFragment() {

    lateinit var mBinding: FragmentCategoryFilterBinding
    val mClickHandler: ClickHandler by lazy { ClickHandler() }
    val mViewModel by viewModels<CategoryFilterViewModel> { MyViewModelProvider(commonCallbacks as AsyncViewController) }
    val mAdapter: CategoryFilterAdapter by lazy { CategoryFilterAdapter(R.layout.layout_category_filter_item) }

    var parentClick : ((position: Int, item: GetCategory)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserver()
        mViewModel.callGetCategoryListApi()
    }

    fun onNoLocationAvailable(){
        if (mAdapter.list.isNotEmpty()) {
            mAdapter.list[0].filterStatus = CategoryFilterStatus.UNSELECTED
            mAdapter.notifyItemChanged(0)
        }
    }

    private fun setupObserver() {
        mViewModel.responseGetCategory.observe(this, Observer {
            if (!it.data.isNullOrEmpty()){
                val final = ArrayList(it.data!!)
                final.add(0,GetCategory(categoryName = getString(R.string.near_by)))
                mAdapter.setNewItems(final)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCategoryFilterBinding.inflate(inflater, container, false).let {
            mBinding = it
            setupRecycler()
            it.root
        }
    }

    private fun setupRecycler() {
        mAdapter.addClickEventWithView(R.id.category_parent, mClickHandler::onClickCategory)
        mBinding.recyclerCategory.itemAnimator = null
        mBinding.recyclerCategory.adapter = mAdapter
    }

    inner class ClickHandler {
        fun onClickCategory(position: Int, item: GetCategory) {
            if (item.filterStatus != CategoryFilterStatus.UNSELECTED) {
                //un select element
                item.filterStatus = CategoryFilterStatus.UNSELECTED
            } else {
                if (position == 0) {
                    item.filterStatus = CategoryFilterStatus.SELECTED_NEAR
                } else {
                    item.filterStatus = CategoryFilterStatus.SELECTED_NORMAL
                }
            }


            mAdapter.notifyItemChanged(position)
            //mAdapter.notifyItemRangeChanged(0, mAdapter.itemCount)
            //mAdapter.notifyDataSetChanged()
            parentClick?.invoke(position, item)
        }
    }


}