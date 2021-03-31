package com.demo.dashboard_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.chips.ChipItem
import com.demo.databinding.FragmentChipsBinding
import com.demo.model.response.GetCategory
import com.google.android.material.chip.Chip


class CategoryFilterFragmentByChip : BaseFragment() {

    companion object {
        fun getInstance(data: List<GetCategory>): CategoryFilterFragmentByChip {
            return CategoryFilterFragmentByChip().let {
                it.arguments = bundleOf("_categories" to data)
                it
            }
        }
    }

    lateinit var mBinding: FragmentChipsBinding
    var categoryList = emptyList<GetCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @SuppressWarnings("Unchecked")
        categoryList =
            arguments?.getSerializable("_categories") as? List<GetCategory> ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentChipsBinding.inflate(inflater, container, false).let {
            mBinding = it
            initUi()
            it.root
        }
    }

    private fun initUi() {

        val categoryList = listOf<ChipItem>(
            ChipItem("#Hello"),
            ChipItem("#This"),
            ChipItem("#is"),
            ChipItem("#chip"),
            ChipItem("#testing"),
            ChipItem("#please"),
            ChipItem("#acknowledge"),
            ChipItem("#successful"),
            ChipItem("#implementation"),
            ChipItem("#Okay"),
            ChipItem("#Fine")
        )

        categoryList.forEachIndexed { index, getCategory ->

            val chip = Chip(context)
            chip.text = categoryList[index].name
            chip.isCheckable = true
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.bg_chip_color_category)

            mBinding.chips.addView(chip)

        }
    }

}