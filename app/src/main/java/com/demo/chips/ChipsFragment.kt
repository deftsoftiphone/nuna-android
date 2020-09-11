package com.demo.chips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentChipsBinding
import com.google.android.material.chip.Chip


class ChipsFragment : BaseFragment() {

    lateinit var mBinding: FragmentChipsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentChipsBinding.inflate(inflater, container, false)
        }
        return mBinding.root
    }

    fun init(chips : List<String>) {
        chips.forEachIndexed { index, s ->
            val chip = Chip(context)

            var str:String
            str="#" + chips[index].trim().replace("#","").replace("\"", "")
                .replace("\"", "").replace("'", "")


            chip.text = str
           chip.setTextAppearanceResource(R.style.chipTextappearance_new);
          //  chip.setPadding(0, 0, 0, 0);

          //  chip.checkedIcon = ContextCompat.getDrawable(requireContext(),R.mipmap.ic_launcher)
            mBinding.chips.addView(chip)
        }
    }
}
