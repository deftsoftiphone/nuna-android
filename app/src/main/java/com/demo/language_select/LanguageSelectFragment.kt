package com.demo.language_select


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.activity.DashboardActivity
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentLangSelectBinding
import com.demo.model.response.baseResponse.Language
import com.demo.util.Prefs
import com.demo.util.Util
import com.demo.util.selectLanguage
import com.demo.util.setMargins
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class LanguageSelectFragment : BaseFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: FragmentLangSelectBinding
    lateinit var mViewModel: LanguageSelectViewModel
    private val viewModelFactory: LanguageSelectViewModelFactory by instance()
    private lateinit var mAdapter: LanguageAdapter
    lateinit var mClickHandler: LanguageSelectFragment.ClickHandler
    private var selectedLanguage: Language? = null
    var mode: String? = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    fun setupRecycler(languages: List<Language>) {
        mAdapter = LanguageAdapter(languages, mClickHandler, activity)
        mBinding.rvLanguage.layoutManager = GridLayoutManager(requireContext(), 2)
        mBinding.rvLanguage.adapter = mAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mClickHandler = ClickHandler()
        mBinding = FragmentLangSelectBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        Util.updateStatusBarColor(true, "#FFFFFF", activity as FragmentActivity)
        getLanguages()
        mode = arguments?.getString("mode")
        if (mode.equals("update")) {
            (requireActivity() as DashboardActivity).apply {
                hideKeyboard()
                setStatusBarColor()
            }
            mBinding.tvTitle.text = getString(R.string.change_language)
            mBinding.toolbar.visibility = VISIBLE
            mBinding.tvTitle.setPadding(0, 0, 0, 20)
            mBinding.tvTitle.setMargins(0, 0, 0, 0)
            mBinding.rvLanguage.setMargins(0, 30, 0, 0)
        } else {
            (requireActivity() as AccountHandlerActivity).apply {
                hideKeyboard()
                setStatusBarColor()
            }
            mBinding.toolbar.visibility = GONE
        }
        return mBinding.root
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(LanguageSelectViewModel::class.java)
    }

    inner class ClickHandler {

        fun backPress() {
            findNavController().navigateUp()
        }

        fun onClickLanguage(language: Language) {
            if (mode.equals("update")) {
                updateLanguage(language)
            } else {
                setLanguage(language)
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }


    private fun setLanguage(language: Language) {
        Prefs.init().selectedLang = language
        Prefs.init().selectedLangId = language.languageIntId!!
        selectedLanguage = language
        requireContext().selectLanguage(language.languageIntId)
//        selectLanguage(language.languageIntId)

    }

    private fun selectLanguage(languageId: Int?) {
        when (languageId ?: Prefs.init().selectedLangId) {
            1 -> (requireActivity() as BaseActivity).setLanguage("hi")
            2 -> (requireActivity() as BaseActivity).setLanguage("en")
            3 -> (requireActivity() as BaseActivity).setLanguage("mr")
            4 -> (requireActivity() as BaseActivity).setLanguage("bn")
            5 -> (requireActivity() as BaseActivity).setLanguage("ta")
            6 -> (requireActivity() as BaseActivity).setLanguage("te")
            7 -> (requireActivity() as BaseActivity).setLanguage("gu")
            8 -> (requireActivity() as BaseActivity).setLanguage("kn")
        }
    }

    private fun getLanguages() = launch {
        commonCallbacks?.showProgressDialog()
        mBinding.vNoData.visibility = GONE
        mViewModel.getLanguage().observe(viewLifecycleOwner, Observer {
            commonCallbacks?.hideProgressDialog()
            if (it.error == null) {
                val languages = it.data?.languages
                if (languages != null && languages.isNotEmpty())
                    setupRecycler(languages)
            } else {
                mBinding.vNoData.visibility = VISIBLE
                it.error?.message?.let { it1 -> handleAPIError(it1) }
            }
        })


    }

    private fun updateLanguage(lang: Language) = launch {
        val language = com.demo.model.request.Language(lang._id)
        commonCallbacks?.showProgressDialog()
        mViewModel.updateLanguage(language).observe(viewLifecycleOwner, {
            commonCallbacks?.hideProgressDialog()
            if (it.success!!) {
//                Validator.showMessage(getString(R.string.language_updated_success))
                setLanguage(lang)
                onFragBack()
            } else {
                it.error?.message?.let { it1 -> handleAPIError(it1) }
            }
        })
    }
}
