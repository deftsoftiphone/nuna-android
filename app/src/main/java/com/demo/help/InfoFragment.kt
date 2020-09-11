package com.demo.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentInfoBinding


class InfoFragment() : BaseFragment() {
    lateinit var binding: FragmentInfoBinding
    private var title: String = ""
    private var fileURl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(getString(R.string.toolbarTitle))!!
        fileURl = arguments?.getString(getString(R.string.fileURL))!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.apply {
            toolbarTitle = title
            vwDescription.loadUrl(fileURl)
            clickHandler = ClickHandler()
        }
        return binding.root
    }


    inner class ClickHandler {
        fun backPress() {
            findNavController().navigateUp()
        }
    }
}