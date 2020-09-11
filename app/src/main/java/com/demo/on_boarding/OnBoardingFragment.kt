package com.demo.on_boarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.SliderActivityBinding
import com.demo.util.Util
import java.util.*


class OnBoardingFragment : BaseFragment() {


    private lateinit var pagerAdapter: SlidingAdapter
    lateinit var mBinding: SliderActivityBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = SliderActivityBinding.inflate(inflater, container, false).apply {

        }

        Util.updateStatusBarColor(false,"#ef8f90",activity as FragmentActivity)
        val strings = ArrayList<String>()
        strings.add("one")
        strings.add("two")
        strings.add("three");
        strings.add("four")
        strings.add("five")
        pagerAdapter = SlidingAdapter(activity, strings)
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.login.setOnClickListener {
            if (mBinding.viewPager.currentItem == 0) {
                mBinding.viewPager.currentItem = 1
            } else if (mBinding.viewPager.currentItem == 1) {
                mBinding.viewPager.currentItem = 2
            } else if (mBinding.viewPager.currentItem == 2) {
                mBinding.viewPager.currentItem = 3
            } else if (mBinding.viewPager.currentItem == 3) {
                mBinding.login.text = resources.getString(R.string.get_started)
                mBinding.viewPager.currentItem = 4
            } else if (mBinding.viewPager.currentItem == 4) {
//                findNavController().navigate(R.id.CategoryFragment)
                findNavController().navigate(R.id.LanguageSelectFragment)
            }
        }

        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                Log.e("Pos ", "" + state)

                if (state == 4) {
                    mBinding.login.text = resources?.getString(R.string.get_started)
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

        })

        mBinding.indicator.attachViewPager(mBinding.viewPager)
        mBinding.indicator.setDotTint(R.color.grey)
        mBinding.indicator.setDotTintRes(R.color.colorPrimary)

        return mBinding.root
    }


}
