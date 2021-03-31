package com.demo.category_select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentCategoryBinding
import com.demo.model.response.GetCategory
import com.demo.util.ParcelKeys
import com.demo.util.Util


class CategoryFragment : BaseFragment() {

    lateinit var mBinding: FragmentCategoryBinding
    lateinit var mViewModel: CategoryViewModel
    private lateinit var mAdapter: CategoryAdapter
    lateinit var mClickHandler: ClickHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        Util.updateStatusBarColor("#FAFAFA", activity as FragmentActivity)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel.callGetCategoryApi()
        mClickHandler = ClickHandler()
        mBinding = FragmentCategoryBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }

        commonCallbacks?.setupToolBar(
            mBinding.includeToolbar,
            false,
            getString(R.string.choose_areas_you_interested)
        )
        return mBinding.root
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(CategoryViewModel::class.java)
        mViewModel.parseBundle(arguments)
    }

    private fun setupObserver() {
        mViewModel.responseGetCategory.observe(this, Observer {
            if (it.data != null) {
                val arr = intArrayOf(
//                    R.drawable.cate_bitmap,
//                    R.drawable.cate_bitmap1,
//                    R.drawable.cate_bitmap2,
//                    R.drawable.cate_bitmap3,
//                    R.drawable.got
                )
                println("Category Response = $it")
                mAdapter = CategoryAdapter(it.data as List<GetCategory>, mClickHandler)
                mBinding.recyclerCategory.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                mBinding.recyclerCategory.adapter = mAdapter
            }
        })

        mViewModel.responseAddUserCategory.observe(this, Observer {
            if (it.data != null) {
                if (mViewModel.postCompleteGoBack) {
                    putParcel(
                        bundleOf(
                            ParcelKeys.PK_CATEGORY_LIST to
                                    mAdapter.selectedCategoriesName.joinToString(",")
                        ),
                        R.id.EditProfileFragment
                    )
                    findNavController().popBackStack()
                } else {
                    com.demo.util.Validator.showCustomToast(getString(R.string.cat_saved_successfully))
                    findNavController().navigate(R.id.FollowPopularUsersFragment)
                }
            }
        })
    }

    inner class ClickHandler {
        fun onClickContinue() {
            if (mAdapter.selectedCategoriesIds.isEmpty()) {
                com.demo.util.Validator.showCustomToast(getString(R.string.select_category))
                return
            }

            mViewModel.callSaveUserCategoryApi(mAdapter.selectedCategoriesIds)
        }
    }

}
